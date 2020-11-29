package evans18.lunatechairportsbackend.data.service;

import evans18.lunatechairportsbackend.data.model.Airport;
import evans18.lunatechairportsbackend.data.repository.airport.AirportService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AirportServiceTest {

    //#region fields

    @Autowired
    private AirportService service; //SUT

    //#endregion

    //#region Parameters Helpers

    /**
     * @return - valid exact country codes of verified countries paired with several airport identifier codes within those countries.
     */
    private static Stream<Arguments> setPairsCountryNamesWithSomeAirportCodes() {
        return Stream.of(
                Arguments.of("DE", Set.of("BER", "EDAC", "DE-0180", "DE-0002", "DE-0001", "EDBF")),
                Arguments.of("DJ", Set.of("HDOB", "HDHE", "HDAS", "HDAG")),
                Arguments.of("DZ", Set.of("DA10", "DAAB", "DAAE", "DAAW")),
                Arguments.of("UA", Set.of("RU-2098", "UA-0002", "UA-0008", "UA-0073")),
                Arguments.of("US", Set.of("UGB", "US-0155", "KJFK", "KJDN", "KLAX"))
        );
    }

    //#endregion

    //#region Tests

    @ParameterizedTest
    @MethodSource("setPairsCountryNamesWithSomeAirportCodes")
    void findAirportsByCountryCodeWithCountriesThatHaveValidAirportsNotEmpty(String countryCode) throws IOException {
        //when
        List<Airport> foundAirports = service.scrollSearchFindAllAirportsByCountryCode(countryCode);

        //then
        assertFalse(foundAirports.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("setPairsCountryNamesWithSomeAirportCodes")
    void findAirportsByCountryCode(String countryCode, Set<String> expectedAirportCodes) throws IOException {
        //when
        List<Airport> foundAirports = service.scrollSearchFindAllAirportsByCountryCode(countryCode);

        //then - expected airport codes are all in the codes of found airports
        assertTrue(
                expectedAirportCodes.stream()
                        .allMatch(expectedCode ->
                                foundAirports.stream()
                                        .anyMatch(airport -> airport.getIdent().equals(expectedCode))
                        ));

    }

    @ParameterizedTest
    @MethodSource("setPairsCountryNamesWithSomeAirportCodes")
    void findAirportsByCountryCodeHasNoDuplicates(String countryCode) throws IOException {
        //when
        List<Airport> foundAirports = service.scrollSearchFindAllAirportsByCountryCode(countryCode);

        //then - get all distinct airport ids
        Set<Integer> foundDistinctAirportIds = foundAirports.stream().map(Airport::getId).collect(Collectors.toUnmodifiableSet());
        assertEquals(foundDistinctAirportIds.size(), foundAirports.size());
    }

    //#endregion

}