package by.test.sample.service.impl;

import by.test.sample.dto.PageDto;
import by.test.sample.dto.UserDto;
import by.test.sample.dto.UserFilter;
import by.test.sample.enums.SearchEngineType;
import by.test.sample.service.SearchEngine;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.DateRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static by.test.sample.utils.ApplicationConstants.USERS_ELASTIC_INDEX;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticSearchEngine implements SearchEngine {

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public PageDto<UserDto> findAllUsers(Pageable pageable) {
        SearchRequest searchRequest = SearchRequest.of(sr -> sr
                .index(USERS_ELASTIC_INDEX)
                .query(q -> q.matchAll(m -> m))
                .from((int) pageable.getOffset())
                .size(pageable.getPageSize())
        );
        return this.getUserDtoPage(pageable, searchRequest);
    }

    @Override
    public PageDto<UserDto> searchUsers(UserFilter filter, Pageable pageable) {
        SearchRequest searchRequest = SearchRequest.of(sr -> sr
                .index(USERS_ELASTIC_INDEX)
                .query(q -> q.bool(this.buildSearchQuery(filter)))
                .from((int) pageable.getOffset())
                .size(pageable.getPageSize())
        );
        return this.getUserDtoPage(pageable, searchRequest);
    }

    @Override
    public SearchEngineType getType() {
        return SearchEngineType.ELASTIC;
    }

    private BoolQuery buildSearchQuery(UserFilter filter) {
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        List<Query> mustQueries = new ArrayList<>();
        if (filter.getDateOfBirth() != null) {
            DateRangeQuery dateRangeQuery = DateRangeQuery.of(d -> d
                    .field("dateOfBirth")
                    .gte(filter.getDateOfBirth().toString()));
            mustQueries.add(Query.of(q -> q.range(r -> r.date(dateRangeQuery))));
        }
        if (filter.getPhone() != null && !filter.getPhone().isEmpty()) {
            mustQueries.add(Query.of(q -> q.term(t -> t
                    .field("phones.keyword")
                    .value(v -> v.stringValue(filter.getPhone()))
            )));
        }
        if (filter.getEmail() != null && !filter.getEmail().isEmpty()) {
            mustQueries.add(Query.of(q -> q.term(t -> t
                    .field("emails.keyword")
                    .value(v -> v.stringValue(filter.getEmail()))
            )));
        }
        if (filter.getName() != null && !filter.getName().isEmpty()) {
            mustQueries.add(Query.of(q -> q.match(m -> m
                    .field("name")
                    .query(filter.getName())
            )));
        }
        return boolQuery.must(mustQueries).build();
    }

    private PageDto<UserDto> getUserDtoPage(Pageable pageable, SearchRequest searchRequest) {
        try {
            SearchResponse<UserDto> response = elasticsearchClient.search(searchRequest, UserDto.class);
            List<UserDto> content = response.hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .toList();

            long totalHits = response.hits().total() != null
                    ? response.hits().total().value()
                    : content.size();
            return new PageDto<>(content, pageable.getPageNumber(), pageable.getPageSize(), totalHits);
        } catch (IOException e) {
            log.error("Failed to execute Elasticsearch query", e);
            return PageDto.empty();
        }
    }
}
