package com.company;

public class Pair<K, V> {
    public K first;
    public V second;

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        if(first != null && second != null)
            return "[" + first.toString() + ", " + second.toString() + "]";

        if(first == null && second == null)
            return "[null, null]";

        if(first != null)
            return "[" + first.toString() + ", null]";

        return "[null, " + second.toString() + "]";

    }
}
