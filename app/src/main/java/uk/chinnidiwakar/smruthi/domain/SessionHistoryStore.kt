package uk.chinnidiwakar.smruthi.domain

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class StoredSession(
    val timestampMs: Long,
    val summary: SessionSummary,
    val protocol: String
)

data class DayTrend(
    val dayLabel: String,
    val avgHitRate: Float,
    val avgFalseAlarmRate: Float,
    val avgHitRtMs: Long
)

class SessionHistoryStore(context: Context) {
    private val prefs = context.getSharedPreferences("smruthi_history", Context.MODE_PRIVATE)
    private val key = "sessions_json"

    fun addSession(summary: SessionSummary, protocol: String) {
        val all = getAllMutable()
        all.put(
            JSONObject()
                .put("timestampMs", System.currentTimeMillis())
                .put("protocol", protocol)
                .put("nLevel", summary.nLevel)
                .put("hits", summary.hits)
                .put("misses", summary.misses)
                .put("falseAlarms", summary.falseAlarms)
                .put("correctRejections", summary.correctRejections)
                .put("avgHitRt", summary.averageHitReactionTimeMs)
                .put("avgFaRt", summary.averageFalseAlarmReactionTimeMs)
        )
        prefs.edit().putString(key, all.toString()).apply()
    }

    fun getRecentDays(days: Int = 7): List<StoredSession> {
        val now = System.currentTimeMillis()
        val cutoff = now - (days * 24L * 60L * 60L * 1000L)
        return getAll().filter { it.timestampMs >= cutoff }
    }

    fun getWeeklyTrends(): List<DayTrend> {
        val byDay = linkedMapOf<String, MutableList<StoredSession>>()
        val formatter = SimpleDateFormat("EEE", Locale.US)

        for (i in 6 downTo 0) {
            val cal = Calendar.getInstance().apply { add(Calendar.DATE, -i) }
            val label = formatter.format(cal.time)
            byDay[label] = mutableListOf()
        }

        getRecentDays(7).forEach { session ->
            val label = formatter.format(Date(session.timestampMs))
            byDay[label]?.add(session)
        }

        return byDay.map { (label, sessions) ->
            if (sessions.isEmpty()) {
                DayTrend(label, 0f, 0f, 0)
            } else {
                val hitRate = sessions.map {
                    val t = it.summary.hits + it.summary.misses
                    if (t > 0) it.summary.hits.toFloat() / t else 0f
                }.average().toFloat()

                val faRate = sessions.map {
                    val t = it.summary.falseAlarms + it.summary.correctRejections
                    if (t > 0) it.summary.falseAlarms.toFloat() / t else 0f
                }.average().toFloat()

                val rt = sessions.map { it.summary.averageHitReactionTimeMs }.average().toLong()
                DayTrend(label, hitRate, faRate, rt)
            }
        }
    }

    fun exportAsJson(days: Int = 30): String {
        val data = JSONArray()
        getRecentDays(days).forEach {
            data.put(
                JSONObject()
                    .put("timestampMs", it.timestampMs)
                    .put("protocol", it.protocol)
                    .put("nLevel", it.summary.nLevel)
                    .put("hits", it.summary.hits)
                    .put("misses", it.summary.misses)
                    .put("falseAlarms", it.summary.falseAlarms)
                    .put("correctRejections", it.summary.correctRejections)
                    .put("averageHitReactionTimeMs", it.summary.averageHitReactionTimeMs)
                    .put("averageFalseAlarmReactionTimeMs", it.summary.averageFalseAlarmReactionTimeMs)
            )
        }
        return data.toString(2)
    }

    fun exportAsCsv(days: Int = 30): String {
        val header = "timestamp_ms,protocol,n_level,hits,misses,false_alarms,correct_rejections,avg_hit_rt_ms,avg_false_alarm_rt_ms"
        val rows = getRecentDays(days).joinToString("\n") {
            listOf(
                it.timestampMs,
                it.protocol,
                it.summary.nLevel,
                it.summary.hits,
                it.summary.misses,
                it.summary.falseAlarms,
                it.summary.correctRejections,
                it.summary.averageHitReactionTimeMs,
                it.summary.averageFalseAlarmReactionTimeMs
            ).joinToString(",")
        }
        return "$header\n$rows"
    }

    private fun getAllMutable(): JSONArray {
        val raw = prefs.getString(key, "[]") ?: "[]"
        return JSONArray(raw)
    }

    private fun getAll(): List<StoredSession> {
        val array = getAllMutable()
        val list = mutableListOf<StoredSession>()
        for (i in 0 until array.length()) {
            val o = array.getJSONObject(i)
            list.add(
                StoredSession(
                    timestampMs = o.optLong("timestampMs"),
                    protocol = o.optString("protocol", "unknown"),
                    summary = SessionSummary(
                        nLevel = o.optInt("nLevel", 1),
                        hits = o.optInt("hits", 0),
                        misses = o.optInt("misses", 0),
                        falseAlarms = o.optInt("falseAlarms", 0),
                        correctRejections = o.optInt("correctRejections", 0),
                        averageHitReactionTimeMs = o.optLong("avgHitRt", 0),
                        averageFalseAlarmReactionTimeMs = o.optLong("avgFaRt", 0)
                    )
                )
            )
        }
        return list.sortedBy { it.timestampMs }
    }
}
