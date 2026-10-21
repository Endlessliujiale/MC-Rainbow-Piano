/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2350
 *  net.minecraft.class_2382
 *  net.minecraft.class_2394
 *  net.minecraft.class_2396
 *  net.minecraft.class_243
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_703
 */
package com.pianofx.client.fx;

import com.pianofx.ModParticles;
import com.pianofx.PianoFX;
import com.pianofx.block.PianoBlockEntity;
import com.pianofx.client.ScorePlayer;
import com.pianofx.client.ShaderState;
import com.pianofx.client.fx.Flyer;
import com.pianofx.client.fx.FxStyle;
import com.pianofx.client.fx.NoteSound;
import com.pianofx.client.fx.Rainbow;
import com.pianofx.client.fx.Ribbon;
import com.pianofx.client.particle.SparkParticle;
import com.pianofx.client.render.PianoKeys;
import com.pianofx.score.NoteEvent;
import com.pianofx.score.Score;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.class_2350;
import net.minecraft.class_2382;
import net.minecraft.class_2394;
import net.minecraft.class_2396;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_703;

public final class FxManager
implements PianoFX.FxHook {
    public static final FxManager INSTANCE = new FxManager();
    private static final int C_WARM = 0;
    private static final int C_WHITE = 1;
    private static final int C_CREAM = 6;
    private static final int C_FAINT_WARM = 10;
    private static final List<Flyer> FLYERS = new ArrayList<Flyer>();
    private static final Map<String, FxStyle> STYLES = new HashMap<String, FxStyle>();
    private static FxStyle currentStyle = FxStyle.defaultStyle();
    private static final int MAX_FLYERS = 400;
    private static final int MAX_FLYERS_ENDING = 620;
    private static final int DUST_PER_TICK = 3;
    private static final int DUST_LIFE = 200;
    private static final double DUST_NEAR = 4.0;
    private static final double DUST_FAR = 62.0;
    private static final float TWINKLE_CHANCE = 0.16f;
    private static final double ENDING_SEC = 9.0;
    private static final double ENDING_HOLD = 320.0;
    private static boolean ending;
    private static double endingTicks;
    private static boolean forcedEnding;
    private static final int GLOW_EVERY = 6;
    private static final int GLYPH_MAX_AGE = 200000;
    private static final float GLYPH_NEAR_CALM = 12.0f;
    private static final float GLYPH_NEAR_FLOOR = 0.3f;
    private static final float HALO_SCALE = 1.12f;
    private static final float HALO_SCALE_2 = 1.3f;
    private static final class_2960 HALO;
    private static final int C_HALO = 6;
    private static final Random DUST;
    private static final int T_SPARK = 0;
    private static final int T_GLOW = 1;
    private static final int T_STAR = 2;
    private static final class_2396<?>[] TYPES;
    private static int tickCounter;
    private static int lastLive;
    private static class_243 basisHome;
    private static class_243 basisAway;
    private static class_243 basisRight;
    private static int lastNoteTick;

    private FxManager() {
    }

    public static String particleDebug() {
        class_310 mc = class_310.method_1551();
        return mc == null || mc.field_1713 == null ? "?" : mc.field_1713.method_3052();
    }

    public static int activeCount() {
        return FLYERS.size();
    }

    public static FxStyle style() {
        return currentStyle;
    }

    public static FxStyle styleFor(Score score) {
        if (score == null) {
            return FxStyle.defaultStyle();
        }
        return STYLES.computeIfAbsent(score.name(), k -> FxStyle.of(score));
    }

    public static void useStyle(FxStyle style) {
        if (style != null) {
            currentStyle = style;
        }
    }

    @Override
    public void onTick(PianoBlockEntity be, double fromSec, double toSec) {
        FxStyle style;
        Score score = be.score();
        if (score == null) {
            if (tickCounter++ % 100 == 0) {
                PianoFX.LOGGER.warn("[PianoFX] \u64ad\u653e\u4e2d\u4f46\u5ba2\u6237\u7aef\u8bfb\u4e0d\u5230\u8c31\u5b50 (scoreName={})", (Object)be.scoreName());
            }
            return;
        }
        if (toSec <= fromSec) {
            return;
        }
        currentStyle = style = FxManager.styleFor(score);
        class_243 center = class_243.method_24953((class_2382)be.method_11016()).method_1031(0.0, 0.15, 0.0);
        class_2350 facing = be.facing();
        for (NoteEvent n : score.notes()) {
            if (!(n.timeSec() > fromSec - 1.0E-4) || !(n.timeSec() <= toSec)) continue;
            FxManager.spawnAt(center, facing, n, style);
            NoteSound.play(n);
        }
        FxManager.songProgress(score.durationSec() - toSec);
    }

    public static void songProgress(double remainSec) {
        if (remainSec > 10.0) {
            if (!forcedEnding) {
                FxManager.resetEnding();
            }
            return;
        }
        if (remainSec <= 0.0) {
            FxManager.beginEnding();
        }
    }

    public static void beginEnding() {
        if (ending) {
            return;
        }
        ending = true;
        endingTicks = 0.0;
        ScoreStaff.beginEnding();
        for(Flyer f:FLYERS)f.endSong();
PianoFX.LOGGER.info("[PianoFX] \u8fdb\u5165\u5c3e\u6bb5\uff1a\u505c\u6b62\u62d6\u5c3e\uff0c{} \u4e2a\u97f3\u7b26\u5f00\u59cb\u6563\u5f00", (Object)FLYERS.size());
    }

    public static void resetEnding() {
        if(ending&&basisHome!=null)clear();
        ending = false;
        endingTicks = 0.0;
        SparkParticle.setSteadyFade(1.0f);
    }

    public static boolean ending() {
        return ending;
    }

    public static void forceEnding(boolean on) {
        forcedEnding = on;
        if (on) {
            FxManager.beginEnding();
        } else {
            FxManager.resetEnding();
        }
    }

    public static void clearForcedEnding() {
        forcedEnding = false;
    }

    public static void playNote(class_243 center, class_2350 facing, NoteEvent note) {
        NoteSound.play(note);
        PianoKeys.press(note.midi());
        FxManager.spawnAt(center, facing, note, currentStyle);
    }

    public static void demoNote(class_243 center, class_2350 facing, NoteEvent n) {
        FxManager.playNote(center, facing, n);
    }

    private static void spawnAt(class_243 center, class_2350 facing, NoteEvent note, FxStyle style) {
        class_638 w = FxManager.world();
        if (w == null) {
            return;
        }
        lastNoteTick = tickCounter;
        if(ending){clear();resetEnding();}
        ScoreStaff.pulse(note.velocity());
        FxManager.ensureBasis(center, facing);
        long h = FxManager.hash((long)note.midi() * 73856093L ^ (long)(note.timeSec() * 1000.0));
        int count = 1;
        for (int i = 0; i < count; ++i) {
            long seed = h + (long)i * -7046029254386353131L;
            int arm = (int)Math.floorMod((h >>> 11) + (long)i * 3L, 3L);
            class_2960 glyph = style.glyphForMidi(note.midi());
            Flyer fl = new Flyer(basisHome, basisAway, arm, note.midi(), note.velocity(), seed, style, glyph, Math.max(600,2400-Math.max(0,FLYERS.size()-1100)*2));
            fl.glyph = FxManager.spawnGlyph(w, fl);
            FLYERS.add(fl);
            if (!ending) continue;
            fl.spread(1.0);
        }

    }

    private static void ensureBasis(class_243 center, class_2350 facing) {
        if (basisHome != null) {
            return;
        }
        double az = FxManager.awayFromPlayer(center, facing);
        basisHome = center;
        basisAway = new class_243(Math.cos(az), 0.0, Math.sin(az)).method_1029();
        basisRight = new class_243(-FxManager.basisAway.field_1350, 0.0, FxManager.basisAway.field_1352);
        Ribbon.reset();
        ScoreStaff.configure(basisHome,basisAway);
        PianoFX.LOGGER.info("[PianoFX] \u87ba\u65cb\u57fa\u51c6\u91cd\u8bbe az={}\u00b0", (Object)String.format("%.1f", Math.toDegrees(az)));
    }

    private static void clearBasis() {
        ScoreStaff.reset();
        basisHome = null;
        basisAway = null;
        basisRight = null;
        Ribbon.reset();
    }

    private static void releaseTopBatches() {
        for(int arm=0;arm<3;arm++) {
            int count=0;
            for(Flyer f:FLYERS)if(f.arm==arm&&f.waitingAtTop())count++;
            if(count>=Flyer.BATCH_SIZE)for(Flyer f:FLYERS)if(f.arm==arm&&f.waitingAtTop())f.release();
        }
    }
    private static double awayFromPlayer(class_243 center, class_2350 facing) {
        class_310 mc = class_310.method_1551();
        if (mc != null && mc.field_1724 != null) {
            class_243 d = center.method_1020(mc.field_1724.method_33571());
            double h = Math.hypot(d.field_1352, d.field_1350);
            if (h > 0.6) {
                return Math.atan2(d.field_1350, d.field_1352);
            }
        }
        return Math.atan2(-facing.method_10165(), -facing.method_10148());
    }

    private static void sparkle(class_638 w, class_243 p, int count, long seed) {
        Random r = new Random(seed ^ 0x9E3779B9L);
        for (int i = 0; i < count; ++i) {
            class_243 q = p.method_1031((r.nextDouble() - 0.5) * 1.4, (r.nextDouble() - 0.5) * 1.0, (r.nextDouble() - 0.5) * 1.4);
            FxManager.spark(w, q, 2, 1, 0.26f + r.nextFloat() * 0.2f, 22);
        }
    }

    public static void tick() {
        class_638 w = FxManager.world();
        if (w == null) {
            return;
        }
        if (tickCounter % 20 == 0) {
            ShaderState.refresh();
        }
        ++tickCounter;
        if (ending) {
            double q=Math.min(1,(endingTicks+=1)/240.0);
            SparkParticle.setSteadyFade((float)(1-q*q*(3-2*q)));
            if(basisHome!=null&&endingTicks<220&&(int)endingTicks%4==0){
                for(int arm=0;arm<3;arm++)for(int i=0;i<8;i++){
                    double u=Math.floorMod((int)endingTicks*13+i*97+arm*43,997)/997.0;
                    float[] c=Rainbow.hsb((float)(u*1.4+arm/3.0+0.02),0.72f,1);
                    ScoreStaff.fragment(w,Spiral.at(arm,u,basisHome,basisAway),c[0],c[1],c[2],tickCounter*37L+i+arm*100);
                }
            }
            if(endingTicks>=320&&basisHome!=null){
                clear();PianoFX.LOGGER.info("[PianoFX] End cleanup complete: notes=0 staff=0 ribbon=0");
            }
        }
        if (basisHome != null && !ending) {
            FxManager.dust(w, basisHome);
        }
        ScoreStaff.tick(w);
        releaseTopBatches();
        for (int i = FLYERS.size() - 1; i >= 0; --i) {
            boolean gone;
            Flyer f = FLYERS.get(i);
            for (int s = 0; s < 1; ++s) {
                f.step(1.0);
            }
            if (ending) {
                f.spread(1.0);
            }
            int tick = (int)f.age;
            FxManager.moveGlyph(w, f);
            if(f.dissolving())ScoreStaff.dissolve(w,f);
            if (!f.fading && tick % 14 == 0 && f.rng.nextFloat() < 0.16f) {
                FxManager.twinkle(w, f);
            }
            boolean bl = gone = f.dead();
            if (!gone) continue;
            if (f.glyph != null) {
                f.glyph.method_3085();
            }
            if (f.halo != null) {
                f.halo.method_3085();
            }
            if (f.halo2 != null) {
                f.halo2.method_3085();
            }
            ScoreStaff.remove(f);
            FLYERS.remove(i);
        }
        lastLive = FLYERS.size();
        if (tickCounter % 100 == 0) {
            int top=0,transfer=0,orbit=0,spiral=0;
            for(Flyer f:FLYERS){if(f.ascending())spiral++;else if(f.waitingAtTop())top++;else if(f.transferring())transfer++;else if(f.orbiting())orbit++;}
            PianoFX.LOGGER.info("[PianoFX] notes={} spiral={} top={} transfer={} orbit={} particles={} score={} replaced={} debris={}", new Object[]{FLYERS.size(),spiral,top,transfer,orbit,particleDebug(),ScoreStaff.count(),ScoreStaff.replacements(),ScoreStaff.dissolveBursts()});
        }
        if (basisHome != null && !ending) {
            boolean active = !ending && (!FLYERS.isEmpty() || tickCounter - lastNoteTick < 60);
            double speed = Math.max(0.55, Math.min(2.2, currentStyle.speed()));
            Ribbon.tick(w, basisHome, basisAway, active, 0.22 / speed * 2.0);
        }
    }

    private static void dust(class_638 w, class_243 home) {
        Random r = DUST;
        for (int i = 0; i < 3; ++i) {
            int life;
            double u = r.nextDouble() * 2.0 - 1.0;
            double phi = r.nextDouble() * Math.PI * 2.0;
            double s = Math.sqrt(Math.max(0.0, 1.0 - u * u));
            double rad = 4.0 + 58.0 * Math.cbrt(r.nextDouble());
            class_243 p = home.method_1031(s * Math.cos(phi) * rad, u * rad * 0.75, s * Math.sin(phi) * rad);
            float scale = 0.018f + r.nextFloat() * 0.026f;
            class_703 q = SparkParticle.spawn(0, w, p.field_1352, p.field_1351, p.field_1350, scale, life = (int)(200.0 * (0.6 + r.nextDouble() * 0.8)), r.nextInt(3) == 0 ? 6 : 1);
            if (!(q instanceof SparkParticle)) continue;
            SparkParticle sp = (SparkParticle)q;
            sp.setBrightness(0.3f + r.nextFloat() * 0.45f);
        }
    }

    private static void moveGlyph(class_638 w, Flyer f) {
        if((int)f.age%80==0||f.glyphDirty){
            f.glyphDirty=false;
            if(f.glyph!=null)f.glyph.method_3085();
            if(f.halo!=null)f.halo.method_3085();
            if(f.halo2!=null)f.halo2.method_3085();
            f.glyph=null;f.halo=null;f.halo2=null;
        }
        // Shed only optional halo layers under pressure; never remove the note itself.
        if(FLYERS.size()>700&&f.halo2!=null){f.halo2.method_3085();f.halo2=null;}
        if(FLYERS.size()>1200&&f.halo!=null){f.halo.method_3085();f.halo=null;}
        SparkParticle hs;
        class_703 class_7032;
        class_703 g = f.glyph;
        if (g == null || !g.method_3086()) {
            f.glyph = FxManager.spawnGlyph(w, f);
            return;
        }
        float s = f.glyphScaleNow();
        g.method_3063(f.pos.field_1352, f.pos.field_1351, f.pos.field_1350);
        if (g instanceof SparkParticle) {
            SparkParticle sp = (SparkParticle)g;
            sp.setScale(s);
            sp.setBrightness(0.86f*f.opacity());
        }
        if (f.halo != null && f.halo.method_3086()) {
            f.halo.method_3063(f.pos.field_1352, f.pos.field_1351, f.pos.field_1350);
            class_7032 = f.halo;
            if (class_7032 instanceof SparkParticle) {
                hs = (SparkParticle)class_7032;
                hs.setScale(s * 1.12f);
                hs.setBrightness((ShaderState.active()?0.24f:0.528f)*f.opacity());
            }
        } else {
            if(FLYERS.size()<=1200)f.halo = FxManager.spawnHalo(w, f, s, 1.12f, 0.24f);
        }
        if (f.halo2 != null && f.halo2.method_3086()) {
            f.halo2.method_3063(f.pos.field_1352, f.pos.field_1351, f.pos.field_1350);
            class_7032 = f.halo2;
            if (class_7032 instanceof SparkParticle) {
                hs = (SparkParticle)class_7032;
                hs.setScale(s * 1.3f);
                hs.setBrightness((ShaderState.active()?0.08f:0.176f)*f.opacity());
            }
        } else {
            if(FLYERS.size()<=700)f.halo2 = FxManager.spawnHalo(w, f, s, 1.3f, 0.08f);
        }
    }

    private static float[] noteColor(int midi) {
        float hue = (float)Math.floorMod(midi, 12) / 12.0f;
        return Rainbow.hsb(hue, 0.78f, 1.0f);
    }

    private static class_703 spawnGlyph(class_638 w, Flyer f) {
        class_703 p = SparkParticle.spawn(3, w, f.pos.field_1352, f.pos.field_1351, f.pos.field_1350, f.glyphScaleNow(), 200000, f.glyphColor, f.renderGlyph());
        if (p instanceof SparkParticle) {
            SparkParticle sp = (SparkParticle)p;
            sp.setClampNear(true);
            sp.setNearFalloff(12.0f, 0.3f);
            float[] c = FxManager.noteColor(f.midi);
            sp.paint(c[0], c[1], c[2]);
            sp.setBrightness(0.86f*f.opacity());
        }
        return p;
    }

    private static class_703 spawnHalo(class_638 w, Flyer f, float scale, float mult, float alpha) {
        class_703 p = SparkParticle.spawn(1, w, f.pos.field_1352, f.pos.field_1351, f.pos.field_1350, scale * mult, 200000, 6, f.renderGlyph());
        if (p instanceof SparkParticle) {
            SparkParticle sp = (SparkParticle)p;
            float[] hc = FxManager.noteColor(f.midi);
            sp.paint(hc[0] + (1.0f - hc[0]) * 0.35f, hc[1] + (1.0f - hc[1]) * 0.35f, hc[2] + (1.0f - hc[2]) * 0.35f);
            sp.setClampNear(true);
            sp.setNearFalloff(12.0f, 0.3f);
            sp.setBrightness((ShaderState.active() ? alpha : Math.min(0.85f, alpha * 2.2f))*f.opacity());
        }
        return p;
    }

    private static void twinkle(class_638 w, Flyer f) {
        Random r = f.rng;
        class_243 q = f.pos.method_1031((r.nextDouble() - 0.5) * 0.8, (r.nextDouble() - 0.5) * 0.8, (r.nextDouble() - 0.5) * 0.8);
        FxManager.spark(w, q, 2, r.nextDouble() < 0.4 ? 0 : 6, 0.16f + r.nextFloat() * 0.12f, 20);
    }

    private static void spark(class_638 w, class_243 p, int type, int color, float scale, int life) {
        w.method_8406((class_2394)TYPES[type], p.field_1352, p.field_1351, p.field_1350, (double)scale, (double)life, (double)color);
    }

    public static void clear() {
        for (Flyer f : FLYERS) {
            if (f.glyph != null) {
                f.glyph.method_3085();
            }
            if (f.halo != null) {
                f.halo.method_3085();
            }
            if (f.halo2 == null) continue;
            f.halo2.method_3085();
        }
        FLYERS.clear();
        FxManager.clearBasis();
    }

    public static int lastLive() {
        return lastLive;
    }

    private static boolean glowLayers() {
        return !ShaderState.active();
    }

    private static class_638 world() {
        class_310 mc = class_310.method_1551();
        return mc == null ? null : mc.field_1687;
    }

    private static long hash(long x) {
        x ^= x >>> 33;
        x *= -49064778989728563L;
        x ^= x >>> 33;
        x *= -4265267296055464877L;
        x ^= x >>> 33;
        return x;
    }

    static {
        HALO = class_2960.method_60655((String)"pianofx", (String)"particle/halo");
        DUST = new Random(1592597733L);
        TYPES = new class_2396[]{ModParticles.SPARK, ModParticles.GLOW, ModParticles.STAR, ModParticles.NOTE};
        tickCounter = 0;
        lastLive = 0;
        lastNoteTick = -10000;
    }
}

