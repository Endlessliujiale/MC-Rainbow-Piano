/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_332
 *  net.minecraft.class_4185
 *  net.minecraft.class_4185$class_4241
 */
package com.pianofx.client.gui;

import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_4185;

public class FlatButton
extends class_4185 {
    private final boolean alignLeft;
    private final boolean selected;

    protected FlatButton(int x, int y, int w, int h, class_2561 text, class_4185.class_4241 onPress, boolean alignLeft, boolean selected) {
        super(x, y, w, h, text, onPress, field_40754);
        this.alignLeft = alignLeft;
        this.selected = selected;
    }

    public static FlatButton centered(int x, int y, int w, int h, class_2561 text, class_4185.class_4241 onPress) {
        return new FlatButton(x, y, w, h, text, onPress, false, false);
    }

    public static FlatButton row(int x, int y, int w, int h, class_2561 text, class_4185.class_4241 onPress, boolean selected) {
        return new FlatButton(x, y, w, h, text, onPress, true, selected);
    }

    protected void method_48579(class_332 ctx, int mouseX, int mouseY, float delta) {
        int bg;
        int edge;
        boolean lit = this.method_49606() || this.method_25370();
        int n = edge = lit ? -10713404 : -13945272;
        int n2 = lit ? -14866375 : (bg = this.selected ? -15195857 : -15591390);
        if (!this.field_22763) {
            bg = -15723234;
        }
        ctx.method_25294(this.method_46426(), this.method_46427(), this.method_46426() + this.method_25368(), this.method_46427() + this.method_25364(), edge);
        ctx.method_25294(this.method_46426() + 1, this.method_46427() + 1, this.method_46426() + this.method_25368() - 1, this.method_46427() + this.method_25364() - 1, bg);
        if (this.selected) {
            ctx.method_25294(this.method_46426() + 1, this.method_46427() + 1, this.method_46426() + 3, this.method_46427() + this.method_25364() - 1, -8407809);
        }
        int color = this.field_22763 ? (lit ? -1 : -2300171) : -9734005;
        class_327 tr = class_310.method_1551().field_1772;
        if (this.alignLeft) {
            ctx.method_51439(tr, this.method_25369(), this.method_46426() + 8, this.method_46427() + (this.method_25364() - 8) / 2, color, false);
        } else {
            ctx.method_27534(tr, this.method_25369(), this.method_46426() + this.method_25368() / 2, this.method_46427() + (this.method_25364() - 8) / 2, color);
        }
    }
}

