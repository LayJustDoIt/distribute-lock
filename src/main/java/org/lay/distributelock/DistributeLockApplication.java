package org.lay.distributelock;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * main program
 *
 * @author liushaowu
 */
//@ImportResource("classpath:redisson.xml")
//@EnableScheduling
@MapperScan(basePackages = {"org.lay.distributelock.dao"})
@SpringBootApplication(scanBasePackages = {"org.lay.distributelock"})
public class DistributeLockApplication {

	public static void main(String[] args) {
		SpringApplication.run(DistributeLockApplication.class, args);
	}

}
