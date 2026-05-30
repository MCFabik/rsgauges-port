package wile.rsgauges.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

import net.neoforged.neoforge.items.ItemStackHandler;
import wile.rsgauges.ModConfig;
import wile.rsgauges.ModContent;
import wile.rsgauges.items.TransportChipItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

public class TransportTerminalBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler itemHandler = createItemHandler();
    public final EnergyStorage energyStorage;

    public TransportTerminalBlockEntity(BlockPos pos, BlockState state) {
        super(ModContent.getBlockEntityTypeOfBlock("transport_terminal"), pos, state);
        this.energyStorage = new EnergyStorage(ModConfig.transport_terminal_capacity(), ModConfig.transport_terminal_capacity(), 0);
    }

    private ItemStackHandler createItemHandler() {
        return new ItemStackHandler(16) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                if(level != null && !level.isClientSide) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
            }

            @Override
            public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
                super.deserializeNBT(provider, nbt);
                if (this.getSlots() != 16) {
                    this.setSize(16);
                }
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() instanceof TransportChipItem;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.rsgauges.transport_terminal");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
        return new TransportTerminalMenu(windowId, playerInventory, this);
    }

    public ItemStack getChip(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < itemHandler.getSlots()) {
            return itemHandler.getStackInSlot(slotIndex);
        }
        return ItemStack.EMPTY;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TransportTerminalBlockEntity be) {
        if (!level.isClientSide) {
            ItemStack input = be.itemHandler.getStackInSlot(0);
            if (!input.isEmpty() && input.getItem() instanceof TransportChipItem) {
                if (be.itemHandler.getStackInSlot(1).isEmpty()) {
                    ItemStack output = input.copy();
                    
                    CustomData.update(DataComponents.CUSTOM_DATA, output, tag -> {
                        tag.putInt("TargetX", pos.getX());
                        tag.putInt("TargetY", pos.getY());
                        tag.putInt("TargetZ", pos.getZ());
                        tag.putString("Dimension", level.dimension().location().toString());
                    });
                    
                    if (!output.has(DataComponents.CUSTOM_NAME)) {
                        output.set(DataComponents.CUSTOM_NAME, Component.literal("Transport Chip " + (pos.getX() + pos.getY() + pos.getZ())));
                    }
                    
                    be.itemHandler.setStackInSlot(1, output);
                    be.itemHandler.setStackInSlot(0, ItemStack.EMPTY);
                    be.setChanged();
                }
            }
        }
    }

    boolean isChipProgrammed(int slotIndex) {
        ItemStack chip = getChip(slotIndex);
        return !chip.isEmpty() && chip.has(DataComponents.CUSTOM_DATA);
    }

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", itemHandler.serializeNBT(registries));
        tag.put("Energy", energyStorage.serializeNBT(registries));
    }

    @Override
    public void loadAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) {
            itemHandler.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
        if(tag.contains("Energy")) {
            energyStorage.deserializeNBT(registries, tag.get("Energy"));
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}