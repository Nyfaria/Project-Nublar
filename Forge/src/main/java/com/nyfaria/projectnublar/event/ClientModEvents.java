package com.nyfaria.projectnublar.event;

import com.nyfaria.projectnublar.Constants;
import com.nyfaria.projectnublar.client.ClientRegistrationHolder;
import com.nyfaria.projectnublar.client.model.fossil.FossilModelLoader;
import com.nyfaria.projectnublar.client.model.testtube.TestTubeModelLoader;
import com.nyfaria.projectnublar.client.renderer.ProcessorRenderer;
import com.nyfaria.projectnublar.init.BlockInit;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Constants.MODID,value= Dist.CLIENT,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerBakedModels(ModelEvent.RegisterGeometryLoaders event) {
         event.register("fossil", new FossilModelLoader());
         event.register("test_tube", new TestTubeModelLoader());
    }
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event){
        ClientRegistrationHolder.entityRenderers().forEach((key, value) -> event.registerEntityRenderer(key.get(), value));
        ClientRegistrationHolder.getBlockEntityRenderers().forEach((key, value) -> event.registerBlockEntityRenderer(key.get(), value));
    }
    @SubscribeEvent
    public static void onFMLClient(FMLClientSetupEvent event) {
        ClientRegistrationHolder.menuScreens();
    }
}
