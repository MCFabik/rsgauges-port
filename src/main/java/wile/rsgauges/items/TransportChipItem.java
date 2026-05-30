package wile.rsgauges.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.CompoundTag;
import java.util.List;

public class TransportChipItem extends Item {
    public TransportChipItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        
        if (!level.getBlockState(pos).is(wile.rsgauges.ModContent.getBlock("transport_terminal"))) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            ResourceKey<Level> dimension = level.dimension();
            ItemStack stack = context.getItemInHand();

            CompoundTag tag = new CompoundTag();
            tag.putInt("TargetX", pos.getX());
            tag.putInt("TargetY", pos.getY() + 1);
            tag.putInt("TargetZ", pos.getZ());
            tag.putString("Dimension", dimension.location().toString());

            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

            if (context.getPlayer() != null) {
                context.getPlayer().sendSystemMessage(Component.literal("§aPosition erfolgreich auf Chip gespeichert!"));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("TargetX")) {
                tooltip.add(Component.literal("§7Dimension: §e" + tag.getString("Dimension")));
                tooltip.add(Component.literal("§7Ziel X: §a" + tag.getInt("TargetX")));
                tooltip.add(Component.literal("§7Ziel Y: §a" + tag.getInt("TargetY")));
                tooltip.add(Component.literal("§7Ziel Z: §a" + tag.getInt("TargetZ")));
                return;
            }
        }
        tooltip.add(Component.literal("§cLeerer Chip. Lege ihn ins Transport Terminal zum Linken."));
    }
}