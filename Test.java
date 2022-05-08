import org.cooklang.jni.Parser;

public class Test {
    public static void main(String[] args) {
        System.out.println("Parsed: "+Parser.parse("Slice @bacon{1} and things"));
    }
}
