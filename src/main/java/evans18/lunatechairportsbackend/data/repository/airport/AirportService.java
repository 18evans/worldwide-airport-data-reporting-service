package evans18.lunatechairportsbackend.data.repository.airport;

import evans18.lunatechairportsbackend.data.manager.ElasticSearchClientManager;
import evans18.lunatechairportsbackend.data.model.Airport;
import evans18.lunatechairportsbackend.data.repository.ElasticSearchConstants;
import evans18.lunatechairportsbackend.util.StreamExtensions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

@Service
@RequiredArgsConstructor
public class AirportService {

    public static final int COUNTRY_LIST_LENGTH_CEILING_FOR_TOP_AIRPORT_COUNT = 10;

    private final ElasticSearchClientManager clientManager;

    /**
     * Finds airports by a provided country code.
     *
     * @throws IOException - on no connection to ES.
     */
    public List<Airport> scrollSearchFindAllAirportsByCountryCode(String countryCode) throws IOException {
        return clientManager.scrollSearch(ElasticSearchClientManager.buildScrollSearchRequest(
                ElasticSearchConstants.DOCUMENT_INDEX_AIRPORTS,
                matchQuery(ElasticSearchConstants.ES_DOC_AIRPORT_FIELD_ISO_COUNTRY_CODE, countryCode)) //search only by this field
                , Airport.class);
    }

    /**
     * Finds airports using a scroll search.
     *
     * @throws IOException - on no connection to ES.
     */
    public List<Airport> scrollSearchFindAllAirports() throws IOException {
        return clientManager.scrollSearchFindAll(
                ElasticSearchConstants.DOCUMENT_INDEX_AIRPORTS,
                Airport.class);
    }


    /**
     * /**
     * Goes through all airports in the repository to find the countries with most/least many airports and returns the top 10.
     *
     * @param isMost - {@code true} if looking for countries with most airports. Else countries with least.
     * @return - sorted order of the codes of countries that have the most or least airports (denoted by param)
     * @throws IOException - on no connection to ES.
     */
    //todo this entire operation should be done ideally by ES
    public LinkedHashMap<String, Integer> findCountryCodesOfCountriesWithTop10MostOrLeastAirports(boolean isMost) throws IOException {
        Iterable<Airport> iterable = clientManager.scrollSearchFindAll(ElasticSearchConstants.DOCUMENT_INDEX_AIRPORTS, Airport.class); //todo: note - this is the bottleneck until operations could be migrated to ES

        //time: O(A)
        //space: O(C)
        Map<String, Integer> mapCountryCodeByAirportCount = StreamExtensions.getStream(iterable) //stream on all airports
                .collect(Collectors.groupingBy(Airport::getIso_country, Collectors.summingInt(x -> 1)));


        LinkedHashMap<String, Integer> sortedCountryCodesByCount = new LinkedHashMap<>();

        //for more readability but duplicate if checks handle comparison inside loop
        Comparator<Map.Entry<String, Integer>> comparator = Comparator.comparingLong(Map.Entry::getValue); //asc order
        if (!isMost) comparator = comparator.reversed(); //desc order

        /* Since we want only 10 elements they can be received following 10 iterative calls looking for the next biggest element
            and removing it from the pool of elements. Note, for removing from the pool needed is to recreate the map entry set.
            Due to our constant 10, for large quantities of countries, time complexity evaluates to linear O(C).
            However for quantities under or equal to the constant then time it's at least exponential O(C^2)
            Space: O(C)

            If ever the constant 10 becomes larger or ever changing visit commented block under.
         */
        for (int i = 0; i < COUNTRY_LIST_LENGTH_CEILING_FOR_TOP_AIRPORT_COUNT; i++) {

            //get next most/least frequent
            Map.Entry<String, Integer> element = mapCountryCodeByAirportCount.entrySet().stream()
                    .max(comparator) //max according TO comparator
                    .orElse(null);

            if (element == null) //no more elements
                break;

            String countryCode = element.getKey();
            int airportCount = element.getValue();

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