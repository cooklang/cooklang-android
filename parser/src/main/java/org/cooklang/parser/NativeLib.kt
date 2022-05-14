package org.cooklang.parser

class NativeLib {

    /**
     * A native method that is implemented by the 'parser' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'parser' library on application startup.
        init {
            System.loadLibrary("parser")
        }
    }
}