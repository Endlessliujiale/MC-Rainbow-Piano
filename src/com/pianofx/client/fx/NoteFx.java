/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_243
 */
package com.pianofx.client.fx;

import java.util.Random;
import net.minecraft.class_243;

public class NoteFx {
    public static final byte KIND_DOT = 0;
    public static final byte KIND_STAR = 1;
    public static final byte KIND_NOTE = 2;
    private static final int DRAW_TICKS = 18;
    private static final int HOLD_TICKS = 26;
    public final class_243[] pts;
    public final byte[] kind;
    public final float lineScale;
    public final Random rng;
    public int cursor;
    public boolean wrapped;
    public double alive;
    public final double holdSeconds;
    public boolean fading;
    public boolean dead;

    public NoteFx(class_243 center, double radius, class_243 h, double t0, double span, double spacing, double starEvery, double noteEvery, double holdSeconds, long seed) {
        this.holdSeconds = holdSeconds;
        this.rng = new Random(seed);
        class_243 up = new class_243(0.0, 1.0, 0.0);
        double len = span * radius;
        int n = Math.max(2, (int)Math.ceil(len / spacing) + 1);
        if (n > 1000) {
            n = 1000;
        }
        this.pts = new class_243[n];
        for (int i = 0; i < n; ++i) {
            double tt = t0 + span * (double)i / ((double)n - 1.0);
            this.pts[i] = center.method_1019(h.method_1021(Math.sin(tt) * radius)).method_1019(up.method_1021(-Math.cos(tt) * radius));
        }
        double eff = n > 1 ? len / (double)(n - 1) : spacing;
        this.lineScale = (float)Math.min(0.26, Math.max(0.09, eff * 1.8));
        this.kind = new byte[n];
        double acc = 0.0;
        double nextStar = this.rng.nextDouble() * starEvery;
        double nextNote = this.rng.nextDouble() * noteEvery;
        for (int i = 0; i < n; ++i) {
            if (i > 0) {
                acc += this.pts[i].method_1022(this.pts[i - 1]);
            }
            if (acc >= nextNote) {
                this.kind[i] = 2;
                nextNote = acc + noteEvery * (0.75 + this.rng.nextDouble() * 0.8);
                continue;
            }
            if (acc >= nextStar) {
                this.kind[i] = 1;
                nextStar = acc + starEvery * (0.55 + this.rng.nextDouble() * 0.9);
                continue;
            }
            this.kind[i] = 0;
        }
    }

    public int size() {
        return this.pts.length;
    }

    public class_243 pos(int i) {
        return this.pts[i];
    }

    public int step() {
        int n = this.pts.length;
        int ticks = this.wrapped ? 26 : 18;
        return Math.max(1, (int)Math.ceil((double)n / (double)ticks));
    }

    public int lifeTicks() {
        return this.wrapped ? 27 : 20;
    }

    public int advance(double dt) {
        this.alive += dt;
        double stopAt = 0.9 + this.holdSeconds;
        if (this.alive > stopAt) {
            this.fading = true;
        }
        if (this.alive > stopAt + 1.45) {
            this.dead = true;
        }
        return this.fading ? -1 : this.cursor;
    }

    public void moveCursor() {
        int n = this.pts.length;
        this.cursor += this.step();
        if (this.cursor >= n) {
            this.cursor -= n;
            this.wrapped = true;
        }
    }
}

