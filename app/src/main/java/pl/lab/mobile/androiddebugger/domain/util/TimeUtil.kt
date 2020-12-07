package pl.lab.mobile.androiddebugger.domain.util

import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {

    private val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ROOT)

    fun format(timestamp: Long): String = simpleDateFormat.format(Date(timestamp))
}