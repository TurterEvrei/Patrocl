package org.turter.patrocl.data.service

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.turter.patrocl.domain.model.Message
import org.turter.patrocl.domain.service.MessageService

@OptIn(FlowPreview::class)
class MessageServiceImpl : MessageService {
    private val log = Logger.withTag("MessageServiceImpl")

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val messageDelay = 3000L

    private val messageChannel = Channel<Message<String>>(capacity = Channel.UNLIMITED)

    private val _messageStateFlow = MutableStateFlow<Message<String>>(Message.initial())

    init {
        coroutineScope.launch {
            messageChannel
                .consumeAsFlow()
                .debounce(messageDelay)
                .collect { message ->
                    log.d { "Update message state flow. Message: $message" }
                    _messageStateFlow.emit(message)
                }
        }
    }

    override fun getMessageStateFlow(): StateFlow<Message<String>> =
        _messageStateFlow.asStateFlow()

    override suspend fun setMessage(message: Message<String>) {
        log.d { "Send new message: $message" }
        messageChannel.send(message)
    }
}