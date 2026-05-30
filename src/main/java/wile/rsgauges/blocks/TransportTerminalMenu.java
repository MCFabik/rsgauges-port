package wile.rsgauges.blocks;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import wile.rsgauges.ModContent;


public class TransportTerminalMenu extends AbstractContainerMenu {
    private final TransportTerminalBlockEntity blockEntity;
    private final ContainerData data;

    public TransportTerminalMenu(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
        this(windowId, playerInventory, playerInventory.player.level().getBlockEntity(data.readBlockPos()));
    }

    public TransportTerminalMenu(int windowId, Inventory playerInventory, BlockEntity blockEntity) {
        super(ModContent.TRANSPORT_TERMINAL_MENU.get(), windowId);
        this.blockEntity = (TransportTerminalBlockEntity) blockEntity;
        
        this.data = new SimpleContainerData(2);
        this.addDataSlots(this.data);

        // Input slot
        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, 0, 8, 28));
        
        // Output slot
        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, 1, 8, 66));

        // Storage slots (14 slots, 7 cols, 2 rows)
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 7; col++) {
                this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, 2 + col + row * 7, 44 + col * 18, 28 + row * 18));
            }
        }

        // Player inventory
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 110 + i * 18));
            }
        }

        // Player hotbar
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 168));
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (!this.blockEntity.getLevel().isClientSide) {
            int energy = this.blockEntity.getEnergyStorage().getEnergyStored();
            this.data.set(0, energy & 0xFFFF);
            this.data.set(1, (energy >> 16) & 0xFFFF);
        }
    }

    public int getEnergy() {
        return (this.data.get(1) << 16) | (this.data.get(0) & 0xFFFF);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            
            if (index < 16) {
                if (!this.moveItemStackTo(stack, 16, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stack, 0, 16, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.blockEntity.getLevel().getBlockState(this.blockEntity.getBlockPos()).is(ModContent.getBlock("transport_terminal"));
    }

    public TransportTerminalBlockEntity getBlockEntity() {
        return blockEntity;
    }
}