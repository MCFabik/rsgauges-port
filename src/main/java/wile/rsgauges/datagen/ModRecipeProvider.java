package wile.rsgauges.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import wile.rsgauges.libmc.detail.Registries;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    private Item getItem(String id) {
        if(id.startsWith("minecraft:")) {
            return BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
        }
        return Registries.getItem(id.replace("rsgauges:", ""));
    }

    private TagKey<Item> getTag(String id) {
        return ItemTags.create(ResourceLocation.parse(id));
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:glass_vertical_bar_gauge"), 1)
            .pattern("NN")
            .pattern("RC")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:glass"))
            .define('C', getItem("minecraft:comparator"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "glass_vertical_bar_gauge_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:industrial_analog_angular_gauge"), 1)
            .pattern("NN")
            .pattern("RC")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:redstone"))
            .define('C', getItem("minecraft:comparator"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_analog_angular_gauge_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_analog_angular_gauge"), 1)
            .requires(getItem("rsgauges:industrial_analog_horizontal_gauge"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_analog_angular_gauge_recipe_backcycle"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_analog_horizontal_gauge"), 1)
            .requires(getItem("rsgauges:industrial_tube_gauge"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_analog_horizontal_gauge_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_small_digital_gauge"), 1)
            .requires(getItem("rsgauges:industrial_vertical_bar_gauge"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_small_digital_gauge_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_tube_gauge"), 1)
            .requires(getItem("rsgauges:industrial_small_digital_gauge"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_tube_gauge_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_vertical_bar_gauge"), 1)
            .requires(getItem("rsgauges:industrial_analog_angular_gauge"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_vertical_bar_gauge_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:glass_button"), 1)
            .pattern("NN")
            .pattern("GL")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('G', getItem("minecraft:glass"))
            .define('L', getTag("rsgauges:buttons"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "glass_button_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:glass_button"), 1)
            .requires(getItem("rsgauges:glass_touch_button"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "glass_button_recipe_backcycle"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:glass_contact_mat"), 1)
            .pattern("NN")
            .pattern("GP")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('G', getItem("minecraft:glass"))
            .define('P', getTag("rsgauges:pressure_plates"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "glass_contact_mat_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:glass_contact_mat"), 1)
            .requires(getItem("rsgauges:glass_door_contact_mat"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "glass_contact_mat_recipe_backcycle"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:glass_day_timer"), 1)
            .pattern("NN")
            .pattern("GD")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('G', getItem("minecraft:glass"))
            .define('D', getItem("minecraft:clock"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "glass_day_timer_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:glass_day_timer"), 1)
            .requires(getItem("rsgauges:glass_interval_timer"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "glass_day_timer_recipe_backcycle"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:glass_door_contact_mat"), 1)
            .requires(getItem("rsgauges:glass_contact_mat"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "glass_door_contact_mat_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:glass_entity_detector"), 1)
            .pattern("NN")
            .pattern("GE")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('G', getItem("minecraft:glass"))
            .define('E', getItem("minecraft:ender_eye"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "glass_entity_detector_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:glass_entity_detector"), 1)
            .requires(getItem("rsgauges:glass_linear_entity_detector"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "glass_entity_detector_recipe_backcycle"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:glass_interval_timer"), 1)
            .requires(getItem("rsgauges:glass_day_timer"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "glass_interval_timer_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:glass_linear_entity_detector"), 1)
            .requires(getItem("rsgauges:glass_entity_detector"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "glass_linear_entity_detector_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:glass_rotary_switch"), 1)
            .pattern("NN")
            .pattern("GL")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('G', getItem("minecraft:glass"))
            .define('L', getItem("minecraft:lever"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "glass_rotary_switch_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:glass_rotary_switch"), 1)
            .requires(getItem("rsgauges:glass_touch_switch"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "glass_rotary_switch_recipe_backcycle"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:glass_small_button"), 1)
            .requires(getItem("rsgauges:glass_button"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "glass_small_button_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:glass_touch_button"), 1)
            .requires(getItem("rsgauges:glass_small_button"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "glass_touch_button_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:glass_touch_switch"), 1)
            .requires(getItem("rsgauges:glass_rotary_switch"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "glass_touch_switch_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:industrial_alarm_lamp"), 1)
            .pattern("NN")
            .pattern("RL")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:redstone"))
            .define('L', getItem("minecraft:redstone_lamp"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_alarm_lamp_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:industrial_alarm_siren"), 1)
            .pattern("NN")
            .pattern("RB")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:redstone"))
            .define('B', getItem("minecraft:note_block"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_alarm_siren_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:industrial_block_detector"), 1)
            .pattern("NBN")
            .pattern("NSN")
            .pattern("NRN")
            .define('B', getItem("minecraft:stone"))
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:redstone"))
            .define('S', getItem("minecraft:ender_eye"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_block_detector_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:industrial_button"), 1)
            .pattern("BN")
            .pattern("RN")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:redstone"))
            .define('B', getTag("rsgauges:buttons"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_button_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_button"), 1)
            .requires(getItem("rsgauges:industrial_pull_handle"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_button_recipe_backcycle"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:industrial_comparator_switch"), 1)
            .pattern("NNN")
            .pattern("RRC")
            .pattern("NNN")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:redstone"))
            .define('C', getItem("minecraft:comparator"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_comparator_switch_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_contact_mat"), 1)
            .requires(getItem("rsgauges:industrial_door_contact_mat"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_contact_mat_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:industrial_day_timer"), 1)
            .pattern("NN")
            .pattern("RD")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:redstone"))
            .define('D', getItem("minecraft:clock"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_day_timer_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_day_timer"), 1)
            .requires(getItem("rsgauges:industrial_interval_timer"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_day_timer_recipe_backcycle"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:industrial_dimmer"), 1)
            .pattern("NNN")
            .pattern("NLN")
            .pattern("NRN")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:redstone"))
            .define('L', getItem("minecraft:stick"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_dimmer_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:industrial_door_contact_mat"), 1)
            .pattern("NN")
            .pattern("RP")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:redstone"))
            .define('P', getTag("rsgauges:pressure_plates"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_door_contact_mat_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_door_contact_mat"), 1)
            .requires(getItem("rsgauges:industrial_shock_sensitive_contact_mat"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_door_contact_mat_recipe_backcycle"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_double_pole_button"), 1)
            .requires(getItem("rsgauges:industrial_fenced_button"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_double_pole_button_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:industrial_entity_detector"), 1)
            .pattern("NN")
            .pattern("RS")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:redstone"))
            .define('S', getItem("minecraft:ender_eye"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_entity_detector_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_entity_detector"), 1)
            .requires(getItem("rsgauges:industrial_linear_entity_detector"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_entity_detector_recipe_backcycle"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_estop_switch"), 1)
            .requires(getItem("rsgauges:industrial_machine_switch"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_estop_switch_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_fallthrough_detector"), 1)
            .requires(getItem("rsgauges:industrial_high_sensitive_trapdoor"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_fallthrough_detector_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_fenced_button"), 1)
            .requires(getItem("rsgauges:industrial_button"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_fenced_button_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_foot_button"), 1)
            .requires(getItem("rsgauges:industrial_double_pole_button"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_foot_button_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_green_blinking_led"), 1)
            .requires(getItem("rsgauges:industrial_red_blinking_led"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_green_blinking_led_b_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_green_blinking_led"), 1)
            .requires(getItem("rsgauges:industrial_green_led"))
            .requires(getItem("minecraft:egg"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_green_blinking_led_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:industrial_green_led"), 1)
            .pattern("NN")
            .pattern("RG")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:redstone"))
            .define('G', getItem("minecraft:glowstone_dust"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_green_led_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_green_led"), 1)
            .requires(getItem("rsgauges:industrial_white_led"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_green_led_recipe_backcycle"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_high_sensitive_trapdoor"), 1)
            .requires(getItem("rsgauges:industrial_shock_sensitive_trapdoor"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_high_sensitive_trapdoor_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_hopper_switch"), 1)
            .requires(getItem("rsgauges:industrial_estop_switch"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_hopper_switch_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_interval_timer"), 1)
            .requires(getItem("rsgauges:industrial_day_timer"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_interval_timer_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:industrial_knock_button"), 1)
            .pattern("NNN")
            .pattern("NSO")
            .pattern("NRN")
            .define('O', getItem("minecraft:observer"))
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:redstone"))
            .define('S', getTag("rsgauges:buttons"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_knock_button_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:industrial_knock_switch"), 1)
            .pattern("NNN")
            .pattern("NSO")
            .pattern("NRN")
            .define('O', getItem("minecraft:observer"))
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:redstone"))
            .define('S', getItem("minecraft:lever"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_knock_switch_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_lever"), 1)
            .requires(getItem("rsgauges:industrial_small_lever"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_lever_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_lightning_sensor"), 1)
            .requires(getItem("rsgauges:industrial_rain_sensor"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_lightning_sensor_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:industrial_light_sensor"), 1)
            .pattern("NN")
            .pattern("RD")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:redstone"))
            .define('D', getItem("minecraft:daylight_detector"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_light_sensor_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_light_sensor"), 1)
            .requires(getItem("rsgauges:industrial_lightning_sensor"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_light_sensor_recipe_backcycle"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_linear_entity_detector"), 1)
            .requires(getItem("rsgauges:industrial_entity_detector"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_linear_entity_detector_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_machine_switch"), 1)
            .requires(getItem("rsgauges:industrial_rotary_machine_switch"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_machine_switch_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_pull_handle"), 1)
            .requires(getItem("rsgauges:industrial_foot_button"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_pull_handle_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_rain_sensor"), 1)
            .requires(getItem("rsgauges:industrial_light_sensor"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_rain_sensor_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_red_blinking_led"), 1)
            .requires(getItem("rsgauges:industrial_red_led"))
            .requires(getItem("minecraft:egg"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_red_blinking_led_b_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_red_blinking_led"), 1)
            .requires(getItem("rsgauges:industrial_yellow_blinking_led"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_red_blinking_led_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_red_led"), 1)
            .requires(getItem("rsgauges:industrial_yellow_led"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_red_led_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_rotary_lever"), 1)
            .requires(getItem("rsgauges:industrial_lever"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_rotary_lever_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_rotary_machine_switch"), 1)
            .requires(getItem("rsgauges:industrial_rotary_lever"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_rotary_machine_switch_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_shock_sensitive_contact_mat"), 1)
            .requires(getItem("rsgauges:industrial_contact_mat"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_shock_sensitive_contact_mat_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:industrial_shock_sensitive_trapdoor"), 1)
            .pattern("NN")
            .pattern("RP")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:redstone"))
            .define('P', getTag("minecraft:trapdoors"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_shock_sensitive_trapdoor_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_shock_sensitive_trapdoor"), 1)
            .requires(getItem("rsgauges:industrial_fallthrough_detector"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_shock_sensitive_trapdoor_recipe_backcycle"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:industrial_small_lever"), 1)
            .pattern("NN")
            .pattern("RL")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:redstone"))
            .define('L', getItem("minecraft:lever"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_small_lever_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_small_lever"), 1)
            .requires(getItem("rsgauges:industrial_hopper_switch"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_small_lever_recipe_backcycle"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:industrial_switchlink_cased_pulse_receiver"), 1)
            .pattern("IT")
            .pattern("RB")
            .define('T', getItem("minecraft:redstone_torch"))
            .define('I', getItem("minecraft:iron_ingot"))
            .define('R', getItem("minecraft:redstone"))
            .define('B', getTag("rsgauges:buttons"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_switchlink_cased_pulse_receiver_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:industrial_switchlink_cased_receiver"), 1)
            .pattern("IT")
            .pattern("RL")
            .define('T', getItem("minecraft:redstone_torch"))
            .define('I', getItem("minecraft:iron_ingot"))
            .define('R', getItem("minecraft:redstone"))
            .define('L', getItem("minecraft:lever"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_switchlink_cased_receiver_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:industrial_switchlink_pulse_receiver"), 1)
            .pattern("NT")
            .pattern("RB")
            .define('T', getItem("minecraft:redstone_torch"))
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:redstone"))
            .define('B', getTag("rsgauges:buttons"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_switchlink_pulse_receiver_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:industrial_switchlink_pulse_relay"), 1)
            .pattern("NR")
            .pattern("RB")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:redstone"))
            .define('B', getTag("rsgauges:buttons"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_switchlink_pulse_relay_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_switchlink_receiver_analog"), 1)
            .requires(getItem("rsgauges:industrial_switchlink_receiver"))
            .requires(getItem("minecraft:comparator"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_switchlink_receiver_analog_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:industrial_switchlink_receiver"), 1)
            .pattern("NT")
            .pattern("RL")
            .define('T', getItem("minecraft:redstone_torch"))
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:redstone"))
            .define('L', getItem("minecraft:lever"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_switchlink_receiver_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_switchlink_relay_analog"), 1)
            .requires(getItem("rsgauges:industrial_switchlink_relay"))
            .requires(getItem("minecraft:comparator"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_switchlink_relay_analog_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:industrial_switchlink_relay"), 1)
            .pattern("NR")
            .pattern("RL")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:redstone"))
            .define('L', getItem("minecraft:lever"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_switchlink_relay_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_white_blinking_led"), 1)
            .requires(getItem("rsgauges:industrial_white_led"))
            .requires(getItem("minecraft:egg"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_white_blinking_led_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_white_led"), 1)
            .requires(getItem("rsgauges:industrial_red_led"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_white_led_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_yellow_blinking_led"), 1)
            .requires(getItem("rsgauges:industrial_yellow_led"))
            .requires(getItem("minecraft:egg"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_yellow_blinking_led_b_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_yellow_blinking_led"), 1)
            .requires(getItem("rsgauges:industrial_green_blinking_led"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_yellow_blinking_led_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:industrial_yellow_led"), 1)
            .requires(getItem("rsgauges:industrial_green_led"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "industrial_yellow_led_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:arrow_target"), 1)
            .pattern("NAN")
            .pattern("NBN")
            .pattern("NRN")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('A', getItem("minecraft:arrow"))
            .define('R', getItem("minecraft:redstone"))
            .define('B', getTag("rsgauges:buttons"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "arrow_target_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:door_sensor_switch"), 1)
            .pattern("BR")
            .pattern("DN")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:redstone"))
            .define('B', getTag("rsgauges:pressure_plates"))
            .define('D', getTag("minecraft:doors"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "door_sensor_switch_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:elevator_button"), 1)
            .pattern("NGN")
            .pattern("NBN")
            .pattern("NRN")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('G', getItem("minecraft:glowstone_dust"))
            .define('R', getItem("minecraft:redstone"))
            .define('B', getTag("rsgauges:buttons"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "elevator_button_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("minecraft:ender_pearl"), 1)
            .requires(getItem("rsgauges:switchlink_pearl"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "ender_pearl_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:light_switch"), 1)
            .requires(getItem("rsgauges:arrow_target"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "light_switch_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:transport_chip"), 1)
            .pattern(" R ")
            .pattern(" P ")
            .pattern(" G ")
            .define('R', getItem("minecraft:redstone"))
            .define('P', getItem("minecraft:paper"))
            .define('G', getItem("minecraft:gold_nugget"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "transport_chip_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:transport_terminal"), 1)
            .pattern("IQI")
            .pattern("RER")
            .pattern("IQI")
            .define('I', getItem("minecraft:iron_ingot"))
            .define('Q', getItem("minecraft:quartz"))
            .define('R', getItem("minecraft:redstone"))
            .define('E', getItem("minecraft:ender_pearl"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "transport_terminal_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:valve_wheel_switch"), 1)
            .pattern("RNR")
            .pattern("NLN")
            .pattern("RNR")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:red_dye"))
            .define('L', getItem("minecraft:lever"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "valve_wheel_switch_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:oldfancy_bistableswitch1"), 1)
            .pattern("NN")
            .pattern("GL")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('G', getItem("minecraft:gold_nugget"))
            .define('L', getItem("minecraft:lever"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "oldfancy_bistableswitch1_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:oldfancy_bistableswitch1"), 1)
            .requires(getItem("rsgauges:oldfancy_bistableswitch2"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "oldfancy_bistableswitch1_recipe_backcycle"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:oldfancy_bistableswitch2"), 1)
            .requires(getItem("rsgauges:oldfancy_bistableswitch1"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "oldfancy_bistableswitch2_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:oldfancy_button"), 1)
            .pattern("NN")
            .pattern("GB")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('G', getItem("minecraft:gold_nugget"))
            .define('B', getTag("rsgauges:buttons"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "oldfancy_button_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:oldfancy_button"), 1)
            .requires(getItem("rsgauges:oldfancy_small_button"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "oldfancy_button_recipe_backcycle"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:oldfancy_small_button"), 1)
            .requires(getItem("rsgauges:oldfancy_spring_reset_chain"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "oldfancy_small_button_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:oldfancy_spring_reset_chain"), 1)
            .requires(getItem("rsgauges:oldfancy_button"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "oldfancy_spring_reset_chain_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:red_power_plant"), 1)
            .pattern("SS")
            .pattern("FP")
            .define('S', getItem("minecraft:stick"))
            .define('F', getItem("minecraft:red_tulip"))
            .define('P', getTag("rsgauges:pressure_plates"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "red_power_plant_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:yellow_power_plant"), 1)
            .pattern("SS")
            .pattern("FP")
            .define('S', getItem("minecraft:stick"))
            .define('F', getItem("minecraft:dandelion"))
            .define('P', getTag("rsgauges:pressure_plates"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "yellow_power_plant_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:rustic_angular_lever"), 1)
            .requires(getItem("rsgauges:rustic_two_hinge_lever"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "rustic_angular_lever_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:rustic_button"), 1)
            .pattern("NN")
            .pattern("SB")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('S', getItem("minecraft:stick"))
            .define('B', getTag("rsgauges:buttons"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "rustic_button_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:rustic_button"), 1)
            .requires(getItem("rsgauges:rustic_nail_button"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "rustic_button_recipe_backcycle"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:rustic_contact_plate"), 1)
            .requires(getItem("rsgauges:rustic_door_contact_plate"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "rustic_contact_plate_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:rustic_door_contact_plate"), 1)
            .pattern("NN")
            .pattern("RP")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:stick"))
            .define('P', getTag("rsgauges:pressure_plates"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "rustic_door_contact_plate_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:rustic_door_contact_plate"), 1)
            .requires(getItem("rsgauges:rustic_shock_sensitive_plate"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "rustic_door_contact_plate_recipe_backcycle"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:rustic_fallthrough_detector"), 1)
            .requires(getItem("rsgauges:rustic_high_sensitive_trapdoor"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "rustic_fallthrough_detector_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:rustic_high_sensitive_trapdoor"), 1)
            .requires(getItem("rsgauges:rustic_shock_sensitive_trapdoor"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "rustic_high_sensitive_trapdoor_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:rustic_lever"), 1)
            .pattern("NN")
            .pattern("SL")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('S', getItem("minecraft:stick"))
            .define('L', getItem("minecraft:lever"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "rustic_lever_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:rustic_lever"), 1)
            .requires(getItem("rsgauges:rustic_nail_lever"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "rustic_lever_recipe_backcycle"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:rustic_nail_button"), 1)
            .requires(getItem("rsgauges:rustic_spring_reset_chain"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "rustic_nail_button_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:rustic_nail_lever"), 1)
            .requires(getItem("rsgauges:rustic_angular_lever"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "rustic_nail_lever_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:rustic_semaphore"), 1)
            .pattern("NN")
            .pattern("RG")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:stick"))
            .define('G', getItem("minecraft:glowstone_dust"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "rustic_semaphore_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:rustic_shock_sensitive_plate"), 1)
            .requires(getItem("rsgauges:rustic_contact_plate"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "rustic_shock_sensitive_plate_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:rustic_shock_sensitive_trapdoor"), 1)
            .pattern("NN")
            .pattern("RP")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:stick"))
            .define('P', getTag("minecraft:trapdoors"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "rustic_shock_sensitive_trapdoor_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:rustic_shock_sensitive_trapdoor"), 1)
            .requires(getItem("rsgauges:rustic_fallthrough_detector"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "rustic_shock_sensitive_trapdoor_recipe_backcycle"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:rustic_small_button"), 1)
            .requires(getItem("rsgauges:rustic_button"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "rustic_small_button_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:rustic_spring_reset_chain"), 1)
            .requires(getItem("rsgauges:rustic_small_button"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "rustic_spring_reset_chain_recipe"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItem("rsgauges:rustic_two_hinge_lever"), 1)
            .requires(getItem("rsgauges:rustic_lever"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "rustic_two_hinge_lever_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:sensitive_glass_block"), 8)
            .pattern("NG")
            .pattern("SR")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:redstone"))
            .define('G', getItem("minecraft:glass"))
            .define('S', getItem("minecraft:glowstone"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "sensitive_glass_block_recipe"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItem("rsgauges:stained_sensitiveglass"), 4)
            .pattern("NG")
            .pattern("DR")
            .define('N', getItem("minecraft:iron_nugget"))
            .define('R', getItem("minecraft:redstone"))
            .define('G', getItem("minecraft:glass"))
            .define('D', getItem("minecraft:white_dye"))
            .unlockedBy("has_item", has(Items.REDSTONE))
            .save(output, ResourceLocation.fromNamespaceAndPath("rsgauges", "stained_sensitiveglass"));
    }
}
