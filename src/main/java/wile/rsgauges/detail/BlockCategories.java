/*
 * @file BlockCategories.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2018 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 */
package wile.rsgauges.detail;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import wile.rsgauges.ModRsGauges;
import wile.rsgauges.libmc.detail.Auxiliaries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class BlockCategories
{
  private static final String MODID = ModRsGauges.MODID;

  public final static void update()
  {}

  public final static Matcher getMatcher(String name)
  { return matchers_.getOrDefault(name, filter_none); }

  public final static List<String> getMatcherNames()
  { return matcher_names_; }

  public interface Matcher { boolean match(Level world, BlockPos pos); }
  private static final Matcher filter_none = (final Level w, final BlockPos p) -> false;
  private static final Map<String, Matcher> matchers_;
  private static final List<String> matcher_names_;

  static
  {
    matchers_ = new HashMap<String, Matcher>();

    matchers_.put("any", (final Level w, final BlockPos p) -> {
      return !w.getBlockState(p).isAir();
    });

    matchers_.put("solid", (final Level w, final BlockPos p) -> {
      // In 1.21.1 nutzen wir isSolidRender als Ersatz für die alte isSolid() Logik
      return w.getBlockState(p).isSolidRender(w, p);
    });

    matchers_.put("liquid", (final Level w, final BlockPos p) -> {
      BlockState st = w.getBlockState(p);
      return (st.liquid()) || (!w.getFluidState(p).isEmpty());
    });

    matchers_.put("air", (final Level w, final BlockPos p) -> {
      return w.getBlockState(p).isAir();
    });

    matchers_.put("plant", (final Level w, final BlockPos p) -> {
      BlockState st = w.getBlockState(p);
      Block b = st.getBlock();
      // IPlantable entfernt; wir nutzen stattdessen GrowingPlantBlock oder Vanilla Tags
      return (b instanceof GrowingPlantBlock) || st.is(BlockTags.FLOWERS) || st.is(BlockTags.REPLACEABLE_BY_TREES) || Auxiliaries.isInBlockTag(b, ResourceLocation.fromNamespaceAndPath(MODID, "plants"));
    });

    matchers_.put("material_wood", (final Level w, final BlockPos p) -> {
      return Auxiliaries.isInBlockTag(w.getBlockState(p).getBlock(), ResourceLocation.fromNamespaceAndPath(MODID, "wooden"));
    });

    matchers_.put("material_stone", (final Level w, final BlockPos p) -> {
      return Auxiliaries.isInBlockTag(w.getBlockState(p).getBlock(), ResourceLocation.fromNamespaceAndPath(MODID, "stone_like"));
    });

    matchers_.put("material_glass", (final Level w, final BlockPos p) -> {
      return Auxiliaries.isInBlockTag(w.getBlockState(p).getBlock(), ResourceLocation.fromNamespaceAndPath(MODID, "glass_like"));
    });

    matchers_.put("material_clay", (final Level w, final BlockPos p) -> {
      return Auxiliaries.isInBlockTag(w.getBlockState(p).getBlock(), ResourceLocation.fromNamespaceAndPath(MODID, "clay_like"));
    });

    matchers_.put("material_water", (final Level w, final BlockPos p) -> {
      BlockState st = w.getBlockState(p);
      if(Auxiliaries.isInBlockTag(st.getBlock() , ResourceLocation.fromNamespaceAndPath(MODID, "water_like"))) return true;
      return st.getFluidState().is(Fluids.WATER) || st.getFluidState().is(Fluids.FLOWING_WATER);
    });

    matchers_.put("ore", (final Level w, final BlockPos p) -> {
      return Auxiliaries.isInBlockTag(w.getBlockState(p).getBlock(), ResourceLocation.fromNamespaceAndPath(MODID, "ores"));
    });

    matchers_.put("woodlog", (final Level w, final BlockPos p) -> {
      return Auxiliaries.isInBlockTag(w.getBlockState(p).getBlock(), ResourceLocation.fromNamespaceAndPath(MODID, "logs"));
    });

    matchers_.put("crop", (final Level w, final BlockPos p) -> {
      Block b = w.getBlockState(p).getBlock();
      return (b instanceof CropBlock) || Auxiliaries.isInBlockTag(b, ResourceLocation.fromNamespaceAndPath(MODID, "crops"));
    });

    matchers_.put("crop_mature", (final Level w, final BlockPos p) -> {
      final BlockState s = w.getBlockState(p);
      final Block b = s.getBlock();
      return ((b instanceof CropBlock) && ((CropBlock)b).isMaxAge(s)) || (b== Blocks.MELON) || (b==Blocks.PUMPKIN);
    });

    matchers_.put("sapling", (final Level w, final BlockPos p) -> {
      return Auxiliaries.isInBlockTag(w.getBlockState(p).getBlock(), ResourceLocation.fromNamespaceAndPath(MODID, "saplings"));
    });

    matchers_.put("soil", (final Level w, final BlockPos p) -> {
      return Auxiliaries.isInBlockTag(w.getBlockState(p).getBlock(), ResourceLocation.fromNamespaceAndPath(MODID, "soils"));
    });

    matchers_.put("fertile", (final Level w, final BlockPos p) -> {
      // isFertile() ist kein Standard-Block-Event mehr. Wir prüfen auf Farmland.
      return w.getBlockState(p).is(Blocks.FARMLAND);
    });

    matchers_.put("planks", (final Level w, final BlockPos p) -> {
      return Auxiliaries.isInBlockTag(w.getBlockState(p).getBlock(), ResourceLocation.fromNamespaceAndPath(MODID, "planks"));
    });

    matchers_.put("slab", (final Level w, final BlockPos p) -> {
      return Auxiliaries.isInBlockTag(w.getBlockState(p).getBlock(), ResourceLocation.fromNamespaceAndPath(MODID, "slabs"));
    });

    matcher_names_ = new ArrayList<String>();
    matcher_names_.add("any");
    matcher_names_.add("solid");
    matcher_names_.add("liquid");
    matcher_names_.add("air");
    matcher_names_.add("plant");
    matcher_names_.add("material_wood");
    matcher_names_.add("material_stone");
    matcher_names_.add("material_glass");
    matcher_names_.add("material_clay");
    matcher_names_.add("material_water");
    matcher_names_.add("ore");
    matcher_names_.add("woodlog");
    matcher_names_.add("crop");
    matcher_names_.add("crop_mature");
    matcher_names_.add("sapling");
    matcher_names_.add("soil");
    matcher_names_.add("fertile");
    matcher_names_.add("planks");
    matcher_names_.add("slab");
    matchers_.forEach((k,v)->{ if(!matcher_names_.contains(k)) matcher_names_.add(k);});
  }
}