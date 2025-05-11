package com.mafuyu404.diligentstalker.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class ClientEvents {
    private static boolean initialized = false;

    public static void init() {
        // 注册客户端tick事件，确保在游戏窗口初始化后再设置回调
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!initialized && client.getWindow() != null) {
                // 注册鼠标滚轮事件处理
                GLFW.glfwSetScrollCallback(client.getWindow().getWindow(), (window, xOffset, yOffset) -> {
                    if (client.screen == null) {
                        MouseScrollCallback.EVENT.invoker().mouseScrolled(client, yOffset);
                    }
                });
                initialized = true;
            }
        });
    }
}