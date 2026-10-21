/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_312
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.pianofx.mixin;

import com.pianofx.client.PianoCamera;
import net.minecraft.class_310;
import net.minecraft.class_312;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_312.class})
public class MouseMixin {
    @Inject(method={"method_1598(JDD)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void pianofx$scrollZoom(long window, double horizontal, double vertical, CallbackInfo ci) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1755 != null || !PianoCamera.active() || vertical == 0.0) {
            return;
        }
        PianoCamera.adjustDistance(vertical > 0.0 ? 1.5f : -1.5f);
        ci.cancel();
    }
}

