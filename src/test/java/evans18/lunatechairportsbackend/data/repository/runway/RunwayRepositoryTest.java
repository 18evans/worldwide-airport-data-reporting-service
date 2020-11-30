package evans18.lunatechairportsbackend.data.repository.runway;

import evans18.lunatechairportsbackend.data.model.Runway;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RunwayRepositoryTest {

    @Autowired
    RunwayRepository repository;

    private static Stream<Arguments> validAirportIds() {
        return Stream.of(
                Arguments.of(45437),
                Arguments.of(6532),
                Arguments.of(3632),
                Arguments.of(3622),
                Arguments.of(20220)
        );
    }

    @ParameterizedTest
    @MethodSource("validAirportIds")
    void findRunwaysByValidAirportIdNotEmpty(int airportId) {
        //when
        List<Runway> foundRunways = repository.findAllByAirportRef(airportId);

        //then
        assertFalse(foundRunways.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("validAirportIds")
    void findRunwaysByValidAirportIdConfirmHaveSameAirportId(int airportId) {
        //when
        List<Runway> foundRunways = repository.findAllByAirportRef(airportId);

        //then - all found runways are part of expected airportId
        assertTrue(
                foundRunways.stream().allMatch(runway ->
                        runway.getAirportRef() == airportId));
    }

}