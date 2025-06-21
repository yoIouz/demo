package by.test.sample.service;

public interface ElasticsearchService<T, ID> {

    void index(T entity);

    void delete(ID id);

}
