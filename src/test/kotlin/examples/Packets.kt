package examples

import com.google.common.base.MoreObjects
import io.netty.buffer.ByteBuf
import us.kunet.fira.protocol.*

fun createPacketRegistry(): FiraPacketRegistry {
    val registry = FiraPacketRegistry()

    registry.register(PacketDirection.FROM_CLIENT, ProtocolState.HANDSHAKE, 0x00, Handshake::class.java)

    registry.register(PacketDirection.FROM_CLIENT, ProtocolState.STATUS, 0x00, StatusRequest::class.java)
    registry.register(PacketDirection.FROM_CLIENT, ProtocolState.STATUS, 0x01, Ping::class.java)

    registry.register(PacketDirection.FROM_SERVER, ProtocolState.STATUS, 0x00, Status::class.java)
    registry.register(PacketDirection.FROM_SERVER, ProtocolState.STATUS, 0x01, Pong::class.java)

    registry.register(PacketDirection.FROM_SERVER, ProtocolState.LOGIN, 0x00, Disconnect::class.java)

    return registry
}

class Handshake: FiraPacket() {
    var version: Int = -1
    var address: String = ""
    var port: Int = 0
    var nextState: Int = 0

    override fun toWire(buf: ByteBuf) {
        buf.writeVarInt(version)
        buf.writeUTF8(address)
        buf.writeShort(port)
        buf.writeVarInt(nextState)
    }

    override fun fromWire(buf: ByteBuf) {
        version = buf.readVarInt()
        address = buf.readUTF8()
        port = buf.readUnsignedShort()
        nextState = buf.readVarInt()
    }

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
            .add("version", version)
            .add("address", address)
            .add("port", port)
            .add("nextState", nextState)
            .toString()
    }
}

class StatusRequest: FiraPacket() {
    override fun toWire(buf: ByteBuf) {}

    override fun fromWire(buf: ByteBuf) {}

    override fun toString(): String {
        return MoreObjects.toStringHelper(this).toString()
    }
}

class Status: FiraPacket() {
    var response: String = ""

    override fun toWire(buf: ByteBuf) {
        buf.writeUTF8(response)
    }

    override fun fromWire(buf: ByteBuf) {
        response = buf.readUTF8()
    }

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
            .add("response", response)
            .toString()
    }
}

class Ping: FiraPacket() {
    var time: Long = 0L

    override fun toWire(buf: ByteBuf) {}

    override fun fromWire(buf: ByteBuf) {
        time = buf.readLong()
    }

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
            .add("time", time)
            .toString()
    }

}

class Pong: FiraPacket() {
    var time: Long = 0L

    override fun toWire(buf: ByteBuf) {
        buf.writeLong(time)
    }

    override fun fromWire(buf: ByteBuf) {}

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
            .add("time", time)
            .toString()
    }
}

class Disconnect: FiraPacket() {
    var message: String = ""

    override fun toWire(buf: ByteBuf) {
        buf.writeUTF8(message)
    }

    override fun fromWire(buf: ByteBuf) {
        message = buf.readUTF8()
    }

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
            .add("message", message)
            .toString()
    }

}