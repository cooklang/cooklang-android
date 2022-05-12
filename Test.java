import org.cooklang.Parser;

public class Test {
    public static void main(String[] args) {
        String text = """
        >> cooking time: 30 min

        Add @chilli{3}, @ginger{10%g} and @milk{1%litre} place in #oven and cook for ~{10%minutes}
        """;
        System.out.println(Parser.parseRecipe(text));
    }
}
