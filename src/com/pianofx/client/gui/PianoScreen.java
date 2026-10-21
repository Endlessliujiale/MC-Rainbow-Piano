/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1074
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_2382
 *  net.minecraft.class_243
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_437
 */
package com.pianofx.client.gui;

import com.pianofx.client.PianoCamera;
import com.pianofx.client.ScorePlayer;
import com.pianofx.client.fx.FxManager;
import com.pianofx.client.gui.SongScreen;
import com.pianofx.score.NoteEvent;
import com.pianofx.score.Score;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.class_1074;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2382;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_437;

public class PianoScreen
extends class_437 {
    private static final long GLOW_MS = 320L;
    private static final int[] WHITE_SEMI = new int[]{0, 2, 4, 5, 7, 9, 11, 12, 14, 16, 17, 19, 21, 23, 24};
    private static final int[] WHITE_CODES = new int[]{81, 87, 69, 82, 84, 65, 83, 68, 70, 71, 90, 88, 67, 86, 66};
    private static final int[] BLACK_SEMI = new int[]{1, 3, 6, 8, 10, 13, 15, 18, 20, 22};
    private static final int[] BLACK_CODES = new int[]{49, 50, 51, 52, 53, 54, 55, 56, 57, 48};
    private static final int SH_DIAMOND = 0;
    private static final int SH_CIRCLE = 1;
    private static final int SH_SQUARE = 2;
    private static final int[] NODE_SHAPE = new int[]{0, 0, 1, 0, 1, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0};
    private static final int C_PANEL = 856100382;
    private static final int C_GOLD = -1523590;
    private static final int C_EDGE_DIM = -1696415606;
    private static final int C_EDGE_LIT = -3896;
    private static final int C_NODE_IN = 1510411806;
    private static final int C_NODE_ON = -419439204;
    private static final int C_NUM = -595508;
    private static final int C_NUM_ON = -11913968;
    private static final int C_TEXT_LIGHT = -1379585;
    private static final int C_DIMTEXT = -1332763708;
    private static final int C_BAR_BG = 0x44FFFFFF;
    private static final int C_BAR_FILL = -993624;
    private static Layout layout = Layout.FULL;
    private final Set<Integer> held = new HashSet<Integer>();
    private final long[] flashAt = new long[36];
    private int baseMidi = 60;
    private Mode mode;
    private int px;
    private int panelTop;
    private int panelW;
    private int panelH;
    private int headH;
    private int node;
    private int gap;
    private int gridX;
    private int gridY;
    private int gridW;
    private int gridH;
    private int accY;
    private int accSize;
    private int accH;
    private int btnH;
    private int hintY;
    private Rect modeBtn;
    private Rect songsBtn;
    private Rect leaveBtn;
    private Rect minBtn;
    private Rect expandBtn;
    private Rect hideBtn;
    private Rect restoreTab;
    private Rect prevBtn;
    private Rect playBtn;
    private Rect nextBtn;
    private int progX;
    private int progY;
    private int progW;
    private int progH;

    public PianoScreen() {
        this(Mode.PLAY);
    }

    public PianoScreen(Mode mode) {
        super((class_2561)class_2561.method_43471((String)"gui.pianofx.title"));
        this.mode = mode;
    }

    public static Layout layout() {
        return layout;
    }

    public static void setLayout(Layout next) {
        layout = next == null ? Layout.FULL : next;
    }

    public static void open(class_2338 pos, class_2350 dir) {
        ScorePlayer.setAnchor(class_243.method_24953((class_2382)pos).method_1031(0.0, 0.9, 0.0), dir);
        Mode m = ScorePlayer.playing() || ScorePlayer.paused() ? Mode.DEMO : Mode.PLAY;
        class_310.method_1551().method_1507((class_437)new PianoScreen(m));
    }

    protected void method_25426() {
        this.clearButtons();
        if (layout == Layout.FULL) {
            this.initFull();
        } else {
            this.initMini();
        }
        if (layout == Layout.HIDDEN) {
            this.restoreTab = new Rect(this.field_22789 - 22, this.field_22790 - 22, 15, 15);
        }
    }

    private void gridGeometry(float nodeScale, int minNode, int maxNode) {
        this.node = Math.max(minNode, Math.min(maxNode, Math.round((float)this.field_22790 * nodeScale)));
        this.gap = Math.max(3, Math.round((float)this.node * 0.26f));
        this.gridW = 5 * this.node + 4 * this.gap;
        this.gridH = 3 * this.node + 2 * this.gap;
        this.accSize = Math.max(8, Math.round((float)this.node * 0.5f));
        this.accH = this.accSize + 3;
    }

    private void initFull() {
        int bottom = this.field_22790 - 8;
        this.headH = 13;
        this.gridGeometry(0.072f, 19, 27);
        this.btnH = 11;
        if (this.mode == Mode.PLAY) {
            this.panelW = Math.min(this.field_22789 - 20, Math.max(232, this.gridW + 30));
            this.panelH = this.headH + this.accH + this.gridH + 13;
        } else {
            this.panelW = Math.min(this.field_22789 - 20, 340);
            this.panelH = this.headH + 4 + 3 + 4 + 13 + 6;
        }
        this.px = (this.field_22789 - this.panelW) / 2;
        this.panelTop = bottom - this.panelH;
        this.gridX = this.px + (this.panelW - this.gridW) / 2;
        this.gridY = this.panelTop + this.headH + this.accH;
        this.accY = this.panelTop + this.headH + 2;
        this.hintY = this.panelTop + this.headH + this.accH + this.gridH + 3;
        int y = this.panelTop + 2;
        int x = this.px + this.panelW - 3 - this.btnH;
        this.leaveBtn = new Rect(x, y, this.btnH, this.btnH);
        this.minBtn = new Rect(x -= this.btnH + 3, y, this.btnH, this.btnH);
        this.songsBtn = new Rect(x -= this.btnH + 3, y, this.btnH, this.btnH);
        this.modeBtn = new Rect(x -= this.btnH + 3, y, this.btnH, this.btnH);
        if (this.mode == Mode.DEMO) {
            int rowY = this.panelTop + this.headH + 11;
            this.progH = 3;
            this.progX = this.px + 7;
            this.progY = this.panelTop + this.headH + 4;
            this.progW = this.panelW - 14;
            int smallW = 20;
            int playW = 30;
            int g = 4;
            this.prevBtn = new Rect(this.px + 7, rowY, smallW, 13);
            this.playBtn = new Rect(this.px + 7 + smallW + g, rowY, playW, 13);
            this.nextBtn = new Rect(this.px + 7 + smallW + g + playW + g, rowY, smallW, 13);
        }
    }

    private void initMini() {
        int bottom = this.field_22790 - 6;
        this.headH = 0;
        this.gridGeometry(0.062f, 17, 23);
        this.btnH = 10;
        if (this.mode == Mode.PLAY) {
            this.panelW = this.gridW + 10;
            this.panelH = this.accH + this.gridH + 3;
        } else {
            this.panelW = Math.min(this.field_22789 - 20, 190);
            this.panelH = 15;
        }
        this.px = (this.field_22789 - this.panelW) / 2;
        this.panelTop = bottom - this.panelH;
        this.gridX = this.px + 5;
        this.gridY = this.panelTop + this.accH;
        this.accY = this.panelTop + 1;
        this.hintY = this.panelTop;
        int y = this.panelTop - this.btnH - 2;
        int x = this.px + this.panelW - this.btnH;
        this.leaveBtn = new Rect(x, y, this.btnH, this.btnH);
        this.hideBtn = new Rect(x -= this.btnH + 2, y, this.btnH, this.btnH);
        this.expandBtn = new Rect(x -= this.btnH + 2, y, this.btnH, this.btnH);
        if (this.mode == Mode.DEMO) {
            y = this.panelTop + 2;
            x = this.px + this.panelW - 3 - this.btnH;
            this.leaveBtn = new Rect(x, y, this.btnH, this.btnH);
            this.hideBtn = new Rect(x - this.btnH - 2, y, this.btnH, this.btnH);
            this.expandBtn = new Rect(x - (this.btnH + 2) * 2, y, this.btnH, this.btnH);
            this.playBtn = new Rect(x - (this.btnH + 2) * 3, y, this.btnH, this.btnH);
            this.progH = 2;
            this.progX = this.px + 1;
            this.progY = this.panelTop + this.panelH - 2;
            this.progW = this.panelW - 2;
        }
    }

    private void clearButtons() {
        this.modeBtn = null;
        this.songsBtn = null;
        this.leaveBtn = null;
        this.minBtn = null;
        this.expandBtn = null;
        this.hideBtn = null;
        this.restoreTab = null;
        this.prevBtn = null;
        this.playBtn = null;
        this.nextBtn = null;
        this.progW = 0;
    }

    public boolean method_25421() {
        return false;
    }

    public void method_25394(class_332 ctx, int mouseX, int mouseY, float delta) {
        this.method_25420(ctx, mouseX, mouseY, delta);
    }

    public void method_25420(class_332 ctx, int mouseX, int mouseY, float delta) {
        ctx.method_25294(this.field_22789-104,8,this.field_22789-8,27,0xDD253044);
        ctx.method_51433(this.field_22793,"自定义键位 [F6]",this.field_22789-99,14,0xFFFFDEAC,true);
        if (layout == Layout.HIDDEN) {
            this.drawRestoreTab(ctx, mouseX, mouseY);
            return;
        }
        boolean mini = layout == Layout.MINI;
        this.shadeBottom(ctx, this.panelTop - (mini ? 16 : 12));
        this.drawPanel(ctx);
        if (this.mode == Mode.PLAY) {
            this.drawAccidentals(ctx, mouseX, mouseY);
            this.drawNodes(ctx);
            if (!mini) {
                this.drawHint(ctx);
            }
        } else if (!mini) {
            this.drawTransport(ctx, mouseX, mouseY);
        } else {
            this.drawMiniTransport(ctx);
        }
        this.drawHeader(ctx, mouseX, mouseY);
    }

    private void shadeBottom(class_332 ctx, int top) {
        int steps = 8;
        int h = Math.max(1, this.field_22790 - top);
        for (int i = 0; i < steps; ++i) {
            int a = (int)(88.0 * (((double)i + 1.0) / (double)steps) * (((double)i + 1.0) / (double)steps));
            int y0 = top + h * i / steps;
            int y1 = top + h * (i + 1) / steps;
            ctx.method_25294(0, y0, this.field_22789, y1, a << 24);
        }
    }

    private void drawPanel(class_332 ctx) {
        boolean mini;
        boolean bl = mini = layout == Layout.MINI;
        if (mini && this.mode == Mode.PLAY) {
            return;
        }
        this.roundRect(ctx, this.px, this.panelTop, this.panelW, this.panelH, mini ? 856100382 : 856100382);
        if (!mini) {
            ctx.method_25294(this.px + 8, this.panelTop + this.headH, this.px + this.panelW - 8, this.panelTop + this.headH + 1, 1441846952);
        }
    }

    private void drawHeader(class_332 ctx, int mouseX, int mouseY) {
        Object title;
        boolean narrow;
        if (layout == Layout.MINI) {
            this.drawMiniHeader(ctx, mouseX, mouseY);
            return;
        }
        boolean bl = narrow = this.panelW < 300;
        String badge = narrow ? (this.mode == Mode.PLAY ? "\u266a" : "\u25b6") : (this.mode == Mode.PLAY ? "\u266a " + class_1074.method_4662((String)"gui.pianofx.mode_play", (Object[])new Object[0]) : "\u25b6 " + class_1074.method_4662((String)"gui.pianofx.mode_demo", (Object[])new Object[0]));
        int ty = this.panelTop + (this.headH - 8) / 2;
        ctx.method_51433(this.field_22793, badge, this.px + 8, ty, -1379585, true);
        int badgeW = this.field_22793.method_1727(badge);
        int rightEdge = (this.modeBtn != null ? this.modeBtn.x : this.px + this.panelW) - 6;
        String time = null;
        if (this.mode == Mode.DEMO) {
            Score sc = ScorePlayer.currentScore();
            time = PianoScreen.fmtTime(ScorePlayer.timeSec()) + " / " + PianoScreen.fmtTime(sc == null ? 0.0 : sc.durationSec());
            rightEdge -= this.field_22793.method_1727(time) + 8;
        }
        Score score = this.mode == Mode.PLAY ? ScorePlayer.selectedScore() : ScorePlayer.currentScore();
        Object object = title = score != null ? score.name() : class_1074.method_4662((String)"gui.pianofx.free_play", (Object[])new Object[0]);
        if (score != null && score.bpm() > 0.0 && !narrow) {
            title = (String)title + "  \u00b7  " + Math.round(score.bpm()) + " BPM";
        }
        int titleX = this.px + 12 + badgeW;
        int avail = Math.max(16, rightEdge - titleX);
        ctx.method_51433(this.field_22793, this.field_22793.method_27523((String)title, avail), titleX, ty, -1379585, true);
        if (time != null) {
            int limit = (this.modeBtn != null ? this.modeBtn.x : this.px + this.panelW) - 6;
            ctx.method_51433(this.field_22793, time, limit - this.field_22793.method_1727(time), ty, -1332763708, true);
        }
        this.drawGlyphButton(ctx, this.modeBtn, this.mode == Mode.PLAY ? "\u25b6" : "\u266a", PianoScreen.hover(this.modeBtn, mouseX, mouseY));
        this.drawGlyphButton(ctx, this.songsBtn, "\u2261", PianoScreen.hover(this.songsBtn, mouseX, mouseY));
        this.drawGlyphButton(ctx, this.minBtn, "\u2013", PianoScreen.hover(this.minBtn, mouseX, mouseY));
        this.drawGlyphButton(ctx, this.leaveBtn, "\u2715", PianoScreen.hover(this.leaveBtn, mouseX, mouseY));
    }

    private void drawMiniHeader(class_332 ctx, int mouseX, int mouseY) {
        if (this.mode == Mode.PLAY) {
            this.drawGlyphButton(ctx, this.expandBtn, "\u25a3", PianoScreen.hover(this.expandBtn, mouseX, mouseY));
            this.drawGlyphButton(ctx, this.hideBtn, "\u2013", PianoScreen.hover(this.hideBtn, mouseX, mouseY));
            this.drawGlyphButton(ctx, this.leaveBtn, "\u2715", PianoScreen.hover(this.leaveBtn, mouseX, mouseY));
            return;
        }
        Score sc = ScorePlayer.currentScore();
        String left = PianoScreen.fmtTime(ScorePlayer.timeSec()) + "/" + PianoScreen.fmtTime(sc == null ? 0.0 : sc.durationSec());
        int limit = this.expandBtn.x - 4;
        ctx.method_51433(this.field_22793, this.field_22793.method_27523(left, Math.max(10, limit - this.px - 6)), this.px + 6, this.panelTop + 4, -1379585, false);
        this.drawGlyphButton(ctx, this.playBtn, ScorePlayer.paused() || !ScorePlayer.playing() ? "\u25b6" : "\u275a\u275a", PianoScreen.hover(this.playBtn, mouseX, mouseY));
        this.drawGlyphButton(ctx, this.expandBtn, "\u25a3", PianoScreen.hover(this.expandBtn, mouseX, mouseY));
        this.drawGlyphButton(ctx, this.hideBtn, "\u2013", PianoScreen.hover(this.hideBtn, mouseX, mouseY));
        this.drawGlyphButton(ctx, this.leaveBtn, "\u2715", PianoScreen.hover(this.leaveBtn, mouseX, mouseY));
    }

    private void drawNodes(class_332 ctx) {
        long now = System.currentTimeMillis();
        for (int i = 0; i < 15; ++i) {
            int cx = this.nodeX(i);
            int cy = this.nodeY(i);
            float g = this.keyGlow(WHITE_SEMI[i], now);
            int midi = this.baseMidi + WHITE_SEMI[i];
            this.drawJianpu(ctx, cx, cy, this.node, NODE_SHAPE[i], PianoScreen.degreeOf(midi), PianoScreen.octaveOf(midi), g);
        }
    }

    private void drawJianpu(class_332 ctx, int cx, int cy, int size, int shape, int digit, int dots, float glow) {
        int r = size / 2;
        int edge = PianoScreen.mix(-1696415606, -3896, glow);
        if (glow > 0.02f) {
            for (int k = 3; k >= 1; --k) {
                int a = (int)(48.0f * glow / (float)k);
                if (a <= 0) continue;
                this.shapeSpan(ctx, cx, cy, r + k, shape, a << 24 | 0xFFD98A);
            }
        }
        this.shapeSpan(ctx, cx, cy, r, shape, edge);
        this.shapeSpan(ctx, cx, cy, r - 1, shape, PianoScreen.mix(1510411806, -419439204, glow));
        String s = Integer.toString(digit);
        int tw = this.field_22793.method_1727(s);
        int ty = cy - 4;
        if (dots > 0) {
            ty = cy - 3;
        }
        ctx.method_51433(this.field_22793, s, cx - tw / 2, ty, PianoScreen.mix(-595508, -11913968, glow), true);
        if (dots > 0) {
            int dcol = PianoScreen.mix(-1696415606, -3896, Math.max(glow, 0.4f));
            int dx = cx + tw / 2 + 2;
            ctx.method_25294(dx, cy - 6, dx + 2, cy - 4, dcol);
            if (dots >= 2) {
                ctx.method_25294(dx, cy - 3, dx + 2, cy - 1, dcol);
            }
        }
    }

    private void drawAccidentals(class_332 ctx, int mouseX, int mouseY) {
        long now = System.currentTimeMillis();
        int cy = this.accY + this.accSize / 2;
        double step = (double)this.gridW / (double)BLACK_SEMI.length;
        for (int i = 0; i < BLACK_SEMI.length; ++i) {
            int cx = (int)Math.round((double)this.gridX + step * ((double)i + 0.5));
            float g = this.keyGlow(BLACK_SEMI[i], now);
            if (PianoScreen.hover(new Rect(cx - this.accSize / 2, this.accY, this.accSize, this.accSize), mouseX, mouseY)) {
                g = Math.max(g, 0.45f);
            }
            int edge = PianoScreen.mix(1088602250, -862308, g);
            this.shapeSpan(ctx, cx, cy, this.accSize / 2, 0, edge);
            this.shapeSpan(ctx, cx, cy, this.accSize / 2 - 1, 0, PianoScreen.mix(570887710, -1426072164, g));
            if (layout == Layout.MINI) continue;
            int midi = this.baseMidi + BLACK_SEMI[i];
            String label = "#" + PianoScreen.degreeOf(midi);
            int lw = this.field_22793.method_1727(label);
            float sc = Math.max(0.5f, Math.min(0.8f, (float)this.accSize / 14.0f));
            ctx.method_51448().method_22903();
            ctx.method_51448().method_46416((float)cx - (float)lw * sc / 2.0f, (float)cy - 4.0f * sc, 0.0f);
            ctx.method_51448().method_22905(sc, sc, 1.0f);
            ctx.method_51433(this.field_22793, label, 0, 0, PianoScreen.mix(-1712799622, -11913968, g), false);
            ctx.method_51448().method_22909();
        }
    }

    private void shapeSpan(class_332 ctx, int cx, int cy, int r, int shape, int color) {
        if (r <= 0) {
            return;
        }
        for (int dy = -r; dy <= r; ++dy) {
            int hw;
            if (shape == 0) {
                hw = r - Math.abs(dy);
            } else if (shape == 1) {
                hw = (int)Math.round(Math.sqrt(Math.max(0, r * r - dy * dy)));
            } else {
                int n = hw = Math.abs(dy) > r - 2 ? r - 1 : r;
            }
            if (hw <= 0) continue;
            ctx.method_25294(cx - hw, cy + dy, cx + hw, cy + dy + 1, color);
        }
    }

    private int nodeX(int i) {
        return this.gridX + i % 5 * (this.node + this.gap) + this.node / 2;
    }

    private int nodeY(int i) {
        return this.gridY + i / 5 * (this.node + this.gap) + this.node / 2;
    }

    private static int degreeOf(int midi) {
        return switch (Math.floorMod(midi, 12)) {
            case 0, 1 -> 1;
            case 2, 3 -> 2;
            case 4 -> 3;
            case 5, 6 -> 4;
            case 7, 8 -> 5;
            case 9, 10 -> 6;
            default -> 7;
        };
    }

    private static int octaveOf(int midi) {
        return Math.max(0, Math.min(2, midi / 12 - 5));
    }

    private void drawHint(class_332 ctx) {
        String text = class_1074.method_4662((String)"gui.pianofx.hint_play", (Object[])new Object[]{Math.round(PianoCamera.totalDistance())});
        ctx.method_51433(this.field_22793, this.field_22793.method_27523(text, this.panelW - 16), this.px + 8, this.hintY, -1332763708, true);
    }

    private void drawTransport(class_332 ctx, int mouseX, int mouseY) {
        Score score = ScorePlayer.currentScore();
        double dur = score == null ? 0.0 : score.durationSec();
        double cur = score == null ? 0.0 : ScorePlayer.timeSec();
        float k = dur <= 0.0 ? 0.0f : (float)Math.max(0.0, Math.min(1.0, cur / dur));
        ctx.method_25294(this.progX, this.progY, this.progX + this.progW, this.progY + this.progH, 0x44FFFFFF);
        if (k > 0.0f) {
            ctx.method_25294(this.progX, this.progY, this.progX + Math.max(1, (int)((float)this.progW * k)), this.progY + this.progH, -993624);
        }
        this.drawChip(ctx, this.prevBtn, "\u23ee", PianoScreen.hover(this.prevBtn, mouseX, mouseY));
        this.drawChip(ctx, this.playBtn, ScorePlayer.paused() || !ScorePlayer.playing() ? "\u25b6" : "\u275a\u275a", PianoScreen.hover(this.playBtn, mouseX, mouseY));
        this.drawChip(ctx, this.nextBtn, "\u23ed", PianoScreen.hover(this.nextBtn, mouseX, mouseY));
        int x = this.nextBtn.x + this.nextBtn.w + 10;
        int ty = this.nextBtn.y + (this.nextBtn.h - 8) / 2;
        String hint = class_1074.method_4662((String)"gui.pianofx.hint_demo", (Object[])new Object[]{Math.round(PianoCamera.totalDistance())});
        ctx.method_51433(this.field_22793, this.field_22793.method_27523(hint, Math.max(20, this.px + this.panelW - 8 - x)), x, ty, -1332763708, true);
    }

    private void drawMiniTransport(class_332 ctx) {
        Score score = ScorePlayer.currentScore();
        double dur = score == null ? 0.0 : score.durationSec();
        double cur = score == null ? 0.0 : ScorePlayer.timeSec();
        float k = dur <= 0.0 ? 0.0f : (float)Math.max(0.0, Math.min(1.0, cur / dur));
        ctx.method_25294(this.progX, this.progY, this.progX + this.progW, this.progY + this.progH, 0x44FFFFFF);
        if (k > 0.0f) {
            ctx.method_25294(this.progX, this.progY, this.progX + Math.max(1, (int)((float)this.progW * k)), this.progY + this.progH, -993624);
        }
    }

    private void drawRestoreTab(class_332 ctx, int mouseX, int mouseY) {
        if (this.restoreTab == null) {
            return;
        }
        boolean hov = PianoScreen.hover(this.restoreTab, mouseX, mouseY);
        int cx = this.restoreTab.x + this.restoreTab.w / 2;
        int cy = this.restoreTab.y + this.restoreTab.h / 2;
        int r = hov ? 7 : 6;
        int a = hov ? 240 : 120;
        this.shapeSpan(ctx, cx, cy, r, 0, a << 24 | 0xE8C07A);
        this.shapeSpan(ctx, cx, cy, r - 1, 0, (int)((float)a * 0.9f) << 24 | 0xA1226);
        int tw = this.field_22793.method_1727("\u266a");
        ctx.method_51433(this.field_22793, "\u266a", cx - tw / 2, cy - 4, hov ? -3380 : -1326923654, false);
    }

    private void drawChip(class_332 ctx, Rect r, String label, boolean hover) {
        if (r == null) {
            return;
        }
        int bg = hover ? 1428040776 : 571611950;
        this.roundRect(ctx, r.x, r.y, r.w, r.h, bg);
        int edge = hover ? -856631640 : 1441846952;
        this.roundRectOutline(ctx, r.x, r.y, r.w, r.h, edge);
        int tw = this.field_22793.method_1727(label);
        ctx.method_51433(this.field_22793, label, r.x + (r.w - tw) / 2, r.y + (r.h - 8) / 2, hover ? -3380 : -1379585, false);
    }

    private void drawGlyphButton(class_332 ctx, Rect r, String glyph, boolean hover) {
        if (r == null) {
            return;
        }
        int cx = r.x + r.w / 2;
        int cy = r.y + r.h / 2;
        int r0 = Math.min(r.w, r.h) / 2;
        if (hover) {
            this.shapeSpan(ctx, cx, cy, r0 + 1, 0, 1428040776);
        }
        int tw = this.field_22793.method_1727(glyph);
        ctx.method_51433(this.field_22793, glyph, cx - tw / 2, cy - 4, hover ? -3380 : -522787594, false);
    }

    private static int mix(int a, int b, float t) {
        t = Math.max(0.0f, Math.min(1.0f, t));
        int aa = (int)((float)(a >>> 24 & 0xFF) + (float)((b >>> 24 & 0xFF) - (a >>> 24 & 0xFF)) * t);
        int rr = (int)((float)(a >> 16 & 0xFF) + (float)((b >> 16 & 0xFF) - (a >> 16 & 0xFF)) * t);
        int gg = (int)((float)(a >> 8 & 0xFF) + (float)((b >> 8 & 0xFF) - (a >> 8 & 0xFF)) * t);
        int bb = (int)((float)(a & 0xFF) + (float)((b & 0xFF) - (a & 0xFF)) * t);
        return aa << 24 | rr << 16 | gg << 8 | bb;
    }

    private void roundRect(class_332 ctx, int x, int y, int w, int h, int color) {
        if (w <= 0 || h <= 0) {
            return;
        }
        ctx.method_25294(x + 1, y, x + w - 1, y + h, color);
        ctx.method_25294(x, y + 1, x + w, y + h - 1, color);
    }

    private void roundRectOutline(class_332 ctx, int x, int y, int w, int h, int color) {
        ctx.method_25294(x + 1, y, x + w - 1, y + 1, color);
        ctx.method_25294(x + 1, y + h - 1, x + w - 1, y + h, color);
        ctx.method_25294(x, y + 1, x + 1, y + h - 1, color);
        ctx.method_25294(x + w - 1, y + 1, x + w, y + h - 1, color);
    }

    private float keyGlow(int semi, long now) {
        if (this.held.contains(semi)) {
            return 1.0f;
        }
        long at = this.flashAt[semi];
        if (at <= 0L) {
            return 0.0f;
        }
        double k = (double)(now - at) / 320.0;
        if (k >= 1.0) {
            return 0.0f;
        }
        if (k < 0.0) {
            return 1.0f;
        }
        double t = Math.max(0.0, (k - 0.3) / 0.7);
        double v = 1.0 - t * t * (3.0 - 2.0 * t);
        return (float)Math.max(0.0, v);
    }

    public boolean method_25401(double mouseX, double mouseY, double horizontal, double vertical) {
        if (vertical != 0.0) {
            PianoCamera.adjustDistance(vertical > 0.0 ? 2.0f : -2.0f);
            return true;
        }
        return super.method_25401(mouseX, mouseY, horizontal, vertical);
    }

    public boolean method_25403(double mx, double my, int button, double dx, double dy) {
        if (dx != 0.0 || dy != 0.0) {
            PianoCamera.dragLook(this.field_22787, dx, dy);
            return true;
        }
        return super.method_25403(mx, my, button, dx, dy);
    }

    public boolean method_25402(double mx, double my, int button) {
        if(button==0&&mx>=this.field_22789-104&&mx<=this.field_22789-8&&my>=8&&my<=27){this.field_22787.method_1507(new CustomPianoScreen(this));return true;}
        if (button != 0) {
            return super.method_25402(mx, my, button);
        }
        if (layout == Layout.HIDDEN) {
            if (PianoScreen.contains(this.restoreTab, mx, my)) {
                this.setLayoutAndRefresh(Layout.FULL);
            }
            return true;
        }
        if (PianoScreen.contains(this.expandBtn, mx, my)) {
            this.setLayoutAndRefresh(Layout.FULL);
            return true;
        }
        if (PianoScreen.contains(this.hideBtn, mx, my)) {
            this.setLayoutAndRefresh(Layout.HIDDEN);
            return true;
        }
        if (PianoScreen.contains(this.minBtn, mx, my)) {
            this.setLayoutAndRefresh(Layout.MINI);
            return true;
        }
        if (PianoScreen.contains(this.modeBtn, mx, my)) {
            this.setMode(this.mode == Mode.PLAY ? Mode.DEMO : Mode.PLAY);
            return true;
        }
        if (PianoScreen.contains(this.songsBtn, mx, my)) {
            this.field_22787.method_1507((class_437)new SongScreen(this));
            return true;
        }
        if (PianoScreen.contains(this.leaveBtn, mx, my)) {
            this.leave();
            return true;
        }
        if (this.mode == Mode.DEMO) {
            if (PianoScreen.contains(this.prevBtn, mx, my)) {
                this.cycleSong(-1);
                return true;
            }
            if (PianoScreen.contains(this.playBtn, mx, my)) {
                ScorePlayer.toggle();
                return true;
            }
            if (PianoScreen.contains(this.nextBtn, mx, my)) {
                this.cycleSong(1);
                return true;
            }
            return super.method_25402(mx, my, button);
        }
        int midi = this.nodeAt(mx, my);
        if (midi > 0) {
            this.strike(midi);
            this.flashAt[Math.floorMod((int)(midi - this.baseMidi), (int)36)] = System.currentTimeMillis();
            return true;
        }
        midi = this.accidentalAt(mx, my);
        if (midi > 0) {
            this.strike(midi);
            this.flashAt[Math.floorMod((int)(midi - this.baseMidi), (int)36)] = System.currentTimeMillis();
            return true;
        }
        return super.method_25402(mx, my, button);
    }

    private int nodeAt(double mx, double my) {
        if (mx < (double)this.gridX || mx > (double)(this.gridX + this.gridW) || my < (double)this.gridY || my > (double)(this.gridY + this.gridH)) {
            return -1;
        }
        int step = this.node + this.gap;
        int col = (int)((mx - (double)this.gridX) / (double)step);
        int row = (int)((my - (double)this.gridY) / (double)step);
        if (col < 0 || col >= 5 || row < 0 || row >= 3) {
            return -1;
        }
        int nx = this.gridX + col * step;
        int ny = this.gridY + row * step;
        if (mx < (double)nx || mx > (double)(nx + this.node) || my < (double)ny || my > (double)(ny + this.node)) {
            return -1;
        }
        return this.baseMidi + WHITE_SEMI[row * 5 + col];
    }

    private int accidentalAt(double mx, double my) {
        int cy = this.accY + this.accSize / 2;
        if (my < (double)(cy - this.accSize / 2) || my > (double)(cy + this.accSize / 2)) {
            return -1;
        }
        double step = (double)this.gridW / (double)BLACK_SEMI.length;
        for (int i = 0; i < BLACK_SEMI.length; ++i) {
            int cx = (int)Math.round((double)this.gridX + step * ((double)i + 0.5));
            if (!(Math.abs(mx - (double)cx) <= (double)(this.accSize / 2 + 1))) continue;
            return this.baseMidi + BLACK_SEMI[i];
        }
        return -1;
    }

    public boolean method_25404(int keyCode, int scanCode, int modifiers) {
        if(keyCode==295){this.field_22787.method_1507(new CustomPianoScreen(this));return true;}
        int i;
        if (keyCode == 258) {
            this.setLayoutAndRefresh(switch (layout.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> Layout.MINI;
                case 1 -> Layout.HIDDEN;
                case 2 -> Layout.FULL;
            });
            return true;
        }
        if (keyCode == 256) {
            this.leave();
            return true;
        }
        if (keyCode == 91) {
            PianoCamera.adjustDistance(-2.0f);
            return true;
        }
        if (keyCode == 93) {
            PianoCamera.adjustDistance(2.0f);
            return true;
        }
        if (this.mode == Mode.DEMO) {
            if (keyCode == 32) {
                ScorePlayer.toggle();
                return true;
            }
            if (keyCode == 263) {
                this.cycleSong(-1);
                return true;
            }
            if (keyCode == 262) {
                this.cycleSong(1);
                return true;
            }
            return super.method_25404(keyCode, scanCode, modifiers);
        }
        if (keyCode == 263) {
            this.shiftOctave(-12);
            return true;
        }
        if (keyCode == 262) {
            this.shiftOctave(12);
            return true;
        }
        for (i = 0; i < WHITE_CODES.length; ++i) {
            if (keyCode != WHITE_CODES[i]) continue;
            this.press(WHITE_SEMI[i]);
            return true;
        }
        for (i = 0; i < BLACK_CODES.length; ++i) {
            if (keyCode != BLACK_CODES[i]) continue;
            this.press(BLACK_SEMI[i]);
            return true;
        }
        return super.method_25404(keyCode, scanCode, modifiers);
    }

    public boolean method_16803(int keyCode, int scanCode, int modifiers) {
        int i;
        for (i = 0; i < WHITE_CODES.length; ++i) {
            if (keyCode != WHITE_CODES[i]) continue;
            this.release(WHITE_SEMI[i]);
        }
        for (i = 0; i < BLACK_CODES.length; ++i) {
            if (keyCode != BLACK_CODES[i]) continue;
            this.release(BLACK_SEMI[i]);
        }
        return super.method_16803(keyCode, scanCode, modifiers);
    }

    private void setLayoutAndRefresh(Layout next) {
        layout = next;
        if (this.field_22787 != null) {
            this.method_25423(this.field_22787, this.field_22789, this.field_22790);
        }
    }

    private void setMode(Mode next) {
        if (next == this.mode) {
            return;
        }
        this.mode = next;
        this.held.clear();
        Arrays.fill(this.flashAt, 0L);
        if (this.mode == Mode.PLAY) {
            ScorePlayer.pause();
            ScorePlayer.useManualStyle();
        } else if (ScorePlayer.paused()) {
            ScorePlayer.resume();
        } else if (!ScorePlayer.playing()) {
            ScorePlayer.start();
        }
        this.method_25426();
    }

    private void press(int semi) {
        if (!this.held.add(semi)) {
            return;
        }
        this.flashAt[semi] = System.currentTimeMillis();
        this.strike(this.baseMidi + semi);
    }

    private void release(int semi) {
        this.held.remove(semi);
        this.flashAt[semi] = System.currentTimeMillis();
    }

    private void strike(int midi) {
        ScorePlayer.useManualStyle();
        FxManager.resetEnding();
        FxManager.playNote(ScorePlayer.anchor(), ScorePlayer.facing(), new NoteEvent(0.0, midi, 100, 0.6));
    }

    private void cycleSong(int dir) {
        List<String> names = ScorePlayer.names();
        if (names.isEmpty()) {
            return;
        }
        int idx = names.indexOf(ScorePlayer.selectedName());
        idx = Math.floorMod(idx + dir, names.size());
        ScorePlayer.select(names.get(idx));
        ScorePlayer.start();
    }

    private void shiftOctave(int delta) {
        int next = this.baseMidi + delta;
        if (next >= 24 && next <= 84) {
            this.baseMidi = next;
        }
    }

    private void leave() {
        ScorePlayer.pause();
        PianoCamera.resetFov();
        this.method_25419();
    }

    private static String fmtTime(double sec) {
        int s = (int)Math.max(0L, Math.round(sec));
        return String.format("%d:%02d", s / 60, s % 60);
    }

    private static boolean inRect(double mx, double my, int x, int y, int w, int h) {
        return mx >= (double)x && mx <= (double)(x + w) && my >= (double)y && my <= (double)(y + h);
    }

    private static boolean contains(Rect r, double mx, double my) {
        return r != null && PianoScreen.inRect(mx, my, r.x, r.y, r.w, r.h);
    }

    private static boolean hover(Rect r, double mx, double my) {
        return PianoScreen.contains(r, mx, my);
    }

    public void method_25432() {
        this.held.clear();
        super.method_25432();
    }

    public static enum Mode {
        DEMO,
        PLAY;

    }

    public static enum Layout {
        FULL,
        MINI,
        HIDDEN;

    }

    private record Rect(int x, int y, int w, int h) {
    }
}

