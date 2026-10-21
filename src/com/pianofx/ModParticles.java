/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2378
 *  net.minecraft.class_2960
 *  net.minecraft.class_7923
 */
package com.pianofx;

import com.pianofx.particle.SparkParticleType;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public final class ModParticles {
    public static final SparkParticleType SPARK = new SparkParticleType();
    public static final SparkParticleType GLOW = new SparkParticleType();
    public static final SparkParticleType STAR = new SparkParticleType();
    public static final SparkParticleType NOTE = new SparkParticleType();

    private ModParticles() {
    }

    public static void init() {
        ModParticles.register("spark", SPARK);
        ModParticles.register("glow", GLOW);
        ModParticles.register("star", STAR);
        ModParticles.register("note", NOTE);
    }

    private static void register(String name, SparkParticleType type) {
        class_2378.method_10230((class_2378)class_7923.field_41180, (class_2960)class_2960.method_60655((String)"pianofx", (String)name), (Object)((Object)type));
    }
}

