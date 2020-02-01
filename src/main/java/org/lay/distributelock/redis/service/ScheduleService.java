package org.lay.distributelock.redis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lay.distributelock.redis.lock.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 测试
 *
 * @author liushaowu
 * @date 2020/2/1 1:01 上午
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ScheduleService {

    private final RedisTemplate redisTemplate;

    @Scheduled(cron = "0/5 * * * * ?")
    public void sendSms() {
        try (RedisLock redisLock = new RedisLock("sendSMS", 20, this.redisTemplate)) {
            redisLock.getLock();
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("向110发送短信");
    }

}
