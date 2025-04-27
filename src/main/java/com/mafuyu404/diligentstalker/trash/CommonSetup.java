package com.mafuyu404.diligentstalker.trash;

import com.mafuyu404.diligentstalker.DiligentStalker;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetup {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        gen.addProvider(
                event.includeServer(),
                new ModItemTagsProvider(
                        output,
                        lookupProvider,
                        new ModBlockTagsProvider(output, lookupProvider, existingFileHelper).contentsGetter(),
                        DiligentStalker.MODID,
                        existingFileHelper
                )
        );
    }
}
