package mwp202109.cs_learning.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HikariProperties {

    @Value("${spring.datasource.hikari.minimum-idle}")
    private int minIdle;

    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private int maxPoolSize;

    @Value("${spring.datasource.hikari.idle-timeout}")
    private int idleTimeout;

    @Value("${spring.datasource.hikari.max-lifetime}")
    private int maxLifetime;

    @Value("${spring.datasource.hikari.connection-timeout}")
    private int connectionTimeout;


    public HikariDataSource dataSource(HikariDataSource dataSource) {
        //配置Hikari连接池
        dataSource.setConnectionTimeout(connectionTimeout);//连接超时时间设置
        dataSource.setIdleTimeout(idleTimeout);//连接空闲生命周期设置
        dataSource.setMaximumPoolSize(maxPoolSize);//连接池允许的最大连接数量
        dataSource.setMaxLifetime(maxLifetime);//检查空余连接优化连接池设置时间,单位毫秒
        dataSource.setMinimumIdle(minIdle);//连接池保持最小空余连接数量
        return dataSource;
    }


}
