package com.mafuyu404.diligentstalker.registry;

import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.ConfigValue<Integer> RENDER_RADIUS_NORMAL;
    public static final ForgeConfigSpec.ConfigValue<Integer> RENDER_RADIUS_SPECIAL;

    static {
        BUILDER.push(Component.translatable("config.diligentstalker.render_radius").toString());
        RENDER_RADIUS_NORMAL = BUILDER
                .comment(Component.translatable("config.diligentstalker.render_radius.normal").toString())
                .define("Normal", 3);
        RENDER_RADIUS_SPECIAL = BUILDER
                .comment(Component.translatable("config.diligentstalker.render_radius.special").toString())
                .define("Special", 5);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
