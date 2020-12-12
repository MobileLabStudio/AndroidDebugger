package pl.lab.mobile.androiddebuggerlogger.data.model

import org.json.JSONObject

data class LogMessage constructor(
    val type: Type,
    val app: String,
    val message: String,
    val time: Long = System.currentTimeMillis()
) {

    fun toJson(): String {
        val map = mapOf(
            KEY_TYPE to type.name,
            KEY_APP to app,
            KEY_MESSAGE to message,
            KEY_TIME to time.toString()
        )
        return JSONObject(map).toString()
    }

    companion object {

        private const val KEY_TYPE = "type"
        private const val KEY_APP = "app"
        private const val KEY_MESSAGE = "message"
        private const val KEY_TIME = "time"

        fun fromJson(jsonRaw: String?): LogMessage? {
            if (jsonRaw == null) {
                return null
            }
            val json = JSONObject(jsonRaw)
            val type =
                json.getString(KEY_TYPE).runCatching(Type::valueOf).getOrNull() ?: return null
            val appId = json.getString(KEY_APP) ?: return null
            val message = json.getString(KEY_MESSAGE) ?: return null
            val time = json.getLong(KEY_TIME)
            return LogMessage(type, appId, message, time)
        }
    }

    enum class Type {
        INFO,
        WARNING,
        ERROR,
        SUCCESS,
    }
}