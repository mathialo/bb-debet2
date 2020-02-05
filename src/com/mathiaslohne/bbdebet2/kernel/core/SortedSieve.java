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

package com.mathiaslohne.bbdebet2.kernel.core;

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
