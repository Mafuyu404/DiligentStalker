package com.mafuyu404.diligentstalker.trash;

import com.mafuyu404.diligentstalker.DiligentStalker;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DiligentStalker.MODID, value = Dist.CLIENT)
public class CameraEntityManage {
    public static float fixedXRot, fixedYRot;
    public static float xRot, yRot;
}
