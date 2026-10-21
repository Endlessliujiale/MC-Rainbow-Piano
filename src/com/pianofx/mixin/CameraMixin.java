/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1922
 *  net.minecraft.class_2338
 *  net.minecraft.class_2374
 *  net.minecraft.class_243
 *  net.minecraft.class_4184
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.pianofx.mixin;

import com.pianofx.PianoFX;
import com.pianofx.client.PianoCamera;
import net.minecraft.class_1297;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_2374;
import net.minecraft.class_243;
import net.minecraft.class_4184;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_4184.class})
public abstract class CameraMixin {
    private static int DBG;

    @Shadow
    protected abstract void method_19322(class_243 var1);

    @Inject(method={"method_19321"}, at={@At(value="TAIL")})
    private void pianofx$zoomOut(class_1922 area, class_1297 focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        class_243 next;
        class_2338 bp;
        double travelled;
        double step;
        if (!PianoCamera.active()) {
            return;
        }
        class_4184 self = (class_4184)this;
        if (!self.method_19333()) {
            return;
        }
        float extra = PianoCamera.distance(tickDelta);
        if (extra <= 0.05f) {
            return;
        }
        class_243 back = class_243.method_1030((float)0.0f, (float)self.method_19330());
        if (inverseView) {
            back = back.method_1021(-1.0);
        }
        back = back.method_1021(-1.0);
        class_243 from = self.method_19326();
        class_243 pos = from;
        for (travelled = 0.0; travelled < (double)extra && area.method_8320(bp = class_2338.method_49638((class_2374)(next = pos.method_1019(back.method_1021(step = Math.min(0.5, (double)extra - travelled)))).method_1031(0.0, 0.45, 0.0))).method_26215(); travelled += step) {
            pos = next;
        }
        if (travelled > 0.05) {
            this.method_19322(pos);
        }
        if (++DBG % 100 == 0) {
            PianoFX.LOGGER.info("[PianoFX] \u76f8\u673a\u540e\u64a4 extra={} travelled={} from={} to={}", new Object[]{String.format("%.2f", Float.valueOf(extra)), String.format("%.2f", travelled), from.toString().substring(0, Math.min(40, from.toString().length())), pos.toString().substring(0, Math.min(40, pos.toString().length()))});
        }
    }
}

