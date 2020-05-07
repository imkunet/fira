package us.kunet.fira.protocol

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import us.kunet.fira.netty.FiraConnection
import java.util.function.BiConsumer

abstract class FiraPacketHandler {
    abstract val packetDirection: PacketDirection
    val handlers: Multimap<Class<out FiraPacket>, BiConsumer<FiraConnection, FiraPacket>> = ArrayListMultimap.create()

    fun <T : FiraPacket> addHandler(packetClass: Class<T>, callback: BiConsumer<FiraConnection, T>) {
        handlers.put(packetClass, callback as BiConsumer<FiraConnection, FiraPacket>)
    }

    fun handlePacket(connection: FiraConnection, packet: FiraPacket, packetClass: Class<out FiraPacket>) {
        for (handler in handlers.get(packetClass)) {
            handler.accept(connection, packet)
        }
    }
}

class FiraClientHandler: FiraPacketHandler() {
    override val packetDirection: PacketDirection = PacketDirection.FROM_SERVER
}

class FiraServerHandler: FiraPacketHandler() {
    override val packetDirection: PacketDirection = PacketDirection.FROM_CLIENT
}