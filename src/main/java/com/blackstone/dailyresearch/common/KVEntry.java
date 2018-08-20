package com.blackstone.dailyresearch.common;

/**
 * 定义通用的Entry
 * @param <K> key
 * @param <V> value
 * @author 王彦锋
 * @date 2018/6/25 15:55
 *
 */
public class KVEntry<K,V> {
    private K key;
    private V value;

    public KVEntry(){

    }
    public KVEntry(K key, V value){
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "KVEntry{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
