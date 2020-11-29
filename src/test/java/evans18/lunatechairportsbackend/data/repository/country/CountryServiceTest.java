package evans18.lunatechairportsbackend.data.repository.country;

import evans18.lunatechairportsbackend.data.model.Country;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CountryServiceTest {

    //#region fields

    /**
     * Keys are part prefix of country code.
     * Irregular upper/lower character casing.
     */
    private static final Map<String, Map<String, String>> mapCountriesByStartingCountryCode = Map.of(
            "D", Map.of(
                    "DE", "Germany",
                    "DJ", "Djibouti",
                    "DK", "Denmark",
                    "DM", "Dominica",
                    "DO", "Dominican Republic",
                    "DZ", "Algeria"
            ),
            "u", Map.of(
                    "UA", "Ukraine",
                    "UG", "Uganda",
                    "UM", "United States Minor Outlying Islands",
                    "US", "United States",
                    "UY", "Uruguay",
                    "UZ", "Uzbekistan"
            ),
            "r", Map.of(
                    "RE", "Réunion",
                    "RO", "Romania",
                    "RS", "Serbia",
                    "RU", "Russia",
                    "RW", "Rwanda"
            )
    );
    private static final Map<String, String> mapCountriesByCountryCode = Map.of(
            "DE", "Germany",
            "DJ", "Djibouti",
            "DZ", "Algeria",
            "UA", "Ukraine",
            "US", "United States",
            "UY", "Uruguay",
            "UZ", "Uzbekistan",
            "RU", "Russia",
            "RW", "Rwanda"
    );
    private static final Set<String> setCountries = Set.of(
            "Germany",
            "Djibouti",
            "Algeria",
            "Ukraine",
            "United States",
            "Uruguay",
            "Uzbekistan",
            "Russia",
            "Rwanda"
    );
    /**
     * Keys are part prefix of country name.
     * Irregular upper/lower character casing.
     */
    private static final Map<String, String> mapCountriesByStartingCountryName = Map.of(
            "Ger", "Germany",
            "dj", "Djibouti",
            "AlgErI", "Algeria",
            "ukRAI", "Ukraine",
            "UniTE", "United States",
            "uru", "Uruguay",
            "uZbE", "Uzbekistan",
            "russ", "Russia",
            "RWAND", "Rwanda"
    );

    @Autowired
    private CountryService service; //SUT

    //#endregion

    //#region Helpers

    private String generateFailMessageOnNoMatchPartialCountryStringInCollection(String query, String expectedCountry, Object[] collection) {
        return "No expected country using given partial query in collection.\n" +
                "Partial query : \t\"" + query + "\"\n" +
                "Expected country : \t\"" + expectedCountry + "\"\n" +
                "Actual collection: \t" + Arrays.deepToString(collection);
    }

    //#region Parameters

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
                Arguments.of("uniTED StaTEs", unitedStates)
        );
    }

    //#endregion

    //#region Tests

    /**
     * Asserts that a search for a country given a partial string of a country name, varying from exact, partial, prefix, suffix, (no)/whitespace
     * finds a country with the expected partial string.
     *
     * @param queryPartialCountryName - partial string variation of an input for a Country Name.
     */
    @ParameterizedTest
    @MethodSource("pairsPartialCountryNameWithActualCountryName")
    void searchPartialCountryNameOnNameInputVariations(String queryPartialCountryName, String expectedCountryName) {
        //when
        List<Country> foundCountries = service.searchCountryByPartialCountryName(queryPartialCountryName);

        //then
        assertTrue(foundCountries.stream()
                        .anyMatch(country -> country.getName().equals(expectedCountryName)),
                generateFailMessageOnNoMatchPartialCountryStringInCollection(queryPartialCountryName, expectedCountryName, foundCountries.stream().map(Country::getName).toArray()));
    }

    //#endregion

}