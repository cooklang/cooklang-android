package org.cooklang;

public class Cookware implements DirectionItem {
    public String name;

    public void setName(String newName) {
        this.name = newName;
    }

    public String toString() {
        return name;
    }
}
