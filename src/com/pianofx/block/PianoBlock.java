/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.class_1269
 *  net.minecraft.class_1657
 *  net.minecraft.class_1750
 *  net.minecraft.class_1937
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2343
 *  net.minecraft.class_2350
 *  net.minecraft.class_2383
 *  net.minecraft.class_2561
 *  net.minecraft.class_2586
 *  net.minecraft.class_2591
 *  net.minecraft.class_2680
 *  net.minecraft.class_2689$class_2690
 *  net.minecraft.class_2769
 *  net.minecraft.class_3965
 *  net.minecraft.class_4970$class_2251
 *  net.minecraft.class_5558
 */
package com.pianofx.block;

import com.mojang.serialization.MapCodec;
import com.pianofx.block.PianoBlockEntity;
import net.minecraft.class_1269;
import net.minecraft.class_1657;
import net.minecraft.class_1750;
import net.minecraft.class_1937;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2343;
import net.minecraft.class_2350;
import net.minecraft.class_2383;
import net.minecraft.class_2561;
import net.minecraft.class_2586;
import net.minecraft.class_2591;
import net.minecraft.class_2680;
import net.minecraft.class_2689;
import net.minecraft.class_2769;
import net.minecraft.class_3965;
import net.minecraft.class_4970;
import net.minecraft.class_5558;

public class PianoBlock
extends class_2383
implements class_2343 {
    public static final MapCodec<PianoBlock> CODEC = PianoBlock.method_54094(PianoBlock::new);

    public PianoBlock(class_4970.class_2251 settings) {
        super(settings);
        this.method_9590((class_2680)this.method_9564().method_11657((class_2769)field_11177, (Comparable)class_2350.field_11043));
    }

    protected MapCodec<? extends class_2383> method_53969() {
        return CODEC;
    }

    protected void method_9515(class_2689.class_2690<class_2248, class_2680> builder) {
        builder.method_11667(new class_2769[]{field_11177});
    }

    public class_2680 method_9605(class_1750 ctx) {
        return (class_2680)this.method_9564().method_11657((class_2769)field_11177, (Comparable)ctx.method_8042().method_10153());
    }

    public class_2586 method_10123(class_2338 pos, class_2680 state) {
        return new PianoBlockEntity(pos, state);
    }

    protected class_1269 method_55766(class_2680 state, class_1937 world, class_2338 pos, class_1657 player, class_3965 hit) {
        if (world.field_9236) {
            return class_1269.field_5812;
        }
        class_2586 class_25862 = world.method_8321(pos);
        if (class_25862 instanceof PianoBlockEntity) {
            PianoBlockEntity be = (PianoBlockEntity)class_25862;
            if (player.method_5715()) {
                String name = be.cycleScore();
                player.method_7353((class_2561)class_2561.method_43469((String)"message.pianofx.now_playing", (Object[])new Object[]{name}), false);
            }
        }
        return class_1269.field_5812;
    }

    public <T extends class_2586> class_5558<T> method_31645(class_1937 world, class_2680 state, class_2591<T> type) {
        return (w, p, s, be) -> {
            if (be instanceof PianoBlockEntity) {
                PianoBlockEntity piano = (PianoBlockEntity)be;
                piano.tick();
            }
        };
    }
}

