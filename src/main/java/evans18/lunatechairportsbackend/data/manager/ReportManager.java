package evans18.lunatechairportsbackend.data.manager;

import evans18.lunatechairportsbackend.data.model.Country;
import evans18.lunatechairportsbackend.data.repository.airport.AirportService;
import evans18.lunatechairportsbackend.data.repository.country.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportManager {

    private final CountryRepository countryRepository;
    private final AirportService airportService;

    /**
     * Finds the countries that have the most/least airports and returns the top 10 of that selection.
     * <p>
     * Handles the sorting and filtering within the {@link AirportService#findCountryCodesOfCountriesWithTop10MostOrLeastAirports(boolean)}
     * which receives an ordered map with all of the coutnry codes. To then find the country name queried is the database which afterwards
     * maps the country name to that country's count result and returns the result.
     *
     * @param isMost - whether looking for most/least airports.
     * @return - sorted order of the country airports count bound to the country name.
     * @throws IOException - on no connection to ES.
     */
    public List<Pair<Country, Integer>> reportTop10CountriesWithMostOrLeastAirports(boolean isMost) throws IOException {
        LinkedHashMap<String, Integer> sortedCountriesAirportCountByCode = airportService.findCountryCodesOfCountriesWithTop10MostOrLeastAirports(isMost); //already sorted

        //find in DB the country object identified by provided country codes
        Map<String, Country> countries = countryRepository.findAllByCodeIsIn(sortedCountriesAirportCountByCode.keySet()) //one DB call note: doesn't maintain order of input
                .stream()
                .collect(Collectors.toMap(Country::getCode, c -> c)); //transform to map for constant look-up of code

        //now both collections are keyed by Country Code

        //maintaining sorted order of top country query get name from country query
        return sortedCountriesAirportCountByCode.entrySet().stream().map(entry -> {
            String code = entry.getKey();
            Country country = countries.get(code);

            int airportCount = entry.getValue();
            return Pair.of(country, airportCount);
        }).collect(Collectors.toUnmodifiableList());
    }

}