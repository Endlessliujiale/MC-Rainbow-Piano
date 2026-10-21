/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1074
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_364
 *  net.minecraft.class_437
 */
package com.pianofx.client.gui;

import com.pianofx.client.ScorePlayer;
import com.pianofx.client.gui.FlatButton;
import com.pianofx.client.gui.MidiImport;
import com.pianofx.score.NoteEvent;
import com.pianofx.score.Score;
import com.pianofx.score.ScoreLibrary;
import java.util.List;
import net.minecraft.class_1074;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_364;
import net.minecraft.class_437;

public class SongScreen
extends class_437 {
    private static final int C_DIM = -1946157056;
    private static final int C_PANEL = -267710697;
    private static final int C_EDGE = -13945272;
    private static final int C_HEADER = -15723234;
    private static final int C_TEXT = -2300171;
    private static final int C_DIMTEXT = -8483677;
    private static final int C_ACCENT = -8407809;
    private int rows = 7;
    private final class_437 parent;
    private int page;
    private int px;
    private int py;
    private int panelW;
    private int panelH;

    public SongScreen(class_437 parent) {
        super((class_2561)class_2561.method_43471((String)"gui.pianofx.songs"));
        this.parent = parent;
    }

    protected void method_25426() {
        int idx;
        this.panelW = Math.min(460, this.field_22789 - 24);
        this.panelH = Math.min(300, this.field_22790 - 16);
        this.px = (this.field_22789 - this.panelW) / 2;
        this.py = (this.field_22790 - this.panelH) / 2;
        this.rows = Math.max(2, (this.panelH - 34 - 74) / 22);
        List<String> names = ScoreLibrary.names();
        int pages = Math.max(1, (names.size() + this.rows - 1) / this.rows);
        this.page = Math.max(0, Math.min(this.page, pages - 1));
        int rowY = this.py + 40;
        int rowW = this.panelW - 24;
        for (int i = 0; i < this.rows && (idx = this.page * this.rows + i) < names.size(); ++i) {
            String name = names.get(idx);
            Score s = ScoreLibrary.get(name);
            boolean selected = name.equals(ScorePlayer.selectedName());
            String label = name + (String)(s == null ? "" : "   " + Math.round(s.bpm()) + " BPM \u00b7 " + class_1074.method_4662((String)"gui.pianofx.notes_count", (Object[])new Object[]{s.notes().size()}));
            this.method_37063((class_364)FlatButton.row(this.px + 12, rowY + i * 22, rowW, 19, (class_2561)class_2561.method_43470((String)label), b -> {
                ScorePlayer.select(name);
                this.refresh();
            }, selected));
        }
        int by = this.py + this.panelH - 32;
        int bw = (this.panelW - 24 - 16) / 3;
        this.method_37063((class_364)FlatButton.centered(this.px + 12, by, bw, 20, (class_2561)class_2561.method_43471((String)(ScorePlayer.playing() ? "gui.pianofx.pause" : "gui.pianofx.play")), b -> {
            ScorePlayer.toggle();
            this.refresh();
        }));
        this.method_37063((class_364)FlatButton.centered(this.px + 12 + bw + 8, by, bw, 20, (class_2561)class_2561.method_43471((String)"gui.pianofx.import"), b -> MidiImport.choose(name -> this.refresh())));
        this.method_37063((class_364)FlatButton.centered(this.px + 12 + (bw + 8) * 2, by, bw, 20, (class_2561)class_2561.method_43471((String)"gui.pianofx.back"), b -> this.method_25419()));
        if (pages > 1) {
            this.method_37063((class_364)FlatButton.centered(this.px + this.panelW - 44, this.py + 9, 15, 16, (class_2561)class_2561.method_43470((String)"\u2039"), b -> {
                this.page = Math.max(0, this.page - 1);
                this.refresh();
            }));
            this.method_37063((class_364)FlatButton.centered(this.px + this.panelW - 26, this.py + 9, 15, 16, (class_2561)class_2561.method_43470((String)"\u203a"), b -> {
                this.page = Math.min(pages - 1, this.page + 1);
                this.refresh();
            }));
        }
    }

    private void refresh() {
        this.method_37067();
        this.method_25426();
    }

    public boolean method_25421() {
        return false;
    }

    public void method_25420(class_332 ctx, int mouseX, int mouseY, float delta) {
        Score sel;
        ctx.method_25294(0, 0, this.field_22789, this.field_22790, -1946157056);
        ctx.method_25294(this.px - 1, this.py - 1, this.px + this.panelW + 1, this.py + this.panelH + 1, -13945272);
        ctx.method_25294(this.px, this.py, this.px + this.panelW, this.py + this.panelH, -267710697);
        ctx.method_25294(this.px, this.py, this.px + this.panelW, this.py + 32, -15723234);
        ctx.method_51433(this.field_22793, class_1074.method_4662((String)"gui.pianofx.songs", (Object[])new Object[0]), this.px + 12, this.py + 11, -2300171, false);
        String count = class_1074.method_4662((String)"gui.pianofx.count", (Object[])new Object[]{ScoreLibrary.names().size()});
        ctx.method_51433(this.field_22793, count, this.px + this.panelW - 12 - this.field_22793.method_1727(count), this.py + 11, -8483677, false);
        if (ScoreLibrary.names().isEmpty()) {
            ctx.method_51433(this.field_22793, class_1074.method_4662((String)"gui.pianofx.empty", (Object[])new Object[0]), this.px + 12, this.py + 54, -8483677, false);
        }
        String info = (sel = ScorePlayer.selectedScore()) == null ? class_1074.method_4662((String)"gui.pianofx.no_selection", (Object[])new Object[0]) : class_1074.method_4662((String)"gui.pianofx.range_info", (Object[])new Object[]{SongScreen.midiName(SongScreen.minMidi(sel)), SongScreen.midiName(SongScreen.maxMidi(sel)), String.format("%.1f", sel.durationSec())});
        ctx.method_51433(this.field_22793, this.field_22793.method_27523(info, this.panelW - 24), this.px + 12, this.py + this.panelH - 54, -8407809, false);
        String dir = class_1074.method_4662((String)"gui.pianofx.dir", (Object[])new Object[]{ScoreLibrary.dir()});
        ctx.method_51433(this.field_22793, this.field_22793.method_27523(dir, this.panelW - 24), this.px + 12, this.py + this.panelH - 42, -8483677, false);
    }

    private static int minMidi(Score s) {
        int lo = 127;
        for (NoteEvent n : s.notes()) {
            lo = Math.min(lo, n.midi());
        }
        return lo;
    }

    private static int maxMidi(Score s) {
        int hi = 0;
        for (NoteEvent n : s.notes()) {
            hi = Math.max(hi, n.midi());
        }
        return hi;
    }

    private static String midiName(int midi) {
        String[] names = new String[]{"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
        return names[Math.floorMod(midi, 12)] + (midi / 12 - 1);
    }

    public void method_25419() {
        class_310.method_1551().method_1507(this.parent);
    }
}

