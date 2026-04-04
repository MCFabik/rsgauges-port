/*
 * @file RsBlock.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2018 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 */
package wile.rsgauges.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import wile.rsgauges.libmc.detail.Auxiliaries;
import wile.rsgauges.libmc.detail.Networking;

import javax.annotation.Nullable;
import java.util.List;

public abstract class RsBlock extends Block implements EntityBlock
{
  public static final long RSBLOCK_CONFIG_SOLID              = 0x0000000000000000L;
  public static final long RSBLOCK_CONFIG_CUTOUT             = 0x1000000000000000L;
  public static final long RSBLOCK_CONFIG_CUTOUT_MIPPED      = 0x2000000000000000L;
  public static final long RSBLOCK_CONFIG_TRANSLUCENT        = 0x3000000000000000L;
  public static final long RSBLOCK_NOT_WATERLOGGABLE         = 0x0008000000000000L;
  public enum RenderTypeHint { SOLID, CUTOUT, CUTOUT_MIPPED, TRANSLUCENT }

  private static final RenderTypeHint[] render_layer_map_ = { RenderTypeHint.SOLID, RenderTypeHint.CUTOUT, RenderTypeHint.CUTOUT_MIPPED, RenderTypeHint.TRANSLUCENT };
  public final long config;

  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

  public RsBlock(long config, BlockBehaviour.Properties properties)
  { this(config, properties, Auxiliaries.getPixeledAABB(0, 0, 0, 16, 16, 16)); }

  public RsBlock(long config, BlockBehaviour.Properties properties, final AABB aabb)
  { this(config, properties, Shapes.create(aabb)); }

  public RsBlock(long config, BlockBehaviour.Properties properties, final VoxelShape vshape)
  { super(properties); this.config = config; registerDefaultState(this.getStateDefinition().any().setValue(WATERLOGGED, false)); }

  public RenderTypeHint getRenderTypeHint()
  { return render_layer_map_[(int)((config>>60)&0x3)]; }

  @Override
  @Nullable
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> te_type)
  { return (world.isClientSide) ? (null) : ((w, p, s, te) -> ((RsTileEntity)te).tick()); }

  @Override
  public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag)
  { Auxiliaries.Tooltip.addInformation(stack, context, tooltip, flag, true); }

  @OnlyIn(Dist.CLIENT)
  public boolean hasCustomBreakingProgress(BlockState state)
  { return true; }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter source, BlockPos pos, CollisionContext selectionContext)
  { return Shapes.block(); }

  @Override
  public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext selectionContext)
  { return getShape(state, world, pos, selectionContext); }

  public boolean isValidSpawn(BlockState state, BlockGetter world, BlockPos pos, EntityType<?> type)
  { return false; }

  @Override
  public PushReaction getPistonPushReaction(BlockState state)
  { return PushReaction.DESTROY; }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
  { super.createBlockStateDefinition(builder); builder.add(WATERLOGGED); }

  @Override
  @Nullable
  public BlockState getStateForPlacement(BlockPlaceContext context)
  {
    BlockState state = super.getStateForPlacement(context);
    if(state==null) return null;
    if((config & RSBLOCK_NOT_WATERLOGGABLE)==0) {
      FluidState fs = context.getLevel().getFluidState(context.getClickedPos());
      state = state.setValue(WATERLOGGED, fs.getType() == Fluids.WATER);
    } else {
      state = state.setValue(WATERLOGGED, false);
    }
    return state;
  }

  @Override
  public FluidState getFluidState(BlockState state)
  { return ((config & RSBLOCK_NOT_WATERLOGGABLE)==0) ? (state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state)) : super.getFluidState(state); }

  @Override
  public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving)
  {
    super.onRemove(state, world, pos, newState, isMoving);
    world.updateNeighbourForOutputSignal(pos, newState.getBlock());
    world.updateNeighborsAt(pos, newState.getBlock());
  }

  @Override
  public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos)
  {
    if((config & RSBLOCK_NOT_WATERLOGGABLE)==0) {
      if(state.getValue(WATERLOGGED)) world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
    }
    return state;
  }

  @Override
  protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit)
  { return InteractionResult.PASS; }

  // -------------------------------------------------------------------------------------------------------------------
  // Basic tile entity
  // -------------------------------------------------------------------------------------------------------------------

  public static abstract class RsTileEntity extends BlockEntity implements Networking.IPacketTileNotifyReceiver
  {
    public RsTileEntity(BlockEntityType<?> te_type, BlockPos pos, BlockState state)
    { super(te_type, pos, state); }

    public void write(CompoundTag nbt, boolean updatePacket) {}
    public void read(CompoundTag nbt, boolean updatePacket) {}
    public void tick() {}

    protected final void syncToClients()
    {
      if(level == null || level.isClientSide()) return;
      CompoundTag nbt = new CompoundTag();
      write(nbt, true);
      Networking.PacketTileNotifyServerToClient.sendToPlayers(this, nbt);
    }

    public final void onServerPacketReceived(CompoundTag nbt)
    { read(nbt, true); }

    // GEÄNDERT: Nutzt nun HolderLookup.Provider statt Holder.Provider
    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries)
    { super.saveAdditional(nbt, registries); write(nbt, false); }

    // GEÄNDERT: Nutzt nun HolderLookup.Provider statt Holder.Provider
    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries)
    { super.loadAdditional(nbt, registries); read(nbt, false); }
  }
}