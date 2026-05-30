package wile.rsgauges.libmc.detail;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class Registries {
  private static String MODID = "";
  private static DeferredRegister.Blocks BLOCKS;
  private static DeferredRegister.Items ITEMS;
  private static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES;
  private static DeferredRegister<CreativeModeTab> CREATIVE_TABS;
  public static DeferredRegister<MenuType<?>> MENUS;

  // Sound Registry für NeoForge 1.21.1
  public static DeferredRegister<SoundEvent> SOUND_EVENTS;

  public static ResourceKey<CreativeModeTab> RSGAUGES_TAB_KEY;
  public static DeferredHolder<CreativeModeTab, CreativeModeTab> RSGAUGES_TAB;

  public static void init(String modid, String tabIconBlockName) {
    MODID = modid;
    IEventBus bus = ModLoadingContext.get().getActiveContainer().getEventBus();

    BLOCKS = DeferredRegister.createBlocks(MODID);
    ITEMS = DeferredRegister.createItems(MODID);
    BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID);
    CREATIVE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, MODID);
    SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, MODID);
    MENUS = DeferredRegister.create(BuiltInRegistries.MENU, MODID);

    RSGAUGES_TAB_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), ResourceLocation.fromNamespaceAndPath(MODID, "rsgauges_tab"));

    RSGAUGES_TAB = CREATIVE_TABS.register("rsgauges_tab", () -> CreativeModeTab.builder()
            .title(Component.literal("Gauges and switches"))
            .icon(() -> {
              var holder = BLOCKS.getEntries().stream()
                      .filter(h -> h.getId().getPath().equals(tabIconBlockName))
                      .findFirst()
                      .orElse(BLOCKS.getEntries().iterator().next());
              return new ItemStack(holder.get());
            })
            .build()
    );

    BLOCKS.register(bus);
    ITEMS.register(bus);
    BLOCK_ENTITY_TYPES.register(bus);
    CREATIVE_TABS.register(bus);
    SOUND_EVENTS.register(bus);
    MENUS.register(bus);
  }

  public static <T extends Block> DeferredHolder<Block, T> addBlock(String name, Supplier<T> blockFactory, Class<T> blockClass) {
    DeferredHolder<Block, T> block = BLOCKS.register(name, blockFactory);
    ITEMS.register(name, () -> new net.minecraft.world.item.BlockItem(block.get(), new net.minecraft.world.item.Item.Properties()));
    return block;
  }

  public static <T extends Item> DeferredHolder<Item, T> addItem(String name, Supplier<T> itemFactory) {
    return ITEMS.register(name, itemFactory);
  }

  public static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> addBlockEntityType(String name, BlockEntityType.BlockEntitySupplier<T> factory, String... blocks) {
    return BLOCK_ENTITY_TYPES.register(name, () -> {
      Block[] blockArray = new Block[blocks.length];
      for (int i = 0; i < blocks.length; i++) {
        blockArray[i] = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(MODID, blocks[i]));
      }
      return BlockEntityType.Builder.of(factory, blockArray).build(null);
    });
  }

  public static Block getBlock(String name) {
    return BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(MODID, name));
  }

  public static Item getItem(String name) {
    return BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MODID, name));
  }

  /**
   * Intelligente Abfrage der BlockEntity-Typen, die NullPointer-Crashes verhindert,
   * auch wenn der alte Block-Code nach falschen Namen fragt.
   */
  public static BlockEntityType<?> getBlockEntityType(String name) {
    // 1. Reguläre Abfrage
    ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(MODID, name);
    BlockEntityType<?> type = BuiltInRegistries.BLOCK_ENTITY_TYPE.get(loc);
    if (type != null) return type;

    // 2. Tippfehler-Korrektur (tet_ vs te_)
    if (name.startsWith("tet_")) {
      type = BuiltInRegistries.BLOCK_ENTITY_TYPE.get(ResourceLocation.fromNamespaceAndPath(MODID, name.replace("tet_", "te_")));
      if (type != null) return type;
    }

    // 3. Fallback: Blockname anstelle von TileEntity-Name übergeben?
    // Holt den Block und prüft, welche TileEntity ihn unterstützt.
    Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(MODID, name));
    if (block != null && block != Blocks.AIR) {
      for (var holder : BLOCK_ENTITY_TYPES.getEntries()) {
        BlockEntityType<?> bet = holder.get();
        if (bet != null && bet.isValid(block.defaultBlockState())) {
          return bet;
        }
      }
    }

    // 4. Ultimativer Fallback per Keyword, damit das Spiel unter keinen Umständen abstürzt!
    if (name.contains("envsensor") || name.contains("light_sensor") || name.contains("rain") || name.contains("lightning")) return BuiltInRegistries.BLOCK_ENTITY_TYPE.get(ResourceLocation.fromNamespaceAndPath(MODID, "te_envsensor_switch"));
    if (name.contains("day_timer") || name.contains("daytimer")) return BuiltInRegistries.BLOCK_ENTITY_TYPE.get(ResourceLocation.fromNamespaceAndPath(MODID, "te_daytimer_switch"));
    if (name.contains("interval")) return BuiltInRegistries.BLOCK_ENTITY_TYPE.get(ResourceLocation.fromNamespaceAndPath(MODID, "te_intervaltimer_switch"));
    if (name.contains("comparator")) return BuiltInRegistries.BLOCK_ENTITY_TYPE.get(ResourceLocation.fromNamespaceAndPath(MODID, "te_comparator_switch"));
    if (name.contains("observer") || name.contains("block_detector")) return BuiltInRegistries.BLOCK_ENTITY_TYPE.get(ResourceLocation.fromNamespaceAndPath(MODID, "te_observer_switch"));
    if (name.contains("doorsensor") || name.contains("door_sensor")) return BuiltInRegistries.BLOCK_ENTITY_TYPE.get(ResourceLocation.fromNamespaceAndPath(MODID, "te_doorsensor_switch"));
    if (name.contains("detector")) return BuiltInRegistries.BLOCK_ENTITY_TYPE.get(ResourceLocation.fromNamespaceAndPath(MODID, "te_detector_switch"));
    if (name.contains("contact") || name.contains("trapdoor") || name.contains("plate") || name.contains("fallthrough") || name.contains("power_plant")) return BuiltInRegistries.BLOCK_ENTITY_TYPE.get(ResourceLocation.fromNamespaceAndPath(MODID, "te_contact_switch"));
    if (name.contains("gauge") || name.contains("led") || name.contains("lamp") || name.contains("siren") || name.contains("semaphore")) return BuiltInRegistries.BLOCK_ENTITY_TYPE.get(ResourceLocation.fromNamespaceAndPath(MODID, "te_gauge"));

    // Rettung: Ein gewöhnlicher Schalter tut's zur Not auch.
    return BuiltInRegistries.BLOCK_ENTITY_TYPE.get(ResourceLocation.fromNamespaceAndPath(MODID, "te_switch"));
  }

  public static List<DeferredHolder<Block, ? extends Block>> getBlockHolders() {
    return BLOCKS.getEntries().stream().toList();
  }

  public static List<Item> getRegisteredItems() {
    return ITEMS.getEntries().stream().map(h -> (Item) h.get()).toList();
  }

  public static void addOptionalBlockTag(String name, String fallback) {}
}