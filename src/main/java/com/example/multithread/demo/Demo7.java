package com.example.multithread.demo;

import org.junit.Test;

/**
 * 线程1循环10次，线程2接着循环20次，如此循环50次
 *
 * @Auther: Akang
 * @Date: 2019/5/15 17:46
 * @Description:
 */
public class Demo7 {
    private final Object lock = new Object();

    @Test
    public void test1() throws InterruptedException {
        new Thread(() -> print(10)).start();
        Thread.sleep(10);
        new Thread(() -> print(20)).start();
        Thread.sleep(3000);
    }

    private void print(int a) {
        for (int j = 0; j < 50; j++) {
            synchronized (lock) {
                for (int i = 1; i <= a; i++) {
                    System.out.println(j + ":" + Thread.currentThread().getName() + ":" + i);
                }

                lock.notify();
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
