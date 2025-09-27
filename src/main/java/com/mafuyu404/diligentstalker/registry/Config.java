package com.mafuyu404.diligentstalker.registry;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.ConfigValue<Integer> RENDER_RADIUS_NORMAL;
    public static final ForgeConfigSpec.ConfigValue<Integer> RENDER_RADIUS_SPECIAL;

    static {
        BUILDER.push("Render Radius");

        RENDER_RADIUS_NORMAL = BUILDER
                .comment("For DroneStalker and ArrowStalker.")
                .define("Normal", 5);
        RENDER_RADIUS_SPECIAL = BUILDER
                .comment("For VoidStalker.")
                .define("Special", 7);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
