/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2246
 *  net.minecraft.class_2350
 *  net.minecraft.class_2680
 *  net.minecraft.class_2769
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  net.minecraft.class_4597
 *  net.minecraft.class_776
 *  net.minecraft.class_7833
 *  net.minecraft.class_827
 */
package com.pianofx.client.render;

import com.pianofx.block.PianoBlock;
import com.pianofx.block.PianoBlockEntity;
import com.pianofx.client.render.PianoKeys;
import net.minecraft.class_2246;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_2769;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_776;
import net.minecraft.class_7833;
import net.minecraft.class_827;

public class PianoRenderer
implements class_827<PianoBlockEntity> {
    private static final class_2680 BODY = class_2246.field_10458.method_9564();
    private static final class_2680 TRIM = class_2246.field_10174.method_9564();
    private static final class_2680 WOOD = class_2246.field_10244.method_9564();
    private static final class_2680 WHITE_KEY = class_2246.field_10107.method_9564();
    private static final class_2680 BLACK_KEY = class_2246.field_10458.method_9564();

    public void render(PianoBlockEntity be, float tickDelta, class_4587 ms, class_4597 vcp, int light, int overlay) {
        light=0xF000F0; // Emissive model, independent of ambient block and sky lighting.
        class_776 brm = class_310.method_1551().method_1541();
        class_2350 facing = (class_2350)be.method_11010().method_11654((class_2769)PianoBlock.field_11177);
        ms.method_22903();
        ms.method_22904(0.5, 0.0, 0.5);
        ms.method_22907(class_7833.field_40716.rotationDegrees(-facing.method_10144()));
        PianoRenderer.box(ms, vcp, brm, WOOD, light, overlay, -1.02, 0.0, 0.85, 0.16, 0.6, 0.16);
        PianoRenderer.box(ms, vcp, brm, WOOD, light, overlay, 0.86, 0.0, 0.85, 0.16, 0.6, 0.16);
        PianoRenderer.box(ms, vcp, brm, WOOD, light, overlay, -0.08, 0.0, -1.35, 0.16, 0.6, 0.16);
        PianoRenderer.box(ms, vcp, brm, BODY, light, overlay, -1.1, 0.6, -1.5, 2.2, 0.46, 2.35);
        PianoRenderer.box(ms, vcp, brm, BODY, light, overlay, -0.72, 0.6, -1.85, 1.44, 0.46, 0.42);
        PianoRenderer.box(ms, vcp, brm, TRIM, light, overlay, -1.13, 0.56, -1.53, 2.26, 0.07, 2.41);
        ms.method_22903();
        ms.method_22904(0.0, 1.06, -1.5);
        ms.method_22907(class_7833.field_40714.rotationDegrees(-14.0f));
        PianoRenderer.box(ms, vcp, brm, BODY, light, overlay, -1.1, 0.0, 0.0, 2.2, 0.08, 2.4);
        // Narrow emissive lid edges make the black piano readable at night.
        PianoRenderer.box(ms,vcp,brm,TRIM,light,overlay,-1.115,0.065,0,0.025,0.025,2.4);
        PianoRenderer.box(ms,vcp,brm,TRIM,light,overlay,1.09,0.065,0,0.025,0.025,2.4);
        PianoRenderer.box(ms,vcp,brm,TRIM,light,overlay,-1.1,0.065,2.375,2.2,0.025,0.025);
        ms.method_22909();
        PianoRenderer.box(ms, vcp, brm, TRIM, light, overlay, 0.98, 1.06, -1.42, 0.06, 0.34, 0.06);
        PianoRenderer.box(ms, vcp, brm, WOOD, light, overlay, -1.1, 0.5, 0.8, 2.2, 0.12, 0.72);
        float keyW = 0.175f;
        for (int pc = 0; pc < 12; ++pc) {
            boolean black = PianoRenderer.isBlack(pc);
            float dip = PianoKeys.level(pc) * 0.06f;
            double x = -1.05 + (double)((float)pc * keyW);
            if (black) {
                PianoRenderer.box(ms, vcp, brm, BLACK_KEY, light, overlay, x + (double)keyW * 0.16, 0.7 - (double)dip, 0.86, (double)keyW * 0.64, 0.09, 0.42);
                continue;
            }
            PianoRenderer.box(ms, vcp, brm, WHITE_KEY, light, overlay, x + 0.012, 0.62 - (double)dip, 0.78, (double)keyW - 0.024, 0.09, 0.58);
        }
        ms.method_22909();
    }

    private static boolean isBlack(int pc) {
        return pc == 1 || pc == 3 || pc == 6 || pc == 8 || pc == 10;
    }

    private static void box(class_4587 ms, class_4597 vcp, class_776 brm, class_2680 state, int light, int overlay, double x, double y, double z, double sx, double sy, double sz) {
        ms.method_22903();
        ms.method_22904(x, y, z);
        ms.method_22905((float)sx, (float)sy, (float)sz);
        brm.method_3353(state, ms, vcp, light, overlay);
        ms.method_22909();
    }

    public boolean rendersOutsideBoundingBox(PianoBlockEntity be) {
        return true;
    }

    public int method_33893() {
        return 160;
    }
    @Override public void method_3569(PianoBlockEntity be,float delta,class_4587 ms,class_4597 vcp,int light,int overlay){render(be,delta,ms,vcp,light,overlay);}
    @Override public boolean method_3563(PianoBlockEntity be){return true;}
}

