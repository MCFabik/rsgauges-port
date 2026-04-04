/*
 * @file SidedProxy.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2020 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 *
 * General client/server sideness selection proxy.
 */
package wile.rsgauges.libmc.detail;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;
import java.util.Optional;

public class SidedProxy
{
  @Nullable
  public static Player getPlayerClientSide()
  { return proxy.getPlayerClientSide(); }

  @Nullable
  public static Level getWorldClientSide()
  { return proxy.getWorldClientSide(); }

  @Nullable
  public static Minecraft mc()
  { return proxy.mc(); }

  @Nullable
  public static Optional<Boolean> isCtrlDown()
  { return proxy.isCtrlDown(); }

  @Nullable
  public static Optional<Boolean> isShiftDown()
  { return proxy.isShiftDown(); }

  // --------------------------------------------------------------------------------------------------------

  // In NeoForge 1.21.1 nutzen wir direkt FMLEnvironment.dist anstelle des veralteten DistExecutors
  private static final ISidedProxy proxy = (FMLEnvironment.dist == Dist.CLIENT) ? new ClientProxy() : new ServerProxy();

  private interface ISidedProxy
  {
    default @Nullable Player getPlayerClientSide() { return null; }
    default @Nullable Level getWorldClientSide() { return null; }
    default @Nullable Minecraft mc() { return null; }
    default Optional<Boolean> isCtrlDown() { return Optional.empty(); }
    default Optional<Boolean> isShiftDown() { return Optional.empty(); }
  }

  private static final class ClientProxy implements ISidedProxy
  {
    @Override
    public @Nullable Player getPlayerClientSide() { return Minecraft.getInstance().player; }

    @Override
    public @Nullable Level getWorldClientSide() { return Minecraft.getInstance().level; }

    @Override
    public @Nullable Minecraft mc() { return Minecraft.getInstance(); }

    @Override
    public Optional<Boolean> isCtrlDown() { return Optional.of(Auxiliaries.isCtrlDown()); }

    @Override
    public Optional<Boolean> isShiftDown() { return Optional.of(Auxiliaries.isShiftDown()); }
  }

  private static final class ServerProxy implements ISidedProxy
  {
  }
}