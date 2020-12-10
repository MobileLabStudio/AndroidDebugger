package pl.lab.mobile.androiddebuggerlogger.data.model

data class LogMessage constructor(
    val type: Type,
    val app: String,
    val message: String,
    val time: Long = System.currentTimeMillis()
) {

    fun toMap(): Map<String, String> {
        return mapOf(
            KEY_TYPE to type.name,
            KEY_APP to app,
            KEY_MESSAGE to message,
            KEY_TIME to time.toString()
        )
    }

    companion object {

        private const val KEY_TYPE = "type"
        private const val KEY_APP = "app"
        private const val KEY_MESSAGE = "message"
        private const val KEY_TIME = "time"


        fun fromMap(json: Map<String, String>?): LogMessage? {
            if (json == null) {
                return null
            }
            val type = json[KEY_TYPE]?.runCatching(Type::valueOf)?.getOrNull() ?: return null
            val appId = json[KEY_APP] ?: return null
            val message = json[KEY_MESSAGE] ?: return null
            val time = json[KEY_TIME]?.toLongOrNull() ?: return null
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