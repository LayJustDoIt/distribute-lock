package org.lay.distributelock.redis.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lay.distributelock.redis.lock.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * redis分布式锁
 *
 * @author liushaowu
 * @date 2020/1/31 11:55 下午
 */
@Slf4j
@RestController
@SuppressWarnings("unchecked")
@RequestMapping(value = "/redis")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RedisLockController {

    private final RedisTemplate redisTemplate;

    @GetMapping(value = "/lock")
    public String lock() {
        System.out.println("进入方法");
        String key = "redisKey";
        try (RedisLock redisLock = new RedisLock(key, 30, redisTemplate)) {
            if (redisLock.getLock()) {
               log.info("进入redis锁");
               Thread.sleep(15000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("方法执行完成");
        return "方法执行完成";
    }

}
