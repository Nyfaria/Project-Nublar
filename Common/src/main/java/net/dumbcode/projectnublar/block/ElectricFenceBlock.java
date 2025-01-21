package net.dumbcode.projectnublar.block;

import net.dumbcode.projectnublar.block.api.BlockConnectableBase;
import net.dumbcode.projectnublar.block.api.ConnectableBlockEntity;
import net.dumbcode.projectnublar.block.api.Connection;
import net.dumbcode.projectnublar.block.entity.BlockEntityElectricFence;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class ElectricFenceBlock extends BlockConnectableBase implements EntityBlock {

    public static final int ITEM_FOLD = 20;

    public ElectricFenceBlock(Properties properties) {
        super(properties.noOcclusion());
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void animateTick(BlockState stateIn, Level world, BlockPos pos, RandomSource rand) {
        BlockEntity te = world.getBlockEntity(pos);
        if(te instanceof ConnectableBlockEntity) {
            for (Connection connection : ((ConnectableBlockEntity) te).getConnections()) {
                if(connection.isBroken() || !connection.isPowered(world)) {
                    continue;
                 }
                Vec3 center = connection.getCenter();

                boolean pb = connection.brokenSide(world, false);

                float chance = 0.02F;

                if(pb || connection.brokenSide(world, true) && rand.nextFloat() < chance) {
                    Vector3f point = (pb ? connection.getPrevCache() : connection.getNextCache()).point();
                    Vector3f norm = new Vector3f(point.x(), point.y(), point.z());
                    //todo: particles
//                    if(norm.normalize()) {
//                        for (int i = 0; i < 8; i++) {
//                            world.addParticle(ProjectNublarParticles.SPARK.get(),
//                                center.x+point.x(), center.y+point.y(), center.z+point.z(),
//                                norm.x(), norm.y(), norm.z()
//                            );
//                        }
//                    }
                }
            }
        }
        super.animateTick(stateIn, world, pos, rand);
    }


    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntityElectricFence(blockPos, blockState);
    }
}
