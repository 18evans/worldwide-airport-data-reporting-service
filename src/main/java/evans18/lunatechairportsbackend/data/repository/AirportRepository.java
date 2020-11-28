package evans18.lunatechairportsbackend.data.repository;

import evans18.lunatechairportsbackend.data.model.Airport;
import org.springframework.data.repository.CrudRepository;

public interface AirportRepository extends CrudRepository<Airport, Integer> {
}
