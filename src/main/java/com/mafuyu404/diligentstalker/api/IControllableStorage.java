package com.mafuyu404.diligentstalker.api;

public interface IControllableStorage {
    int getFuel();

    void setFuel(int amount);

    boolean consumeFuel(int amount);

    int getMaxFuel();

    void setMaxFuel(int amount);

    String getCameraState();

    void setCameraState(String value);

    void switchCameraState();

    int getSignalRadius();

    void setSignalRadius(int value);

    boolean isActionControlling();

    void turnActionControlling();
}
