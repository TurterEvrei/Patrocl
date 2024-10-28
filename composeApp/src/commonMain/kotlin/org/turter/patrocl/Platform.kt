package org.turter.patrocl

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform