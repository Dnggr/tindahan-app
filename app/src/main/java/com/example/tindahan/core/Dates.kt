package com.example.tindahan.core

import java.util.Calendar
import java.util.TimeZone

object Dates {
    /** Epoch millis of 00:00:00.000 on the same calendar day as [millis], in [zone]. */
    fun startOfDay(millis: Long, zone: TimeZone = TimeZone.getDefault()): Long {
        val c = Calendar.getInstance(zone)
        c.timeInMillis = millis
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }
}
