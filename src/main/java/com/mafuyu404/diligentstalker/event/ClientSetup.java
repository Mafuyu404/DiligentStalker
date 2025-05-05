package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.registry.KeyBindings;
import com.mafuyu404.diligentstalker.render.ArrowStalkerRender;
import com.mafuyu404.diligentstalker.registry.StalkerEntities;
import com.mafuyu404.diligentstalker.render.DroneStalkerRenderer;
import com.mafuyu404.diligentstalker.render.DroneStalkerModel;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(StalkerEntities.DRONE_STALKER.get(), DroneStalkerRenderer::new);
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
    }
    @SubscribeEvent
    public static void registerKeyMapping(RegisterKeyMappingsEvent event) {
        // 注册键位
        event.register(KeyBindings.DISCONNECT);
    }
}
