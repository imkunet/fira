package examples

import io.netty.buffer.ByteBuf
import us.kunet.fira.protocol.*
import java.util.*

fun createPacketRegistry() = FiraPacketRegistry().apply {
    register<ClientHandshakePacket>()

    register<ClientRequestPacket>()
    register<ClientPingPacket>()

    register<ServerResponsePacket>()
    register<ServerPongPacket>()

    register<ClientLoginStartPacket>()

    register<ServerDisconnectPacket>()
    register<ServerLoginSuccessPacket>()
}

class ClientHandshakePacket(
        val protocolVersion: Int,
        val serverAddress: String,
        val serverPort: Int,
        val nextState: ProtocolState
) : FiraPacket {
    companion object : FiraPacket.Companion<ClientHandshakePacket>(
            0x00,
            ProtocolState.HANDSHAKE,
            PacketDirection.FROM_CLIENT
    ) {
        override fun fromWire(buf: ByteBuf): ClientHandshakePacket {
            return ClientHandshakePacket(
                    buf.readVarInt(),
                    buf.readUTF8(),
                    buf.readUnsignedShort(),
                    ProtocolState.fromId(buf.readVarInt())
            )
        }

        override fun ClientHandshakePacket.toWire(buf: ByteBuf) {
            buf.writeVarInt(protocolVersion)
            buf.writeUTF8(serverAddress)
            buf.writeShort(serverPort)
            buf.writeVarInt(nextState.ordinal)
        }
    }
}

class ClientRequestPacket : FiraPacket {
    companion object : FiraPacket.Companion<ClientRequestPacket>(
            0x00,
            ProtocolState.STATUS,
            PacketDirection.FROM_CLIENT
    ) {
        override fun fromWire(buf: ByteBuf): ClientRequestPacket {
            return ClientRequestPacket()
        }

        override fun ClientRequestPacket.toWire(buf: ByteBuf) {}
    }
}

class ClientPingPacket(
        val payload: Long
) : FiraPacket {
    companion object : FiraPacket.Companion<ClientPingPacket>(
            0x01,
            ProtocolState.STATUS,
            PacketDirection.FROM_CLIENT
    ) {
        override fun fromWire(buf: ByteBuf): ClientPingPacket {
            return ClientPingPacket(
                    buf.readLong()
            )
        }

        override fun ClientPingPacket.toWire(buf: ByteBuf) {
            buf.writeLong(payload)
        }
    }
}

class ServerResponsePacket(
        val jsonResponse: String
) : FiraPacket {
    companion object : FiraPacket.Companion<ServerResponsePacket>(
            0x00,
            ProtocolState.STATUS,
            PacketDirection.FROM_SERVER
    ) {
        override fun fromWire(buf: ByteBuf): ServerResponsePacket {
            val readUTF8 = buf.readUTF8()
            return ServerResponsePacket(readUTF8)
        }

        override fun ServerResponsePacket.toWire(buf: ByteBuf) {
            buf.writeUTF8(jsonResponse)
        }
    }
}

class ServerPongPacket(
        val payload: Long
) : FiraPacket {
    companion object : FiraPacket.Companion<ServerPongPacket>(
            0x01,
            ProtocolState.STATUS,
            PacketDirection.FROM_SERVER
    ) {
        override fun fromWire(buf: ByteBuf): ServerPongPacket {
            return ServerPongPacket(
                    buf.readLong()
            )
        }

        override fun ServerPongPacket.toWire(buf: ByteBuf) {
            buf.writeLong(payload)
        }
    }
}

class ClientLoginStartPacket(
        val name: String
) : FiraPacket {
    companion object : FiraPacket.Companion<ClientLoginStartPacket>(
            0x00,
            ProtocolState.LOGIN,
            PacketDirection.FROM_CLIENT
    ) {
        override fun fromWire(buf: ByteBuf): ClientLoginStartPacket {
            return ClientLoginStartPacket(
                    buf.readUTF8()
            )
        }

        override fun ClientLoginStartPacket.toWire(buf: ByteBuf) {
            buf.writeUTF8(name)
        }
    }
}

class ServerDisconnectPacket(
        val reason: String
) : FiraPacket {
    companion object : FiraPacket.Companion<ServerDisconnectPacket>(
            0x00,
            ProtocolState.LOGIN,
            PacketDirection.FROM_SERVER
    ) {
        override fun fromWire(buf: ByteBuf): ServerDisconnectPacket {
            return ServerDisconnectPacket(buf.readUTF8())
        }

        override fun ServerDisconnectPacket.toWire(buf: ByteBuf) {
            buf.writeUTF8(reason)
        }
    }
}

class ServerLoginSuccessPacket(
        val uuid: UUID,
        val username: String
) : FiraPacket {
    companion object : FiraPacket.Companion<ServerLoginSuccessPacket>(
            0x02,
            ProtocolState.LOGIN,
            PacketDirection.FROM_SERVER
    ) {
        override fun fromWire(buf: ByteBuf): ServerLoginSuccessPacket {
            return ServerLoginSuccessPacket(
                    UUID.fromString(buf.readUTF8()),
                    buf.readUTF8()
            )
        }

        override fun ServerLoginSuccessPacket.toWire(buf: ByteBuf) {
            buf.writeUTF8(uuid.toString())
            buf.writeUTF8(username)
        }
    }
}