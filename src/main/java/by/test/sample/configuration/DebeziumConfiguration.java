package by.test.sample.configuration;

import by.test.sample.debezium.DebeziumProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class DebeziumConfiguration {

    private final DebeziumProperties properties;

    @Bean
    public io.debezium.config.Configuration debeziumConfig(DataSource ds) {
        var props = io.debezium.config.Configuration.create()
                .with("name", "connector")
                .with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
                .with("database.hostname", properties.getHost())
                .with("database.port", properties.getPort())
                .with("database.user", properties.getUser())
                .with("database.password", properties.getPassword())
                .with("database.dbname", "demo")
                .with("database.server.name", "dbserver")
                .with("plugin.name", "pgoutput")
                .with("slot.name", "debezium_slot")
                .with("publication.autocreate.mode", "disabled")
                .with("publication.name", "dbz_publication")
                .with("table.include.list", "public.users,public.email_data,public.phone_data")
                .with("topic.prefix", "debezium")
                .with("slot.drop.on.stop", "true")
                .with("offset.storage", "by.test.sample.debezium.RedisOffsetBackingStore")
                .with("offset.storage.redis.uri", "redis://" + properties.getRedisHost() + ":6379")
                .build();
        return props;
    }

}
