package evans18.lunatechairportsbackend.data.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
@Document(indexName = "country")
public class Country {
    @CsvBindByName
    private int id;
    @CsvBindByName
    private String code;
    @Field
    @CsvBindByName
    private String name;
    @CsvBindByName
    private String continent;
    @CsvBindByName
    private String wikipedia_link;
    @CsvBindByName
    private String keywords;
}
