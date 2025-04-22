package com.mafuyu404.diligentstalker.registry;

import com.mafuyu404.diligentstalker.DiligentStalker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

import static net.minecraftforge.registries.ForgeRegistries.SOUND_EVENTS;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModSounds {
//    public static final SoundEvent DRONE_INTERACT = SoundEvent.createVariableRangeEvent(
//            new ResourceLocation(DiligentStalker.MODID, "drone_stalker_interact"));
//
//    @SubscribeEvent
//    public static void registerSounds(RegisterEvent event) {
//        event.register(ForgeRegistries.Keys.SOUND_EVENTS, helper -> {
//            helper.register(new ResourceLocation(DiligentStalker.MODID, "drone_stalker_interact"),
//                    DRONE_INTERACT);
//        });
//    }
}
