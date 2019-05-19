package com.example.multithread.threadpool;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 * ForkJoinPool:是Java7提供的一个用于并行执行任务的框架，是一个把大任务分割成若干个互不依赖小任务，最终汇总每个小任务结果后得到大任务结果的框架。
 * 工作窃取算法:
 * 工作窃取（work-stealing）算法是指某个线程从其他队列里窃取任务来执行。假如需要做一个比较大任务，可以把这个任务分割为若干互不依赖的子任务，
 * 为了减少线程间的竞争，于是把这些子任务分别放到不同的队列里，并为每个队列创建一个单独的线程来执行队列里的任务，线程和队列一一对应，
 * 比如A线程负责处理A队列里的任务。但是有的线程会先把自己队列里的任务干完，而其他线程对应的队列里还有任务等待处理。干完活的线程与其等着，
 * 不如去帮其他线程干活，于是它就去其他线程的队列里窃取一个任务来执行。而在这时它们会访问同一个队列，所以为了减少窃取任务线程和被窃取任务线程之间
 * 的竞争，通常会使用双端队列(所谓双端队列，就是说队列中的元素（ForkJoinTask任务及其子任务）可以从一端入队出队，还可以从另一端入队出队)，被窃取
 * 任务线程永远从双端队列的头部拿任务执行，而窃取任务的线程永远从双端队列的尾部拿任务执行。工作窃取算法的优点是充分利用线程进行并行计算，
 * 并减少了线程间的竞争，其缺点是在某些情况下还是存在竞争，比如双端队列里只有一个任务时。并且消耗了更多的系统资源，比如创建多个线程和多个双端队列。
 * <p>
 * Fork/Join框架的设计:
 * 1. 分割任务。
 * 首先我们需要有一个fork类来把大任务分割成子任务，有可能子任务还是很大，所以还需要不停的分割，直到分割出的子任务足够小。
 * 2. 执行任务并合并结果。
 * 分割的子任务分别放在双端队列里，然后几个启动线程分别从双端队列里获取任务执行。子任务执行完的结果都统一放在一个队列里，
 * 启动一个线程从队列里拿数据，然后合并这些数据。
 * <p>
 * Fork/Join使用两个类来完成以上两件事情：
 * 1.ForkJoinTask：
 * 要使用ForkJoin框架，必须首先创建一个ForkJoin任务。它提供在任务中执行fork()和join()操作的机制，通常情况下不需要直接继承ForkJoinTask类，
 * 而只需要继承它的子类，Fork/Join框架提供了以下两个子类：
 * 1.1. RecursiveAction：用于没有返回结果的任务。
 * 1.2. RecursiveTask ：用于有返回结果的任务。
 * 2.ForkJoinPool ：
 * ForkJoinTask需要通过ForkJoinPool来执行，任务分割出的子任务会添加到当前工作线程所维护的双端队列中，进入队列的头部。
 * 当一个工作线程的队列里暂时没有任务时，它会随机从其他工作线程的队列的尾部获取一个任务。
 *
 * @Auther: Akang
 * @Date: 2019/5/17 11:28
 * @Description:
 */
public class ForkJoinPoolTest {

    /**
     * 计算1+2+3+4的结果。
     * 使用Fork／Join框架首先要考虑到的是如何分割任务，如果我们希望每个子任务最多执行两个数的相加，那么我们设置分割的阈值是2，
     * 由于是4个数字相加，所以Fork／Join框架会把这个任务fork成两个子任务，子任务一负责计算1+2，子任务二负责计算3+4，然后再join两个子任务的结果。
     */
    @Test
    public void test1() {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        //生成一个计算任务，负责计算1+2+3+4
        CountTask task = new CountTask(1, 4);
        //执行一个任务
        Future result = forkJoinPool.submit(task);
        try {
            System.out.println(result.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Data
    @AllArgsConstructor
    class CountTask extends RecursiveTask {
        private static final int THRESHOLD = 2; //阈值
        private int start;
        private int end;

        @Override
        protected Integer compute() {
            int sum = 0;
            boolean canCompute = (end - start) <= THRESHOLD;
            //如果任务足够小就计算任务
            if (canCompute) {
                for (int i = start; i <= end; i++) {
                    sum += i;
                }
            } else {
                //如果任务大于阀值，就分裂成两个子任务计算
                int middle = (start + end) / 2;
                // 1+2
                CountTask leftTask = new CountTask(start, middle);
                // 3+4
                CountTask rightTask = new CountTask(middle + 1, end);
                //执行子任务
                leftTask.fork();
                rightTask.fork();
                //等待子任务执行完，并得到其结果
                int leftResult = (int) leftTask.join();
                int rightResult = (int) rightTask.join();
                //合并子任务
                sum = leftResult + rightResult;
            }
            return sum;
        }
    }
}
