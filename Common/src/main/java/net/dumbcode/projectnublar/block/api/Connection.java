package net.dumbcode.projectnublar.block.api;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import earth.terrarium.botarium.common.energy.base.BotariumEnergyBlock;
import net.dumbcode.projectnublar.util.LineUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;


public class Connection {

    private final Runnable reRenderCallback;
    private final ConnectionType type;

    private final double offset;
    //Used to help compare Connections
    private final int toFromHash;
    private final BlockPos from;
    private final BlockPos to;

    private final BlockPos next;
    private final BlockPos previous;

    boolean sign;

    private final BlockPos position;
    private final int compared;

    private RenderData renderData;

    private boolean broken;

    private final double[] in;
    private final boolean valid;

    private final double xzlen;
    private final double fullLen;

    private final Random random;

    private final Vec3 center;
    private final RotatedRayBox rayBox;

    private final SurroundingCache prevCache;
    private final SurroundingCache nextCache;

    private final VoxelShape collisionShape;

    public Connection(BlockEntity internalBlockEntity, ConnectionType type, double offset, BlockPos from, BlockPos to, BlockPos previous, BlockPos next, BlockPos position) {
        this(() -> {
//            internalBlockEntity.requestModelDataUpdate();
            Level level = internalBlockEntity.getLevel();
            if(level != null) {
//                TileEntity p = level.getBlockEntity(previous);
//                if(p instanceof ConnectableBlockEntity) {
//                    p.requestModelDataUpdate();
//                }
//                TileEntity n = level.getBlockEntity(next);
//                if(n instanceof ConnectableBlockEntity) {
//                    n.requestModelDataUpdate();
//                }
                level.sendBlockUpdated(position, Blocks.AIR.defaultBlockState(), internalBlockEntity.getBlockState(), 3);
            }
        }, type, offset, from, to, previous, next, position);
    }

    private Connection(Runnable reRenderCallback, ConnectionType type, double offset, BlockPos from, BlockPos to, BlockPos previous, BlockPos next, BlockPos position) {
        this.reRenderCallback = reRenderCallback;
        this.type = type;
        this.offset = offset;
        this.position = position;

        if ((this.compared = (from.getX() == to.getX() ? to.getZ() - from.getZ() : from.getX() - to.getX())) < 0) {
            BlockPos ref = from;
            from = to;
            to = ref;

            ref = previous;
            previous = next;
            next = ref;

        }

        this.from = from;
        this.to = to;
        this.next = next;
        this.previous = previous;

        this.toFromHash = (this.compared < 0 ? this.from : this.to).hashCode() + (this.compared < 0 ? this.to : this.from).hashCode() * 31;

        double[] intercept = LineUtils.intersect(this.position, this.from, this.to, this.offset);
        if (intercept == null) {
            intercept = new double[6]; //ew
            this.valid = false;
        } else {
            this.valid = true;
        }

        this.random = new Random(this.getPosition().asLong() * (long) (this.getOffset() * 1000));
        this.in = intercept;
        double w = this.type.getCableWidth();
        this.xzlen = Math.sqrt((this.in[1] - this.in[0]) * (this.in[1] - this.in[0]) + (this.in[3] - this.in[2]) * (this.in[3] - this.in[2]));
        this.fullLen = Math.sqrt(this.xzlen * this.xzlen + (this.in[5] - this.in[4]) * (this.in[5] - this.in[4]));

        this.center = new Vec3((this.in[0] + this.in[1]) / 2, (this.in[4] + this.in[5]) / 2, (this.in[2] + this.in[3]) / 2);
        this.rayBox = new RotatedRayBox.Builder(new AABB(0, -w, -w, -this.fullLen, w, w))
            .origin(this.in[0], this.in[4], this.in[2])
            .rotate(Math.atan((this.in[5] - this.in[4]) / this.xzlen), 0, 0, 1)
            .rotate(this.in[1] == this.in[0] ? Math.PI * 1.5D : Math.atan((this.in[3] - this.in[2]) / (this.in[1] - this.in[0])), 0, 1, 0)
            .build();

        this.prevCache = this.genCache(false);
        this.nextCache = this.genCache(true);

        //todo: fix this
//        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> this.renderData = this.buildRenderData());
        this.renderData = this.buildRenderData();

        VoxelShape collisionShape = Shapes.empty();
        for (BlockConnectableBase.ConnectionAxisAlignedBB bb : BlockConnectableBase.createBoundingBox(Collections.singleton(this), position)) {
            collisionShape = Shapes.or(collisionShape, Shapes.create(bb));
        }
        this.collisionShape = collisionShape;
    }

    public Connection setBroken(boolean broken) {
        this.broken = broken;
        this.reRenderCallback.run();
        return this;
    }

    private Connection silentlySetBroken(boolean broken) {
        this.broken = broken;
        return this;
    }

    private SurroundingCache genCache(boolean next) {
        Vec3 point = new Vec3((next ? 1 : -1) * this.fullLen / 2, 0, 0);
        double w = this.type.getCableWidth();
        RotatedRayBox fixedBox = new RotatedRayBox.Builder(new AABB(0, -w, -w, -this.fullLen / 2, w, w))
            .origin(next ? this.center.x : this.in[0], next ? this.center.y : this.in[4], next ? this.center.z : this.in[2])
            .rotate(Math.atan((this.in[5] - this.in[4]) / this.xzlen), 0, 0, 1)
            .rotate(this.in[1] == this.in[0] ? Math.PI * 1.5D : Math.atan((this.in[3] - this.in[2]) / (this.in[1] - this.in[0])), 0, 1, 0)
            .build();
        double yang = (this.random.nextFloat() - 0.5F) * Math.PI / 3F;
        double zang = (this.random.nextFloat() - 0.5F) * Math.PI / 3F;
        RotatedRayBox rotatedBox = this.genRotatedBox((next ? 1 : -1) * this.fullLen / 2, yang, zang);

        Vector4f vec4 = new Vector4f(new Vector3f((float) point.x, (float) point.y, (float) point.z),1.0f);
        vec4.mul(rotatedBox.getBackwards());
        point = new Vec3(vec4.x(), vec4.y(), vec4.z());

        AABB aabb = new AABB(this.position);
        Vec3 centerVec = new Vec3(this.center.x, this.center.y, this.center.z);
        Vec3 vec3d = new Vec3(point.x, point.y, point.z).add(centerVec);
        if (!aabb.contains(vec3d)) { //Point outside of bounding box. Cant happen for selction box reasons
            Optional<Vec3> clip = aabb.clip(centerVec, vec3d);
            if (clip.isPresent()) {
                double dist = clip.get().distanceTo(centerVec) * (next ? 1 : -1);
                rotatedBox = this.genRotatedBox(dist, yang, zang);
                point = new Vec3(dist, 0, 0);

                vec4 = new Vector4f(new Vector3f((float)point.x, (float)point.y, (float)point.z), 1.0f);
                vec4.mul(rotatedBox.getBackwards());
                point = new Vec3(vec4.x(), vec4.y(), vec4.z());
            }
        }
        return new SurroundingCache(new Vector3f((float)point.x, (float)point.y, (float)point.z), fixedBox, rotatedBox);
    }

    public Connection copy() {
        return new Connection(this.reRenderCallback, this.type, this.offset, this.from, this.to, this.previous, this.next, this.position).setBroken(this.broken);
    }

    private RotatedRayBox genRotatedBox(double length, double yang, double zang) {
        double w = this.type.getCableWidth();
        return new RotatedRayBox.Builder(new AABB(0, -w, -w, length, w, w))
            .origin(this.center.x, this.center.y, this.center.z)
            .rotate((this.in[1] == this.in[0] ? Math.PI * 1.5D : Math.atan((this.in[3] - this.in[2]) / (this.in[1] - this.in[0]))) + yang, 0, 1, 0)
            .rotate(Math.atan((this.in[5] - this.in[4]) / this.xzlen) + zang, 0, 0, 1)
            .build();
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        nbt.putString("id", this.type.getRegistryName().toString());
        nbt.putDouble("offset", this.offset);
        nbt.put("from", NbtUtils.writeBlockPos(this.getFrom()));
            nbt.put("to", NbtUtils.writeBlockPos(this.getTo()));
        nbt.putBoolean("sign", this.sign);
        nbt.put("next", NbtUtils.writeBlockPos(this.next));
        nbt.put("previous", NbtUtils.writeBlockPos(this.previous));
        nbt.putBoolean("broken", this.broken);
        return nbt;
    }


    public static Connection fromNBT(CompoundTag nbt, BlockEntity tileEntity) {
        return new Connection(
            tileEntity,
            ConnectionType.getType(new ResourceLocation(nbt.getString("id"))),
            nbt.getDouble("offset"),
            NbtUtils.readBlockPos(nbt.getCompound("from")),
            NbtUtils.readBlockPos(nbt.getCompound("to")),
            NbtUtils.readBlockPos(nbt.getCompound("previous")),
            NbtUtils.readBlockPos(nbt.getCompound("next")),
            tileEntity.getBlockPos()
        ).silentlySetBroken(nbt.getBoolean("broken")).setSign(nbt.getBoolean("sign"));
    }

    public boolean lazyEquals(Connection con) {
        return this.getFrom().equals(con.getFrom()) && this.getTo().equals(con.getTo()) && this.offset == con.offset;
    }

    public BlockPos getMin() {
        return this.compared < 0 ? this.to : this.from;
    }

    public BlockPos getMax() {
        return this.compared >= 0 ? this.to : this.from;
    }

    public boolean brokenSide(BlockGetter world, boolean next) {
        BlockEntity te = world.getBlockEntity(next == this.compared < 0 ? this.previous : this.next);
        if (te instanceof ConnectableBlockEntity) {
            ConnectableBlockEntity fe = (ConnectableBlockEntity) te;
            for (Connection fenceConnection : fe.getConnections()) {
                if (this.lazyEquals(fenceConnection) && fenceConnection.isBroken()) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public boolean isPowered(BlockGetter world) {
        for (BlockPos pos : LineUtils.getBlocksInbetween(this.from, this.to, this.offset)) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof ConnectableBlockEntity) {
                if (!this.isContactablePowerAllowed((ConnectableBlockEntity) te)) {
                    return false;
                }
            } else {
                return false;
            }
        }

        //todo: energy
        for (BlockPos pos : Lists.newArrayList(this.from, this.to)) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof BotariumEnergyBlock wbec && wbec.getEnergyStorage().getStoredEnergy() > 0) {
                return true;
            }
        }
//todo: return to false when energy
        return false;
    }

    private boolean isContactablePowerAllowed(ConnectableBlockEntity connectable) {
        boolean has = false;
        for (Connection connection : connectable.getConnections()) {
            if (connection.lazyEquals(this)) {
                if (connection.isBroken()) {
                    return false;
                }
                has = true;
                break;
            }
        }
        return has;
    }


    public CompiledRenderData compileRenderData(BlockGetter world) {
        List<float[]> out = new ArrayList<>();

        boolean pb = this.brokenSide(world, false);
        boolean nb = this.brokenSide(world, true);
        if(!this.isBroken()) {
            if (nb) {
                out.add(this.renderData.nextRotated());
                if (!pb) {
                    out.add(this.renderData.nextFixed());
                }
            }
            if (pb) {
                out.add(this.renderData.prevRotated());
                if (!nb) {
                    out.add(this.renderData.prevFixed());
                }
            }
            if (!pb && !nb) {
                out.add(this.renderData.data());
            }
        }

        return new CompiledRenderData(this.isSign(), out);
    }


    private RenderData buildRenderData() {
        double halfthick = this.type.getCableWidth() / 2F;

        double posdist = this.distance(this.from, this.to.getX() + 0.5F, this.to.getZ() + 0.5F);
        double yrange = posdist == 0 ? 1 : (this.to.getY() - this.from.getY()) / posdist;
        double tangrad = this.in[1] == this.in[0] ? Math.PI / 2D : Math.atan((this.in[2] - this.in[3]) / (this.in[1] - this.in[0]));
        double xcomp = halfthick * Math.sin(tangrad);
        double zcomp = halfthick * Math.cos(tangrad);
        double tangrady = posdist == 0 ? Math.PI / 2D : Math.atan((this.to.getY() - this.from.getY()) / posdist);
        double yxzcomp = Math.sin(tangrady);
        double[] ct = new double[]{
            this.in[0] - xcomp + yxzcomp * zcomp, this.in[2] - zcomp - yxzcomp * xcomp,
            this.in[1] - xcomp + yxzcomp * zcomp, this.in[3] - zcomp - yxzcomp * xcomp,
            this.in[1] + xcomp + yxzcomp * zcomp, this.in[3] + zcomp - yxzcomp * xcomp,
            this.in[0] + xcomp + yxzcomp * zcomp, this.in[2] + zcomp - yxzcomp * xcomp
        };
        double[] cb = new double[]{
            this.in[0] - xcomp - yxzcomp * zcomp, this.in[2] - zcomp + yxzcomp * xcomp,
            this.in[1] - xcomp - yxzcomp * zcomp, this.in[3] - zcomp + yxzcomp * xcomp,
            this.in[1] + xcomp - yxzcomp * zcomp, this.in[3] + zcomp + yxzcomp * xcomp,
            this.in[0] + xcomp - yxzcomp * zcomp, this.in[2] + zcomp + yxzcomp * xcomp
        };
        double[] cent = new double[]{
            (ct[0] + ct[2]) / 2D,
            (ct[1] + ct[3]) / 2D,
            (ct[4] + ct[6]) / 2D,
            (ct[5] + ct[7]) / 2D
        };
        double[] cenb = new double[]{
            (cb[0] + cb[2]) / 2D,
            (cb[1] + cb[3]) / 2D,
            (cb[4] + cb[6]) / 2D,
            (cb[5] + cb[7]) / 2D
        };
        double ytop = yrange * this.distance(this.from, this.in[0], this.in[2]) - this.position.getY() + this.from.getY();
        double ybot = yrange * this.distance(this.from, this.in[1], this.in[3]) - this.position.getY() + this.from.getY();
        double len = Math.sqrt(Math.pow(ct[0] == ct[2] ? ct[1] - ct[3] : ct[0] - ct[2], 2) + (ytop - ybot) * (ytop - ybot)) / (halfthick * 32F);
        double yThick = halfthick * Math.cos(tangrady);
        double x = -this.position.getX();
        double y = this.offset;
        double z = -this.position.getZ();

        float worldWidth = this.type.getCableWidth() * 32;
        double uvLen = (Math.sqrt(
            Math.pow(this.in[0]-this.in[1], 2)
                + Math.pow(this.in[2]-this.in[3], 2)
                + Math.pow(this.in[4]-this.in[5], 2)
        )) / worldWidth;

        Pair<float[], float[]> prevRenderCache = this.genRenderCache(x, y, z, false, new double[]{ct[0], ct[1], cent[0], cent[1], cent[2], cent[3], ct[6], ct[7]}, new double[]{cb[0], cb[1], cenb[0], cenb[1], cenb[2], cenb[3], cb[6], cb[7]}, yThick, uvLen, worldWidth, ytop, ybot);
        Pair<float[], float[]> nextRenderCache = this.genRenderCache(x, y, z, true, new double[]{cent[0], cent[1], ct[2], ct[3], ct[4], ct[5], cent[2], cent[3]}, new double[]{cenb[0], cenb[1], cb[2], cb[3], cb[4], cb[5], cenb[2], cenb[3]}, yThick, uvLen, worldWidth, ytop, ybot);

        int maximumTexSize = (int) Math.min(64, Math.ceil(Math.max(uvLen * 16, worldWidth) * 2));
        double[] uvs = IntStream.range(0, 12).mapToDouble(i -> this.random.nextInt(65 - maximumTexSize) / 64F).toArray();
        float[] main = Floats.toArray(Doubles.asList(
            ct[0] + x, ytop + yThick + y, ct[1] + z,
            ct[2] + x, ybot + yThick + y, ct[3] + z,
            ct[6] + x, ytop + yThick + y, ct[7] + z,
            ct[4] + x, ybot + yThick + y, ct[5] + z,
            cb[0] + x, ytop - yThick + y, cb[1] + z,
            cb[2] + x, ybot - yThick + y, cb[3] + z,
            cb[6] + x, ytop - yThick + y, cb[7] + z,
            cb[4] + x, ybot - yThick + y, cb[5] + z,
            uvs[0], uvs[1],
            uvs[2], uvs[3],
            uvs[4], uvs[5],
            uvs[6], uvs[7],
            uvs[8], uvs[9],
            uvs[10], uvs[11],
            uvLen/2F, 1F/64F, 1F/64F
        ));
        return new RenderData(main, prevRenderCache.getLeft(), nextRenderCache.getLeft(), prevRenderCache.getRight(), nextRenderCache.getRight());
    }

    private double distance(BlockPos from, double x, double z) {
        return Math.sqrt((from.getX() + 0.5F - x) * (from.getX() + 0.5F - x) + (from.getZ() + 0.5F - z) * (from.getZ() + 0.5F - z));
    }

    private Pair<float[], float[]> genRenderCache(double x, double y, double z, boolean next, double[] ct, double[] cb, double yThick, double len, double worldWidth, double ytop, double ybot) {
        Vector3f point = next ? this.nextCache.point : this.prevCache.point;
        double ycenter = ybot + (ytop - ybot) / 2D;
        int maximumTexSize = (int) Math.min(64, Math.ceil(Math.max(len * 16, worldWidth) * 2));
        double[] uvs = IntStream.range(0, 12).mapToDouble(i -> this.random.nextInt(65 - maximumTexSize) / 64F).toArray();
        float[] rotated;
        if (next) {
            ytop = ycenter;
            rotated = Floats.toArray(Doubles.asList(
                x + ct[0] + point.x(), y + ycenter + yThick + point.y(), z + ct[1] + point.z(),
                x + ct[0], y + ycenter + yThick, z + ct[1],
                x + ct[6] + point.x(), y + ycenter + yThick + point.y(), z + ct[7] + point.z(),
                x + ct[6], y + ycenter + yThick, z + ct[7],

                x + cb[0] + point.x(), y + ycenter - yThick + point.y(), z + cb[1] + point.z(),
                x + cb[0], y + ycenter - yThick, z + cb[1],
                x + cb[6] + point.x(), y + ycenter - yThick + point.y(), z + cb[7] + point.z(),
                x + cb[6], y + ycenter - yThick, z + cb[7],

                uvs[0], uvs[1],
                uvs[2], uvs[3],
                uvs[4], uvs[5],
                uvs[6], uvs[7],
                uvs[8], uvs[9],
                uvs[10], uvs[11],
                len/4F, 1F/64F, 1F/64F
            ));
        } else {
            ybot = ycenter;
            rotated = Floats.toArray(Doubles.asList(
                x + ct[2], y + ycenter + yThick, z + ct[3],
                x + ct[2] + point.x(), y + ycenter + yThick + point.y(), z + ct[3] + point.z(),
                x + ct[4], y + ycenter + yThick, z + ct[5],
                x + ct[4] + point.x(), y + ycenter + yThick + point.y(), z + ct[5] + point.z(),

                x + cb[2], y + ycenter - yThick, z + cb[3],
                x + cb[2] + point.x(), y + ycenter - yThick + point.y(), z + cb[3] + point.z(),
                x + cb[4], y + ycenter - yThick, z + cb[5],
                x + cb[4] + point.x(), y + ycenter - yThick + point.y(), z + cb[5] + point.z(),

                uvs[0], uvs[1],
                uvs[2], uvs[3],
                uvs[4], uvs[5],
                uvs[6], uvs[7],
                uvs[8], uvs[9],
                uvs[10], uvs[11],
                len/4F, 1F/64F, 1F/64F
            ));
        }
        uvs = IntStream.range(0, 12).mapToDouble(i -> this.random.nextInt(65 - maximumTexSize) / 64F).toArray();
        float[] fixed =
            Floats.toArray(Doubles.asList(
                x + ct[0], y + ytop + yThick, z + ct[1],
                x + ct[2], y + ybot + yThick, z + ct[3],
                x + ct[6], y + ytop + yThick, z + ct[7],
                x + ct[4], y + ybot + yThick, z + ct[5],
                x + cb[0], y + ytop - yThick, z + cb[1],
                x + cb[2], y + ybot - yThick, z + cb[3],
                x + cb[6], y + ytop - yThick, z + cb[7],
                x + cb[4], y + ybot - yThick, z + cb[5],
                uvs[0], uvs[1],
                uvs[2], uvs[3],
                uvs[4], uvs[5],
                uvs[6], uvs[7],
                uvs[8], uvs[9],
                uvs[10], uvs[11],
                len/4F, 1F/64F, 1F/64F
            ));
        return Pair.of(fixed, rotated);
    }

    public Runnable getReRenderCallback() {
        return reRenderCallback;
    }

    public ConnectionType getType() {
        return type;
    }

    public double getOffset() {
        return offset;
    }

    public int getToFromHash() {
        return toFromHash;
    }

    public BlockPos getFrom() {
        return from;
    }

    public BlockPos getTo() {
        return to;
    }

    public BlockPos getNext() {
        return next;
    }

    public BlockPos getPrevious() {
        return previous;
    }

    public boolean isSign() {
        return sign;
    }

    public Connection setSign(boolean sign) {
        this.sign = sign;
        return this;
    }

    public BlockPos getPosition() {
        return position;
    }

    public int getCompared() {
        return compared;
    }

    public RenderData getRenderData() {
        return renderData;
    }

    public void setRenderData(RenderData renderData) {
        this.renderData = renderData;
    }

    public boolean isBroken() {
        return broken;
    }

    public double[] getIn() {
        return in;
    }

    public boolean isValid() {
        return valid;
    }

    public double getXzlen() {
        return xzlen;
    }

    public double getFullLen() {
        return fullLen;
    }

    public Random getRandom() {
        return random;
    }

    public Vec3 getCenter() {
        return center;
    }

    public RotatedRayBox getRayBox() {
        return rayBox;
    }

    public SurroundingCache getPrevCache() {
        return prevCache;
    }

    public SurroundingCache getNextCache() {
        return nextCache;
    }

    public VoxelShape getCollisionShape() {
        return collisionShape;
    }

    public record SurroundingCache(Vector3f point, RotatedRayBox fixedBox, RotatedRayBox rotatedBox) {
    }

    //Each array is length 39, and should be passed to RenderUtils.drawSpacedCube

    public record RenderData(float[] data, float[] prevFixed, float[] nextFixed, float[] prevRotated, float[] nextRotated) {
    }


    public record CompiledRenderData(boolean renderSign, List<float[]> connectionData) {
    }
}
