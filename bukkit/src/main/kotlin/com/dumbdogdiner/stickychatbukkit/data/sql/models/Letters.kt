package com.dumbdogdiner.stickychatbukkit.data.sql.models

import com.dumbdogdiner.stickychatbukkit.utils.FormatUtils
import org.jetbrains.exposed.sql.Table

/**
 * Table for storing sent mail messages.
 */
object Letters : Table(name = FormatUtils.formatTableName("letters")) {
    val id = integer("id").autoIncrement()

    val fromUuid = varchar("fromUuid", 36)
    val fromName = varchar("fromName", 16)
    val toUuid = varchar("toUuid", 36).nullable()
    val toName = varchar("toName", 16)

    val title = text("title")
    val pages = text("pages")

    val createdAt = long("createdAt")

    val unread = bool("unread")

    override val primaryKey = PrimaryKey(id)
}
