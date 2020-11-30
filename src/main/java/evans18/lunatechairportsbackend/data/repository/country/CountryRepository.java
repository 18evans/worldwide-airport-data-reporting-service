package evans18.lunatechairportsbackend.data.repository.country;

import evans18.lunatechairportsbackend.data.model.Country;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface CountryRepository extends CrudRepository<Country, Integer> {
    Set<Country> findAllByCodeIsIn(Set<String> countryCodes);

}
