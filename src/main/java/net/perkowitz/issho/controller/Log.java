package net.perkowitz.issho.controller;

import com.google.common.collect.Lists;
import lombok.Setter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Log {

    public static final int ERROR = 30;
    public static final int WARNING = 20;
    public static final int INFO = 10;
    public static final int OFF = 0;
    public static final int ALWAYS = 1000;

    private static final int SHORT_DELAY = 10;
    private static final long MEGABYTE = 1000000;
    private static final boolean showCallingMethod = true;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SSSSSS")
                    .withLocale( Locale.US )
                    .withZone( ZoneId.systemDefault() );

    private static Runtime runtime = Runtime.getRuntime();

    private static List<Instant> stopWatches = Lists.newArrayList();

    @Setter private static int logLevel = INFO;

    public static void log(Object source, int level, String format, Object ... args) {
        if (level >= logLevel) {
            Instant now = Instant.now();
            String t = formatter.format(now);
            String s = "";
            String sourceName = "static";
            int sourceHash = -1;
            if (source != null) {
                sourceName = source.getClass().getSimpleName();
                sourceHash = source.hashCode();
            }
            if (showCallingMethod) {
                String callingMethod = new Throwable().getStackTrace()[1].getMethodName();
                s = String.format("%s [%s@%s.%s] %s\n", t, sourceName, sourceHash, callingMethod, format);
            } else {
                 s = String.format("%s [%s@%s] %s\n", t, sourceName, sourceHash, format);
            }
            System.out.printf(s, args);
        }
    }

    public static void delay(int millis) {
        if (millis == 0) return;
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {}
    }

    public static void delay() {
        delay(SHORT_DELAY);
    }

    public static void memory(Object source, int level, String message) {
        long m = runtime.totalMemory() / MEGABYTE;
        long free = runtime.freeMemory();
        long f = free / MEGABYTE;
        log(source, level, message + " Mem=%dMB, Free=%dMB (%d)", m, f, free);
    }

    public static long freeMem() {
        long free = runtime.freeMemory();
        return free / MEGABYTE;
    }

    public static void gc(Object source, int level) {
        memory(source, level, "Before GC:");
        runtime.gc();
        memory(source, level, "After GC:");
    }

    public static void addStopwatch() {
        stopWatches.add(Instant.now());
    }

    public static void resetStopWatch(int index) {
        if (index >= 0 && index < stopWatches.size()) {
            stopWatches.set(index, Instant.now());
        }
    }

    public static void resetAllStopWatches() {
        Instant now = Instant.now();
        for (int i = 0; i < stopWatches.size(); i++) {
            stopWatches.set(i, now);
        }
    }

    public static String stopWatchTimes() {
        List<String> times = Lists.newArrayList();
        Instant now = Instant.now();
        for (Instant stopWatch : stopWatches) {
            times.add(String.format("%04d", ChronoUnit.MILLIS.between(stopWatch, now)));
        }
        if (ChronoUnit.MILLIS.between(stopWatches.get(stopWatches.size()-1), now) > 15) {
            times.add("*****");
        }
        return String.join(":", times);
    }

}
