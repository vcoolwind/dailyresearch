package com.blackstone.dailyresearch.kafka;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.server.ConfigType;
import kafka.utils.ZkUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.security.JaasUtils;
import org.apache.kafka.common.serialization.StringSerializer;


public class KafkaUtils {

    //服务器ip:端口号，集群用逗号分隔
    static String BOOTSTRAP_SERVERS = "192.168.85.166:9092,192.168.85.167:9092";
    static String BOOTSTRAP_SERVERS_P = "192.168.85.166:9092";
    static String BOOTSTRAP_SERVERS_C = "192.168.85.167:9092";
    static String ZK_SERVERS = "192.168.85.166:2181,192.168.85.167:2181,192.168.85.168:2181";

    static String TOPIC_NAME = "wangyfA";
    private static Producer<String, String> producer;

    /**
     * 生产者，注意kafka生产者不能够从代码上生成主题，只有在服务器上用命令生成
     */
    static {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS_P);

        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);

        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        producer = new KafkaProducer<>(props);
    }

    private KafkaUtils() {
    }

    /**
     * 创建分区。 比如，4个分区，2份复制，kafka集群数量为3。 那么，消息总共存8个分区，相对均匀的分布到3个集群中。
     *
     * @param topic
     * @param partition   消息写入分区数量。
     * @param replication 消息复制数量。
     * @return
     */
    public static boolean createTopic(String topic, int partition, int replication) {

        ZkUtils zkUtils = ZkUtils.apply(ZK_SERVERS, 30000, 30000, JaasUtils.isZkSecurityEnabled());
        // 创建一个单分区单副本名为t1的topic
        Properties conf = new Properties();
        KafkaTopicBean bean = new KafkaTopicBean();
        bean.setTopic(topic);
        bean.setPartition(partition);
        bean.setReplication(replication);

        AdminUtils.createTopic(zkUtils,
                bean.getTopic(),
                bean.getPartition(),
                bean.getReplication(),
                new Properties(),
                new RackAwareMode.Enforced$()
        );
        zkUtils.close();

        return true;
    }

    public static boolean existsTopic(String topic) {
        ZkUtils zkUtils = ZkUtils.apply(ZK_SERVERS, 30000, 30000, JaasUtils.isZkSecurityEnabled());
        if (StringUtils.isBlank(topic)) {
            return false;
        } else {
            try {
                return AdminUtils.topicExists(zkUtils, topic);
            } finally {
                zkUtils.close();
            }
        }
    }

    /**
     * 发送对象消息 至kafka上,调用json转化为json字符串，应为kafka存储的是String。
     *
     * @param msg
     */
    public static void sendMsgToKafka(String msg) throws Exception {
        String key = String.valueOf(System.currentTimeMillis());
        RecordMetadata metadata = producer.send(new ProducerRecord<String, String>(TOPIC_NAME,
                //Long.valueOf(System.currentTimeMillis()%2).intValue(),
                key,
                msg)
        ).get();
        System.out.printf("sent record(key=%s value=%s) " +
                        "meta(partition=%d, offset=%d) ",
                key,
                msg,
                metadata.partition(),
                metadata.offset());
    }

    public static void produce() {
        System.out.println("produce start...");
        if (!existsTopic(TOPIC_NAME)) {
            createTopic(TOPIC_NAME, 4, 2);
        }

        Thread one = new Thread() {
            public void run() {
                try {
                    int i = 0;
                    while (true) {
                        try {
                            KafkaUtils.sendMsgToKafka("test" + i);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println("produce ..." + i);
                        Thread.sleep(1000);
                        i++;
                    }
                } catch (InterruptedException v) {
                    v.printStackTrace();
                }
            }
        };
        one.start();
    }

    /**
     * 从kafka上接收对象消息，将json字符串转化为对象，便于获取消息的时候可以使用get方法获取。
     */
    public static void getMsgFromKafka() {
        System.out.println("Consumer start... " + Thread.currentThread().getName());
        Consumer<String, String> consumer = KafkaUtils.getKafkaConsumer();
        while (true) {

            ConsumerRecords<String, String> records = consumer.poll(100);
            if (records.count() > 0) {
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(Thread.currentThread().getName() + " --> partition:" + record.partition() + " value:" + record.value().toString());
                }
            }
        }
    }

    public static Consumer<String, String> getKafkaConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", BOOTSTRAP_SERVERS_C);//服务器ip:端口号，集群用逗号分隔
        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(TOPIC_NAME));
        return consumer;
    }

    public static void closeKafkaProducer() {
        producer.close();
    }

    /**
     * 获取topic的配置信息
     */
    public static Properties getTopicProp(String topic) {
        ZkUtils zkUtils = ZkUtils.apply(ZK_SERVERS, 30000, 30000, JaasUtils.isZkSecurityEnabled());
        return AdminUtils.fetchEntityConfig(zkUtils, ConfigType.Topic(), topic);
    }

    public static int partitionCount(String topicName) {
        List<PartitionInfo> list = producer.partitionsFor(topicName);
        for (PartitionInfo partitionInfo : list) {
            System.out.println(partitionInfo);
        }
        return list.size();
    }

    /**
     * 删除topic信息（前提是server.properties中要配置delete.topic.enable=true）
     *
     * @param topicName
     */
    public void deleteTopic(String topicName) {
        ZkUtils zkUtils = ZkUtils.apply(ZK_SERVERS, 30000, 30000, JaasUtils.isZkSecurityEnabled());
        // 删除topic 't1'
        AdminUtils.deleteTopic(zkUtils, topicName);
        System.out.println("删除成功！");
    }

    public static void main(String[] args) {
        Properties p = getTopicProp(TOPIC_NAME);
        System.out.println(p);
        if (existsTopic(TOPIC_NAME)) {
            System.out.println("topic " + TOPIC_NAME + " exists");
        } else {
            System.out.println("topic " + TOPIC_NAME + " not exists");
        }

        KafkaUtils.produce();
        for (int i = 0; i < partitionCount(TOPIC_NAME); i++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    KafkaUtils.getMsgFromKafka();
                }
            });
            t.setName("consumer--" + i);
            t.start();
        }
    }
}
