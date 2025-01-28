package org.turter.patrocl.domain.service

import kotlinx.coroutines.flow.StateFlow
import org.turter.patrocl.domain.model.Message

interface MessageService {
    fun getMessageStateFlow(): StateFlow<Message<String>>

    //    fun getRemoteSourceStatus(): StateFlow<RemoteSourceStatus>
//    suspend fun checkRemoteMessageSrc()
//    suspend fun resetRemoteMessageSrc()
    suspend fun setMessage(message: Message<String>)
}