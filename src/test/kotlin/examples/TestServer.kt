package examples

import us.kunet.fira.instance.FiraServer
import us.kunet.fira.netty.pipeline.FiraPipeline
import us.kunet.fira.protocol.FiraServerHandler
import us.kunet.fira.protocol.ProtocolState
import us.kunet.fira.protocol.addHandler
import java.util.*

fun main() {
    val registry = createPacketRegistry()
    val handlers = FiraServerHandler()

    handlers.addHandler<ClientHandshakePacket> { connection, packet ->
        connection.state = packet.nextState
    }

    handlers.addHandler<ClientLoginStartPacket> { connection, packet ->
        // this skips authentication (server emulates offline mode)
        val loginSuccessPacket = ServerLoginSuccessPacket(UUID.randomUUID(), packet.name)
        connection.sendPacket(loginSuccessPacket)
        connection.state = ProtocolState.PLAY
    }

    handlers.addHandler<ClientRequestPacket> { connection, _ ->
        connection.sendPacket(
            ServerResponsePacket(
                """
            {
                "version": {
                    "name": "fira (test)",
                    "protocol": 47
                },
                "players": {
                    "max": 50,
                    "online": 0,
                    "sample": []
                },	
                "description": {
                    "text": "Hello fira!"
                }
            }
            """.trimIndent()
            )
        )
    }

    handlers.addHandler<ClientPingPacket> { connection, packet ->
        connection.sendPacket(ServerPongPacket(packet.payload))
    }

    val server = FiraServer(FiraPipeline(registry, handlers))

    server.start()
}