package evans18.lunatechairportsbackend.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import evans18.lunatechairportsbackend.data.model.Airport;
import evans18.lunatechairportsbackend.data.model.Country;
import evans18.lunatechairportsbackend.data.model.Runway;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@AllArgsConstructor
@Component
public class CsvDataLoader {
    private static final String FILE_NAME_CSV_COUNTRIES = "/static/countries.csv";
    private static final String FILE_NAME_CSV_AIRPORTS = "/static/airports.csv";
    private static final String FILE_NAME_CSV_RUNWAYS = "/static/runways.csv";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @PostConstruct
    public void loadData() throws IOException, URISyntaxException {
        loadCSVData(CsvDataLoader.class.getResource(FILE_NAME_CSV_COUNTRIES), Country.class);
        loadCSVData(CsvDataLoader.class.getResource(FILE_NAME_CSV_AIRPORTS),  Airport.class);
        loadCSVData(CsvDataLoader.class.getResource(FILE_NAME_CSV_RUNWAYS),  Runway.class);
    }

    private <T> void loadCSVData(URL url, Class<T> clazz) throws IOException, URISyntaxException {
        @Cleanup Reader reader = Files.newBufferedReader(Paths.get(url.toURI()));

        CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                .withType(clazz)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        List<T> listItems = csvToBean.parse();

        log.info("Loaded " + listItems.size() + " instances of: " + clazz.getSimpleName());
    }


}
