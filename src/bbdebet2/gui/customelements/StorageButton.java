/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.gui.customelements;

import bbdebet2.kernel.datastructs.CurrencyFormatter;
import bbdebet2.kernel.datastructs.Product;
import javafx.scene.control.Button;

public class StorageButton extends Button {

    private Product product;

    public StorageButton() {
        super("Annet");

        setWrapText(true);
        setPrefHeight(100);
        setPrefWidth(140);
    }


    public StorageButton(Product product) {
        super(String.format("%s\n%s", product.getName(), CurrencyFormatter.format(product.getSellPrice())));

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


    public Product getProduct() {
        return product;
    }
}
