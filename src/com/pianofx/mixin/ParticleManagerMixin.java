/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.class_3999
 *  net.minecraft.class_4184
 *  net.minecraft.class_702
 *  net.minecraft.class_765
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.pianofx.mixin;

import com.google.common.collect.ImmutableList;
import com.pianofx.client.particle.AdditiveSheet;
import java.util.List;
import net.minecraft.class_3999;
import net.minecraft.class_4184;
import net.minecraft.class_702;
import net.minecraft.class_765;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_702.class})
public class ParticleManagerMixin {
    @Shadow
    @Final
    @Mutable
    private static List<class_3999> field_17820;
    private static boolean pianofx$registered;

    @Inject(method={"method_3049"}, at={@At(value="HEAD")})
    private void pianofx$registerAdditiveSheet(class_765 lightmap, class_4184 camera, float tickDelta, CallbackInfo ci) {
        if (pianofx$registered) {
            return;
        }
        pianofx$registered = true;
        if (!field_17820.contains(AdditiveSheet.INSTANCE)) {
            field_17820 = ImmutableList.builder().addAll(field_17820).add((Object)AdditiveSheet.INSTANCE).build();
        }
    }
}

