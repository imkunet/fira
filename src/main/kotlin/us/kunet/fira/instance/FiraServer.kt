package us.kunet.fira.instance

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import us.kunet.fira.netty.pipeline.FiraPipeline
import java.net.InetSocketAddress
import java.net.SocketAddress
import kotlin.concurrent.thread

open class FiraServer(
    private val pipeline: FiraPipeline,
    private val address: SocketAddress = InetSocketAddress(25565),
    private val backlog: Int = 128,

    private val bossGroup: NioEventLoopGroup = NioEventLoopGroup(),
    private val workerGroup: NioEventLoopGroup = NioEventLoopGroup()
) {
    fun start() {
        thread {
            try {
                val bootstrap = ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel::class.java)
                    .option(ChannelOption.SO_BACKLOG, backlog)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(pipeline)

                bootstrap.option(ChannelOption.TCP_NODELAY, true)
                bootstrap.bind(address).sync().channel().closeFuture().sync()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                bossGroup.shutdownGracefully()
                workerGroup.shutdownGracefully()
            }
        }
    }

    fun stop() {
        bossGroup.shutdownGracefully()
        workerGroup.shutdownGracefully()
    }
}