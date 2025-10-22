package com.f1calendar.util

import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object DateTimeUtil {
    private val ukZone = ZoneId.of("Europe/London")

    fun formatToUKTime(date: String, time: String?): String {
        return try {
            if (time == null) return date

            // Parse the ISO date and time
            val dateTime = ZonedDateTime.parse("${date}T${time}")

            // Convert to UK time
            val ukTime = dateTime.withZoneSameInstant(ukZone)

            // Format the output
            val formatter = DateTimeFormatter.ofPattern("EEE d MMM, HH:mm 'BST'")
            ukTime.format(formatter)
        } catch (e: Exception) {
            "$date ${time ?: ""}"
        }
    }

    fun formatDate(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.UK)
            val outputFormat = SimpleDateFormat("d MMM yyyy", Locale.UK)
            val parsedDate = inputFormat.parse(date)
            parsedDate?.let { outputFormat.format(it) } ?: date
        } catch (e: Exception) {
            date
        }
    }

    fun isRaceCompleted(raceDate: String): Boolean {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.UK)
            val date = format.parse(raceDate)
            date?.before(Date()) ?: false
        } catch (e: Exception) {
            false
        }
    }

    fun formatDateRange(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.UK)
            val parsedDate = inputFormat.parse(date) ?: return date

            val calendar = Calendar.getInstance().apply {
                time = parsedDate
            }

            // Get Friday (2 days before Sunday race)
            val friday = calendar.clone() as Calendar
            friday.add(Calendar.DAY_OF_MONTH, -2)

            val outputFormat = SimpleDateFormat("d", Locale.UK)
            val monthFormat = SimpleDateFormat("MMM", Locale.UK)

            val fridayDay = outputFormat.format(friday.time)
            val sundayDay = outputFormat.format(calendar.time)
            val month = monthFormat.format(calendar.time)

            "$fridayDay-$sundayDay $month"
        } catch (e: Exception) {
            date
        }
    }
}
