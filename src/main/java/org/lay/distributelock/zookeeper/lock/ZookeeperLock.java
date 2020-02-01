package org.lay.distributelock.zookeeper.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * zk分布式锁
 *
 * @author liushaowu
 * @date 2020/2/1 1:39 上午
 */
@Slf4j
public class ZookeeperLock implements AutoCloseable, Watcher {

    private ZooKeeper zooKeeper;

    private String znode;

    public ZookeeperLock() throws IOException {
        this.zooKeeper = new ZooKeeper("192.168.1.12:2181", 10000, this);
    }

    /**
     * 获取锁
     *
     * @param businessCode
     */
    public Boolean lock(String businessCode) {
        try {
            // 创建业务根节点
            Stat stat = this.zooKeeper.exists("/" + businessCode, false);
            // 节点不存在
            if (StringUtils.isEmpty(stat)) {
                this.zooKeeper.create("/" + businessCode, businessCode.getBytes(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            // 创建瞬时有序节点
            znode = this.zooKeeper.create("/" + businessCode
                            + "/" + businessCode + "_", businessCode.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            // 获取所有子节点
            List<String> childrenNodes = this.zooKeeper.getChildren("/" + businessCode, false);
            // 子节点排序
            Collections.sort(childrenNodes);
            // 获取最小节点
            String firstNode = childrenNodes.get(0);
            if (znode.endsWith(firstNode)) {
                return true;
            }
            // 存储第一个节点
            String lastNode = firstNode;
            for (String node : childrenNodes) {
                if (znode.endsWith(node)) {
                    // 进行监听
                    this.zooKeeper.exists("/" + businessCode + "/" + lastNode, true);
                    break;
                } else {
                    lastNode = node;
                }
            }
            synchronized (this) {
                wait();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 锁释放
     *
     * @throws Exception
     */
    public void unlock() throws Exception {
        this.zooKeeper.delete(znode, -1);
        this.zooKeeper.close();
        log.info("线程: {}", Thread.currentThread().getName() + "释放锁！");
    }

    @Override
    public void close() throws Exception {
        this.unlock();
    }

    /**
     * 监听节点消失此方法执行
     *
     * @param watchedEvent
     */
    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType().equals(Event.EventType.NodeDeleted)) {
            synchronized (this) {
                notify();
            }
        }
    }

}
