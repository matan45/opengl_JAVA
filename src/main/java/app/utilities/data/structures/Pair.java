package app.utilities.data.structures;

public class Pair<T, K> {
    T value;
    K value2;

    public Pair(T value, K value2) {
        this.value = value;
        this.value2 = value2;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public K getValue2() {
        return value2;
    }

    public void setValue2(K value2) {
        this.value2 = value2;
    }
}
