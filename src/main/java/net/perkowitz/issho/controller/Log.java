package net.perkowitz.issho.controller;

import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Log {

    public static final int ERROR = 30;
    public static final int WARNING = 20;
    public static final int INFO = 10;
    public static final int OFF = 0;

    private static final int SHORT_DELAY = 10;
    private static final boolean showCallingMethod = true;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Setter private static int logLevel = INFO;

    public static void log(Object source, int level, String format, Object ... args) {
        if (level >= logLevel) {
            Date now = new Date();
            String d = sdf.format(now);
            String s = "";
            if (showCallingMethod) {
                String callingMethod = new Throwable().getStackTrace()[1].getMethodName();
                s = String.format("%s [%s@%s.%s] %s\n", d, source.getClass().getSimpleName(), source.hashCode(), callingMethod, format);
            } else {
                 s = String.format("%s [%s@%s] %s\n", d, source.getClass().getSimpleName(), source.hashCode(), format);
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
}
