package evans18.lunatechairportsbackend.data.model;

import com.opencsv.bean.CsvBindByName;
import evans18.lunatechairportsbackend.Constants;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
@Document(indexName = Constants.DOCUMENT_INDEX_AIRPORTS)
public class Airport {
    public static final String AIRPORT_FIELD_COUNTRY_CODE = "isoCountry";

    @CsvBindByName
    private int id;
    /**
     * Searchable field.
     */
    @CsvBindByName
    private String ident;
    @CsvBindByName
    @Transient
    private String type;
    @CsvBindByName
    @Transient
    private String name;
    @CsvBindByName
    @Transient
    private double latitude_deg;
    @CsvBindByName
    @Transient
    private double longitude_deg;
    @CsvBindByName
    @Transient
    private double elevation_ft;
    @CsvBindByName
    @Transient
    private String continent;
    /**
     * Searchable field.
     */
    @CsvBindByName
    @Field(AIRPORT_FIELD_COUNTRY_CODE)
    private String iso_country;
    @CsvBindByName
    @Transient
    private String municipality;
    @CsvBindByName
    @Transient
    private String scheduled_service;
    @CsvBindByName
    @Transient
    private String gps_code;
    @CsvBindByName
    @Transient
    private String iata_code;
    @CsvBindByName
    @Transient
    private String local_code;
    @CsvBindByName
    @Transient
    private String home_link;
    @CsvBindByName
    @Transient
    private String wikipedia_link;
    @CsvBindByName
    @Transient
    private String keywords;
}
