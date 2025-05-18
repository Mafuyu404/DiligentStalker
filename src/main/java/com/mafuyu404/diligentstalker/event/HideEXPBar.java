package com.mafuyu404.diligentstalker.event;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.init.Stalker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

@Environment(EnvType.CLIENT)
public class HideEXPBar {
    public static void init() {
        HudRenderCallback.EVENT.register((guiGraphics, tickDelta) -> {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            
            // 如果玩家正在使用跟踪器，则设置一个标志来阻止经验条渲染
            DiligentStalker.HIDE_EXP_BAR = player != null && Stalker.hasInstanceOf(player);
        });
    }
}
