package com.blackstone.dailyresearch.kafka;

public class KafkaTopicBean {

    // topic name
    private String topic;
    // partition num
    private Integer partition;
    // replication num
    private Integer replication;
    private String descrbe;
    // 操作类型
    private Integer operationType;

    public Integer getOperationType() {
        return operationType;
    }

    public void setOperationType(Integer operationType) {
        this.operationType = operationType;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getPartition() {
        return partition;
    }

    public void setPartition(Integer partition) {
        this.partition = partition;
    }

    public Integer getReplication() {
        return replication;
    }

    public void setReplication(Integer replication) {
        this.replication = replication;
    }

    public String getDescrbe() {
        return descrbe;
    }

    public void setDescrbe(String descrbe) {
        this.descrbe = descrbe;
    }

    @Override
    public String toString() {
        return "KafkaTopicBean [topic=" + topic + ", partition=" + partition
                + ", replication=" + replication + ", descrbe=" + descrbe
                + ", operationType=" + operationType + "]";
    }

}
