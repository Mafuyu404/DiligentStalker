package com.mafuyu404.diligentstalker.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public interface MouseScrollCallback {
    Event<MouseScrollCallback> EVENT = EventFactory.createArrayBacked(MouseScrollCallback.class,
            (listeners) -> (client, scrollDelta) -> {
                for (MouseScrollCallback listener : listeners) {
                    if (!listener.mouseScrolled(client, scrollDelta)) {
                        return false;
                    }
                }
                return true;
            });

    /**
     * 当鼠标滚轮滚动时调用
     * @param client Minecraft客户端实例
     * @param scrollDelta 滚动量
     * @return 如果事件应该继续传播返回true，否则返回false
     */
    boolean mouseScrolled(Minecraft client, double scrollDelta);
}