/*
 * @file ModRsGauges.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2018 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 *
 * Main mod class (NeoForge 1.21.1 Update).
 */
package wile.rsgauges;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wile.rsgauges.detail.BlockCategories;
import wile.rsgauges.libmc.detail.*;

import java.util.List;

@Mod(ModRsGauges.MODID)
public class ModRsGauges
{
  public static final String MODID = "gaugesswitchesported";
  public static final String MODNAME = "Gauges and Switches";
  public static final int VERSION_DATAFIXER = 0;
  private static final Logger LOGGER = LogManager.getLogger();

  public ModRsGauges(IEventBus modEventBus, ModContainer modContainer)
  {
    Auxiliaries.init(MODID, LOGGER, CompoundTag::new);
    Auxiliaries.logGitVersion(MODNAME);

    Registries.init(MODID, "industrial_small_lever");
    ModContent.init(MODID);
    OptionalRecipeCondition.init(MODID, LOGGER);

    modContainer.registerConfig(ModConfig.Type.COMMON, wile.rsgauges.ModConfig.COMMON_CONFIG_SPEC);

    modEventBus.addListener(this::onSetup);
    modEventBus.addListener(this::onClientSetup);
    modEventBus.addListener(this::addCreative);
    modEventBus.addListener(wile.rsgauges.libmc.detail.Networking::init);

    PlayerBlockInteraction.init(MODID, LOGGER);
  }

  private void addCreative(BuildCreativeModeTabContentsEvent event)
  {
    if(event.getTabKey() == Registries.RSGAUGES_TAB_KEY)
    {
      // FIX: Wir iterieren NUR noch über die Items. Da addBlock in Registries.java
      // automatisch ein BlockItem generiert, landen hierdurch alle Blöcke UND
      // reine Items (wie die Link-Perle) im Tab, ohne dass es zu Duplikaten kommt.
      Registries.getRegisteredItems().forEach(item -> {
        // Wir holen den Registrierungsnamen, um die Opt-Out Config zu prüfen
        String name = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item).getPath();

        // HIER IST DER TÜRSTEHER FÜR DEN DUMMY-BLOCK:
        if (name.equals("industrialswitch_top")) {
          return; // Überspringt dieses Item und macht beim nächsten weiter!
        }

        if (!wile.rsgauges.ModConfig.isOptedOut(name)) {
          event.accept(item);
        }
      });
    }
  }

  public void onSetup(final FMLCommonSetupEvent event)
  {
    wile.rsgauges.ModConfig.apply();
    BlockCategories.update();
  }

  public void onClientSetup(final FMLClientSetupEvent event)
  {
    Overlay.register();
    ModContent.processContentClientSide(event);
  }

  @EventBusSubscriber(modid = MODID)
  public static final class ModEvents {
    @SubscribeEvent
    public static void onConfigLoad(final ModConfigEvent.Loading event)
    {
    }

    @SubscribeEvent
    public static void onConfigReload(final ModConfigEvent.Reloading event)
    {
      try {
        Auxiliaries.logger().info("Config file changed {}", event.getConfig().getFileName());
        wile.rsgauges.ModConfig.apply();
      } catch(Throwable e) {
        Auxiliaries.logger().error("Failed to load changed config: " + e.getMessage());
      }
    }
  }
}