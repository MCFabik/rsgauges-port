package wile.rsgauges;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModConfig {
  public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
  public static final ModConfigSpec COMMON_CONFIG_SPEC;

  // Config Definitionen
  private static final ModConfigSpec.BooleanValue WITHOUT_SWITCH_LINKING;
  private static final ModConfigSpec.BooleanValue WITHOUT_PULSETIME_CONFIG;
  private static final ModConfigSpec.BooleanValue WITHOUT_RIGHTCLICK_ITEM_SWITCHCONFIG;
  private static final ModConfigSpec.BooleanValue WITHOUT_SCULK_TRIGGERING;
  private static final ModConfigSpec.BooleanValue WITHOUT_SWITCH_NOOUTPUT;
  private static final ModConfigSpec.IntValue GAUGE_UPDATE_INTERVAL;
  private static final ModConfigSpec.IntValue COMPARATOR_SWITCH_UPDATE_INTERVAL;
  private static final ModConfigSpec.IntValue AUTOSWITCH_LINEAR_UPDATE_INTERVAL;
  private static final ModConfigSpec.IntValue AUTOSWITCH_VOLUMETRIC_UPDATE_INTERVAL;
  private static final ModConfigSpec.IntValue CONFIG_LEFT_CLICK_TIMEOUT;
  private static final ModConfigSpec.IntValue MAX_SWITCH_LINKING_DISTANCE;
  private static final ModConfigSpec.ConfigValue<List<? extends String>> OPT_OUTS;

  private static final Set<String> optOutSet = new HashSet<>();

  static {
    BUILDER.push("General");

    WITHOUT_SWITCH_LINKING = BUILDER.comment("Disable wireless switch linking.").define("without_switch_linking", false);
    WITHOUT_PULSETIME_CONFIG = BUILDER.comment("Disable pulse time configuration.").define("without_pulsetime_config", false);
    WITHOUT_RIGHTCLICK_ITEM_SWITCHCONFIG = BUILDER.comment("Disable right-click item switch configuration.").define("without_rightclick_item_switchconfig", false);
    WITHOUT_SCULK_TRIGGERING = BUILDER.comment("Disable sculk sensor triggering.").define("without_sculk_triggering", false);
    WITHOUT_SWITCH_NOOUTPUT = BUILDER.comment("Disable 'no output' option for switches.").define("without_switch_nooutput", false);

    GAUGE_UPDATE_INTERVAL = BUILDER.comment("Update interval for gauges in ticks.").defineInRange("gauge_update_interval", 4, 1, 100);
    COMPARATOR_SWITCH_UPDATE_INTERVAL = BUILDER.comment("Update interval for comparator switches in ticks.").defineInRange("comparator_switch_update_interval", 2, 1, 100);
    AUTOSWITCH_LINEAR_UPDATE_INTERVAL = BUILDER.comment("Update interval for linear entity detectors.").defineInRange("autoswitch_linear_update_interval", 4, 1, 100);
    AUTOSWITCH_VOLUMETRIC_UPDATE_INTERVAL = BUILDER.comment("Update interval for volumetric entity detectors.").defineInRange("autoswitch_volumetric_update_interval", 10, 1, 100);

    CONFIG_LEFT_CLICK_TIMEOUT = BUILDER.comment("Timeout for double-left-click config in ms.").defineInRange("config_left_click_timeout", 500, 100, 2000);
    MAX_SWITCH_LINKING_DISTANCE = BUILDER.comment("Maximum distance for switch linking (0=infinite).").defineInRange("max_switch_linking_distance", 48, 0, 2048);

    OPT_OUTS = BUILDER.comment("List of block registry names to disable.").defineList("optOuts", List.of(), o -> o instanceof String);

    BUILDER.pop();
    COMMON_CONFIG_SPEC = BUILDER.build();
  }

  // --- Statische Getter-Methoden ---
  public static boolean without_switch_linking() { return WITHOUT_SWITCH_LINKING.get(); }
  public static boolean without_pulsetime_config() { return WITHOUT_PULSETIME_CONFIG.get(); }
  public static boolean without_rightclick_item_switchconfig() { return WITHOUT_RIGHTCLICK_ITEM_SWITCHCONFIG.get(); }
  public static boolean without_sculk_triggering() { return WITHOUT_SCULK_TRIGGERING.get(); }
  public static boolean without_switch_nooutput() { return WITHOUT_SWITCH_NOOUTPUT.get(); }
  public static int gauge_update_interval() { return GAUGE_UPDATE_INTERVAL.get(); }
  public static int comparator_switch_update_interval() { return COMPARATOR_SWITCH_UPDATE_INTERVAL.get(); }
  public static int autoswitch_linear_update_interval() { return AUTOSWITCH_LINEAR_UPDATE_INTERVAL.get(); }
  public static int autoswitch_volumetric_update_interval() { return AUTOSWITCH_VOLUMETRIC_UPDATE_INTERVAL.get(); }
  public static int config_left_click_timeout() { return CONFIG_LEFT_CLICK_TIMEOUT.get(); }
  public static int max_switch_linking_distance() { return MAX_SWITCH_LINKING_DISTANCE.get(); }

  public static boolean isWrench(ItemStack stack) {
    if (stack.isEmpty()) return false;
    String name = stack.getItem().toString();
    return name.contains("wrench") || name.contains("hammer") || name.contains("screwdriver");
  }

  public static void apply() { updateOptouts(); }
  public static void updateOptouts() {
    optOutSet.clear();
    List<? extends String> list = OPT_OUTS.get();
    if (list != null) optOutSet.addAll(list);
  }
  public static boolean isOptedOut(String name) { return optOutSet.contains(name); }
  public static ModConfigSpec getServerConfig() { return COMMON_CONFIG_SPEC; }
}