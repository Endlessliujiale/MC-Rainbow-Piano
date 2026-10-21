/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_2382
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package com.pianofx.client;

import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2382;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;

public final class PianoSeat {
    private static boolean sitting;
    private static class_243 seatPos;
    private static float seatYaw;

    private PianoSeat() {
    }

    public static boolean sitting() {
        return sitting;
    }

    public static void sit(class_310 mc, class_2338 pos, class_2350 facing) {
        class_746 p = mc.field_1724;
        if (p == null) {
            return;
        }
        class_243 center = class_243.method_24953((class_2382)pos);
        class_243 front = new class_243((double)facing.method_10148(), 0.0, (double)facing.method_10165()).method_1029();
        seatPos = new class_243(center.field_1352, (double)pos.method_10264(), center.field_1350).method_1019(front.method_1021(1.45));
        seatYaw = facing.method_10144() + 180.0f;
        p.method_5814(PianoSeat.seatPos.field_1352, PianoSeat.seatPos.field_1351, PianoSeat.seatPos.field_1350);
        p.method_18800(0.0, 0.0, 0.0);
        p.method_36456(seatYaw);
        p.field_5982 = seatYaw;
        p.method_36457(8.0f);
        p.field_6004 = 8.0f;
        sitting = true;
    }

    public static void tick(class_310 mc) {
        if (!sitting) {
            return;
        }
        class_746 p = mc.field_1724;
        if (p == null) {
            sitting = false;
            return;
        }
        if (mc.field_1690.field_1832.method_1434() || p.method_5715()) {
            PianoSeat.stand(p);
            return;
        }
        if (p.method_19538().method_1025(seatPos) > 4.0) {
            sitting = false;
            return;
        }
        p.method_5814(PianoSeat.seatPos.field_1352, PianoSeat.seatPos.field_1351, PianoSeat.seatPos.field_1350);
        p.method_18800(0.0, 0.0, 0.0);
        p.field_6017 = 0.0f;
        p.method_24830(true);
    }

    public static void stand(class_310 mc) {
        if (mc.field_1724 != null) {
            PianoSeat.stand(mc.field_1724);
        }
    }

    private static void stand(class_746 p) {
        sitting = false;
        p.method_18800(0.0, 0.0, 0.0);
    }

    public static void reset() {
        sitting = false;
    }

    static {
        seatPos = class_243.field_1353;
    }
}

