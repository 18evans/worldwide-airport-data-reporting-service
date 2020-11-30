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
import java.util.Set;
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
    public List<Pair<String, Long>> reportTop10CountriesWithMostOrLeastAirports(boolean isMost) throws IOException {
        LinkedHashMap<String, Long> countryCodesWithCount = airportService.findCountryCodesOfCountriesWithTop10MostOrLeastAirports(isMost); //already sorted

        //look-up DB for country names
        Set<Country> countries = countryRepository.findAllByCodeIsIn(countryCodesWithCount.keySet()); //one DB call

        //map
        return countries.stream().map(country -> {
            long airportCount = countryCodesWithCount.get(country.getCode());
            return Pair.of(country.getName(), airportCount);
        })
                .collect(Collectors.toList());
    }
}
