import org.cooklang.Parser;

public class Test {
    public static void main(String[] args) {
        System.out.println("Parsed: " + Parser.parseRecipe("Slice @bacon{1%piece} and things"));
    }
}
