/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.loader.api.FabricLoader
 */
package com.pianofx.client;

import com.pianofx.PianoFX;
import net.fabricmc.loader.api.FabricLoader;

public final class ShaderState {
    private static volatile boolean shadersActive;
    private static volatile boolean probed;
    private static boolean lastLogged;

    private ShaderState() {
    }

    public static boolean active() {
        if (!probed) {
            ShaderState.refresh();
        }
        return shadersActive;
    }

    public static void refresh() {
        boolean now = ShaderState.probe();
        probed = true;
        if (now != shadersActive || !lastLogged) {
            lastLogged = true;
            PianoFX.LOGGER.info("[PianoFX] \u5149\u5f71\u72b6\u6001 = {}\uff08\u7279\u6548\u6a21\u5f0f\uff1a{}\uff09", (Object)(now ? "\u5f00" : "\u5173"), (Object)(now ? "\u4eae\u8272/\u534a\u900f\u660e" : "\u52a0\u8272\u53e0\u52a0"));
        }
        shadersActive = now;
    }

    private static boolean probe() {
        if (!FabricLoader.getInstance().isModLoaded("iris")) {
            return false;
        }
        try {
            Class<?> api = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            Object instance = api.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
            Object inUse = api.getMethod("isShaderPackInUse", new Class[0]).invoke(instance, new Object[0]);
            return Boolean.TRUE.equals(inUse);
        }
        catch (Throwable t) {
            PianoFX.LOGGER.warn("[PianoFX] \u8bfb\u53d6 Iris \u72b6\u6001\u5931\u8d25\uff0c\u6309\u65e0\u5149\u5f71\u5904\u7406", t);
            return false;
        }
    }
}

