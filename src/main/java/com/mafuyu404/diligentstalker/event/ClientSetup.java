package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.model.ArrowStalkerRender;
import com.mafuyu404.diligentstalker.registry.ModEntities;
import com.mafuyu404.diligentstalker.model.DroneStalkerRenderer;
import com.mafuyu404.diligentstalker.model.DroneStalkerModel;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.DRONE_STALKER.get(), DroneStalkerRenderer::new);
    }
    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        // 创建自定义模型层
        event.registerLayerDefinition(
                DroneStalkerRenderer.LAYER,
                DroneStalkerModel::createBodyLayer
        );
    }
    @SubscribeEvent
    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.DRONE_STALKER.get(), DroneStalkerRenderer::new);
        event.registerEntityRenderer(ModEntities.ARROW_STALKER.get(), ArrowStalkerRender::new);
    }
}
