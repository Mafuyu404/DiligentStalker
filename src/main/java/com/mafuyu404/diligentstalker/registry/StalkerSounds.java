package com.mafuyu404.diligentstalker.registry;

import com.mafuyu404.diligentstalker.DiligentStalker;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class StalkerSounds {
    public static final ResourceLocation DRONE_INTERACT_ID = new ResourceLocation(DiligentStalker.MODID, "drone_stalker_interact");
    public static final SoundEvent DRONE_INTERACT = SoundEvent.createVariableRangeEvent(DRONE_INTERACT_ID);

    public static void register() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, DRONE_INTERACT_ID, DRONE_INTERACT);
    }
}
