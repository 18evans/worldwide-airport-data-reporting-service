package evans18.lunatechairportsbackend.data.model;

import com.opencsv.bean.CsvBindByName;
import evans18.lunatechairportsbackend.Constants;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = Constants.DOCUMENT_INDEX_RUNWAYS)
public class Runway {
    @CsvBindByName
    private int id;
    @CsvBindByName
    private int airport_ref;
    @CsvBindByName
    private String airport_ident;
    @CsvBindByName
    private int length_ft;
    @CsvBindByName
    private int width_ft;
    @CsvBindByName
    private String surface;
    @CsvBindByName
    private boolean lighted;
    @CsvBindByName
    private boolean closed;
    @CsvBindByName
    private String le_ident;
    @CsvBindByName
    private double le_latitude_deg;
    @CsvBindByName
    private double le_longitude_deg;
    @CsvBindByName
    private double le_elevation_ft;
    @CsvBindByName
    private double le_heading_degT;
    @CsvBindByName
    private double le_displaced_threshold_ft;
    @CsvBindByName
    private String he_ident;
    @CsvBindByName
    private double he_latitude_deg;
    @CsvBindByName
    private double he_longitude_deg;
    @CsvBindByName
    private double he_elevation_ft;
    @CsvBindByName
    private double he_heading_degT;
    @CsvBindByName
    private double he_displaced_threshold_ft;
}
