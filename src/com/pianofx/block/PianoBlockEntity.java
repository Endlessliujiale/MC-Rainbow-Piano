/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_2487
 *  net.minecraft.class_2586
 *  net.minecraft.class_2596
 *  net.minecraft.class_2602
 *  net.minecraft.class_2622
 *  net.minecraft.class_2680
 *  net.minecraft.class_2769
 *  net.minecraft.class_3218
 *  net.minecraft.class_7225$class_7874
 */
package com.pianofx.block;

import com.pianofx.ModRegistry;
import com.pianofx.PianoFX;
import com.pianofx.block.PianoBlock;
import com.pianofx.score.Score;
import com.pianofx.score.ScoreLibrary;
import java.util.List;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2487;
import net.minecraft.class_2586;
import net.minecraft.class_2596;
import net.minecraft.class_2602;
import net.minecraft.class_2622;
import net.minecraft.class_2680;
import net.minecraft.class_2769;
import net.minecraft.class_3218;
import net.minecraft.class_7225;

public class PianoBlockEntity
extends class_2586 {
    private boolean playing;
    private long startTick;
    private double pausedSec;
    private String scoreName = "";
    private double lastClientSec;

    public PianoBlockEntity(class_2338 pos, class_2680 state) {
        super(ModRegistry.PIANO_BE, pos, state);
    }

    public Score score() {
        String first;
        Score s = ScoreLibrary.get(this.scoreName);
        if (s == null && (first = ScoreLibrary.first()) != null) {
            this.scoreName = first;
            s = ScoreLibrary.get(first);
        }
        return s;
    }

    public double playheadSec() {
        if (this.field_11863 == null) {
            return this.pausedSec;
        }
        if (!this.playing) {
            return this.pausedSec;
        }
        return this.pausedSec + (double)(this.field_11863.method_8510() - this.startTick) / 20.0;
    }

    public boolean toggle() {
        if (this.playing) {
            this.pausedSec = this.playheadSec();
            this.playing = false;
        } else {
            if (this.score() == null) {
                this.cycleScore();
            }
            if (this.score() == null) {
                return false;
            }
            if (this.pausedSec > this.score().durationSec()) {
                this.pausedSec = 0.0;
            }
            this.lastClientSec = this.pausedSec;
            this.startTick = this.field_11863.method_8510();
            this.playing = true;
        }
        this.sync();
        PianoFX.LOGGER.info("[PianoFX] toggle side={} playing={} t={}s score={}", new Object[]{this.field_11863.field_9236 ? "client" : "server", this.playing, String.format("%.2f", this.playheadSec()), this.scoreName()});
        return this.playing;
    }

    public String cycleScore() {
        List<String> all = ScoreLibrary.names();
        if (all.isEmpty()) {
            ScoreLibrary.reload();
            all = ScoreLibrary.names();
        }
        if (all.isEmpty()) {
            return "(\u65e0\u8c31\u5b50)";
        }
        int idx = all.indexOf(this.scoreName);
        this.scoreName = all.get((idx + 1) % all.size());
        this.pausedSec = 0.0;
        this.startTick = this.field_11863 != null ? this.field_11863.method_8510() : 0L;
        this.playing = false;
        this.sync();
        return this.scoreName;
    }

    public String scoreName() {
        String f;
        if ((this.scoreName == null || this.scoreName.isEmpty()) && (f = ScoreLibrary.first()) != null) {
            this.scoreName = f;
        }
        return this.scoreName;
    }

    public boolean isPlaying() {
        return this.playing;
    }

    public void forceStart() {
        if (this.field_11863 == null) {
            return;
        }
        this.pausedSec = 0.0;
        this.lastClientSec = 0.0;
        this.startTick = this.field_11863.method_8510();
        this.playing = true;
    }

    public void tick() {
        Score s;
        if (this.field_11863 == null) {
            return;
        }
        double cur = this.playheadSec();
        if (this.field_11863.field_9236 && PianoFX.fxHook != null && this.playing) {
            PianoFX.fxHook.onTick(this, this.lastClientSec, cur);
        }
        this.lastClientSec = cur;
        if (this.playing && (s = this.score()) != null && cur > s.durationSec() + 0.5) {
            this.playing = false;
            this.pausedSec = 0.0;
            this.sync();
        }
    }

    private void sync() {
        this.method_5431();
        if (this.field_11863 != null) {
            class_1937 class_19372;
            class_2680 st = this.method_11010();
            this.field_11863.method_8413(this.field_11867, st, st, 3);
            if (!this.field_11863.field_9236 && (class_19372 = this.field_11863) instanceof class_3218) {
                class_3218 sw = (class_3218)class_19372;
                sw.method_14178().method_14128(this.field_11867);
            }
        }
    }

    protected void method_11007(class_2487 nbt, class_7225.class_7874 registries) {
        super.method_11007(nbt, registries);
        nbt.method_10556("playing", this.playing);
        nbt.method_10544("start", this.startTick);
        nbt.method_10549("paused", this.pausedSec);
        nbt.method_10582("score", this.scoreName == null ? "" : this.scoreName);
    }

    protected void method_11014(class_2487 nbt, class_7225.class_7874 registries) {
        super.method_11014(nbt, registries);
        this.playing = nbt.method_10577("playing");
        this.startTick = nbt.method_10537("start");
        this.pausedSec = nbt.method_10574("paused");
        this.scoreName = nbt.method_10558("score");
    }

    public class_2487 method_16887(class_7225.class_7874 registries) {
        return this.method_38244(registries);
    }

    public class_2596<class_2602> method_38235() {
        return class_2622.method_38585((class_2586)this);
    }

    public class_2350 facing() {
        class_2680 st = this.method_11010();
        return st.method_28498((class_2769)PianoBlock.field_11177) ? (class_2350)st.method_11654((class_2769)PianoBlock.field_11177) : class_2350.field_11043;
    }
}

