package com.mafuyu404.diligentstalker.data;

import com.mafuyu404.diligentstalker.DiligentStalker;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class StalkerDataAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, DiligentStalker.MODID);

    public static final Supplier<AttachmentType<ControllableStorage>> CONTROLLABLE_STORAGE =
            ATTACHMENT_TYPES.register("controllable_storage", () ->
                    AttachmentType.builder(ControllableStorage::new)
                            .serialize(new IAttachmentSerializer<CompoundTag, ControllableStorage>() {
                                @Override
                                public ControllableStorage read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
                                    ControllableStorage storage = new ControllableStorage();
                                    storage.deserializeNBT(provider, tag);
                                    return storage;
                                }

                                @Override
                                public CompoundTag write(ControllableStorage attachment, HolderLookup.Provider provider) {
                                    return attachment.serializeNBT(provider);
                                }
                            })
                            .copyOnDeath()
                            .sync(
                                    (holder, player) -> holder instanceof ServerPlayer,
                                    ControllableStorage.STREAM_CODEC
                            )
                            .build()
            );
}
