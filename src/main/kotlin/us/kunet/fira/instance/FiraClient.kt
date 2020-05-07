package us.kunet.fira.instance

import io.netty.bootstrap.Bootstrap
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import us.kunet.fira.netty.pipeline.FiraPipeline
import java.net.SocketAddress
import kotlin.concurrent.thread

class FiraClient(private val pipeline: FiraPipeline) {
    fun connect(address: SocketAddress) {
        thread {
            val group: EventLoopGroup = NioEventLoopGroup()

            try {
                val bootstrap = Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel::class.java)
                    .handler(pipeline)

                bootstrap.connect(address).sync().channel().closeFuture().sync()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                group.shutdownGracefully()
            }
        }
    }
}
