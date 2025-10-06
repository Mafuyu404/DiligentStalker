package com.mafuyu404.diligentstalker.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public interface HideExperienceBarEvents {
    Event<RenderExpBarCallback> RENDER_EXP_BAR =
            EventFactory.createArrayBacked(RenderExpBarCallback.class, listeners -> (client, gui) -> {
                for (RenderExpBarCallback listener : listeners) {
                    if (listener.shouldCancel(client, gui)) return true;
                }
                return false;
            });

    @FunctionalInterface
    interface RenderExpBarCallback {

        /**
         * 返回 true 表示取消渲染经验条
         */
        boolean shouldCancel(Minecraft client, Gui gui);
    }
}
