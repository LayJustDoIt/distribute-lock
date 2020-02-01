package org.lay.distributelock;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.lay.distributelock.zookeeper.lock.ZookeeperLock;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class DistributeLockApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	public void testZkLock() {

		try (ZookeeperLock lock = new ZookeeperLock()) {
			Boolean lockResult = lock.lock("order");
			log.info("获取锁的结果：" + lockResult);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
