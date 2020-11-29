package evans18.lunatechairportsbackend.data.repository.country;

import evans18.lunatechairportsbackend.data.model.Country;
import lombok.AllArgsConstructor;
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
import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class CountryService {
    private final RestHighLevelClient elasticsearchClient;

    /**
     * Finds countries by providing a partial variation of their name.
     *
     * @param queryString - partial name of a country: may be exact, partial of a multi-word, prefix, suffix, white space delimited anywhere.
     */
    public List<Country> searchCountryByPartialCountryName(String queryString) {
        String aQueryWithPartialSearch;

        //decide if multiple tokens
        if (queryString.contains(" ")) {
            String[] arrTokens = queryString.split("\\s+");
            aQueryWithPartialSearch = Arrays.stream(arrTokens).map(p -> "*" + p + "*").collect(Collectors.joining(" "));
        } else aQueryWithPartialSearch = "*" + queryString + "*";

        //build queries
        final BoolQueryBuilder aQuery = new BoolQueryBuilder()
                .must(QueryBuilders.queryStringQuery(aQueryWithPartialSearch)
//                        .defaultField("name")
                );

        //todo add name + code

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(aQuery)
                .withSort(new ScoreSortBuilder().order(SortOrder.DESC)) //best hit score first
                .build();
        //execute search
        SearchHits<Country> searchHits = new ElasticsearchRestTemplate(elasticsearchClient)
                .search(nativeSearchQuery, Country.class);

        //process
        return searchHits.stream()
                .sorted((o1, o2) -> {
                    int cmpScore = -Float.compare(o1.getScore(), o2.getScore()); //best hit score first
                    if (cmpScore == 0)
                        return Integer.compare(o1.getContent().getName().length(), o2.getContent().getName().length()); //sort by name length ASC //todo: there might be a script available to do this at search
                    return cmpScore;
                })
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}