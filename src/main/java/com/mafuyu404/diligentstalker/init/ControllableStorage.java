package com.mafuyu404.diligentstalker.init;

import com.mafuyu404.diligentstalker.api.IControllableStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

import java.util.List;

public class ControllableStorage implements IControllableStorage {
    private static final String FUEL_KEY = "ControllableFuel";
    private static final String MAX_FUEL_KEY = "ControllableMaxFuel";
    private static final String CAMERA_STATE_KEY = "CameraState";
    private static final String SIGNAL_RADIUS_KEY = "SignalRadius";
    private static final String ACTION_CONTROL_KEY = "ActionControl";

    public static final List<String> CAMERA_STATE_TYPE = List.of("free", "follow", "control");

    private int fuel = 0;
    private int maxFuel = 100;
    private String cameraState = CAMERA_STATE_TYPE.get(0);
    private int signalRadius = 256;
    private boolean isActionControlling = true;


    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(FUEL_KEY, fuel);
        tag.putInt(MAX_FUEL_KEY, maxFuel);
        tag.putString(CAMERA_STATE_KEY, cameraState);
        tag.putInt(SIGNAL_RADIUS_KEY, signalRadius);
        tag.putBoolean(ACTION_CONTROL_KEY, isActionControlling);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        fuel = tag.getInt(FUEL_KEY);
        maxFuel = tag.getInt(MAX_FUEL_KEY);
        cameraState = tag.getString(CAMERA_STATE_KEY);
        signalRadius = tag.getInt(SIGNAL_RADIUS_KEY);
        isActionControlling = tag.getBoolean(ACTION_CONTROL_KEY);
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
    public void setCameraState(String value) {
        if (!CAMERA_STATE_TYPE.contains(value)) return;
        cameraState = value;
    }

    @Override
    public String getCameraState() {
        return cameraState;
    }

    @Override
    public void switchCameraState() {
        int index = CAMERA_STATE_TYPE.indexOf(cameraState);
        int next = index + 1;
        if (next == CAMERA_STATE_TYPE.size()) next = 0;
        cameraState = CAMERA_STATE_TYPE.get(next);
    }

    @Override
    public int getSignalRadius() {
        return signalRadius;
    }

    @Override
    public void setSignalRadius(int value) {
        signalRadius = value;
    }

    @Override
    public boolean isActionControlling() {
        return isActionControlling;
    }

    @Override
    public void turnActionControlling() {
        isActionControlling = !isActionControlling;
    }
}
