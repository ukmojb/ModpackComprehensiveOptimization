package dev.mco.internal.cache;

import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class EmptySoundPack {
    private static final Set<String> DIRECTORIES = Set.of("assets/", "assets/minecraft/", "assets/minecraft/sounds/");
    private static final byte[] METADATA = "{\"pack\":{\"pack_format\":1,\"description\":\"Chance Cubes Resource Pack\"}}"
            .getBytes(StandardCharsets.UTF_8);
    private static final Pattern EMPTY_OBJECT = Pattern.compile("[ \t\r\n]*\\{[ \t\r\n]*}[ \t\r\n]*");

    private EmptySoundPack() {}

    public static boolean isEmpty(File file) {
        boolean metadata = false;
        boolean sounds = false;
        try (ZipFile zip = new ZipFile(file)) {
            for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements();) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if (entry.isDirectory() && DIRECTORIES.contains(name)) continue;
                if (name.equals("pack.mcmeta") && !metadata) {
                    try (InputStream stream = zip.getInputStream(entry)) {
                        if (!Arrays.equals(METADATA, stream.readNBytes(METADATA.length + 1))) return false;
                    }
                    metadata = true;
                } else if (name.equals("assets/minecraft/sounds.json") && !sounds) {
                    if (entry.getSize() > 4096) return false;
                    try (InputStream stream = zip.getInputStream(entry)) {
                        byte[] data = stream.readNBytes(4097);
                        if (data.length > 4096 || !EMPTY_OBJECT.matcher(new String(data, StandardCharsets.UTF_8)).matches()) return false;
                    }
                    sounds = true;
                } else {
                    return false;
                }
            }
            return metadata && sounds;
        } catch (IOException failure) {
            LogManager.getLogger("mco").warn("无法读取声音包 {}", file, failure);
            return false;
        }
    }
}
