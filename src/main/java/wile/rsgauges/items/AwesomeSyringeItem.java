package wile.rsgauges.items;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class AwesomeSyringeItem extends Item {

    public AwesomeSyringeItem(Properties properties) {
        super(properties.stacksTo(16));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            int duration = 1000; // 50 seconds
            // Nausea V (5 seconds)
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 4));
            
            // Speed I (50 seconds)
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, 0));
            
            // Jump Boost XX (50 seconds)
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, duration, 19));
            
            // Haste X (50 seconds)
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, duration, 9));
            
            // Strength XXV (50 seconds)
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, 24));
            
            // Health Boost X (50 seconds)
            player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, duration, 9));
            
            // Absorption V (50 seconds)
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, duration, 4));
            
            // Resistance X (50 seconds)
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, 9));
            
            // Regeneration X (50 seconds)
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, duration, 9));
            
            // Fire Resistance (50 seconds)
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, duration, 0));

            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_HURT, SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        ItemStack emptySyringe = new ItemStack(wile.rsgauges.ModContent.getItem("empty_syringe"));
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
            if (stack.isEmpty()) {
                return InteractionResultHolder.sidedSuccess(emptySyringe, level.isClientSide());
            } else {
                if (!player.getInventory().add(emptySyringe)) {
                    player.drop(emptySyringe, false);
                }
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7Just one shot an hour helps!"));
    }
}
