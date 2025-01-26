package org.turter.patrocl.data.remote.config

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO

actual fun getKtorEngine(): HttpClientEngine {
    return CIO.create()
}