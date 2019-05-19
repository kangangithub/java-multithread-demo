package com.example.multithread.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

/**
 * 3个线程交替打印,线程1:A,线程2:B,线程3打印C,结果ABCABCABC....,保证线程1先打印
 * synchronized+wait+notifyAll
 *
 * @Auther: Akang
 * @Date: 2019/5/15 11:19
 * @Description:
 */
public class Demo3 {
    /**
     * 锁1
     */
    private final Object lock1 = new Object();
    /**
     * 锁2
     */
    private final Object lock2 = new Object();
    /**
     * 锁3
     */
    private final Object lock3 = new Object();

    @Test
    public void test1() throws InterruptedException {
        /**
         * 1.线程1持有lock1,lock2锁,打印A,唤醒线程2,阻塞线程1
         * 2.线程2持有lock2,lock3锁,打印B,唤醒线程3,阻塞线程2
         * 3.线程3持有lock3,lock1锁,打印C,唤醒线程1,阻塞线程3
         * 4.线程1持有lock1,lock2锁,打印A,唤醒线程2,阻塞线程1
         * ......
         */
        new Thread(() -> print("A", lock1, lock2)).start(); // 持有lock1,lock2锁
        Thread.sleep(10); // 保证初始ABC的启动顺序
        new Thread(() -> print("B", lock2, lock3)).start(); // 持有lock2,lock3锁
        Thread.sleep(10); // 保证初始ABC的启动顺序
        new Thread(() -> print("C", lock3, lock1)).start(); // 持有lock3,lock1锁
        Thread.sleep(1000); // 保证初始ABC的启动顺序
    }

    private void print(String s, Object o1, Object o2) {
        int count = 0;
        while (count < 10) {
            try {
                synchronized (o1) { // 先获取o1锁
                    synchronized (o2) { // 再获取o2锁
                        System.out.print(s); // 同时持有o1,o2锁才能打印
                        count++; // 计数器+1
                        o2.notify(); // 唤醒线程, 执行完毕释放o2锁
                    }
                    o1.wait(); // 阻塞当前线程,释放o1锁
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
