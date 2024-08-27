package net.dumbcode.projectnublar.client.model.testtube;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.dumbcode.projectnublar.Constants;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public class TestTubeModelLoader implements IGeometryLoader<TestTubeModelLoader.TestTubeModelGeometry> {
    public static final ResourceLocation GENERATOR = Constants.modLoc( "test_tube");

    @Override
    public TestTubeModelGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {

        return new TestTubeModelGeometry();
    }
    public record TestTubeModelGeometry() implements IUnbakedGeometry<TestTubeModelGeometry> {


        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
            return new TestTubeItemModel();
        }
    }
}
