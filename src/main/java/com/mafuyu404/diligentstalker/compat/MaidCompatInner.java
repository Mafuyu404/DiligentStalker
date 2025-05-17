package com.mafuyu404.diligentstalker.compat;

import net.minecraft.world.entity.Entity;

public class MaidCompatInner {
    public static boolean isMaid(Entity entity) {
        String type = entity.getType().getDescriptionId();
        return type.equals("entity.touhou_little_maid.maid");
    }
}
