/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.ModInitializer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.pianofx;

import com.pianofx.ModParticles;
import com.pianofx.ModRegistry;
import com.pianofx.block.PianoBlockEntity;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PianoFX
implements ModInitializer {
    public static final String MOD_ID = "pianofx";
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"pianofx");
    public static FxHook fxHook;

    public void onInitialize() {
        ModParticles.init();
        ModRegistry.register();
        LOGGER.info("[PianoFX] initialized");
    }

    public static interface FxHook {
        public void onTick(PianoBlockEntity var1, double var2, double var4);
    }
}

