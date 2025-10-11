package com.mafuyu404.diligentstalker.event.handler;

import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.registry.KeyBindings;
import com.mafuyu404.diligentstalker.registry.StalkerBlockEntities;
import com.mafuyu404.diligentstalker.registry.StalkerEntities;
import com.mafuyu404.diligentstalker.render.ArrowStalkerRender;
import com.mafuyu404.diligentstalker.render.CameraStalkerRenderer;
import com.mafuyu404.diligentstalker.render.DroneStalkerModel;
import com.mafuyu404.diligentstalker.render.DroneStalkerRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class ClientSetup implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        setup();
        registerEntityRenderers();
        registerLayerDefinitions();
        registerKeyBindings();
        registerBlockEntityRenderers();
    }

    private void registerLayerDefinitions() {
        EntityModelLayerRegistry.registerModelLayer(
                DroneStalkerRenderer.LAYER,
                DroneStalkerModel::createBodyLayer
        );
    }

    private void registerKeyBindings() {
        KeyBindingHelper.registerKeyBinding(KeyBindings.DISCONNECT);
        KeyBindingHelper.registerKeyBinding(KeyBindings.CONTROL);
        KeyBindingHelper.registerKeyBinding(KeyBindings.VIEW);
    }

    private void registerEntityRenderers() {
        EntityRendererRegistry.register(StalkerEntities.DRONE_STALKER, DroneStalkerRenderer::new);
        EntityRendererRegistry.register(StalkerEntities.ARROW_STALKER, ArrowStalkerRender::new);
        EntityRendererRegistry.register(StalkerEntities.VOID_STALKER, ThrownItemRenderer::new);
        EntityRendererRegistry.register(StalkerEntities.CAMERA_STALKER, NoopRenderer::new);
    }

    private void registerBlockEntityRenderers() {
        BlockEntityRenderers.register(StalkerBlockEntities.CAMERA_STALKER, CameraStalkerRenderer::new);
    }

    private void setup() {
        NetworkHandler.registerClient();
        ChunkLoadTask.initClientTick();
        DroneStalkerHUD.initHud();
        HideEXPBar.onRenderExperienceBar();
        StalkerControl.initClientEvents();
    }
}