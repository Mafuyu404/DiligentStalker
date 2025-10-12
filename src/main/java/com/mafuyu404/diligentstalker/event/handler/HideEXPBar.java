package com.mafuyu404.diligentstalker.event.handler;

import com.mafuyu404.diligentstalker.init.Stalker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(value = Dist.CLIENT)
public class HideEXPBar {
    @SubscribeEvent
    public static void onRenderExperienceBar(RenderGuiLayerEvent.Pre event) {
        if (event.getName().equals(VanillaGuiLayers.EXPERIENCE_BAR)) {
            Player player = Minecraft.getInstance().player;
            if (Stalker.hasInstanceOf(player)) {
                event.setCanceled(true);
            }
        }
    }
}
