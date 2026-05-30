import os
import json
import glob

# Paths
mod_dir = r"c:\Users\Fabia\Desktop\Java\rsgaugesport"
recipes_dir = os.path.join(mod_dir, "src", "main", "resources", "data", "rsgauges", "recipe")

java_code = """package wile.rsgauges.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import wile.rsgauges.libmc.detail.Registries;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    private Item getItem(String id) {
        if(id.startsWith("minecraft:")) {
            return BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
        }
        return Registries.getItem(id.replace("rsgauges:", ""));
    }

    private TagKey<Item> getTag(String id) {
        return ItemTags.create(ResourceLocation.parse(id));
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
"""

for root, dirs, files in os.walk(recipes_dir):
    for file in files:
        if file.endswith(".json"):
            filepath = os.path.join(root, file)
            with open(filepath, "r", encoding="utf-8") as f:
                try:
                    data = json.load(f)
                except:
                    continue
            
            recipe_type = data.get("type")
            result_id = None
            if "result" in data:
                if isinstance(data["result"], str):
                    result_id = data["result"]
                elif isinstance(data["result"], dict):
                    result_id = data["result"].get("id", data["result"].get("item"))
                    
            if not result_id: continue
            
            # handle count
            count = 1
            if "result" in data and isinstance(data["result"], dict):
                count = data["result"].get("count", 1)

            result_name = result_id.replace("rsgauges:", "")
            recipe_name = file.replace(".json", "")
            
            conditions_code = ""
            if "neoforge:conditions" in data or "conditions" in data:
                # We can't easily parse arbitrary conditions into Java, but most are optional block checks.
                pass
            
            if recipe_type == "minecraft:crafting_shapeless":
                ingredients = data.get("ingredients", [])
                java_code += f"        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem(\"{result_id}\"), {count})\n"
                for ing in ingredients:
                    if isinstance(ing, str):
                        java_code += f"            .requires(getItem(\"{ing}\"))\n"
                    elif isinstance(ing, dict):
                        if "item" in ing:
                            java_code += f"            .requires(getItem(\"{ing['item']}\"))\n"
                        elif "tag" in ing:
                            java_code += f"            .requires(getTag(\"{ing['tag']}\"))\n"
                java_code += f"            .unlockedBy(\"has_item\", has(Items.REDSTONE))\n"
                java_code += f"            .save(output, ResourceLocation.fromNamespaceAndPath(\"rsgauges\", \"{recipe_name}\"));\n\n"
                
            elif recipe_type == "minecraft:crafting_shaped":
                pattern = data.get("pattern", [])
                java_code += f"        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem(\"{result_id}\"), {count})\n"
                for line in pattern:
                    java_code += f"            .pattern(\"{line}\")\n"
                
                key = data.get("key", {})
                for k, v in key.items():
                    if isinstance(v, str):
                        java_code += f"            .define('{k}', getItem(\"{v}\"))\n"
                    elif isinstance(v, dict):
                        if "item" in v:
                            java_code += f"            .define('{k}', getItem(\"{v['item']}\"))\n"
                        elif "tag" in v:
                            java_code += f"            .define('{k}', getTag(\"{v['tag']}\"))\n"
                        elif isinstance(v, list):
                            # Usually array of dicts for alternatives
                            java_code += f"            .define('{k}', Ingredient.of("
                            for i, alt in enumerate(v):
                                if "item" in alt:
                                    java_code += f"getItem(\"{alt['item']}\")"
                                elif "tag" in alt:
                                    java_code += f"getTag(\"{alt['tag']}\")"
                                if i < len(v) - 1: java_code += ", "
                            java_code += f"))\n"
                java_code += f"            .unlockedBy(\"has_item\", has(Items.REDSTONE))\n"
                java_code += f"            .save(output, ResourceLocation.fromNamespaceAndPath(\"rsgauges\", \"{recipe_name}\"));\n\n"

java_code += """    }
}
"""

out_path = os.path.join(mod_dir, "src", "main", "java", "wile", "rsgauges", "datagen", "ModRecipeProvider.java")
with open(out_path, "w", encoding="utf-8") as f:
    f.write(java_code)
print(f"Generated {out_path}")
