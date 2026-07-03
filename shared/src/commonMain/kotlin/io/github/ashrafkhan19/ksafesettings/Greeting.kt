package io.github.ashrafkhan19.ksafesettings

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return sayHello(platform.name)
    }
}