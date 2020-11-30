package evans18.lunatechairportsbackend.controller;

import evans18.lunatechairportsbackend.data.manager.ReportManager;
import evans18.lunatechairportsbackend.data.model.Country;
import evans18.lunatechairportsbackend.data.model.Runway;
import evans18.lunatechairportsbackend.data.model.response.CountryWithAirportCount;
import evans18.lunatechairportsbackend.data.model.response.CountryWithRunwayTypes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Exposes GET endpoints that contain reports on the data.
 * <p>
 * Future note: Consider if data is going to be frequently updated and if that is not the case,
 * then consider having a caching mechanism to store data in memory in stead of looking it up at each call.
 */
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportManager reportManager;

    /**
     * Finds the countries with most/least airports and returns the top 10 in those categories sorted in the
     * corresponding order.
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

    /**
     * Gets a collection of all Countries along with all runway types within the airports of those countries.
     *
     * @return - collection of wrapper object for each country along with runway type.
     */
    @GetMapping("/country_runway_types")
    List<CountryWithRunwayTypes> reportRunwayTypesInCountries() {
        try {
            Map<Country, Set<String>> countrySetMap = reportManager.reportRunwayTypesInAllCountryAirports();
            return countrySetMap.entrySet().stream()
                    .map(entry -> new CountryWithRunwayTypes(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toUnmodifiableList());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal database server temporarily unavailable. :(");
        }
    }

    /**
     * Gets the top 10 most frequent {@link Runway#getLe_ident()}.
     *
     * @return - a sorted by popularity list of the 10 Strings containing {@link Runway#getLe_ident()}.
     */
    @GetMapping("/top10_frequent_runaway_le_ident")
    List<String> reportTop10MostFrequentRunwayIdentifications() {
        try {
            return reportManager.reportTop10MostFrequentRunwayIdentifications();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal database server temporarily unavailable. :(");
        }
    }

}
