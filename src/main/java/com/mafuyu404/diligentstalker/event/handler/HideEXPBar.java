package com.mafuyu404.diligentstalker.event.handler;


import com.mafuyu404.diligentstalker.event.client.HideExperienceBarEvents;
import com.mafuyu404.diligentstalker.init.Stalker;

public class HideEXPBar {
    public static void onRenderExperienceBar() {
        HideExperienceBarEvents.RENDER_EXP_BAR.register((client, gui) -> Stalker.hasInstanceOf(client.player));
    }
}
