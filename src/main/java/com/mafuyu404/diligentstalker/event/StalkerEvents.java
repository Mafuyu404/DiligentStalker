package com.mafuyu404.diligentstalker.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface StalkerEvents {

    Event<Connect> CONNECT = EventFactory.createArrayBacked(Connect.class,
            listeners -> (player, stalker) -> {
                for (Connect listener : listeners) {
                    listener.onConnect(player, stalker);
                }
            });
    Event<Disconnect> DISCONNECT = EventFactory.createArrayBacked(Disconnect.class,
            listeners -> (player, stalker) -> {
                for (Disconnect listener : listeners) {
                    listener.onDisconnect(player, stalker);
                }
            });

    @FunctionalInterface
    interface Connect {
        void onConnect(Player player, Entity stalker);
    }

    @FunctionalInterface
    interface Disconnect {
        void onDisconnect(Player player, Entity stalker);
    }
}