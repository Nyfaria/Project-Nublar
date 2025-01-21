package net.dumbcode.projectnublar.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import earth.terrarium.botarium.common.energy.base.BotariumEnergyBlock;
import earth.terrarium.botarium.common.energy.impl.InsertOnlyEnergyContainer;
import earth.terrarium.botarium.common.energy.impl.WrappedBlockEnergyContainer;
import net.dumbcode.projectnublar.block.ElectricFencePostBlock;
import net.dumbcode.projectnublar.block.api.ConnectableBlockEntity;
import net.dumbcode.projectnublar.block.api.Connection;
import net.dumbcode.projectnublar.block.api.MathUtils;
import net.dumbcode.projectnublar.init.BlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class BlockEntityElectricFencePole extends BlockEntityElectricFence implements ConnectableBlockEntity, GeoBlockEntity, BotariumEnergyBlock<WrappedBlockEnergyContainer> {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean flippedAround;

    private VoxelShape cachedShape = Shapes.block();

    private double cachedRotation = 0;

    private boolean shouldRefreshNextTick = false;

    public boolean isFlippedAround() {
        return flippedAround;
    }

    public VoxelShape getCachedShape() {
        return cachedShape;
    }

    public double getCachedRotation() {
        return cachedRotation;
    }

    public boolean isShouldRefreshNextTick() {
        return shouldRefreshNextTick;
    }

    private WrappedBlockEnergyContainer energyContainer;


    public BlockEntityElectricFencePole(BlockPos pos, BlockState state) {
        super(BlockInit.ELECTRIC_FENCE_POST_BLOCK_ENTITY.get(),pos, state);
    }

    @Override
    public void saveData(CompoundTag compound) {
        compound.putBoolean("rotation_flipped", this.flippedAround);
        compound.put("energy", this.energyContainer.serialize(new CompoundTag()));
//        compound.putInt("energy", this.energy.getEnergyStored());
        super.saveData(compound);
    }


    @Override
    public void loadData(CompoundTag compound) {
        this.flippedAround = compound.getBoolean("rotation_flipped");
        this.energyContainer.deserialize(compound.getCompound("energy"));
//        this.energy.setEnergy(compound.getInt("energy"));
        super.loadData(compound);
    }

    @Override
    public boolean removedByFenceRemovers() {
        return false;
    }


    public void setFlippedAround(boolean flippedAround) {
        this.flippedAround = flippedAround;
//        this.requestModelDataUpdate();
        this.level.sendBlockUpdated(this.getBlockPos(), Blocks.AIR.defaultBlockState(), this.getBlockState(), 3);
    }



    //todo: forge
//    @Override
//    public void onLoad() {
//        this.shouldRefreshNextTick = true;
//    }

    public void tick(Level world, BlockPos blockPos, BlockState pState, BlockEntityElectricFencePole be) {
        if(this.shouldRefreshNextTick) {
            this.shouldRefreshNextTick = false;
//            this.requestModelDataUpdate();
            this.cachedRotation = this.computeRotation();
        }
        double oldRotation = this.cachedRotation;
        this.cachedRotation = this.computeRotation();
        if (oldRotation != this.cachedRotation) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
        //todo: energy
        boolean powered = this.getEnergyStorage().getStoredEnergy() > 0;
        if(powered) {
            boolean update = false;
            if (this.level.getBlockState(this.getBlockPos()).getValue(ElectricFencePostBlock.POWERED_PROPERTY) != powered) {
                update = true;
            }
            getEnergyStorage().internalExtract(10, false);
            BlockState state = this.level.getBlockState(this.getBlockPos());
            if (state.getBlock() instanceof ElectricFencePostBlock && state.getValue(((ElectricFencePostBlock) state.getBlock()).indexProperty) == 0) {
                if (update) {
                    for (int y = 0; y < ((ElectricFencePostBlock) state.getBlock()).getType().getHeight(); y++) {
                        BlockPos pos = this.getBlockPos().above(y);
                        BlockState s = this.level.getBlockState(pos);
                        if (s.getBlock() == state.getBlock()) { //When placing the blocks can be air
                            this.level.setBlock(pos, s.setValue(ElectricFencePostBlock.POWERED_PROPERTY, powered), 3);
                        }
                    }
                }
                //Pass power to other poles connected to this.
                if (this.getEnergyStorage().getStoredEnergy() > 300) {
                    Set<WrappedBlockEnergyContainer> storages = Sets.newLinkedHashSet();
                    for (Connection connection : this.getConnections()) {
                        BlockEntity te = this.level.getBlockEntity(connection.getPosition().equals(connection.getFrom()) ? connection.getTo() : connection.getFrom());
                        if (te != null) {
                            if (te instanceof BlockEntityElectricFencePole e) {
                                storages.add(e.getEnergyStorage());
                            }
                        }
                    }
                    List<WrappedBlockEnergyContainer> list = Lists.newArrayList(storages);
                    list.sort(Comparator.comparing(WrappedBlockEnergyContainer::getStoredEnergy));
                    for (WrappedBlockEnergyContainer storage : list) {
                        long sendEnergy = storage.internalInsert(this.getEnergyStorage().internalExtract(300 / list.size(), true), true);
                        this.getEnergyStorage().internalExtract(sendEnergy, false);
                        storage.internalInsert(sendEnergy, false);
                    }
                }
            }
        }
    }


    //todo: forge
//    @Override
//    public void requestModelDataUpdate() {
//        this.cachedRotation = this.computeRotation();
//
//        BlockState state = this.getBlockState();
//        if (state.getBlock() instanceof BlockElectricFencePole) {
//            ConnectionType type = ((BlockElectricFencePole) state.getBlock()).getType();
//
//            float t = type.getHalfSize();
//            double x = Math.sin(Math.toRadians(this.cachedRotation + 90F - type.getRotationOffset())) * type.getRadius();
//            double z = Math.cos(Math.toRadians(this.cachedRotation + 90F - type.getRotationOffset())) * type.getRadius();
//            this.cachedShape = VoxelShapes.box(x-t, 0, z-t, x+t, 1, z+t).move(0.5, 0, 0.5);
//        }
//
//        super.requestModelDataUpdate();
//
//    }


//    @Nonnull
//    @Override
//    public IModelData getModelData() {
//        return new ModelDataMap.Builder()
//            .withInitial(ProjectNublarModelData.CONNECTIONS, this.compiledRenderData())
//            .withInitial(ProjectNublarModelData.FENCE_POLE_ROTATION_DEGS, this.cachedRotation)
//            .build();
//    }

    private double computeRotation() {
        double rotation = 0;

        if(this.level == null || !this.level.isLoaded(this.getBlockPos())) {
            return this.flippedAround ? 0F : 180F;
        }

        BlockState state = this.level.getBlockState(this.getBlockPos());
        if (state.getBlock() instanceof ElectricFencePostBlock) {
            ElectricFencePostBlock pole = (ElectricFencePostBlock) state.getBlock();
            BlockEntity te = this.level.getBlockEntity(this.getBlockPos().below(state.getValue((pole).indexProperty)));
            if (te instanceof BlockEntityElectricFencePole) {
                BlockEntityElectricFencePole ef = (BlockEntityElectricFencePole) te;
                if (!ef.getConnections().isEmpty()) {

                    List<Connection> differingConnections = Lists.newArrayList();
                    for (Connection connection : ef.getConnections()) {
                        boolean has = false;
                        for (Connection dc : differingConnections) {
                            if (connection.getFrom().equals(dc.getFrom()) && connection.getTo().equals(dc.getTo())) {
                                has = true;
                                break;
                            }
                        }
                        if (!has) {
                            differingConnections.add(connection);
                        }
                    }

                    if (differingConnections.size() == 1) {
                        Connection connection = differingConnections.get(0);
                        double[] in = connection.getIn();
                        rotation += (float) Math.toDegrees(Math.atan((in[2] - in[3]) / (in[1] - in[0]))) + 90;
                    } else {
                        Connection connection1 = differingConnections.get(0);
                        Connection connection2 = differingConnections.get(1);

                        double[] in1 = connection1.getIn();
                        double[] in2 = connection2.getIn();

                        double angle1 = MathUtils.horizontalDegree(in1[1] - in1[0], in1[2] - in1[3], connection1.getPosition().equals(connection1.getMin()));
                        double angle2 = MathUtils.horizontalDegree(in2[1] - in2[0], in2[2] - in2[3], connection2.getPosition().equals(connection2.getMin()));

                        rotation += (float) (angle1 + (angle2 - angle1) / 2D);
                    }
                }

                rotation += pole.getType().getRotationOffset();
                if (ef.isFlippedAround()) {
                    rotation += 180;
                }
            }
        }
        return rotation;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }


    @Override
    public WrappedBlockEnergyContainer getEnergyStorage() {
        return energyContainer == null ? this.energyContainer = new WrappedBlockEnergyContainer(this, new InsertOnlyEnergyContainer(350,350)) : this.energyContainer;
    }
}
