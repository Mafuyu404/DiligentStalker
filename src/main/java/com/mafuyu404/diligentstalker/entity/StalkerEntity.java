package com.mafuyu404.diligentstalker.entity;

import com.mafuyu404.diligentstalker.init.NetworkHandler;
import com.mafuyu404.diligentstalker.network.EntityDataPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class StalkerEntity extends Entity {
    public StalkerEntity(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public Player getMasterPlayer() {
        return this.level().getPlayerByUUID(this.getPersistentData().getUUID("MasterPlayer"));
    }

    public void setMasterPlayer(Player player) {
        this.getPersistentData().putUUID("MasterPlayer", player.getUUID());
        if (player.isLocalPlayer()) {
            NetworkHandler.CHANNEL.sendToServer(new EntityDataPacket(this.getId(), this.getPersistentData()));
        }
    }

    public boolean underControlling() {
        return this.getPersistentData().getUUID("MasterPlayer") != this.uuid;
    }

    public void disconnect() {
        this.getPersistentData().putUUID("MasterPlayer", this.uuid);
        if (this.level().isClientSide) {
            NetworkHandler.CHANNEL.sendToServer(new EntityDataPacket(this.getId(), this.getPersistentData()));
        }
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag p_20052_) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag p_20139_) {

    }
}
