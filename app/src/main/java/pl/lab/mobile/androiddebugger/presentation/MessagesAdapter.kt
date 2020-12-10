package pl.lab.mobile.androiddebugger.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import pl.lab.mobile.androiddebugger.R
import pl.lab.mobile.androiddebugger.databinding.ItemMessageBinding
import pl.lab.mobile.androiddebuggerlogger.data.model.LogMessage

class MessagesAdapter : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    private var data = mutableListOf<Message>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(ItemMessageBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    fun submitList(messages: MutableList<Message>) {
        data = messages
        notifyDataSetChanged()
    }

    inner class MessageViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Message) {
            val hasMessage = item.message.isNotBlank()
            binding.idTextView.apply {
                val id = "${item.time} - ${item.app}"
                text = id
                alpha = 0.7f
            }
            binding.messageTextView.text = item.message.takeIf { hasMessage } ?: "no message"
            binding.messageTextView.alpha = if (hasMessage) 1f else 0.3f
            val textColor = when (item.type) {
                LogMessage.Type.INFO -> R.color.log_info
                LogMessage.Type.WARNING -> R.color.log_warning
                LogMessage.Type.ERROR -> R.color.log_error
                LogMessage.Type.SUCCESS -> R.color.log_success
            }.run { ContextCompat.getColor(binding.root.context, this) }
            binding.idTextView.setTextColor(textColor)
            binding.messageTextView.setTextColor(textColor)
        }
    }

    data class Message constructor(
        val type: LogMessage.Type,
        val time: String,
        val app: String,
        val message: String
    )
}