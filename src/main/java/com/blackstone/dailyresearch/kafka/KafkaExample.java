package com.blackstone.dailyresearch.kafka;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaExample {
    private final String topic;
    private final Properties props;

    public KafkaExample(String brokers, String username, String password) {
        this.topic = username + "-default";

        //String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
        //String jaasCfg = String.format(jaasTemplate, username, password);

        String serializer = StringSerializer.class.getName();
        String deserializer = StringDeserializer.class.getName();
        props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, username + "-consumer");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, deserializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, serializer);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializer);
        //props.put("security.protocol", "SASL_SSL");
        //props.put("sasl.mechanism", "SCRAM-SHA-256");
        //props.put("sasl.jaas.config", jaasCfg);
    }

    public void consume() {
        System.out.println("consume start...");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        List<String> topics = new ArrayList<>();
        topics.add(topic);
        consumer.subscribe(topics);

        while (true) {
            System.out.println("consume ...");
            ConsumerRecords<String, String> records = consumer.poll(1000);
            System.out.println("consume ..."+ records.count());
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("%s [%d] offset=%d, key=%s, value=\"%s\"\n",
                        record.topic(), record.partition(),
                        record.offset(), record.key(), record.value());
            }
        }
    }

    public void produce() {
        System.out.println("produce start...");

        Thread one = new Thread() {
            public void run() {
                try {
                    Producer<String, String> producer = new KafkaProducer<>(props);
                    int i = 0;
                    while(true) {
                        System.out.println("produce ..."+topic);
                        Date d = new Date();
                        producer.send(new ProducerRecord<>(topic, Integer.toString(i), d.toString()));
                        Thread.sleep(1000);
                        i++;
                    }
                } catch (InterruptedException v) {
                    System.out.println(v);
                }
            }
        };
        one.start();
    }

    public static void main(String[] args) {
        //String brokers = "192.168.85.166:9092,192.168.85.167:9092,192.168.85.167:9092";
        String brokers = "192.168.85.166:9092";
        String username = "wangyf";
        String password = "123456";
        KafkaExample c = new KafkaExample(brokers, username, password);
        c.produce();
        c.consume();
    }
}