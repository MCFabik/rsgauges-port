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
import wile.rsgauges.blocks.TransportTerminalBlock;
import wile.rsgauges.blocks.TransportTerminalBlockEntity;

public record TeleportPacket(BlockPos pos, int slotIndex) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ModRsGauges.MODID, "teleport");
    public static final Type<TeleportPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, TeleportPacket> STREAM_CODEC = CustomPacketPayload.codec(TeleportPacket::write, TeleportPacket::new);

    public TeleportPacket(final FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readInt());
    }

    public void write(final FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(slotIndex);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final TeleportPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            if (player == null) return;
            BlockEntity be = player.level().getBlockEntity(packet.pos);
            if (be instanceof TransportTerminalBlockEntity terminal) {
                if(player.level().getBlockState(packet.pos).getBlock() instanceof TransportTerminalBlock block) {
                    block.teleportPlayer(player, terminal, packet.slotIndex);
                }
            }
        });
    }
}