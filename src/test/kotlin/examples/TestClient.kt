package examples

import us.kunet.fira.instance.FiraClient
import us.kunet.fira.netty.pipeline.FiraPipeline
import us.kunet.fira.protocol.FiraClientHandler
import us.kunet.fira.protocol.ProtocolState
import us.kunet.fira.protocol.addHandler
import java.net.InetSocketAddress

fun main() {
    val registry = createPacketRegistry()
    val handlers = FiraClientHandler()

    handlers.addHandler<ServerResponsePacket> { connection, packet ->
        println("Status from server: ${packet.jsonResponse}")
        connection.close()
    }

    val client = FiraClient(FiraPipeline(registry, handlers) { connection ->
        connection.sendPacket(ClientHandshakePacket(-1, "localhost", 25565, ProtocolState.STATUS))
        connection.state = ProtocolState.STATUS

        connection.sendPacket(ClientRequestPacket())
    })

    client.connect(InetSocketAddress("localhost", 25565))
}

