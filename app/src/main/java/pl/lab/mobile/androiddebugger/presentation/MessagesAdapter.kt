package pl.lab.mobile.androiddebugger.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.lab.mobile.androiddebugger.R
import pl.lab.mobile.androiddebugger.databinding.ItemMessageBinding
import pl.lab.mobile.androiddebuggerlogger.data.model.LogMessage

class MessagesAdapter :
    ListAdapter<MessagesAdapter.Message, MessagesAdapter.MessageViewHolder>(DIFFER) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(ItemMessageBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFFER
            get() = object : DiffUtil.ItemCallback<Message>() {
                override fun areItemsTheSame(
                    oldItem: Message,
                    newItem: Message
                ): Boolean =
                    oldItem.time == newItem.time

                override fun areContentsTheSame(
                    oldItem: Message,
                    newItem: Message
                ): Boolean =
                    oldItem == newItem
            }
    }

    inner class MessageViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Message) {
            binding.timestampTextView.text = item.time
            binding.messageTextView.text = item.message
            val textColor = when (item.type) {
                LogMessage.Type.INFO -> R.color.log_info
                LogMessage.Type.WARNING -> R.color.log_warning
                LogMessage.Type.ERROR -> R.color.log_error
            }.run { ContextCompat.getColor(binding.root.context, this) }
            binding.timestampTextView.setTextColor(textColor)
            binding.messageTextView.setTextColor(textColor)
        }
    }

    data class Message(val type: LogMessage.Type, val time: String, val message: String)
}