$RecipesDir = "src\main\resources\data\rsgauges\recipe"
$OutFile = "src\main\java\wile\rsgauges\datagen\ModRecipeProvider.java"

$JavaCode = @"
package wile.rsgauges.datagen;

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
"@

$Files = Get-ChildItem -Path $RecipesDir -Recurse -Filter "*.json"

foreach ($File in $Files) {
    try {
        $JsonContent = Get-Content $File.FullName -Raw | ConvertFrom-Json
    } catch {
        continue
    }
    
    $Type = $JsonContent.type
    if (-not $Type) { continue }
    
    $ResultId = $null
    $Count = 1
    
    if ($JsonContent.result) {
        if ($JsonContent.result -is [string]) {
            $ResultId = $JsonContent.result
        } elseif ($JsonContent.result.id) {
            $ResultId = $JsonContent.result.id
        } elseif ($JsonContent.result.item) {
            $ResultId = $JsonContent.result.item
        }
        
        if ($JsonContent.result.count) {
            $Count = $JsonContent.result.count
        }
    }
    
    if (-not $ResultId) { continue }
    
    $RecipeName = $File.BaseName
    
    if ($Type -eq "minecraft:crafting_shapeless") {
        $JavaCode += "`n        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem(`"$ResultId`"), $Count)"
        
        foreach ($Ing in $JsonContent.ingredients) {
            if ($Ing -is [string]) {
                $JavaCode += "`n            .requires(getItem(`"$Ing`"))"
            } elseif ($Ing.item) {
                $JavaCode += "`n            .requires(getItem(`"$($Ing.item)`"))"
            } elseif ($Ing.tag) {
                $JavaCode += "`n            .requires(getTag(`"$($Ing.tag)`"))"
            }
        }
        $JavaCode += "`n            .unlockedBy(`"has_item`", has(Items.REDSTONE))"
        $JavaCode += "`n            .save(output, ResourceLocation.fromNamespaceAndPath(`"rsgauges`", `"$RecipeName`"));`n"
    }
    elseif ($Type -eq "minecraft:crafting_shaped") {
        $JavaCode += "`n        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem(`"$ResultId`"), $Count)"
        
        foreach ($Line in $JsonContent.pattern) {
            $JavaCode += "`n            .pattern(`"$Line`")"
        }
        
        if ($JsonContent.key) {
            foreach ($K in $JsonContent.key.PSObject.Properties) {
                $KeyChar = $K.Name
                $Val = $K.Value
                if ($Val -is [string]) {
                    $JavaCode += "`n            .define('$KeyChar', getItem(`"$Val`"))"
                } elseif ($Val.item) {
                    $JavaCode += "`n            .define('$KeyChar', getItem(`"$($Val.item)`"))"
                } elseif ($Val.tag) {
                    $JavaCode += "`n            .define('$KeyChar', getTag(`"$($Val.tag)`"))"
                } elseif ($Val -is [array]) {
                    # Arrays in keys mean alternative items
                    $JavaCode += "`n            .define('$KeyChar', Ingredient.of("
                    $Items = @()
                    foreach ($Alt in $Val) {
                        if ($Alt.item) { $Items += "getItem(`"$($Alt.item)`")" }
                        elseif ($Alt.tag) { $Items += "getTag(`"$($Alt.tag)`")" }
                    }
                    $JavaCode += ($Items -join ", ") + "))"
                }
            }
        }
        
        $JavaCode += "`n            .unlockedBy(`"has_item`", has(Items.REDSTONE))"
        $JavaCode += "`n            .save(output, ResourceLocation.fromNamespaceAndPath(`"rsgauges`", `"$RecipeName`"));`n"
    }
}

$JavaCode += @"
    }
}
"@

Set-Content -Path $OutFile -Value $JavaCode -Encoding UTF8
Write-Host "Generated ModRecipeProvider.java"
