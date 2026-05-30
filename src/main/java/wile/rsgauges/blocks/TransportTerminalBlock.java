package wile.rsgauges.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

import wile.rsgauges.ModConfig;
import wile.rsgauges.ModContent;
import javax.annotation.Nullable;

public class TransportTerminalBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static VoxelShape combine(VoxelShape... shapes) {
        if (shapes.length == 0) return Shapes.empty();
        VoxelShape result = shapes[0];
        for (int i = 1; i < shapes.length; i++) {
            result = Shapes.or(result, shapes[i]);
        }
        return result;
    }

    private static final VoxelShape BASE = Block.box(0, 0, 0, 16, 2, 16);

    // Stepped console approximation for NORTH
    private static final VoxelShape CONSOLE_N1 = Block.box(1.5, 9, 11, 14.5, 10, 13);
    private static final VoxelShape CONSOLE_N2 = Block.box(1.5, 10, 10, 14.5, 11, 14);
    private static final VoxelShape CONSOLE_N3 = Block.box(1.5, 11, 9, 14.5, 12, 15);
    private static final VoxelShape CONSOLE_N4 = Block.box(1.5, 12, 10, 14.5, 13, 15);
    private static final VoxelShape CONSOLE_N5 = Block.box(1.5, 13, 11, 14.5, 14, 14);
    private static final VoxelShape CONSOLE_N6 = Block.box(1.5, 14, 12, 14.5, 15, 13);
    private static final VoxelShape CONSOLE_NORTH = combine(CONSOLE_N1, CONSOLE_N2, CONSOLE_N3, CONSOLE_N4, CONSOLE_N5, CONSOLE_N6);

    private static final VoxelShape PAD_NORTH = Block.box(1, 2, 1, 15, 3, 12);
    private static final VoxelShape SUPPORT_NORTH = Block.box(4, 2, 12, 12, 11, 14);
    private static final VoxelShape SHAPE_NORTH = combine(BASE, PAD_NORTH, SUPPORT_NORTH, CONSOLE_NORTH);

    // Stepped console approximation for SOUTH
    private static final VoxelShape CONSOLE_S1 = Block.box(1.5, 9, 3, 14.5, 10, 5);
    private static final VoxelShape CONSOLE_S2 = Block.box(1.5, 10, 2, 14.5, 11, 6);
    private static final VoxelShape CONSOLE_S3 = Block.box(1.5, 11, 1, 14.5, 12, 7);
    private static final VoxelShape CONSOLE_S4 = Block.box(1.5, 12, 1, 14.5, 13, 6);
    private static final VoxelShape CONSOLE_S5 = Block.box(1.5, 13, 2, 14.5, 14, 5);
    private static final VoxelShape CONSOLE_S6 = Block.box(1.5, 14, 3, 14.5, 15, 4);
    private static final VoxelShape CONSOLE_SOUTH = combine(CONSOLE_S1, CONSOLE_S2, CONSOLE_S3, CONSOLE_S4, CONSOLE_S5, CONSOLE_S6);

    private static final VoxelShape PAD_SOUTH = Block.box(1, 2, 4, 15, 3, 15);
    private static final VoxelShape SUPPORT_SOUTH = Block.box(4, 2, 2, 12, 11, 4);
    private static final VoxelShape SHAPE_SOUTH = combine(BASE, PAD_SOUTH, SUPPORT_SOUTH, CONSOLE_SOUTH);

    // Stepped console approximation for WEST
    private static final VoxelShape CONSOLE_W1 = Block.box(11, 9, 1.5, 13, 10, 14.5);
    private static final VoxelShape CONSOLE_W2 = Block.box(10, 10, 1.5, 14, 11, 14.5);
    private static final VoxelShape CONSOLE_W3 = Block.box(9, 11, 1.5, 15, 12, 14.5);
    private static final VoxelShape CONSOLE_W4 = Block.box(10, 12, 1.5, 15, 13, 14.5);
    private static final VoxelShape CONSOLE_W5 = Block.box(11, 13, 1.5, 14, 14, 14.5);
    private static final VoxelShape CONSOLE_W6 = Block.box(12, 14, 1.5, 13, 15, 14.5);
    private static final VoxelShape CONSOLE_WEST = combine(CONSOLE_W1, CONSOLE_W2, CONSOLE_W3, CONSOLE_W4, CONSOLE_W5, CONSOLE_W6);

    private static final VoxelShape PAD_WEST = Block.box(4, 2, 1, 15, 3, 15);
    private static final VoxelShape SUPPORT_WEST = Block.box(12, 2, 4, 14, 11, 12);
    private static final VoxelShape SHAPE_WEST = combine(BASE, PAD_WEST, SUPPORT_WEST, CONSOLE_WEST);

    // Stepped console approximation for EAST
    private static final VoxelShape CONSOLE_E1 = Block.box(3, 9, 1.5, 5, 10, 14.5);
    private static final VoxelShape CONSOLE_E2 = Block.box(2, 10, 1.5, 6, 11, 14.5);
    private static final VoxelShape CONSOLE_E3 = Block.box(1, 11, 1.5, 7, 12, 14.5);
    private static final VoxelShape CONSOLE_E4 = Block.box(1, 12, 1.5, 6, 13, 14.5);
    private static final VoxelShape CONSOLE_E5 = Block.box(2, 13, 1.5, 5, 14, 14.5);
    private static final VoxelShape CONSOLE_E6 = Block.box(3, 14, 1.5, 4, 15, 14.5);
    private static final VoxelShape CONSOLE_EAST = combine(CONSOLE_E1, CONSOLE_E2, CONSOLE_E3, CONSOLE_E4, CONSOLE_E5, CONSOLE_E6);

    private static final VoxelShape PAD_EAST = Block.box(1, 2, 1, 12, 3, 15);
    private static final VoxelShape SUPPORT_EAST = Block.box(2, 2, 4, 4, 11, 12);
    private static final VoxelShape SHAPE_EAST = combine(BASE, PAD_EAST, SUPPORT_EAST, CONSOLE_EAST);

    public TransportTerminalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            case EAST -> SHAPE_EAST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(TransportTerminalBlock::new);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TransportTerminalBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof TransportTerminalBlockEntity terminal) {
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.openMenu(terminal, pos);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModContent.TRANSPORT_TERMINAL_BLOCK_ENTITY.get(), TransportTerminalBlockEntity::tick);
    }

    public void teleportPlayer(Player player, TransportTerminalBlockEntity be, int slotIndex) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        
        if (slotIndex < 0 || slotIndex >= 16) return;
        ItemStack chip = be.getChip(slotIndex);
        if(chip.isEmpty()) return;

        if(!chip.has(net.minecraft.core.component.DataComponents.CUSTOM_DATA) || !chip.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA).copyTag().contains("TargetX")) {
            net.minecraft.nbt.CompoundTag tag = chip.has(net.minecraft.core.component.DataComponents.CUSTOM_DATA) ? chip.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA).copyTag() : new net.minecraft.nbt.CompoundTag();
            
            Direction facing = be.getBlockState().getValue(FACING);
            BlockPos targetPos = be.getBlockPos().relative(facing);
            
            tag.putInt("TargetX", targetPos.getX());
            tag.putInt("TargetY", targetPos.getY());
            tag.putInt("TargetZ", targetPos.getZ());
            tag.putString("Dimension", player.level().dimension().location().toString());
            
            chip.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
            be.setChanged();
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aChip im Terminal gelinkt!"));
            return;
        }

        int cost = ModConfig.transport_terminal_teleport_cost();
        if (!wile.rsgauges.ModRsGauges.isEnergyModLoaded()) {
            cost = 0;
        }

        if (cost > 0) {
            if (be.getEnergyStorage().getEnergyStored() < cost) {
                serverPlayer.displayClientMessage(net.minecraft.network.chat.Component.translatable("message.rsgauges.transport_terminal.not_enough_energy"), true);
                return;
            }
        }

        CustomData customData = chip.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return;

        CompoundTag tag = customData.copyTag();
        if (tag.contains("TargetX")) {
            int x = tag.getInt("TargetX");
            int y = tag.getInt("TargetY");
            int z = tag.getInt("TargetZ");
            String dimStr = tag.getString("Dimension");

            ResourceKey<Level> targetDim = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(dimStr));
            ServerLevel targetLevel = serverPlayer.getServer().getLevel(targetDim);

            if (targetLevel != null) {
                if (cost > 0) {
                    be.getEnergyStorage().extractEnergy(cost, false);
                }
                serverPlayer.teleportTo(targetLevel, x + 0.5, y, z + 0.5, serverPlayer.getYRot(), serverPlayer.getXRot());
                serverPlayer.closeContainer();
                serverPlayer.sendSystemMessage(Component.literal("§6Teleportation erfolgreich!"));
            } else {
                player.sendSystemMessage(Component.literal("§cZiel-Dimension nicht gefunden!"));
            }
        }
    }
    
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof TransportTerminalBlockEntity be) {
                for (int i = 0; i < be.itemHandler.getSlots(); ++i) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), be.itemHandler.getStackInSlot(i));
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}