package us.kunet.fira.netty

import io.netty.channel.ChannelHandlerContext
import us.kunet.fira.protocol.FiraPacket
import us.kunet.fira.protocol.ProtocolState
import java.net.SocketAddress

class FiraConnection(private val ctx: ChannelHandlerContext, var state: ProtocolState = ProtocolState.HANDSHAKE) {
    fun close() {
        ctx.channel().close()
    }

    fun remoteAddress(): SocketAddress {
        return ctx.channel().remoteAddress()
    }

    fun sendPacket(packet: FiraPacket) {
        ctx.channel().writeAndFlush(packet)
    }
}