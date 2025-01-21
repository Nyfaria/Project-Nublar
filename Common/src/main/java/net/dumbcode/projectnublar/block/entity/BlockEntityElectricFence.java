package net.dumbcode.projectnublar.block.entity;

import com.google.common.collect.Sets;
import net.dumbcode.projectnublar.block.api.ConnectableBlockEntity;
import net.dumbcode.projectnublar.block.api.Connection;
import net.dumbcode.projectnublar.block.api.SyncingBlockEntity;
import net.dumbcode.projectnublar.init.BlockInit;
import net.dumbcode.projectnublar.util.LineUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;



import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BlockEntityElectricFence extends SyncingBlockEntity implements ConnectableBlockEntity {

    private final Set<Connection> fenceConnections = Sets.newLinkedHashSet();

    private VoxelShape collidableCache;

    public BlockEntityElectricFence(BlockPos pos, BlockState state) {
        super(BlockInit.ELECTRIC_FENCE_BLOCK_ENTITY.get(), pos, state);
    }

    protected BlockEntityElectricFence(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void saveData(CompoundTag compound) {
        CompoundTag nbt = new CompoundTag();
        int i = 0;
        for (Connection connection : this.fenceConnections) {
            nbt.put(i + "c",connection.writeToNBT(new CompoundTag()));
        }
        compound.put("connections", nbt);
    }

    @Override
    public void loadData(CompoundTag compound) {
        CompoundTag nbt = compound.getCompound("connections");
        for (int i = 0; i < nbt.size(); i++) {
            Connection connection = Connection.fromNBT(nbt.getCompound(i+"c"), this);
            if(connection.isValid()) {
                this.fenceConnections.add(connection);
            }
        }

        if(this.level != null) {
//            this.requestModelDataUpdate();
        }
    }

    @Override
    public VoxelShape getOrCreateCollision() {
        if(this.collidableCache == null) {
            VoxelShape shape = Shapes.empty();
            for (Connection connection : this.fenceConnections) {
                shape = Shapes.or(shape, connection.getCollisionShape());
            }
            this.collidableCache = shape;
        }

        return this.collidableCache;
    }

//    @Override
//    public double getViewDistance() {
//        return Double.MAX_VALUE;
//    }
//
//    @Override
//    public AABB getRenderBoundingBox() {
//        return new AxisAlignedBB(this.getBlockPos().offset(-1, -1, -1), this.getBlockPos().offset(1, 1, 1));
//    }

    @Override
    public void addConnection(Connection connection) {
        this.fenceConnections.add(connection);
//        this.requestModelDataUpdate();
        this.setChanged();
    }
//todo: forge stuff

//    @Nonnull
//    @Override
//    public IModelData getModelData() {
//        return new ModelDataMap.Builder()
//            .withInitial(ProjectNublarModelData.CONNECTIONS, this.compiledRenderData())
//            .build();
//    }
//
//    @Override
//    public void requestModelDataUpdate() {
//        super.requestModelDataUpdate();
//        if(this.level != null) {
////            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
//        }
//        this.collidableCache = null;
//    }


    protected Set<Connection.CompiledRenderData> compiledRenderData() {
        return this.getConnections().stream()
            .map(c -> c.compileRenderData(this.level))
            .collect(Collectors.toSet());
    }

    @Override
    public Set<Connection> getConnections() {
        return Collections.unmodifiableSet(this.fenceConnections);
    }

    /**
     * Breaks the surrounding fence. Used for entities
     * who "attack" the fence.
     * @param intensity Intensity at which the fence breaks.
     */
    public void breakFence(int intensity) { // TODO: Add more randomness.
        for (Connection connection : fenceConnections) {
            for (double offset : connection.getType().getOffsets()) {
                List<BlockPos> blocks = LineUtils.getBlocksInbetween(connection.getFrom(), connection.getTo(), offset);
                for (int k = 0; k < blocks.size(); k++) {
                    for (int i = 0; i < connection.getType().getHeight(); i++) {
                        BlockPos position = blocks.get(k).above(i);
                        if ((k == blocks.size() / 2 - 1 || k == blocks.size() / 2 + 1) && i < intensity / 2 + 1) {
                            this.level.destroyBlock(position, true);
                        } else if (k == blocks.size() / 2 && i < intensity) {
                            this.level.destroyBlock(position, true);
                        }
                    }
                }
            }
        }
    }
}
