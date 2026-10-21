/*
 * Decompiled with CFR 0.152.
 */
package com.pianofx.score;

import com.pianofx.score.NoteEvent;
import java.util.List;

public record Score(String name, List<NoteEvent> notes, double durationSec, double bpm) {
}

