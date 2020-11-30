package evans18.lunatechairportsbackend.data.service;

import evans18.lunatechairportsbackend.data.model.Country;
import evans18.lunatechairportsbackend.data.repository.country.CountryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CountryServiceTest {

    //#region fields

    @Autowired
    private CountryService service; //SUT

    //#endregion

    //#region Parameters Helpers

    /**
     * Partial and impartial ways to write country names for: Netherlands, United States and all other countries which may include that name.
     *
     * @return - Gives the following cases: exact match (one word or more via delimited whitespace), partial prefix (+ delimited whitespace),
     * partial suffix (+ delimited white space as its prefix and as its suffix).
     */
    private static Stream<Arguments> pairsPartialCountryNameWithActualCountryName() {
        String netherlands = "Netherlands"; //1 word
        String unitedStates = "United States"; //multi-word delimited by whitespace
        return Stream.of(
                //Netherlands
                Arguments.of(netherlands, netherlands), //exact
                Arguments.of("Nether", netherlands), //partial prefix
                Arguments.of("   Netherlands", netherlands), //exact with prefix whitespace
                Arguments.of("   Netherlands        ", netherlands), //exact with pre+suf/fix ws
                Arguments.of("Ne the rl ands", netherlands), //exact but roughly split by ws
                //United States
                Arguments.of(unitedStates, unitedStates), //exact but multi-word (ws delimiter)
                Arguments.of(unitedStates, unitedStates + " Minor Outlying Islands"), //multi-word exacts in more-words country name (this is actual country name btw)
                Arguments.of("United", unitedStates), //exact first word
                Arguments.of("Uni", unitedStates), //partial prefix first word
                Arguments.of("ted", unitedStates), //partial suffix first word
                Arguments.of("Sta", unitedStates), //partial prefix second word
                Arguments.of("tes", unitedStates), //partial suffix second word
                Arguments.of("United ", unitedStates), //exact first word + ws
                Arguments.of("States    ", unitedStates), //exact second word + redundant ws
                Arguments.of("    States", unitedStates), //exact second word + prefix ws

                //case insensitive
                Arguments.of("nEthErlaNds", netherlands),
                Arguments.of("uniTED StaTEs", unitedStates),

                //Maaike example :)
                Arguments.of("zimb", "Zimbabwe")
        );
    }

    private static Stream<Arguments> pairsPartialCountryCodeWithActualCountryNameAndCode() {
        String germany = "Germany";
        String germanyCode = "DE";

        return Stream.of(
                Arguments.of(germanyCode, germany, germanyCode), //exact
                Arguments.of("dE", germany, germanyCode), //case-insensitive
                Arguments.of("D", germany, germanyCode), //partial prefix
                Arguments.of("d", germany, germanyCode), //partial prefix case-insensitive
                Arguments.of("e", germany, germanyCode), //suffix prefix
                Arguments.of("E", germany, germanyCode), //suffix prefix case-insensitive

                //countries with country code having the prefix query
                Arguments.of("D", germany, germanyCode),
                Arguments.of("D", "Djibouti", "DJ"),
                Arguments.of("D", "Denmark", "DK"),
                Arguments.of("D", "Dominica", "DM"),
                Arguments.of("D", "Dominican Republic", "DO"),
                Arguments.of("D", "Algeria", "DZ"),

                //countries with country code having the suffix query
                Arguments.of("O", "Dominican Republic", "DO"),
                Arguments.of("O", "Romania", "RO")
        );
    }

    private static Stream<Arguments> partialCountryNameWithMatchingFullCountryNames() {
        String netherlands = "Netherlands"; //1 word
        String unitedStates = "United States"; //multi-word delimited by whitespace

        return Stream.of(
                //exact name
                Arguments.of(netherlands, List.of(netherlands, "Caribbean " + netherlands)),
                Arguments.of(unitedStates, List.of(unitedStates, unitedStates + " Minor Outlying Islands")),

                //partial name
                Arguments.of(netherlands.substring(netherlands.length() / 2), List.of(netherlands, "Caribbean " + netherlands)),
                Arguments.of(unitedStates.substring(unitedStates.length() / 2), List.of(unitedStates, unitedStates + " Minor Outlying Islands")),

                //case insensitive name
                Arguments.of(netherlands.toLowerCase(), List.of(netherlands, "Caribbean " + netherlands)),
                Arguments.of(unitedStates.toUpperCase(), List.of(unitedStates, unitedStates + " Minor Outlying Islands")),

                //case insensitive + partial name
                Arguments.of(netherlands.toLowerCase().substring(netherlands.length() / 2), List.of(netherlands, "Caribbean " + netherlands)),
                Arguments.of(unitedStates.toUpperCase().substring(unitedStates.length() / 2), List.of(unitedStates, unitedStates + " Minor Outlying Islands"))
        );
    }

    /**
     * Partial and impartial ways that are close enough to expectedly always find the specific country that it is paired with.
     * <p>
     * Note: if made even more unspecific might return different top result.
     */
    private static Stream<Arguments> pairsPartialCountryNameWithExpectedTopCountryNameResult() {
        String netherlands = "Netherlands"; //1 word
        String unitedStates = "United States"; //multi-word delimited by whitespace
        return Stream.of(
                //Netherlands
                Arguments.of(netherlands, netherlands), //exact
                Arguments.of("Nether", netherlands), //partial prefix
                Arguments.of("   Netherlands", netherlands), //exact with prefix whitespace
                Arguments.of("   Netherlands        ", netherlands), //exact with pre+suf/fix ws
                Arguments.of("Ne the rl ands", netherlands), //exact but roughly split by ws
                //United States
                Arguments.of(unitedStates, unitedStates), //exact but multi-word (ws delimiter)
                Arguments.of(unitedStates, unitedStates), //multi-word exacts in more-words country name (this is actual country name btw)
                Arguments.of("United St", unitedStates), //exact first word
                Arguments.of("Uni St", unitedStates), //partial multiple prefix wordw

                //Maaike example :)
                Arguments.of("zimb", "Zimbabwe")
        );
    }

    //#endregion

    //#region Tests

    //#region Partial String Search

    //#region Multiple Countries

    @Test
    void findNoCountriesOnInvalidNameOrCodeVariations() {
        String invalidCountryNameCodeInputQuery = UUID.randomUUID().toString();
        //when
        List<Country> foundCountries = service.findCountriesByPartialCountryNameOrCode(invalidCountryNameCodeInputQuery);

        //then
        assertTrue(foundCountries.isEmpty());
    }

    /**
     * Asserts that a search for a country given a partial string of a country name, varying from exact, partial, prefix, suffix, (no)/whitespace
     * finds a country with the expected partial string.
     *
     * @param queryPartialCountryName - partial string variation of an input for a Country Name.
     */
    @ParameterizedTest
    @MethodSource("pairsPartialCountryNameWithActualCountryName")
    void findAllCountriesByPartialNameInputVariations(String queryPartialCountryName, String expectedCountryName) {
        //when
        List<Country> foundCountries = service.findCountriesByPartialCountryNameOrCode(queryPartialCountryName);

        String msgFailPartialNameMatch = "No expected country using given partial query in collection.\n" +
                "Partial name query : \t\"" + queryPartialCountryName + "\"\n" +
                "Expected country : \t\"" + expectedCountryName + "\"\n" +
                "Actual collection: \t" + Arrays.deepToString(foundCountries.stream().map(Country::getName).toArray());
        //then
        assertTrue(foundCountries.stream()
                        .anyMatch(country -> country.getName().equals(expectedCountryName)),
                msgFailPartialNameMatch);
    }

    @ParameterizedTest
    @MethodSource("pairsPartialCountryCodeWithActualCountryNameAndCode")
    void findAllCountriesByPartialCodeInputVariations(String queryPartialCountryCode, String expectedCountryName, String expectedCountryCode) {
        //when
        List<Country> foundCountries = service.findCountriesByPartialCountryNameOrCode(queryPartialCountryCode);

        String msgFailPartialCodeMatch = "No expected country using given partial query in collection.\n" +
                "Partial query : \t\"" + queryPartialCountryCode + "\"\n" +
                "Expected code : \t\"" + expectedCountryCode + "\"\n" +
                "Expected country : \t\"" + expectedCountryName + "\"\n" +
                "Actual collection: \t" + Arrays.deepToString(foundCountries.stream().map(Country::getName).toArray());

        //then
        assertTrue(foundCountries.stream()
                        .anyMatch(country -> country.getName().equals(expectedCountryName) && country.getCode().equals(expectedCountryCode)),
                msgFailPartialCodeMatch);
    }

    @ParameterizedTest
    @MethodSource("partialCountryNameWithMatchingFullCountryNames")
    void findCountriesThatMatchThePartialNameInputVariations(String queryPartialCountryName, List<String> expectedCountryNames) {
        //when
        Set<String> foundCountriesNames = service.findCountriesByPartialCountryNameOrCode(queryPartialCountryName)
                .stream().map(Country::getName).collect(Collectors.toUnmodifiableSet());

        String msgFailPartialNameMatch = "Some of the expected countries were not in the found collection returned by search of the given partial query.\n" +
                "Partial name query : \t\"" + queryPartialCountryName + "\"\n" +
                "Expected matching countries : \t\"" + Arrays.deepToString(expectedCountryNames.toArray()) + "\"\n" +
                "Actual collection: \t" + Arrays.deepToString(service.findCountriesByPartialCountryNameOrCode(queryPartialCountryName).stream().map(Country::getName).toArray());
        //then
        assertTrue(
                foundCountriesNames.containsAll(expectedCountryNames),
                msgFailPartialNameMatch
        );
    }

    //#endregion

    //#region Specific Country

    @ParameterizedTest
    @MethodSource("partialCountryNameWithMatchingFullCountryNames")
    void findNoCountryOnInvalidNameOrCodeVariations() {
        String invalidCountryNameCodeInputQuery = UUID.randomUUID().toString();
        //when
        Optional<Country> foundCountries = service.findTopCountryByPartialCountryNameOrCode(invalidCountryNameCodeInputQuery);

        //then
        assertTrue(foundCountries.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("pairsPartialCountryNameWithExpectedTopCountryNameResult")
    void findSpecificCountryByCloseEnoughPartialNameInputVariations(String queryPartialCountryName, String expectedCountryName) {
        //when
        Optional<Country> foundCountry = service.findTopCountryByPartialCountryNameOrCode(queryPartialCountryName);

        //then
        assertFalse(foundCountry.isEmpty()); //expecting existing country
        String countryName = foundCountry.get().getName();

        String msgFailPartialCodeMatch = "Received unexpected country using given partial query in collection.\n" +
                "Partial query : \t\"" + queryPartialCountryName + "\"\n" +
                "Expected country : \t\"" + expectedCountryName + "\"\n" +
                "Actual country: \t" + countryName;

        assertEquals(expectedCountryName, countryName, msgFailPartialCodeMatch);
    }

    //#endregion

    //#endregion

    //#endregion
}