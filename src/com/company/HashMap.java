package com.company;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class HashMap<V>  {
    static private final int m = 13;
    private final List<LinkedList<Pair<String, V>>> array;

    public HashMap() {
        this.array = new ArrayList<>(m);

        for (int i = 0; i < m; i++) {
            array.add(new LinkedList<>());
        }
    }

    static private int hash(String identifier) {
        int accumulator = 0;
        for (char c : identifier.toCharArray()) {
            accumulator += c;
        }

        return accumulator % m;
    }

    public Pair<String, V> add(String identifier, V value) {
        int hash = hash(identifier);

        Pair<String, V> pair =  new Pair<>(identifier, value);
        array.get(hash).add(pair);

        return pair;
    }

    public Pair<String, V> search(String key) {
        // Calculate the hash of the key
        int hash = hash(key);

        LinkedList<Pair<String, V>> list = array.get(hash);

        Optional<Pair<String, V>> entry = list.stream()
                .filter(stringVPair -> stringVPair.first.equals(key))
                .findAny();

        if(entry.isEmpty())
            return null;

        return entry.get();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        for (LinkedList<Pair<String, V>> list:
             array) {
            str.append(list.toString()).append('\n');
        }

        return str.toString();
    }
}
