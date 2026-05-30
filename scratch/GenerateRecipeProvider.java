import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class GenerateRecipeProvider {
    public static void main(String[] args) throws Exception {
        String recipesDir = "src/main/resources/data/rsgauges/recipe";
        StringBuilder javaCode = new StringBuilder();
        javaCode.append("package wile.rsgauges.datagen;\n\n")
            .append("import net.minecraft.core.HolderLookup;\n")
            .append("import net.minecraft.data.PackOutput;\n")
            .append("import net.minecraft.data.recipes.RecipeCategory;\n")
            .append("import net.minecraft.data.recipes.RecipeOutput;\n")
            .append("import net.minecraft.data.recipes.RecipeProvider;\n")
            .append("import net.minecraft.data.recipes.ShapedRecipeBuilder;\n")
            .append("import net.minecraft.data.recipes.ShapelessRecipeBuilder;\n")
            .append("import net.minecraft.resources.ResourceLocation;\n")
            .append("import net.minecraft.world.item.Items;\n")
            .append("import net.minecraft.world.item.crafting.Ingredient;\n")
            .append("import net.minecraft.tags.ItemTags;\n")
            .append("import net.minecraft.tags.TagKey;\n")
            .append("import net.minecraft.world.item.Item;\n")
            .append("import net.minecraft.core.registries.BuiltInRegistries;\n")
            .append("import wile.rsgauges.libmc.detail.Registries;\n")
            .append("import java.util.concurrent.CompletableFuture;\n\n")
            .append("public class ModRecipeProvider extends RecipeProvider {\n")
            .append("    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {\n")
            .append("        super(output, lookupProvider);\n")
            .append("    }\n\n")
            .append("    private Item getItem(String id) {\n")
            .append("        if(id.startsWith(\"minecraft:\")) {\n")
            .append("            return BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));\n")
            .append("        }\n")
            .append("        return Registries.getItem(id.replace(\"rsgauges:\", \"\"));\n")
            .append("    }\n\n")
            .append("    private TagKey<Item> getTag(String id) {\n")
            .append("        return ItemTags.create(ResourceLocation.parse(id));\n")
            .append("    }\n\n")
            .append("    @Override\n")
            .append("    protected void buildRecipes(RecipeOutput output) {\n");

        try (Stream<Path> paths = Files.walk(Path.of(recipesDir))) {
            paths.filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".json")).forEach(p -> {
                try {
                    String content = Files.readString(p);
                    String fileName = p.getFileName().toString().replace(".json", "");
                    
                    // A rudimentary JSON parser for our specific format
                    String type = extractString(content, "\"type\": \"", "\"");
                    if (type == null) return;
                    
                    String resultId = null;
                    int count = 1;
                    if (content.contains("\"result\": {")) {
                        resultId = extractString(content.substring(content.indexOf("\"result\": {")), "\"id\": \"", "\"");
                        if (resultId == null) {
                            resultId = extractString(content.substring(content.indexOf("\"result\": {")), "\"item\": \"", "\"");
                        }
                        String countStr = extractString(content.substring(content.indexOf("\"result\": {")), "\"count\": ", ",");
                        if (countStr != null) {
                            try { count = Integer.parseInt(countStr.trim().replaceAll("[^0-9]", "")); } catch (Exception ignored) {}
                        }
                    } else if (content.contains("\"result\": \"")) {
                        resultId = extractString(content, "\"result\": \"", "\"");
                    }
                    if (resultId == null) return;

                    if (type.equals("minecraft:crafting_shapeless")) {
                        javaCode.append("        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem(\"").append(resultId).append("\"), ").append(count).append(")\n");
                        String ingredientsBlock = extractBlock(content, "\"ingredients\": [", "]");
                        if (ingredientsBlock != null) {
                            String[] items = ingredientsBlock.split(",");
                            for (String item : items) {
                                String id = extractString(item, "\"", "\"");
                                if (id == null) continue;
                                javaCode.append("            .requires(getItem(\"").append(id).append("\"))\n");
                            }
                        }
                        javaCode.append("            .unlockedBy(\"has_item\", has(Items.REDSTONE))\n");
                        javaCode.append("            .save(output, ResourceLocation.fromNamespaceAndPath(\"rsgauges\", \"").append(fileName).append("\"));\n\n");
                    } else if (type.equals("minecraft:crafting_shaped")) {
                        javaCode.append("        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem(\"").append(resultId).append("\"), ").append(count).append(")\n");
                        String patternBlock = extractBlock(content, "\"pattern\": [", "]");
                        if (patternBlock != null) {
                            String[] lines = patternBlock.split(",");
                            for (String line : lines) {
                                String pat = extractString(line, "\"", "\"");
                                if (pat == null) continue;
                                javaCode.append("            .pattern(\"").append(pat).append("\")\n");
                            }
                        }
                        String keyBlock = extractBlock(content, "\"key\": {", "}");
                        if (keyBlock != null) {
                            String[] keys = keyBlock.split("},?");
                            for (String keyStr : keys) {
                                String k = extractString(keyStr, "\"", "\"");
                                if (k == null) continue;
                                String item = extractString(keyStr.substring(keyStr.indexOf(":")), "\"item\": \"", "\"");
                                if (item == null) {
                                    item = extractString(keyStr.substring(keyStr.indexOf(":")), "\"tag\": \"", "\"");
                                    if (item != null) {
                                        javaCode.append("            .define('").append(k).append("', getTag(\"").append(item).append("\"))\n");
                                        continue;
                                    }
                                }
                                if (item == null) {
                                    item = extractString(keyStr.substring(keyStr.indexOf(":")), "\"", "\""); // Fallback for simple string key
                                }
                                if (item != null) {
                                    javaCode.append("            .define('").append(k).append("', getItem(\"").append(item).append("\"))\n");
                                }
                            }
                        }
                        javaCode.append("            .unlockedBy(\"has_item\", has(Items.REDSTONE))\n");
                        javaCode.append("            .save(output, ResourceLocation.fromNamespaceAndPath(\"rsgauges\", \"").append(fileName).append("\"));\n\n");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        
        javaCode.append("    }\n}\n");
        Files.writeString(Path.of("src/main/java/wile/rsgauges/datagen/ModRecipeProvider.java"), javaCode.toString());
        System.out.println("Generated ModRecipeProvider.java");
    }

    private static String extractString(String source, String prefix, String suffix) {
        int start = source.indexOf(prefix);
        if (start == -1) return null;
        start += prefix.length();
        int end = source.indexOf(suffix, start);
        if (end == -1) return null;
        return source.substring(start, end);
    }

    private static String extractBlock(String source, String prefix, String suffix) {
        int start = source.indexOf(prefix);
        if (start == -1) return null;
        start += prefix.length();
        int end = source.indexOf(suffix, start);
        if (end == -1) return null;
        return source.substring(start, end);
    }
}
