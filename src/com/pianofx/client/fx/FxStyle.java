/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2960
 */
package com.pianofx.client.fx;

import com.pianofx.score.NoteEvent;
import com.pianofx.score.Score;
import net.minecraft.class_2960;

public record FxStyle(String kind, double speed, double range, double curl, double wobble, double lifeTicks, float glyphScale, int trailColor, int glyphColor, double centerMidi, class_2960[] glyphs) {
    public static final class_2960 G_WHOLE = class_2960.method_60655((String)"pianofx", (String)"particle/note_0");
    public static final class_2960 G_REST8 = class_2960.method_60655((String)"pianofx", (String)"particle/note_1");
    public static final class_2960 G_HALF = class_2960.method_60655((String)"pianofx", (String)"particle/note_2");
    public static final class_2960 G_SHARP_Q = class_2960.method_60655((String)"pianofx", (String)"particle/note_3");
    public static final class_2960 G_QUARTER = class_2960.method_60655((String)"pianofx", (String)"particle/note_4");
    public static final class_2960 G_EIGHTH = class_2960.method_60655((String)"pianofx", (String)"particle/note_5");
    public static final class_2960 G_REST16 = class_2960.method_60655((String)"pianofx", (String)"particle/note_6");
    public static final class_2960 G_BEAM8 = class_2960.method_60655((String)"pianofx", (String)"particle/note_7");
    public static final class_2960 G_TREBLE = class_2960.method_60655((String)"pianofx", (String)"particle/note_8");
    public static final class_2960 G_SIXTEENTH = class_2960.method_60655((String)"pianofx", (String)"particle/note_9");
    public static final class_2960 G_BASS = class_2960.method_60655((String)"pianofx", (String)"particle/note_10");
    public static final class_2960 G_BEAM16 = class_2960.method_60655((String)"pianofx", (String)"particle/note_11");
    private static final class_2960[] PITCH_GLYPHS = new class_2960[]{G_WHOLE, G_REST8, G_HALF, G_SHARP_Q, G_QUARTER, G_EIGHTH, G_REST16, G_BEAM8, G_TREBLE, G_SIXTEENTH, G_BASS, G_BEAM16};
    private static final int C_WARM = 0;
    private static final int C_WHITE = 1;
    private static final int C_BLUE = 2;
    private static final int C_GOLD = 4;
    private static final int C_COOL = 5;
    private static final int C_CREAM = 6;
    private static final int LOW_CLEF_MAX = 50;
    private static final int HIGH_CLEF_MIN = 78;

    private static int fnv(String s) {
        int h = -2128831035;
        for (int i = 0; i < s.length(); ++i) {
            h ^= s.charAt(i);
            h *= 16777619;
        }
        return h;
    }

    public class_2960 glyphFor(int index) {
        return this.glyphs[Math.floorMod(index, this.glyphs.length)];
    }

    public class_2960 glyphForMidi(int midi) {
        if (midi <= 50) {
            return G_BASS;
        }
        if (midi >= 78) {
            return G_TREBLE;
        }
        return PITCH_GLYPHS[Math.floorMod(midi, 12)];
    }

    public static FxStyle defaultStyle() {
        return new FxStyle("\u661f\u4e91", 1.0, 1.0, 0.055, 0.03, 150.0, 0.34f, 0, 6, 68.0, PITCH_GLYPHS);
    }

    public static FxStyle of(Score score) {
        int glyphColor;
        int trailColor;
        double wobble;
        double curl;
        double speed;
        String kind;
        if (score == null || score.notes().isEmpty()) {
            return FxStyle.defaultStyle();
        }
        double duration = Math.max(1.0, score.durationSec());
        double density = (double)score.notes().size() / duration;
        int lo = 127;
        int hi = 0;
        double velSum = 0.0;
        double midiSum = 0.0;
        for (NoteEvent n : score.notes()) {
            lo = Math.min(lo, n.midi());
            hi = Math.max(hi, n.midi());
            velSum += (double)n.velocity();
            midiSum += (double)n.midi();
        }
        double centerMidi = midiSum / (double)score.notes().size();
        int span = Math.max(1, hi - lo);
        double avgVel = velSum / (double)score.notes().size();
        int arch = Math.floorMod(FxStyle.fnv(score.name()), 3);
        class_2960[] glyphs = switch (arch) {
            case 1 -> {
                kind = "\u87ba\u65cb";
                speed = 0.95;
                double range = 1.12;
                curl = 0.115;
                wobble = 0.05;
                float glyphScale = 0.2f;
                trailColor = 4;
                glyphColor = 6;
                yield PITCH_GLYPHS;
            }
            case 2 -> {
                kind = "\u6d41\u661f";
                speed = 1.55;
                double range = 1.45;
                curl = 0.022;
                wobble = 0.012;
                float glyphScale = 0.21f;
                trailColor = 0;
                glyphColor = 6;
                yield PITCH_GLYPHS;
            }
            default -> {
                kind = "\u661f\u4e91";
                speed = 0.9;
                double range = 0.95;
                curl = 0.058;
                wobble = 0.042;
                float glyphScale = 0.205f;
                trailColor = 0;
                glyphColor = 6;
                yield PITCH_GLYPHS;
            }
        };
        double densFactor = Math.max(0.75, Math.min(1.45, 0.72 + density * 0.1));
        double bpm = score.bpm() > 0.0 ? score.bpm() : 120.0;
        double tempoFactor = Math.max(0.55, Math.min(1.45, bpm / 120.0));
        double life = Math.max(110.0, Math.min(300.0, (210.0 - density * 9.0) / Math.sqrt(tempoFactor)));
        return new FxStyle(kind, speed * densFactor * tempoFactor, range *= 0.82 + Math.min(1.0, (double)span / 72.0) * 0.55, curl, wobble, life, glyphScale *= 0.88f + (float)(avgVel / 127.0) * 0.35f, trailColor, glyphColor, centerMidi, glyphs);
    }
}

