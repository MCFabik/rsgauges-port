import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FixTags {
    public static void main(String[] args) throws Exception {
        Path p = Path.of("src/main/java/wile/rsgauges/datagen/ModRecipeProvider.java");
        String content = Files.readString(p);
        
        // Replace getItem("#somemod:sometag") with getTag("somemod:sometag")
        Pattern pattern = Pattern.compile("getItem\\(\"(#[^\"]+)\"\\)");
        Matcher matcher = pattern.matcher(content);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String fullMatch = matcher.group(1);
            String tagName = fullMatch.substring(1); // remove '#'
            matcher.appendReplacement(sb, "getTag(\"" + tagName + "\")");
        }
        matcher.appendTail(sb);
        
        Files.writeString(p, sb.toString());
        System.out.println("Tags fixed.");
    }
}
