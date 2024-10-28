package org.turter.patrocl.data.client

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.engine.darwin.DarwinClientEngineConfig

actual fun getKtorEngine(): HttpClientEngine {
    return Darwin.create()
}