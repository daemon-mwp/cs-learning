package mwp202109.cs_learning.config.elasticsearch;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "spring.elasticsearch.rest")
@Setter
@Getter
public class RestClientProperties {

    /**
     * Elasticsearch URI list
     */
    private List<String> uris;

    /**
     * username for basic auth
     */
    private String username;

    /**
     * password for basic auth
     */
    private String password;

    /**
     * connection timeout
     */
    private Integer connectionTimeout;

    /**
     * read timeout
     */
    private Integer readTimeout;

}
