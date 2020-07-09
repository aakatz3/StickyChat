package com.dumbdogdiner.stickychat.data

import com.dumbdogdiner.stickychat.utils.StringUtils
import org.jetbrains.exposed.sql.Table

/**
 * Stores group formats persistently.
 */
object GroupChatFormats : Table(name = StringUtils.formatTableName("groups")) {
    val key = text("key")
    val value = text("value")
    override val primaryKey = PrimaryKey(key)
}