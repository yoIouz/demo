package by.test.sample.configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfiguration {

    @Bean
    public ElasticsearchClient elasticsearchClient(RestClient restClient, ObjectMapper mapper) {
        JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper(mapper);
        RestClientTransport transport = new RestClientTransport(restClient, jsonpMapper);
        return new ElasticsearchClient(transport);
    }
}
