package us.kunet.fira.protocol

import io.netty.buffer.ByteBuf

abstract class FiraPacket {
    lateinit var direction: PacketDirection
    lateinit var state: ProtocolState
    var id: Int = 0x00

    abstract fun toWire(buf: ByteBuf)
    abstract fun fromWire(buf: ByteBuf)
    abstract override fun toString(): String
}