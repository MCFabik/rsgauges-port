/*
 * @file PlayerBlockInteraction.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2020 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 *
 * Encapsulates Player interaction events with blocks (NeoForge 1.21.1 Update)
 */
package wile.rsgauges.libmc.detail;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.ICancellableEvent;
import org.apache.logging.log4j.Logger;

public class PlayerBlockInteraction
{
  public interface INeighbourBlockInteractionSensitive
  {
    default boolean onNeighborBlockPlayerInteraction(Level world, BlockPos pos, BlockState state, BlockPos fromPos, LivingEntity entity, InteractionHand hand, boolean isLeftClick)
    { return false; }
  }

  public static void init(String modid, Logger logger)
  {
    // 1.21.1 Update: Wir registrieren die Methode explizit für die Unterklassen,
    // da die abstrakte Basisklasse PlayerInteractEvent nicht mehr direkt erlaubt ist.
    NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, (RightClickBlock event) -> onPlayerInteract(event));
    NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, (LeftClickBlock event) -> onPlayerInteract(event));
  }

  public static void onPlayerInteract(PlayerInteractEvent event)
  {
    final Level world = event.getLevel();
    if(world.isClientSide()) return;

    // Hand-Check: Wir reagieren nur auf die Haupthand
    if(event.getHand() != InteractionHand.MAIN_HAND) return;

    final boolean is_rclick = (event instanceof RightClickBlock);
    final boolean is_lclick = (event instanceof LeftClickBlock) && (event.getFace() != Direction.DOWN);

    if((!is_rclick) && (!is_lclick)) return;

    final BlockPos fromPos = event.getPos();
    for(Direction facing: Direction.values()) {
      if(event.getFace() == facing) continue;
      final BlockPos pos = fromPos.relative(facing);
      final BlockState state = event.getLevel().getBlockState(pos);

      if(!((state.getBlock()) instanceof INeighbourBlockInteractionSensitive)) continue;

      if(((INeighbourBlockInteractionSensitive)state.getBlock()).onNeighborBlockPlayerInteraction(world, pos, state, fromPos, event.getEntity(), event.getHand(), is_lclick)) {

        if (event instanceof ICancellableEvent cancellableEvent) {
          cancellableEvent.setCanceled(true);
        }
      }
    }
  }
}