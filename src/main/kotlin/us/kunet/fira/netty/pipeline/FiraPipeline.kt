package us.kunet.fira.netty.pipeline

import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelPipeline
import io.netty.channel.socket.SocketChannel
import us.kunet.fira.netty.FiraChannelHandler
import us.kunet.fira.netty.FiraConnection
import us.kunet.fira.protocol.FiraPacketHandler
import us.kunet.fira.protocol.FiraPacketRegistry

class FiraPipeline(
    private val registry: FiraPacketRegistry, private val handler: FiraPacketHandler,
    private val pipelineCallback: ((ChannelPipeline) -> Unit)? = null,
    private val connectCallback: ((FiraConnection) -> Unit)? = null
) : ChannelInitializer<SocketChannel>() {

    override fun initChannel(socketChannel: SocketChannel) {
        val pipeline: ChannelPipeline = socketChannel.pipeline()

        pipeline.addLast("lengthDecoder", FiraPacketLengthDecoder())
        pipeline.addLast("decoder", FiraPacketDecoder(registry, handler))

        pipeline.addLast("lengthEncoder", FiraPacketLengthEncoder())
        pipeline.addLast("encoder", FiraPacketEncoder(registry))

        pipeline.addLast("handler", FiraChannelHandler(connectCallback))

        pipelineCallback?.invoke(pipeline)
    }
}