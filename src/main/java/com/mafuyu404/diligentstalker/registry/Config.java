package com.mafuyu404.diligentstalker.registry;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.ConfigValue<Integer> RENDER_RADIUS_NORMAL;
    public static final ForgeConfigSpec.ConfigValue<Integer> RENDER_RADIUS_SPECIAL;
    public static final ForgeConfigSpec.ConfigValue<Integer> SIGNAL_RADIUS;

    static {
        BUILDER.push("Render Radius");

        RENDER_RADIUS_NORMAL = BUILDER
                .comment("For DroneStalker and ArrowStalker.")
                .define("Normal", 3);
        RENDER_RADIUS_SPECIAL = BUILDER
                .comment("For VoidStalker.")
                .define("Special", 5);
        BUILDER.pop();

        BUILDER.push("Drone Setting");
        SIGNAL_RADIUS = BUILDER
                .comment("Range that DroneStalker can arrive.")
                .define("SignalRadius", 1024);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
