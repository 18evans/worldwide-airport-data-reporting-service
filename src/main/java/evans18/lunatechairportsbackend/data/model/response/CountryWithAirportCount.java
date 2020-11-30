package evans18.lunatechairportsbackend.data.model.response;

import evans18.lunatechairportsbackend.data.model.Country;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Wrapper for a country along with its count of airports.
 */
@Data
@AllArgsConstructor
public class CountryWithAirportCount {
    private Country country;
    private int airportCount;
}
