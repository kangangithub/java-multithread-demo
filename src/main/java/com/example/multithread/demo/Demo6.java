package com.example.multithread.demo;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 3个线程交替打印,线程1:A,线程2:B,线程3打印C,结果ABCABCABC....,保证线程1先打印
 * AtomicInteger CAS机制
 *
 * @Auther: Akang
 * @Date: 2019/5/15 17:45
 * @Description:
 */
public class Demo6 {
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Test
    public void test1() throws InterruptedException {
        new Thread(() -> print(0, "A")).start();
        Thread.sleep(10);
        new Thread(() -> print(1, "B")).start();
        Thread.sleep(10);
        new Thread(() -> print(2, "C")).start();
        Thread.sleep(1000);
    }

    private void print(int i, String a) {
        while (atomicInteger.intValue() < 30) {
            if (atomicInteger.intValue() % 3 == i) {
                System.out.print(a);
                atomicInteger.incrementAndGet();
            }
        }
    }
}
