package com.mafuyu404.diligentstalker.registry;

import com.mafuyu404.diligentstalker.entity.ArrowStalkerEntity;
import com.mafuyu404.diligentstalker.entity.DroneStalkerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.mafuyu404.diligentstalker.DiligentStalker.MODID;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);

    public static final RegistryObject<EntityType<DroneStalkerEntity>> DRONE_STALKER = ENTITIES.register(
            "drone_stalker",
            () -> EntityType.Builder.<DroneStalkerEntity>of(DroneStalkerEntity::new, MobCategory.MISC)
                    .sized(0.8f, 0.4f)
                    .clientTrackingRange(8)
                    .setUpdateInterval(3)
                    .build(new ResourceLocation(MODID, "drone_stalker").toString())
    );
    public static final RegistryObject<EntityType<ArrowStalkerEntity>> ARROW_STALKER = ENTITIES.register(
            "arrow_stalker",
            () -> EntityType.Builder.<ArrowStalkerEntity>of(ArrowStalkerEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(MODID, "arrow_stalker").toString())
    );
}
