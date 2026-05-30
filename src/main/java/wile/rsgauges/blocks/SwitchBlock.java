/*
 * @file SwitchBlock.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2018 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 */
package wile.rsgauges.blocks;

import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import wile.rsgauges.ModConfig;
import wile.rsgauges.detail.ModResources;
import wile.rsgauges.detail.SwitchLink;
import wile.rsgauges.detail.SwitchLink.LinkMode;
import wile.rsgauges.detail.SwitchLink.RequestResult;
import wile.rsgauges.items.SwitchLinkPearlItem;
import wile.rsgauges.libmc.detail.Auxiliaries;
import wile.rsgauges.libmc.detail.Overlay;
import wile.rsgauges.libmc.detail.Registries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;

public class SwitchBlock extends RsDirectedBlock implements SwitchLink.ISwitchLinkable
{
  public static final long SWITCH_DATA_POWERED_POWER_MASK       = 0x000000000000000fl;
  public static final long SWITCH_DATA_INVERTED                 = 0x0000000000000100l;
  public static final long SWITCH_DATA_WEAK                     = 0x0000000000000200l;
  public static final long SWITCH_DATA_NOOUTPUT                 = 0x0000000000000400l;
  public static final long SWITCH_DATA_SIDE_ENABLED_BOTTOM      = 0x0000000000001000l;
  public static final long SWITCH_DATA_SIDE_ENABLED_TOP         = 0x0000000000002000l;
  public static final long SWITCH_DATA_SIDE_ENABLED_FRONT       = 0x0000000000004000l;
  public static final long SWITCH_DATA_SIDE_ENABLED_BACK        = 0x0000000000008000l;
  public static final long SWITCH_DATA_SIDE_ENABLED_LEFT        = 0x0000000000010000l;
  public static final long SWITCH_DATA_SIDE_ENABLED_RIGHT       = 0x0000000000020000l;
  public static final long SWITCH_DATA_SIDE_ENABLED_ALL         = 0x000000000003f000l;
  public static final long SWITCH_DATA_SIDE_ENABLED_MASK        = SWITCH_DATA_SIDE_ENABLED_ALL;
  public static final long SWITCH_DATA_SIDE_ENABLED_SHIFT       = 12;
  public static final long SWITCH_DATA_ENTITY_DEFAULTS_MASK     = 0x000000000003ffffl;
  public static final long SWITCH_CONFIG_INVERTABLE             = 0x0000000000100000l;
  public static final long SWITCH_CONFIG_WEAKABLE               = 0x0000000000200000l;
  public static final long SWITCH_CONFIG_PULSETIME_CONFIGURABLE = 0x0000000000400000l;
  public static final long SWITCH_CONFIG_TOUCH_CONFIGURABLE     = 0x0000000000800000l;
  public static final long SWITCH_CONFIG_PULSE_EXTENDABLE       = 0x0000000001000000l;
  public static final long SWITCH_CONFIG_LCLICK_RESETTABLE      = 0x0000000002000000l;
  public static final long SWITCH_CONFIG_BISTABLE               = 0x0000000010000000l;
  public static final long SWITCH_CONFIG_PULSE                  = 0x0000000020000000l;
  public static final long SWITCH_CONFIG_CONTACT                = 0x0000000040000000l;
  public static final long SWITCH_CONFIG_TIMER_DAYTIME          = 0x0000000100000000l;
  public static final long SWITCH_CONFIG_TIMER_INTERVAL         = 0x0000000200000000l;
  public static final long SWITCH_CONFIG_SENSOR_VOLUME          = 0x0000000400000000l;
  public static final long SWITCH_CONFIG_SENSOR_LINEAR          = 0x0000000800000000l;
  public static final long SWITCH_CONFIG_SENSOR_LIGHT           = 0x0000001000000000l;
  public static final long SWITCH_CONFIG_SENSOR_RAIN            = 0x0000002000000000l;
  public static final long SWITCH_CONFIG_SENSOR_LIGHTNING       = 0x0000004000000000l;
  public static final long SWITCH_CONFIG_SENSOR_BLOCKDETECT     = 0x0000008000000000l|SWITCH_CONFIG_TOUCH_CONFIGURABLE;
  public static final long SWITCH_CONFIG_SENSOR_DETECTOR        = SWITCH_CONFIG_SENSOR_VOLUME|SWITCH_CONFIG_SENSOR_LINEAR|SWITCH_CONFIG_TOUCH_CONFIGURABLE;
  public static final long SWITCH_CONFIG_SENSOR_ENVIRONMENTAL   = SWITCH_CONFIG_SENSOR_LIGHT|SWITCH_CONFIG_TIMER_DAYTIME|SWITCH_CONFIG_SENSOR_RAIN|SWITCH_CONFIG_SENSOR_LIGHTNING|SWITCH_CONFIG_TOUCH_CONFIGURABLE;
  public static final long SWITCH_CONFIG_AUTOMATIC              = SWITCH_CONFIG_TIMER_INTERVAL|SWITCH_CONFIG_SENSOR_DETECTOR|SWITCH_CONFIG_SENSOR_ENVIRONMENTAL|SWITCH_CONFIG_SENSOR_BLOCKDETECT|SWITCH_CONFIG_TOUCH_CONFIGURABLE;
  public static final long SWITCH_CONFIG_PROJECTILE_SENSE_ON    = 0x0000100000000000l;
  public static final long SWITCH_CONFIG_PROJECTILE_SENSE_OFF   = 0x0000200000000000l;
  public static final long SWITCH_CONFIG_PROJECTILE_SENSE       = SWITCH_CONFIG_PROJECTILE_SENSE_ON|SWITCH_CONFIG_PROJECTILE_SENSE_OFF;
  public static final long SWITCH_CONFIG_SHOCK_SENSITIVE        = 0x0000400000000000l;
  public static final long SWITCH_CONFIG_HIGH_SENSITIVE         = 0x0000800000000000l;
  public static final long SWITCH_CONFIG_TRANSLUCENT            = RSBLOCK_CONFIG_TRANSLUCENT;
  public static final long SWITCH_CONFIG_NOT_PASSABLE           = 0x0010000000000000l;
  public static final long SWITCH_CONFIG_SIDES_CONFIGURABLE     = 0x0040000000000000l;
  public static final long SWITCH_CONFIG_LINK_SOURCE_SUPPORT    = 0x0100000000000000l;
  public static final long SWITCH_CONFIG_LINK_TARGET_SUPPORT    = 0x0200000000000000l;
  public static final long SWITCH_CONFIG_LINK_SENDER            = 0x0400000000000000l;
  public static final long SWITCH_CONFIG_WALLMOUNT              = RSBLOCK_CONFIG_WALLMOUNT;
  public static final long SWITCH_CONFIG_LATERAL                = RSBLOCK_CONFIG_LATERAL;
  public static final long SWITCH_CONFIG_LATERAL_WALLMOUNT      = SWITCH_CONFIG_WALLMOUNT|SWITCH_CONFIG_LATERAL;

  public static final BooleanProperty POWERED = BooleanProperty.create("powered");
  public static final int base_tick_rate = 2;
  public static final int default_pulse_on_time = 20;
  public final long config;
  protected final ModResources.BlockSoundEvent power_on_sound;
  protected final ModResources.BlockSoundEvent power_off_sound;

  public SwitchBlock(long config, BlockBehaviour.Properties properties, AABB unrotatedBBUnpowered, @Nullable AABB unrotatedBBPowered, @Nullable ModResources.BlockSoundEvent powerOnSound, @Nullable ModResources.BlockSoundEvent powerOffSound)
  {
    super(config, properties, unrotatedBBUnpowered, unrotatedBBPowered);
    if((powerOnSound==null) && (powerOffSound==null)) {
      powerOnSound  = ModResources.BlockSoundEvents.DEFAULT_SWITCH_ACTIVATION;
      powerOffSound = ModResources.BlockSoundEvents.DEFAULT_SWITCH_DEACTIVATION;
    } else {
      if(powerOnSound==null) powerOnSound =  ModResources.BlockSoundEvents.DEFAULT_SWITCH_MUTE;
      if(powerOffSound==null) powerOffSound = ModResources.BlockSoundEvents.DEFAULT_SWITCH_MUTE;
    }
    power_on_sound = powerOnSound;
    power_off_sound = powerOffSound;
    registerDefaultState(super.defaultBlockState().setValue(POWERED, false));
    if((config & 0xff)==0xff) config &= ~0xffL;
    else if((config & 0xff)==0) config |= 15;
    if((config & SWITCH_DATA_SIDE_ENABLED_MASK)!=0) config |= SWITCH_CONFIG_SIDES_CONFIGURABLE;
    this.config = config;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
  { super.createBlockStateDefinition(builder); builder.add(POWERED); }

  @Override
  public RenderTypeHint getRenderTypeHint()
  { return ((config & SWITCH_CONFIG_TRANSLUCENT) != 0) ? (RenderTypeHint.TRANSLUCENT) : (RenderTypeHint.CUTOUT); }

  // Form und Box wechseln dynamisch zwischen [0] (aus) und [1] (an).
  @Override
  public VoxelShape getShape(BlockState state, BlockGetter source, BlockPos pos, CollisionContext selectionContext)
  { return shapes_[state.getValue(FACING).get3DDataValue()][state.getValue(POWERED) ? 1 : 0]; }

  @Override
  public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext selectionContext)
  { return ((config & SWITCH_CONFIG_NOT_PASSABLE)==0) ? (Shapes.empty()) : (getShape(state, world, pos, selectionContext)); }

  @Override
  public boolean isSignalSource(BlockState state)
  { return ((config &SWITCH_CONFIG_LINK_SENDER)==0); }

  @Override
  public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side)
  { return (!isWallMount()) && ((side==null) || ((side)==(Direction.UP)) || ((side)==(state.getValue(FACING)))); }

  @Override
  public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side)
  { return getPower(state, world, pos, side, false); }

  @Override
  public int getDirectSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side)
  { return getPower(state, world, pos, side, true); }

  @Override
  public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
  {
    final SwitchTileEntity te = getTe(world, pos);
    if(te != null) te.reset(world);
    world.setBlock(pos, world.getBlockState(pos).setValue(POWERED, false), 1|2|8|16);
    notifyNeighbours(world, pos, state,  te, true);
  }

  @Override
  public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving)
  {
    if(!newState.is(this)) {
      final SwitchTileEntity te = getTe(world, pos);
      if(te!=null) {
        te.unlinkAllSwitchLinks(true);
        te.nooutput(true);
        notifyNeighbours(world, pos, state,  te, true);
      }
    }
    super.onRemove(state, world, pos, newState, isMoving);
  }

  @Override
  public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity)
  {
    if(world.isClientSide() || ((config & SWITCH_CONFIG_PROJECTILE_SENSE)==0) || (!(entity instanceof Projectile))) return;
    if(state.getValue(POWERED)) {
      if((config & SWITCH_CONFIG_PROJECTILE_SENSE_OFF)==0) return;
    } else {
      if((config & SWITCH_CONFIG_PROJECTILE_SENSE_ON)==0) return;
    }
    onSwitchActivated(world, pos, state, null, null);
  }

  @Override
  protected ItemInteractionResult useItemOn(ItemStack stack_held, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
  {
    SwitchTileEntity te = getTe(world, pos);
    if(te==null) return ItemInteractionResult.FAIL;
    if(world.isClientSide()) return ItemInteractionResult.SUCCESS;
    te.click_config(null, false);
    ClickInteraction ck = ClickInteraction.get(state, world, pos, player, hand, hit);
    if((ck.touch_configured) && te.touch_config(state, player, ck.x, ck.y)) {
      ModResources.BlockSoundEvents.DEFAULT_SWITCH_CONFIGCLICK.play(world, pos);
      return ItemInteractionResult.CONSUME;
    }
    if(ck.wrenched && te.click_config(this, false)) {
      ModResources.BlockSoundEvents.DEFAULT_SWITCH_CONFIGCLICK.play(world, pos);
      Overlay.show(player, te.configStatusTextComponentTranslation((SwitchBlock) state.getBlock()));
      return ItemInteractionResult.CONSUME;
    }
    if(!ModConfig.without_rightclick_item_switchconfig() &&
            ((ck.item==Items.REDSTONE && (((SwitchBlock)state.getBlock()).config & SWITCH_CONFIG_PULSETIME_CONFIGURABLE) != 0)
                    || ck.item==Items.ENDER_PEARL || ck.item==Registries.getItem("switchlink_pearl"))) {
      attack(state, world, pos, player);
      return ItemInteractionResult.CONSUME;
    }
    if((config & (SWITCH_CONFIG_BISTABLE|SWITCH_CONFIG_PULSE))==0) {
      return ItemInteractionResult.CONSUME;
    } else {
      return onSwitchActivated(world, pos, state, player, hit.getDirection()) ? ItemInteractionResult.CONSUME : ItemInteractionResult.FAIL;
    }
  }

  @Override
  protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit)
  {
    SwitchTileEntity te = getTe(world, pos);
    if(te==null) return InteractionResult.FAIL;
    if(world.isClientSide()) return InteractionResult.SUCCESS;
    te.click_config(null, false);
    ClickInteraction ck = ClickInteraction.get(state, world, pos, player, InteractionHand.MAIN_HAND, hit);
    if((ck.touch_configured) && te.touch_config(state, player, ck.x, ck.y)) {
      ModResources.BlockSoundEvents.DEFAULT_SWITCH_CONFIGCLICK.play(world, pos);
      return InteractionResult.CONSUME;
    }
    if((config & (SWITCH_CONFIG_BISTABLE|SWITCH_CONFIG_PULSE))==0) {
      return InteractionResult.CONSUME;
    } else {
      return onSwitchActivated(world, pos, state, player, hit.getDirection()) ? InteractionResult.CONSUME : InteractionResult.FAIL;
    }
  }

  @Override
  protected void attack(BlockState state, Level world, BlockPos pos, Player player)
  {
    if(world.isClientSide()) return;
    final SwitchTileEntity te = getTe(world, pos);
    if(te == null) return;
    final Item item_held = player.getInventory().getSelected().getItem();
    ClickInteraction ck = ClickInteraction.get(state, world, pos, player);
    if(ck.wrenched) {
      if(te.click_config(this, false)) {
        Overlay.show(player, te.configStatusTextComponentTranslation((SwitchBlock) state.getBlock()));
        return;
      }
    } else if((ck.item==Items.REDSTONE) && (((SwitchBlock)state.getBlock()).config & SWITCH_CONFIG_PULSETIME_CONFIGURABLE) != 0) {
      if(!ModConfig.without_pulsetime_config()) {
        te.configured_on_time(ck.item_count * 2);
        Overlay.show(player, te.configStatusTextComponentTranslation((SwitchBlock)state.getBlock()));
        return;
      }
    } else if(ck.item==Items.ENDER_PEARL) {
      if(!ModConfig.without_switch_linking()) {
        final SwitchBlock block = (SwitchBlock)state.getBlock();
        if((block.config & SWITCH_CONFIG_LINK_TARGET_SUPPORT) == 0) {
          Overlay.show(player, Auxiliaries.localizable("switchlinking.target_assign.error_notarget"));
          ModResources.BlockSoundEvents.SWITCHLINK_CANNOT_LINK_THAT.play(world, pos);
        } else {
          ItemStack link_stack = SwitchLinkPearlItem.createFromPearl(world, pos, player);
          if(!link_stack.isEmpty()) {
            player.getInventory().setItem(player.getInventory().selected, link_stack);
            Overlay.show(player, Auxiliaries.localizable("switchlinking.target_assign.ok"));
            ModResources.BlockSoundEvents.SWITCHLINK_LINK_TARGET_SELECTED.play(world, pos);
          } else {
            ModResources.BlockSoundEvents.SWITCHLINK_CANNOT_LINK_THAT.play(world, pos);
          }
        }
      }
    } else if(ck.item==Registries.getItem("switchlink_pearl") && item_held==ck.item) {
      if(!ModConfig.without_switch_linking()) {
        switch (te.assignSwitchLink(world, pos, player.getInventory().getSelected())) {
          case OK -> {
            player.getInventory().getSelected().shrink(1);
            ModResources.BlockSoundEvents.SWITCHLINK_LINK_SOURCE_SELECTED.play(world, pos);
            Overlay.show(player, Auxiliaries.localizable("switchlinking.source_assign.ok"));
            return;
          }
          case E_SELF_ASSIGN -> {
            SwitchLinkPearlItem.cycleLinkMode(player.getInventory().getSelected(), world, pos, true);
            Overlay.show(player, Auxiliaries.localizable("switchlinking.relayconfig.confval" + Integer.toString(SwitchLink.fromItemStack(player.getInventory().getSelected()).mode().index())));
            return;
          }
          case E_NOSOURCE -> Overlay.show(player, Auxiliaries.localizable("switchlinking.source_assign.error_nosource"));
          case E_ALREADY_LINKED -> Overlay.show(player, Auxiliaries.localizable("switchlinking.source_assign.error_alreadylinked"));
          case E_TOO_FAR -> Overlay.show(player, Auxiliaries.localizable("switchlinking.source_assign.error_toofaraway"));
          case E_FAILED -> Overlay.show(player, Auxiliaries.localizable("switchlinking.source_assign.error_failed"));
        }
        ModResources.BlockSoundEvents.SWITCHLINK_LINK_SOURCE_FAILED.play(world, pos);
      }
      return;
    }
    if((item_held==Items.AIR) && te.click_config(this, true)) {
      ModResources.BlockSoundEvents.DEFAULT_SWITCH_CONFIGCLICK.play(world, pos);
      Overlay.show(player, te.configStatusTextComponentTranslation((SwitchBlock)state.getBlock()));
      return;
    }
    if((config & (SWITCH_CONFIG_LCLICK_RESETTABLE))!=0) {
      te.on_timer_reset();
      if(!state.getValue(POWERED)) return;
      world.setBlock(pos, state.setValue(POWERED, false), 1|2|8|16);
      power_off_sound.play(world, pos);
      if (!ModConfig.without_sculk_triggering() && (power_off_sound.volume() >= 0.1f)) {
        world.gameEvent(player, GameEvent.BLOCK_DEACTIVATE, pos);
      }
    }
    if(((config &SWITCH_CONFIG_LINK_SENDER)==0) && (!te.nooutput())) {
      notifyNeighbours(world, pos, state, te, false);
    }
  }

  @Override
  public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random)
  {
    if(!state.getValue(POWERED)) return;
    final SwitchTileEntity te = getTe(world, pos);
    if((te!=null) && (te.on_time_remaining() > 0)) { te.reschedule_block_tick(); return; }
    world.setBlock(pos, (state=state.setValue(POWERED, false)), 1|2|8|16);
    power_off_sound.play(world, pos);
    if (!ModConfig.without_sculk_triggering() && (power_off_sound.volume() >= 0.1f)) {
      world.gameEvent(GameEvent.BLOCK_DEACTIVATE, pos, GameEvent.Context.of(state));
    }
    if((config &SWITCH_CONFIG_LINK_SENDER)==0) notifyNeighbours(world, pos, state, te, false);
    if((config & SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT)!=0) {
      if(!te.activateSwitchLinks(0, 0, true)) {
        ModResources.BlockSoundEvents.SWITCHLINK_LINK_PEAL_USE_FAILED.play(world, pos);
      }
    }
  }

  @Override
  @Nullable
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
  { return new SwitchTileEntity(pos, state); }

  // HIER IST DIE KORRIGIERTE ZEILE MIT "BlockPos pos"
  public SwitchTileEntity getTe(LevelReader world, BlockPos pos)
  {
    final BlockEntity te = world.getBlockEntity(pos);
    return (te instanceof SwitchTileEntity) ? ((SwitchTileEntity)te) : (null);
  }

  protected void notifyNeighbours(Level world, BlockPos pos, BlockState state, @Nullable SwitchTileEntity te, boolean force)
  {
    if((!force) && (te!=null) && (te.nooutput())) return;
    if(net.neoforged.neoforge.event.EventHooks.onNeighborNotify(world, pos, state, java.util.EnumSet.allOf(Direction.class), false).isCanceled()) return;
    if((config & SWITCH_CONFIG_SIDES_CONFIGURABLE)!=0) {
      if((te==null)) return;
      final long disabled_sides = te.enabled_sides();
      for(Direction facing: Direction.values()) {
        if((disabled_sides & ((1l<<getAbsoluteFacing(state, facing).get3DDataValue()) << SWITCH_DATA_SIDE_ENABLED_SHIFT)) == 0) continue;
        world.neighborChanged(pos.relative(facing), this, pos);
        if(force || !te.weak()) world.updateNeighborsAtExceptFromFacing(pos.relative(facing), this, facing.getOpposite());
      }
    } else if((!isLateral()) && (isWallMount())) {
      final Direction wall = state.getValue(FACING).getOpposite();
      world.neighborChanged(pos.relative(wall), this, pos);
      if(force || !te.weak()) world.updateNeighborsAtExceptFromFacing(pos.relative(wall), this, wall.getOpposite());
    } else if((isLateral()) && (!isWallMount())) {
      final Direction wall = state.getValue(FACING);
      world.neighborChanged(pos.relative(wall), this, pos);
      world.neighborChanged(pos.below(), this, pos);
      if(force || !te.weak()) {
        world.updateNeighborsAtExceptFromFacing(pos.relative(wall), this, wall.getOpposite());
        world.updateNeighborsAtExceptFromFacing(pos.below(), this, Direction.UP);
      }
    } else {
      world.updateNeighborsAt(pos, this);
      if(force || !te.weak()) {
        for(Direction d: Direction.values()) world.updateNeighborsAtExceptFromFacing(pos.relative(d), this, d.getOpposite());
      }
    }
  }

  protected int getPower(BlockState state, BlockGetter world, BlockPos pos, Direction side, boolean strong)
  {
    if(((config &SWITCH_CONFIG_LINK_SENDER)!=0) || (!(world instanceof Level))) return 0;
    final SwitchTileEntity te = getTe((Level)world, pos);
    if(te==null) return 0;
    if((config & SWITCH_CONFIG_CONTACT)!=0) {
      if(!((side == (state.getValue(FACING).getOpposite())) || ((side == Direction.UP) && (!isWallMount())))) return 0;
      return te.power(state, strong);
    } else if((config & SWITCH_CONFIG_SIDES_CONFIGURABLE)==0) {
      boolean is_main_direction = (side == ((isLateral()) ? ((state.getValue(FACING)).getOpposite()) : (state.getValue(FACING))));
      if(!is_main_direction && (strong || te.weak())) return 0;
      return te.power(state, strong);
    } else {
      return te.power(state, strong);
    }
  }

  protected boolean onSwitchActivated(Level world, BlockPos pos, BlockState state, @Nullable Player player, @Nullable Direction facing)
  {
    if(world.isClientSide()) return true;
    final SwitchTileEntity te = getTe(world, pos);
    if(te == null) return true;
    boolean was_powered = state.getValue(POWERED);
    if((config & (SWITCH_CONFIG_BISTABLE|SWITCH_CONFIG_SENSOR_BLOCKDETECT))!=0) {
      world.setBlock(pos, (state=state.cycle(POWERED)), 1|2|8|16);
      if (was_powered) {
        power_off_sound.play(world, pos);
        if (!ModConfig.without_sculk_triggering() && (power_off_sound.volume() >= 0.1f)) {
          world.gameEvent(player, GameEvent.BLOCK_DEACTIVATE, pos);
        }
      } else {
        power_on_sound.play(world, pos);
        if (!ModConfig.without_sculk_triggering() && (power_on_sound.volume() >= 0.1f)) {
          world.gameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);
        }
      }
    } else {
      world.setBlock(pos, state=state.setValue(POWERED, true), 1|2|8|16);
      power_on_sound.play(world, pos);
      if (!ModConfig.without_sculk_triggering() && (power_on_sound.volume() >= 0.1f)) {
        world.gameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);
      }
    }
    if((config & SWITCH_CONFIG_LINK_SENDER)==0) notifyNeighbours(world, pos, state, te, false);
    if((config & SWITCH_CONFIG_PULSE)!=0) {
      if((config & SWITCH_CONFIG_PULSE_EXTENDABLE)==0) te.on_timer_reset();
      te.on_timer_extend();
      te.reschedule_block_tick();
    }
    if(((config & (SWITCH_CONFIG_LINK_SOURCE_SUPPORT))!=0) && ((config & (SWITCH_CONFIG_BISTABLE|SWITCH_CONFIG_PULSE|SWITCH_CONFIG_SENSOR_BLOCKDETECT))!=0)) {
      final boolean powered = state.getValue(POWERED);
      if(!was_powered || (config & SWITCH_CONFIG_PULSE) == 0) {
        if(!te.activateSwitchLinks(powered?te.setpower():0, powered?15:0, was_powered!=powered)) {
          ModResources.BlockSoundEvents.SWITCHLINK_LINK_PEAL_USE_FAILED.play(world, pos);
        }
      }
    }
    return true;
  }

  @Override
  public boolean switchLinkHasTargetSupport(Level world, BlockPos pos) { return (config & SWITCH_CONFIG_LINK_TARGET_SUPPORT)!=0; }
  @Override
  public boolean switchLinkHasSourceSupport(Level world, BlockPos pos) { return (config & SWITCH_CONFIG_LINK_SOURCE_SUPPORT)!=0; }
  @Override
  public boolean switchLinkHasAnalogSupport(Level world, BlockPos pos) { return false; }

  @Override
  public ImmutableList<SwitchLink.LinkMode> switchLinkGetSupportedTargetModes()
  {
    if((config & SWITCH_CONFIG_PULSE)!=0) return ImmutableList.of(SwitchLink.LinkMode.ACTIVATE, SwitchLink.LinkMode.DEACTIVATE, SwitchLink.LinkMode.TOGGLE);
    return ImmutableList.of(SwitchLink.LinkMode.AS_STATE, SwitchLink.LinkMode.ACTIVATE, SwitchLink.LinkMode.DEACTIVATE, SwitchLink.LinkMode.TOGGLE, SwitchLink.LinkMode.INV_STATE);
  }

  @Override
  public Optional<Integer> switchLinkOutputPower(Level world, BlockPos pos)
  {
    SwitchTileEntity te = getTe(world, pos);
    return (te==null) ? Optional.empty() : Optional.of(te.power(world.getBlockState(pos), false));
  }

  @Override
  public Optional<Integer> switchLinkInputPower(Level world, BlockPos pos)
  {
    BlockState state = world.getBlockState(pos);
    if(!(state.getBlock() instanceof SwitchBlock)) return Optional.empty();
    if(((SwitchBlock)(state.getBlock())).isCube()) return Optional.of(world.getBestNeighborSignal(pos));
    Direction facing = state.getValue(FACING);
    return Optional.of(world.getSignal(pos.relative(facing), facing.getOpposite()));
  }

  @Override
  public Optional<Integer> switchLinkComparatorInput(Level world, BlockPos pos)
  {
    BlockState state = world.getBlockState(pos);
    if(!(state.getBlock() instanceof SwitchBlock)) return Optional.empty();
    BlockPos adjacent_pos = pos.relative(state.getValue(FACING));
    BlockState adjacent = world.getBlockState(adjacent_pos);
    return adjacent.hasAnalogOutputSignal() ? Optional.of(adjacent.getAnalogOutputSignal(world, adjacent_pos)) : Optional.empty();
  }

  @Override
  public SwitchLink.RequestResult switchLinkTrigger(SwitchLink link)
  {
    if((config & (SWITCH_CONFIG_BISTABLE|SWITCH_CONFIG_PULSE))==0) return SwitchLink.RequestResult.REJECTED;
    SwitchTileEntity te = getTe(link.world, link.target_position);
    if((te==null) || (!te.verifySwitchLinkTarget(link))) return RequestResult.TARGET_GONE;
    return onSwitchActivated(link.world, link.target_position, link.world.getBlockState(link.target_position), link.player, null) ? RequestResult.OK : RequestResult.REJECTED;
  }

  @Override
  public void switchLinkInit(SwitchLink link)
  {
    if((link.mode()!=LinkMode.AS_STATE) && (link.mode()!=LinkMode.INV_STATE)) return;
    BlockState state = link.world.getBlockState(link.target_position);
    if((link.source_digital_power==0) == (state.getValue(POWERED))) onSwitchActivated(link.world, link.target_position, state, link.player, null);
  }

  @Override
  public void switchLinkUnlink(SwitchLink link) {}

  public static class SwitchTileEntity extends RsTileEntity
  {
    public static final int SWITCH_DATA_SVD_ACTIVE_TIME_MASK = 0x000000ff;

    protected static final int max_pulse_time = 200;
    protected long pulse_off_deadline_ = 0;
    protected int scd_ = 0;
    protected int svd_ = 0;
    protected long click_config_time_lastclicked_ = 0;
    protected long click_config_last_cycled_ = 0;
    protected long touch_config_time_lastclicked_ = 0;
    protected long last_link_request_ = 0;
    protected ArrayList<SwitchLink> links_ = null;

    public SwitchTileEntity(BlockEntityType<?> te_type, BlockPos pos, BlockState state) { super(te_type, pos, state); }
    public SwitchTileEntity(BlockPos pos, BlockState state) { super(Registries.getBlockEntityType("te_switch"), pos, state); }

    @Override
    public void write(CompoundTag nbt, boolean updatePacket)
    {
      nbt.putInt("scd", scd_);
      nbt.putInt("svd", svd_);
      if(updatePacket) return;
      if(links_!=null && !links_.isEmpty()) {
        ListTag tl = new ListTag();
        for(SwitchLink e:links_) tl.add(e.toNbt());
        nbt.put("links", tl);
      }
    }

    @Override
    public void read(CompoundTag nbt, boolean updatePacket)
    {
      scd_ = nbt.getInt("scd");
      svd_ = nbt.getInt("svd");
      if(!updatePacket) {
        if(scd_ == 0) reset(null);
        if(nbt.contains("links")) {
          ListTag tl = nbt.getList("links", 10);
          links_ = new ArrayList<>();
          for(Tag e:tl) links_.add(SwitchLink.fromNbt((CompoundTag)e));
        }
      }
    }

    public int configured_on_time() { return (svd_ & SWITCH_DATA_SVD_ACTIVE_TIME_MASK); }
    public void configured_on_time(int t) { svd_ = (svd_ & (~SWITCH_DATA_SVD_ACTIVE_TIME_MASK)) | (t & SWITCH_DATA_SVD_ACTIVE_TIME_MASK); }
    public int setpower() { return (scd_ & (int)SWITCH_DATA_POWERED_POWER_MASK); }
    public boolean inverted() { return (scd_ & (int)SWITCH_DATA_INVERTED) != 0; }
    public boolean weak() { return (scd_ & (int)SWITCH_DATA_WEAK) != 0; }
    public boolean nooutput() { return (scd_ & (int)SWITCH_DATA_NOOUTPUT) != 0; }
    public long enabled_sides() { return (scd_ & SWITCH_DATA_SIDE_ENABLED_MASK); }

    public void setpower(int p) {
      scd_ = (scd_ & ~((int)SWITCH_DATA_POWERED_POWER_MASK)) | (int)(((p<0)?0:(Math.min(p, 15)) & ((int)SWITCH_DATA_POWERED_POWER_MASK))<<0);
    }

    public void nooutput(boolean val) { if(val) scd_ |= (int)SWITCH_DATA_NOOUTPUT; else scd_ &= ~(int)SWITCH_DATA_NOOUTPUT; }

    public void reset(@Nullable LevelReader world)
    {
      pulse_off_deadline_ = 0;
      svd_ = 0;
      if(world instanceof Level) {
        try { scd_ = (int)((((SwitchBlock)(world.getBlockState(getBlockPos()).getBlock())).config) & SWITCH_DATA_ENTITY_DEFAULTS_MASK); } catch(Exception e) { scd_ = 15; }
      } else { scd_ = 15; }
    }

    public int power(BlockState state, boolean strong)
    { return nooutput() ? 0 : ((strong && weak()) ? 0 : ((inverted() == state.getValue(POWERED)) ? 0 : setpower())); }

    public void reschedule_block_tick()
    {
      int t = on_time_remaining();
      if(t <= 0) return;
      final Block block = getBlockState().getBlock();
      if(!level.getBlockTicks().hasScheduledTick(worldPosition, block)) level.scheduleTick(worldPosition, block, t);
    }

    public int on_time_remaining() { long dt = Math.max(0, pulse_off_deadline_ - level.getGameTime()); return (dt > max_pulse_time) ? 0 : (int)dt; }
    public void on_timer_reset() { on_timer_reset(0); }
    public void on_timer_reset(int preset_ticks) { pulse_off_deadline_ = level.getGameTime() + Math.max(0, preset_ticks); }

    public void on_timer_extend()
    {
      int t = on_time_remaining();
      if(configured_on_time() >= base_tick_rate) t = configured_on_time();
      else if(t > 90) t = max_pulse_time;
      else if(t > 45) t = 100;
      else if(t > 15) t = 50;
      else if(t > 1) t = 30;
      else t = default_pulse_on_time;
      on_timer_reset(t);
    }

    public boolean touch_config(BlockState state, @Nullable Player player, double x, double y)
    {
      final long t = level.getGameTime();
      final boolean show_only = Math.abs(t-touch_config_time_lastclicked_) > 40;
      touch_config_time_lastclicked_ = t;
      return activation_config(state, player, x, y, show_only);
    }

    protected boolean activation_config(BlockState state, @Nullable Player player, double x, double y, boolean show_only) { return false; }

    public boolean click_config(@Nullable SwitchBlock block, boolean needsDoubleClick)
    {
      if(block==null) { click_config_time_lastclicked_ = 0; return false; }
      final long t = System.currentTimeMillis();
      if(needsDoubleClick) {
        boolean dblclicked = ((t - click_config_time_lastclicked_) > 0) && ((t - click_config_time_lastclicked_) < ModConfig.config_left_click_timeout());
        click_config_time_lastclicked_ = dblclicked ? 0 : t;
        if(!dblclicked) return false;
      }
      boolean multiclicked = ((t-click_config_last_cycled_) > 0) && ((t-click_config_last_cycled_) < 3000);
      click_config_last_cycled_ = t;
      if(!multiclicked) return true;

      boolean w = weak(), i = inverted(), n = nooutput();
      if((block.config & (SWITCH_CONFIG_INVERTABLE|SWITCH_CONFIG_WEAKABLE))==(SWITCH_CONFIG_INVERTABLE|SWITCH_CONFIG_WEAKABLE)) {
        if(!w && !i && !n) w=true; else if(w && !i) {w=false; i=true;} else if(!w && i) {w=true; i=true;} else if(w && i) {w=false; i=false; n=true;} else {w=false; i=false; n=false;}
      } else if((block.config & SWITCH_CONFIG_WEAKABLE) != 0) {
        if(!w && !n) w=true; else if(w) n=true; else {w=false; n=false;}
      } else if((block.config & SWITCH_CONFIG_INVERTABLE) != 0) {
        if(!i && !n) i=true; else if(i) n=true; else {i=false; n=false;}
      }

      if((block.config & (SWITCH_DATA_WEAK|SWITCH_CONFIG_WEAKABLE))==SWITCH_DATA_WEAK) w = true;
      if(ModConfig.without_switch_nooutput() || (block.config & SWITCH_CONFIG_LINK_SENDER)!=0) n = false;

      if(w) scd_ |= (int)SWITCH_DATA_WEAK; else scd_ &= ~(int)SWITCH_DATA_WEAK;
      if(i) scd_ |= (int)SWITCH_DATA_INVERTED; else scd_ &= ~(int)SWITCH_DATA_INVERTED;
      nooutput(n);

      setChanged();
      level.updateNeighborsAt(worldPosition, block);
      for(Direction f:Direction.values()) level.updateNeighborsAt(worldPosition.relative(f), block);
      return true;
    }

    public MutableComponent configStatusTextComponentTranslation(SwitchBlock block)
    {
      MutableComponent status = net.minecraft.network.chat.Component.empty().withStyle(ChatFormatting.RESET);
      if(setpower() < 15 && (block == null || (block.config & (SWITCH_CONFIG_AUTOMATIC|SWITCH_CONFIG_LINK_SENDER))==0)) {
        status.append(Auxiliaries.localizable("switchconfig.options.output_power", ChatFormatting.RED, new Object[]{setpower()}));
      }
      if(nooutput()) status.append(Auxiliaries.localizable("switchconfig.options.no_output", ChatFormatting.DARK_AQUA));
      else if(!inverted()) status.append(Auxiliaries.localizable("switchconfig.options." + (weak() ? "weak" : "strong"), ChatFormatting.DARK_AQUA));
      else status.append(Auxiliaries.localizable("switchconfig.options." + (weak() ? "weakinverted" : "stronginverted"), ChatFormatting.DARK_AQUA));
      return status;
    }

    public static long linktime() { return System.currentTimeMillis(); }

    public boolean verifySwitchLinkTarget(final SwitchLink link)
    {
      final long t = linktime();
      if(level.isClientSide() || last_link_request_ == t) return false;
      last_link_request_ = t;
      final BlockState state = level.getBlockState(worldPosition);
      return (state.getBlock() instanceof SwitchBlock && (((SwitchBlock)state.getBlock()).config & SWITCH_CONFIG_LINK_TARGET_SUPPORT)!=0);
    }

    enum SwitchLinkAssignmentResult {OK, E_SELF_ASSIGN, E_ALREADY_LINKED, E_TOO_FAR, E_NOSOURCE, E_FAILED}
    SwitchLinkAssignmentResult assignSwitchLink(final Level world, final BlockPos sourcepos, final ItemStack stack)
    {
      final SwitchLink link = SwitchLink.fromItemStack(stack);
      if(!link.valid || !sourcepos.equals(getBlockPos())) return SwitchLinkAssignmentResult.E_FAILED;
      final BlockState state = world.getBlockState(sourcepos);
      if(!(state.getBlock() instanceof SwitchBlock)) return SwitchLinkAssignmentResult.E_FAILED;
      if(link.target_position.equals(sourcepos)) return SwitchLinkAssignmentResult.E_SELF_ASSIGN;
      if((((SwitchBlock)state.getBlock()).config & SWITCH_CONFIG_LINK_SOURCE_SUPPORT)==0) return SwitchLinkAssignmentResult.E_NOSOURCE;
      if(link.isTooFar(sourcepos)) return SwitchLinkAssignmentResult.E_TOO_FAR;
      if(links_==null) links_ = new ArrayList<>();
      for(SwitchLink lnk: links_) if(lnk.target_position.equals(link.target_position)) return SwitchLinkAssignmentResult.E_ALREADY_LINKED;
      links_.add(link);
      final boolean powered = state.getValue(POWERED);
      link.initializeTarget(getLevel(), getBlockPos(), powered? setpower():0, powered?15:0);
      setChanged();
      return SwitchLinkAssignmentResult.OK;
    }

    public ArrayList<ItemStack> unlinkAllSwitchLinks(boolean drop)
    {
      ArrayList<ItemStack> stacks = new ArrayList<>();
      if(level.isClientSide() || links_==null) return stacks;
      for(SwitchLink e : links_) {
        stacks.add(e.toSwitchLinkPearl());
        e.unlinkTarget(getLevel(), getBlockPos());
      }
      links_.clear();
      if(drop) for(ItemStack e:stacks) level.addFreshEntity(new ItemEntity(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), e));
      return stacks;
    }

    public boolean activateSwitchLinks(int analog_power, int digital_power, boolean state_changed)
    {
      if(ModConfig.without_switch_linking()) return true;
      last_link_request_ = linktime();
      if(links_==null) return true;
      int n_fails = 0;
      for(SwitchLink lnk:links_) if(lnk.trigger(level, worldPosition, analog_power, digital_power, state_changed) != RequestResult.OK && lnk.trigger(level, worldPosition, analog_power, digital_power, state_changed) != RequestResult.NOT_MATCHED) ++n_fails;
      return (n_fails==0);
    }
  }

  protected static final class ClickInteraction
  {
    public boolean touch_configured = false, wrenched = false;
    public Item item = Items.AIR;
    public int item_count = 0;
    public double x = 0, y = 0;
    private static ClickInteraction touch(ClickInteraction ck, BlockState state, Direction facing, float x, float y, float z)
    {
      final SwitchBlock block = (SwitchBlock)(state.getBlock());
      // Touch config check
      double xo=0, yo=0;
      if((block.isCube()) || ((block.isWallMount()) && (!block.isLateral()))) {
        // UI facing the player in horizontal direction
        if(!block.isCube()) {
          if(facing != state.getValue(FACING)) return ck;
        } else {
          if(facing != state.getValue(FACING).getOpposite()) return ck;
        }
        switch (facing.get3DDataValue()) {
          case 0 -> { xo = 1 - x; yo = 1 - z; } // DOWN
          case 1 -> { xo = 1 - x; yo = z; } // UP
          case 2 -> { xo = 1 - x; yo = y; } // NORTH
          case 3 -> { xo = x; yo = y; } // SOUTH
          case 4 -> { xo = z; yo = y; } // WEST
          case 5 -> { xo = 1 - z; yo = y; } // EAST
        }
        final AABB aa = block.getShape(block.defaultBlockState().setValue(FACING, Direction.SOUTH)).bounds();
        xo = Math.round(((xo-aa.minX) * (1.0/(aa.maxX-aa.minX)) * 15.5) - 0.25);
        yo = Math.round(((yo-aa.minY) * (1.0/(aa.maxY-aa.minY)) * 15.5) - 0.25);
      } else if(block.isLateral()) {
        // Floor mounted UI facing up
        if(facing != Direction.UP) return ck;
        facing = state.getValue(FACING);
        switch (facing.get3DDataValue()) {
          case 0 -> { xo = x; yo = z; } // DOWN
          case 1 -> { xo = x; yo = z; } // UP
          case 2 -> { xo = x; yo = 1 - z; } // NORTH
          case 3 -> { xo = 1 - x; yo = z; } // SOUTH
          case 4 -> { xo = 1 - z; yo = 1 - x; } // WEST
          case 5 -> { xo = z; yo = x; } // EAST
        }
        final AABB aa = block.getShape(block.defaultBlockState().setValue(FACING, Direction.NORTH)).bounds();
        xo = 0.1 * Math.round(10.0 * (((xo-aa.minX) * (1.0/(aa.maxX-aa.minX)) * 15.5) - 0.25));
        yo = 0.1 * Math.round(10.0 * (((yo-(1.0-aa.maxZ)) * (1.0/(aa.maxZ-aa.minZ)) * 15.5) - 0.25));
      } else {
        return ck;
      }
      ck.x = ((xo > 15.0) ? (15.0) : (Math.max(xo, 0.0)));
      ck.y = ((yo > 15.0) ? (15.0) : (Math.max(yo, 0.0)));
      ck.touch_configured = true;
      return ck;
    }

    public static ClickInteraction get(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
      float x = (float)(hit.getLocation().x() - Math.floor(hit.getLocation().x()));
      float y = (float)(hit.getLocation().y() - Math.floor(hit.getLocation().y()));
      float z = (float)(hit.getLocation().z() - Math.floor(hit.getLocation().z()));
      return get(world, pos, state, player, hand, hit.getDirection(), x,y,z);
    }

    public static ClickInteraction get(BlockState state, Level world, BlockPos pos, Player player) { return get(world, pos, state, player, null, null, 0, 0, 0); }

    public static ClickInteraction get(Level world, BlockPos pos, @Nullable BlockState state, Player player, @Nullable InteractionHand hand, @Nullable Direction facing, float x, float y, float z)
    {
      ClickInteraction ck = new ClickInteraction();
      if(world==null || pos==null) return ck;
      if(state==null) state = world.getBlockState(pos);
      if(!(state.getBlock() instanceof SwitchBlock)) return ck;
      final SwitchBlock block = (SwitchBlock)(state.getBlock());
      final ItemStack item = player.getMainHandItem();
      if(item != null) {
        if(item.getItem() == Items.REDSTONE) { ck.item = Items.REDSTONE; ck.item_count = item.getCount(); }
        else if(item.getItem() == Items.ENDER_PEARL) { ck.item = Items.ENDER_PEARL; ck.item_count = item.getCount(); }
        else if(item.getItem() == Registries.getItem("switchlink_pearl")) {
          ck.item = item.getItem();
          ck.item_count = item.getCount();
        }
        else if(item.getItem() != Items.AIR) { ck.wrenched = ModConfig.isWrench(item); if(ck.wrenched) return ck; }
      }

      if(facing != null && (block.config & SWITCH_CONFIG_TOUCH_CONFIGURABLE) != 0 && !ck.wrenched && ck.item != Items.REDSTONE && ck.item != Items.ENDER_PEARL && ck.item != Registries.getItem("switchlink_pearl")) {
        return touch(ck, state, facing, x, y, z);
      }
      return ck;
    }
  }
}