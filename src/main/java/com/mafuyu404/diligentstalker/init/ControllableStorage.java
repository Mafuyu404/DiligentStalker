package com.mafuyu404.diligentstalker.init;

import com.mafuyu404.diligentstalker.api.Controllable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

public class ControllableStorage implements Controllable {
    public static final String FUEL_KEY = "ControllableFuel";
    public static final String MAX_FUEL_KEY = "ControllableMaxFuel";

    private int fuel = 0;
    private int maxFuel = 0;
    private boolean isCameraControlling = false;

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(FUEL_KEY, fuel);
        tag.putInt(MAX_FUEL_KEY, maxFuel);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        fuel = tag.getInt(FUEL_KEY);
        maxFuel = tag.getInt(MAX_FUEL_KEY);
    }

    @Override
    public int getFuel() {
        return fuel;
    }

    @Override
    public void setFuel(int amount) {
        fuel = Mth.clamp(amount, 0, maxFuel);
    }

    @Override
    public boolean consumeFuel(int amount) {
        if (fuel >= amount) {
            setFuel(fuel - amount);
            return true;
        }
        return false;
    }

    @Override
    public void setMaxFuel(int amount) {
        maxFuel = amount;
    }

    @Override
    public int getMaxFuel() {
        return maxFuel;
    }

    @Override
    public void setCameraControlling(boolean value) {
        isCameraControlling = value;
    }

    @Override
    public boolean getCameraControlling() {
        return isCameraControlling;
    }
}
