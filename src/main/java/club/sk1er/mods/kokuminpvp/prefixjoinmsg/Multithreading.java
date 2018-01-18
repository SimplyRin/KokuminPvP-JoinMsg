package club.sk1er.mods.kokuminpvp.prefixjoinmsg;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Multithreading {

    static AtomicInteger counter = new AtomicInteger(0);
    public static ExecutorService POOL = Executors.newCachedThreadPool(r -> new Thread(r, String.format("Thread %s", counter.incrementAndGet())));
    private static ScheduledExecutorService RUNNABLE_POOL = Executors.newScheduledThreadPool(4, r -> new Thread(r, String.format("Thread " + counter.incrementAndGet())));

    public static void runBackground(Runnable runnable) {
        POOL.execute(runnable);
    }

    public static void schedule(Runnable r, long initialDelay, long delay, TimeUnit unit) {
        RUNNABLE_POOL.scheduleAtFixedRate(r, initialDelay, delay, unit);
    }

    public static void runAsync(Runnable runnable) {
        POOL.execute(runnable);
    }

    public static int getTotal() {
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) Multithreading.POOL;
        return tpe.getActiveCount();
    }

}