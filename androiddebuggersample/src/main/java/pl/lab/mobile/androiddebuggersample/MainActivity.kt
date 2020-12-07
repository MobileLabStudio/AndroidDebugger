package pl.lab.mobile.androiddebuggersample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import pl.lab.mobile.androiddebuggerlogger.data.model.LogMessage
import pl.lab.mobile.androiddebuggerlogger.domain.logger.Logger

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.log_message_button).setOnClickListener {
            val content = findViewById<EditText>(R.id.message_edit_text)
                .text
                ?.toString()
                ?: return@setOnClickListener
            val message =
                LogMessage(
                    LogMessage.Type.INFO,
                    "sample app",
                    content,
                    System.currentTimeMillis()
                )
            Logger.log(message)
        }
    }

    override fun onStart() {
        super.onStart()
        Logger.start(this)
    }

    override fun onStop() {
        super.onStop()
        Logger.stop(this)
    }
}