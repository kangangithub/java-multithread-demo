package com.example.multithread.demo;

import org.junit.Test;

/**
 * 线程1负责打印a,b,c,d,线程2负责打印1,2,3,4,要求控制台中输出的内容为 a1b2c3d4
 *
 * @Auther: Akang
 * @Date: 2019/5/15 15:47
 * @Description:
 */
public class Demo2 {
    private final Object lock = new Object(); // 锁

    @Test
    public void test1() throws InterruptedException {
        new Thread(() -> {
            String[] strings = new String[]{"a", "b", "c", "d"};
            print(strings);
        }).start();
        Thread.sleep(10); // 阻塞mian线程,保证thread1先运行
        new Thread(() -> {
            String[] strings = new String[]{"1", "2", "3", "4"};
            print(strings);
        }).start();
        Thread.sleep(100); // 阻塞main线程,保证thread1,thread2运行完毕再结束main线程
    }

    private void print(String[] strings) {
        for (String string : strings) {
            synchronized (lock) {
                System.out.print(string);
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
