package org.lay.distributelock.zookeeper.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.lay.distributelock.zookeeper.config.CuratorClientConfig;
import org.lay.distributelock.zookeeper.lock.ZookeeperLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * zk 分布式锁
 *
 * @author liushaowu
 * @date 2020/2/1 12:28 下午
 */
@Slf4j
@RestController
@RequestMapping(value = "/zklock")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ZkDistributeLockController {

    private static final long TIME_OUT = 30;

    private final CuratorClientConfig curatorClientConfig;

    /**
     * zk dis lock
     *
     * @return
     */
    @GetMapping(value = "/test")
    public String test() {
        try (ZookeeperLock zookeeperLock = new ZookeeperLock()) {
            final Boolean lockResult = zookeeperLock.lock("order");
            if (lockResult) {
                log.info("线程：{} ", Thread.currentThread().getName() + "获取锁");
                Thread.sleep(10000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("线程：{} ", Thread.currentThread().getName() + "执行完毕");
        return "方法执行完毕";
    }

    /**
     * curator client dis lock
     *
     * @return
     */
    @GetMapping(value = "/curator")
    public String curatorLock() {
        log.info("线程：{} ", Thread.currentThread().getName() + "进入方法");
        final InterProcessMutex interProcessMutex =
                new InterProcessMutex(this.curatorClientConfig.curatorFramework(), "/order");
        try {
            if (interProcessMutex.acquire(TIME_OUT, TimeUnit.SECONDS)) {
                log.info("线程：{} ", Thread.currentThread().getName() + "获取锁");
                Thread.sleep(10000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                interProcessMutex.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("线程：{} ", Thread.currentThread().getName() + "执行完毕");
        return "执行完毕";
    }

}
