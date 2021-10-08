package mwp202109.cs_learning.config.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

@Configuration
public class ZookeeperConfig {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.zookeeper.server}")
    private String host;
    @Value("${spring.zookeeper.maxRetries}")
    private int maxRetry;
    @Value("${spring.zookeeper.sessionTimeoutMs}")
    private int sessionTimeout;
    @Value("${spring.zookeeper.connectionTimeoutMs}")
    private int connectTimeout;

    @Bean
    public CuratorFramework curatorFramework(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 1);
        CuratorFramework client =
                CuratorFrameworkFactory.builder()
                        .connectString(host)
                        .sessionTimeoutMs(sessionTimeout)
                        .connectionTimeoutMs(connectTimeout)
                        .retryPolicy(retryPolicy)
                        .build();
        client.start();
        return client;
    }

    @PreDestroy
    private void destroyClient(){
        curatorFramework().close();
        logger.info("==================关闭成功==================");
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getMaxRetry() {
        return maxRetry;
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
}
