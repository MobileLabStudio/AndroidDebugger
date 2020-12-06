package pl.lab.mobile.androiddebuggerlogger.data.model

data class LogMessage(
    val type: Type,
    val appId: String,
    val message: String,
    val time: Long
) {

    fun toMap(): Map<String, String> {
        return mapOf(
            "type" to type.name,
            "appId" to appId,
            "message" to message,
            "time" to time.toString()
        )
    }

    companion object {

        fun fromMap(json: Map<String, String>?): LogMessage? {
            if (json == null) {
                return null
            }
            val type = json["type"]?.runCatching(Type::valueOf)?.getOrNull() ?: return null
            val appId = json["appId"] ?: return null
            val message = json["message"] ?: return null
            val time = json["time"]?.toLongOrNull() ?: return null
            return LogMessage(type, appId, message, time)
        }
    }

    enum class Type {
        INFO,
        WARNING,
        ERROR
    }
}