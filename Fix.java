import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Fix {
    public static void main(String[] args) throws IOException {
        File modDir = new File(".");
        File recipesDir = new File(modDir, "src/main/resources/data/rsgauges/recipe");
        File lootDir = new File(modDir, "src/main/resources/data/rsgauges/loot_table/block");

        // 1. Fix Recipes
        if (recipesDir.exists()) {
            for (File file : recipesDir.listFiles()) {
                if (file.getName().endsWith(".json")) {
                    String content = Files.readString(file.toPath());
                    boolean modified = false;
                    
                    if (content.contains("\"conditions\"")) {
                        content = content.replace("\"conditions\"", "\"neoforge:conditions\"");
                        modified = true;
                    }
                    if (content.contains("\"item\":")) {
                        // replace "item" with "id" ONLY inside "result"
                        content = content.replaceAll("\"result\"\\s*:\\s*\\{\\s*\"item\"", "\"result\": {\n    \"id\"");
                        modified = true;
                    }
                    if (modified) {
                        Files.writeString(file.toPath(), content);
                    }
                }
            }
        }

        // 2. Generate Loot Tables for Blocks
        lootDir.mkdirs();
        File modContentFile = new File(modDir, "src/main/java/wile/rsgauges/ModContent.java");
        if (modContentFile.exists()) {
            String content = Files.readString(modContentFile.toPath());
            Matcher m = Pattern.compile("Registries\\.addBlock\\(\\s*\"([^\"]+)\"").matcher(content);
            while (m.find()) {
                String blockName = m.group(1);
                String lootJson = "{\n" +
                        "  \"type\": \"minecraft:block\",\n" +
                        "  \"pools\": [\n" +
                        "    {\n" +
                        "      \"rolls\": 1.0,\n" +
                        "      \"bonus_rolls\": 0.0,\n" +
                        "      \"entries\": [\n" +
                        "        {\n" +
                        "          \"type\": \"minecraft:item\",\n" +
                        "          \"name\": \"rsgauges:" + blockName + "\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"conditions\": [\n" +
                        "        {\n" +
                        "          \"condition\": \"minecraft:survives_explosion\"\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";
                File lootFile = new File(lootDir, blockName + ".json");
                Files.writeString(lootFile.toPath(), lootJson);
            }
        }
    }
}
