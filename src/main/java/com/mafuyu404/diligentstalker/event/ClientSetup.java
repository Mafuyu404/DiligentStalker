package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.registry.KeyBindings;
import com.mafuyu404.diligentstalker.registry.StalkerBlockEntities;
import com.mafuyu404.diligentstalker.render.ArrowStalkerRender;
import com.mafuyu404.diligentstalker.registry.StalkerEntities;
import com.mafuyu404.diligentstalker.render.CameraStalkerRenderer;
import com.mafuyu404.diligentstalker.render.DroneStalkerRenderer;
import com.mafuyu404.diligentstalker.render.DroneStalkerModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

@Environment(EnvType.CLIENT)
public class ClientSetup implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        ClientEvents.init();
        StalkerManage.init();
        HideEXPBar.init();
        ChunkLoadTask.init();
        DroneStalkerHUD.init();
        ModSetup.init();
        StalkerControl.init();
        // 注册实体渲染器
        registerEntityRenderers();

        // 注册层定义
        registerLayerDefinitions();

        // 注册键位绑定
        registerKeyBindings();

        // 注册方块实体渲染器
        registerBlockEntityRenderers();
    }

    private void registerEntityRenderers() {
        EntityRendererRegistry.register(StalkerEntities.DRONE_STALKER, DroneStalkerRenderer::new);
        EntityRendererRegistry.register(StalkerEntities.ARROW_STALKER, ArrowStalkerRender::new);
        EntityRendererRegistry.register(StalkerEntities.VOID_STALKER, ThrownItemRenderer::new);
        EntityRendererRegistry.register(StalkerEntities.CAMERA_STALKER, NoopRenderer::new);
    }

    private void registerLayerDefinitions() {
        EntityModelLayerRegistry.registerModelLayer(
                DroneStalkerRenderer.LAYER,
                DroneStalkerModel::createBodyLayer
        );
    }

    private void registerKeyBindings() {
        KeyBindingHelper.registerKeyBinding(KeyBindings.DISCONNECT);
    }

    private void registerBlockEntityRenderers() {
        BlockEntityRenderers.register(StalkerBlockEntities.CAMERA_STALKER, CameraStalkerRenderer::new);
    }
}