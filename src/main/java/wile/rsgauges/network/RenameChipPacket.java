package wile.rsgauges.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import wile.rsgauges.ModRsGauges;
import wile.rsgauges.blocks.TransportTerminalBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;

public record RenameChipPacket(BlockPos pos, int slotIndex, String newName) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ModRsGauges.MODID, "rename_chip");
    public static final Type<RenameChipPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, RenameChipPacket> STREAM_CODEC = CustomPacketPayload.codec(RenameChipPacket::write, RenameChipPacket::new);

    public RenameChipPacket(final FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readInt(), buf.readUtf());
    }

    public void write(final FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(slotIndex);
        buf.writeUtf(newName);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final RenameChipPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            if (player == null) return;
            BlockEntity be = player.level().getBlockEntity(packet.pos);
            if (be instanceof TransportTerminalBlockEntity terminal) {
                if (packet.slotIndex >= 0 && packet.slotIndex < 14) {
                    ItemStack chip = terminal.getChip(packet.slotIndex);
                    if (!chip.isEmpty()) {
                        chip.set(DataComponents.CUSTOM_NAME, Component.literal(packet.newName));
                        terminal.setChanged();
                    }
                }
            }
        });
    }
}
