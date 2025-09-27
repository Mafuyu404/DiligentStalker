package com.mafuyu404.diligentstalker.init;

import com.mafuyu404.diligentstalker.api.IControllableStorage;
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
    public static final Capability<IControllableStorage> CONTROLLABLE_STORAGE =
            CapabilityManager.get(new CapabilityToken<>() {});

    private IControllableStorage state = null;
    private final LazyOptional<IControllableStorage> opt = LazyOptional.of(this::createControllableStorage);

    @Nonnull
    private IControllableStorage createControllableStorage() {
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
