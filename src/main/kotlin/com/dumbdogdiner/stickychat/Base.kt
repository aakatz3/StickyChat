package com.dumbdogdiner.stickychat

/**
 * this is hot~
 * will always love jcx for this idea
 */
interface Base {
    val plugin
        get() = StickyChatPlugin.instance

    val config
        get() = plugin.config

    val logger
        get() = plugin.logger
}
