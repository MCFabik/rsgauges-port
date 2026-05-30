/*
 * @file Networking.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2020 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 *
 * Main client/server message handling (NeoForge 1.21.1 Update).
 */
package wile.rsgauges.libmc.detail;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import wile.rsgauges.ModRsGauges;
import wile.rsgauges.network.TeleportPacket;
import wile.rsgauges.network.RenameChipPacket;

import java.util.function.BiConsumer;

public class Networking
{
  // --- Interfaces für TileEntities und Container ---
  public interface IPacketTileNotifyReceiver {
    default void onServerPacketReceived(CompoundTag nbt) {}
    default void onClientPacketReceived(Player player, CompoundTag nbt) {}
  }

  public interface INetworkSynchronisableContainer {
    void onServerPacketReceived(int windowId, CompoundTag nbt);
    void onClientPacketReceived(int windowId, Player player, CompoundTag nbt);
  }

  // --- Initialisierung (Wird über Event aufgerufen) ---
  public static void init(final RegisterPayloadHandlersEvent event) {
    final PayloadRegistrar registrar = event.registrar(ModRsGauges.MODID).versioned("1");

    // Registrierung der Payloads
    registrar.playToServer(PacketTileNotifyClientToServer.TYPE, PacketTileNotifyClientToServer.STREAM_CODEC, PacketTileNotifyClientToServer::handle);
    registrar.playToClient(PacketTileNotifyServerToClient.TYPE, PacketTileNotifyServerToClient.STREAM_CODEC, PacketTileNotifyServerToClient::handle);
    registrar.playToServer(PacketContainerSyncClientToServer.TYPE, PacketContainerSyncClientToServer.STREAM_CODEC, PacketContainerSyncClientToServer::handle);
    registrar.playToClient(PacketContainerSyncServerToClient.TYPE, PacketContainerSyncServerToClient.STREAM_CODEC, PacketContainerSyncServerToClient::handle);
    registrar.playToClient(OverlayTextMessage.TYPE, OverlayTextMessage.STREAM_CODEC, OverlayTextMessage::handle);
    registrar.playToServer(TeleportPacket.TYPE, TeleportPacket.STREAM_CODEC, TeleportPacket::handle);
    registrar.playToServer(RenameChipPacket.TYPE, RenameChipPacket.STREAM_CODEC, RenameChipPacket::handle);
  }

  // --------------------------------------------------------------------------------------------------------------------
  // 1. PacketTileNotifyClientToServer (C -> S)
  // --------------------------------------------------------------------------------------------------------------------
  public record PacketTileNotifyClientToServer(BlockPos pos, CompoundTag nbt) implements CustomPacketPayload {
    public static final Type<PacketTileNotifyClientToServer> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ModRsGauges.MODID, "tile_notify_c2s"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketTileNotifyClientToServer> STREAM_CODEC = StreamCodec.of(
            (RegistryFriendlyByteBuf buf, PacketTileNotifyClientToServer pkt) -> { buf.writeBlockPos(pkt.pos()); buf.writeNbt(pkt.nbt()); },
            (RegistryFriendlyByteBuf buf) -> new PacketTileNotifyClientToServer(buf.readBlockPos(), buf.readNbt())
    );

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void sendToServer(BlockPos pos, CompoundTag nbt) {
      if(pos != null && nbt != null) PacketDistributor.sendToServer(new PacketTileNotifyClientToServer(pos, nbt));
    }

    public static void handle(final PacketTileNotifyClientToServer pkt, final IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
        Player player = ctx.player();
        if(player == null) return;
        BlockEntity te = player.level().getBlockEntity(pkt.pos());
        if(te instanceof IPacketTileNotifyReceiver) ((IPacketTileNotifyReceiver)te).onClientPacketReceived(player, pkt.nbt());
      });
    }
  }

  // --------------------------------------------------------------------------------------------------------------------
  // 2. PacketTileNotifyServerToClient (S -> C)
  // --------------------------------------------------------------------------------------------------------------------
  public record PacketTileNotifyServerToClient(BlockPos pos, CompoundTag nbt) implements CustomPacketPayload {
    public static final Type<PacketTileNotifyServerToClient> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ModRsGauges.MODID, "tile_notify_s2c"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketTileNotifyServerToClient> STREAM_CODEC = StreamCodec.of(
            (RegistryFriendlyByteBuf buf, PacketTileNotifyServerToClient pkt) -> { buf.writeBlockPos(pkt.pos()); buf.writeNbt(pkt.nbt()); },
            (RegistryFriendlyByteBuf buf) -> new PacketTileNotifyServerToClient(buf.readBlockPos(), buf.readNbt())
    );

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void sendToPlayers(BlockEntity te, CompoundTag nbt) {
      // 1.21.1: Prüfen ob das Level ein ServerLevel ist, da sendToPlayersInDimension dieses nun erwartet
      if(te == null || !(te.getLevel() instanceof ServerLevel serverLevel) || nbt == null) return;
      PacketDistributor.sendToPlayersInDimension(serverLevel, new PacketTileNotifyServerToClient(te.getBlockPos(), nbt));
    }

    public static void handle(final PacketTileNotifyServerToClient pkt, final IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
        Level world = ctx.player().level();
        BlockEntity te = world.getBlockEntity(pkt.pos());
        if(te instanceof IPacketTileNotifyReceiver) ((IPacketTileNotifyReceiver)te).onServerPacketReceived(pkt.nbt());
      });
    }
  }

  // --------------------------------------------------------------------------------------------------------------------
  // 3. PacketContainerSyncClientToServer (C -> S)
  // --------------------------------------------------------------------------------------------------------------------
  public record PacketContainerSyncClientToServer(int id, CompoundTag nbt) implements CustomPacketPayload {
    public static final Type<PacketContainerSyncClientToServer> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ModRsGauges.MODID, "ct_sync_c2s"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketContainerSyncClientToServer> STREAM_CODEC = StreamCodec.of(
            (RegistryFriendlyByteBuf buf, PacketContainerSyncClientToServer pkt) -> { buf.writeInt(pkt.id()); buf.writeNbt(pkt.nbt()); },
            (RegistryFriendlyByteBuf buf) -> new PacketContainerSyncClientToServer(buf.readInt(), buf.readNbt())
    );

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void sendToServer(AbstractContainerMenu container, CompoundTag nbt) {
      PacketDistributor.sendToServer(new PacketContainerSyncClientToServer(container.containerId, nbt));
    }

    public static void handle(final PacketContainerSyncClientToServer pkt, final IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
        Player player = ctx.player();
        if(player.containerMenu instanceof INetworkSynchronisableContainer && player.containerMenu.containerId == pkt.id()) {
          ((INetworkSynchronisableContainer)player.containerMenu).onClientPacketReceived(pkt.id(), player, pkt.nbt());
        }
      });
    }
  }

  // --------------------------------------------------------------------------------------------------------------------
  // 4. PacketContainerSyncServerToClient (S -> C)
  // --------------------------------------------------------------------------------------------------------------------
  public record PacketContainerSyncServerToClient(int id, CompoundTag nbt) implements CustomPacketPayload {
    public static final Type<PacketContainerSyncServerToClient> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ModRsGauges.MODID, "ct_sync_s2c"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketContainerSyncServerToClient> STREAM_CODEC = StreamCodec.of(
            (RegistryFriendlyByteBuf buf, PacketContainerSyncServerToClient pkt) -> { buf.writeInt(pkt.id()); buf.writeNbt(pkt.nbt()); },
            (RegistryFriendlyByteBuf buf) -> new PacketContainerSyncServerToClient(buf.readInt(), buf.readNbt())
    );

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(final PacketContainerSyncServerToClient pkt, final IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
        Player player = ctx.player();
        if(player.containerMenu instanceof INetworkSynchronisableContainer && player.containerMenu.containerId == pkt.id()) {
          ((INetworkSynchronisableContainer)player.containerMenu).onServerPacketReceived(pkt.id(), pkt.nbt());
        }
      });
    }
  }

  // --------------------------------------------------------------------------------------------------------------------
  // 5. OverlayTextMessage (S -> C)
  // --------------------------------------------------------------------------------------------------------------------
  public record OverlayTextMessage(Component message, int delay) implements CustomPacketPayload {
    public static final Type<OverlayTextMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ModRsGauges.MODID, "overlay_text"));
    private static BiConsumer<Component, Integer> handler_ = null;

    public static final StreamCodec<RegistryFriendlyByteBuf, OverlayTextMessage> STREAM_CODEC = StreamCodec.of(
            (RegistryFriendlyByteBuf buf, OverlayTextMessage pkt) -> {
              ComponentSerialization.STREAM_CODEC.encode(buf, pkt.message());
              buf.writeInt(pkt.delay());
            },
            (RegistryFriendlyByteBuf buf) -> new OverlayTextMessage(
                    ComponentSerialization.STREAM_CODEC.decode(buf),
                    buf.readInt()
            )
    );

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void setHandler(BiConsumer<Component, Integer> handler) { handler_ = handler; }

    public static void sendToPlayer(Player player, Component message, int delay) {
      if(player instanceof ServerPlayer sp && !(sp instanceof FakePlayer)) {
        PacketDistributor.sendToPlayer(sp, new OverlayTextMessage(message, delay));
      }
    }

    public static void handle(final OverlayTextMessage pkt, final IPayloadContext ctx) {
      if(handler_ != null) ctx.enqueueWork(() -> handler_.accept(pkt.message(), pkt.delay()));
    }
  }
}