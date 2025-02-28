package com.example.proyectocita

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// data class Message(val text: String, val isBot: Boolean)

class MessageAdapter(private val messages: List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_BOT = 0
        private const val VIEW_TYPE_USER = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isBot) VIEW_TYPE_BOT else VIEW_TYPE_USER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_BOT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_bot, parent, false)
            BotMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_user, parent, false)
            UserMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is BotMessageViewHolder) {
            holder.bind(message)
        } else if (holder is UserMessageViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    class BotMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textViewBotMessage)
        fun bind(message: Message) {
            textView.text = message.text
        }
    }

    class UserMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textViewUserMessage)
        fun bind(message: Message) {
            textView.text = message.text
        }
    }
}