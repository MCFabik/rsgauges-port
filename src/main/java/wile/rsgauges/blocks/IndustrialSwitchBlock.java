package wile.rsgauges.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import wile.rsgauges.detail.ModResources;
import wile.rsgauges.detail.SwitchLink;

import javax.annotation.Nullable;

public class IndustrialSwitchBlock extends BistableSwitchBlock {

    public IndustrialSwitchBlock(long config, BlockBehaviour.Properties properties, AABB unrotatedBBUnpowered, @Nullable AABB unrotatedBBPowered, @Nullable ModResources.BlockSoundEvent powerOnSound, @Nullable ModResources.BlockSoundEvent powerOffSound) {
        super(config, properties, unrotatedBBUnpowered, unrotatedBBPowered, powerOnSound, powerOffSound);
    }

    public IndustrialSwitchBlock(long config, BlockBehaviour.Properties properties, AABB unrotatedBBUnpowered, @Nullable AABB unrotatedBBPowered) {
        super(config, properties, unrotatedBBUnpowered, unrotatedBBPowered);
    }

    public IndustrialSwitchBlock(long config, BlockBehaviour.Properties properties, AABB unrotatedBB) {
        super(config, properties, unrotatedBB);
    }

    @Override
    protected boolean onSwitchActivated(Level world, BlockPos pos, BlockState state, @Nullable Player player, @Nullable Direction facing) {
        if (world.isClientSide()) return true;
        SwitchTileEntity te = getTe(world, pos);
        if (te == null) return true;

        boolean was_powered = state.getValue(POWERED);
        boolean powered = !was_powered;

        world.setBlock(pos, state.setValue(POWERED, powered), 1 | 2 | 8 | 16);

        if (powered) {
            power_on_sound.play(world, pos);
            // ON: Send stored power wirelessly and update neighbors
            int power = te.setpower();
            te.activateSwitchLinks(power, power > 0 ? 15 : 0, true);
        } else {
            power_off_sound.play(world, pos);
            // OFF: Stop sending power
            te.activateSwitchLinks(0, 0, true);
        }
        notifyNeighbours(world, pos, state.setValue(POWERED, powered), te, true);
        return true;
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!isAffectedByNeigbour(state, world, pos, fromPos)) return;
        SwitchTileEntity te = getTe(world, pos);
        if (te == null) return;

        int power;
        if (isCube()) {
            power = world.getBestNeighborSignal(pos);
        } else {
            Direction facing = state.getValue(FACING).getOpposite();
            BlockPos neighbour_pos = pos.relative(facing);
            BlockState neighbour_state = world.getBlockState(neighbour_pos);
            if (!state.isSignalSource()) {
                power = world.getBestNeighborSignal(neighbour_pos);
            } else {
                power = Math.max(neighbour_state.getSignal(world, neighbour_pos, facing), neighbour_state.getDirectSignal(world, neighbour_pos, facing));
            }
        }

        if (te.setpower() == power) return;
        te.setpower(power);

        if (state.getValue(POWERED)) {
            te.activateSwitchLinks(power, power > 0 ? 15 : 0, true);
            notifyNeighbours(world, pos, state, te, true);
        }
    }

    @Override
    public SwitchLink.RequestResult switchLinkTrigger(SwitchLink link) {
        Level world = link.world;
        BlockPos pos = link.target_position;
        SwitchTileEntity te = getTe(world, pos);
        if (te == null || !te.verifySwitchLinkTarget(link)) return SwitchLink.RequestResult.TARGET_GONE;

        int p = link.source_digital_power;
        if (te.setpower() == p) return SwitchLink.RequestResult.OK;
        te.setpower(p);

        BlockState state = world.getBlockState(pos);
        if (state.getValue(POWERED)) {
            te.activateSwitchLinks(p, p > 0 ? 15 : 0, true);
            notifyNeighbours(world, pos, state, te, true);
        }

        return SwitchLink.RequestResult.OK;
    }

    @Override
    protected int getPower(BlockState state, BlockGetter world, BlockPos pos, Direction side, boolean strong) {
        if (!state.getValue(POWERED)) return 0;

        // "unten" (also zu dem Block, an dem er befestigt ist) immer ein Signal von 15 ausgeben
        if (state.getValue(FACING) == side) {
            return 15;
        }

        // Ansonsten das durchgeleitete Signal (Relais) ausgeben
        if (!(world instanceof Level)) return 0;
        SwitchTileEntity te = getTe((Level) world, pos);
        if (te == null) return 0;
        return te.power(state, strong);
    }

    @Override
    public boolean switchLinkHasTargetSupport(Level world, BlockPos pos) { return true; }

    @Override
    public boolean switchLinkHasSourceSupport(Level world, BlockPos pos) { return true; }

    public void manualPearlClick(BlockState state, Level world, BlockPos pos, Player player) {
        this.attack(state, world, pos, player);
    }
}
