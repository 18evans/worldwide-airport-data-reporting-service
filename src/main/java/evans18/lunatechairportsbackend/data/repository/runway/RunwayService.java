package evans18.lunatechairportsbackend.data.repository.runway;

import evans18.lunatechairportsbackend.data.manager.ElasticSearchClient;
import evans18.lunatechairportsbackend.data.model.Runway;
import evans18.lunatechairportsbackend.data.repository.ElasticSearchConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


@Service
@RequiredArgsConstructor
public class RunwayService {

    private final ElasticSearchClient client;

    /**
     * Finds runways using a scroll search.
     *
     * @throws IOException - on no connection to ES.
     */
    public List<Runway> scrollSearchFindAllRunways() throws IOException {
        return client.scrollSearchFindAll(ElasticSearchConstants.DOCUMENT_INDEX_RUNWAYS, Runway.class);
    }

}