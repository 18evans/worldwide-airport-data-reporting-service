package evans18.lunatechairportsbackend.controller;

import evans18.lunatechairportsbackend.data.manager.ReportManager;
import evans18.lunatechairportsbackend.data.model.response.CountryWithAirportCount;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportManager reportManager;

    /**
     * Finds the countries
     *
     * @param isMost - default value {@code true} - find countries with most airports. Else {@code false} those with least airports.
     * @return - sorted top 10 countries with most/least airports sorted by {@link CountryWithAirportCount#getAirportCount()}. Wrapper object.
     * @see ReportManager#reportTop10CountriesWithMostOrLeastAirports(boolean)
     * @see evans18.lunatechairportsbackend.data.repository.airport.AirportService#findCountryCodesOfCountriesWithTop10MostOrLeastAirports(boolean)
     */
    @GetMapping("/top10")
    List<CountryWithAirportCount> reportTop10AirportCountCountries(@RequestParam(value = "is_most", defaultValue = "true") boolean isMost) {
        try {
            return reportManager.reportTop10CountriesWithMostOrLeastAirports(isMost).stream()
                    .map(pair -> new CountryWithAirportCount(pair.getFirst(), pair.getSecond()))
                    .collect(Collectors.toUnmodifiableList());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal database server temporarily unavailable. :(");
        }
    }

}
