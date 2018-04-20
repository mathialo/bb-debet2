/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.customelements;

import bbdebet2.gui.Main;
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
    }


    public StorageButton(String productName) {
        super(productName + "\nUtsolgt");

        setWrapText(true);
        setPrefHeight(100);
        setPrefWidth(140);
        setDisable(true);
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
