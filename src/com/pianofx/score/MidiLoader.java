/*
 * Decompiled with CFR 0.152.
 */
package com.pianofx.score;

import com.pianofx.score.NoteEvent;
import com.pianofx.score.Score;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public final class MidiLoader {
    private MidiLoader() {
    }

    public static Score load(Path file, String name) throws Exception {
        Sequence seq = MidiSystem.getSequence(file.toFile());
        int resolution = seq.getResolution();
        ArrayList<double[]> tempos = new ArrayList<double[]>();
        tempos.add(new double[]{0.0, 500000.0});
        ArrayList<long[]> raw = new ArrayList<long[]>();
        long maxTick = 0L;
        for (Track track : seq.getTracks()) {
            HashMap<Integer, long[]> on = new HashMap<Integer, long[]>();
            for (int i = 0; i < track.size(); ++i) {
                long[] start;
                Object d;
                MetaMessage meta;
                MidiEvent ev = track.get(i);
                long tick = ev.getTick();
                maxTick = Math.max(maxTick, tick);
                MidiMessage midiMessage = ev.getMessage();
                if (midiMessage instanceof MetaMessage && (meta = (MetaMessage)midiMessage).getType() == 81) {
                    d = meta.getData();
                    int usPerQuarter = (d[0] & 0xFF) << 16 | (d[1] & 0xFF) << 8 | d[2] & 0xFF;
                    tempos.add(new double[]{tick, usPerQuarter});
                    continue;
                }
                d = ev.getMessage();
                if (!(d instanceof ShortMessage)) continue;
                ShortMessage sm = (ShortMessage)d;
                int cmd = sm.getCommand();
                int key = sm.getChannel() << 8 | sm.getData1();
                int vel = sm.getData2();
                if (cmd == 144 && vel > 0) {
                    on.put(key, new long[]{tick, vel});
                    continue;
                }
                if (cmd != 128 && (cmd != 144 || vel != 0) || (start = (long[])on.remove(key)) == null) continue;
                raw.add(new long[]{start[0], Math.max(tick, start[0] + 1L), sm.getData1(), start[1]});
            }
            for (Map.Entry e : on.entrySet()) {
                long[] s = (long[])e.getValue();
                raw.add(new long[]{s[0], maxTick, (Integer)e.getKey() & 0xFF, s[1]});
            }
        }
        tempos.sort(Comparator.comparingDouble(a -> a[0]));
        ArrayList<double[]> merged = new ArrayList<double[]>();
        for (double[] t : tempos) {
            if (!merged.isEmpty() && ((double[])merged.get(merged.size() - 1))[0] == t[0]) {
                merged.set(merged.size() - 1, t);
                continue;
            }
            merged.add(t);
        }
        ArrayList<NoteEvent> notes = new ArrayList<NoteEvent>(raw.size());
        double lastEnd = 0.0;
        for (long[] r : raw) {
            double t0 = MidiLoader.tickToSec(r[0], resolution, merged);
            double t1 = MidiLoader.tickToSec(r[1], resolution, merged);
            notes.add(new NoteEvent(t0, (int)r[2], (int)r[3], Math.max(0.05, t1 - t0)));
            lastEnd = Math.max(lastEnd, t1);
        }
        notes.sort(Comparator.comparingDouble(NoteEvent::timeSec));
        if (notes.isEmpty()) {
            throw new IllegalStateException("MIDI \u91cc\u6ca1\u6709\u97f3\u7b26: " + String.valueOf(file.getFileName()));
        }
        return new Score(name, List.copyOf(notes), (double)seq.getMicrosecondLength() / 1000000.0, MidiLoader.averageBpm(merged, resolution, maxTick));
    }

    private static double averageBpm(List<double[]> tempos, int resolution, long maxTick) {
        double weighted = 0.0;
        double totalSec = 0.0;
        for (int i = 0; i < tempos.size(); ++i) {
            double startTick = tempos.get(i)[0];
            double usPerQuarter = tempos.get(i)[1];
            double endTick = i + 1 < tempos.size() ? tempos.get(i + 1)[0] : (double)maxTick;
            double sec = Math.max(0.0, endTick - startTick) * usPerQuarter / 1000000.0 / (double)resolution;
            weighted += sec * usPerQuarter;
            totalSec += sec;
        }
        if (totalSec <= 0.0 || weighted <= 0.0) {
            return 120.0;
        }
        return 6.0E7 / (weighted / totalSec);
    }

    private static double tickToSec(long tick, int resolution, List<double[]> tempos) {
        double sec = 0.0;
        for (int i = 0; i < tempos.size(); ++i) {
            double segEnd;
            double segStart = tempos.get(i)[0];
            double usPerQuarter = tempos.get(i)[1];
            double d = segEnd = i + 1 < tempos.size() ? tempos.get(i + 1)[0] : Double.MAX_VALUE;
            if ((double)tick <= segStart) break;
            double end = Math.min((double)tick, segEnd);
            sec += (end - segStart) * usPerQuarter / 1000000.0 / (double)resolution;
            if ((double)tick <= segEnd) break;
        }
        return sec;
    }
}

