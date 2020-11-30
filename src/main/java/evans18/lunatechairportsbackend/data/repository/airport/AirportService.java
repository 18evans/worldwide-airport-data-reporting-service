package evans18.lunatechairportsbackend.data.repository.airport;

import com.google.gson.Gson;
import evans18.lunatechairportsbackend.data.model.Airport;
import evans18.lunatechairportsbackend.data.repository.ElasticSearchConstants;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;


@Service
@RequiredArgsConstructor
public class AirportService {

    public static final TimeValue SCROLL_DEFAULT_TIMEOUT_DURATION = TimeValue.timeValueSeconds(15L);
    public static final int SCROLL_DEFAULT_HITS_COUNT_PER_SCROLL = 1000; //note: increasing value for countries with many airports reduces scroll search request count drastically
    private static final Gson gson = new Gson();

    private final RestHighLevelClient client;
    private final AirportRepository repository;

    /**
     * Finds airports by a provided country code.
     *
     * @throws IOException - on no connection to ES.
     */
    public List<Airport> scrollSearchFindAllAirportsByCountryCode(String countryCode) throws IOException {
        return scrollSearch(buildScrollSearchRequest(
                matchQuery(ElasticSearchConstants.ES_DOC_AIRPORT_FIELD_COUNTRY_CODE, countryCode)) //search only by this field
        );
    }

    /**
     * Finds all Airplanes but using scroll search with default configurations.
     *
     * @throws IOException - on no connection to ES.
     * @see AirportRepository#findAll()
     */
    //todo: should be a simple way through the Elastic client configurator to increase the max window size.
    private List<Airport> scrollSearchFindAll() throws IOException {
        return scrollSearch(buildScrollSearchRequest(null));
    }

    /**
     * Builds a request for scroll search using default configurations for each scroll's size and scroll timeout.
     */
    private SearchRequest buildScrollSearchRequest(@Nullable MatchQueryBuilder qb) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(qb) //nullable
                .size(SCROLL_DEFAULT_HITS_COUNT_PER_SCROLL); //size per scroll. Note: affects how many scroll search requests in total will be done.

        //build request
        return new SearchRequest(ElasticSearchConstants.DOCUMENT_INDEX_AIRPORTS)
                .source(searchSourceBuilder)
                .scroll(SCROLL_DEFAULT_TIMEOUT_DURATION); //time-out scrolling after 15 secs
    }

    /**
     * Methods allows searching beyound the ElasticSearch setting limit for index window size.
     * Note: Well... it's probly set low for a reason but whatevz for demo.
     *
     * @throws IOException - on no connection to ES.
     */
    private List<Airport> scrollSearch(SearchRequest searchRequest) throws IOException {
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

    /**
     * /**
     * Goes through all airports in the repository to find the countries with most/least many airports and returns the top 10.
     *
     * @param isMost - {@code true} if looking for countries with most airports. Else countries with least.
     * @return - sorted order of the codes of countries that have the most or least airports (denoted by param)
     * @throws IOException - on no connection to ES.
     */
    //todo this should be done ideally by ES
    public LinkedHashMap<String, Long> findCountryCodesOfCountriesWithTop10MostOrLeastAirports(boolean isMost) throws IOException {
        Iterable<Airport> iterable = scrollSearchFindAll();

        //time: O(A)
        //space: O(C)
        Map<String, Long> mapCountryCodeByAirportCount = StreamSupport.stream(iterable.spliterator(), false) //stream on all airports
                .collect(Collectors.groupingBy(Airport::getIso_country, Collectors.counting()));


        LinkedHashMap<String, Long> sortedCountryCodesByCount = new LinkedHashMap<>();

        //for more readability but duplicate if checks handle comparison inside loop
        Comparator<Map.Entry<String, Long>> comparator = Comparator.comparingLong(Map.Entry::getValue); //asc order
        if (!isMost) comparator = comparator.reversed(); //desc order

        /* Since we want only 10 elements they can be received following 10 iterative calls looking for the next biggest element
            and removing it from the pool of elements. Note, for removing from the pool needed is to recreate the map entry set.
            Due to our constant 10, for large quantities of countries, time complexity evaluates to linear O(C).
            However for quantities under or equal to the constant then time it's exponential O(C^2)
            Space: O(C)

            If ever the constant 10 becomes larger or ever changing visit commented block under:
         */
        for (int i = 0; i < 10; i++) {
            Stream<Map.Entry<String, Long>> entryStream = mapCountryCodeByAirportCount.entrySet().stream();

            //get next most/least frequent
            Map.Entry<String, Long> element = entryStream.max(comparator) //max according TO comparator
                    .orElse(null);

            if (element == null) //no more elements
                break;

            String countryCode = element.getKey();
            long airportCount = element.getValue();

            mapCountryCodeByAirportCount.remove(countryCode); //remove from pool
            sortedCountryCodesByCount.put(countryCode, airportCount);
        }

        return sortedCountryCodesByCount;

        //sort Map by count using TreeMap through min-heap structure implementation
        //time: O(C log C)
        //space: O(C)
//        TreeMap<Long, Map.Entry<String, Long>> sortedByCount = new TreeMap<>(comparator); //highest count first
//        mapCountryCodeByAirportCount.entrySet().forEach(stringLongEntry -> sortedByCount.put(stringLongEntry.getValue(), stringLongEntry));

        //note: also valid to explore directly sorting map's entryset through stream().sorted() api
    }

}