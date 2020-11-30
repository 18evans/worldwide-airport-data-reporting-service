package evans18.lunatechairportsbackend.data.manager;

import evans18.lunatechairportsbackend.util.SerializationUtil;
import lombok.Getter;
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
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ElasticSearchClientManager {

    public static final TimeValue SCROLL_DEFAULT_TIMEOUT_DURATION = TimeValue.timeValueSeconds(15L);
    public static final int SCROLL_DEFAULT_HITS_COUNT_PER_SCROLL = 10000; //> 10k will crash //note: increasing value for countries with many airports reduces scroll search request count drastically

    @Getter
    private final RestHighLevelClient client;

    /**
     * Builds a request for scroll search using default configurations for each scroll's size and scroll timeout.
     *
     * @param documentIndex - the index of the document in which to look in (I.e. a model class's index @Document alias)
     * @param qb            - nullable query builder. {@link Nullable} seems to be introducing interesting build warning "warning: unknown enum constant When.MAYBE". See @see <a href="https://stackoverflow.com/questions/53326271/spring-nullable-annotation-generates-unknown-enum-constant-warning">here</a>
     */
    public static SearchRequest buildScrollSearchRequest(String documentIndex, @Nullable MatchQueryBuilder qb) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(qb) //nullable
                .size(SCROLL_DEFAULT_HITS_COUNT_PER_SCROLL); //size per scroll. Note: affects how many scroll search requests in total will be done.

        //build request
        return new SearchRequest(documentIndex)
                .source(searchSourceBuilder)
                .scroll(SCROLL_DEFAULT_TIMEOUT_DURATION); //time-out scrolling after 15 secs
    }

    /**
     * Find all command but using scroll search with default configurations.
     *
     * @throws IOException - on no connection to ES.
     * @see org.springframework.data.repository.CrudRepository#findAll()
     */
    //todo: should be a simple way through the Elastic client configurator to increase the max window size.
    public <T> List<T> scrollSearchFindAll(String documentIndex, Class<T> clazz) throws IOException {
        return scrollSearch(buildScrollSearchRequest(documentIndex, null), clazz);
    }

    /**
     * Methods allows searching beyound the ElasticSearch setting limit for index window size.
     * Note: Well... it's probly set low for a reason but whatevz for demo.
     *
     * @throws IOException - on no connection to ES.
     */
    public <T> List<T> scrollSearch(SearchRequest searchRequest, Class<T> clazz) throws IOException {
        //execute initial search
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        String scrollId = searchResponse.getScrollId();
        SearchHit[] hits = searchResponse.getHits().getHits();

        List<T> foundItems = new ArrayList<>();

        while (hits != null && hits.length > 0) {
            //process hit items and store into list
            SerializationUtil.parseSearchHitResultsAndPlaceIntoList(foundItems, hits, clazz);

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

        return foundItems;
    }
}
