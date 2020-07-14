package us.kunet.fira.netty

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.internal.logging.InternalLogger
import io.netty.util.internal.logging.Slf4JLoggerFactory
import us.kunet.fira.protocol.FiraPacket

class FiraChannelHandler(private val connectCallback: ((FiraConnection) -> Unit)?) :
    SimpleChannelInboundHandler<FiraPacket>() {

    companion object {
        val logger: InternalLogger = Slf4JLoggerFactory.getInstance(FiraChannelHandler::class.java)
    }

    var connection: FiraConnection? = null

    override fun channelActive(ctx: ChannelHandlerContext) {
        connection = FiraConnection(ctx)
        connectCallback?.invoke(connection!!)
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        logger.debug("Dropping inactive connection ${connection?.remoteAddress()}")
        connection = null
        ctx.channel()?.close()
        ctx.close()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        connection = null
        ctx.close()
        cause.printStackTrace()
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: FiraPacket) {
        logger.debug("Handled packet: $msg")
    }
}
