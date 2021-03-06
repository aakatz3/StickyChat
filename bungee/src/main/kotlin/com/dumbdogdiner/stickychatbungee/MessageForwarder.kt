package com.dumbdogdiner.stickychatbungee

import com.dumbdogdiner.stickychatcommon.Constants
import com.dumbdogdiner.stickychatcommon.MessageHandler
import com.dumbdogdiner.stickychatcommon.MessageType
import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

/**
 * Forwards messages from a single node to other server nodes.
 */
object MessageForwarder : Base, Listener, MessageHandler {

    private const val CHANNEL_NAME = Constants.CHANNEL_NAME

    /**
     * Handle a plugin message received from a server instance.
     */
    @EventHandler
    fun onPluginMessageReceived(ev: PluginMessageEvent) {
        if (ev.tag != CHANNEL_NAME) {
            return
        }

        logger.info("Received message from node - decoding")
        handlePacket(ByteStreams.newDataInput(ev.data))
    }

    override fun handleMessage(data: ByteArrayDataInput) {
        logger.info("Got message packet - broadcasting to nodes")

        // this is dumb
        val uuid = data.readUTF()
        val name = data.readUTF()
        val content = data.readUTF()

        val msg = build(MessageType.MESSAGE)
        msg.writeUTF(uuid)
        msg.writeUTF(name)
        msg.writeUTF(content)

        sendPluginMessage(msg)
    }

    override fun handlePrivateMessage(data: ByteArrayDataInput) {
        logger.info("Got private message packet - attempting to forward to targeted player")
        val uuid = data.readUTF()
        val name = data.readUTF()
        val content = data.readUTF()
        val nonce = data.readInt()

        // if the player is not online anywhere, send the error back to the node.
        val target = proxy.players.find { it.name == name }
        if (target == null) {
            logger.warning("Could not find specified player")
            val msg = build(MessageType.PRIVATE_MESSAGE_ERROR)
            msg.writeUTF(uuid)
            msg.writeShort(nonce)
            return sendTargetedPluginMessage(uuid, msg)
        }

        val msg = build(MessageType.PRIVATE_MESSAGE)
        msg.writeUTF(uuid)
        msg.writeUTF(name)
        msg.writeUTF(content)
        msg.writeShort(nonce)

        sendTargetedPluginMessage(target, msg)
    }

    override fun handlePrivateMessageAck(data: ByteArrayDataInput) {
        logger.info("Got private message ACK packet - attempting to forward to targeted player")

        val uuid = data.readUTF()
        val nonce = data.readInt()

        val msg = build(MessageType.PRIVATE_MESSAGE_ACK)
        msg.writeUTF(uuid)
        msg.writeInt(nonce)

        sendTargetedPluginMessage(uuid, msg)
    }

    override fun handlePrivateMessageError(data: ByteArrayDataInput) {
        return
    }

    override fun handleMailReceive(data: ByteArrayDataInput) {
        logger.info("Got mail received packet - broadcasting to nodes")

        val uuid = data.readUTF()
        val name = data.readUTF()
        val to = data.readUTF()
        val content = data.readUTF()

        val msg = build(MessageType.MAIL)
        msg.writeUTF(uuid)
        msg.writeUTF(name)
        msg.writeUTF(to)
        msg.writeUTF(content)

        sendPluginMessage(msg)
    }

    /**
     * Sends a global plugin message to all servers - not guaranteed to be received on
     * all servers.
     */
    override fun sendPluginMessage(data: ByteArrayDataOutput) {
        proxy.servers.values.forEach {
            if (it.players.isEmpty()) {
                logger.warning("Could not forward message to server '${it.name}' - no players are online")
            } else {
                val player = it.players.first()
                player.sendData(CHANNEL_NAME, data.toByteArray())
                logger.info("Sent packet to server '${it.name}' via proxied player '${player.name}'")
            }
        }
    }

    /**
     * Send a plugin message to the player with the specified id - not guaranteed to be
     * received.
     */
    override fun sendTargetedPluginMessage(uuid: String, data: ByteArrayDataOutput) {
        for (server in proxy.servers.values) {
            for (player in server.players) {
                if (player.uniqueId.toString() == uuid) {
                    sendTargetedPluginMessage(player, data)
                    return
                }
            }
        }
        logger.warning("Failed to find proxied player with uuid '$uuid'")
    }

    /**
     * SEnd a plugin message to the targeted player.
     */
    fun sendTargetedPluginMessage(player: ProxiedPlayer, data: ByteArrayDataOutput) {
        player.sendData(CHANNEL_NAME, data.toByteArray())
        logger.info("Sent packet to proxied player '${player.name}'")
    }

    /**
     * Send a plugin message to the player with the specified name - not guaranteed to be received.
     */
    fun sendNamedPluginMessage(name: String, data: ByteArrayDataOutput) {
        for (server in proxy.servers.values) {
            for (player in server.players) {
                if (player.name == name) {
                    player.sendData(CHANNEL_NAME, data.toByteArray())
                }
            }
        }
        logger.warning("Failed to find proxied player with name '$name'")
    }
}
