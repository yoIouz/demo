package by.test.sample.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Document(indexName = "users")
public class UserElasticDocument {

    @Field(type = FieldType.Keyword)
    private Long id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Date)
    private LocalDate dateOfBirth;

    @Field(type = FieldType.Keyword)
    private List<String> emails;

    @Field(type = FieldType.Keyword)
    private List<String> phones;
}
