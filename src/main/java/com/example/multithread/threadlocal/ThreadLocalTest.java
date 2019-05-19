package com.example.multithread.threadlocal;

import org.junit.Test;

/**
 * ThreadLocal线程局部变量: 是一个以ThreadLocal对象为键、任意对象为值的存储结构。
 * set(T)方法来设置一个值，在当前线程下再通过get()方法获取到原先设置的值。
 *
 * @Auther: Akang
 * @Date: 2019/4/11 15:52
 * @Description:
 */
public class ThreadLocalTest {
    private static final ThreadLocal<Long> THREAD_LOCAL = ThreadLocal.withInitial(System::currentTimeMillis);

    @Test
    public void testThreadLocal() throws InterruptedException {
        new Thread(() -> {
            begin();
            try {
                // 阻塞当前线程1秒
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 打印当前线程执行时间
            System.out.println(Thread.currentThread().getName() + " Cost: " + end() + " mills");
        }, "thread1").start();

        // 阻塞main线程2秒, 等待子线程执行完毕
        Thread.sleep(2000);

        begin();
        try {
            // 阻塞main线程1秒
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 打印main线程执行时间
        System.out.println(Thread.currentThread().getName() + " Cost: " + end() + " mills");
    }

    /**
     * set(T)方法来设置一个值
     */
    public void begin() {
        THREAD_LOCAL.set(System.currentTimeMillis());
    }

    /**
     * get()方法获取到原先设置的值。
     */
    public long end() {
        return System.currentTimeMillis() - THREAD_LOCAL.get();
    }
}
