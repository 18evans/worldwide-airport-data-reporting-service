package evans18.lunatechairportsbackend.controller;

import evans18.lunatechairportsbackend.data.model.Airport;
import evans18.lunatechairportsbackend.data.model.Runway;
import evans18.lunatechairportsbackend.data.repository.country.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/query")
@RequiredArgsConstructor
public class QueryController {

    private final CountryService service;

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
        return null;
    }

}
