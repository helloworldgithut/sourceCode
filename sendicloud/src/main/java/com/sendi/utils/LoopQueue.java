package com.sendi.utils;

import java.util.concurrent.ConcurrentHashMap;


/**
 * LoopQueue只在PassiveSocketServer线程中调用，而replyMessCodeMap会在多线程中用到
 * LoopQueue循环队列保存最近100条请求,replyMessCodeMap记录对应的回复报文
 * @author yangx
 * 2017年8月23日
 */
public class LoopQueue {

    private int front; // 头
    private int rear; // 尾
    private int capacity; // 保存当前长度
    private int maxSize;
    private  int CACHE_QUEUE_SIZE=1000;
    private String[] queueList;
    private  ConcurrentHashMap<String, byte[]> replyMessCodeMap = new ConcurrentHashMap<String, byte[]>();

    public LoopQueue() {
        maxSize = CACHE_QUEUE_SIZE;
        queueList = new String[maxSize];
        capacity = 0;
        front = 0;
        rear = 0;
    }

    public boolean isEmpty() {
        if (front == rear) {
            return true;
        }
        return false;
    }
    /**
     * 如果循环队列已满，直接覆盖第一个元素
     *
     * @author yangx 2017年8月17日
     * @param
     */
    public void add(String hashCode) {

        if (capacity >= maxSize) {
            String removeMap = this.queueList[rear];
            replyMessCodeMap.remove(removeMap);
        } else {
            capacity++;
        }
        this.queueList[rear] = hashCode;
        rear = (rear + 1) % maxSize;
    }
    /**
     * 判断是否有重复元素
     *
     * @author yangx 2017年8月17日
     * @return
     */
    public boolean isDuplication(String hashCode) {
        int i = 0;
        boolean flag = false;
        int step = 0;
        while (i < capacity) {
            if (rear - 1 + i < 0) {
                step = capacity - 1;
            } else {
                step = (rear - 1 + i) % capacity;
            }
            if (queueList[step].equals(hashCode)) {
                flag = true;
                break;
            }
            i++;
        }
        return flag;
    }

    public int getFront() {
        return front;
    }

    public int getRear() {
        return rear;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public String[] getQueueList() {
        return queueList;
    }

    public  ConcurrentHashMap<String, byte[]> getReplyMessCodeMap() {
        return replyMessCodeMap;
    }

}
