package examples

import us.kunet.fira.instance.FiraClient
import us.kunet.fira.netty.pipeline.FiraPipeline
import us.kunet.fira.protocol.FiraClientHandler
import us.kunet.fira.protocol.ProtocolState
import java.net.InetSocketAddress
import java.util.function.BiConsumer
import java.util.function.Consumer

fun main() {
    val registry = createPacketRegistry()
    val handler = FiraClientHandler()

    handler.addHandler(Status::class.java, BiConsumer { t, u ->
        println("Stats retrieved from server: ${u.response}")
        t.close()
    })

    val client = FiraClient(FiraPipeline(registry, handler, connectCallback = Consumer {
        val handshake = Handshake()
        handshake.version = -1
        handshake.address = "localhost"
        handshake.port = 25565
        handshake.nextState = 1
        it.sendPacket(handshake)

        val statusRequest = StatusRequest()
        it.sendPacket(statusRequest)
        it.state = ProtocolState.STATUS
    }))

    client.connect(InetSocketAddress("localhost", 25565))
}

