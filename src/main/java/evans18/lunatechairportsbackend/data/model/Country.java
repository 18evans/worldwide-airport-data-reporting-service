package evans18.lunatechairportsbackend.data.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "country")
public class Country {
    @CsvBindByName
    private int id;
    @CsvBindByName
    private String code;
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
