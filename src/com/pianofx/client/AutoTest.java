/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1132
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2350
 *  net.minecraft.class_2382
 *  net.minecraft.class_243
 *  net.minecraft.class_2586
 *  net.minecraft.class_276
 *  net.minecraft.class_310
 *  net.minecraft.class_318
 *  net.minecraft.class_4063
 *  net.minecraft.class_4184
 *  net.minecraft.class_437
 *  net.minecraft.class_746
 */
package com.pianofx.client;

import com.pianofx.ModRegistry;
import com.pianofx.PianoFX;
import com.pianofx.block.PianoBlockEntity;
import com.pianofx.client.PianoCamera;
import com.pianofx.client.ScorePlayer;
import com.pianofx.client.fx.FxManager;
import com.pianofx.client.gui.PianoScreen;
import com.pianofx.client.gui.SongScreen;
import com.pianofx.score.NoteEvent;
import com.pianofx.score.ScoreLibrary;
import java.io.File;
import net.minecraft.class_1132;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2382;
import net.minecraft.class_243;
import net.minecraft.class_2586;
import net.minecraft.class_276;
import net.minecraft.class_310;
import net.minecraft.class_318;
import net.minecraft.class_4063;
import net.minecraft.class_4184;
import net.minecraft.class_437;
import net.minecraft.class_746;

public final class AutoTest {
    private static final int[] SHOT_AT = new int[]{100, 150, 200, 250, 300};
    private static final int[] SCALE = new int[]{0, 2, 4, 5, 7, 9, 11, 12, 14, 16, 19, 21, 23};
    private static boolean enabled;
    private static boolean armed;
    private static int ticks;
    private static int shot;
    private static int melody;
    private static PianoBlockEntity pianoBe;
    private static class_243 camTarget;
    private static boolean cameraPlaced;

    private AutoTest() {
    }

    public static void init() {
        String v = System.getProperty("pianofx.autotest", "");
        enabled = v.equals("1") || v.equalsIgnoreCase("true");
        PianoFX.LOGGER.info("[PianoFX] \u81ea\u52a8\u6d4b\u8bd5\u5f00\u5173 = {}", (Object)enabled);
    }

    public static void onJoin() {
        if (!enabled) {
            return;
        }
        armed = true;
        ticks = 0;
        shot = 0;
        melody = 0;
        pianoBe = null;
        cameraPlaced = false;
    }

    public static void tick() {
        class_437 scr;
        class_1132 ss3;
        if (!enabled || !armed) {
            return;
        }
        class_310 mc = class_310.method_1551();
        class_746 p = mc.field_1724;
        if (p == null || mc.field_1687 == null) {
            return;
        }
        if (++ticks == 1) {
            mc.field_1690.field_1837 = false;
            mc.method_1507(null);
            mc.field_1690.field_1842 = true;
            mc.field_1690.method_41808().method_41748((Object)82);
            mc.field_1690.method_42528().method_41748((Object)class_4063.field_18162);
            try {
                mc.field_1687.method_8435(18000L);
                ss3 = mc.method_1576();
                if (ss3 != null && ss3.method_30002() != null) {
                    ss3.method_30002().method_29199(18000L);
                }
            }
            catch (Throwable ss2) {
                // empty catch block
            }
        }
        if (ticks == 10) {
            AutoTest.findPiano(mc, p.method_24515(), 40);
            PianoFX.LOGGER.info("[PianoFX] \u81ea\u52a8\u6d4b\u8bd5\uff1a\u94a2\u7434 = {}", (Object)(pianoBe == null ? "\u65e0" : pianoBe.method_11016().method_23854()));
        }
        if (ticks == 14 && pianoBe != null) {
            pianoBe.forceStart();
            PianoFX.LOGGER.info("[PianoFX] \u81ea\u52a8\u6d4b\u8bd5\uff1a\u5f3a\u5236\u5f00\u59cb\u64ad\u653e");
        }
        if (ticks % 40 == 0) {
            try {
                mc.field_1687.method_8435(18000L);
                ss3 = mc.method_1576();
                if (ss3 != null && ss3.method_30002() != null) {
                    ss3.method_30002().method_29199(18000L);
                }
            }
            catch (Throwable ss3) {
                // empty catch block
            }
            mc.field_1690.method_42528().method_41748((Object)class_4063.field_18162);
        }
        if (pianoBe != null) {
            if (!cameraPlaced) {
                AutoTest.placeCamera(p, pianoBe);
                cameraPlaced = true;
            }
            p.method_18800(0.0, 0.0, 0.0);
            if (camTarget != null && !PianoCamera.forced()) {
                AutoTest.lookAt(p, camTarget);
            }
        } else {
            p.method_36456(0.0f);
            p.method_36457(-14.0f);
            if (ticks >= 30 && ticks % 3 == 0) {
                class_243 eye = p.method_33571();
                class_243 look = p.method_5828(1.0f);
                class_243 flat = new class_243(look.field_1352, 0.0, look.field_1350);
                class_243 class_2432 = flat = flat.method_1027() < 1.0E-6 ? new class_243(0.0, 0.0, 1.0) : flat.method_1029();
                class_2350 facing = Math.abs(flat.field_1352) > Math.abs(flat.field_1350) ? (flat.field_1352 > 0.0 ? class_2350.field_11034 : class_2350.field_11039) : (flat.field_1350 > 0.0 ? class_2350.field_11035 : class_2350.field_11043);
                int midi = 48 + SCALE[melody % SCALE.length];
                ++melody;
                FxManager.demoNote(eye.method_1019(flat.method_1021(7.0)).method_1031(0.0, -2.0, 0.0), facing, new NoteEvent(0.0, midi, 96, 0.5));
            }
        }
        if (ticks == 200) {
            PianoScreen.setLayout(PianoScreen.Layout.FULL);
            mc.method_1507((class_437)new PianoScreen(PianoScreen.Mode.PLAY));
        }
        if (ticks == 202 && (scr = mc.field_1755) != null) {
            scr.method_25404(81, 0, 0);
        }
        if (ticks == 209 && (scr = mc.field_1755) != null) {
            scr.method_16803(81, 0, 0);
        }
        if (ticks == 208) {
            AutoTest.shoot(mc);
            PianoFX.LOGGER.info("[PianoFX] \u5b8c\u6574\u754c\u9762\u622a\u56fe\uff08Q \u952e\u5e94\u4e3a\u91d1\u8272\uff09\uff0c\u955c\u5934 {} \u683c", (Object)Float.valueOf(PianoCamera.totalDistance()));
        }
        if (ticks == 216) {
            PianoScreen.setLayout(PianoScreen.Layout.MINI);
            mc.method_1507((class_437)new PianoScreen(PianoScreen.Mode.PLAY));
            PianoFX.LOGGER.info("[PianoFX] \u5207\u5230\u5c0f\u7a97\u7248");
        }
        if (ticks == 226) {
            AutoTest.shoot(mc);
        }
        if (ticks == 234) {
            PianoScreen.setLayout(PianoScreen.Layout.HIDDEN);
            mc.method_1507((class_437)new PianoScreen(PianoScreen.Mode.PLAY));
            PianoFX.LOGGER.info("[PianoFX] \u5207\u5230\u9690\u85cf\u7248\uff08\u53ea\u7559\u53f3\u4e0b\u89d2\u83f1\u5f62\uff09");
        }
        if (ticks == 242) {
            AutoTest.shoot(mc);
        }
        if (ticks == 250) {
            String first = ScoreLibrary.first();
            if (first != null) {
                ScorePlayer.select(first);
                ScorePlayer.start();
            }
            PianoScreen.setLayout(PianoScreen.Layout.MINI);
            mc.method_1507((class_437)new PianoScreen(PianoScreen.Mode.DEMO));
        }
        if (ticks == 260) {
            AutoTest.shoot(mc);
            PianoFX.LOGGER.info("[PianoFX] \u6f14\u793a\u5c0f\u7a97\u622a\u56fe\uff0cplaying={} paused={} t={}", new Object[]{ScorePlayer.playing(), ScorePlayer.paused(), String.format("%.1f", ScorePlayer.timeSec())});
        }
        if (ticks == 268) {
            PianoScreen.setLayout(PianoScreen.Layout.FULL);
            mc.method_1507((class_437)new PianoScreen(PianoScreen.Mode.DEMO));
        }
        if (ticks == 278) {
            AutoTest.shoot(mc);
        }
        if (ticks == 286) {
            ScorePlayer.pause();
            mc.method_1507(null);
            PianoFX.LOGGER.info("[PianoFX] Esc \u9a8c\u8bc1\uff1aplaying={} paused={} t={}", new Object[]{ScorePlayer.playing(), ScorePlayer.paused(), String.format("%.1f", ScorePlayer.timeSec())});
        }
        if (ticks == 292) {
            mc.method_1507((class_437)new SongScreen(null));
        }
        if (ticks == 300) {
            AutoTest.shoot(mc);
        }
        if (ticks == 304) {
            mc.method_1507(null);
            PianoCamera.resetDistance();
            PianoFX.LOGGER.info("[PianoFX] \u673a\u4f4d\u590d\u4f4d -> {} \u683c", (Object)Float.valueOf(PianoCamera.totalDistance()));
        }
        if (ticks == 340) {
            AutoTest.shoot(mc);
            PianoFX.LOGGER.info("[PianoFX] t={} \u6f29\u6da1\u4e3b\u753b\u9762 \u955c\u5934={} \u683c \u97f3\u7b26={} \u7c92\u5b50={} FPS={}", new Object[]{ticks, Float.valueOf(PianoCamera.totalDistance()), FxManager.activeCount(), FxManager.particleDebug(), mc.method_47599()});
        }
        if (ticks == 262) {
            PianoCamera.resetDistance();
            for (int i = 0; i < 6; ++i) {
                PianoCamera.adjustDistance(-2.0f);
            }
            PianoFX.LOGGER.info("[PianoFX] \u81ea\u52a8\u6d4b\u8bd5\uff1a\u955c\u5934\u62c9\u8fd1 -> {}", (Object)Float.valueOf(PianoCamera.totalDistance()));
        }
        if (ticks == 270) {
            AutoTest.shoot(mc);
            PianoFX.LOGGER.info("[PianoFX] t={} \u955c\u5934={} \u683c\uff08\u8fd1\uff09", (Object)ticks, (Object)Float.valueOf(PianoCamera.totalDistance()));
        }
        if (ticks == 278) {
            for (int i = 0; i < 8; ++i) {
                PianoCamera.adjustDistance(2.0f);
            }
            PianoFX.LOGGER.info("[PianoFX] \u81ea\u52a8\u6d4b\u8bd5\uff1a\u955c\u5934\u62c9\u8fdc -> {}", (Object)Float.valueOf(PianoCamera.totalDistance()));
        }
        if (ticks == 288) {
            AutoTest.shoot(mc);
            PianoFX.LOGGER.info("[PianoFX] t={} \u955c\u5934={} \u683c\uff08\u8fdc\uff09", (Object)ticks, (Object)Float.valueOf(PianoCamera.totalDistance()));
        }
        if (shot < SHOT_AT.length && ticks == SHOT_AT[shot]) {
            AutoTest.shoot(mc);
            ++shot;
            class_4184 cam = mc.field_1773.method_19418();
            PianoFX.LOGGER.info("[PianoFX] \u673a\u4f4d {} yaw={} pitch={} \u7b2c\u4e09\u4eba\u79f0={}", new Object[]{cam.method_19326().toString().substring(0, Math.min(48, cam.method_19326().toString().length())), String.format("%.1f", Float.valueOf(cam.method_19330())), String.format("%.1f", Float.valueOf(cam.method_19329())), mc.field_1690.method_31044()});
            PianoFX.LOGGER.info("[PianoFX] t={} FPS={} \u5728\u98de\u97f3\u7b26={} \u7c92\u5b50={}", new Object[]{ticks, mc.method_47599(), FxManager.activeCount(), FxManager.particleDebug()});
        }
        if (ticks == 400) {
            FxManager.forceEnding(true);
            PianoFX.LOGGER.info("[PianoFX] \u81ea\u52a8\u6d4b\u8bd5\uff1a\u8fdb\u5165\u5c3e\u6bb5\uff08\u5e94\u4e3a\u65e0\u62d6\u5c3e\u3001\u97f3\u7b26\u6563\u5f00\uff09");
        }
        if (ticks == 440 || ticks == 500) {
            AutoTest.shoot(mc);
            PianoFX.LOGGER.info("[PianoFX] t={} \u5c3e\u6bb5 FPS={} \u5728\u98de\u97f3\u7b26={} \u7c92\u5b50={}", new Object[]{ticks, mc.method_47599(), FxManager.activeCount(), FxManager.particleDebug()});
        }
        if (ticks > 550) {
            armed = false;
            PianoFX.LOGGER.info("[PianoFX] \u81ea\u52a8\u6d4b\u8bd5\u7ed3\u675f\uff0c\u622a\u56fe {} \u5f20", (Object)shot);
            mc.method_1592();
        }
    }

    private static void placeCamera(class_746 p, PianoBlockEntity be) {
        class_2350 f = be.facing();
        class_243 fwd = new class_243((double)f.method_10148(), 0.0, (double)f.method_10165());
        class_243 c = class_243.method_24953((class_2382)be.method_11016());
        class_243 side = new class_243(-fwd.field_1350, 0.0, fwd.field_1352);
        class_243 stand = new class_243(c.field_1352 + fwd.field_1352 * 2.0 + side.field_1352 * 4.6, (double)be.method_11016().method_10264(), c.field_1350 + fwd.field_1350 * 2.0 + side.field_1350 * 4.6);
        p.method_5814(stand.field_1352, stand.field_1351, stand.field_1350);
        camTarget = c.method_1031(0.0, 2.5, 0.0);
        class_243 d = camTarget.method_1020(p.method_33571().method_1019(fwd.method_1021((double)(-PianoCamera.distance()))));
        p.method_36456((float)Math.toDegrees(Math.atan2(-d.field_1352, d.field_1350)));
        p.method_36457((float)Math.toDegrees(-Math.atan2(d.field_1351, Math.hypot(d.field_1352, d.field_1350))));
        PianoCamera.forceForTest(true);
        PianoFX.LOGGER.info("[PianoFX] \u81ea\u52a8\u6d4b\u8bd5\uff1a\u7ad9\u5230 {}\uff0c\u6f14\u51fa\u76f8\u673a\u5df2\u542f\u7528\uff08{} \u683c\uff09", (Object)stand, (Object)Float.valueOf(PianoCamera.totalDistance()));
    }

    private static void lookAt(class_746 p, class_243 target) {
        class_243 d = target.method_1020(p.method_33571());
        double horiz = Math.hypot(d.field_1352, d.field_1350);
        float yaw = (float)Math.toDegrees(Math.atan2(-d.field_1352, d.field_1350));
        float pitch = (float)Math.toDegrees(-Math.atan2(d.field_1351, horiz));
        p.method_36456(yaw);
        p.method_36457(pitch);
    }

    private static void findPiano(class_310 mc, class_2338 center, int radius) {
        class_2338.class_2339 mut = new class_2338.class_2339();
        double bestD = Double.MAX_VALUE;
        for (int dy = -12; dy <= 12; ++dy) {
            for (int dx = -radius; dx <= radius; ++dx) {
                for (int dz = -radius; dz <= radius; ++dz) {
                    class_2586 be;
                    mut.method_10103(center.method_10263() + dx, center.method_10264() + dy, center.method_10260() + dz);
                    if (!mc.field_1687.method_8320((class_2338)mut).method_27852((class_2248)ModRegistry.PIANO) || !((be = mc.field_1687.method_8321((class_2338)mut)) instanceof PianoBlockEntity)) continue;
                    PianoBlockEntity pbe = (PianoBlockEntity)be;
                    double d = mut.method_10262((class_2382)center);
                    if (!(d < bestD)) continue;
                    bestD = d;
                    pianoBe = pbe;
                }
            }
        }
    }

    private static void shoot(class_310 mc) {
        File dir = new File(new File(mc.field_1697, "screenshots"), "pianofx");
        if (!dir.isDirectory() && !dir.mkdirs()) {
            PianoFX.LOGGER.error("[PianoFX] \u65e0\u6cd5\u521b\u5efa\u622a\u56fe\u76ee\u5f55 {}", (Object)dir);
        }
        String name = "pianofx/" + String.format("t_%03d.png", ticks);
        try {
            class_318.method_22690((File)mc.field_1697, (String)name, (class_276)mc.method_1522(), text -> PianoFX.LOGGER.info("[PianoFX] \u622a\u56fe {}", (Object)text.getString()));
        }
        catch (Throwable t) {
            PianoFX.LOGGER.error("[PianoFX] \u622a\u56fe\u5931\u8d25", t);
        }
    }
}

