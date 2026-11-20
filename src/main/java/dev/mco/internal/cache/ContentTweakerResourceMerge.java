package dev.mco.internal.cache;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class ContentTweakerResourceMerge {
    private final Path target;
    private final Path backup;
    private final Set<Path> initialFiles = new HashSet<>();
    private long copiedFiles;
    private long skippedFiles;

    private ContentTweakerResourceMerge(Path target, Path backup) throws IOException {
        this.target = target;
        this.backup = backup;
        try (Stream<Path> paths = Files.walk(target)) {
            paths.filter(Files::isRegularFile).forEach(file -> initialFiles.add(target.relativize(file)));
        }
    }

    public static ContentTweakerResourceMerge create(File target, File backup) throws IOException {
        if (!target.isDirectory() || backup.exists()) return null;
        return new ContentTweakerResourceMerge(absolute(target), absolute(backup));
    }

    public void copyDirectory(File source, File destination) throws IOException {
        Path from = absolute(source);
        Path to = absolute(destination);
        if (from.equals(target) && to.equals(backup)) return;
        if (!to.equals(target)) {
            FileUtils.copyDirectory(source, destination);
            return;
        }
        FileUtils.copyDirectory(source, destination, file -> {
            if (file.isDirectory()) return true;
            if (initialFiles.contains(from.relativize(absolute(file)))) {
                skippedFiles++;
                return false;
            }
            copiedFiles++;
            return true;
        });
    }

    private static Path absolute(File file) {
        return file.toPath().toAbsolutePath().normalize();
    }

    public int getInitialFileCount() { return initialFiles.size(); }
    public long getCopiedFiles() { return copiedFiles; }
    public long getSkippedFiles() { return skippedFiles; }
}
