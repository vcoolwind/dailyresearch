package com.blackstone.dailyresearch.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * desc: LRUMap第一个版本,根据最近使用时间淘汰。
 *
 * @author 王彦锋
 * @date 2018/7/6 18:54
 */
public class LRVMapV1<K, V> {
    private Map<K, Node<K, V>> kvMap = null;
    private int maxSize;
    private Node<K, V> head;
    private Node<K, V> tail;

    public LRVMapV1(int maxSize) {
        this.maxSize = maxSize;
        kvMap = new ConcurrentHashMap<>(maxSize + 1);
    }

    public void put(K key, V value) {
        Node<K, V> node = new Node<>(key, value);
        Node<K, V> removeNode = kvMap.get(key);
        setLatest(removeNode, node);
        kvMap.put(key, node);
        if (kvMap.size() > maxSize) {
            kvMap.remove(tail.getKey());
            setLatest(tail, null);
        }
    }

    public V get(K key) {
        Node<K, V> node = kvMap.get(key);
        if (node != null) {
            setLatest(node, node);
            return node.getValue();
        } else {
            return null;
        }
    }

    public void remove(K key) {
        Node<K, V> node = kvMap.get(key);
        if (node != null) {
            setLatest(node, null);
        }
        kvMap.remove(key);
    }

    public boolean containsKey(K key) {
        return kvMap.containsKey(key);
    }

    public void clear() {
        head = null;
        tail = null;
        kvMap.clear();
    }

    public int size() {
        return kvMap.size();
    }

    private void setLatest(Node<K, V> removeNode, Node<K, V> newNode) {
        if (removeNode != null) {
            if (removeNode.previous != null) {
                removeNode.previous.next = removeNode.next;
            } else {
                head = removeNode.next;
            }
            if (removeNode.next != null) {
                removeNode.next.previous = removeNode.previous;
            } else {
                tail = removeNode.previous;
            }
        }
        if(newNode==head){
            return;
        }
        if (newNode != null) {
            if (head == null) {
                head = newNode;
                head.next = null;
                tail = newNode;
                tail.previous = null;
            } else {
                newNode.next = head;
                newNode.previous = null;
                head.previous = newNode;
                head = newNode;
            }
        }
    }

    public void showFromHead() {
        if (head != null && head.previous != null) {
            throw new RuntimeException("系统异常，previous of head is not null");
        }
        System.out.print("Head to Tail:NUll --> ");
        Node<K, V> current = head;
        while (current != null) {
            System.out.print(current.getKey() + "--> ");
            current = current.next;
        }
        System.out.println("NUll");
    }

    public void showFromTail() {
        if (tail != null && tail.next != null) {
            throw new RuntimeException("系统异常，next of tail is not null");
        }
        System.out.print("Tail to Head:NUll --> ");

        Node<K, V> current = tail;
        while (current != null) {
            System.out.print(current.getKey() + "--> ");
            current = current.previous;
        }
        System.out.println("NUll");

    }

    class Node<K, V> {
        private K key;
        private V value;
        private Node<K, V> previous;
        private Node<K, V> next;

        public Node(K k, V v) {
            this.key = k;
            this.value = v;
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

        public Node<K, V> getPrevious() {
            return previous;
        }

        public void setPrevious(Node<K, V> previous) {
            this.previous = previous;
        }

        public Node<K, V> getNext() {
            return next;
        }

        public void setNext(Node<K, V> next) {
            this.next = next;
        }

        @Override
        public String toString() {
            Object pre = previous == null ? "NULL" : previous.getKey();
            Object ne = next == null ? "NULL" : next.getKey();
            return pre + "--> " + key + " -->" + ne;
        }
    }
}
