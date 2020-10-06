package com.dumbdogdiner.stickychatbukkit.data.h2

import com.dumbdogdiner.stickychatbukkit.data.Letter
import com.dumbdogdiner.stickychatbukkit.data.StorageMethod
import org.bukkit.entity.Player

/**
 * Stores data locally using H2.
 */
class H2Method : StorageMethod {
    override fun init() {
        TODO("Not yet implemented")
    }

    override fun getPlayerNickname(player: Player): String? {
        TODO("Not yet implemented")
    }

    override fun setPlayerNickname(player: Player, new: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun clearPlayerNickname(player: Player): Boolean {
        TODO("Not yet implemented")
    }

    override fun savePartialLetter(from: Player, toName: String, title: String, pages: List<String>, createdAt: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun hydratePartialLetters(to: Player) {
        TODO("Not yet implemented")
    }

    override fun getLetter(id: Int): Letter? {
        TODO("Not yet implemented")
    }

    override fun fetchLettersForPlayer(player: Player): List<Letter> {
        TODO("Not yet implemented")
    }

    override fun fetchLettersForPlayer(player: Player, filterUnread: Boolean): List<Letter> {
        TODO("Not yet implemented")
    }
}