/*
 * Copyright (c) 2018. Mathias Lohne
 */

package kernel.datastructs;

public class SortedSieve<Key extends Comparable<Key>, Value> extends Sieve<Key, Value> {

    private int numInserted;

    public SortedSieve(int size) {
        super(size);
        numInserted = 0;
    }

    public void put(Key key, Value value) {
        if (size == 0) {
            return;
        }

        if (numInserted == 0) {
            keys.set(0, key);
            values.set(0, value);

            numInserted++;

        } else if (keys.get(size-1) != null && keys.get(size-1).compareTo(key) > 0) {
            // key is smaller than all other keys in sieve, stop and do nothing.

        } else {
            int i = 0;

            if (numInserted < size)  i = numInserted;
            else i = numInserted-1;

            while (i > 0 && key.compareTo(keys.get(i-1)) > 0) {
                keys.set(i, keys.get(i-1));
                values.set(i, values.get(i-1));
                i--;
            }

            keys.set(i, key);
            values.set(i, value);

            if (numInserted < size) numInserted++;
        }
    }
}
