package wile.rsgauges.blocks;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.neoforged.neoforge.network.PacketDistributor;
import wile.rsgauges.ModRsGauges;
import wile.rsgauges.ModConfig;
import wile.rsgauges.network.RenameChipPacket;
import wile.rsgauges.network.TeleportPacket;
public class TransportTerminalScreen extends AbstractContainerScreen<TransportTerminalMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ModRsGauges.MODID, "textures/gui/transport_terminal.png");
    private EditBox nameField;

    public TransportTerminalScreen(TransportTerminalMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 192;
    }

    @Override
    protected void init() {
        super.init();
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // Edit box for renaming the chip in the output slot (slot 1)
        this.nameField = new EditBox(this.font, x + 8, y + 86, 160, 12, Component.literal("Chip Name"));
        this.nameField.setMaxLength(32);
        this.nameField.setResponder(this::onNameChanged);
        this.addRenderableWidget(this.nameField);

        // Add 14 small gray buttons
        for (int i = 0; i < 7; i++) {
            int col = i;
            // Top row button
            this.addRenderableWidget(new net.minecraft.client.gui.components.ImageButton(x + 44 + col * 18, y + 18, 16, 8, new net.minecraft.client.gui.components.WidgetSprites(TEXTURE, TEXTURE), (button) -> {
                PacketDistributor.sendToServer(new TeleportPacket(this.menu.getBlockEntity().getBlockPos(), 2 + col));
            }) {
                @Override
                public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
                    int vOffset = this.isHoveredOrFocused() ? 8 : 0;
                    guiGraphics.blit(TEXTURE, this.getX(), this.getY(), 176, vOffset, this.width, this.height);
                }
            });
            // Bottom row button
            this.addRenderableWidget(new net.minecraft.client.gui.components.ImageButton(x + 44 + col * 18, y + 66, 16, 8, new net.minecraft.client.gui.components.WidgetSprites(TEXTURE, TEXTURE), (button) -> {
                PacketDistributor.sendToServer(new TeleportPacket(this.menu.getBlockEntity().getBlockPos(), 9 + col));
            }) {
                @Override
                public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
                    int vOffset = this.isHoveredOrFocused() ? 8 : 0;
                    guiGraphics.blit(TEXTURE, this.getX(), this.getY(), 176, vOffset, this.width, this.height);
                }
            });
        }
    }

    private void onNameChanged(String newName) {
        ItemStack chip = this.menu.getBlockEntity().getChip(1);
        if (!chip.isEmpty() && (!chip.has(DataComponents.CUSTOM_NAME) || !chip.getHoverName().getString().equals(newName))) {
            PacketDistributor.sendToServer(new RenameChipPacket(this.menu.getBlockEntity().getBlockPos(), 1, newName));
        }
    }

    @Override
    public void containerTick() {
        super.containerTick();
        ItemStack chip = this.menu.getBlockEntity().getChip(1);
        if (!chip.isEmpty() && chip.has(DataComponents.CUSTOM_NAME)) {
            String currentName = chip.getHoverName().getString();
            if (!this.nameField.isFocused() && !this.nameField.getValue().equals(currentName)) {
                this.nameField.setValue(currentName);
            }
        } else if (!chip.isEmpty() && !this.nameField.isFocused()) {
            this.nameField.setValue("");
        } else if (chip.isEmpty()) {
            this.nameField.setEditable(false);
            if (!this.nameField.isFocused()) this.nameField.setValue("");
        }
        
        if (!chip.isEmpty()) {
            this.nameField.setEditable(true);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
    
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = this.menu.getBlockEntity().getBlockPos().getX();
        int y = this.menu.getBlockEntity().getBlockPos().getY();
        int z = this.menu.getBlockEntity().getBlockPos().getZ();
        
        guiGraphics.drawString(this.font, "Location X: " + x + " Y: " + y + " Z: " + z, 8, 6, 0x404040, false);
        
        guiGraphics.drawString(this.font, "Inventory", 8, 100, 0x404040, false);
        
        boolean useEnergy = ModRsGauges.isEnergyModLoaded() && ModConfig.transport_terminal_teleport_cost() > 0;
        if (useEnergy) {
            guiGraphics.drawString(this.font, "FE: " + this.menu.getEnergy(), 100, 100, 0x404040, false);
        }
    }
}