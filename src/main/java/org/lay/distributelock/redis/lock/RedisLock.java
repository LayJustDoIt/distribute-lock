package org.lay.distributelock.redis.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 分布式锁 for redis
 *
 * @author liushaowu
 * @date 2020/2/1 12:43 上午
 */
@Slf4j
public class RedisLock implements AutoCloseable {

    private String key;
    private String value;

    private long expire;

    private RedisTemplate redisTemplate;

    public RedisLock(String key, long expire, RedisTemplate redisTemplate) {
        this.key = key;
        this.value = UUID.randomUUID().toString().trim();
        this.expire = expire;
        this.redisTemplate = redisTemplate;
    }

    public Boolean getLock() {
        RedisCallback<Boolean> redisCallback = connection -> {

            RedisStringCommands.SetOption option = RedisStringCommands.SetOption.ifAbsent();

            Expiration seconds = Expiration.seconds(30);
            byte[] binaryKey = this.redisTemplate.getKeySerializer().serialize(key);
            byte[] binaryValue = this.redisTemplate.getValueSerializer().serialize(value);

            return connection.set(binaryKey, binaryValue, seconds, option);
        };

        return (Boolean) this.redisTemplate.execute(redisCallback);
    }

    /**
     * 释放锁
     *
     * @return
     */
    public Boolean unlock() {
        String luaScript = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                            "    return redis.call(\"del\",KEYS[1])\n" +
                            "else\n" +
                            "    return 0\n" +
                            "end";
        RedisScript<Boolean> redisScript = RedisScript.of(luaScript, Boolean.class);
        // 封装key和value
        List<String> list = Collections.singletonList(key);
        // 释放锁
        Boolean result = (Boolean) this.redisTemplate.execute(redisScript, list, value);
        log.info("释放锁：" + result);
        return result;
    }

    @Override
    public void close() {
        this.unlock();
    }
}
