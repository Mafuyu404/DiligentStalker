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
                .comment("For DroneStalker and ArrowStalker. 5 loads up to 121 square-range client slots, higher values can be expensive.")
                .defineInRange("Normal", 5, 1, 12);
        RENDER_RADIUS_SPECIAL = BUILDER
                .comment("For VoidStalker. Keep this bounded because remote chunk packets and forced tickets scale by radius squared.")
                .defineInRange("Special", 7, 1, 16);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
