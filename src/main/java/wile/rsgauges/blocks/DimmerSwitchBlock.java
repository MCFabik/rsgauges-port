/*
 * @file DimmerSwitchBlock.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2018 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 */
package wile.rsgauges.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import wile.rsgauges.ModConfig;
import wile.rsgauges.detail.ModResources;
import wile.rsgauges.libmc.detail.Auxiliaries;
import wile.rsgauges.libmc.detail.Overlay;
import wile.rsgauges.libmc.detail.Registries;

import javax.annotation.Nullable;

public class DimmerSwitchBlock extends SwitchBlock
{
  public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 15);

  public DimmerSwitchBlock(long config, BlockBehaviour.Properties properties, AABB unrotatedBBUnpowered, @Nullable AABB unrotatedBBPowered, @Nullable ModResources.BlockSoundEvent powerOnSound, @Nullable ModResources.BlockSoundEvent powerOffSound)
  { super(config|0xff, properties, unrotatedBBUnpowered, unrotatedBBPowered, powerOnSound, powerOffSound); }

  @Override
  public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random)
  {}

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
  { super.createBlockStateDefinition(builder); builder.add(POWER); }

  private void handleTouchConfig(Level world, BlockPos pos, BlockState state, Player player, SwitchTileEntity te, ClickInteraction ck, boolean was_powered)
  {
    // KORREKTUR: Der physische Regler ist 14 Pixel lang (Y=1 bis Y=15).
    // Wir teilen diese 14 Pixel mathematisch exakt in 16 anklickbare Zonen (0-15) auf.
    int p = net.minecraft.util.Mth.clamp((int) Math.floor((ck.y - 1.0) * 16.0 / 14.0), 0, 15);

    if(p != te.setpower()) {
      te.setpower(p);
      p = te.setpower();
      Overlay.show(player,
              Auxiliaries.localizable("switchconfig.dimmerswitch.output_power", ChatFormatting.RED, new Object[]{p})
      );
      final int state_p = state.getValue(POWER);
      if(state_p!=p) {
        world.setBlock(pos, state.setValue(POWER, p).setValue(POWERED, p>0), 1|2|8|16);
        notifyNeighbours(world, pos, state, te, false);
        te.setChanged();
      }
      if(was_powered && (p==0)) power_off_sound.play(world, pos); else power_on_sound.play(world, pos);
      if((state_p!=p) && ((config & SWITCH_CONFIG_LINK_SOURCE_SUPPORT)!=0))  {
        if(!te.activateSwitchLinks(p, (p>0)?15:0, (state_p==0)!=(p==0))) {
          ModResources.BlockSoundEvents.SWITCHLINK_LINK_PEAL_USE_FAILED.play(world, pos);
        }
      }
    }
  }

  @Override
  protected ItemInteractionResult useItemOn(ItemStack stack_held, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
  {
    if((!(state.getBlock() instanceof DimmerSwitchBlock))) return ItemInteractionResult.FAIL;
    if(world.isClientSide()) return ItemInteractionResult.SUCCESS;
    SwitchTileEntity te = getTe(world, pos);
    if(te==null) return ItemInteractionResult.FAIL;
    te.click_config(null, false);
    ClickInteraction ck = ClickInteraction.get(state, world, pos, player, hand, hit);
    boolean was_powered = te.setpower()!=0;

    if(ck.touch_configured) {
      handleTouchConfig(world, pos, state, player, te, ck, was_powered);
      return ItemInteractionResult.CONSUME;
    } else if(ck.wrenched) {
      if(te.click_config(this, false)) {
        Overlay.show(player, te.configStatusTextComponentTranslation((SwitchBlock) state.getBlock()));
      }
      return ItemInteractionResult.CONSUME;
    } else if(!ModConfig.without_rightclick_item_switchconfig() &&
            ((ck.item==Items.REDSTONE && (((SwitchBlock)state.getBlock()).config & SWITCH_CONFIG_PULSETIME_CONFIGURABLE) != 0)
                    || ck.item==Items.ENDER_PEARL || ck.item==Registries.getItem("switchlink_pearl"))) {
      attack(state, world, pos, player);
      return ItemInteractionResult.CONSUME;
    }
    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
  }

  @Override
  protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit)
  {
    if((!(state.getBlock() instanceof DimmerSwitchBlock))) return InteractionResult.FAIL;
    if(world.isClientSide()) return InteractionResult.SUCCESS;
    SwitchTileEntity te = getTe(world, pos);
    if(te==null) return InteractionResult.FAIL;
    te.click_config(null, false);
    ClickInteraction ck = ClickInteraction.get(state, world, pos, player, InteractionHand.MAIN_HAND, hit);
    boolean was_powered = te.setpower()!=0;

    if(ck.touch_configured) {
      handleTouchConfig(world, pos, state, player, te, ck, was_powered);
      return InteractionResult.CONSUME;
    }
    return InteractionResult.PASS;
  }
}