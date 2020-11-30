package evans18.lunatechairportsbackend.data.model;

import com.opencsv.bean.CsvBindByName;
import evans18.lunatechairportsbackend.data.repository.ElasticSearchConstants;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
@Document(indexName = ElasticSearchConstants.DOCUMENT_INDEX_COUNTRIES)
public class Country {
    @CsvBindByName
    private int id;
    /**
     * Searchable field.
     */
    @Field(ElasticSearchConstants.ES_DOC_COUNTRY_FIELD_COUNTRY_NAME)
    @CsvBindByName
    private String code;
    /**
     * Searchable field.
     */
    @Field(ElasticSearchConstants.ES_DOC_COUNTRY_FIELD_COUNTRY_CODE)
    @CsvBindByName
    private String name;
    @CsvBindByName
    private String continent;
    @CsvBindByName
    private String wikipedia_link;
    @CsvBindByName
    private String keywords;
}
