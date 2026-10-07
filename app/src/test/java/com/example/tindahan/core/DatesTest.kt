package com.example.tindahan.core

import java.time.Instant
import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Test

class DatesTest {
    @Test fun startOfDayInUtc() {
        val now = Instant.parse("2026-10-07T15:30:45.123Z").toEpochMilli()
        val expected = Instant.parse("2026-10-07T00:00:00Z").toEpochMilli()
        assertEquals(expected, Dates.startOfDay(now, TimeZone.getTimeZone("UTC")))
    }

    @Test fun startOfDayUsesLocalCalendarDay() {
        // 20:00Z on Oct 6 is already 04:00 on Oct 7 in Manila (UTC+8).
        val now = Instant.parse("2026-10-06T20:00:00Z").toEpochMilli()
        val expected = Instant.parse("2026-10-06T16:00:00Z").toEpochMilli() // Oct 7 00:00 Manila
        assertEquals(expected, Dates.startOfDay(now, TimeZone.getTimeZone("Asia/Manila")))
    }
}
