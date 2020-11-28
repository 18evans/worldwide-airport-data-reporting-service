package evans18.lunatechairportsbackend.data.repository;

import evans18.lunatechairportsbackend.data.model.Country;
import org.springframework.data.repository.CrudRepository;

public interface CountryRepository extends CrudRepository<Country, Integer> {
}
