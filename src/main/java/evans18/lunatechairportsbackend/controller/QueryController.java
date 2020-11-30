package evans18.lunatechairportsbackend.controller;

import evans18.lunatechairportsbackend.data.model.Airport;
import evans18.lunatechairportsbackend.data.model.Country;
import evans18.lunatechairportsbackend.data.model.Runway;
import evans18.lunatechairportsbackend.data.repository.airport.AirportService;
import evans18.lunatechairportsbackend.data.repository.country.CountryService;
import evans18.lunatechairportsbackend.data.repository.runway.RunwayRepository;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/query")
@RequiredArgsConstructor
public class QueryController {

    private final CountryService countryService;
    private final AirportService airportService;
    private final RunwayRepository runwayRepository;

    /**
     * Receives a query parameter consisting of either a country name or code
     * and if a country exists which completes such a prefix name/code returned
     * are that country's airports and all runways per airport.
     *
     * @param countryNameOrCode - partial country name or code.
     * @return - Collection of Airports and Runways with each Runway bound to their Airport.
     */
    @GetMapping
    Map<Airport, List<Runway>> getAirportsRunwaysFromCountryNameCode(@RequestParam("country") String countryNameOrCode) {
//        service.searchCountryByPartialCountryNameOrCode()
        Country country = countryService.findTopCountryByPartialCountryNameOrCode(countryNameOrCode).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No such country exists.") //User does need to know his input is invalid. Note: there's conflict on the correct code for this use case so followed is the popular answer
        );

        List<Airport> airports;
        try {
            airports = airportService.scrollSearchFindAllAirportsByCountryCode(country.getCode());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal database server temporarily unavailable. :(");
        }

        return airports.stream()
                .collect(Collectors.toMap(airport -> airport,
                        airport -> runwayRepository.findAllByAirportRef(airport.getId()) //get airport's runways
                ));
    }

}
