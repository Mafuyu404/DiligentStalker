package com.mafuyu404.diligentstalker.api;

public interface IControllableStorage {
    int getFuel();

    void setFuel(int amount);

    boolean consumeFuel(int amount);

    void setMaxFuel(int amount);

    int getMaxFuel();

    void setCameraState(String value);

    String getCameraState();

    void switchCameraState();

    int getSignalRadius();

    void setSignalRadius(int value);

    boolean isActionControlling();

    void turnActionControlling();
}
