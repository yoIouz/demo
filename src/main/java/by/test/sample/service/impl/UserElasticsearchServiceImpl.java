package by.test.sample.service.impl;

import by.test.sample.document.UserElasticDocument;
import by.test.sample.service.ElasticsearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

import static by.test.sample.utils.ApplicationConstants.USERS_ELASTIC_INDEX;

@Service
@RequiredArgsConstructor
public class UserElasticsearchServiceImpl implements ElasticsearchService<UserElasticDocument, Long> {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void index(UserElasticDocument entity) {
        if (entity != null) {
            IndexQuery indexQuery = new IndexQueryBuilder()
                    .withId(entity.getId().toString())
                    .withObject(entity)
                    .build();
            elasticsearchOperations.index(indexQuery, IndexCoordinates.of(USERS_ELASTIC_INDEX));
        }
    }

    @Override
    public void delete(Long id) {
        if (id != null) {
            elasticsearchOperations.delete(id.toString(), IndexCoordinates.of(USERS_ELASTIC_INDEX));
        }
    }
}
