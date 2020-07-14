package us.kunet.fira.protocol

import io.netty.buffer.ByteBuf

interface FiraPacket {
    abstract class Companion<T : FiraPacket>(var id: Int, var state: ProtocolState, val direction: PacketDirection) {
        abstract fun fromWire(buf: ByteBuf): T
        abstract fun T.toWire(buf: ByteBuf)
    }
}
