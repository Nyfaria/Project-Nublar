package net.dumbcode.projectnublar.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.dumbcode.projectnublar.api.Genes;
import net.dumbcode.projectnublar.client.renderer.layer.DinoLayer;
import net.dumbcode.projectnublar.entity.Dinosaur;
import net.dumbcode.projectnublar.init.GeneInit;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.Arrays;
import java.util.List;

public class DinosaurRenderer extends GeoEntityRenderer<Dinosaur> {
    public DinosaurRenderer(EntityRendererProvider.Context renderManager, GeoModel<Dinosaur> model, List<DinoLayer> layers) {
        super(renderManager, model);
        for(DinoLayer layer : layers) {
            this.addRenderLayer(new GeoRenderLayer<>(this) {
                @Override
                public void render(PoseStack poseStack, Dinosaur animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
                    Color color = animatable.layerColor(layers.indexOf(layer) + 1, layer);
                    buffer = bufferSource.getBuffer(RenderType.entityTranslucent(getTextureResource(animatable)));
                    reRender(bakedModel, poseStack, bufferSource, animatable, renderType, buffer, partialTick, packedLight, packedOverlay, color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), 1f);
//                    reRender(bakedModel, poseStack, bufferSource, animatable, renderType, buffer, partialTick, packedLight, packedOverlay, 1, 1, 1, 1);

                }

                @Override
                public GeoModel<Dinosaur> getGeoModel() {
                    return DinosaurRenderer.this.getGeoModel();
                }

                @Override
                protected ResourceLocation getTextureResource(Dinosaur animatable) {
                    return layer.getTextureLocation(animatable);
                }
            });
        }
    }


    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, Dinosaur animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float scale = ((float) animatable.getDinoData().getGeneValue(GeneInit.SIZE.get()) / 100) + 1.0f;
        super.scaleModelForRender(scale, scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }

    private Color color = null;

    @Override
    public Color getRenderColor(Dinosaur animatable, float partialTick, int packedLight) {
        return animatable.layerColor(0, null);
    }

//    @Override
//    public RenderType getRenderType(Dinosaur animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
//        return RenderType.entityTranslucent(texture);
//    }
}
