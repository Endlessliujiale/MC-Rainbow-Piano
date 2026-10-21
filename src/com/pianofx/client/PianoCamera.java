/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_5498
 */
package com.pianofx.client;

import com.pianofx.PianoFX;
import com.pianofx.client.ScorePlayer;
import com.pianofx.client.gui.PianoScreen;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_5498;

public final class PianoCamera {
    public static final float DEFAULT_DISTANCE = 7.0f;
    public static final float MIN_DISTANCE = 3.0f;
    public static final float MAX_DISTANCE = 60.0f;
    private static final float LERP = 0.18f;
    public static final int DEFAULT_FOV = 82;
    public static final int MIN_FOV = 40;
    public static final int MAX_FOV = 120;
    private static boolean active;
    private static boolean forced;
    private static boolean overridden;
    private static class_5498 previous;
    private static float extra;
    private static float lastExtra;
    private static float distance;
    private static int fov;
    private static int savedFov;
    private static final float DRAG_YAW = 0.22f;
    private static final float DRAG_PITCH = 0.18f;

    private PianoCamera() {
    }

    public static void tick(class_310 mc) {
        boolean want = forced || mc.field_1755 instanceof PianoScreen || ScorePlayer.playing();
        lastExtra = extra;
        if (!want) {
            overridden = false;
            if (active) {
                active = false;
                mc.field_1690.method_31043(previous);
                PianoFX.LOGGER.info("[PianoFX] \u6f14\u51fa\u76f8\u673a\uff1a\u6062\u590d\u539f\u89c6\u89d2 {}", (Object)previous);
            }
            PianoCamera.restoreFov(mc);
        } else {
            if (!active && !overridden) {
                active = true;
                previous = mc.field_1690.method_31044();
                if (previous != class_5498.field_26665) {
                    mc.field_1690.method_31043(class_5498.field_26665);
                }
                PianoFX.LOGGER.info("[PianoFX] \u6f14\u51fa\u76f8\u673a\uff1a\u5207\u7b2c\u4e09\u4eba\u79f0\u5e76\u62c9\u8fdc\u5230 {} \u683c\uff0cFOV {}", (Object)String.format("%.1f", Float.valueOf(4.0f + distance)), (Object)fov);
            }
            if (active && mc.field_1690.method_31044() != class_5498.field_26665) {
                active = false;
                overridden = true;
                PianoFX.LOGGER.info("[PianoFX] \u6f14\u51fa\u76f8\u673a\uff1a\u73a9\u5bb6\u81ea\u5df1\u5207\u4e86\u89c6\u89d2\uff0c\u9000\u51fa\u81ea\u52a8\u673a\u4f4d");
            }
            PianoCamera.applyFov(mc);
        }
        float target = active ? distance : 0.0f;
        extra += (target - extra) * 0.18f;
        if (Math.abs(target - extra) < 0.02f) {
            extra = target;
        }
    }

    public static void dragLook(class_310 mc, double dx, double dy) {
        if (mc == null || mc.field_1724 == null) {
            return;
        }
        float yaw = mc.field_1724.method_36454() + (float)dx * 0.22f;
        float pitch = class_3532.method_15363((float)(mc.field_1724.method_36455() + (float)dy * 0.18f), (float)-70.0f, (float)55.0f);
        mc.field_1724.method_36456(yaw);
        mc.field_1724.field_5982 = yaw;
        mc.field_1724.method_36457(pitch);
        mc.field_1724.field_6004 = pitch;
    }

    public static void adjustDistance(float delta) {
        distance = class_3532.method_15363((float)(distance + delta), (float)3.0f, (float)60.0f);
    }

    public static float distance() {
        return distance;
    }

    public static void resetDistance() {
        distance = 7.0f;
    }

    public static float totalDistance() {
        return 4.0f + distance;
    }

    public static void adjustFov(int delta) {
        fov = class_3532.method_15340((int)(fov + delta), (int)40, (int)120);
    }

    public static int fov() {
        return fov;
    }

    public static void resetFov() {
        fov = 82;
    }

    private static void applyFov(class_310 mc) {
        if (mc.field_1690 == null) {
            return;
        }
        if (savedFov < 0) {
            savedFov = (Integer)mc.field_1690.method_41808().method_41753();
        }
        if ((Integer)mc.field_1690.method_41808().method_41753() != fov) {
            mc.field_1690.method_41808().method_41748((Object)fov);
        }
    }

    private static void restoreFov(class_310 mc) {
        if (savedFov < 0) {
            return;
        }
        if (mc.field_1690 != null && (Integer)mc.field_1690.method_41808().method_41753() != savedFov) {
            mc.field_1690.method_41808().method_41748((Object)savedFov);
        }
        savedFov = -1;
        fov = 82;
    }

    public static void forceForTest(boolean on) {
        forced = on;
    }

    public static boolean forced() {
        return forced;
    }

    public static boolean active() {
        return active && extra > 0.05f;
    }

    public static float distance(float tickDelta) {
        return lastExtra + (extra - lastExtra) * tickDelta;
    }

    static {
        previous = class_5498.field_26664;
        distance = 7.0f;
        fov = 82;
        savedFov = -1;
    }
}

