package net.dumbcode.projectnublar.api;

import net.dumbcode.projectnublar.CommonClass;
import net.dumbcode.projectnublar.Constants;
import net.dumbcode.projectnublar.config.FossilsConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DNAData {
    private EntityType<?> entityType;
    private double dnaPercentage;
    private String variant;
    private FossilPiece fossilPiece;
    private Quality quality;
    boolean isEmbryo;
    private DyeColor tFish1 = DyeColor.BLACK;
    private DyeColor tFish2 = DyeColor.BLACK;

    public DNAData() {
    }

    public DyeColor gettFish1() {
        return tFish1;
    }

    public DyeColor gettFish2() {
        return tFish2;
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType<?> entityType) {
        this.entityType = entityType;
    }

    public double getDnaPercentage() {
        if (quality != null) {
            return FossilsConfig.getQuality(quality.getName()).dnaYield().get() / 100d;
        }
        return dnaPercentage;
    }

    public void setDnaPercentage(double dnaPercentage) {
        this.dnaPercentage = dnaPercentage;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public boolean isEmbryo() {
        return isEmbryo;
    }

    public void setEmbryo(boolean embryo) {
        isEmbryo = embryo;
    }

    public FossilPiece getFossilPiece() {
        return fossilPiece;
    }

    public void setFossilPiece(FossilPiece fossilPiece) {
        this.fossilPiece = fossilPiece;
    }

    public Quality getQuality() {
        return quality;
    }

    public void setQuality(Quality quality) {
        this.quality = quality;
    }

    public String getNameSpace() {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getNamespace();
    }

    public String getPath() {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath();
    }


    public String getStorageName() {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entityType) + (variant == null ? "" : "_" + variant);
    }

    public static void createTooltip(ItemStack stack, List<Component> tooltip) {
        if (stack.hasTag()) {
            DNAData dnaData = loadFromNBT(stack.getTag().getCompound("DNAData"));
            tooltip.add(dnaData.getFormattedType());
            if (dnaData.getDnaPercentage() != 0)
                tooltip.add(dnaData.getFormattedDNA());
            if (dnaData.getQuality() != null) {
                tooltip.add(Component.translatable("quality." + Constants.MODID + "." + dnaData.getQuality().getName()));
            }
            if (dnaData.variant != null) {
                tooltip.add(Component.literal(CommonClass.checkReplace(dnaData.variant)));
            }
            dnaData.addTFishTT(tooltip);
        }
    }

    public void addTFishTT(List<Component> tooltip) {
        if (tFish1 != DyeColor.BLACK) {
            tooltip.add(Component.translatable("tooltip." + Constants.MODID + ".tropical", Component.translatable("color.minecraft." + tFish1.getName()), Component.translatable("color.minecraft." + tFish2.getName())));
        }
    }

    public static DNAData combineDNA(DNAData dna1, DNAData dna2) {
        DNAData dnaData = new DNAData();
        if (dna1.getStorageName().equals(dna2.getStorageName())) {
            dnaData.setEntityType(dna1.getEntityType());
            dnaData.setDnaPercentage(Math.min(1.0d, dna1.getDnaPercentage() + dna2.getDnaPercentage()));
            dnaData.setVariant(dna1.getVariant());
            dnaData.setFossilPiece(dna1.getFossilPiece());
            dnaData.setQuality(dna1.getQuality());
            dnaData.setEmbryo(false);
            return dnaData;
        }
        return null;
    }

    public MutableComponent getFormattedType() {
        String localVariant = "";
        if (getVariant() != null) {
            if (entityType.getDescription().getString().toLowerCase().contains("parrot"))
                localVariant = CommonClass.checkReplace(variant);
            else if (entityType.getDescription().getString().toLowerCase().contains("cat"))
                localVariant = CommonClass.checkReplace(new ResourceLocation(variant).getPath());
        }
        return Component.literal(localVariant + getEntityType().getDescription().getString());
    }

    public MutableComponent getFormattedDNA() {
        return Component.literal(Mth.floor(getDnaPercentage() * 100) + "% DNA");
    }

    public MutableComponent getFormattedDNANoDescriptor() {
        return Component.literal(Mth.floor(getDnaPercentage() * 100) + "%");
    }

    public CompoundTag saveToNBT(CompoundTag tag) {
        tag.putString("entityType", BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString());
        if (dnaPercentage != 0)
            tag.putDouble("dnaPercentage", dnaPercentage);
        if (variant != null)
            tag.putString("variant", variant);
        if (fossilPiece != null)
            tag.putString("fossilPiece", fossilPiece.name());
        if (quality != null)
            tag.putString("quality", quality.getName());
        tag.putBoolean("isEmbryo", isEmbryo);
        return tag;
    }

    public void addTFish(TropicalFish tropicalFish) {
        tFish1 = tropicalFish.getPatternColor();
        tFish2 = tropicalFish.getBaseColor();
    }

    public static DNAData loadFromNBT(CompoundTag tag) {
        DNAData dnaData = new DNAData();
        dnaData.setEntityType(BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(tag.getString("entityType"))));
        if (tag.contains("dnaPercentage"))
            dnaData.setDnaPercentage(tag.getDouble("dnaPercentage"));
        if (tag.contains("variant"))
            dnaData.setVariant(tag.getString("variant"));
        if (tag.contains("fossilPiece"))
            dnaData.setFossilPiece(FossilPieces.getPieceByName(tag.getString("fossilPiece")));
        if (tag.contains("quality"))
            dnaData.setQuality(Quality.byName(tag.getString("quality")));
        dnaData.setEmbryo(tag.getBoolean("isEmbryo"));
        return dnaData;
    }

    public static DNAData fromDrive(ItemStack stack, EntityType<?> entityType) {

        return loadFromNBT(stack.getTag().getCompound("DNAData"));
    }

    public static String createStorageKey(EntityType<?> entityType, String variant) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entityType) + (variant == null ? "" : "_" + variant);
    }

    public DinoData.EntityInfo getEntityInfo() {
        return new DinoData.EntityInfo(entityType, variant);
    }
}
