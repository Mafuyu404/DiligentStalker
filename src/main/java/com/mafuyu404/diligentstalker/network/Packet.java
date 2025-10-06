package com.mafuyu404.diligentstalker.network;

import net.minecraft.network.FriendlyByteBuf;

public interface Packet {
    void encode(FriendlyByteBuf buf);
}
