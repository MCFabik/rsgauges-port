/*
 * @file ComparatorSwitchBlock.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2018 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 */
package wile.rsgauges.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import wile.rsgauges.ModConfig;
import wile.rsgauges.blocks.EnvironmentalSensorSwitchBlock.EnvironmentalSensorSwitchTileEntity;
import wile.rsgauges.detail.ModResources;
import wile.rsgauges.libmc.detail.Auxiliaries;
import wile.rsgauges.libmc.detail.Overlay;
import wile.rsgauges.libmc.detail.Registries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;

public class ComparatorSwitchBlock extends AutoSwitchBlock
{
  public ComparatorSwitchBlock(long config, BlockBehaviour.Properties properties, AABB unrotatedBBUnpowered, @Nullable AABB unrotatedBBPowered, @Nullable ModResources.BlockSoundEvent powerOnSound, @Nullable ModResources.BlockSoundEvent powerOffSound)
  { super(config, properties, unrotatedBBUnpowered, unrotatedBBPowered, powerOnSound, powerOffSound); }

  public ComparatorSwitchBlock(long config, BlockBehaviour.Properties properties, AABB unrotatedBBUnpowered, @Nullable AABB unrotatedBBPowered)
  { super(config, properties, unrotatedBBUnpowered, unrotatedBBPowered, null, null); }

  @Override
  public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
  {
    if(!isAffectedByNeigbour(state, world, pos, fromPos)) return;
    final SwitchTileEntity te = getTe(world, pos);
    if(!(te instanceof ComparatorSwitchTileEntity)) return;
    ((ComparatorSwitchTileEntity)te).block_updated();
  }

  @Override
  @Nullable
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
  { return new ComparatorSwitchTileEntity(pos, state); }

  @Override
  public Optional<Integer> switchLinkOutputPower(Level world, BlockPos pos)
  {
    BlockEntity te = world.getBlockEntity(pos);
    if((!(te instanceof ComparatorSwitchTileEntity))) return Optional.empty();
    return Optional.of(((ComparatorSwitchTileEntity)te).link_output_power());
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Tile entity
  // -------------------------------------------------------------------------------------------------------------------

  public static class ComparatorSwitchTileEntity extends EnvironmentalSensorSwitchTileEntity
  {
    private int link_output_power_ = 0;
    public int link_output_power() { return link_output_power_; }

    public ComparatorSwitchTileEntity(BlockPos pos, BlockState state)
    { super(Registries.getBlockEntityType("te_comparator_switch"), pos, state); }

    private interface Acquisition { int sample(Level world, BlockPos pos, BlockState state, Direction side); }

    private static final Acquisition acquisitions[] = {
            // 0: Comparator input override
            (world, pos, state, side) -> {
              return (!state.hasAnalogOutputSignal()) ? (-1) : (state.getAnalogOutputSignal(world, pos));
            },
            // 1: Used inventory slots
            (world, pos, state, side) -> {
              final BlockEntity te = world.getBlockEntity(pos);
              if(te == null) return -1;

              // NeoForge 1.21.1 Capability Lookup
              final IItemHandler handler = world.getCapability(Capabilities.ItemHandler.BLOCK, pos, state, te, side);
              if (handler != null) {
                final int size = handler.getSlots();
                if (size == 0) return 0;
                int n = 0;
                for (int i = 0; i < size; ++i) n += handler.getStackInSlot(i).isEmpty() ? 0 : 1;
                return (int) Math.round(((double) n * 15) / (double) size);
              } else if (te instanceof Container inventory) {
                final int size = inventory.getContainerSize();
                if (size == 0) return 0;
                int n = 0;
                for (int i = 0; i < size; ++i) n += inventory.getItem(i).isEmpty() ? 0 : 1;
                return (int) Math.round(((double) n * 15) / (double) size);
              }
              return -1;
            },
            // 2: Free inventory slots
            (world, pos, state, side) -> {
              final BlockEntity te = world.getBlockEntity(pos);
              if(te == null) return -1;

              final IItemHandler handler = world.getCapability(Capabilities.ItemHandler.BLOCK, pos, state, te, side);
              if (handler != null) {
                final int size = handler.getSlots();
                if (size == 0) return 0;
                int n = 0;
                for (int i = 0; i < size; ++i) n += handler.getStackInSlot(i).isEmpty() ? 1 : 0;
                return (int) Math.round(((double) n * 15) / (double) size);
              } else if (te instanceof Container inventory) {
                final int size = inventory.getContainerSize();
                if (size == 0) return 0;
                int n = 0;
                for (int i = 0; i < size; ++i) n += inventory.getItem(i).isEmpty() ? 1 : 0;
                return (int) Math.round(((double) n * 15) / (double) size);
              }
              return -1;
            },
            // 3: Redstone signal
            (world, pos, state, side) -> world.getSignal(pos, side)
    };

    private int acquisition_mode() { return debounce(); }
    private void acquisition_mode(int mode) { debounce(Mth.clamp(mode, 0, acquisitions.length-1)); }
    public void block_updated() { if(update_timer_ > 1) update_timer_ = 1; }

    @Override
    public void reset(LevelReader world) {
      super.reset(world);
      setpower(15);
      threshold0_on(1);
      threshold0_off(0);
      debounce(0);
    }

    @Override
    public boolean activation_config(BlockState state, @Nullable Player player, double x, double y, boolean show_only) {
      if(state == null) return false;
      final int direction = (y >= 9) ? (1) : ((y <= 6) ? (-1) : (0));
      final int field = ((x>=2) && (x<=3.95)) ? (1) : (
              ((x>=4.25) && (x<=7)) ? (2) : (
                      ((x>=8) && (x<=10)) ? (3) : (
                              ((x>=11) && (x<=13)) ? (4) : (0)
                      )));
      if((direction==0) || (field==0)) return false;
      if(!show_only) {
        switch (field) {
          case 1 -> {
            double v = threshold0_on() + (direction);
            threshold0_on(Mth.clamp(v, 1, 15));
            if (threshold0_on() < threshold0_off()) threshold0_off(threshold0_on());
          }
          case 2 -> {
            double v = threshold0_off() + (direction);
            threshold0_off(Mth.clamp(v, 0, 14));
            if (threshold0_off() > threshold0_on()) threshold0_on(threshold0_off());
          }
          case 3 -> acquisition_mode(acquisition_mode() + direction);
          case 4 -> setpower(setpower() + direction);
        }
        if(setpower() < 1) setpower(1);
        setChanged();
      }
      {
        MutableComponent separator = (Component.literal(" | ")); separator.withStyle(ChatFormatting.GRAY);
        ArrayList<Object> tr = new ArrayList<>();
        tr.add(Auxiliaries.localizable("switchconfig.comparator_switch.threshold_on", ChatFormatting.BLUE, new Object[]{(int)threshold0_on()}));
        tr.add(separator.copy().append(Auxiliaries.localizable("switchconfig.comparator_switch.threshold_off", ChatFormatting.YELLOW, new Object[]{(int)threshold0_off()})));
        tr.add(separator.copy().append(Auxiliaries.localizable("switchconfig.comparator_switch.output_power", ChatFormatting.RED, new Object[]{(int) setpower()})));
        tr.add(separator.copy().append(Auxiliaries.localizable("switchconfig.comparator_switch.mode"+((int)acquisition_mode()), ChatFormatting.DARK_GREEN, new Object[]{})));
        Overlay.show(player, Auxiliaries.localizable("switchconfig.comparator_switch", ChatFormatting.RESET, tr.toArray()));
      }
      return true;
    }

    @Override
    public int power(BlockState state, boolean strong)
    { return (acquisition_mode()==3) ? (0) : (super.power(state, strong)); }

    @Override
    public void tick() {
      if(level == null || level.isClientSide() || (--update_timer_ > 0)) return;
      // FIX: Nutzt nun die statische Getter-Methode aus ModConfig
      update_timer_ = Mth.clamp(ModConfig.comparator_switch_update_interval(), 1, 10);
      BlockState state = getBlockState();
      if(!(state.getBlock() instanceof ComparatorSwitchBlock)) return;

      final boolean last_active = state.getValue(POWERED);
      final Direction facing = state.getValue(FACING);
      final BlockPos adjacent_pos = worldPosition.relative(facing.getOpposite());
      final BlockState adjacent_state = level.getBlockState(adjacent_pos);

      final int value = Mth.clamp(acquisitions[acquisition_mode()].sample(level, adjacent_pos, adjacent_state, facing), -1, 15);
      final int last_link_output_power = link_output_power_;
      boolean active = last_active;

      if(value < 0) {
        active = false;
        update_timer_ = 20;
        link_output_power_= 0;
      } else {
        link_output_power_ = value;
        int measurement = 0;
        if(threshold0_off() >= threshold0_on()) {
          measurement += (value==threshold0_on()) ? 1 : -1;
        } else {
          if(value >= threshold0_on()) measurement = 1;
          if(value <= threshold0_off()) measurement = -1;
        }
        debounce_counter_ = Mth.clamp(debounce_counter_ + measurement, 0, 2);
        active = (debounce_counter_ >= 2);
      }

      updateSwitchState(state, (ComparatorSwitchBlock)(state.getBlock()), active, 0, false);
      if((link_output_power_ != last_link_output_power) || (last_active != active)) {
        if(!activateSwitchLinks(link_output_power_, active?15:0, (last_active != active))) {
          ModResources.BlockSoundEvents.SWITCHLINK_LINK_PEAL_USE_FAILED.play(level, worldPosition);
        }
      }
    }
  }
}