package net.dumbcode.projectnublar.block;

import com.google.common.collect.Lists;
import net.dumbcode.projectnublar.Constants;
import net.dumbcode.projectnublar.block.api.BlockConnectableBase;
import net.dumbcode.projectnublar.block.api.ConnectableBlockEntity;
import net.dumbcode.projectnublar.block.api.Connection;
import net.dumbcode.projectnublar.block.api.ConnectionType;
import net.dumbcode.projectnublar.block.entity.BlockEntityElectricFencePole;
import net.dumbcode.projectnublar.init.BlockInit;
import net.dumbcode.projectnublar.init.ItemInit;
import net.dumbcode.projectnublar.util.LineUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;

public class ElectricFencePostBlock extends BlockConnectableBase implements EntityBlock {

    private final ConnectionType type;
    public final IntegerProperty indexProperty;
    public static final BooleanProperty POWERED_PROPERTY = BooleanProperty.create("powered");
    private final StateDefinition<Block, BlockState> stateDefinition;

    private static boolean destroying = false;

    public static final int LIMIT = 15;

    public ElectricFencePostBlock(Properties properties, ConnectionType type) {
        super(properties);
        this.type = type;
        this.indexProperty = IntegerProperty.create("index", 0, type.getHeight() - 1);

        StateDefinition.Builder<Block, BlockState> builder = new StateDefinition.Builder<>(this);
        this.createBlockStateDefinition(builder);
        builder.add(indexProperty);
        this.stateDefinition = builder.create(Block::defaultBlockState, BlockState::new);

        this.registerDefaultState(this.stateDefinition.any().setValue(indexProperty, 0).setValue(POWERED_PROPERTY, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED_PROPERTY);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public StateDefinition<Block, BlockState> getStateDefinition() {
        return this.stateDefinition;
    }


    @Override
    protected VoxelShape getDefaultShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof BlockEntityElectricFencePole) {
            return ((BlockEntityElectricFencePole) entity).getCachedShape();
        }
        return Shapes.block();
    }


    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        boolean flag = true;
        for (int i = 0; i < this.type.getHeight(); i++) {
            flag &= world.getBlockState(pos.above(i)).getBlock().canBeReplaced(state, Fluids.EMPTY);
        }
        return flag;
    }


    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(this.indexProperty, 0);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState old, boolean p_220082_5_) {
        if (state.getValue(indexProperty) == 0) {
            for (int i = 1; i < this.type.getHeight(); i++) {
                world.setBlock(pos.above(i), this.defaultBlockState().setValue(indexProperty, i), 3);
            }

        }
        super.onPlace(state, world, pos, old, p_220082_5_);
    }


    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray) {
        int index = state.getValue(indexProperty);
        if (index == 0) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.isEmpty()) {
                BlockEntity te = world.getBlockEntity(pos);
                if (te instanceof BlockEntityElectricFencePole) {
                    ((BlockEntityElectricFencePole) te).setFlippedAround(!((BlockEntityElectricFencePole) te).isFlippedAround());
                    te.setChanged();
                    for (int y = 0; y < this.type.getHeight(); y++) {
                        BlockEntity t = world.getBlockEntity(pos.above(y));
                        if (t != null) {
//                            t.requestModelDataUpdate();
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
            } else if (stack.getItem() == ItemInit.WIRE_SPOOL.get()) { //Move to item class ?
                CompoundTag nbt = stack.getOrCreateTagElement(Constants.MODID);
                if (nbt.contains("fence_position", Tag.TAG_COMPOUND)) {
                    BlockPos other = NbtUtils.readBlockPos(nbt.getCompound("fence_position"));
                    double dist = Math.sqrt(other.distSqr(pos));
                    if (dist > LIMIT) {
                        if (!world.isClientSide) {
                            player.displayClientMessage(Component.translatable("projectnublar.fences.length.toolong", Math.round(dist), LIMIT), true);
                        }
                        nbt.put("fence_position", NbtUtils.writeBlockPos(pos));
                    } else if (world.getBlockState(other).getBlock() == this && !other.equals(pos)) {
                        int itemMax;
                        int itemAmount = itemMax = Mth.ceil(dist / ElectricFenceBlock.ITEM_FOLD * this.type.getHeight());
                        int total = 0;
                        boolean full = false;
                        List<Pair<ItemStack, Integer>> stacksFound = Lists.newArrayList();

                        if (itemAmount <= stack.getCount()) {
                            total += itemAmount;
                            stacksFound.add(Pair.of(stack, itemAmount));
                            full = true;
                        } else {
                            total += stack.getCount();
                            stacksFound.add(Pair.of(stack, stack.getCount()));
                        }
                        itemAmount -= stack.getCount();

                        for (ItemStack itemStack : player.getInventory().items) {
                            if (itemStack != stack && itemStack.getItem() == ItemInit.WIRE_SPOOL.get()) {
                                if (itemAmount <= itemStack.getCount()) {
                                    total += itemAmount;
                                    stacksFound.add(Pair.of(itemStack, itemAmount));
                                    full = true;
                                    break;
                                } else {
                                    total += itemStack.getCount();
                                    stacksFound.add(Pair.of(itemStack, itemStack.getCount()));
                                }
                                itemAmount -= itemStack.getCount();
                            }
                        }
                        if (!full) {
                            if (!world.isClientSide) {
                                player.displayClientMessage(Component.translatable("projectnublar.fences.length.notenough", itemMax, total), true);
                            }
                        } else {
                            if (!player.isCreative()) {
                                stacksFound.forEach(p -> p.getLeft().shrink(p.getRight()));
                            }
                            for (double offset : this.type.getOffsets()) {
                                List<BlockPos> positions = LineUtils.getBlocksInbetween(pos, other, offset);
                                for (int i = 0; i < this.type.getHeight(); i++) {
                                    BlockPos pos1 = pos.above(i);
                                    BlockPos other1 = other.above(i);
                                    for (int i1 = 0; i1 < positions.size(); i1++) {
                                        BlockPos position = positions.get(i1).above(i);
                                        if ((world.getBlockState(position).isAir() || world.getBlockState(position).canBeReplaced(Fluids.EMPTY)) && !(world.getBlockState(position).getBlock() instanceof ElectricFencePostBlock)) {
                                            world.setBlock(position, BlockInit.ELECTRIC_FENCE.get().defaultBlockState(), 3);
                                        }
                                        BlockEntity fencete = world.getBlockEntity(position);
                                        if (fencete instanceof ConnectableBlockEntity) {
                                            ((ConnectableBlockEntity) fencete).addConnection(new Connection(fencete, this.type, offset, pos1, other1, positions.get(Math.min(i1 + 1, positions.size() - 1)).above(i), positions.get(Math.max(i1 - 1, 0)).above(i), position));
                                        }
                                    }
                                }
                            }
                        }
                        nbt.put("fence_position", NbtUtils.writeBlockPos(pos));
                    } else {
                        nbt.remove("fence_position");
                    }
                } else {
                    nbt.put("fence_position", NbtUtils.writeBlockPos(pos));
                }
                return InteractionResult.SUCCESS;
            }
        } else if (world.getBlockState(pos.below(index)).getBlock() == this) {
            return this.use(world.getBlockState(pos.below(index)), world, pos.below(index), player, hand, ray);
        }
        return super.use(state, world, pos, player, hand, ray);
    }

    @Override
    public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
        BlockEntity BlockEntity = world.getBlockEntity(pos);
        if (BlockEntity instanceof BlockEntityElectricFencePole) {
            for (Connection connection : ((BlockEntityElectricFencePole) BlockEntity).getConnections()) {
                BlockPos blockpos = connection.getFrom();
                if (blockpos.equals(pos)) {
                    blockpos = connection.getTo();
                }
                if (world.getBlockState(blockpos).getBlock() != this) {
                    for (BlockPos blockPos : LineUtils.getBlocksInbetween(connection.getFrom(), connection.getTo(), connection.getOffset())) {
                        if (blockPos.equals(connection.getTo()) || blockPos.equals(connection.getFrom())) {
                            continue;
                        }
                        BlockEntity te = world.getBlockEntity(blockPos);
                        if (te instanceof ConnectableBlockEntity) {
                            boolean left = false;
                            for (Connection bitcon : ((ConnectableBlockEntity) te).getConnections()) {
                                if (connection.lazyEquals(bitcon)) {
                                    bitcon.setBroken(true);
                                }
                                left |= !bitcon.isBroken();
                            }
                            if (!left) {
                                world.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                            }
                        }
                    }
                }

            }
        }
        super.destroy(world, pos, state);
        if (!destroying) {
            destroying = true;
            int index = state.getValue(indexProperty);
            for (int i = 1; i < index + 1; i++) {
                world.setBlock(pos.below(i), Blocks.AIR.defaultBlockState(), 3); //TODO: verify if our block?
            }
            for (int i = 1; i < this.type.getHeight() - index; i++) {
                world.setBlock(pos.above(i), Blocks.AIR.defaultBlockState(), 3);
            }
            destroying = false;
        }
        super.destroy(world, pos, state);
    }


    @Override
    public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return state.getValue(POWERED_PROPERTY) && state.getValue(indexProperty) == this.type.getHeight() - 1 ? this.type.getLightLevel() : 0;
    }


    public ConnectionType getType() {
        return type;
    }

    public IntegerProperty getIndexProperty() {
        return indexProperty;
    }

    public static boolean isDestroying() {
        return destroying;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BlockEntityElectricFencePole(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, pBlockEntityType, (level, pos, state, be) -> ((BlockEntityElectricFencePole)be).tick(level, pos, state, (BlockEntityElectricFencePole)be));
    }
    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> pServerType, BlockEntityType<E> pClientType, BlockEntityTicker<? super E> pTicker) {
        return pClientType == pServerType ? (BlockEntityTicker<A>)pTicker : null;
    }
}
