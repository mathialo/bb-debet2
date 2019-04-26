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

package bbdebet2.gui.modelwrappers;

import bbdebet2.kernel.datastructs.CategoryDict;
import bbdebet2.kernel.datastructs.Product;

import javafx.beans.property.SimpleStringProperty;


public class ViewCategoryRule {

    private final SimpleStringProperty productName;
    private final SimpleStringProperty categoryName;

    private CategoryDict.Category category;


    public ViewCategoryRule(String productName, CategoryDict.Category category) {
        this.productName = new SimpleStringProperty(productName);
        this.categoryName = new SimpleStringProperty(category.getName());

        this.category = category;
    }


    public ViewCategoryRule(Product product, CategoryDict.Category category) {
        this.productName = new SimpleStringProperty(product.getName());
        this.categoryName = new SimpleStringProperty(category.getName());

        this.category = category;
    }


    public String getProductName() {
        return productName.get();
    }


    public SimpleStringProperty productNameProperty() {
        return productName;
    }


    public String getCategoryName() {
        return categoryName.get();
    }


    public SimpleStringProperty categoryNameProperty() {
        return categoryName;
    }


    public CategoryDict.Category getCategory() {
        return category;
    }


    @Override
    public String toString() {
        return productName.get() + " -> " + categoryName.get();
    }
}
