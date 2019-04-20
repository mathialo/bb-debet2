/*
 * Copyright (C) 2019  Mathias Lohne
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package bbdebet2.kernel.datastructs;

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