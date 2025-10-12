package com.mafuyu404.diligentstalker.registry;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    public static ModConfigSpec.ConfigValue<Integer> RENDER_RADIUS_NORMAL;
    public static ModConfigSpec.ConfigValue<Integer> RENDER_RADIUS_SPECIAL;

    public static ModConfigSpec init() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("Render Radius");

        RENDER_RADIUS_NORMAL = builder
                .comment("For DroneStalker and ArrowStalker.")
                .define("Normal", 5);
        RENDER_RADIUS_SPECIAL = builder
                .comment("For VoidStalker.")
                .define("Special", 7);
        builder.pop();

        return builder.build();
    }
}

