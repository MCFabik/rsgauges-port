/*
 * @file OptionalRecipeCondition.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2020 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 *
 * Recipe condition to enable opt'ing out JSON based recipes.
 */
package wile.rsgauges.libmc.detail;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class OptionalRecipeCondition implements ICondition
{
  public static ResourceLocation NAME;

  // Raw fields specifically for the Codec serialization
  private final Optional<String> raw_result;
  private final List<String> raw_required;
  private final List<String> raw_missing;
  private final boolean experimental;

  // Parsed fields for logic
  private final @Nullable ResourceLocation result;
  private final boolean result_is_tag;
  private final List<ResourceLocation> all_required = new ArrayList<>();
  private final List<ResourceLocation> any_missing = new ArrayList<>();
  private final List<ResourceLocation> all_required_tags = new ArrayList<>();
  private final List<ResourceLocation> any_missing_tags = new ArrayList<>();

  private static boolean with_experimental = false;
  private static boolean without_recipes = false;
  private static Predicate<Block> block_optouts = (block)->false;
  private static Predicate<Item> item_optouts = (item)->false;

  // NeoForge 1.21.1: MapCodec replaces the old IConditionSerializer
  public static final MapCodec<OptionalRecipeCondition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
          Codec.STRING.optionalFieldOf("result").forGetter(c -> c.raw_result),
          Codec.STRING.listOf().optionalFieldOf("required", List.of()).forGetter(c -> c.raw_required),
          Codec.STRING.listOf().optionalFieldOf("missing", List.of()).forGetter(c -> c.raw_missing),
          Codec.BOOL.optionalFieldOf("experimental", false).forGetter(c -> c.experimental)
  ).apply(inst, OptionalRecipeCondition::new));

  public static void init(String modid, Logger logger)
  {
    NAME = ResourceLocation.fromNamespaceAndPath(modid, "optional");
  }

  public static void on_config(boolean enable_experimental, boolean disable_all_recipes,
                               Predicate<Block> block_optout_provider,
                               Predicate<Item> item_optout_provider)
  {
    with_experimental = enable_experimental;
    without_recipes = disable_all_recipes;
    block_optouts = block_optout_provider;
    item_optouts = item_optout_provider;
  }

  public OptionalRecipeCondition(Optional<String> raw_result, List<String> raw_required, List<String> raw_missing, boolean experimental)
  {
    this.raw_result = raw_result;
    this.raw_required = raw_required;
    this.raw_missing = raw_missing;
    this.experimental = experimental;

    if (raw_result.isPresent()) {
      String s = raw_result.get();
      if (s.startsWith("#")) {
        this.result = ResourceLocation.parse(s.substring(1));
        this.result_is_tag = true;
      } else {
        this.result = ResourceLocation.parse(s);
        this.result_is_tag = false;
      }
    } else {
      this.result = null;
      this.result_is_tag = false;
    }

    for (String s : raw_required) {
      if (s.startsWith("#")) all_required_tags.add(ResourceLocation.parse(s.substring(1)));
      else all_required.add(ResourceLocation.parse(s));
    }

    for (String s : raw_missing) {
      if (s.startsWith("#")) any_missing_tags.add(ResourceLocation.parse(s.substring(1)));
      else any_missing.add(ResourceLocation.parse(s));
    }
  }

  @Override
  public MapCodec<? extends ICondition> codec()
  { return CODEC; }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("Optional recipe, all-required: [");
    for(ResourceLocation e:all_required) sb.append(e.toString()).append(",");
    for(ResourceLocation e:all_required_tags) sb.append("#").append(e.toString()).append(",");
    if(!all_required.isEmpty() || !all_required_tags.isEmpty()) sb.delete(sb.length()-1, sb.length());
    sb.append("], any-missing: [");
    for(ResourceLocation e:any_missing) sb.append(e.toString()).append(",");
    for(ResourceLocation e:any_missing_tags) sb.append("#").append(e.toString()).append(",");
    if(!any_missing.isEmpty() || !any_missing_tags.isEmpty()) sb.delete(sb.length()-1, sb.length());
    sb.append("]");
    if(experimental) sb.append(" EXPERIMENTAL");
    return sb.toString();
  }

  @Override
  public boolean test(IContext context)
  {
    if(without_recipes) return false;
    if((experimental) && (!with_experimental)) return false;

    if(result != null) {
      boolean item_registered = BuiltInRegistries.ITEM.containsKey(result);
      if(!item_registered) return false; // required result not registered
      if(item_optouts.test(BuiltInRegistries.ITEM.get(result))) return false;
      if(BuiltInRegistries.BLOCK.containsKey(result) && block_optouts.test(BuiltInRegistries.BLOCK.get(result))) return false;
    }

    if(!all_required.isEmpty()) {
      for(ResourceLocation rl:all_required) {
        if(!BuiltInRegistries.ITEM.containsKey(rl)) return false;
      }
    }

    if(!all_required_tags.isEmpty()) {
      for(ResourceLocation rl:all_required_tags) {
        TagKey<Item> tag = TagKey.create(Registries.ITEM, rl);
        if(BuiltInRegistries.ITEM.getTag(tag).map(holders -> holders.size() == 0).orElse(true)) return false;
      }
    }

    boolean hasAnyMissingLists = !any_missing.isEmpty() || !any_missing_tags.isEmpty();
    if (hasAnyMissingLists) {
      boolean foundMissing = false;
      for(ResourceLocation rl : any_missing) {
        if(!BuiltInRegistries.ITEM.containsKey(rl)) {
          foundMissing = true;
          break;
        }
      }
      if (!foundMissing) {
        for(ResourceLocation rl : any_missing_tags) {
          TagKey<Item> tag = TagKey.create(Registries.ITEM, rl);
          if(BuiltInRegistries.ITEM.getTag(tag).map(holders -> holders.size() == 0).orElse(true)) {
            foundMissing = true;
            break;
          }
        }
      }
      if (!foundMissing) return false;
    }

    return true;
  }
}
