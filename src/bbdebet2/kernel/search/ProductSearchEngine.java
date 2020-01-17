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

package bbdebet2.kernel.search;

import bbdebet2.kernel.Kernel;
import bbdebet2.kernel.datastructs.Product;
import bbdebet2.kernel.datastructs.User;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class ProductSearchEngine {

    public static List<Product> search(Kernel kernel, String query) {
        Set<Product> productSet = kernel.getStorage().getProductSet();

        List<Product> result = productSet.stream().filter(product -> product.getName().toLowerCase().contains(query.toLowerCase())).collect(Collectors.toList());

        Map<String, Integer> salenums = kernel.getSalesHistory().filterLastItems(50).getSummary();
        result.sort(Comparator.comparingInt(p -> -salenums.getOrDefault(p.getName(), 0)));

        return result;
    }


    public static List<Product> search(Kernel kernel, String query, User user) {
        if (user == null) return search(kernel, query);

        Set<Product> productSet = kernel.getStorage().getProductSet();

        List<Product> result = productSet.stream().filter(product -> product.getName().toLowerCase().contains(query.toLowerCase())).collect(Collectors.toList());

        Map<String, Integer> salenums = kernel.getSalesHistory().filterOnUser(user).filterLastItems(50).getSummary();
        result.sort(Comparator.comparingInt(p -> -salenums.getOrDefault(p.getName(), 0)));

        return result;
    }
}
