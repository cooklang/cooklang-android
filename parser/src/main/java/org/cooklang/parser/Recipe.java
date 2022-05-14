package org.cooklang.parser;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Recipe {
    public List<Step> steps = new ArrayList<>();
    public Hashtable<String, String> metadata = new Hashtable<String, String>();

    public void addStep(Step step) {
        steps.add(step);
    }

    public void addMetadata(String key, String value) {
        metadata.put(key, value);
    }

    public String toString() {
        if (steps == null || steps.size() <= 0) return "";

        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : metadata.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            sb.append(String.format(">> %s: %s\n", key, value));
        }

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
