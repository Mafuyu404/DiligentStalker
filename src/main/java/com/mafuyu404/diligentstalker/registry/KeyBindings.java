package com.mafuyu404.diligentstalker.registry;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final KeyMapping DISCONNECT = new KeyMapping("key.diligentstalker.disconnect.desc",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.diligentstalker"
    );
    public static final KeyMapping VIEW = new KeyMapping("key.diligentstalker.view.desc",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.categories.diligentstalker"
    );
    public static final KeyMapping CONTROL = new KeyMapping("key.diligentstalker.control.desc",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            "key.categories.diligentstalker"
    );
}