package io.noties.markwon.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class TimestampLoggerTest {

    @Test
    public void formatTimestamp_zeroTime() {
        // 0 milliseconds should be 00:00:00:000
        String result = TimestampLogger.formatTimestamp(0);
        assertEquals("00:00:00:000", result);
    }

    @Test
    public void formatTimestamp_oneSecond() {
        // 1000 milliseconds should be 00:00:01:000
        String result = TimestampLogger.formatTimestamp(1000);
        assertEquals("00:00:01:000", result);
    }

    @Test
    public void formatTimestamp_oneMinute() {
        // 60000 milliseconds should be 00:01:00:000
        String result = TimestampLogger.formatTimestamp(60000);
        assertEquals("00:01:00:000", result);
    }

    @Test
    public void formatTimestamp_oneHour() {
        // 3600000 milliseconds should be 01:00:00:000
        String result = TimestampLogger.formatTimestamp(3600000);
        assertEquals("01:00:00:000", result);
    }

    @Test
    public void formatTimestamp_exampleFromProblemStatement() {
        // Test the specific timestamp format from problem statement: 01:04:01:929
        // 1 hour + 4 minutes + 1 second + 929 milliseconds
        long timeMillis = (1 * 3600 + 4 * 60 + 1) * 1000 + 929;
        String result = TimestampLogger.formatTimestamp(timeMillis);
        assertEquals("01:04:01:929", result);
    }

    @Test
    public void formatTimestamp_complexTime() {
        // 2 hours, 30 minutes, 45 seconds, 123 milliseconds
        long timeMillis = (2 * 3600 + 30 * 60 + 45) * 1000 + 123;
        String result = TimestampLogger.formatTimestamp(timeMillis);
        assertEquals("02:30:45:123", result);
    }

    @Test
    public void formatTimestamp_maxMilliseconds() {
        // Test maximum milliseconds (999)
        long timeMillis = 999;
        String result = TimestampLogger.formatTimestamp(timeMillis);
        assertEquals("00:00:00:999", result);
    }

    @Test
    public void formatTimestamp_maxSeconds() {
        // Test 59 seconds, 999 milliseconds
        long timeMillis = 59 * 1000 + 999;
        String result = TimestampLogger.formatTimestamp(timeMillis);
        assertEquals("00:00:59:999", result);
    }

    @Test
    public void formatTimestamp_maxMinutes() {
        // Test 59 minutes, 59 seconds, 999 milliseconds
        long timeMillis = (59 * 60 + 59) * 1000 + 999;
        String result = TimestampLogger.formatTimestamp(timeMillis);
        assertEquals("00:59:59:999", result);
    }

    @Test
    public void formatTimestamp_maxHours() {
        // Test 23 hours, 59 minutes, 59 seconds, 999 milliseconds
        long timeMillis = (23 * 3600 + 59 * 60 + 59) * 1000 + 999;
        String result = TimestampLogger.formatTimestamp(timeMillis);
        assertEquals("23:59:59:999", result);
    }

    @Test
    public void formatTimestamp_wrapsAfter24Hours() {
        // Test that hours wrap after 24 (modulo 24)
        // 25 hours should show as 01:00:00:000
        long timeMillis = 25L * 3600 * 1000;
        String result = TimestampLogger.formatTimestamp(timeMillis);
        assertEquals("01:00:00:000", result);
    }

    @Test
    public void getCurrentTimestamp_returnsValidFormat() {
        String result = TimestampLogger.getCurrentTimestamp();
        assertNotNull(result);
        // Should match pattern HH:MM:SS:mmm
        assertTrue(result.matches("\\d{2}:\\d{2}:\\d{2}:\\d{3}"));
    }

    @Test
    public void getCurrentTimestamp_returnsReasonableTime() {
        String result = TimestampLogger.getCurrentTimestamp();
        // Just verify it doesn't throw and returns something
        assertNotNull(result);
        assertEquals(15, result.length()); // "HH:MM:SS:mmm" = 15 characters
    }

    @Test
    public void logMethods_doNotThrow() {
        // These tests verify that the logging methods don't throw exceptions
        // We can't easily verify the actual log output without mocking Android's Log class
        
        try {
            TimestampLogger.d("TEST", "Debug message");
            TimestampLogger.i("TEST", "Info message");
            TimestampLogger.w("TEST", "Warning message");
            TimestampLogger.e("TEST", "Error message");
            TimestampLogger.e("TEST", "Error with exception", new RuntimeException("test"));
            assertTrue(true);
        } catch (Exception e) {
            throw new AssertionError("Logging methods should not throw exceptions", e);
        }
    }
}
