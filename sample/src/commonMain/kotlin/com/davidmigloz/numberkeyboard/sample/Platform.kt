package com.davidmigloz.numberkeyboard.sample

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform