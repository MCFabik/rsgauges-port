package wile.rsgauges.detail;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import wile.rsgauges.ModContent;
import wile.rsgauges.ModRsGauges;

@EventBusSubscriber(modid = ModRsGauges.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CapabilityHandler {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModContent.TRANSPORT_TERMINAL_BLOCK_ENTITY.get(),
                (be, side) -> be.itemHandler
        );
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModContent.TRANSPORT_TERMINAL_BLOCK_ENTITY.get(),
                (be, side) -> be.energyStorage
        );
    }
}