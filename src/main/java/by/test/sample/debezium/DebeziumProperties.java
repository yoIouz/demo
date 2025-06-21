package by.test.sample.debezium;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "debezium")
public class DebeziumProperties {

    private String host;

    private int port;

    private String user;

    private String password;

    private String redisHost;

}
