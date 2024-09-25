package com.kob.botrunningsystem.service.impl.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

//消费者
public class BotPool extends Thread {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final Queue<Bot> bots = new LinkedList<>();

    public void addBot(Integer userId, String botCode, String input) {
        lock.lock();//涉及到对queue的修改
        try {
            bots.add(new Bot(userId, botCode, input));
            condition.signalAll();//队列里有元素就唤醒阻塞线程
        } finally {
            lock.unlock();
        }
    }

    //编译代码函数
    private void consume(Bot bot) {//线程控制执行时间
        //要搞一个限时的线程，防止用户写的死循环
        Consumer consumer = new Consumer();
        consumer.startTimeout(2000, bot);
    }

    @Override
    public void run() {
        while (true) {
            lock.lock();
            if (bots.isEmpty()) {
                try {
                    condition.await();//如果为空就阻塞
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    lock.unlock();
                    break;
                }
            } else {
                Bot bot = bots.remove();
                lock.unlock();
                consume(bot);  // 比较耗时，可能会执行几秒钟，所以放在lock后面
            }
        }
    }
}
