package org.lay.distributelock.zookeeper.lock;

import lombok.RequiredArgsConstructor;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.lay.distributelock.zookeeper.config.CuratorClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * curator lock
 *
 * @author liushaowu
 * @date 2020/2/1 2:48 下午
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CuratorLock {

    private static final long TIME_OUT = 30;

    private final CuratorClientConfig curatorClientConfig;

    /**
     * 获取锁
     */
    public void lock() {
        InterProcessMutex lock = new InterProcessMutex(this.curatorClientConfig.curatorFramework(), "/order");
        try {
            if (lock.acquire(TIME_OUT, TimeUnit.SECONDS)) {
                try {
                    System.out.println("我获得了锁");
                } finally {
                    lock.release();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
