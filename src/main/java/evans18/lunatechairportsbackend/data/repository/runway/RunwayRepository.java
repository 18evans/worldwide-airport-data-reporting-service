package evans18.lunatechairportsbackend.data.repository.runway;

import evans18.lunatechairportsbackend.data.model.Runway;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RunwayRepository extends CrudRepository<Runway, Integer> {
    List<Runway> findAllByAirportRef(int airportId);
}
