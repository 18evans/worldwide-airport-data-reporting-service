package evans18.lunatechairportsbackend.data.repository.airport;

import com.google.gson.Gson;
import evans18.lunatechairportsbackend.Constants;
import evans18.lunatechairportsbackend.data.model.Airport;
import lombok.AllArgsConstructor;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;


@Service
@AllArgsConstructor
public class AirportService {

    public static final TimeValue SCROLL_DEFAULT_TIMEOUT_DURATION = TimeValue.timeValueSeconds(15L);
    public static final int SCROLL_DEFAULT_HITS_COUNT_PER_SCROLL = 1000; //note: increasing value for countries with many airports reduces scroll search request count drastically
    private final RestHighLevelClient client;
    private final Gson gson = new Gson();

    public List<Airport> scrollSearchFindAllAirportsByCountryCode(String countryCode) throws IOException {
        //build initial search - initialize scroll session
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder() //note:
                .query(matchQuery(Airport.AIRPORT_FIELD_COUNTRY_CODE, countryCode)) //note name must be equal to field
                .size(SCROLL_DEFAULT_HITS_COUNT_PER_SCROLL); //size per scroll. Note: affects how many scroll search requests in total will be done.

        SearchRequest searchRequest = new SearchRequest(Constants.DOCUMENT_INDEX_AIRPORTS)
                .source(searchSourceBuilder)
                .scroll(SCROLL_DEFAULT_TIMEOUT_DURATION); //time-out scrolling after 15 secs

        //execute initial search
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);//.toBuilder().setRequestConfig(RequestConfig));
        String scrollId = searchResponse.getScrollId();
        SearchHit[] hits = searchResponse.getHits().getHits();

        List<Airport> foundAirports = new ArrayList<>();

        while (hits != null && hits.length > 0) {
            //process hit items and store into list
            parseSearchHitResultsAndPlaceIntoList(foundAirports, hits, Airport.class);

            //build next scroll request
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId)
                    .scroll(SCROLL_DEFAULT_TIMEOUT_DURATION);

            //process next scroll
            searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);

            //save hits AND scrollId for next one
            scrollId = searchResponse.getScrollId();
            hits = searchResponse.getHits().getHits();
        }


        //clear scroll search context
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);

        return foundAirports;
    }

    private <T> void parseSearchHitResultsAndPlaceIntoList(List<T> list, SearchHit[] searchHits, Class<T> clazz) {
        list.addAll(Arrays.stream(searchHits)
                .map(h ->
                        gson.fromJson(h.getSourceAsString(), clazz)
                )
                .collect(Collectors.toList())
        );
    }
}