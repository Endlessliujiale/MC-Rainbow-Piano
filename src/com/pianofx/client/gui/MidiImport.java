/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1074
 *  net.minecraft.class_310
 */
package com.pianofx.client.gui;

import com.pianofx.PianoFX;
import com.pianofx.score.ScoreLibrary;
import java.awt.FileDialog;
import java.awt.Frame;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.function.Consumer;
import net.minecraft.class_1074;
import net.minecraft.class_310;

public final class MidiImport {
    private MidiImport() {
    }

    public static void choose(Consumer<String> onDone) {
        String dialogTitle = class_1074.method_4662((String)"gui.pianofx.choose_file", (Object[])new Object[0]);
        Thread thread = new Thread(() -> {
            String imported = "";
            try {
                FileDialog dialog = new FileDialog((Frame)null, dialogTitle, 0);
                dialog.setFilenameFilter((dir, name) -> {
                    String n = name.toLowerCase();
                    return n.endsWith(".mid") || n.endsWith(".midi");
                });
                dialog.setMultipleMode(false);
                dialog.setVisible(true);
                String file = dialog.getFile();
                String dir2 = dialog.getDirectory();
                dialog.dispose();
                if (file != null && dir2 != null) {
                    Path src = Paths.get(dir2, file);
                    Path dstDir = ScoreLibrary.dir();
                    Files.createDirectories(dstDir, new FileAttribute[0]);
                    Files.copy(src, dstDir.resolve(file), StandardCopyOption.REPLACE_EXISTING);
                    imported = file.substring(0, file.lastIndexOf(46));
                    PianoFX.LOGGER.info("[PianoFX] \u5df2\u5bfc\u5165 MIDI: {} -> {}", (Object)src, (Object)dstDir.resolve(file));
                }
            }
            catch (Throwable t) {
                PianoFX.LOGGER.warn("[PianoFX] \u5bfc\u5165 MIDI \u5931\u8d25\uff0c\u53ef\u4ee5\u628a .mid \u6587\u4ef6\u76f4\u63a5\u653e\u8fdb {}", (Object)ScoreLibrary.dir(), (Object)t);
            }
            String result = imported;
            class_310.method_1551().execute(() -> {
                ScoreLibrary.reload();
                if (onDone != null) {
                    onDone.accept(result);
                }
            });
        }, "pianofx-midi-import");
        thread.setDaemon(true);
        thread.start();
    }
}

