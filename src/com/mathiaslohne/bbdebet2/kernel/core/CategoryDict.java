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

import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;


public class CategoryDict implements Exportable {

    private Map<String, Category> productToCategory;
    private Map<String, Category> existingCategories;


    public CategoryDict() {
        productToCategory = new TreeMap<>();
        existingCategories = new TreeMap<>();
    }


    public CategoryDict(File file) throws IOException, ErrorInFileException {
        productToCategory = new TreeMap<>();
        existingCategories = new TreeMap<>();
        readCsv(file);
    }


    public Category getProductCategory(String productName) {
        return productToCategory.get(productName);
    }


    public Category getProductCategory(Product product) {
        return getProductCategory(product.getName());
    }


    public Category getCategoryObject(String categoryName) {
        return existingCategories.get(categoryName);
    }


    public Category newCategory(String categoryName, Color color) {
        if (existingCategories.containsKey(categoryName)) return existingCategories.get(categoryName);

        Category category = new Category(categoryName, color);
        existingCategories.put(categoryName, category);

        return category;
    }


    public void updateCategory(String oldName, String newName, Color newColor) {
        Category category = existingCategories.remove(oldName);
        if (category == null) throw new IllegalArgumentException("No category named '" + oldName + "'");

        category.setName(newName);
        category.setColor(newColor);

        existingCategories.put(newName, category);
    }


    public List<String> getRulesInCategory(Category category) {
        List<String> list = new LinkedList<>();

        for (String productName : productToCategory.keySet()) {
            if (productToCategory.get(productName) == category) list.add(productName);
        }

        return list;
    }


    public void deleteCategory(Category category) {
        // Create list because of invariant in for-each loop
        List<String> rulesToDelete = new LinkedList<>();

        for (String productName : productToCategory.keySet()) {
            if (productToCategory.get(productName) == category) {
                rulesToDelete.add(productName);
            }
        }

        for (String productName : rulesToDelete) productToCategory.remove(productName);

        existingCategories.remove(category.getName());
    }


    public void newCategoryRule(String productName, Category category) {
        productToCategory.put(productName, category);
    }


    public void newCategoryRule(Product product, Category category) {
        newCategoryRule(product.getName(), category);
    }


    public void deleteCategoryRule(String productName) {
        productToCategory.remove(productName);
    }


    public void deleteCategoryRule(Product product) {
        deleteCategoryRule(product.getName());
    }


    public Collection<Category> getCategories() {
        return existingCategories.values();
    }


    public boolean contains(String categoryName) {
        for (String existingName : existingCategories.keySet()) {
            if (categoryName.equals(existingName)) return true;
        }

        return false;
    }


    public Collection<String> getProductNames() {
        return productToCategory.keySet();
    }


    private void readCsv(File file) throws IOException, ErrorInFileException {
        Scanner scanner = new Scanner(file);

        // Skip header
        scanner.nextLine();

        try {
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split("\\s*,\\s*");

                Category c = null;
                if (!existingCategories.containsKey(line[1])) {
                    c = newCategory(line[1], Color.web(line[2]));
                } else {
                    c = existingCategories.get(line[1]);
                }

                newCategoryRule(line[0], c);
            }
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            throw new ErrorInFileException("Feil i kategorifil");
        }

        scanner.close();
    }


    @Override
    public void saveFile(File file) throws IOException {
        PrintWriter printWriter = new PrintWriter(file);
        printWriter.println("ProductName,Category,Color");

        for (String productName : productToCategory.keySet()) {
            printWriter.println(
                productName + "," + productToCategory.get(productName).getName() + "," + productToCategory.get(productName).getColorHtml()
            );
        }

        printWriter.close();
    }


    @Override
    public void saveFile() throws IOException {
        saveFile(new File(Kernel.CATEGORIES_FILEPATH));
    }


    public class Category {

        private String name;
        private Color color;


        private Category(String name, Color color) {
            this.name = name;
            this.color = color;
        }


        public Color getColor() {
            return color;
        }


        /**
         * Convert color to html encoding. From StackOverflow at
         * https://stackoverflow.com/questions/17925318/how-to-get-hex-web-string-from-javafx-colorpicker-color
         *
         * @return Color in HTML format
         */
        public String getColorHtml() {
            return String.format(
                "#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255)
            );
        }


        public String getName() {
            return name;
        }


        private void setName(String name) {
            this.name = name;
        }


        private void setColor(Color color) {
            this.color = color;
        }


        public boolean isCategory(String productName) {
            return productName.equals(this.name);
        }


        public boolean isCategory(Product product) {
            return product.getName().equals(name);
        }


        @Override
        public String toString() {
            return name;
        }
    }

}
