个人的几个关键理解：
kafka基于zookeeper做各种配置存储及可靠性，其本身只存储消息，其他内容强依赖ZooKeeper。
ZooKeeper的配置见其他文章，需要在kaka的配置文件server.properties中明确ZooKeeper的集群链接地址。zookeeper.connect=192.168.7.100:12181,192.168.7.101:12181,192.168.7.107:12181
每个topic都是独立的，系统中有默认配置，也可以在创建topic时，指定topic的属性，主要是分片个数 partition、复制个数replication。
消费topic时，基于单个分片是有序的，分片与分片的读取是无需的。业务中可以根据key值确保业务有序。
kafka做集群比较简单，只要链接到同一个ZooKeeper集群即可。需要关注的就是broker.id的设置，集群内不能重复。
一个分区至多分配到一个消费线程中，所以消费线程不应多余分区个数。多余也没有什么用。



1、wget https://www.apache.org/dyn/closer.cgi?path=/kafka/1.1.0/kafka_2.12-1.1.0.tgz

2、tar -xzf kafka_2.12-1.1.0.tgz

3、bin/kafka-server-start.sh -daemon config/server.properties

4、几个关键配置：

broker.id=0  #每台服务器的broker.id都不能相同
listeners=PLAINTEXT://0.0.0.0:9092
advertised.listeners=PLAINTEXT://192.168.85.167:9092 #各个主机配置本机IP

#在log.retention.hours=168 下面新增下面三项
message.max.byte=5242880
default.replication.factor=2
replica.fetch.max.bytes=5242880

#设置zookeeper的连接端口
zookeeper.connect=192.168.7.100:12181,192.168.7.101:12181,192.168.7.107:12181

1、这里要是本机IP，否则java端无法读写
listeners=PLAINTEXT://0.0.0.0:9092
advertised.listeners=PLAINTEXT://192.168.85.167:9092

2、zookeeper集群链接（链接同一个zookeeper，kafka自动构建一个集群）
zookeeper.connect=192.168.85.166:2181,192.168.85.167:2181,192.168.85.168:2181

3、消息分区 --- 分区越多，读的越快。但是，写入就收影响，要综合考虑。
【在kafka配置文件中可随时修改num.partitions参数来配置更改topic的partition数量，在创建Topic时通过参数指定parittion数量。Topic创建之后通过Kafka提供的工具也可以修改partiton数量。   一般来说，（1）一个Topic的Partition数量大于等于Broker的数量，可以提高吞吐率。（2）同一个Partition的Replica尽量分散到不同的机器，高可用。】
num.partitions=2
调用发送时，可以指定分区，也可以指定分区算法。
在生成topic时有效，生成后写入时不能调整。
命令行调整：./bin/kafka-topics.sh --zookeeper  localhost:2181 --alter --partitions 2 --topic  foo



4、集群内各个主机不能相同，通过链接同一组zookeeper来自动构建一个集群。
broker.id=1


5、同一个分区，读取是有序的；跨分区时，各走各的顺序。这个和RabbitMQ有很大的不同。

6、数据保留副本数，不能高于kafka集群个数。
default.replication.factor=2  #kafka保存消息的副本数，如果一个副本失效了，另一个还可以继续提供服务
7、自动创建topic，否则，客户端需要先创建topic才能使用。

auto.create.topics.enable=true


#创建Topic
./kafka-topics.sh --create --zookeeper 192.168.7.100:12181 --replication-factor 2 --partitions 1 --topic shuaige
#解释
--replication-factor 2   #复制两份
--partitions 1 #创建1个分区
--topic #主题为shuaige

'''在一台服务器上创建一个发布者'''
#创建一个broker，发布者
./kafka-console-producer.sh --broker-list 192.168.7.100:19092 --topic shuaige

'''在一台服务器上创建一个订阅者'''
./kafka-console-consumer.sh --zookeeper localhost:12181 --topic shuaige --from-beginning

4.1、查看topic
./kafka-topics.sh --list --zookeeper localhost:12181
#就会显示我们创建的所有topic
4.2、查看topic状态
/kafka-topics.sh --describe --zookeeper localhost:12181 --topic shuaige
#下面是显示信息
Topic:ssports    PartitionCount:1    ReplicationFactor:2    Configs:
    Topic: shuaige    Partition: 0    Leader: 1    Replicas: 0,1    Isr: 1
#分区为为1  复制因子为2   他的  shuaige的分区为0
#Replicas: 0,1   复制的为0，1

