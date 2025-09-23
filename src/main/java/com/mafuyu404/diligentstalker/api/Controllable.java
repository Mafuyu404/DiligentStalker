package com.mafuyu404.diligentstalker.api;

import com.mafuyu404.diligentstalker.init.Stalker;
import net.minecraft.util.Mth;

public interface Controllable {
    int getFuel();

    void setFuel(int amount);

    boolean consumeFuel(int amount);

    void setMaxFuel(int amount);

    int getMaxFuel();
}
