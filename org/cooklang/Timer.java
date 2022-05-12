package org.cooklang;

public class Timer implements DirectionItem {
    public String units;
    public String name;

    private Float quantityFloat;
    private String quantityString;

    public void setQuantityFloat(Float newQuantity) {
        this.quantityFloat = newQuantity;
    }

    public void setQuantityString(String newQuantity) {
        this.quantityString = newQuantity;
    }

    public void setUnits(String newUnits) {
        this.units = newUnits;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public String toString() {
        return String.format("%s %s %s %s", name, quantityFloat, quantityString, units);
    }
}
