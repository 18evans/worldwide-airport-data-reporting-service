package evans18.lunatechairportsbackend.data.model;

import com.opencsv.bean.CsvBindByName;
import evans18.lunatechairportsbackend.data.repository.ElasticSearchConstants;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = ElasticSearchConstants.DOCUMENT_INDEX_COUNTRIES)
public class Country {
    @CsvBindByName
    private int id;
    /**
     * Searchable field.
     */
    @CsvBindByName
    private String code;
    /**
     * Searchable field.
     */
    @CsvBindByName
    private String name;
    @Transient
    @CsvBindByName
    private String continent;
    @Transient
    @CsvBindByName
    private String wikipedia_link;
    @Transient
    @CsvBindByName
    private String keywords;
}
