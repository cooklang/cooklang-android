package org.cooklang;

public class Parser {
    static { System.loadLibrary("cooklang"); }
    public static native Recipe parseRecipe(String text);
}
