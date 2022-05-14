package org.cooklang.parser;

public class TextItem implements DirectionItem {
    public String value;

    public void setValue(String newValue) {
        this.value = newValue;
    }

    public String toString() {
        return value;
    }
}
