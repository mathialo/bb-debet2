/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.kernel.datastructs;

public class ProductQuery {

    private boolean anyName = false;
    private boolean anyPrice = false;

    private boolean changeName = false;
    private boolean changePrice = false;

    private String name;
    private double price;


    public void setAnyName() {
        anyName = true;
    }


    public void setAnyPrice() {
        anyPrice = true;
    }


    public void setChangeName() {
        this.changeName = true;
    }


    public void setChangePrice() {
        this.changePrice = true;
    }


    public void setName(String name) {
        this.name = name;
    }


    public void setPrice(double price) {
        this.price = price;
    }


    private boolean doubleEqual(double d1, double d2) {
        return Math.abs(d1 - d2) < 1e-7;
    }


    public boolean match(Product product) {
        boolean[] results = new boolean[2];

        results[0] = anyName || product.getName().equals(name);
        results[1] = anyPrice || doubleEqual(product.getSellPrice(), price);

        return results[0] && results[1];
    }


    public boolean changeName() {
        return changeName;
    }


    public boolean changePrice() {
        return changePrice;
    }
}
