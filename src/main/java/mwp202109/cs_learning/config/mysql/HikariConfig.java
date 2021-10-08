package mwp202109.cs_learning.config.mysql;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Mr.裴
 * @create 2019/9/27
 * @Description: Hikari多数据源配置
 **/
@Configuration
public class HikariConfig {

    @Bean(name = "dataSourceDb1")
    @ConfigurationProperties("spring.datasource.db1")
    public DataSource dataSourceDb1(HikariProperties properties)
    {
        HikariDataSource dataSource = DataSourceBuilder.create().type(HikariDataSource.class).build();
        return properties.dataSource(dataSource);
    }

    @Bean(name = "dataSourceCK1")
    @ConfigurationProperties("spring.datasource.ck1")
    @ConditionalOnProperty(prefix = "spring.datasource.ck1", name = "enabled", havingValue = "true")
    public DataSource dataSourceCK1(HikariProperties properties)
    {
        HikariDataSource dataSource = DataSourceBuilder.create().type(HikariDataSource.class).build();
        return properties.dataSource(dataSource);
    }

    @Primary
    @Bean(name = "dynamicDataSource")
    public DynamicDataSource dataSource(DataSource dataSourceDb1,
                                        DataSource dataSourceCK1)
    {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.DB1.name(), dataSourceDb1);
        targetDataSources.put(DataSourceType.CK1.name(), dataSourceCK1);
        return new DynamicDataSource(dataSourceDb1, targetDataSources);
    }

}
