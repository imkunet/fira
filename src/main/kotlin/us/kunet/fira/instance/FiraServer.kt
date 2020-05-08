package us.kunet.fira.instance

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import us.kunet.fira.netty.pipeline.FiraPipeline
import java.net.InetSocketAddress
import java.net.SocketAddress
import kotlin.concurrent.thread

class FiraServer(
    private val pipeline: FiraPipeline,
    private val address: SocketAddress = InetSocketAddress(25565),
    private val backlog: Int = 128
) {

    fun start() {
        thread {
            val bossGroup = NioEventLoopGroup()
            val workerGroup = NioEventLoopGroup()

            try {
                val bootstrap = ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel::class.java)
                    .option(ChannelOption.SO_BACKLOG, backlog)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(pipeline)

                bootstrap.bind(address).sync().channel().closeFuture().sync()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                bossGroup.shutdownGracefully()
                workerGroup.shutdownGracefully()
            }
        }
    }
}