package wile.rsgauges.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import wile.rsgauges.libmc.detail.Auxiliaries;

import java.util.List;

public abstract class RsItem extends Item
{
  public RsItem(Item.Properties properties)
  { super(properties); }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag)
  {
    // Hinweis: Auxiliaries muss ebenfalls auf TooltipContext angepasst sein/werden.
    Auxiliaries.Tooltip.addInformation(stack, context, tooltip, flag, true);
  }
}