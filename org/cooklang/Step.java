package org.cooklang;

import java.util.*;

public class Step {    
    public List<DirectionItem> directions = new ArrayList<>();
    // public List<Timer> timers = new ArrayList<>();
    // public List<Equipment> equipments = new ArrayList<>();

    public void addTextItem(TextItem texItem) {
        directions.add(texItem);
    }

    public void addIngredient(Ingredient ingredient) {
        directions.add(ingredient);
    }

    public String toString() {
        if (directions == null || directions.size() <= 0) return "";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < directions.size(); i++) {

            sb.append(directions.get(i).toString());

            // if not the last item
            if (i != directions.size() - 1) {
                sb.append(",");
            }

        }

        return sb.toString();
    }
}
