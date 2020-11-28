package evans18.lunatechairportsbackend.data.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class Runway {
    @CsvBindByName
    private String id;
    @CsvBindByName
    private String airport_ref;
    @CsvBindByName
    private String airport_ident;
    @CsvBindByName
    private String length_ft;
    @CsvBindByName
    private String width_ft;
    @CsvBindByName
    private String surface;
    @CsvBindByName
    private String lighted;
    @CsvBindByName
    private String closed;
    @CsvBindByName
    private String le_ident;
    @CsvBindByName
    private String le_latitude_deg;
    @CsvBindByName
    private String le_longitude_deg;
    @CsvBindByName
    private String le_elevation_ft;
    @CsvBindByName
    private String le_heading_degT;
    @CsvBindByName
    private String le_displaced_threshold_ft;
    @CsvBindByName
    private String he_ident;
    @CsvBindByName
    private String he_latitude_deg;
    @CsvBindByName
    private String he_longitude_deg;
    @CsvBindByName
    private String he_elevation_ft;
    @CsvBindByName
    private String he_heading_degT;
    @CsvBindByName
    private String he_displaced_threshold_ft;
}
