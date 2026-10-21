/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$class_4534
 *  com.mojang.blaze3d.platform.GlStateManager$class_4535
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.class_1059
 *  net.minecraft.class_1060
 *  net.minecraft.class_287
 *  net.minecraft.class_289
 *  net.minecraft.class_290
 *  net.minecraft.class_293$class_5596
 *  net.minecraft.class_2960
 *  net.minecraft.class_3999
 */
package com.pianofx.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.class_1059;
import net.minecraft.class_1060;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_293;
import net.minecraft.class_2960;
import net.minecraft.class_3999;

public final class AdditiveSheet
implements class_3999 {
    public static final class_3999 INSTANCE = new AdditiveSheet();

    private AdditiveSheet() {
    }

    public class_287 method_18130(class_289 tessellator, class_1060 textureManager) {
        RenderSystem.depthMask((boolean)false);
        RenderSystem.setShaderTexture((int)0, (class_2960)class_1059.field_17898);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate((GlStateManager.class_4535)GlStateManager.class_4535.SRC_ALPHA, (GlStateManager.class_4534)GlStateManager.class_4534.ONE, (GlStateManager.class_4535)GlStateManager.class_4535.ONE, (GlStateManager.class_4534)GlStateManager.class_4534.ONE);
        return tessellator.method_60827(class_293.class_5596.field_27382, class_290.field_1584);
    }

    public String toString() {
        return "pianofx:additive";
    }
}

