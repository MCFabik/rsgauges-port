package wile.rsgauges;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import wile.rsgauges.blocks.*;
import wile.rsgauges.detail.ModResources;
import wile.rsgauges.items.SwitchLinkPearlItem;
import wile.rsgauges.libmc.detail.Auxiliaries;
import wile.rsgauges.libmc.detail.Registries;

public class ModContent {
  private static class detail {
    public static String MODID = "";

    public static final BlockBehaviour.Properties gauge_metallic_block_properties() {
      return BlockBehaviour.Properties.of().strength(0.5f, 15f).mapColor(MapColor.METAL).sound(SoundType.METAL).noCollission().isValidSpawn((s, w, p, e) -> false);
    }

    public static final BlockBehaviour.Properties gauge_glass_block_properties() {
      return BlockBehaviour.Properties.of().strength(0.5f, 15f).mapColor(MapColor.METAL).sound(SoundType.METAL).noOcclusion().isValidSpawn((s, w, p, e) -> false);
    }

    public static final BlockBehaviour.Properties indicator_metallic_block_properties() {
      return BlockBehaviour.Properties.of().strength(0.5f, 15f).mapColor(MapColor.METAL).sound(SoundType.METAL).lightLevel((state) -> 3).noOcclusion().isValidSpawn((s, w, p, e) -> false);
    }

    public static final BlockBehaviour.Properties indicator_glass_block_properties() {
      return BlockBehaviour.Properties.of().strength(0.5f, 15f).mapColor(MapColor.METAL).sound(SoundType.METAL).lightLevel((state) -> 3).noOcclusion().isValidSpawn((s, w, p, e) -> false);
    }

    public static final BlockBehaviour.Properties alarm_lamp_block_properties() {
      return BlockBehaviour.Properties.of().strength(0.5f, 15f).mapColor(MapColor.METAL).sound(SoundType.METAL).noOcclusion().lightLevel((state) -> state.getValue(IndicatorBlock.POWERED) ? 12 : 2).isValidSpawn((s, w, p, e) -> false);
    }

    public static final BlockBehaviour.Properties colored_sensitive_glass_block_properties() {
      return BlockBehaviour.Properties.of().strength(0.35f, 15f).mapColor(MapColor.METAL).sound(SoundType.GLASS).noOcclusion().isValidSpawn((s, w, p, e) -> false);
    }

    public static final BlockBehaviour.Properties light_emitting_sensitive_glass_block_properties() {
      return BlockBehaviour.Properties.of().strength(0.35f, 15f).mapColor(MapColor.METAL).sound(SoundType.GLASS).noOcclusion().emissiveRendering((s, w, p) -> true).lightLevel((state) -> state.getValue(SensitiveGlassBlock.POWERED) ? 15 : 0).isValidSpawn((s, w, p, e) -> false);
    }

    public static final BlockBehaviour.Properties switch_metallic_block_properties() {
      return gauge_metallic_block_properties();
    }

    public static final BlockBehaviour.Properties switch_glass_block_properties() {
      return gauge_glass_block_properties();
    }

    public static final BlockBehaviour.Properties switch_metallic_faint_light_block_properties() {
      return BlockBehaviour.Properties.of().strength(0.5f, 15f).mapColor(MapColor.METAL).sound(SoundType.METAL).lightLevel((state) -> 5);
    }

    private static Item.Properties default_item_properties() {
      return (new Item.Properties());
    }
  }

  public static void init(String modid) {
    detail.MODID = modid;
    ModResources.init();
    initTags();
    initBlocks();
    initItems();
  }

  private static void initTags() {
    Registries.addOptionalBlockTag("clay_like", "minecraft:clay");
    Registries.addOptionalBlockTag("glass_like", "minecraft:glass");
    Registries.addOptionalBlockTag("logs", "minecraft:oak_log");
    Registries.addOptionalBlockTag("ores", "minecraft:iron_ore");
    Registries.addOptionalBlockTag("planks", "minecraft:oak_planks");
    Registries.addOptionalBlockTag("plants", "minecraft:dandelion");
    Registries.addOptionalBlockTag("saplings", "minecraft:oak_sapling");
    Registries.addOptionalBlockTag("slabs", "minecraft:oak_slab");
    Registries.addOptionalBlockTag("soils", "minecraft:farmland");
    Registries.addOptionalBlockTag("stone_like", "minecraft:stone");
    Registries.addOptionalBlockTag("water_like", "minecraft:water");
    Registries.addOptionalBlockTag("wooden", "minecraft:oak_log");
  }

  public static void initBlocks() {
    Registries.addBlock("industrial_small_lever", () -> new BistableSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_BISTABLE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(4, 4, 0, 12, 15, 4),
            Auxiliaries.getPixeledAABB(4, 1, 0, 12, 12, 4)
    ), BistableSwitchBlock.class);

    Registries.addBlock("industrial_lever", () -> new BistableSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_BISTABLE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(5, 4, 0, 11, 15, 5),
            Auxiliaries.getPixeledAABB(5, 1, 0, 11, 12, 5)
    ), BistableSwitchBlock.class);

    Registries.addBlock("industrial_rotary_lever", () -> new BistableSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_BISTABLE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(1, 4, 0, 12, 12, 6),
            Auxiliaries.getPixeledAABB(1, 1, 0, 12, 12, 6)
    ), BistableSwitchBlock.class);

    Registries.addBlock("industrial_rotary_machine_switch", () -> new BistableSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_BISTABLE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(4, 4, 0, 12, 12, 1.5), null
    ), BistableSwitchBlock.class);

    Registries.addBlock("industrial_machine_switch", () -> new BistableSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_BISTABLE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(4, 4, 0, 12, 12, 1.5), null
    ), BistableSwitchBlock.class);

    Registries.addBlock("industrial_estop_switch", () -> new BistableSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_BISTABLE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT |
                    SwitchBlock.SWITCH_CONFIG_PROJECTILE_SENSE_OFF,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(5, 5, 0, 11, 11, 2.5),
            Auxiliaries.getPixeledAABB(5, 5, 0, 11, 11, 3.5)
    ), BistableSwitchBlock.class);

    Registries.addBlock("industrial_hopper_switch", () -> new BistableSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_BISTABLE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT |
                    SwitchBlock.SWITCH_DATA_WEAK,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(3, 10, 0, 13, 12, 6.7),
            Auxiliaries.getPixeledAABB(3, 10, 0, 13, 12, 3.7)
    ), BistableSwitchBlock.class);

    Registries.addBlock("industrial_button", () -> new PulseSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_PULSE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSE_EXTENDABLE | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LCLICK_RESETTABLE | SwitchBlock.SWITCH_CONFIG_PROJECTILE_SENSE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(5, 5, 0, 11, 11, 2), null
    ), PulseSwitchBlock.class);

    Registries.addBlock("industrial_fenced_button", () -> new PulseSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_PULSE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSE_EXTENDABLE |
                    SwitchBlock.SWITCH_CONFIG_LCLICK_RESETTABLE | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(5, 5, 0, 11, 11, 2), null
    ), PulseSwitchBlock.class);

    Registries.addBlock("industrial_double_pole_button", () -> new PulseSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_PULSE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSE_EXTENDABLE |
                    SwitchBlock.SWITCH_CONFIG_LCLICK_RESETTABLE | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(4, 4, 0, 12, 12, 3),
            Auxiliaries.getPixeledAABB(4, 4, 0, 12, 12, 2)
    ), PulseSwitchBlock.class);

    Registries.addBlock("industrial_foot_button", () -> new PulseSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_PULSE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSE_EXTENDABLE |
                    SwitchBlock.SWITCH_CONFIG_LCLICK_RESETTABLE | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(5, 3, 0, 11, 7, 4), null
    ), PulseSwitchBlock.class);

    Registries.addBlock("industrial_pull_handle", () -> new PulseSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_PULSE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSE_EXTENDABLE |
                    SwitchBlock.SWITCH_CONFIG_LCLICK_RESETTABLE | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(4, 4, 0, 12, 12, 2),
            Auxiliaries.getPixeledAABB(4, 4, 0, 12, 12, 2)
    ), PulseSwitchBlock.class);

    Registries.addBlock("industrial_dimmer", () -> new DimmerSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_WALLMOUNT | SwitchBlock.SWITCH_CONFIG_TOUCH_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(4, 1, 0, 12, 15, 2),
            null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.01f, 1.9f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.01f, 1.7f)
    ), DimmerSwitchBlock.class);

    Registries.addBlock("industrial_door_contact_mat", () -> new ContactMatBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_CONTACT | SwitchBlock.SWITCH_CONFIG_LATERAL |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE | SwitchBlock.SWITCH_CONFIG_TOUCH_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(1, 0, 0, 15, 1, 13), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.1f, 1.3f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.1f, 1.2f)
    ), ContactMatBlock.class);

    Registries.addBlock("industrial_contact_mat", () -> new ContactMatBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_CONTACT | SwitchBlock.SWITCH_CONFIG_LATERAL | SwitchBlock.SWITCH_CONFIG_WEAKABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(0, 0, 0, 16, 1, 16), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.1f, 1.3f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.1f, 1.2f)
    ), ContactMatBlock.class);

    Registries.addBlock("industrial_shock_sensitive_contact_mat", () -> new ContactMatBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_CONTACT | SwitchBlock.SWITCH_CONFIG_LATERAL | SwitchBlock.SWITCH_CONFIG_WEAKABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_SHOCK_SENSITIVE | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(0, 0, 0, 16, 1, 16), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.1f, 1.3f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.1f, 1.2f)
    ), ContactMatBlock.class);

    Registries.addBlock("industrial_shock_sensitive_trapdoor", () -> new TrapdoorSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_CONTACT | SwitchBlock.SWITCH_CONFIG_LATERAL_WALLMOUNT | SwitchBlock.SWITCH_CONFIG_NOT_PASSABLE |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE | SwitchBlock.SWITCH_CONFIG_SHOCK_SENSITIVE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(0, 15.6, 0, 16, 16, 16),
            Auxiliaries.getPixeledAABB(0, 2, 0, 16, 1, 0.1),
            new ModResources.BlockSoundEvent(SoundEvents.IRON_DOOR_CLOSE, 0.05f, 3.0f),
            new ModResources.BlockSoundEvent(SoundEvents.IRON_DOOR_CLOSE, 0.05f, 2.5f)
    ), TrapdoorSwitchBlock.class);

    Registries.addBlock("industrial_high_sensitive_trapdoor", () -> new TrapdoorSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_CONTACT | SwitchBlock.SWITCH_CONFIG_LATERAL_WALLMOUNT | SwitchBlock.SWITCH_CONFIG_NOT_PASSABLE |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE | SwitchBlock.SWITCH_CONFIG_SHOCK_SENSITIVE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT |
                    SwitchBlock.SWITCH_CONFIG_HIGH_SENSITIVE,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(0, 15.6, 0, 16, 16, 16),
            Auxiliaries.getPixeledAABB(0, 2, 0, 16, 1, 0.1),
            new ModResources.BlockSoundEvent(SoundEvents.IRON_DOOR_CLOSE, 0.05f, 2.5f),
            new ModResources.BlockSoundEvent(SoundEvents.IRON_DOOR_CLOSE, 0.05f, 2.0f)
    ), TrapdoorSwitchBlock.class);

    Registries.addBlock("industrial_fallthrough_detector", () -> new TrapdoorSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_CONTACT | SwitchBlock.SWITCH_CONFIG_LATERAL_WALLMOUNT | SwitchBlock.SWITCH_CONFIG_WEAKABLE |
                    SwitchBlock.SWITCH_CONFIG_INVERTABLE | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(0.1, 12.6, 0.1, 15.9, 13, 15.9),
            Auxiliaries.getPixeledAABB(0.1, 12.6, 0.1, 15.9, 13, 15.9),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.05f, 2.5f),
            null
    ), TrapdoorSwitchBlock.class);

    Registries.addBlock("industrial_day_timer", () -> new DayTimerSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_TIMER_DAYTIME |
                    SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_TOUCH_CONFIGURABLE | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(4, 4, 0, 12, 12, 1.5), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.1f, 1.3f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.1f, 1.2f)
    ), DayTimerSwitchBlock.class);

    Registries.addBlock("industrial_interval_timer", () -> new IntervalTimerSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_TIMER_INTERVAL |
                    SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_TOUCH_CONFIGURABLE | SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(4, 4, 0, 12, 12, 1.5), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.1f, 1.3f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.1f, 1.2f)
    ), IntervalTimerSwitchBlock.class);

    Registries.addBlock("industrial_entity_detector", () -> new EntityDetectorSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_SENSOR_VOLUME |
                    SwitchBlock.SWITCH_CONFIG_WALLMOUNT | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_TOUCH_CONFIGURABLE | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(6, 6, 0, 10, 10, 1), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.1f, 1.3f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.1f, 1.2f)
    ), EntityDetectorSwitchBlock.class);

    Registries.addBlock("industrial_linear_entity_detector", () -> new EntityDetectorSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_SENSOR_LINEAR |
                    SwitchBlock.SWITCH_CONFIG_WALLMOUNT | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_TOUCH_CONFIGURABLE | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(6, 6, 0, 10, 10, 1), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.1f, 1.3f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.1f, 1.2f)
    ), EntityDetectorSwitchBlock.class);

    Registries.addBlock("industrial_light_sensor", () -> new EnvironmentalSensorSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_SENSOR_LIGHT | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_TOUCH_CONFIGURABLE | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(4, 4, 0, 12, 12, 1.5), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.1f, 1.3f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.1f, 1.2f)
    ), EnvironmentalSensorSwitchBlock.class);

    Registries.addBlock("industrial_rain_sensor", () -> new EnvironmentalSensorSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_SENSOR_RAIN | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_TOUCH_CONFIGURABLE | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(4, 4, 0, 12, 12, 1.5), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.1f, 1.3f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.1f, 1.2f)
    ), EnvironmentalSensorSwitchBlock.class);

    Registries.addBlock("industrial_lightning_sensor", () -> new EnvironmentalSensorSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_SENSOR_LIGHTNING | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_TOUCH_CONFIGURABLE | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(4, 4, 0, 12, 12, 1.5), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.1f, 1.3f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.1f, 1.2f)
    ), EnvironmentalSensorSwitchBlock.class);

    Registries.addBlock("industrial_comparator_switch", () -> new ComparatorSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_TOUCH_CONFIGURABLE | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(4, 10, 0, 12, 15, 1.5), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.1f, 1.3f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.1f, 1.2f)
    ), ComparatorSwitchBlock.class);

    Registries.addBlock("industrial_block_detector", () -> new ObserverSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_SENSOR_BLOCKDETECT |
                    SwitchBlock.SWITCH_CONFIG_TOUCH_CONFIGURABLE | SwitchBlock.SWITCH_CONFIG_NOT_PASSABLE |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT |
                    SwitchBlock.SWITCH_DATA_SIDE_ENABLED_BOTTOM | SwitchBlock.SWITCH_DATA_SIDE_ENABLED_TOP |
                    SwitchBlock.SWITCH_DATA_SIDE_ENABLED_FRONT | SwitchBlock.SWITCH_DATA_SIDE_ENABLED_LEFT |
                    SwitchBlock.SWITCH_DATA_SIDE_ENABLED_RIGHT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(0.5, 0.5, 0.5, 15.5, 15.5, 15.5), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.2f, 1.3f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.2f, 1.2f)
    ), ObserverSwitchBlock.class);

    Registries.addBlock("industrial_switchlink_receiver", () -> new LinkReceiverSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_WALLMOUNT | SwitchBlock.SWITCH_CONFIG_BISTABLE |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(4, 4, 0, 12, 12, 1.5), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.01f, 1.9f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.01f, 1.7f),
            false
    ), LinkReceiverSwitchBlock.class);

    Registries.addBlock("industrial_switchlink_receiver_analog", () -> new LinkReceiverSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_WALLMOUNT | SwitchBlock.SWITCH_CONFIG_BISTABLE |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(4, 4, 0, 12, 12, 1.5), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.01f, 1.9f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.01f, 1.7f),
            true
    ), LinkReceiverSwitchBlock.class);

    Registries.addBlock("industrial_switchlink_cased_receiver", () -> new LinkReceiverSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_FULLCUBE | SwitchBlock.SWITCH_CONFIG_NOT_PASSABLE |
                    SwitchBlock.SWITCH_CONFIG_BISTABLE | SwitchBlock.SWITCH_CONFIG_WEAKABLE |
                    SwitchBlock.SWITCH_CONFIG_INVERTABLE | SwitchBlock.SWITCH_DATA_SIDE_ENABLED_ALL |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(0, 0, 0, 16, 16, 16), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.01f, 1.9f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.01f, 1.7f),
            false
    ), LinkReceiverSwitchBlock.class);

    Registries.addBlock("industrial_switchlink_pulse_receiver", () -> new LinkReceiverSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_PULSE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_LCLICK_RESETTABLE | SwitchBlock.SWITCH_CONFIG_WEAKABLE |
                    SwitchBlock.SWITCH_CONFIG_INVERTABLE | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(4, 4, 0, 12, 12, 1.5), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.01f, 1.9f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.01f, 1.7f),
            false
    ), LinkReceiverSwitchBlock.class);

    Registries.addBlock("industrial_switchlink_cased_pulse_receiver", () -> new LinkReceiverSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_FULLCUBE | SwitchBlock.SWITCH_CONFIG_NOT_PASSABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSE | SwitchBlock.SWITCH_CONFIG_WEAKABLE |
                    SwitchBlock.SWITCH_CONFIG_INVERTABLE | SwitchBlock.SWITCH_CONFIG_LCLICK_RESETTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE | SwitchBlock.SWITCH_DATA_SIDE_ENABLED_ALL |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(0, 0, 0, 16, 16, 16), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.01f, 1.9f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.01f, 1.7f),
            false
    ), LinkReceiverSwitchBlock.class);

    Registries.addBlock("industrial_switchlink_relay", () -> new LinkSenderSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_BISTABLE | SwitchBlock.SWITCH_CONFIG_LINK_SENDER |
                    SwitchBlock.SWITCH_CONFIG_INVERTABLE | SwitchBlock.SWITCH_DATA_WEAK |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(4, 4, 0, 12, 12, 1.5), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.01f, 1.9f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.01f, 1.7f),
            false
    ), LinkSenderSwitchBlock.class);

    Registries.addBlock("industrial_switchlink_relay_analog", () -> new LinkSenderSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_BISTABLE | SwitchBlock.SWITCH_CONFIG_LINK_SENDER |
                    SwitchBlock.SWITCH_CONFIG_INVERTABLE | SwitchBlock.SWITCH_DATA_WEAK |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(4, 4, 0, 12, 12, 1.5), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.01f, 1.9f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.01f, 1.7f),
            true
    ), LinkSenderSwitchBlock.class);

    Registries.addBlock("industrial_switchlink_pulse_relay", () -> new LinkSenderSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_PULSE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_LINK_SENDER |
                    SwitchBlock.SWITCH_CONFIG_INVERTABLE | SwitchBlock.SWITCH_DATA_WEAK |
                    SwitchBlock.SWITCH_CONFIG_LCLICK_RESETTABLE | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(4, 4, 0, 12, 12, 1.5), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.01f, 1.9f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.01f, 1.7f),
            false
    ), LinkSenderSwitchBlock.class);

    Registries.addBlock("industrial_knock_switch", () -> new BistableKnockSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_OPOSITE_PLACEMENT | SwitchBlock.RSBLOCK_CONFIG_FULLCUBE |
                    SwitchBlock.SWITCH_CONFIG_BISTABLE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT |
                    SwitchBlock.SWITCH_CONFIG_NOT_PASSABLE | SwitchBlock.SWITCH_DATA_SIDE_ENABLED_ALL,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(0.5, 0.5, 0.5, 15.5, 15.5, 15.5), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.2f, 1.3f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.2f, 1.2f)
    ), BistableKnockSwitchBlock.class);

    Registries.addBlock("industrial_knock_button", () -> new PulseKnockSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.RSBLOCK_CONFIG_OPOSITE_PLACEMENT | SwitchBlock.RSBLOCK_CONFIG_FULLCUBE |
                    SwitchBlock.SWITCH_CONFIG_PULSE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSE_EXTENDABLE | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LCLICK_RESETTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT |
                    SwitchBlock.SWITCH_CONFIG_NOT_PASSABLE | SwitchBlock.SWITCH_DATA_SIDE_ENABLED_ALL,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(0.5, 0.5, 0.5, 15.5, 15.5, 15.5), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.2f, 1.3f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.2f, 1.2f)
    ), PulseKnockSwitchBlock.class);

    Registries.addBlock("industrial_analog_angular_gauge", () -> new GaugeBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_WALLMOUNT,
            detail.gauge_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(2, 2, 0, 14, 14, 1)
    ), GaugeBlock.class);

    Registries.addBlock("industrial_analog_horizontal_gauge", () -> new GaugeBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_WALLMOUNT,
            detail.gauge_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(2, 4, 0, 14, 12, 1)
    ), GaugeBlock.class);

    Registries.addBlock("industrial_vertical_bar_gauge", () -> new GaugeBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_WALLMOUNT,
            detail.gauge_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(4, 2, 0, 12, 14, 1)
    ), GaugeBlock.class);

    Registries.addBlock("industrial_small_digital_gauge", () -> new GaugeBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_WALLMOUNT,
            detail.gauge_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(4, 5, 0, 12, 11, 1)
    ), GaugeBlock.class);

    Registries.addBlock("industrial_tube_gauge", () -> new GaugeBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_WALLMOUNT,
            detail.gauge_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(7, 4, 0, 9, 12, 3)
    ), GaugeBlock.class);

    Registries.addBlock("industrial_alarm_lamp", () -> new IndicatorBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | GaugeBlock.GAUGE_DATA_BLINKING | SwitchBlock.SWITCH_CONFIG_WALLMOUNT,
            detail.alarm_lamp_block_properties(),
            Auxiliaries.getPixeledAABB(6, 6, 0, 10, 10, 4)
    ), IndicatorBlock.class);

    Registries.addBlock("industrial_alarm_siren", () -> new IndicatorBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | GaugeBlock.GAUGE_DATA_BLINKING | SwitchBlock.SWITCH_CONFIG_WALLMOUNT,
            detail.indicator_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(4, 6.5, 0, 11.5, 9.5, 4),
            new ModResources.BlockSoundEvent(ModResources.ALARM_SIREN_SOUND, 2f),
            null
    ), IndicatorBlock.class);

    Registries.addBlock("industrial_green_led", () -> new IndicatorBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_WALLMOUNT,
            detail.indicator_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(6, 6, 0, 10, 10, 0.5)
    ), IndicatorBlock.class);

    Registries.addBlock("industrial_yellow_led", () -> new IndicatorBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_WALLMOUNT,
            detail.indicator_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(6, 6, 0, 10, 10, 0.5)
    ), IndicatorBlock.class);

    Registries.addBlock("industrial_red_led", () -> new IndicatorBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_WALLMOUNT,
            detail.indicator_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(6, 6, 0, 10, 10, 0.5)
    ), IndicatorBlock.class);

    Registries.addBlock("industrial_white_led", () -> new IndicatorBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_WALLMOUNT,
            detail.indicator_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(6, 6, 0, 10, 10, 0.5)
    ), IndicatorBlock.class);

    Registries.addBlock("industrial_green_blinking_led", () -> new IndicatorBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | GaugeBlock.GAUGE_DATA_BLINKING | SwitchBlock.SWITCH_CONFIG_WALLMOUNT,
            detail.indicator_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(6, 6, 0, 10, 10, 0.5)
    ), IndicatorBlock.class);

    Registries.addBlock("industrial_yellow_blinking_led", () -> new IndicatorBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | GaugeBlock.GAUGE_DATA_BLINKING | SwitchBlock.SWITCH_CONFIG_WALLMOUNT,
            detail.indicator_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(6, 6, 0, 10, 10, 0.5)
    ), IndicatorBlock.class);

    Registries.addBlock("industrial_red_blinking_led", () -> new IndicatorBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | GaugeBlock.GAUGE_DATA_BLINKING | SwitchBlock.SWITCH_CONFIG_WALLMOUNT,
            detail.indicator_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(6, 6, 0, 10, 10, 0.5)
    ), IndicatorBlock.class);

    Registries.addBlock("industrial_white_blinking_led", () -> new IndicatorBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_WALLMOUNT,
            detail.indicator_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(6, 6, 0, 10, 10, 0.5)
    ), IndicatorBlock.class);

    // -----------------------------------------------------------------------------------------------------------------
    // -- Rustic
    // -----------------------------------------------------------------------------------------------------------------

    Registries.addBlock("rustic_lever", () -> new BistableSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_BISTABLE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(6, 5, 0, 10.3, 15, 4.5),
            Auxiliaries.getPixeledAABB(6, 2, 0, 10.3, 11, 4.5)
    ), BistableSwitchBlock.class);

    Registries.addBlock("rustic_two_hinge_lever", () -> new BistableSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_BISTABLE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(2, 6, 0, 14, 13, 4.5),
            Auxiliaries.getPixeledAABB(2, 4, 0, 14, 10, 4.5)
    ), BistableSwitchBlock.class);

    Registries.addBlock("rustic_angular_lever", () -> new BistableSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_BISTABLE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(0, 10, 0, 14, 15, 4.5),
            Auxiliaries.getPixeledAABB(6, 2, 0, 14, 15, 4.5)
    ), BistableSwitchBlock.class);

    Registries.addBlock("rustic_nail_lever", () -> new BistableSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_BISTABLE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(6, 7, 0, 9, 10, 3), null
    ), BistableSwitchBlock.class);

    Registries.addBlock("rustic_button", () -> new PulseSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_PULSE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSE_EXTENDABLE | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LCLICK_RESETTABLE | SwitchBlock.SWITCH_CONFIG_PROJECTILE_SENSE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(5, 5, 0, 11, 11, 2.5), null
    ), PulseSwitchBlock.class);

    Registries.addBlock("rustic_small_button", () -> new PulseSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_PULSE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSE_EXTENDABLE | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LCLICK_RESETTABLE | SwitchBlock.SWITCH_CONFIG_PROJECTILE_SENSE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(6, 6, 0, 10, 10, 2.5), null
    ), PulseSwitchBlock.class);

    Registries.addBlock("rustic_spring_reset_chain", () -> new PulseSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_PULSE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSE_EXTENDABLE | SwitchBlock.SWITCH_CONFIG_LCLICK_RESETTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(5, 3.5, 0, 11, 15, 4), null
    ), PulseSwitchBlock.class);

    Registries.addBlock("rustic_nail_button", () -> new PulseSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_PULSE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSE_EXTENDABLE | SwitchBlock.SWITCH_CONFIG_LCLICK_RESETTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(6, 7, 0, 9, 10, 3), null
    ), PulseSwitchBlock.class);

    Registries.addBlock("rustic_door_contact_plate", () -> new ContactMatBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_CONTACT | SwitchBlock.SWITCH_CONFIG_LATERAL |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE | SwitchBlock.SWITCH_CONFIG_TOUCH_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(1, 0, 0, 15, 1, 12),
            Auxiliaries.getPixeledAABB(1, 0, 0, 15, 0.5, 12),
            new ModResources.BlockSoundEvent(SoundEvents.IRON_DOOR_CLOSE, 0.05f, 2.5f),
            new ModResources.BlockSoundEvent(SoundEvents.IRON_DOOR_CLOSE, 0.05f, 2.0f)
    ), ContactMatBlock.class);

    Registries.addBlock("rustic_contact_plate", () -> new ContactMatBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_CONTACT | SwitchBlock.SWITCH_CONFIG_LATERAL | SwitchBlock.SWITCH_CONFIG_WEAKABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(0, 0, 0, 16, 1, 16),
            Auxiliaries.getPixeledAABB(0, 0, 0, 16, 0.5, 16),
            new ModResources.BlockSoundEvent(SoundEvents.IRON_DOOR_CLOSE, 0.05f, 2.5f),
            new ModResources.BlockSoundEvent(SoundEvents.IRON_DOOR_CLOSE, 0.05f, 2.0f)
    ), ContactMatBlock.class);

    Registries.addBlock("rustic_shock_sensitive_plate", () -> new ContactMatBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_CONTACT | SwitchBlock.SWITCH_CONFIG_LATERAL | SwitchBlock.SWITCH_CONFIG_WEAKABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_SHOCK_SENSITIVE | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(0, 0, 0, 16, 1, 16),
            Auxiliaries.getPixeledAABB(0, 0, 0, 16, 0.5, 16),
            new ModResources.BlockSoundEvent(SoundEvents.IRON_DOOR_CLOSE, 0.05f, 2.5f),
            new ModResources.BlockSoundEvent(SoundEvents.IRON_DOOR_CLOSE, 0.05f, 2.0f)
    ), ContactMatBlock.class);

    Registries.addBlock("rustic_shock_sensitive_trapdoor", () -> new TrapdoorSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_CONTACT | SwitchBlock.SWITCH_CONFIG_LATERAL_WALLMOUNT | SwitchBlock.SWITCH_CONFIG_NOT_PASSABLE |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE | SwitchBlock.SWITCH_CONFIG_SHOCK_SENSITIVE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(0, 15.6, 0, 16, 16, 16),
            Auxiliaries.getPixeledAABB(0, 2.0, 0, 16, 16, 0.1),
            new ModResources.BlockSoundEvent(SoundEvents.IRON_DOOR_CLOSE, 0.05f, 2.5f),
            new ModResources.BlockSoundEvent(SoundEvents.IRON_DOOR_CLOSE, 0.05f, 2.0f)
    ), TrapdoorSwitchBlock.class);

    Registries.addBlock("rustic_high_sensitive_trapdoor", () -> new TrapdoorSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_CONTACT | SwitchBlock.SWITCH_CONFIG_LATERAL_WALLMOUNT | SwitchBlock.SWITCH_CONFIG_NOT_PASSABLE |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE | SwitchBlock.SWITCH_CONFIG_SHOCK_SENSITIVE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_HIGH_SENSITIVE |
                    SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(0, 15.6, 0, 16, 16, 16),
            Auxiliaries.getPixeledAABB(0, 2.0, 0, 16, 16, 0.1),
            new ModResources.BlockSoundEvent(SoundEvents.IRON_DOOR_CLOSE, 0.05f, 2.5f),
            new ModResources.BlockSoundEvent(SoundEvents.IRON_DOOR_CLOSE, 0.05f, 2.0f)
    ), TrapdoorSwitchBlock.class);

    Registries.addBlock("rustic_fallthrough_detector", () -> new TrapdoorSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_CONTACT | SwitchBlock.SWITCH_CONFIG_LATERAL_WALLMOUNT | SwitchBlock.SWITCH_CONFIG_WEAKABLE |
                    SwitchBlock.SWITCH_CONFIG_INVERTABLE | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(0, 12.6, 0, 16, 13, 16),
            Auxiliaries.getPixeledAABB(0, 12.6, 0, 16, 13, 16),
            new ModResources.BlockSoundEvent(SoundEvents.IRON_DOOR_CLOSE, 0.05f, 2.5f),
            null
    ), TrapdoorSwitchBlock.class);

    Registries.addBlock("rustic_semaphore", () -> new IndicatorBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_WALLMOUNT,
            detail.indicator_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(3, 4, 0, 13, 11, 1),
            null,
            null
    ), IndicatorBlock.class);

    // -----------------------------------------------------------------------------------------------------------------
    // -- Glass
    // -----------------------------------------------------------------------------------------------------------------

    Registries.addBlock("glass_rotary_switch", () -> new BistableSwitchBlock(
            SwitchBlock.SWITCH_CONFIG_TRANSLUCENT |
                    SwitchBlock.SWITCH_CONFIG_BISTABLE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_faint_light_block_properties(),
            Auxiliaries.getPixeledAABB(5.5, 5.5, 0, 10.5, 10.5, 0.5), null
    ), BistableSwitchBlock.class);

    Registries.addBlock("glass_touch_switch", () -> new BistableSwitchBlock(
            SwitchBlock.SWITCH_CONFIG_TRANSLUCENT |
                    SwitchBlock.SWITCH_CONFIG_BISTABLE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_faint_light_block_properties(),
            Auxiliaries.getPixeledAABB(5.5, 5.5, 0, 10.5, 10.5, 0.5), null
    ), BistableSwitchBlock.class);

    Registries.addBlock("glass_button", () -> new PulseSwitchBlock(
            SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT |
                    SwitchBlock.SWITCH_CONFIG_PULSE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSE_EXTENDABLE | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LCLICK_RESETTABLE |
                    SwitchBlock.SWITCH_CONFIG_TRANSLUCENT |
                    SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_faint_light_block_properties(),
            Auxiliaries.getPixeledAABB(5.5, 5.5, 0, 10.5, 10.5, 0.5), null
    ), PulseSwitchBlock.class);

    Registries.addBlock("glass_small_button", () -> new PulseSwitchBlock(
            SwitchBlock.SWITCH_CONFIG_TRANSLUCENT |
                    SwitchBlock.SWITCH_CONFIG_PULSE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSE_EXTENDABLE | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LCLICK_RESETTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_faint_light_block_properties(),
            Auxiliaries.getPixeledAABB(5.5, 5.5, 0, 10.5, 10.5, 0.5), null
    ), PulseSwitchBlock.class);

    Registries.addBlock("glass_touch_button", () -> new PulseSwitchBlock(
            SwitchBlock.SWITCH_CONFIG_TRANSLUCENT |
                    SwitchBlock.SWITCH_CONFIG_PULSE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSE_EXTENDABLE | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LCLICK_RESETTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_faint_light_block_properties(),
            Auxiliaries.getPixeledAABB(5.5, 5.5, 0, 10.5, 10.5, 0.5), null
    ), PulseSwitchBlock.class);

    Registries.addBlock("glass_door_contact_mat", () -> new ContactMatBlock(
            SwitchBlock.SWITCH_CONFIG_TRANSLUCENT |
                    SwitchBlock.SWITCH_CONFIG_CONTACT | SwitchBlock.SWITCH_CONFIG_LATERAL | SwitchBlock.SWITCH_CONFIG_TOUCH_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(0, 0, 0, 16, 0.25, 16), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.0f, 1.3f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.0f, 1.2f)
    ), ContactMatBlock.class);

    Registries.addBlock("glass_contact_mat", () -> new ContactMatBlock(
            SwitchBlock.SWITCH_CONFIG_TRANSLUCENT |
                    SwitchBlock.SWITCH_CONFIG_CONTACT | SwitchBlock.SWITCH_CONFIG_LATERAL |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(0, 0, 0, 16, 0.25, 16), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.0f, 1.3f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.0f, 1.2f)
    ), ContactMatBlock.class);

    Registries.addBlock("glass_day_timer", () -> new DayTimerSwitchBlock(
            SwitchBlock.SWITCH_CONFIG_TRANSLUCENT |
                    SwitchBlock.SWITCH_CONFIG_TIMER_DAYTIME | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_TOUCH_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(5.5, 5.5, 0, 10.5, 10.5, 0.1), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.0f, 1.3f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.0f, 1.2f)
    ), DayTimerSwitchBlock.class);

    Registries.addBlock("glass_interval_timer", () -> new IntervalTimerSwitchBlock(
            SwitchBlock.SWITCH_CONFIG_TRANSLUCENT |
                    SwitchBlock.SWITCH_CONFIG_TIMER_INTERVAL | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_TOUCH_CONFIGURABLE | SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(5.5, 5.5, 0, 10.5, 10.5, 0.1), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.0f, 1.3f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.0f, 1.2f)
    ), IntervalTimerSwitchBlock.class);

    Registries.addBlock("glass_entity_detector", () -> new EntityDetectorSwitchBlock(
            SwitchBlock.SWITCH_CONFIG_TRANSLUCENT | SwitchBlock.SWITCH_CONFIG_SENSOR_VOLUME |
                    SwitchBlock.SWITCH_CONFIG_WALLMOUNT | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_TOUCH_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(5.5, 5.5, 0, 10.5, 10.5, 0.1), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.0f, 1.3f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.0f, 1.2f)
    ), EntityDetectorSwitchBlock.class);

    Registries.addBlock("glass_linear_entity_detector", () -> new EntityDetectorSwitchBlock(
            SwitchBlock.SWITCH_CONFIG_TRANSLUCENT | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_SENSOR_LINEAR | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_TOUCH_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(5.5, 5.5, 0, 10.5, 10.5, 0.1), null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.0f, 1.3f),
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.0f, 1.2f)
    ), EntityDetectorSwitchBlock.class);

    Registries.addBlock("glass_vertical_bar_gauge", () -> new GaugeBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_WALLMOUNT,
            detail.gauge_glass_block_properties(),
            Auxiliaries.getPixeledAABB(7, 3.7, 0, 10, 12, 0.4)
    ), GaugeBlock.class);

    // -----------------------------------------------------------------------------------------------------------------
    // -- Old Fancy
    // -----------------------------------------------------------------------------------------------------------------

    Registries.addBlock("oldfancy_bistableswitch1", () -> new BistableSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_BISTABLE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(6, 6.5, 0, 10.3, 13.5, 4.5),
            Auxiliaries.getPixeledAABB(6, 3.5, 0, 10.3, 10.0, 4.5)
    ), BistableSwitchBlock.class);

    Registries.addBlock("oldfancy_bistableswitch2", () -> new BistableSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_BISTABLE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(2.5, 6.0, 0, 9.7, 10, 4.5),
            Auxiliaries.getPixeledAABB(4.5, 3.5, 0, 9.2, 10, 4.5)
    ), BistableSwitchBlock.class);

    Registries.addBlock("oldfancy_button", () -> new PulseSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_PULSE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSE_EXTENDABLE | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LCLICK_RESETTABLE | SwitchBlock.SWITCH_CONFIG_PROJECTILE_SENSE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(6, 6, 0, 10, 10, 1.5), null
    ), PulseSwitchBlock.class);

    Registries.addBlock("oldfancy_spring_reset_chain", () -> new PulseSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_PULSE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSE_EXTENDABLE | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LCLICK_RESETTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(6.5, 4.8, 0, 9.5, 13, 4),
            Auxiliaries.getPixeledAABB(6.5, 3.8, 0, 9.5, 12, 4)
    ), PulseSwitchBlock.class);

    Registries.addBlock("oldfancy_small_button", () -> new PulseSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_PULSE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSE_EXTENDABLE | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LCLICK_RESETTABLE | SwitchBlock.SWITCH_CONFIG_PROJECTILE_SENSE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(7, 7, 0, 9, 9, 1.5), null
    ), PulseSwitchBlock.class);

    // -----------------------------------------------------------------------------------------------------------------
    // -- Other
    // -----------------------------------------------------------------------------------------------------------------

    Registries.addBlock("yellow_power_plant", () -> new PowerPlantBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_CONTACT | SwitchBlock.SWITCH_CONFIG_LATERAL | SwitchBlock.SWITCH_CONFIG_WEAKABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(5, 0, 5, 11, 9, 11), null,
            new ModResources.BlockSoundEvent(SoundEvents.GRASS_BREAK, 0.09f, 3.6f),
            new ModResources.BlockSoundEvent(SoundEvents.GRASS_BREAK, 0.04f, 3.0f)
    ), PowerPlantBlock.class);

    Registries.addBlock("red_power_plant", () -> new PowerPlantBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_CONTACT | SwitchBlock.SWITCH_CONFIG_LATERAL | SwitchBlock.SWITCH_CONFIG_WEAKABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(5, 0, 5, 11, 9, 11), null,
            new ModResources.BlockSoundEvent(SoundEvents.GRASS_BREAK, 0.09f, 3.6f),
            new ModResources.BlockSoundEvent(SoundEvents.GRASS_BREAK, 0.04f, 3.0f)
    ), PowerPlantBlock.class);

    Registries.addBlock("light_switch", () -> new BistableSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_BISTABLE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(7, 6, 0, 9, 10, 1.5), null
    ), BistableSwitchBlock.class);

    Registries.addBlock("arrow_target", () -> new PulseSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_PULSE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LCLICK_RESETTABLE |
                    SwitchBlock.SWITCH_CONFIG_PROJECTILE_SENSE | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(5, 5, 0, 11, 11, 1), null
    ), PulseSwitchBlock.class);

    Registries.addBlock("valve_wheel_switch", () -> new BistableSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_BISTABLE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(4, 4, 0, 12, 12, 3.5), null
    ), BistableSwitchBlock.class);

    Registries.addBlock("elevator_button", () -> new ElevatorSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_PULSE | SwitchBlock.SWITCH_CONFIG_WALLMOUNT |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_PULSE_EXTENDABLE | SwitchBlock.SWITCH_CONFIG_PULSETIME_CONFIGURABLE |
                    SwitchBlock.SWITCH_CONFIG_LCLICK_RESETTABLE | SwitchBlock.SWITCH_CONFIG_PROJECTILE_SENSE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_faint_light_block_properties(),
            Auxiliaries.getPixeledAABB(4, 4, 0, 12, 12, 1), null
    ), ElevatorSwitchBlock.class);

    Registries.addBlock("door_sensor_switch", () -> new DoorSensorSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT |
                    SwitchBlock.SWITCH_CONFIG_PULSE | SwitchBlock.SWITCH_CONFIG_LATERAL_WALLMOUNT | SwitchBlock.SWITCH_CONFIG_WEAKABLE |
                    SwitchBlock.SWITCH_CONFIG_INVERTABLE | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(5, 0, 0, 11, 1, 1.5),
            null,
            new ModResources.BlockSoundEvent(SoundEvents.LEVER_CLICK, 0.05f, 2.5f),
            null
    ), DoorSensorSwitchBlock.class);

    Registries.addBlock("sensitive_glass_block", () -> new SensitiveGlassBlock(
            detail.light_emitting_sensitive_glass_block_properties()
    ), SensitiveGlassBlock.class);

    Registries.addBlock("stained_sensitiveglass", () -> new SensitiveGlassBlock(
            detail.colored_sensitive_glass_block_properties()
    ), SensitiveGlassBlock.class);

// 1. DUMMY-BLOCK
    Registries.addBlock("industrialswitch_top", () -> new Block(
            BlockBehaviour.Properties.of().mapColor(MapColor.NONE).noOcclusion().sound(SoundType.METAL).strength(0.5f, 15f).pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY)
    ) {
      @Override
      protected net.minecraft.world.InteractionResult useWithoutItem(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, net.minecraft.world.entity.player.Player player, net.minecraft.world.phys.BlockHitResult hit) {
        net.minecraft.core.BlockPos below = pos.below();
        net.minecraft.world.level.block.state.BlockState stateBelow = level.getBlockState(below);
        if (stateBelow.getBlock() instanceof BistableSwitchBlock) {
          return stateBelow.useWithoutItem(level, player, hit.withPosition(below));
        }
        return net.minecraft.world.InteractionResult.PASS;
      }

      @Override
      public net.minecraft.world.level.block.state.BlockState playerWillDestroy(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.entity.player.Player player) {
        net.minecraft.core.BlockPos below = pos.below();
        if (level.getBlockState(below).is(ModContent.getBlock("industrialswitch"))) {
          level.destroyBlock(below, true, player);
        }
        return super.playerWillDestroy(level, pos, state, player);
      }

      @Override
      public net.minecraft.world.phys.shapes.VoxelShape getShape(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.BlockGetter level, net.minecraft.core.BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) {
        net.minecraft.core.BlockPos below = pos.below();
        net.minecraft.world.level.block.state.BlockState stateBelow = level.getBlockState(below);
        if (stateBelow.getBlock() instanceof BistableSwitchBlock) {
          return stateBelow.getShape(level, below, context);
        }
        return net.minecraft.world.level.block.Block.box(0, 0, 0, 16, 16, 8);
      }

      @Override
      public net.minecraft.world.level.block.RenderShape getRenderShape(net.minecraft.world.level.block.state.BlockState state) {
        return net.minecraft.world.level.block.RenderShape.INVISIBLE;
      }

      @Override
      public net.minecraft.world.item.ItemStack getCloneItemStack(net.minecraft.world.level.LevelReader level, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        return new net.minecraft.world.item.ItemStack(ModContent.getBlock("industrialswitch"));
      }
    }, Block.class);

    // 2. DER ECHTE SCHALTER (Jetzt mit UP und DOWN Unterstützung)
    Registries.addBlock("industrialswitch", () -> new BistableSwitchBlock(
            SwitchBlock.RSBLOCK_CONFIG_CUTOUT | SwitchBlock.SWITCH_CONFIG_BISTABLE |
                    SwitchBlock.SWITCH_CONFIG_WEAKABLE | SwitchBlock.SWITCH_CONFIG_INVERTABLE |
                    SwitchBlock.SWITCH_CONFIG_LINK_TARGET_SUPPORT | SwitchBlock.SWITCH_CONFIG_LINK_SOURCE_SUPPORT,
            detail.switch_metallic_block_properties(),
            Auxiliaries.getPixeledAABB(0, 0, 0, 16, 16, 16),
            null
    ) {
      @Override
      public net.minecraft.world.phys.shapes.VoxelShape getShape(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.BlockGetter level, net.minecraft.core.BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) {
        net.minecraft.core.Direction facing = net.minecraft.core.Direction.NORTH;

        if (state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING)) {
          facing = state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
        } else if (state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING)) {
          facing = state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING);
        }

        switch (facing) {
          case NORTH: return net.minecraft.world.level.block.Block.box(0, 0, 0, 16, 16, 8);
          case SOUTH: return net.minecraft.world.level.block.Block.box(0, 0, 8, 16, 16, 16);
          case WEST:  return net.minecraft.world.level.block.Block.box(0, 0, 0, 8, 16, 16);
          case EAST:  return net.minecraft.world.level.block.Block.box(8, 0, 0, 16, 16, 16);
          case UP:    return net.minecraft.world.level.block.Block.box(0, 0, 8, 16, 16, 16);
          case DOWN:  return net.minecraft.world.level.block.Block.box(0, 0, 8, 16, 16, 16);
          default:    return net.minecraft.world.level.block.Block.box(0, 0, 0, 16, 16, 8);
        }
      }

      @Override
      public void setPlacedBy(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.entity.LivingEntity placer, net.minecraft.world.item.ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.getBlockState(pos.above()).canBeReplaced()) {
          level.setBlock(pos.above(), ModContent.getBlock("industrialswitch_top").defaultBlockState(), 3);
        }
      }

      @Override
      public net.minecraft.world.level.block.state.BlockState playerWillDestroy(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.entity.player.Player player) {
        if (level.getBlockState(pos.above()).is(ModContent.getBlock("industrialswitch_top"))) {
          level.setBlock(pos.above(), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
        }
        return super.playerWillDestroy(level, pos, state, player);
      }
    }, BistableSwitchBlock.class);

    // --- TILE ENTITIES ---
    Registries.addBlockEntityType("te_gauge", AbstractGaugeBlock.GaugeTileEntity::new,
            "industrial_analog_angular_gauge", "industrial_analog_horizontal_gauge",
            "industrial_vertical_bar_gauge", "industrial_small_digital_gauge",
            "industrial_tube_gauge", "glass_vertical_bar_gauge", "rustic_semaphore",
            "industrial_alarm_lamp", "industrial_alarm_siren", "industrial_green_led",
            "industrial_yellow_led", "industrial_red_led", "industrial_white_led",
            "industrial_green_blinking_led", "industrial_yellow_blinking_led",
            "industrial_red_blinking_led", "industrial_white_blinking_led"
    );

    Registries.addBlockEntityType("te_switch", SwitchBlock.SwitchTileEntity::new,
            "industrial_small_lever", "industrial_lever", "industrial_rotary_lever",
            "industrial_rotary_machine_switch", "industrial_machine_switch", "industrial_estop_switch",
            "industrial_hopper_switch", "industrial_button", "industrial_fenced_button",
            "industrial_double_pole_button", "industrial_foot_button", "industrial_pull_handle",
            "industrial_dimmer", "rustic_lever", "rustic_two_hinge_lever", "rustic_angular_lever",
            "rustic_nail_lever", "rustic_button", "rustic_small_button", "rustic_spring_reset_chain",
            "rustic_nail_button", "light_switch", "arrow_target", "valve_wheel_switch", "elevator_button",
            "glass_rotary_switch", "glass_touch_switch", "glass_button", "glass_small_button",
            "glass_touch_button", "oldfancy_bistableswitch1", "oldfancy_bistableswitch2",
            "oldfancy_button", "oldfancy_spring_reset_chain", "oldfancy_small_button",
            "industrial_switchlink_receiver", "industrial_switchlink_receiver_analog",
            "industrial_switchlink_cased_receiver", "industrial_switchlink_pulse_receiver",
            "industrial_switchlink_cased_pulse_receiver", "industrial_switchlink_relay",
            "industrial_switchlink_relay_analog", "industrial_switchlink_pulse_relay",
            "industrial_knock_switch", "industrial_knock_button", "industrialswitch"
    );

    Registries.addBlockEntityType("te_contact_switch", ContactSwitchBlock.ContactSwitchTileEntity::new,
            "industrial_door_contact_mat", "industrial_contact_mat", "industrial_shock_sensitive_contact_mat",
            "industrial_shock_sensitive_trapdoor", "industrial_high_sensitive_trapdoor",
            "industrial_fallthrough_detector", "yellow_power_plant", "red_power_plant",
            "rustic_door_contact_plate", "rustic_contact_plate", "rustic_shock_sensitive_plate",
            "rustic_shock_sensitive_trapdoor", "rustic_high_sensitive_trapdoor",
            "rustic_fallthrough_detector", "glass_door_contact_mat", "glass_contact_mat"
    );

    Registries.addBlockEntityType("te_detector_switch", EntityDetectorSwitchBlock.DetectorSwitchTileEntity::new,
            "industrial_entity_detector", "industrial_linear_entity_detector", "glass_entity_detector",
            "glass_linear_entity_detector"
    );

    Registries.addBlockEntityType("te_envsensor_switch", EnvironmentalSensorSwitchBlock.EnvironmentalSensorSwitchTileEntity::new,
            "industrial_light_sensor", "industrial_rain_sensor", "industrial_lightning_sensor"
    );

    Registries.addBlockEntityType("te_daytimer_switch", DayTimerSwitchBlock.DayTimerSwitchTileEntity::new,
            "industrial_day_timer", "glass_day_timer"
    );

    Registries.addBlockEntityType("te_intervaltimer_switch", IntervalTimerSwitchBlock.IntervalTimerSwitchTileEntity::new,
            "industrial_interval_timer", "glass_interval_timer"
    );

    Registries.addBlockEntityType("te_comparator_switch", ComparatorSwitchBlock.ComparatorSwitchTileEntity::new,
            "industrial_comparator_switch"
    );

    Registries.addBlockEntityType("te_observer_switch", ObserverSwitchBlock.ObserverSwitchTileEntity::new,
            "industrial_block_detector"
    );

    Registries.addBlockEntityType("te_doorsensor_switch", DoorSensorSwitchBlock.DoorSensorSwitchTileEntity::new,
            "door_sensor_switch"
    );
  }

  public static void initItems() {
    Registries.addItem("switchlink_pearl", () -> new SwitchLinkPearlItem(detail.default_item_properties()));
  }

  public static Block getBlock(String name) { return Registries.getBlock(name); }
  public static Item getItem(String name) { return Registries.getItem(name); }
  public static TagKey<Block> getBlockTagKey(String name) { return net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, net.minecraft.resources.ResourceLocation.parse(name)); }
  public static TagKey<Item> getItemTagKey(String name) { return net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse(name)); }
  public static BlockEntityType<?> getBlockEntityTypeOfBlock(String block_name) { return Registries.getBlockEntityType(block_name); }

  @OnlyIn(Dist.CLIENT)
  public static void processContentClientSide(final FMLClientSetupEvent event) {}
}