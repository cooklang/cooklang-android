package org.cooklang;

public class Ingredient implements DirectionItem {
    public String name;

    private Float quantityFloat;
    private String quantityString;
    public String units;

    public void setName(String newName) {
        this.name = newName;
    }

    public void setQuantityFloat(Float newQuantity) {
        this.quantityFloat = newQuantity;
    }

    public void setQuantityString(String newQuantity) {
        this.quantityString = newQuantity;
    }

    public void setUnits(String newUnits) {
        this.units = newUnits;
    }

    public String toString() {
        return String.format("%s %s %s %s", name, quantityFloat, quantityString, units);
    }
}
