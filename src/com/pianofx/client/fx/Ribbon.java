/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_243
 *  net.minecraft.class_2960
 *  net.minecraft.class_638
 *  net.minecraft.class_703
 */
package com.pianofx.client.fx;

import com.pianofx.client.ShaderState;
import com.pianofx.client.fx.Rainbow;
import com.pianofx.client.fx.Spiral;
import com.pianofx.client.particle.SparkParticle;
import java.util.Arrays;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_638;
import net.minecraft.class_703;

final class Ribbon {
    private static final double SPACING = 0.062;
    private static final int LIFE = 300;
    private static final double WIDTH = 0.04;
    private static final double SEG_LEN = 0.06820000000000001;
    private static final int GLOW_EVERY = 3;
    private static final float GLOW_BRIGHT = 0.11f;
    private static final float LINE_GAIN = 0.34f;
    private static final boolean RING_ON = false;
    private static final float RING_GAIN = 0.7f;
    private static final double TIP_SEGS = 30.0;
    private static final class_2960 PEARL;
    private static final int[] COUNT;
    private static final int[] RING_COUNT;
    private static final double[] RING_SCAN;
    private static final double[] HEAD;
    private static final double[] SCAN;
    private static double drawnBlocks;
    private static final double START_DRAWN = 18.0;
    private static final double MAX_LEN;

    private Ribbon() {
    }

    static double drawn() {
        return drawnBlocks;
    }

    static void reset() {
        Arrays.fill(HEAD, 0.0);
        Arrays.fill(SCAN, 0.0);
    }

    static void tick(class_638 w, class_243 home, class_243 away, boolean advance, double growPerTick) {
        if (advance) {
            drawnBlocks = Math.min(MAX_LEN, drawnBlocks + growPerTick);
        }
        for (int arm = 0; arm < 3; ++arm) {
            int drawn;
            int n = COUNT[arm];
            double len = Spiral.LENGTH[arm];
            double want = Math.min((double)n, drawnBlocks / len * (double)n);
            if (want > HEAD[arm]) {
                int i0 = (int)Math.floor(HEAD[arm]) + 1;
                int i1 = (int)Math.floor(want);
                for (int k = i0; k <= i1; ++k) {
                    Ribbon.emit(w, arm, k, want, true, home, away);
                }
                Ribbon.HEAD[arm] = want;
            }
            if ((drawn = (int)Math.floor(HEAD[arm])) <= 0) continue;
            double perTick = Math.max(0.5, (double)drawn / 300.0);
            double from = SCAN[arm];
            double to = from + perTick;
            int k0 = (int)Math.floor(from) + 1;
            int k1 = (int)Math.floor(to);
            for (int k = k0; k <= k1; ++k) {
                Ribbon.emit(w, arm, Math.floorMod(k, drawn), drawn, false, home, away);
            }
            Ribbon.SCAN[arm] = to >= (double)drawn ? to % (double)drawn : to;
        }
    }

    private static void emitRing(class_638 w, int ring, int i, int n, class_243 home, class_243 away) {
        double ang = ((double)i + 0.5) / (double)n * Math.PI * 2.0;
        class_243 p = Spiral.ringAt(ring, ang, home, away);
        class_243 dir = Spiral.ringTangent(ring, ang, home, away);
        float[] c = Ribbon.ridge(0.62, ring);
        float j = Ribbon.hash01(i * 17 + ring * 101) - 0.5f;
        float bright = c[3] * (1.0f + 0.1f * j) * 0.7f;
        Ribbon.segment(w, p, dir, 0.06820000000000001, 0.04 * (double)(1.0f + 0.08f * j), c, bright);
    }

    private static void emit(class_638 w, int arm, int index, double head, boolean fadeTip, class_243 home, class_243 away) {
        int n = COUNT[arm];
        int i = Math.floorMod(index, n);
        double u = ((double)i + 0.5) / (double)n;
        class_243 p = Spiral.at(arm, u, home, away);
        class_243 dir = Spiral.tangent(arm, u, home, away);
        float[] c = Ribbon.ridge(u, arm);
        float j = Ribbon.hash01(i * 31 + arm) - 0.5f;
        float tip = fadeTip ? Ribbon.smooth((float)((head - (double)i) / 30.0)) : 1.0f;
        float bright = c[3] * (1.0f + 0.1f * j) * tip * 0.34f;
        Ribbon.segment(w, p, dir, 0.06820000000000001, 0.04 * (double)(1.0f + 0.08f * j), c, bright);
        if (i % 3 == 0) {
            float halo = ShaderState.active() ? bright * 0.1f : bright * 0.11f * 1.4f;
            double wide = ShaderState.active() ? 0.12 : 0.136;
            Ribbon.segment(w, p, dir, 0.12276000000000002, wide, c, halo);
        }
    }

    private static float hash01(int x) {
        x ^= x >>> 15;
        x *= 2146121005;
        x ^= x >>> 12;
        x *= -2073254261;
        x ^= x >>> 16;
        return (float)(x & 0xFFFF) / 65536.0f;
    }

    private static void segment(class_638 w, class_243 p, class_243 dir, double len, double width, float[] c, float bright) {
        if (bright <= 0.004f) {
            return;
        }
        class_703 q = SparkParticle.spawn(0, w, p.field_1352, p.field_1351, p.field_1350, (float)width, 300, 0, PEARL);
        if (q instanceof SparkParticle) {
            SparkParticle sp = (SparkParticle)q;
            sp.setSteady(true);
            sp.setStreak(dir.field_1352, dir.field_1351, dir.field_1350, (float)len);
            sp.paint(c[0], c[1], c[2]);
            sp.setBrightness(bright);
        }
    }

    private static float[] ridge(double u, int arm) {
        float[] out = new float[4];
        float hue = (float)(u * 1.4 + (double)arm / 3.0 + 0.02);
        float[] rgb = Rainbow.hsb(hue, 0.72f, 1.0f);
        out[0] = rgb[0];
        out[1] = rgb[1];
        out[2] = rgb[2];
        float fall = Ribbon.smooth((float)Math.max(0.0, (u - 0.15) / 0.78));
        float bright = 1.0f - 0.95f * (float)Math.pow(fall, 1.35);
        float end = (float)Math.max(0.0, (u - 0.94) / 0.06);
        out[3] = Math.max(0.0f, bright *= 1.0f - Ribbon.smooth(end));
        return out;
    }

    private static float smooth(float x) {
        float y = Math.max(0.0f, Math.min(1.0f, x));
        return y * y * (3.0f - 2.0f * y);
    }

    static {
        int k;
        PEARL = class_2960.method_60655((String)"pianofx", (String)"particle/streak");
        COUNT = new int[3];
        RING_COUNT = new int[2];
        RING_SCAN = new double[2];
        HEAD = new double[3];
        SCAN = new double[3];
        for (k = 0; k < 3; ++k) {
            Ribbon.COUNT[k] = Spiral.segments(k, 0.062);
        }
        for (k = 0; k < 2; ++k) {
            Ribbon.RING_COUNT[k] = Spiral.ringSegments(k, 0.062);
            Ribbon.RING_SCAN[k] = 0.0;
        }
        Arrays.fill(HEAD, 0.0);
        Arrays.fill(SCAN, 0.0);
        double m = 0.0;
        for (double l : Spiral.LENGTH) {
            m = Math.max(m, l);
        }
        MAX_LEN = m;
        drawnBlocks = 18.0;
    }
}

