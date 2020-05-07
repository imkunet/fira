package examples

import us.kunet.fira.instance.FiraServer
import us.kunet.fira.netty.pipeline.FiraPipeline
import us.kunet.fira.protocol.FiraServerHandler
import us.kunet.fira.protocol.ProtocolState
import java.util.function.BiConsumer

fun main() {
    val registry = createPacketRegistry()
    val handler = FiraServerHandler()

    handler.addHandler(Handshake::class.java, BiConsumer { t, u ->
        when (u.nextState) {
            1 -> {
                t.state = ProtocolState.STATUS
            }
            2 -> {
                t.state = ProtocolState.LOGIN
                // don't do anything but kick them, we didn't implement actual login logic
                val kick = Disconnect()
                kick.message = """{"text":"This isn't a real server, dummy!"}"""
                t.sendPacket(kick)
                t.close()
            }
            else -> {
                throw IllegalArgumentException("Invalid client handshake intention")
            }
        }
    })

    handler.addHandler(StatusRequest::class.java, BiConsumer { t, _ ->
        val response = Status()
        response.response = """
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
        t.sendPacket(response)
    })

    handler.addHandler(Ping::class.java, BiConsumer { t, u ->
        val pong = Pong()
        pong.time = u.time
        t.sendPacket(pong)
    })

    val server = FiraServer(FiraPipeline(registry, handler))

    server.start()
}