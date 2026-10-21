/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2350
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 */
package com.pianofx.client;

import com.pianofx.PianoFX;
import com.pianofx.client.fx.FxManager;
import com.pianofx.client.fx.FxStyle;
import com.pianofx.score.NoteEvent;
import com.pianofx.score.Score;
import com.pianofx.score.ScoreLibrary;
import java.util.List;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_310;

public final class ScorePlayer {
    private static Score score;
    private static double t;
    private static boolean playing;
    private static boolean paused;
    private static int cursor;
    private static String selected;
    private static class_243 anchor;
    private static class_2350 facing;

    private ScorePlayer() {
    }

    public static void setAnchor(class_243 pos, class_2350 dir) {
        anchor = pos;
        if (dir != null) {
            facing = dir;
        }
    }

    public static class_243 anchor() {
        if (anchor != null) {
            return anchor;
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null) {
            return class_243.field_1353;
        }
        return mc.field_1724.method_33571().method_1019(mc.field_1724.method_5828(1.0f).method_1021(2.0)).method_1031(0.0, -0.6, 0.0);
    }

    public static class_2350 facing() {
        if (anchor != null) {
            return facing;
        }
        class_310 mc = class_310.method_1551();
        return mc.field_1724 == null ? class_2350.field_11043 : mc.field_1724.method_5735();
    }

    public static List<String> names() {
        return ScoreLibrary.names();
    }

    public static void select(String name) {
        selected = name == null ? "" : name;
        ScorePlayer.stop();
    }

    public static String selectedName() {
        if (selected.isEmpty()) {
            String first = ScoreLibrary.first();
            selected = first == null ? "" : first;
        }
        return selected;
    }

    public static Score selectedScore() {
        String n = ScorePlayer.selectedName();
        return n.isEmpty() ? null : ScoreLibrary.get(n);
    }

    public static void start() {
        Score s = ScorePlayer.selectedScore();
        if (s == null) {
            return;
        }
        score = s;
        t = 0.0;
        cursor = 0;
        playing = true;
        paused = false;
        FxManager.clear();
        FxManager.clearForcedEnding();
        FxManager.resetEnding();
        FxManager.useStyle(FxStyle.of(s));
        PianoFX.LOGGER.info("[PianoFX] \u81ea\u52a8\u6f14\u594f {} ({} \u97f3\u7b26, {} BPM)", new Object[]{s.name(), s.notes().size(), Math.round(s.bpm())});
    }

    public static void stop() {
        com.pianofx.client.fx.NoteSound.releaseAll();
        if(score!=null)FxManager.beginEnding();
        playing = false;
        paused = false;
        score = null;
        t = 0.0;
        cursor = 0;
    }

    public static void pause() {
        com.pianofx.client.fx.NoteSound.releaseAll();
        if (score == null) {
            return;
        }
        playing = false;
        paused = true;
    }

    public static void resume() {
        if (score == null || t <= 0.0) {
            ScorePlayer.start();
            return;
        }
        playing = true;
        paused = false;
    }

    public static boolean paused() {
        return paused;
    }

    public static void toggle() {
        if (playing) {
            ScorePlayer.pause();
        } else if (paused) {
            ScorePlayer.resume();
        } else {
            ScorePlayer.start();
        }
    }

    public static boolean playing() {
        return playing;
    }

    public static double timeSec() {
        return t;
    }

    public static Score currentScore() {
        return score;
    }

    public static void useManualStyle() {
        if (!playing) {
            FxManager.useStyle(FxStyle.defaultStyle());
        }
    }

    public static void tick() {
        if (!playing || score == null) {
            return;
        }
        t += 0.05;
        List<NoteEvent> notes = score.notes();
        while (cursor < notes.size() && notes.get(cursor).timeSec() <= t) {
            FxManager.playNote(ScorePlayer.anchor(), ScorePlayer.facing(), notes.get(cursor));
            ++cursor;
        }
        FxManager.songProgress(score.durationSec() - t);
        if (t > score.durationSec() + 0.2) {
            ScorePlayer.stop();
        }
    }

    static {
        selected = "";
        facing = class_2350.field_11043;
    }
}

