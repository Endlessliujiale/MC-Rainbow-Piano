/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
 *  net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder
 *  net.minecraft.class_1747
 *  net.minecraft.class_1792
 *  net.minecraft.class_1792$class_1793
 *  net.minecraft.class_1935
 *  net.minecraft.class_2248
 *  net.minecraft.class_2378
 *  net.minecraft.class_2591
 *  net.minecraft.class_2960
 *  net.minecraft.class_4970$class_2251
 *  net.minecraft.class_5321
 *  net.minecraft.class_7706
 *  net.minecraft.class_7923
 */
package com.pianofx;

import com.pianofx.block.PianoBlock;
import com.pianofx.block.PianoBlockEntity;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1935;
import net.minecraft.class_2248;
import net.minecraft.class_2378;
import net.minecraft.class_2591;
import net.minecraft.class_2960;
import net.minecraft.class_4970;
import net.minecraft.class_5321;
import net.minecraft.class_7706;
import net.minecraft.class_7923;

public final class ModRegistry {
    public static final class_2960 PIANO_ID = class_2960.method_60655((String)"pianofx", (String)"piano");
    public static final PianoBlock PIANO = (PianoBlock)((Object)class_2378.method_10230((class_2378)class_7923.field_41175, (class_2960)PIANO_ID, (Object)((Object)new PianoBlock(class_4970.class_2251.method_9637().method_9629(2.0f, 6.0f)))));
    public static final class_1792 PIANO_ITEM = (class_1792)class_2378.method_10230((class_2378)class_7923.field_41178, (class_2960)PIANO_ID, (Object)new class_1747((class_2248)PIANO, new class_1792.class_1793()));
    public static final class_2591<PianoBlockEntity> PIANO_BE = (class_2591)class_2378.method_10230((class_2378)class_7923.field_41181, (class_2960)PIANO_ID, (Object)FabricBlockEntityTypeBuilder.create(PianoBlockEntity::new, (class_2248[])new class_2248[]{PIANO}).build());

    private ModRegistry() {
    }

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent((class_5321)class_7706.field_40197).register(e -> e.method_45421((class_1935)PIANO_ITEM));
    }
}

