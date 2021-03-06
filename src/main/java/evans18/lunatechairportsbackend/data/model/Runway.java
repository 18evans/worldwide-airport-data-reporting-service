package evans18.lunatechairportsbackend.data.model;

import com.opencsv.bean.CsvBindByName;
import evans18.lunatechairportsbackend.data.repository.ElasticSearchConstants;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = ElasticSearchConstants.DOCUMENT_INDEX_RUNWAYS)
public class Runway {
    @Id
    @CsvBindByName
    private int id;
    /**
     * Foreign key for Airport id.
     * <p>
     * Note Query expression {@link evans18.lunatechairportsbackend.data.repository.runway.RunwayRepository#findAllByAirportRef(int)}
     * demands variables to not have underscore character.
     */
    @CsvBindByName(column = "airport_ref")
    private int airportRef;
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
