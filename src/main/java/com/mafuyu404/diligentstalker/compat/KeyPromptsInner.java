package com.mafuyu404.diligentstalker.compat;

import com.mafuyu404.diligentstalker.DiligentStalker;
import com.mafuyu404.smartkeyprompts.SmartKeyPrompts;

public class KeyPromptsInner {
    public static void show(String desc) {
        SmartKeyPrompts.show(DiligentStalker.MODID, desc);
    }
}
