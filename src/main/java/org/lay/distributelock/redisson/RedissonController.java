package org.lay.distributelock.redisson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * redisson 分布式锁
 *
 * @author liushaowu
 * @date 2020/2/1 3:49 下午
 */
@Slf4j
@RestController
@RequestMapping(value = "redisson")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RedissonController {

    private final RedissonClient redissonClient;

    @GetMapping(value = "/lock")
    public String testRedisson() {
        log.info("线程：{}" , Thread.currentThread().getName() + "进入方法。");
        // 1. Create config object
        /*final Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://192.168.1.12:6379").setPassword("redis-dev");
        // 2. Sync and Async API
        final RedissonClient redissonClient = Redisson.create(config);*/
        final RLock lock = redissonClient.getLock("Lay");
        lock.lock();
        log.info("线程：{}" , Thread.currentThread().getName() + "获取锁🔐。");
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            log.info("线程：{}" , Thread.currentThread().getName() + "释放锁🔐");
            lock.unlock();
        }
        log.info("线程：{}" , Thread.currentThread().getName() + "执行完毕。");
        return "执行完毕";
    }

}
