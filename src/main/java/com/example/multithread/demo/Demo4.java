package com.example.multithread.demo;

import org.junit.Test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 3个线程交替打印,线程1:A,线程2:B,线程3打印C,结果ABCABCABC....,保证线程1先打印
 * ReentrantLock+state和ReentrantLock+Condition
 *
 * @Auther: Akang
 * @Date: 2019/5/15 17:05
 * @Description:
 */
public class Demo4 {
    private ReentrantLock lock1 = new ReentrantLock();
    private static int state = 0;

    @Test
    public void test1() throws InterruptedException {
        new Thread(() -> print1(0, "A")).start();
        Thread.sleep(10);
        new Thread(() -> print1(1, "B")).start();
        Thread.sleep(10);
        new Thread(() -> print1(2, "C")).start();
        Thread.sleep(1000);

    }

    private void print1(int a, String s) {
        /**
         *  当state % 3 != a时,该线程结束,所以这里需要for循环保证线程第一次打印后保持存活
         */
        for (int i = 0; i < 10; ) { // 打印10次
            try {
                lock1.lock(); // 加锁
                while (state % 3 == a) { // 多线程并发，不能用if，必须用循环测试等待条件，避免虚假唤醒
                    System.out.print(s);
                    state++;
                    i++;
                }
            } finally {
                lock1.unlock(); // unlock()操作必须放在finally块中
            }
        }
    }

    private ReentrantLock lock2 = new ReentrantLock();
    private Condition conditionA = lock2.newCondition();
    private Condition conditionB = lock2.newCondition();
    private Condition conditionC = lock2.newCondition();

    @Test
    public void test2() throws InterruptedException {
        new Thread(() -> print2(0, "A", conditionA, conditionB)).start();
        Thread.sleep(10);
        new Thread(() -> print2(1, "B", conditionB, conditionC)).start();
        Thread.sleep(10);
        new Thread(() -> print2(2, "C", conditionC, conditionA)).start();
        Thread.sleep(1000);
    }

    private void print2(int a, String s, Condition condition1, Condition condition2) {
        /**
         * 1.state=0,线程1打印A,唤醒线程2,阻塞线程1
         * 2.state=1,线程2打印B,唤醒线程3,阻塞线程2
         * 3.state=2,线程3打印C,唤醒线程1,阻塞线程3
         * 4.state=3,线程1打印A,唤醒线程2,阻塞线程1
         * ......
         */
        try {
            lock2.tryLock(); // 加锁
            while (state % 3 == a && state < 30) {
                System.out.print(s);
                state++;

                condition2.signal(); // 唤醒线程2
                try {
                    condition1.await(); // 阻塞线程1
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            lock2.unlock(); // unlock()操作必须放在finally块中
        }
    }
}
