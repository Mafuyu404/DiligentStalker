package com.mafuyu404.diligentstalker.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public abstract class StalkerEvents extends PlayerEvent {
    private final Entity stalker;

    public StalkerEvents(Player player, Entity stalker) {
        super(player);
        this.stalker = stalker;
    }

    public Entity getStalker() {
        return stalker;
    }

    public static class ConnectEvent extends StalkerEvents {
        public ConnectEvent(Player player, Entity stalker) {
            super(player, stalker);
        }
    }

    public static class DisconnectEvent extends StalkerEvents {
        public DisconnectEvent(Player player, Entity stalker) {
            super(player, stalker);
        }
    }
}
