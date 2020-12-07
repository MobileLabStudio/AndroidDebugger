package pl.lab.mobile.androiddebugger.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import pl.lab.mobile.androiddebugger.domain.util.TimeUtil
import pl.lab.mobile.androiddebuggerlogger.data.model.LogMessage
import java.util.concurrent.TimeUnit

class MainViewModel : ViewModel() {

    private val _messages = MutableLiveData<MutableList<MessagesAdapter.Message>>()
    val messages: LiveData<MutableList<MessagesAdapter.Message>>
        get() = _messages

    private val newMessagePublisher = PublishSubject.create<LogMessage>()
    private val newMessageConsumer = newMessagePublisher.toFlowable(BackpressureStrategy.BUFFER)
        .debounce(1, TimeUnit.SECONDS)
        .subscribeOn(Schedulers.computation())
        .observeOn(Schedulers.computation())
        .map {
            return@map MessagesAdapter.Message(
                type = it.type,
                time = TimeUtil.format(it.time),
                message = it.message
            )
        }
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext {
            val messages = _messages.value?.apply { add(it) } ?: mutableListOf(it)
            _messages.postValue(messages)
        }
        .subscribe()

    fun addMessage(message: LogMessage) {
        newMessagePublisher.onNext(message)
    }

    override fun onCleared() {
        super.onCleared()
        newMessageConsumer.dispose()
    }
}