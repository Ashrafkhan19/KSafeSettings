package io.github.ashrafkhan19.ksafesettings

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform