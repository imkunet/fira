package us.kunet.fira.protocol

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import java.util.*

class FiraPacketRegistry {
    private val serverRegistry: Table<ProtocolState, Int, Class<out FiraPacket>> = HashBasedTable.create()
    private val clientRegistry: Table<ProtocolState, Int, Class<out FiraPacket>> = HashBasedTable.create()

    fun register(direction: PacketDirection, state: ProtocolState, packetId: Int, packetClass: Class<out FiraPacket>) {
        if (direction == PacketDirection.FROM_CLIENT) {
            clientRegistry.put(state, packetId, packetClass)
        } else {
            serverRegistry.put(state, packetId, packetClass)
        }
    }

    fun getPacket(direction: PacketDirection, state: ProtocolState, packetId: Int): Class<out FiraPacket>? {
        return if (direction == PacketDirection.FROM_CLIENT) {
            clientRegistry.get(state, packetId)
        } else {
            serverRegistry.get(state, packetId)
        }
    }

    fun fillInfo(packet: FiraPacket) {
        var cell = serverRegistry.cellSet().stream().filter { Objects.equals(it.value, packet.javaClass) }.findFirst()
        if (!cell.isPresent) {
            cell = clientRegistry.cellSet().stream().filter { Objects.equals(it.value, packet.javaClass) }.findFirst()
        }
        cell.ifPresent {
            packet.id = it.columnKey!!
            packet.state = it.rowKey!!
        }
    }
}