package mwp202109.cs_learning.config.zookeeper;

import mwp202109.cs_learning.util.ServerConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.UUID;

@Component
public class MyLeaderLatch {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private LeaderLatch leaderLatch;

    @Autowired
    private ServerConfig serverConfig;

    @Value("${spring.zookeeper.path}")
    private String path;

    @Autowired
    public void setLeaderLatch(CuratorFramework client, LeaderListener listener) {
        String ip = serverConfig.getUrl();
        if (StringUtils.isBlank(ip)) {
            ip = UUID.randomUUID().toString();
            logger.warn("未获取到IP地址, 随机生成UUID: [{}]", ip);
        }
        try {
            this.leaderLatch = new LeaderLatch(client, path, ip);
            this.leaderLatch.addListener(listener);
        } catch (Exception e) {
            logger.error("主节点选举失败", e);
            CloseableUtils.closeQuietly(client);
        }
    }

    public LeaderLatch getLeaderLatch() {
        return this.leaderLatch;
    }

    /**
     * 启动选举
     */
    @PostConstruct
    public void start() {
        if (this.leaderLatch != null) {
            try {
                this.leaderLatch.start();
                logger.info("{} init Success...", this.leaderLatch.getId());
            } catch (Exception e) {
                logger.error("启动选举失败", e);
                CloseableUtils.closeQuietly(this.leaderLatch);
            }
        }
    }

    public Boolean hasLeadership() {
        return this.leaderLatch != null && this.leaderLatch.hasLeadership();
    }

    public void close() throws IOException {
        if (this.leaderLatch != null) {
            this.leaderLatch.close();
            String ip = serverConfig.getUrl();
            logger.info(ip + " Closing...");
        }
    }

    public void close(LeaderLatch.CloseMode closeMode) throws IOException {
        if (this.leaderLatch != null) {
            this.leaderLatch.close(closeMode);
            String ip = serverConfig.getUrl();
            logger.info(ip + " Closing...");
        }
    }
}
