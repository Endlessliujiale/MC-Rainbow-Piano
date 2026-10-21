/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.loader.api.FabricLoader
 */
package com.pianofx.score;

import com.pianofx.PianoFX;
import com.pianofx.score.MidiLoader;
import com.pianofx.score.Score;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.fabricmc.loader.api.FabricLoader;

public final class ScoreLibrary {
    private static final Map<String, Score> SCORES = new LinkedHashMap<String, Score>();
    private static List<String> names = List.of();

    private ScoreLibrary() {
    }

    public static Path dir() {
        return FabricLoader.getInstance().getConfigDir().resolve("pianofx").resolve("scores");
    }

    public static synchronized void reload() {
        SCORES.clear();
        Path dir = ScoreLibrary.dir();
        try {
            Files.createDirectories(dir, new FileAttribute[0]);
            if (!Files.exists(dir.resolve("_\u628amid\u6587\u4ef6\u653e\u8fd9\u91cc.txt"), new LinkOption[0])) {
                Files.writeString(dir.resolve("_\u628amid\u6587\u4ef6\u653e\u8fd9\u91cc.txt"), (CharSequence)"\u628a .mid / .midi \u6587\u4ef6\u4e22\u8fdb\u8fd9\u4e2a\u76ee\u5f55\uff0c\u7136\u540e\u5728\u6e38\u620f\u91cc\u6f5c\u884c\u53f3\u952e\u94a2\u7434\u5373\u53ef\u91cd\u65b0\u626b\u63cf\u5e76\u5207\u6362\u66f2\u76ee\u3002\n", new OpenOption[0]);
            }
        }
        catch (IOException e) {
            PianoFX.LOGGER.warn("[PianoFX] \u65e0\u6cd5\u521b\u5efa\u8c31\u5b50\u76ee\u5f55: {}", (Object)dir, (Object)e);
        }
        try (Stream<Path> stream = Files.list(dir);){
            List<Path> files = stream.filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).filter(p -> {
                String n = p.getFileName().toString().toLowerCase();
                return n.endsWith(".mid") || n.endsWith(".midi");
            }).sorted().toList();
            for (Path p2 : files) {
                String fileName = p2.getFileName().toString();
                String key = fileName.substring(0, fileName.lastIndexOf(46));
                try {
                    Score s = MidiLoader.load(p2, key);
                    SCORES.put(key, s);
                    PianoFX.LOGGER.info("[PianoFX] \u8f7d\u5165\u8c31\u5b50 {} ({} \u97f3\u7b26, {}s)", new Object[]{key, s.notes().size(), String.format("%.1f", s.durationSec())});
                }
                catch (Exception e) {
                    PianoFX.LOGGER.warn("[PianoFX] \u89e3\u6790\u5931\u8d25 {}: {}", (Object)fileName, (Object)e.toString());
                }
            }
            names = new ArrayList<String>(SCORES.keySet());
        }
        catch (IOException e) {
            PianoFX.LOGGER.warn("[PianoFX] \u626b\u63cf\u8c31\u5b50\u76ee\u5f55\u5931\u8d25", (Throwable)e);
        }
    }

    public static synchronized List<String> names() {
        if (names.isEmpty()) {
            ScoreLibrary.reload();
        }
        return names;
    }

    public static synchronized Score get(String name) {
        if (SCORES.isEmpty()) {
            ScoreLibrary.reload();
        }
        return SCORES.get(name);
    }

    public static synchronized String first() {
        List<String> n = ScoreLibrary.names();
        return n.isEmpty() ? null : n.get(0);
    }
}

