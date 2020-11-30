package evans18.lunatechairportsbackend.util;

import com.google.gson.Gson;
import org.elasticsearch.search.SearchHit;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SerializationUtil {
    public static Gson gson = new Gson();

    public static <T> void parseSearchHitResultsAndPlaceIntoList(List<T> list, SearchHit[] searchHits, Class<T> clazz) {
        list.addAll(Arrays.stream(searchHits)
                .map(h -> gson.fromJson(h.getSourceAsString(), clazz))
                .collect(Collectors.toList())
        );
    }
}
