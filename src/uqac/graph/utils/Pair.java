package uqac.graph.utils;

public class Pair<V, T> {


    private V key;
    private T value;

    public Pair(V key, T value) {
        this.key = key;
        this.value = value;
    }

    public V getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }
}
