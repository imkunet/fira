package us.kunet.fira.protocol

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance

open class FiraPacketRegistry {
    private val serverRegistry: Table<ProtocolState, Int, KClass<out FiraPacket>> = HashBasedTable.create()
    private val clientRegistry: Table<ProtocolState, Int, KClass<out FiraPacket>> = HashBasedTable.create()

    fun register(packetClass: KClass<out FiraPacket>) {
        (packetClass.companionObjectInstance as FiraPacket.Companion<*>).also { companion ->
            if (companion.direction == PacketDirection.FROM_CLIENT) {
                clientRegistry.put(companion.state, companion.id, packetClass)
            } else {
                serverRegistry.put(companion.state, companion.id, packetClass)
            }
        }
    }

    fun getPacket(direction: PacketDirection, state: ProtocolState, packetId: Int): KClass<out FiraPacket>? {
        return if (direction == PacketDirection.FROM_CLIENT) {
            clientRegistry.get(state, packetId)
        } else {
            serverRegistry.get(state, packetId)
        }
    }

    fun fillInfo(packet: FiraPacket) {
        var cell = serverRegistry.cellSet().stream().filter { Objects.equals(it.value, packet::class) }.findFirst()
        if (!cell.isPresent) {
            cell = clientRegistry.cellSet().stream().filter { Objects.equals(it.value, packet::class) }.findFirst()
        }
        cell.ifPresent {
            @Suppress("UNCHECKED_CAST")
            (packet::class.companionObjectInstance as FiraPacket.Companion<FiraPacket>).also { companion ->
                companion.id = it.columnKey!!
                companion.state = it.rowKey!!
            }
        }
    }
}

inline fun <reified T : FiraPacket> FiraPacketRegistry.register() {
    register(T::class)
}