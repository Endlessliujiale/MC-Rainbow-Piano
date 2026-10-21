/*
 * Decompiled with CFR 0.152.
 */
package com.pianofx.score;

public record NoteEvent(double timeSec, int midi, int velocity, double durSec) {
    public float pitch01() {
        return Math.max(0.0f, Math.min(1.0f, (float)(this.midi - 21) / 87.0f));
    }
}

