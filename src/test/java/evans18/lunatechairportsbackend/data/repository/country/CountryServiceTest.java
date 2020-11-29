package evans18.lunatechairportsbackend.data.repository.country;

import evans18.lunatechairportsbackend.data.model.Country;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

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
            "United\\ States",
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

    //#region Parameter helpers
    private static Arguments[] parameterCountryCodes() {
        return new Arguments[]{
                Arguments.of(
                        mapCountriesByCountryCode.keySet().toArray()
                )
        };
    }

    private static Arguments[] parameterIncompleteCountryCodes() {
        return new Arguments[]{
                Arguments.of(
                        mapCountriesByStartingCountryCode.keySet().toArray()
                )
        };
    }

    private static Arguments[] parameterExactCountryNames() {
        return new Arguments[]{
                Arguments.of(
                        setCountries
                )
        };
    }
    //#endregion


    @ParameterizedTest
    @MethodSource("parameterExactCountryNames")
    void wildcardSearchExactCountryNamesNoWhiteSpace(Set<String> listCountryNames) {

        for (String countryName : listCountryNames) {
            if (Pattern.compile("\\s").matcher(countryName).find())
                continue; //skips countries with white space names. currently not supported //todo test and support white space

            List<Country> foundCountries = service.wildcardSearchCountries(countryName);

            assertTrue(foundCountries.stream()
                    .anyMatch(country -> country.getName().equals(countryName)));
        }
    }

    //    @Ignore  - //todo test and support white space
    void wildcardSearchExactCountryNamesWithWhiteSpace(Set<String> listCountryNames) {

        for (String countryName : listCountryNames) {
            List<Country> foundCountries = service.wildcardSearchCountries(countryName);

            assertTrue(foundCountries.stream()
                    .anyMatch(country -> country.getName().equals(countryName)));
        }
    }

}