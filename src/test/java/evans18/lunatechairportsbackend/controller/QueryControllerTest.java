package evans18.lunatechairportsbackend.controller;

import evans18.lunatechairportsbackend.data.model.Airport;
import evans18.lunatechairportsbackend.data.model.Country;
import evans18.lunatechairportsbackend.data.model.Runway;
import evans18.lunatechairportsbackend.data.repository.airport.AirportService;
import evans18.lunatechairportsbackend.data.repository.country.CountryService;
import evans18.lunatechairportsbackend.data.repository.runway.RunwayRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

//todo a lot of more tests can be done here, including integration testing between dependencies
@SpringBootTest
class QueryControllerTest {

    @InjectMocks
    private QueryController controller; //SUT

    @Mock
    private RunwayRepository runwayRepository;
    @Mock
    private AirportService airportService;
    @Mock
    private CountryService countryService;


    @Test
    void getRunwaysOfAirportsInCountryGivesExpectedValueOnExactCountryNameInputVariations() throws IOException {
        //given - valid country
        Country country = new Country();
        country.setName(UUID.randomUUID().toString());
        String countryCode = UUID.randomUUID().toString();
        country.setCode(countryCode);

        //- airports
        Airport a1 = new Airport(),
                a2 = new Airport(),
                a3 = new Airport();
        a1.setId(111);
        a2.setId(222);
        a3.setId(333);
        List<Airport> expectedAirports = List.of(a1, a2, a3);

        //runways - 1 for each airport (using same index as airport's index)
        Runway r1 = new Runway(),
                r2 = new Runway(),
                r3 = new Runway();
        r2.setId(555);
        r1.setId(444);
        r3.setId(666);
        List<List<Runway>> expectedRunways = List.of(List.of(r1), List.of(r2), List.of(r3));

        //when
        when(countryService.findTopCountryByPartialCountryNameOrCode(country.getName())) //by name
                .thenReturn(Optional.of(country));
        when(airportService.scrollSearchFindAllAirportsByCountryCode(country.getCode()))
                .thenReturn(expectedAirports);
        for (int i = 0, airportsSize = expectedAirports.size(); i < airportsSize; i++) {
            when(runwayRepository.findAllByAirportRef(expectedAirports.get(i).getId()))
                    .thenReturn(expectedRunways.get(i));
        }

        //then
        Map<Airport, List<Runway>> countrysRunwaysByAirports = controller.getAirportsRunwaysFromCountryNameCode(country.getName()); //endpoint results

        //airports
        assertEquals(expectedAirports.size(), countrysRunwaysByAirports.keySet().size(), "Found airport count different from expected.");
        assertTrue(countrysRunwaysByAirports.keySet().containsAll(expectedAirports), "Found airports do not contain all expected airports.");

        //runways
        assertEquals(expectedRunways.size(), countrysRunwaysByAirports.values().size(), "Found runways count is different from expected.");
        assertTrue(countrysRunwaysByAirports.values().containsAll(expectedRunways), "Found runways do not contain all expected runways.");

        //runways is at correct airport //warning: use small test case as time complexity explodes here
        countrysRunwaysByAirports.forEach((airport, runways) ->
                assertEquals(expectedAirports.indexOf(airport), expectedRunways.indexOf(runways))
        );
    }
}