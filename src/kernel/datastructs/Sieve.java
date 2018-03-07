/*
 * Copyright (c) 2018. Mathias Lohne
 */

package kernel.datastructs;

import java.util.ArrayList;

public class Sieve<Key extends Comparable<Key>, Value> {

    protected ArrayList<Key> keys;
    protected ArrayList<Value> values;

    protected int size;


    @SuppressWarnings("unchecked")
    public Sieve(int size) {
        keys = new ArrayList<Key>(size);
        values = new ArrayList<Value>(size);

        this.size = size;

        // initialize arraylist with nulls
        for (int i = 0; i < size; i++) {
            keys.add(null);
            values.add(null);
        }
    }


    public ArrayList<Key> getMaxKeys() {
        return keys;
    }


    public ArrayList<Value> getMaxValues() {
        return values;
    }


    public void put(Key key, Value value) {
        if (size == 0) {
            return;
        }

        int smallestIndex = 0;

        for (int i = 0; i < size; i++) {
            // check if one spot is free
            if (keys.get(i) == null) {
                keys.set(i, key);
                values.set(i, value);
                return;
            }

            // find smallest index
            if (keys.get(i).compareTo(keys.get(smallestIndex)) < 0) {
                smallestIndex = i;
            }
        }

        if (key.compareTo(keys.get(smallestIndex)) > 0) {
            keys.set(smallestIndex, key);
            values.set(smallestIndex, value);
        }
    }


    public String toString() {
        String result = "[";

        for (Value v : values) {
            result += v + ", ";
        }

        return result + "]";
    }
}