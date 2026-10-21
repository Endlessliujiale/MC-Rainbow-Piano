/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_243
 */
package com.pianofx.client.fx;

import net.minecraft.class_243;

public final class Spiral {
    public static final int ARMS = 3;
    private static final double TAU = Math.PI * 2;
    private static final double CORE_LIFT = -0.5;
    private static final double R0 = 1.2;
    private static final double[] R1 = new double[]{22.0, 22.0, 22.0};
    private static final double[] TURNS = new double[]{2.6, 2.6, 2.6};
    private static final double[] DIR = new double[]{1.0, 1.0, 1.0};
    private static final double[] AZ = new double[]{0.0, 120.0, 240.0};
    private static final double[] RISE = new double[]{12.0, 12.0, 12.0};
    private static final double Y_WAVE = 0.9;
    private static final double TH_EXP = 1.0;
    private static final double Y_EXP = 1.3;
    static final double OFFSET_R = 0.0;
    public static final double LEAD_U = 0.16;
    private static final int N = 720;
    private static final double[][] ARC = new double[3][721];
    public static final double[] LENGTH = new double[3];
    public static final int RINGS = 2;
    public static final double[] RING_R;
    public static final double[] RING_Y;
    public static final double[] RING_TILT;
    private static double cachedAz;
    private static final double[][] B;

    private Spiral() {
    }

    private static void local(int arm, double u, double[] out) {
        int k = Math.floorMod(arm, 3);
        double offAz = Math.PI * 2 * (AZ[k] / 360.0);
        double cx = 0.0 * Math.cos(offAz);
        double cz = 0.0 * Math.sin(offAz);
        double p = u;
        double th = Math.PI * 2 * (DIR[k] * TURNS[k] * Math.pow(p, 1.0));
        double r = 1.2 + (R1[k] - 1.2) * p;
        out[0] = cx + r * Math.cos(th);
        out[1] = RISE[k] * Math.pow(p, 1.3) + 0.9 * Math.sin(th + (double)k) * p;
        out[2] = cz + r * Math.sin(th);
    }

    private static double paramOf(int arm, double u) {
        int k = Math.floorMod(arm, 3);
        double s = Math.max(0.0, Math.min(1.0, u)) * LENGTH[k];
        int lo = 0;
        int hi = 720;
        while (lo < hi) {
            int mid = lo + hi >>> 1;
            if (ARC[k][mid] < s) {
                lo = mid + 1;
                continue;
            }
            hi = mid;
        }
        if (lo <= 0) {
            return 0.0;
        }
        double seg = ARC[k][lo] - ARC[k][lo - 1];
        double f = seg <= 1.0E-9 ? 0.0 : (s - ARC[k][lo - 1]) / seg;
        return ((double)(lo - 1) + f) / 720.0;
    }

    public static class_243 at(int arm, double u, class_243 home, class_243 away) {
        int k = Math.floorMod(arm, 3);
        double[] b = Spiral.basis(k, away);
        double[] l = new double[3];
        Spiral.local(k, Spiral.paramOf(k, u), l);
        return new class_243(home.field_1352 + b[0] * l[0] + b[2] * l[2], home.field_1351 + -0.5 + l[1], home.field_1350 + b[1] * l[0] + b[3] * l[2]);
    }

    public static class_243 tangent(int arm, double u, class_243 home, class_243 away) {
        double d = 0.35 / Math.max(2.0, LENGTH[Math.floorMod(arm, 3)]);
        class_243 a = Spiral.at(arm, Math.max(0.0, u - d), home, away);
        class_243 b = Spiral.at(arm, Math.min(1.0, u + d), home, away);
        class_243 t = b.method_1020(a);
        return t.method_1027() < 1.0E-9 ? new class_243(0.0, 1.0, 0.0) : t.method_1029();
    }

    public static int segments(int arm, double spacing) {
        return (int)Math.max(24.0, Math.ceil(LENGTH[Math.floorMod(arm, 3)] / spacing));
    }

    public static class_243 ringAt(int ring, double ang, class_243 home, class_243 away) {
        int r = Math.floorMod(ring, 2);
        double[] b = Spiral.basis(r, away);
        double rad = RING_R[r];
        double c = rad * Math.cos(ang);
        double s = rad * Math.sin(ang);
        double y = RING_Y[r] + Math.tan(Math.toRadians(RING_TILT[r])) * c;
        return new class_243(home.field_1352 + b[0] * c + b[2] * s, home.field_1351 + -0.5 + y, home.field_1350 + b[1] * c + b[3] * s);
    }

    public static class_243 ringTangent(int ring, double ang, class_243 home, class_243 away) {
        double d = 0.02;
        class_243 a = Spiral.ringAt(ring, ang - d, home, away);
        class_243 b = Spiral.ringAt(ring, ang + d, home, away);
        class_243 t = b.method_1020(a);
        return t.method_1027() < 1.0E-9 ? new class_243(0.0, 1.0, 0.0) : t.method_1029();
    }

    public static int ringSegments(int ring, double spacing) {
        int r = Math.floorMod(ring, 2);
        return (int)Math.max(64.0, Math.ceil(Math.PI * 2 * RING_R[r] / spacing));
    }

    private static double[] basis(int arm, class_243 away) {
        int k = Math.floorMod(arm, 3);
        double baseAz = Math.atan2(away.field_1350, away.field_1352);
        if (baseAz != cachedAz) {
            cachedAz = baseAz;
            for (int i = 0; i < 3; ++i) {
                Spiral.fill(i, baseAz, B[i]);
            }
        }
        return B[k];
    }

    private static void fill(int k, double baseAz, double[] out) {
        double az = baseAz + Math.toRadians(AZ[k]);
        out[0] = Math.cos(az);
        out[1] = Math.sin(az);
        out[2] = -Math.sin(az);
        out[3] = Math.cos(az);
    }

    static {
        double[] a = new double[3];
        double[] b = new double[3];
        for (int k = 0; k < 3; ++k) {
            Spiral.local(k, 0.0, a);
            for (int i = 1; i <= 720; ++i) {
                Spiral.local(k, (double)i / 720.0, b);
                double dx = b[0] - a[0];
                double dy = b[1] - a[1];
                double dz = b[2] - a[2];
                Spiral.ARC[k][i] = ARC[k][i - 1] + Math.sqrt(dx * dx + dy * dy + dz * dz);
                a[0] = b[0];
                a[1] = b[1];
                a[2] = b[2];
            }
            Spiral.LENGTH[k] = ARC[k][720];
        }
        RING_R = new double[]{30.0, 38.0};
        RING_Y = new double[]{2.0, 2.0};
        RING_TILT = new double[]{9.0, -7.0};
        cachedAz = Double.NaN;
        B = new double[3][4];
    }
}

