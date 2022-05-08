package org.cooklang.jni;

public class Parser {
    static { System.loadLibrary("cooklang"); }
    public static native String parse(String recipe);
}
