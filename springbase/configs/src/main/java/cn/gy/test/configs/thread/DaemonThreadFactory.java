package cn.gy.test.configs.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DaemonThreadFactory implements ThreadFactory {

    private final ThreadGroup group;

    private final String namePrefix;

    private AtomicInteger threadNumber = new AtomicInteger(1);

    private AtomicInteger poolNumber = new AtomicInteger(1);

    public DaemonThreadFactory(String name) {
        SecurityManager sm = System.getSecurityManager();
        if (null != sm) {
            group = sm.getThreadGroup();
        } else {
            group = Thread.currentThread().getThreadGroup();
        }
        namePrefix = "pool-" + name + "-" + poolNumber.getAndIncrement() + "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        if (!t.isDaemon()) {
            t.setDaemon(true);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }

}
