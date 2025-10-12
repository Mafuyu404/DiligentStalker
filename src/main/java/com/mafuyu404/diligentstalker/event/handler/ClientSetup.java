package com.mafuyu404.diligentstalker.event.handler;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.compat.KeyPrompts;
import com.mafuyu404.diligentstalker.registry.StalkerBlockEntities;
import com.mafuyu404.diligentstalker.registry.StalkerEntities;
import com.mafuyu404.diligentstalker.render.ArrowStalkerRender;
import com.mafuyu404.diligentstalker.render.CameraStalkerRenderer;
import com.mafuyu404.diligentstalker.render.DroneStalkerModel;
import com.mafuyu404.diligentstalker.render.DroneStalkerRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = DiligentStalker.MODID, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(KeyPrompts::init);
    }

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(
                DroneStalkerRenderer.LAYER,
                DroneStalkerModel::createBodyLayer
        );
    }

    @SubscribeEvent
    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(StalkerEntities.DRONE_STALKER.get(), DroneStalkerRenderer::new);
        event.registerEntityRenderer(StalkerEntities.ARROW_STALKER.get(), ArrowStalkerRender::new);
        event.registerEntityRenderer(StalkerEntities.VOID_STALKER.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(StalkerEntities.CAMERA_STALKER.get(), NoopRenderer::new);

        event.registerBlockEntityRenderer(StalkerBlockEntities.CAMERA_STALKER.get(), CameraStalkerRenderer::new);
    }
}
