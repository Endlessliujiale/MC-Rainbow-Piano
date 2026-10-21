/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1044
 *  net.minecraft.class_1058
 *  net.minecraft.class_1059
 *  net.minecraft.class_1060
 *  net.minecraft.class_2400
 *  net.minecraft.class_243
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_3999
 *  net.minecraft.class_4002
 *  net.minecraft.class_4003
 *  net.minecraft.class_4184
 *  net.minecraft.class_4588
 *  net.minecraft.class_638
 *  net.minecraft.class_703
 *  net.minecraft.class_707
 */
package com.pianofx.client.particle;

import com.pianofx.PianoFX;
import com.pianofx.client.ShaderState;
import com.pianofx.client.particle.AdditiveSheet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.class_1044;
import net.minecraft.class_1058;
import net.minecraft.class_1059;
import net.minecraft.class_1060;
import net.minecraft.class_2400;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3999;
import net.minecraft.class_4002;
import net.minecraft.class_4003;
import net.minecraft.class_4184;
import net.minecraft.class_4588;
import net.minecraft.class_638;
import net.minecraft.class_703;
import net.minecraft.class_707;

public class SparkParticle
extends class_4003 {
    private static final float[][] PALETTE = new float[][]{{1.0f, 0.94f, 0.82f}, {1.0f, 1.0f, 1.0f}, {0.74f, 0.87f, 1.0f}, {1.0f, 0.78f, 0.86f}, {1.0f, 0.84f, 0.55f}, {0.92f, 0.96f, 1.0f}, {1.0f, 0.88f, 0.7f}, {0.2f, 0.17f, 0.13f}, {0.13f, 0.16f, 0.22f}, {0.55f, 0.52f, 0.46f}, {0.115f, 0.1f, 0.072f}, {0.125f, 0.13f, 0.148f}};
    private static int DBG;
    private static final float S_R = 1.0f;
    private static final float S_G = 0.985f;
    private static final float S_B = 0.955f;
    private static final float G_R = 1.0f;
    private static final float G_G = 0.8f;
    private static final float G_B = 0.48f;
    private static final float A_R = 0.84f;
    private static final float A_G = 0.56f;
    private static final float A_B = 0.28f;
    private float streakLen;
    private float skDx;
    private float skDy;
    private float skDz;
    private boolean sunset;
    private float tint = 1.0f;
    private float brightness = 1.0f;
    private boolean steady;
    private static float STEADY_FADE;
    private boolean clampNear;
    private static final float NEAR_CALM = 9.0f;
    private float nearCalm = 9.0f;
    private float nearFloor = 0.16f;
    private static final Set<class_2960> LOGGED;
    public static final int SLOT_SPARK = 0;
    public static final int SLOT_GLOW = 1;
    public static final int SLOT_STAR = 2;
    public static final int SLOT_NOTE = 3;
    private static final class_4002[] PROVIDERS;

    public static void setSteadyFade(float v) {
        STEADY_FADE = Math.max(0.0f, Math.min(1.0f, v));
    }

    public void setClampNear(boolean on) {
        this.clampNear = on;
    }

    public void setNearFalloff(float calm, float floor) {
        this.nearCalm = Math.max(1.0f, calm);
        this.nearFloor = Math.max(0.05f, floor);
    }

    public void setScale(float s) {
        if (s > 1.0E-4f) {
            this.field_17867 = s;
        }
    }

    public void setBrightness(float b) {
        this.brightness = Math.max(0.0f, Math.min(1.0f, b));
    }

    public void paint(float r, float g, float b) {
        this.method_3084(r, g, b);
    }

    public void setSteady(boolean on) {
        this.steady = on;
    }

    public SparkParticle setStreak(double dx, double dy, double dz, float len) {
        double l = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (l < 1.0E-6 || len <= 0.0f) {
            return this;
        }
        this.skDx = (float)(dx / l);
        this.skDy = (float)(dy / l);
        this.skDz = (float)(dz / l);
        this.streakLen = len;
        float f = this.tint = ShaderState.active() ? 0.44f : 0.66f;
        if (this.sunset) {
            this.applySunsetColor();
        }
        return this;
    }

    public void setSunset(boolean on) {
        this.sunset = on;
        if (on) {
            this.applySunsetColor();
        }
    }

    protected SparkParticle(class_638 world, double x, double y, double z, class_4002 sprites, float scale, int life, int colorIndex, class_2960 spriteId) {
        super(world, x, y, z);
        class_1058 s;
        this.method_18140(sprites);
        if (spriteId != null && (s = SparkParticle.spriteOf(spriteId)) != null) {
            this.method_18141(s);
        }
        this.field_17867 = scale;
        this.field_3847 = Math.max(4, life);
        float[] c = PALETTE[Math.floorMod(colorIndex, PALETTE.length)];
        if (ShaderState.active()) {
            c = SparkParticle.litify(c);
        }
        this.method_3084(c[0], c[1], c[2]);
        this.field_3852 = 0.0;
        this.field_3869 = 0.0;
        this.field_3850 = 0.0;
        this.field_3844 = 0.0f;
        this.field_3862 = false;
    }

    private static class_1059 atlas() {
        try {
            class_1060 tm = class_310.method_1551().method_1531();
            class_1044 class_10442 = tm.method_4619(class_1059.field_17898);
            if (class_10442 instanceof class_1059) {
                class_1059 a = (class_1059)class_10442;
                return a;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    private static boolean missing(class_1058 s) {
        return s == null || s.method_45851().method_45816().method_12832().contains("missingno");
    }

    private static class_1058 spriteOf(class_2960 id) {
        class_1059 atlas = SparkParticle.atlas();
        if (atlas == null) {
            return null;
        }
        String path = id.method_12832();
        int slash = path.indexOf(47);
        ArrayList<class_2960> candidates = new ArrayList<class_2960>();
        candidates.add(id);
        if (slash >= 0) {
            candidates.add(class_2960.method_60655((String)id.method_12836(), (String)path.substring(slash + 1)));
        }
        candidates.add(class_2960.method_60655((String)id.method_12836(), (String)("textures/" + path)));
        for (class_2960 cand : candidates) {
            class_1058 s = atlas.method_4608(cand);
            if (SparkParticle.missing(s)) continue;
            if (LOGGED.add(id)) {
                PianoFX.LOGGER.info("[PianoFX] \u5b57\u5f62\u7cbe\u7075 {} -> {} (\u952e {})", new Object[]{id, s.method_45851().method_45816(), cand});
            }
            return s;
        }
        if (LOGGED.add(id)) {
            PianoFX.LOGGER.warn("[PianoFX] \u5b57\u5f62\u7cbe\u7075 {} \u5728\u56fe\u96c6\u91cc\u627e\u4e0d\u5230\uff0c\u9000\u56de\u9ed8\u8ba4\u5b57\u5f62", (Object)id);
        }
        return null;
    }

    public void method_3070() {
        super.method_3070();
        if (this.steady) {
            this.field_3841 = this.brightness * STEADY_FADE;
            return;
        }
        if (this.sunset) {
            this.applySunsetColor();
        } else {
            float t = (float)this.field_3866 / (float)Math.max(1, this.field_3847);
            this.field_3841 = t < 0.75f ? 1.0f : Math.max(0.0f, (1.0f - t) / 0.25f);
        }
        this.field_3841 *= this.brightness;
    }

    private void applySunsetColor() {
        float b;
        float g;
        float r;
        float t = Math.min(1.0f, (float)this.field_3866 / (float)Math.max(1, this.field_3847));
        float h = SparkParticle.smooth((t - 0.52f) / 0.4f);
        if (h < 0.55f) {
            k = h / 0.55f;
            r = SparkParticle.lerp(1.0f, 1.0f, k);
            g = SparkParticle.lerp(0.985f, 0.8f, k);
            b = SparkParticle.lerp(0.955f, 0.48f, k);
        } else {
            k = (h - 0.55f) / 0.45f;
            r = SparkParticle.lerp(1.0f, 0.84f, k);
            g = SparkParticle.lerp(0.8f, 0.56f, k);
            b = SparkParticle.lerp(0.48f, 0.28f, k);
        }
        float d = 1.0f - SparkParticle.smooth((t - 0.55f) / 0.45f);
        if (ShaderState.active()) {
            this.method_3084(r, g, b);
            this.field_3841 = d * this.tint;
        } else {
            float k = d * this.tint;
            this.method_3084(r * k, g * k, b * k);
            this.field_3841 = 1.0f;
        }
    }

    private static float lerp(float a, float b, float k) {
        return a + (b - a) * k;
    }

    private static float smooth(float x) {
        float y = class_3532.method_15363((float)x, (float)0.0f, (float)1.0f);
        return y * y * (3.0f - 2.0f * y);
    }

    public void method_3074(class_4588 vc, class_4184 camera, float tickDelta) {
        float ez;
        float ey;
        if (this.streakLen <= 1.0E-4f) {
            double dz0;
            double dy0;
            class_243 cp0;
            double dx0;
            double d0;
            if (this.clampNear && (d0 = Math.sqrt((dx0 = this.field_3874 - (cp0 = camera.method_19326()).method_10216()) * dx0 + (dy0 = this.field_3854 - cp0.method_10214()) * dy0 + (dz0 = this.field_3871 - cp0.method_10215()) * dz0)) < (double)this.nearCalm) {
                float savedScale = this.field_17867;
                float k = (float)Math.max((double)this.nearFloor, d0 / (double)this.nearCalm);
                this.field_17867 = savedScale * k;
                super.method_3074(vc, camera, tickDelta);
                this.field_17867 = savedScale;
                return;
            }
            super.method_3074(vc, camera, tickDelta);
            return;
        }
        class_243 cp = camera.method_19326();
        float px = (float)(class_3532.method_16436((double)tickDelta, (double)this.field_3858, (double)this.field_3874) - cp.method_10216());
        float py = (float)(class_3532.method_16436((double)tickDelta, (double)this.field_3838, (double)this.field_3854) - cp.method_10214());
        float pz = (float)(class_3532.method_16436((double)tickDelta, (double)this.field_3856, (double)this.field_3871) - cp.method_10215());
        float ex = this.skDy * pz - this.skDz * py;
        float el = class_3532.method_15355((float)(ex * ex + (ey = this.skDz * px - this.skDx * pz) * ey + (ez = this.skDx * py - this.skDy * px) * ez));
        if (el < 1.0E-4f) {
            super.method_3074(vc, camera, tickDelta);
            return;
        }
        if (DBG++ % 400 == 0) {
            PianoFX.LOGGER.info("[PianoFX] \u5149\u4e1d\u7ed8\u5236 len={} scale={} el={} alpha={}", new Object[]{String.format("%.2f", Float.valueOf(this.streakLen)), String.format("%.2f", Float.valueOf(this.field_17867)), String.format("%.2f", Float.valueOf(el)), String.format("%.2f", Float.valueOf(this.field_3841))});
        }
        float halfW = this.field_17867 * 0.5f / el;
        ex *= halfW;
        ey *= halfW;
        ez *= halfW;
        float distNow = class_3532.method_15355((float)(px * px + py * py + pz * pz));
        float nearShrink = class_3532.method_15363((float)((distNow - 1.0f) / 6.0f), (float)0.3f, (float)1.0f);
        if (nearShrink < 0.999f) {
            ex *= nearShrink;
            ey *= nearShrink;
            ez *= nearShrink;
        }
        float tx = px - this.skDx * this.streakLen;
        float ty = py - this.skDy * this.streakLen;
        float tz = pz - this.skDz * this.streakLen;
        int light = this.method_3068(tickDelta);
        float a = this.field_3841;
        float dist = class_3532.method_15355((float)(px * px + py * py + pz * pz));
        if ((a *= class_3532.method_15363((float)((dist - 1.2f) / 2.3f), (float)0.0f, (float)1.0f)) < 0.01f) {
            return;
        }
        this.quad(vc, light, a, px, py, pz, tx, ty, tz, ex, ey, ez, false);
        this.quad(vc, light, a, px, py, pz, tx, ty, tz, ex, ey, ez, true);
    }

    private void quad(class_4588 vc, int light, float a, float px, float py, float pz, float tx, float ty, float tz, float ex, float ey, float ez, boolean flip) {
        if (!flip) {
            vc.method_22912(px + ex, py + ey, pz + ez).method_22913(this.method_18134(), this.method_18135()).method_22915(this.field_3861, this.field_3842, this.field_3859, a).method_60803(light);
            vc.method_22912(px - ex, py - ey, pz - ez).method_22913(this.method_18134(), this.method_18136()).method_22915(this.field_3861, this.field_3842, this.field_3859, a).method_60803(light);
            vc.method_22912(tx - ex, ty - ey, tz - ez).method_22913(this.method_18133(), this.method_18136()).method_22915(this.field_3861, this.field_3842, this.field_3859, a).method_60803(light);
            vc.method_22912(tx + ex, ty + ey, tz + ez).method_22913(this.method_18133(), this.method_18135()).method_22915(this.field_3861, this.field_3842, this.field_3859, a).method_60803(light);
        } else {
            vc.method_22912(tx + ex, ty + ey, tz + ez).method_22913(this.method_18133(), this.method_18135()).method_22915(this.field_3861, this.field_3842, this.field_3859, a).method_60803(light);
            vc.method_22912(tx - ex, ty - ey, tz - ez).method_22913(this.method_18133(), this.method_18136()).method_22915(this.field_3861, this.field_3842, this.field_3859, a).method_60803(light);
            vc.method_22912(px - ex, py - ey, pz - ez).method_22913(this.method_18134(), this.method_18136()).method_22915(this.field_3861, this.field_3842, this.field_3859, a).method_60803(light);
            vc.method_22912(px + ex, py + ey, pz + ez).method_22913(this.method_18134(), this.method_18135()).method_22915(this.field_3861, this.field_3842, this.field_3859, a).method_60803(light);
        }
    }

    public int method_3068(float tint) {
        return 0xF000F0;
    }

    public class_3999 method_18122() {
        if (ShaderState.active()) {
            return class_3999.field_17829;
        }
        return AdditiveSheet.INSTANCE;
    }

    private static float[] litify(float[] c) {
        float m = Math.max(c[0], Math.max(c[1], c[2]));
        if (m >= 0.55f) {
            return c;
        }
        if (m < 1.0E-4f) {
            return new float[]{0.55f, 0.55f, 0.55f};
        }
        float k = 0.62f / m;
        return new float[]{Math.min(1.0f, c[0] * k), Math.min(1.0f, c[1] * k), Math.min(1.0f, c[2] * k)};
    }

    public static class_703 spawn(int slot, class_638 world, double x, double y, double z, float scale, int life, int colorIndex, class_2960 spriteId) {
        class_4002 sprites = PROVIDERS[Math.floorMod(slot, PROVIDERS.length)];
        if (sprites == null) {
            return null;
        }
        SparkParticle particle = new SparkParticle(world, x, y, z, sprites, scale, life, colorIndex, spriteId);
        class_310.method_1551().field_1713.method_3058((class_703)particle);
        return particle;
    }

    public static class_703 spawn(int slot, class_638 world, double x, double y, double z, float scale, int life, int colorIndex) {
        return SparkParticle.spawn(slot, world, x, y, z, scale, life, colorIndex, null);
    }

    static {
        STEADY_FADE = 1.0f;
        LOGGED = new HashSet<class_2960>();
        PROVIDERS = new class_4002[4];
    }

    public static class Factory
    implements class_707<class_2400> {
        private final class_4002 sprites;
        private final int slot;

        public Factory(class_4002 sprites, int slot) {
            this.sprites = sprites;
            this.slot = slot;
            SparkParticle.PROVIDERS[Math.floorMod((int)slot, (int)SparkParticle.PROVIDERS.length)] = sprites;
        }

        public Factory(class_4002 sprites) {
            this(sprites, 0);
        }

        public class_703 createParticle(class_2400 type, class_638 world, double x, double y, double z, double scale, double life, double colorIndex) {
            float s = scale > 0.0 ? (float)scale : 0.1f;
            int l = life > 0.0 ? (int)life : 60;
            int ci = (int)Math.round(colorIndex);
            return new SparkParticle(world, x, y, z, this.sprites, s, l, ci, null);
        }

        public int slot() {
            return this.slot;
        }
    }
}

