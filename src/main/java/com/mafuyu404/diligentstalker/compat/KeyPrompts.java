package com.mafuyu404.diligentstalker.compat;

import net.fabricmc.loader.api.FabricLoader;

public class KeyPrompts {
    private static final String MOD_ID = "smartkeyprompts";
    private static boolean INSTALLED = false;

    public static void init() {
        INSTALLED = FabricLoader.getInstance().isModLoaded(MOD_ID);
    }

    public static boolean show(String desc) {
        if (INSTALLED) {
            KeyPromptsInner.show(desc);
        }
        return false;
    }
}
