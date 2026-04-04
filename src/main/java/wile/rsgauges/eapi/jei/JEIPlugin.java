/*
 * @file JEIPlugin.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2020 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 */
package wile.rsgauges.eapi.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import wile.rsgauges.ModConfig;
import wile.rsgauges.ModRsGauges;
import wile.rsgauges.libmc.detail.Auxiliaries;
import wile.rsgauges.libmc.detail.Registries;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@mezz.jei.api.JeiPlugin
public class JEIPlugin implements IModPlugin
{
  @Override
  public @NotNull ResourceLocation getPluginUid()
  {
    // 1.21.1 Syntax für ResourceLocation
    return ResourceLocation.fromNamespaceAndPath(ModRsGauges.MODID, "jei_plugin_uid");
  }

  @Override
  public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime)
  {
    HashSet<Item> blacklisted = new HashSet<>();

    // FIX 1: Nutzt getBlockHolders und ruft die gebundenen Block-Werte ab
    for(DeferredHolder<Block, ? extends Block> holder : Registries.getBlockHolders()) {
      Block e = holder.value(); // Sicher, da JEI nach dem Registrierungs-Freeze läuft
      Item item = e.asItem();
      // Nutzt BuiltInRegistries
      ResourceLocation blockKey = BuiltInRegistries.BLOCK.getKey(e);
      ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(item);

      // FIX 2: Übergibt den Namen als String (blockKey.getPath()) an isOptedOut
      if(ModConfig.isOptedOut(blockKey.getPath()) && itemKey.getPath().equals(blockKey.getPath())) {
        blacklisted.add(item);
      }
    }

    for(Item e : Registries.getRegisteredItems()) {
      ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(e);
      // FIX 3: Übergibt den Namen als String (itemKey.getPath()) an isOptedOut
      if(ModConfig.isOptedOut(itemKey.getPath()) && (!(e instanceof BlockItem))) {
        blacklisted.add(e);
      }
    }

    if(!blacklisted.isEmpty()) {
      List<ItemStack> blacklist = blacklisted.stream().map(ItemStack::new).collect(Collectors.toList());
      try {
        jeiRuntime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, blacklist);
      } catch(Exception e) {
        Auxiliaries.logger().warn("Exception in JEI opt-out processing: '" + e.getMessage() + "'");
      }
    }
  }
}