package evans18.lunatechairportsbackend.data.repository.runway;

import evans18.lunatechairportsbackend.data.manager.ElasticSearchClientManager;
import evans18.lunatechairportsbackend.data.model.Runway;
import evans18.lunatechairportsbackend.data.repository.ElasticSearchConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RunwayService {

    private final ElasticSearchClientManager clientManager;

    /**
     * Finds runways using a scroll search.
     *
     * @throws IOException - on no connection to ES.
     */
    public List<Runway> scrollSearchFindAllRunways() throws IOException {
        return clientManager.scrollSearchFindAll(ElasticSearchConstants.DOCUMENT_INDEX_RUNWAYS, Runway.class);
    }

    //todo: can't find an elasticsearch query that works on java client for now use this monstosity
    public List<String> getTopMostFrequentRunwayIdentifications(int limit) throws IOException {
        Map<String, Integer> leIdentFrequencies = clientManager.scrollSearchFindAll(ElasticSearchConstants.DOCUMENT_INDEX_RUNWAYS, Runway.class).stream()
                .collect(Collectors.groupingBy(Runway::getLe_ident, Collectors.summingInt(x -> 1)));

        return leIdentFrequencies.entrySet().stream()
                .sorted((o1, o2) -> -Integer.compare(o1.getValue(), o2.getValue())) //desc order
                .map(Map.Entry::getKey)
                .limit(limit)
                .collect(Collectors.toList());
    }
}