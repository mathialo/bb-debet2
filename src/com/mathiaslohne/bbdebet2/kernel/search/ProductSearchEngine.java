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

package com.mathiaslohne.bbdebet2.kernel.search;

import com.mathiaslohne.bbdebet2.kernel.core.Kernel;
import com.mathiaslohne.bbdebet2.kernel.core.Product;
import com.mathiaslohne.bbdebet2.kernel.core.User;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ProductSearchEngine {

    private static List<Product> innerSearch(Kernel kernel, String query) {
        return kernel.getStorage().getProductSet().stream()
            .filter(product ->
                Arrays.stream(query.split("\\s+")).allMatch(chunk ->
                    product.getName().toLowerCase().contains(chunk.toLowerCase())
                )
            )
            .collect(Collectors.toList());
    }


    public static List<Product> search(Kernel kernel, String query) {
        List<Product> result = innerSearch(kernel, query);
        Map<String, Integer> salenums = kernel.getSalesHistory().filterLastItems(50).getSummary();
        result.sort(Comparator.comparingInt(p -> -salenums.getOrDefault(p.getName(), 0)));

        return result;
    }


    public static List<Product> search(Kernel kernel, String query, User user) {
        if (user == null) return search(kernel, query);

        List<Product> result = innerSearch(kernel, query);

        Map<String, Integer> salenumsTotal = kernel.getSalesHistory().filterLastItems(50).getSummary();
        Map<String, Integer> salenumsUser = kernel.getSalesHistory().filterOnUser(user).filterLastItems(50).getSummary();

        // Sort on total first, then users (sorting is stable, so if user has no purchases of a product, show global most popular first)
        result.sort(Comparator.comparingInt(p -> -salenumsTotal.getOrDefault(p.getName(), 0)));
        result.sort(Comparator.comparingInt(p -> -salenumsUser.getOrDefault(p.getName(), 0)));

        return result;
    }
}
