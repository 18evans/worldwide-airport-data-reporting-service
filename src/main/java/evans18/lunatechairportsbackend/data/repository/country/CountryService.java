package evans18.lunatechairportsbackend.data.repository.country;

import evans18.lunatechairportsbackend.data.model.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository repository;

    public List<Country> wildcardSearch(String query) {
        List<Country> searchResults = repository.findCountriesByNameStartingWithOrCodeStartingWith(query, query);
        return searchResults;
    }
}