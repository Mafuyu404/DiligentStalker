package com.mafuyu404.diligentstalker.data;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.diligentstalker.api.HasControllableStorage;
import com.mafuyu404.diligentstalker.api.HasStalkerData;
import com.mafuyu404.diligentstalker.api.IControllableStorage;
import com.mafuyu404.diligentstalker.api.IStalkerData;
import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class ModLookupApi {
    public static final EntityApiLookup<IControllableStorage, Void> CONTROLLABLE_STORAGE =
            EntityApiLookup.get(new ResourceLocation(DiligentStalker.MODID, "controllable_storage"),
                    IControllableStorage.class, Void.class);

    public static final EntityApiLookup<IStalkerData, Void> STALKER_DATA =
            EntityApiLookup.get(new ResourceLocation(DiligentStalker.MODID, "stalker_data"),
                    IStalkerData.class, Void.class);

    static {
        CONTROLLABLE_STORAGE.registerFallback((Entity entity, Void ignored) -> {
            if (entity instanceof IControllableStorage) {
                return (IControllableStorage) entity;
            }
            if (entity instanceof HasControllableStorage accessor) {
                return accessor.diligentstalker$getControllableStorage();
            }
            return null;
        });

        STALKER_DATA.registerFallback((Entity entity, Void ignored) -> {
            if (entity instanceof HasStalkerData accessor) {
                return accessor.diligentstalker$getStalkerData();
            }
            return null;
        });
    }
}