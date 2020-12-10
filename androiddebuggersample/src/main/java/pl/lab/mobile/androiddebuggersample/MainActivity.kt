package pl.lab.mobile.androiddebuggersample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pl.lab.mobile.androiddebuggerlogger.domain.logger.Logger
import pl.lab.mobile.androiddebuggersample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logInfoButton.setOnClickListener {
            Logger.logInfo(getLogMessage())
        }

        binding.logWarningButton.setOnClickListener {
            Logger.logWarning(getLogMessage())
        }

        binding.logErrorButton.setOnClickListener {
            Logger.logError(getLogMessage())
        }

        binding.logSuccessButton.setOnClickListener {
            Logger.logSuccess(getLogMessage())
        }

        Logger.start("Android Debugger Sample App", application)
    }

    private fun getLogMessage() = binding.messageEditText.text.toString() ?: ""

    override fun onDestroy() {
        super.onDestroy()
        Logger.stop(application)
    }
}