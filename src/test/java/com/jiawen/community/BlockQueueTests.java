package com.jiawen.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockQueueTests {

    public static void main(String[] args) {
        //生产者和消费者共用一个阻塞队列
        BlockingQueue queue = new ArrayBlockingQueue(10);
        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
    }

}

class Producer implements Runnable {

    private BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            //一共生产100个数据 每一个数据都放到队列里
            for (int i = 0; i < 100; i++) {
                //生产的每一个数据之间都有一个间隔 每20毫秒就生产一个数据
                Thread.sleep(20);
                //生产者生产的数据都是随机的
                queue.put(i);
                //打印出来当前谁在生产 并且打印出来生产的数据
                System.out.println(Thread.currentThread().getName() + "生产:" + queue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class Consumer implements Runnable {
    //定义一个变量接受传入的阻塞队列 存整数
    private BlockingQueue<Integer> queue;

    //构造方法 将传入的阻塞队列传入 并初始化
    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                //消费的每一个数据之间都有一个间隔 每1000毫秒就消费一个数据
                Thread.sleep(new Random().nextInt(1000));
                queue.take();
                System.out.println(Thread.currentThread().getName() + "消费:" + queue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}