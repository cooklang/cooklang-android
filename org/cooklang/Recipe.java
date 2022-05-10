package org.cooklang;

import java.util.*;

public class Recipe {    
    public List<Step> steps = new ArrayList<>();
    // public metadata[]: [String: String] = [:]

    public void addStep(Step step) {
        steps.add(step);
    }

    public String toString() {
        if (steps == null || steps.size() <= 0) return "";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < steps.size(); i++) {

            sb.append(steps.get(i).toString());

            // if not the last item
            if (i != steps.size() - 1) {
                sb.append("\n");
            }

        }

        return sb.toString();

    }
}
