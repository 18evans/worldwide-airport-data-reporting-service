package evans18.lunatechairportsbackend.data.repository.country;

import evans18.lunatechairportsbackend.data.model.Country;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static evans18.lunatechairportsbackend.data.repository.ElasticSearchConstants.ES_DOC_COUNTRY_FIELD_COUNTRY_CODE;
import static evans18.lunatechairportsbackend.data.repository.ElasticSearchConstants.ES_DOC_COUNTRY_FIELD_COUNTRY_NAME;


@Service
@RequiredArgsConstructor
public class CountryService {

    private static final String DELIMITER_SEARCH_MULTI_TOKEN = " ";
    public static final Comparator<Country> COMPARATOR_COUNTRY_NAME_LENGTH = Comparator.comparingInt(country -> country.getName().length());

    private final RestHighLevelClient elasticsearchClient;

    /**
     * Searches for countries by providing a partial variation of country's name or code.
     * Results are sorted by best search hits first (descending).
     *
     * @param queryString - partial name of a country: may be exact, partial of a multi-word, prefix, suffix, white space delimited anywhere.
     * @return - wrapper of search hits for countries.
     */
    private SearchHits<Country> searchCountriesByPartialCountryNameOrCode(String queryString) {
        String queryForPartialSearch;

        if (queryString.contains(DELIMITER_SEARCH_MULTI_TOKEN)) { //multiple tokens
            String[] arrTokens = queryString.split("\\s+");
            queryForPartialSearch = Arrays.stream(arrTokens).map(p -> "*" + p + "*").collect(Collectors.joining(" "));
        } else queryForPartialSearch = "*" + queryString + "*";

        //build queries
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(new BoolQueryBuilder()
                        .must(QueryBuilders.queryStringQuery(queryForPartialSearch))
                )
                .withFields(ES_DOC_COUNTRY_FIELD_COUNTRY_NAME, ES_DOC_COUNTRY_FIELD_COUNTRY_CODE) //fields to look in
                .withSort(new ScoreSortBuilder().order(SortOrder.DESC)) //best hit score first
                .build();

        //execute search
        return new ElasticsearchRestTemplate(elasticsearchClient)
                .search(nativeSearchQuery, Country.class);
    }

    /**
     * Searches and retrieves a country matching in its country name or code the given partial string.
     * Retrieves single country with the most accurate score with the shortest country name length.
     *
     * @param queryString - partial country code or name.
     * @return - optional of .
     */
    public Optional<Country> findTopCountryByPartialCountryNameOrCode(String queryString) {
        SearchHits<Country> searchResults = searchCountriesByPartialCountryNameOrCode(queryString);

        float scoreHighest = searchResults.getMaxScore();

        return searchResults.stream()
                .filter(hit -> hit.getScore() == scoreHighest) //todo: consider speed of filtering at ES search level
                .map(SearchHit::getContent)
                .min(COMPARATOR_COUNTRY_NAME_LENGTH);
    }

    /**
     * Searches and retrieves all countries matching in its country name or code the given partial string.
     * Results are sorted by most accurate score and then by shortest country name length.
     *
     * @param queryString - partial country code or name.
     * @return - optional of .
     */
    public List<Country> findCountriesByPartialCountryNameOrCode(String queryString) {
        return searchCountriesByPartialCountryNameOrCode(queryString).stream()
                .sorted((o1, o2) -> {
                    int cmpScore = -Float.compare(o1.getScore(), o2.getScore()); //best hit score first //note: this is already done at ES search level (i.e. maintaining)
                    if (cmpScore == 0)
                        return COMPARATOR_COUNTRY_NAME_LENGTH.compare(o1.getContent(), o2.getContent()); //in addition - sort by name length //note: block only exists because of this line //todo: there should be an ES script to do this at search level
                    return cmpScore;
                })
                .map(SearchHit::getContent)
                .collect(Collectors.toUnmodifiableList());
    }


}