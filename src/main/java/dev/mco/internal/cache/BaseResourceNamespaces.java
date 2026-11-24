package dev.mco.internal.cache;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.Platform;
import java.io.File;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;

public final class BaseResourceNamespaces implements AutoCloseable {
    private static volatile BaseResourceNamespaces active;
    private final File root;
    private final Set<String> names = new HashSet<>();
    private final ConcurrentHashMap<String, Boolean> absent = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lifetime = new ReentrantReadWriteLock();
    private Pointer parentWatch;
    private Pointer rootWatch;
    private volatile boolean valid;

    private BaseResourceNamespaces(File root) {
        this.root = root;
    }

    public static void duringModels(Runnable load) {
        if (!Platform.isWindows() || !Platform.is64Bit()) {
            load.run();
            return;
        }
        var index = new BaseResourceNamespaces(new File(Minecraft.getMinecraft().gameDir, "resources").getAbsoluteFile());
        try (index) {
            index.open();
            active = index;
            load.run();
        } finally {
            active = null;
        }
    }

    private void open() {
        parentWatch = watch(root.getParentFile(), 2);
        if (parentWatch == null) return;
        rootWatch = watch(root, 3);
        if (rootWatch == null) return;
        String[] entries = root.list();
        if (entries == null) {
            LogManager.getLogger("mco").warn("无法枚举资源目录 {}", root);
            return;
        }
        for (String entry : entries) {
            // 非 ASCII 文件名可能具有不同的 NTFS 大小写映射或 DOS 别名。
            if (!entry.chars().allMatch(c -> c >= 32 && c < 127)) return;
            names.add(entry.toLowerCase(Locale.ROOT));
        }
        valid = true;
    }

    private static Pointer watch(File directory, int filter) {
        Pointer handle = Windows.FindFirstChangeNotificationW(new WString(directory.toString()), 0, filter);
        if (Pointer.nativeValue(handle) != -1 && handle != null) return handle;
        LogManager.getLogger("mco").warn("无法监视目录 {}，Windows 错误 {}", directory, Native.getLastError());
        return null;
    }

    public static boolean excludes(File directory, String name) {
        var index = active;
        if (index == null || !index.valid || !index.root.equals(directory) || !name.startsWith("assets/")) return false;
        String path = name.replace("assets/", "");
        int slash = path.indexOf('/');
        if (slash <= 0 || path.contains("..") || path.indexOf('\\') >= 0 || path.indexOf(':') >= 0) return false;
        String namespace = path.substring(0, slash);
        if (namespace.endsWith(".") || index.names.contains(namespace)) return false;
        for (int i = 0; i < namespace.length(); i++) {
            char c = namespace.charAt(i);
            if (!(c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '_' || c == '-' || c == '.')) return false;
        }
        var read = index.lifetime.readLock();
        read.lock();
        try {
            if (!index.valid) return false;
            // 首次查询按文件系统规则识别 NTFS 的短文件名及自定义别名。
            if (!index.absent.computeIfAbsent(namespace, key -> !new File(directory, key).exists())) return false;
            if (unchanged(index.rootWatch) && unchanged(index.parentWatch)) return true;
            index.valid = false;
            return false;
        } finally {
            read.unlock();
        }
    }

    private static boolean unchanged(Pointer handle) {
        int result = Windows.WaitForSingleObject(handle, 0);
        if (result == -1) LogManager.getLogger("mco").warn("读取目录通知失败，Windows 错误 {}", Native.getLastError());
        return result == 258;
    }

    @Override
    public void close() {
        var write = lifetime.writeLock();
        write.lock();
        try {
            valid = false;
            closeWatch(rootWatch);
            closeWatch(parentWatch);
        } finally {
            write.unlock();
        }
    }

    private static void closeWatch(Pointer handle) {
        if (handle != null && Windows.FindCloseChangeNotification(handle) == 0)
            LogManager.getLogger("mco").warn("关闭目录通知失败，Windows 错误 {}", Native.getLastError());
    }

    private static final class Windows {
        static { Native.register("kernel32"); }
        static native Pointer FindFirstChangeNotificationW(WString directory, int subtree, int filter);
        static native int FindCloseChangeNotification(Pointer handle);
        static native int WaitForSingleObject(Pointer handle, int timeout);
    }
}
