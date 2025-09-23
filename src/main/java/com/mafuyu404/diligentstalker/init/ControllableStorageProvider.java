package com.mafuyu404.diligentstalker.init;

import com.mafuyu404.diligentstalker.api.Controllable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ControllableStorageProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<Controllable> CONTROLLABLE_STORAGE =
            CapabilityManager.get(new CapabilityToken<>() {});

    private Controllable state = null;
    private final LazyOptional<Controllable> opt = LazyOptional.of(this::createControllableStorage);

    @Nonnull
    private Controllable createControllableStorage() {
        if (state == null) {
            state = new ControllableStorage();
        }
        return state;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CONTROLLABLE_STORAGE) {
            return opt.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        createControllableStorage();
        if (state instanceof ControllableStorage) {
            tag = ((ControllableStorage) state).serializeNBT();
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createControllableStorage();
        if (state instanceof ControllableStorage) {
            ((ControllableStorage) state).deserializeNBT(nbt);
        }
    }
}
