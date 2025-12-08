package io.noties.markwon.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Locale;

/**
 * Utility class for logging messages with timestamps in the format HH:MM:SS:mmm
 * 
 * @since 4.6.3
 */
public abstract class TimestampLogger {

    /**
     * Format a timestamp in HH:MM:SS:mmm format
     * 
     * @param timeMillis time in milliseconds since epoch
     * @return formatted timestamp string
     */
    @NonNull
    public static String formatTimestamp(long timeMillis) {
        long totalSeconds = timeMillis / 1000;
        long hours = (totalSeconds / 3600) % 24;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        long millis = timeMillis % 1000;
        
        return String.format(Locale.US, "%02d:%02d:%02d:%03d", hours, minutes, seconds, millis);
    }

    /**
     * Get current timestamp formatted as HH:MM:SS:mmm
     * 
     * Note: This method calls System.currentTimeMillis() on each invocation to provide
     * accurate timestamps for logging. This is intentional for logging use cases where
     * the actual time of the log message is important.
     * 
     * @return formatted current timestamp
     */
    @NonNull
    public static String getCurrentTimestamp() {
        return formatTimestamp(System.currentTimeMillis());
    }

    /**
     * Log a debug message with timestamp
     * 
     * @param tag the log tag
     * @param message the log message
     */
    public static void d(@NonNull String tag, @NonNull String message) {
        Log.d(tag, "[" + getCurrentTimestamp() + "] " + message);
    }

    /**
     * Log an info message with timestamp
     * 
     * @param tag the log tag
     * @param message the log message
     */
    public static void i(@NonNull String tag, @NonNull String message) {
        Log.i(tag, "[" + getCurrentTimestamp() + "] " + message);
    }

    /**
     * Log a warning message with timestamp
     * 
     * @param tag the log tag
     * @param message the log message
     */
    public static void w(@NonNull String tag, @NonNull String message) {
        Log.w(tag, "[" + getCurrentTimestamp() + "] " + message);
    }

    /**
     * Log an error message with timestamp
     * 
     * @param tag the log tag
     * @param message the log message
     */
    public static void e(@NonNull String tag, @NonNull String message) {
        Log.e(tag, "[" + getCurrentTimestamp() + "] " + message);
    }

    /**
     * Log an error message with timestamp and throwable
     * 
     * @param tag the log tag
     * @param message the log message
     * @param throwable the throwable to log
     */
    public static void e(@NonNull String tag, @NonNull String message, @NonNull Throwable throwable) {
        Log.e(tag, "[" + getCurrentTimestamp() + "] " + message, throwable);
    }

    private TimestampLogger() {
    }
}
