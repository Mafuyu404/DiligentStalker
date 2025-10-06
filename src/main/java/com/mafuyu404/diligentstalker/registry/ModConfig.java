package com.mafuyu404.diligentstalker.registry;

import com.mafuyu404.diligentstalker.DiligentStalker;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@me.shedaniel.autoconfig.annotation.Config(name = DiligentStalker.MODID)
public class ModConfig implements ConfigData {
    @ConfigEntry.Gui.CollapsibleObject
    public RenderRadius renderRadius = new RenderRadius();

    @ConfigEntry.Gui.CollapsibleObject
    public DroneSetting droneSetting = new DroneSetting();

    public static class RenderRadius {
        @Comment("For DroneStalker and ArrowStalker.")
        public int normal = 5;

        @Comment("For VoidStalker.")
        public int special = 7;
    }

    public static class DroneSetting {
        @Comment("Range that DroneStalker can arrive.")
        public int signalRadius = 1024;
    }

    private static ModConfig INSTANCE;

    public static ModConfig get() {
        return INSTANCE;
    }

    public static void register() {
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    public static int getRenderRadiusNormal() {
        return INSTANCE.renderRadius.normal;
    }

    public static int getRenderRadiusSpecial() {
        return INSTANCE.renderRadius.special;
    }

    public static int getSignalRadius() {
        return INSTANCE.droneSetting.signalRadius;
    }
}
