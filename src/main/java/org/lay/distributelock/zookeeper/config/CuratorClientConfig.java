package org.lay.distributelock.zookeeper.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * curator 客户端封装
 *
 * @author liushaowu
 * @date 2020/2/1 2:55 下午
 */
@Configuration
public class CuratorClientConfig {

    @Bean(initMethod = "start", destroyMethod = "close")
    public CuratorFramework curatorFramework() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        return CuratorFrameworkFactory.newClient("192.168.1.12:2181", retryPolicy);
    }

}
