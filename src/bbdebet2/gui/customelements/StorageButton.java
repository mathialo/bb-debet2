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

package bbdebet2.gui.customelements;

import bbdebet2.gui.Main;
import bbdebet2.kernel.datastructs.CategoryDict;
import bbdebet2.kernel.datastructs.CurrencyFormatter;
import bbdebet2.kernel.datastructs.Product;
import javafx.scene.control.Button;

public class StorageButton extends Button {

    private Product product;

    private boolean isGlasUser;


    public StorageButton() {
        super("Annet");

        setWrapText(true);
        setPrefHeight(100);
        setPrefWidth(140);
    }


    public StorageButton(Product product) {
        super(String.format(
            "%s\n%s", product.getName(),
            CurrencyFormatter.format(product.getSellPrice())
        ));

        this.product = product;
        setWrapText(true);
        setPrefHeight(100);
        setPrefWidth(140);

        CategoryDict.Category category = Main.getKernel().getCategories().getProductCategory(product);

        if (category != null)
            setStyle("-fx-base: " + category.getColorHtml() + ";");
    }


    public StorageButton(String productName) {
        super(productName + "\nUtsolgt");

        setWrapText(true);
        setPrefHeight(100);
        setPrefWidth(140);
        setDisable(true);

        CategoryDict.Category category = Main.getKernel().getCategories().getProductCategory(productName);

        if (category != null)
            setStyle("-fx-base: " + category.getColorHtml() + ";");
    }


    public void setGlasUser(boolean glasUser) {
        isGlasUser = glasUser;

        if (product != null) {
            setText(String.format(
                "%s\n%s", product.getName(),
                CurrencyFormatter.format(convertPrice(product.getSellPrice()))
            ));
        }
    }


    public double convertPrice(double oldPrice) {
        if (isGlasUser) {
            double newPrice = 0;
            while (newPrice < oldPrice) {
                newPrice += Main.getKernel().getSettingsHolder().getGlasUserRoundTo();
            }
            return newPrice;
        } else {
            return oldPrice;
        }
    }


    public Product getProduct() {
        return product;
    }
}
