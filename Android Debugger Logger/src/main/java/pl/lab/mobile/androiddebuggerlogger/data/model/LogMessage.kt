package pl.lab.mobile.androiddebuggerlogger.data.model

data class LogMessage(val type: Type, val appId: String, val message: String) {

    internal val time = System.currentTimeMillis()

    enum class Type {
        INFO,
        WARNING,
        ERROR
    }
}