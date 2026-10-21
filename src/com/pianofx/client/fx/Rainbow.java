/*
 * Decompiled with CFR 0.152.
 */
package com.pianofx.client.fx;

public final class Rainbow {
    private Rainbow() {
    }

    public static float[] hsb(float h, float s, float v) {
        float g;
        float r;
        h -= (float)Math.floor(h);
        float c = v * s;
        float x = c * (1.0f - Math.abs(h * 6.0f % 2.0f - 1.0f));
        float m = v - c;
        int seg = (int)(h * 6.0f);
        return new float[]{r + m, g + m, (switch (seg) {
            case 0 -> {
                r = c;
                g = x;
                yield 0.0f;
            }
            case 1 -> {
                r = x;
                g = c;
                yield 0.0f;
            }
            case 2 -> {
                r = 0.0f;
                g = c;
                yield x;
            }
            case 3 -> {
                r = 0.0f;
                g = x;
                yield c;
            }
            case 4 -> {
                r = x;
                g = 0.0f;
                yield c;
            }
            default -> {
                r = c;
                g = 0.0f;
                yield x;
            }
        }) + m};
    }
}

