package org.cooklang.parser;


import java.util.ArrayList;
import java.util.List;

public class Step {
    public List<DirectionItem> directions = new ArrayList<>();

    public void addTextItem(TextItem texItem) {
        directions.add(texItem);
    }

    public void addIngredient(Ingredient ingredient) {
        directions.add(ingredient);
    }

    public void addCookware(Cookware cookware) {
        directions.add(cookware);
    }

    public void addTimer(Timer timer) {
        directions.add(timer);
    }

    public String toString() {
        if (directions == null || directions.size() <= 0) return "";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < directions.size(); i++) {
            sb.append(directions.get(i).toString());
        }

        return sb.toString();
    }
}
