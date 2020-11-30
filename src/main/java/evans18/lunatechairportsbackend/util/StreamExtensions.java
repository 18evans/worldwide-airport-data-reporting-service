package evans18.lunatechairportsbackend.util;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamExtensions {

    /**
     * Abstraction of receiving a {@link Stream} type for {@link Iterable} structures.
     */
    public static <T> Stream<T> getStream(Iterable<T> iterable) {
        return StreamSupport.stream(
                iterable.spliterator(),
                false
        );
    }

}
