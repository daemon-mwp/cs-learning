package mwp202109.cs_learning.config.zookeeper;

import lombok.extern.slf4j.Slf4j;
import mwp202109.cs_learning.util.ServerConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LeaderListener implements LeaderLatchListener {

    @Autowired
    private ServerConfig serverConfig;

    @Override
    public void isLeader() {
        String ip = serverConfig.getUrl();
        if (StringUtils.isNotBlank(ip)) {
            log.info(" [{}] say I am leader", ip);
        } else {
            log.info(" I am leader...");
        }
    }

    @Override
    public void notLeader() {
        String ip = serverConfig.getUrl();
        if (StringUtils.isNotBlank(ip)) {
            log.info("[{}] is not leader now!!!", ip);
        } else {
            log.info(" I am not leader...");
        }
    }
}
