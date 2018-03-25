import java.util.AbstractMap;

public class Pair<K, V> extends AbstractMap.SimpleEntry<K, V> {

    public static <K, V> Pair<K, V> of(final K first, final V second) {
        return new Pair<>(first, second);
    }

    public Pair(final K first, final V second) {
        super(first, second);
    }
}
