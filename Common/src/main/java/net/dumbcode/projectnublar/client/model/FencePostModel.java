package net.dumbcode.projectnublar.client.model;

import net.dumbcode.projectnublar.block.entity.BlockEntityElectricFencePole;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FencePostModel extends GeoModel<BlockEntityElectricFencePole> {
    private static final Map<ResourceLocation, ResourceLocation> modelCache = new HashMap<>();
    private static final Map<ResourceLocation, ResourceLocation> textureCache = new HashMap<>();
    private static final Map<ResourceLocation, ResourceLocation> animationCache = new HashMap<>();
    @Override
    public ResourceLocation getModelResource(BlockEntityElectricFencePole animatable) {
        return modelCache.computeIfAbsent(BuiltInRegistries.BLOCK.getKey(animatable.getBlockState().getBlock()), k -> new ResourceLocation(k.getNamespace(), "geo/block/" + k.getPath() + ".geo.json"));
    }

    @Override
    public ResourceLocation getTextureResource(BlockEntityElectricFencePole animatable) {
        return textureCache.computeIfAbsent(BuiltInRegistries.BLOCK.getKey(animatable.getBlockState().getBlock()), k -> new ResourceLocation(k.getNamespace(), "textures/block/" + k.getPath() + ".png"));
    }

    @Override
    public ResourceLocation getAnimationResource(BlockEntityElectricFencePole animatable) {
        return null;
    }
}
