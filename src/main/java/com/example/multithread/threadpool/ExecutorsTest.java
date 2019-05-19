package com.example.multithread.threadpool;

import org.junit.Test;

import java.util.concurrent.*;

/**
 * Executors类，提供了一系列工厂方法用于创先线程池，返回的线程池都实现了ExecutorService接口。
 * ExecutorService提供了submit()方法，传递一个Callable，或Runnable，返回Future。
 * 如果Executor后台线程池还没有完成Callable的计算，这调用返回Future对象的get()方法，会阻塞直到计算完成。
 * <p>
 * Executors各个方法的弊端：
 * 1. newFixedThreadPool和newSingleThreadExecutor:  主要问题是堆积的请求处理队列可能会耗费非常大的内存，甚至OOM。
 * 2. newCachedThreadPool和newScheduledThreadPool:  主要问题是线程数最大数是Integer.MAX_VALUE，可能会创建数量非常多的线程，甚至OOM。
 */
public class ExecutorsTest {

    /**
     * newFixedThreadPool(int nThreads): 创建固定数目线程的线程池。
     * 特点:
     * 1.每当提交一个任务就创建一个工作线程，如果工作线程数量达到线程池最大数，则将提交的任务存入到池队列中。
     * 2.在线程池空闲时，即线程池中没有可运行任务时，它不会释放工作线程，还会占用一定的系统资源。
     * 3.可控制线程最大并发数（同时执行的线程数）
     * <p>
     * void execute(Runnable command); 执行任务没有返回值
     *
     * @throws InterruptedException
     */
    @Test
    public void newFixedThreadPoolTest() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " start");

        ExecutorService executor = Executors.newFixedThreadPool(3);
        // 创建10个任务去执行
        for (int i = 0; i < 10; i++) {
            executor.execute(() -> System.out.println(Thread.currentThread().getName() + ":newFixedThreadPoolTest"));
        }
        // 结束线程池
        executor.shutdown();
        // 线程池未终止,当前线程sleep1秒
        while (!executor.isTerminated()) {
            Thread.sleep(1000L);
        }

        System.out.println(Thread.currentThread().getName() + " end");
    }

    /**
     * newCachedThreadPool(): 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
     * 特点：
     * 1.线程数无限制,Integer.MAX_VALUE
     * 2.有空闲线程则复用空闲线程，若无空闲线程则新建线程
     * 3.一定程序减少频繁创建/销毁线程，减少系统开销
     * <p>
     * Future<?> submit(Runnable task); 执行任务
     * <T> Future<T> submit(Callable<T> task);
     *
     * @throws InterruptedException
     */
    @Test
    public void newCachedThreadPoolTest() throws InterruptedException, ExecutionException {
        System.out.println(Thread.currentThread().getName() + " start");

        ExecutorService executor = Executors.newCachedThreadPool();
        // 创建10个任务去执行
        for (int i = 0; i < 10; i++) {
//            executor.submit(() -> System.out.println(Thread.currentThread().getName() + ":newCachedThreadPoolTest"));
            Future<String> future = executor.submit(() -> Thread.currentThread().getName() + ":newCachedThreadPoolTest");
            System.out.println("future.get(): " + future.get());
        }
        // 结束线程池
        executor.shutdown();
        // 线程池未终止,当前线程sleep1秒
        while (!executor.isTerminated()) {
            Thread.sleep(1000L);
        }

        System.out.println(Thread.currentThread().getName() + " end");
    }

    /**
     * newSingleThreadExecutor(): 创建一个单线程化的Executor。
     * 特点:
     * 1.只创建唯一的工作者线程来执行任务，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
     * 如果这个线程异常结束，会有另一个取代它，保证顺序执行。单工作线程最大的特点是可保证顺序地执行各个任务，
     * 并且在任意给定的时间不会有多个线程是活动的。
     * <p>
     * <T> Future<T> submit(Runnable task, T result); 执行任务返回result
     *
     * @throws InterruptedException
     */
    @Test
    public void newSingleThreadExecutorTest() throws InterruptedException, ExecutionException {
        System.out.println(Thread.currentThread().getName() + " start");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        // 创建10个任务去执行
        for (int i = 0; i < 10; i++) {
            Future<String> future = executor.submit(() -> System.out.println(Thread.currentThread().getName() + ":newSingleThreadExecutorTest"), "newSingleThreadExecutorTest");
            System.out.println("future.get(): " + future.get());
        }
        // 结束线程池
        executor.shutdown();
        // 线程池未终止,当前线程sleep1秒
        while (!executor.isTerminated()) {
            Thread.sleep(1000L);
        }

        System.out.println(Thread.currentThread().getName() + " end");
    }

    /**
     * newScheduledThreadPool(int corePoolSize): 创建一个定长的线程池，而且支持定时的以及周期性的任务执行，支持定时及周期性任务执行。
     * <p>
     * schedule(Runnable command, long delay, TimeUnit unit): command: 任务 delay: 延时 unit: 延时单位
     * 延时delay后执行任务
     * <p>
     * scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit): command: 任务 initialDelay: 延时 period: 周期 unit: 周期单位
     * 固定频率：每间隔固定时间就执行一次任务。注重频率。
     * <p>
     * scheduleWithFixedDelay(Runnable command, long initialDelay, long period, TimeUnit unit): command: 任务 initialDelay: 延时 period: 周期(上一次任务结束开始算起) unit: 周期单位
     * 固定延迟：任务之间的时间间隔，也就是说当上一个任务执行完成后，我会在图定延迟时间后出发第二次任务。注重距上次完成任务后的时间间隔。
     *
     * @throws InterruptedException
     */
    @Test
    public void newScheduledThreadPoolTest() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " start");

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
        // 延迟1秒执行
//            executor.schedule(() -> System.out.println(Thread.currentThread().getName() + ":newScheduledThreadPoolTest" + System.currentTimeMillis()), 1L, TimeUnit.SECONDS);
        // 延迟1秒每2秒执行一次
        executor.scheduleAtFixedRate(() -> System.out.println(Thread.currentThread().getName() + ":newScheduledThreadPoolTest" + System.currentTimeMillis()), 1L, 2L, TimeUnit.SECONDS);
        // 延迟1秒每2秒执行一次, 当前一个任务结束的时刻，开始结算间隔时间，如0秒开始执行第一次任务，任务耗时5秒，任务间隔时间3秒，那么第二次任务执行的时间是在第8秒开始。
//            executor.scheduleWithFixedDelay(() -> System.out.println(Thread.currentThread().getName() + ":newScheduledThreadPoolTest:" + System.currentTimeMillis()), 1L, 2L, TimeUnit.SECONDS);
        // 当前线程sleep10秒
        Thread.sleep(10000L);
        // 结束线程池
        executor.shutdown();

        System.out.println(Thread.currentThread().getName() + " end");
    }
}
