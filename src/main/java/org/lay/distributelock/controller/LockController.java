package org.lay.distributelock.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lay.distributelock.dao.DistributeLockMapper;
import org.lay.distributelock.pojo.DistributeLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 锁测试
 *
 * @author liushaowu
 * @date 2020/1/31 9:34 下午
 */
@Slf4j
@RestController
@RequestMapping(value = "lock")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LockController {

    private final DistributeLockMapper distributeLockMapper;

    @Transactional(rollbackFor = Exception.class)
    @GetMapping(value = "/test")
    public String test() {
        log.info(Thread.currentThread().getName() + ",进入方法");
        try {
            DistributeLock distributeLock = this.distributeLockMapper.selectByBusinessCode("1001");
            log.info("进入锁");
            if (StringUtils.isEmpty(distributeLock)) {
                throw new Exception("查不到数据~~");
            }
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + ",执行完毕");
        return "执行完毕";
    }

}
