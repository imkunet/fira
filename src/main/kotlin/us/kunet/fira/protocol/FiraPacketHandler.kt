package us.kunet.fira.protocol

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import us.kunet.fira.netty.FiraConnection
import kotlin.reflect.KClass

open class FiraPacketHandler(val packetDirection: PacketDirection) {
    private val handlers: Multimap<KClass<out FiraPacket>, ((FiraConnection, FiraPacket) -> Unit)> =
        ArrayListMultimap.create()

    fun <T : FiraPacket> addHandler(packetClass: KClass<T>, callback: (FiraConnection, T) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        handlers.put(packetClass, callback as (FiraConnection, FiraPacket) -> Unit)
    }

    fun handlePacket(connection: FiraConnection, packet: FiraPacket, packetClass: KClass<out FiraPacket>) {
        for (handler in handlers.get(packetClass)) {
            handler.invoke(connection, packet)
        }
    }
}

inline fun <reified T : FiraPacket> FiraPacketHandler.addHandler(noinline callback: (FiraConnection, T) -> Unit) {
    addHandler(T::class, callback)
}

class FiraClientHandler : FiraPacketHandler(PacketDirection.FROM_SERVER)

class FiraServerHandler : FiraPacketHandler(PacketDirection.FROM_CLIENT)