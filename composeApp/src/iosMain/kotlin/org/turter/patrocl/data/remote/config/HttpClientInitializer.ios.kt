package org.turter.patrocl.data.remote.config

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

actual fun getKtorEngine(): HttpClientEngine {
    return Darwin.create()
}