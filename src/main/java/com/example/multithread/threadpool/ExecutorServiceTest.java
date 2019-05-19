package com.example.multithread.threadpool;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.junit.Test;

import java.util.concurrent.*;

/**
 * ExecutorService线程池
 * <p>
 * Created by ankang on 2017-03-25.
 */
public class ExecutorServiceTest {
    /**
     * 线程池运行流程:
     * 创建时
     * JDK提供了一个ThreadPoolExecutor类供我们来手动创建线程池, public class ThreadPoolExecutor extends AbstractExecutorService
     * 构造参数:
     * 参数名	含义
     * corePoolSize:	线程池维护线程的最少数量。线程池至少会保持该数量的线程存在，即使没有任务可以处理。（注意：这里说的至少是指线程达到这个数量后，即使有空闲的线程也不会释放，而不是说线程池创建好之后就会初始化这么多线程）
     * maximumPoolSize:	线程池最大数量，线程池允许创建的最大线程数，如果队列满了，并且已创建的线程数小于最大线程数，则线程池会再创建新的线程来执行任务。值得注意的是，线程池队列如果使用的是无界队列，那么这个参数就没有什么效果
     * unit:	线程活动保持时间的单位，可选择的时间有时分秒等，即线程池维护线程所允许的空闲时间的单位，和keepAliveTime配合使用
     * keepAliveTime:	线程活动保持时间，线程池的工作线程空闲后，保持存活的时间，所以，当任务很多的时候，并且每个任务执行的时间比较短，可以调大时间，即调大线程活动保持时间，可以提高线程的利用率
     * workQueue:	任务队列，用于暂时保存任务的工作队列
     * threadFactory:	用于创建线程的工厂
     * handler:	饱和策略
     * <p>
     * 工作队列: 存放被提交但尚未被执行的任务的队列, 在线程池中，一般使用一个队列进行任务和线程池中线程进行耦合，工作队列有如下几种：
     * ArrayBlockingQueue：有界的任务队列
     * 可以限定队列的长度，接收到任务的时候，如果没有达到corePoolSize的值，则新建线程(核心线程)执行任务，如果达到了，则入队等候，如果队列已满，
     * 则新建线程(非核心线程)执行任务，如果总线程数到了maximumPoolSize，并且队列也满了，则发生错误
     * 基于数组结构的有界阻塞队列，此队列按照FIFO（先进先出）原则对元素进行排序。
     * 特点:
     * 1.创建队列时，指定队列的最大容量。
     * 2.若有新的任务要执行，如果线程池中的线程数小于corePoolSize，则会优先创建新的线程。若大于corePoolSize，则会将新任务加入到等待队列中。
     * 3.若等待队列已满，无法加入。如果总线程数不大于线程数最大值maximumPoolSize，则创建新的线程执行任务。若大于maximumPoolSize，则执行拒绝策略。
     * <p>
     * LinkedBlockingQueue：无界的任务队列  newFixedThreadPool
     * 队列接收到任务的时候，如果当前线程数小于核心线程数，则新建线程(核心线程)处理任务；如果当前线程数等于核心线程数，则进入队列等待。
     * 由于这个队列没有最大值限制，即所有超过核心线程数的任务都将被添加到队列中，因为总线程数永远不会超过corePoolSize
     * 基于链表结构的有界阻塞队列，也按照FIFO排序元素，吞吐量高于ArrayBlockingQueue。静态工厂方法Executors.newFixedThreadPool(n)使用了此队列。
     * 特点:
     * 1.与有界队列相比，除非系统资源耗尽，否则不存在任务入队失败的情况。
     * 2.若有新的任务要执行，如果线程池中的线程数小于corePoolSize，线程池会创建新的线程。若大于corePoolSize，此时又没有空闲的线程资源，则任务直接进入等待队列。
     * 3.当线程池中的线程数达到corePoolSize后，线程池不会创建新的线程。
     * 4.若任务创建和处理的速度差异很大，无界队列将保持快速增长，直到耗尽系统内存。
     * 5.使用无界队列将导致在所有 corePoolSize 线程都忙时，新任务在队列中等待。这样，创建的线程就不会超过 corePoolSize（因此，maximumPoolSize 的值也就无效了）。
     * 当每个任务完全独立于其他任务，即任务执行互不影响时，适合于使用无界队列；例如，在 Web 页服务器中。这种排队可用于处理瞬态突发请求，
     * 当命令以超过队列所能处理的平均数连续到达时，此策略允许无界线程具有增长的可能性。
     * <p>
     * PriorityBlockingQueue：具有优先级的无界阻塞队列，即优先队列。
     * 特点:
     * 1.带有执行优先级的队列。是一个特殊的无界队列。
     * 2.ArrayBlockingQueue和LinkedBlockingQueue都是按照先进先出算法来处理任务。而PriorityBlockingQueue可根据任务自身的优先级顺序先后执行（总是确保高优先级的任务先执行）。
     * <p>
     * SynchronousQueue：直接提交的任务队列,视为只有一个元素的队列   newCachedThreadPool()
     * 这个队列接收到任务的时候，会直接提交给线程处理，而不保留它，如果所有线程都在工作, 就新建一个线程来处理这个任务！所以为了保证不出现
     * <线程数达到了maximumPoolSize而不能新建线程>的错误，使用这个类型队列的时候，maximumPoolSize一般指定成Integer.MAX_VALUE，即无限大
     * 吞吐量要高于LinkedBlockingQueue，静态工厂方法Executors.newCachedThreadPool()使用了此队列。
     * 特点:
     * 1.SynchronousQueue没有容量。
     * 2.提交的任务不会被真实的保存在队列中，而总是将新任务提交给线程执行。如果没有空闲的线程，则尝试创建新的线程。如果线程数大于最大值maximumPoolSize，则执行拒绝策略。
     * <p>
     * DelayQueue：延时队列
     * 队列内元素必须实现Delayed接口，这就意味着你传进去的任务必须先实现Delayed接口。这个队列接收到任务时，首先先入队，只有达到了指定的延时时间，才会执行任务
     * <p>
     * 饱和策略
     * 饱和策略又称拒绝策略，指的是当线程池中每个线程都在承载线程任务时，这时如果又有了新的线程任务，线程池将会采用的策略，常见线程策略如下：
     * AbortPolicy：该策略是线程池的默认策略。使用该策略时，如果线程池队列满了丢掉这个任务并且抛出RejectedExecutionException异常。
     * CallerRunsPolicy：只用调用这所在的线程来运行任务, 线程池队列满了，会直接丢弃新加入的任务并且不会抛出异常。
     * DiscardOldestPolicy：丢弃队列里最近的一个任务，并执行当前任务, 如果队列满了，会将最早进入队列的线程任务退出，再尝试将新的任务加入队列。
     * DiscardPolicy：当线程池使用此策略，如果添加到线程池失败(线程池满时)，那么主线程将会自己去执行该任务，不会等待线程池中的线程去执行。
     * 自定义: JDK允许我们自定义饱和策略，只要实现RejectedExecutionHandler接口，并且实现rejectedExecution方法就可以了，rejectedExecution方法中定义饱和策略的逻辑代码。
     */
    @Test
    public void threadPoolExecutorTest() throws InterruptedException {
        System.out.println("main start");
        // 线程池维护线程的最少数量
        int corePoolSize = 10;
        // 线程池最大数量
        int maximumPoolSizeSize = 100;
        // 线程活动保持时间
        long keepAliveTime = 1;
        // 线程活动保持时间的单位
        TimeUnit timeUnit = TimeUnit.SECONDS;
        // 有界工作队列, 先进先出
        ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(10);
        // 无界工作队列, 先进先出
//        LinkedBlockingQueue workQueue = new LinkedBlockingQueue();
        // 无界优先工作队列, 按线程优先级高到底执行(总是确保高优先级的任务先执行)
//        PriorityBlockingQueue workQueue = new PriorityBlockingQueue();
        // 线程工厂, 创建线程或线程池时请指定有意义的线程名称，方便出错时回溯。
        BasicThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("ThreadPoolExecutor-%d").daemon(true).build();
        // 饱和策略
        ThreadPoolExecutor.AbortPolicy handler = new ThreadPoolExecutor.AbortPolicy();
        // 创建线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSizeSize, keepAliveTime, timeUnit, workQueue, threadFactory, handler);
        // 向线程池中添加1000个任务
        for (int i = 0; i < 1000; i++) {
            executor.execute(() -> new Thread(()-> System.out.println(Thread.currentThread().getName())).start());
        }
        // 结束线程池
        executor.shutdown();
        // 线程池未终止,main线程sleep1秒
        while (!executor.isTerminated()) {
            Thread.sleep(1000L);
        }

        System.out.println("main end");
    }
}
