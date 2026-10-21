/*
 * Decompiled with CFR 0.152.
 */
package com.pianofx.client.render;

import java.util.Arrays;

public final class PianoKeys {
    private static final float[] LEVEL = new float[12];
    private static final float DECAY = 0.12f;

    private PianoKeys() {
    }

    public static void press(int midi) {
        PianoKeys.LEVEL[Math.floorMod((int)midi, (int)12)] = 1.0f;
    }

    public static float level(int pitchClass) {
        return LEVEL[Math.floorMod(pitchClass, 12)];
    }

    public static void tick() {
        for (int i = 0; i < LEVEL.length; ++i) {
            if (!(LEVEL[i] > 0.0f)) continue;
            PianoKeys.LEVEL[i] = Math.max(0.0f, LEVEL[i] - 0.12f);
        }
    }

    public static void clear() {
        Arrays.fill(LEVEL, 0.0f);
    }
}

