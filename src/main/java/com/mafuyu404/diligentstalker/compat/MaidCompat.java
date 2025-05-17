package com.mafuyu404.diligentstalker.compat;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.ModList;

public class MaidCompat {
    private static final String MOD_ID = "touhou_little_maid";
    private static boolean INSTALLED = false;

    public static void init() {
        INSTALLED = ModList.get().isLoaded(MOD_ID);
    }

    public static boolean isMaid(Entity entity) {
        if (INSTALLED) {
            return MaidCompatInner.isMaid(entity);
        }
        return false;
    }
}
