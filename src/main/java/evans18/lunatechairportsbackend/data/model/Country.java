package evans18.lunatechairportsbackend.data.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
public class Country {
    @CsvBindByName
    private int id;
    @CsvBindByName
    private String code;
    @CsvBindByName
    private String name;
    @CsvBindByName
    private String continent;
    @CsvBindByName
    private String wikipedia_link;
    @CsvBindByName
    private String keywords;
}
