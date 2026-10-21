/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.ClientModInitializer
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
 *  net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
 *  net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
 *  net.fabricmc.fabric.api.event.player.UseBlockCallback
 *  net.minecraft.class_1269
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_2382
 *  net.minecraft.class_2396
 *  net.minecraft.class_2680
 *  net.minecraft.class_2769
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_3675$class_307
 *  net.minecraft.class_4002
 *  net.minecraft.class_5616
 */
package com.pianofx.client;

import com.pianofx.ModParticles;
import com.pianofx.ModRegistry;
import com.pianofx.PianoFX;
import com.pianofx.block.PianoBlock;
import com.pianofx.client.AutoTest;
import com.pianofx.client.PianoCamera;
import com.pianofx.client.PianoSeat;
import com.pianofx.client.ScorePlayer;
import com.pianofx.client.ShaderState;
import com.pianofx.client.fx.FxManager;
import com.pianofx.client.fx.FxStyle;
import com.pianofx.client.gui.PianoScreen;
import com.pianofx.client.particle.SparkParticle;
import com.pianofx.client.render.PianoKeys;
import com.pianofx.client.render.PianoRenderer;
import com.pianofx.score.Score;
import com.pianofx.score.ScoreLibrary;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.class_1269;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2382;
import net.minecraft.class_2396;
import net.minecraft.class_2680;
import net.minecraft.class_2769;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3675;
import net.minecraft.class_4002;
import net.minecraft.class_5616;

public class PianofxClient
implements ClientModInitializer {
    public static final String KEY_CATEGORY = "key.categories.pianofx";
    public static final class_304 ZOOM_IN = KeyBindingHelper.registerKeyBinding((class_304)new class_304("key.pianofx.zoom_in", class_3675.class_307.field_1668, 91, "key.categories.pianofx"));
    public static final class_304 ZOOM_OUT = KeyBindingHelper.registerKeyBinding((class_304)new class_304("key.pianofx.zoom_out", class_3675.class_307.field_1668, 93, "key.categories.pianofx"));
    public static final class_304 OPEN_UI = KeyBindingHelper.registerKeyBinding((class_304)new class_304("key.pianofx.open_ui", class_3675.class_307.field_1668, 80, "key.categories.pianofx"));
    public static final float ZOOM_STEP = 1.5f;

    public void onInitializeClient() {
        ScoreLibrary.reload();
        PianoFX.fxHook = FxManager.INSTANCE;
        class_5616.method_32144(ModRegistry.PIANO_BE, ctx -> new PianoRenderer());
        ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
        registry.register((class_2396)ModParticles.SPARK, sp -> new SparkParticle.Factory((class_4002)sp, 0));
        registry.register((class_2396)ModParticles.GLOW, sp -> new SparkParticle.Factory((class_4002)sp, 1));
        registry.register((class_2396)ModParticles.STAR, sp -> new SparkParticle.Factory((class_4002)sp, 2));
        registry.register((class_2396)ModParticles.NOTE, sp -> new SparkParticle.Factory((class_4002)sp, 3));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (ZOOM_IN.method_1436()) {
                PianoCamera.adjustDistance(-1.5f);
            }
            while (ZOOM_OUT.method_1436()) {
                PianoCamera.adjustDistance(1.5f);
            }
            while (OPEN_UI.method_1436()) {
                PianofxClient.openNearestPiano(client);
            }
            PianoKeys.tick();
            PianoSeat.tick(client);
            FxManager.tick();
            ScorePlayer.tick();
            PianoCamera.tick(client);
            AutoTest.tick();
        });
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (player.method_7325() || !world.method_8320(hit.method_17777()).method_27852((class_2248)ModRegistry.PIANO)) {
                return class_1269.field_5811;
            }
            if (player.method_5715()) {
                return class_1269.field_5811;
            }
            if (world.field_9236) {
                class_2350 facing = (class_2350)world.method_8320(hit.method_17777()).method_11654((class_2769)PianoBlock.field_11177);
                PianoSeat.sit(class_310.method_1551(), hit.method_17777(), facing);
                PianoScreen.open(hit.method_17777(), facing);
            }
            return class_1269.field_5812;
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            FxManager.clear();
            ScoreLibrary.reload();
            ShaderState.refresh();
            for (String n : ScoreLibrary.names()) {
                Score sc = ScoreLibrary.get(n);
                FxStyle st = FxStyle.of(sc);
                PianoFX.LOGGER.info("[PianoFX] \u66f2\u76ee\u76ae\u80a4 {} -> {} \u901f\u5ea6{} \u8303\u56f4{} \u5b57\u5f62{} \u5bff\u547d{}", new Object[]{n, st.kind(), String.format("%.2f", st.speed()), String.format("%.2f", st.range()), st.glyphs().length, String.format("%.0f", st.lifeTicks())});
            }
            AutoTest.onJoin();
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> FxManager.clear());
        AutoTest.init();
        PianoFX.LOGGER.info("[PianoFX] client ready, scores = {}", ScoreLibrary.names());
    }

    private static void openNearestPiano(class_310 mc) {
        if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1755 != null) {
            return;
        }
        class_2338 origin = mc.field_1724.method_24515();
        class_2338 best = null;
        double bestD = 36.0;
        for (class_2338 p : class_2338.method_10097((class_2338)origin.method_10069(-6, -3, -6), (class_2338)origin.method_10069(6, 3, 6))) {
            double d;
            class_2680 st = mc.field_1687.method_8320(p);
            if (!st.method_27852((class_2248)ModRegistry.PIANO) || !((d = p.method_10262((class_2382)origin)) < bestD)) continue;
            bestD = d;
            best = p.method_10062();
        }
        if (best == null) {
            return;
        }
        PianoScreen.open(best, (class_2350)mc.field_1687.method_8320(best).method_11654((class_2769)PianoBlock.field_11177));
    }
}

