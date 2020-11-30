package evans18.lunatechairportsbackend.data.model.response;

import evans18.lunatechairportsbackend.data.model.Country;
import evans18.lunatechairportsbackend.data.model.Runway;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

/**
 * Wrapper object for a country and all of its airports' runway types.
 *
 * @see Runway#getSurface()
 */
@Data
@AllArgsConstructor
public
class CountryWithRunwayTypes {
    Country country;
    /**
     * @see Runway#getSurface()
     */
    Set<String> runwayTypes;
}
