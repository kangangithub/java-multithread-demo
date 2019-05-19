package com.example.multithread.demo;

import org.junit.Test;

/**
 * 两个线程交替打印, 线程1:123,线程2:456,线程1:789....
 *
 * @Auther: Akang
 * @Date: 2019/5/14 14:46
 * @Description:
 */
public class Demo1 {
    /**
     * 静态属性,打印初始值,两个线程竞争打印--共享资源
     */
    private static int i = 1;
    /**
     * 锁
     */
    private final Object lock = new Object();

    @Test
    public void test1() throws InterruptedException {
        new Thread(()->print1()).start();
        Thread.sleep(10L);
        new Thread(()->print1()).start();
        Thread.sleep(1000L);
    }

    private void print1() {
        int j = 0;
        synchronized (lock) {
            while (i <= 30) {
                System.out.println(Thread.currentThread().getName() + ":" + i);
                i++;
                j++;

                if (j > 2) {
                    j = 0;
                    lock.notify(); // 调用锁对象的notify()
//                        notify(); // 相当于MyThread1.notify(),报IllegalMonitorStateException异常,说明当前的线程不是此对象监视器的所有者
                    try {
                        lock.wait();
//                            wait(); // 相当于MyThread1.wait()
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Test
    public void test2() throws InterruptedException {
        new Thread(()->print2()).start();
        Thread.sleep(10L);
        new Thread(()->print2()).start();
        Thread.sleep(1000L);
    }

    private synchronized void print2() {
        int j = 0;
        while (i <= 30) {
            System.out.println(Thread.currentThread().getName() + ":" + i);
            i++;
            j++;

            if (j > 2) {
                j = 0;
                notify();
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}