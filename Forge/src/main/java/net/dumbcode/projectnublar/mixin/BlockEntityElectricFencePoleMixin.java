package net.dumbcode.projectnublar.mixin;

import net.dumbcode.projectnublar.block.entity.BlockEntityElectricFence;
import net.dumbcode.projectnublar.block.entity.BlockEntityElectricFencePole;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockEntityElectricFencePole.class)
public abstract class BlockEntityElectricFencePoleMixin extends BlockEntityElectricFence {
    public BlockEntityElectricFencePoleMixin(BlockPos pos, BlockState state) {
        super(pos, state);
    }


}
