package evans18.lunatechairportsbackend.data.model;

import com.google.gson.annotations.SerializedName;
import com.opencsv.bean.CsvBindByName;
import evans18.lunatechairportsbackend.data.repository.ElasticSearchConstants;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
@Document(indexName = ElasticSearchConstants.DOCUMENT_INDEX_AIRPORTS)
public class Airport {

    @CsvBindByName
    private int id;
    /**
     * Searchable field.
     */
    @CsvBindByName
    private String ident;
    @CsvBindByName
    private String type;
    @CsvBindByName
    private String name;
    @CsvBindByName
    private double latitude_deg;
    @CsvBindByName
    private double longitude_deg;
    @CsvBindByName
    private double elevation_ft;
    @CsvBindByName
    private String continent;
    /**
     * Searchable field.
     */
    @CsvBindByName
    @SerializedName(ElasticSearchConstants.ES_DOC_AIRPORT_FIELD_ISO_COUNTRY_CODE)
    @Field(ElasticSearchConstants.ES_DOC_AIRPORT_FIELD_ISO_COUNTRY_CODE)
    private String iso_country;
    @CsvBindByName
    private String municipality;
    @CsvBindByName
    private String scheduled_service;
    @CsvBindByName
    private String gps_code;
    @CsvBindByName
    private String iata_code;
    @CsvBindByName
    private String local_code;
    @CsvBindByName
    private String home_link;
    @CsvBindByName
    private String wikipedia_link;
    @CsvBindByName
    private String keywords;
}
