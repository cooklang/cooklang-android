package org.cooklang;

public class Cookware implements DirectionItem {
    public String name;

    private Float quantityFloat;
    private String quantityString;

    public void setQuantityFloat(Float newQuantity) {
        this.quantityFloat = newQuantity;
    }

    public void setQuantityString(String newQuantity) {
        this.quantityString = newQuantity;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public String toString() {
        return name;
    }
}
