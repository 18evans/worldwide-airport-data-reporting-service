package evans18.lunatechairportsbackend.data;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import evans18.lunatechairportsbackend.data.model.Airport;
import evans18.lunatechairportsbackend.data.model.Country;
import evans18.lunatechairportsbackend.data.model.Runway;
import evans18.lunatechairportsbackend.data.repository.AirportRepository;
import evans18.lunatechairportsbackend.data.repository.RunwayRepository;
import evans18.lunatechairportsbackend.data.repository.country.CountryRepository;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * At runtime loads items from CSV files and inserts into local database.
 */
@AllArgsConstructor
@Component
public class CsvDataLoader {
    private static final String FILE_NAME_CSV_COUNTRIES = "/static/countries.csv";
    private static final String FILE_NAME_CSV_AIRPORTS = "/static/airports.csv";
    private static final String FILE_NAME_CSV_RUNWAYS = "/static/runways.csv";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final AirportRepository airportRepo;
    private final RunwayRepository runwayRepo;
    private final CountryRepository countryRepo;

    @PostConstruct
    public void loadData() throws IOException, URISyntaxException {
        loadCSVData(CsvDataLoader.class.getResource(FILE_NAME_CSV_COUNTRIES), Country.class, countryRepo);
        loadCSVData(CsvDataLoader.class.getResource(FILE_NAME_CSV_AIRPORTS), Airport.class, airportRepo);
        loadCSVData(CsvDataLoader.class.getResource(FILE_NAME_CSV_RUNWAYS), Runway.class, runwayRepo);
    }

    private <T, ID> void loadCSVData(URL url, Class<T> clazz, CrudRepository<T, ID> repository) throws IOException, URISyntaxException {
        @Cleanup Reader reader = Files.newBufferedReader(Paths.get(url.toURI()));

        CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                .withType(clazz)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        List<T> listItems = csvToBean.parse();
        repository.saveAll(listItems);

        log.info("Loaded " + listItems.size() + " instances of: " + clazz.getSimpleName());
    }


}
