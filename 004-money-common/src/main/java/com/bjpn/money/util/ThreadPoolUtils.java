package com.bjpn.money.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtils {
    // 核心线程数
    private static final int CORE_POOL_SIZE = 50;
    // 最大线程数
    private static final int MAX_POOL_ZISE = 1000;
    // 存活时间
    private static final Long KEEP_ALIVE_TIME = 60L;

    //私有化构造方法
    private ThreadPoolUtils() {
    }
    //内部类
    private static class ThreadPoolHolder {
        //创建线程池对象
        private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_ZISE,
                KEEP_ALIVE_TIME, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    }
    //获得线程对象不带任务
    public static ExecutorService getExecutorService() {
        //获得线程池
        return ThreadPoolHolder.executor;
    }
    //获得线程带任务并执行(一般调用这个方法)
    public static void execute(Runnable task) {
        //线程池对象执行线程任务   放入runnable
        ThreadPoolHolder.executor.execute(getExcpThread(task));
    }
    //调用者传任务和异常
    public static void execute(Runnable runnable, Thread.UncaughtExceptionHandler excpHandler) {
        //取带有异常处理的线程
        Thread thread = getExcpThread(runnable);
        if (excpHandler != null)
            thread.setUncaughtExceptionHandler(excpHandler);
        //线程池对象执行线程任务   放入thread
        ThreadPoolHolder.executor.execute(thread);
    }

    /**
     * 内部类
     * 默认线程异常处理器
     */
    static class ThreadUncaughtException implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            // log.error....
        }
    }

    /**
     * 获取带有异常处理的线程
     */
    public static Thread getExcpThread(Runnable task) {
        Thread taskThread = null;
        if (task instanceof Thread)
            taskThread = (Thread) task;
        else
            taskThread = new Thread(task);

        if (taskThread.getUncaughtExceptionHandler() == null)
            taskThread.setUncaughtExceptionHandler(new ThreadUncaughtException());
        return taskThread;
    }
}
