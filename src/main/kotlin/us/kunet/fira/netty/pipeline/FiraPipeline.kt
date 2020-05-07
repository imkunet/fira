package us.kunet.fira.netty.pipeline

import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelPipeline
import io.netty.channel.socket.SocketChannel
import us.kunet.fira.netty.FiraChannelHandler
import us.kunet.fira.netty.FiraConnection
import us.kunet.fira.protocol.FiraPacketHandler
import us.kunet.fira.protocol.FiraPacketRegistry
import java.util.function.Consumer

class FiraPipeline(private val registry: FiraPacketRegistry, private val handler: FiraPacketHandler,
                   private val pipelineCallback: Consumer<ChannelPipeline>? = null,
                   private val connectCallback: Consumer<FiraConnection>? = null): ChannelInitializer<SocketChannel>() {

    override fun initChannel(socketChannel: SocketChannel?) {
        val pipeline: ChannelPipeline = socketChannel?.pipeline() ?: return

        pipeline.addLast("lengthDecoder", FiraPacketLengthDecoder())
        pipeline.addLast("decoder", FiraPacketDecoder(registry, handler))

        pipeline.addLast("lengthEncoder", FiraPacketLengthEncoder())
        pipeline.addLast("encoder", FiraPacketEncoder(registry))

        pipeline.addLast("handler", FiraChannelHandler(connectCallback))

        pipelineCallback?.accept(pipeline)
    }
}