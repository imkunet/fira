package us.kunet.fira.netty.pipeline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.util.internal.logging.InternalLogger
import io.netty.util.internal.logging.Slf4JLoggerFactory
import us.kunet.fira.netty.FiraChannelHandler
import us.kunet.fira.protocol.FiraPacket
import us.kunet.fira.protocol.FiraPacketHandler
import us.kunet.fira.protocol.FiraPacketRegistry
import us.kunet.fira.protocol.readVarInt
import kotlin.reflect.full.companionObjectInstance

class FiraPacketLengthDecoder : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, output: MutableList<Any>) {
        if (ctx.channel()?.isOpen != null && !ctx.channel().isOpen) return

        val packetLength = input.readVarInt()

        if (input.readableBytes() < packetLength) {
            input.resetReaderIndex()
        } else {
            output.add(input.readBytes(packetLength))
        }
    }
}

class FiraPacketDecoder(private val registry: FiraPacketRegistry, private val handler: FiraPacketHandler) :
    ByteToMessageDecoder() {

    companion object {
        val logger: InternalLogger = Slf4JLoggerFactory.getInstance(FiraPacketDecoder::class.java)
    }

    override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, output: MutableList<Any>) {
        val connection = ctx.pipeline()?.get(FiraChannelHandler::class.java)?.connection ?: return
        if (!ctx.channel().isOpen) return

        val packetId = input.readVarInt()
        logger.debug(
            "Decoding packet with ID: $packetId State: ${connection.state} " +
                    "with ${input.readableBytes()} bytes to read"
        )

        val packetClass = registry.getPacket(handler.packetDirection, connection.state, packetId)
        if (packetClass == null) {
            logger.debug(
                "Skipping unknown packet ID: " + "$packetId State: ${connection.state} (${handler.packetDirection})"
            )
            input.skipBytes(input.readableBytes())
            return
        }

        val packet = packetClass.companionObjectInstance as FiraPacket.Companion<*>
        handler.handlePacket(connection, packet.fromWire(input), packetClass)

        if (input.readableBytes() > 0) {
            logger.debug(
                "Packet Decoder didn't fully read packet ${packetClass.simpleName} " +
                        "and has ${input.readableBytes()} bytes left to read"
            )
            input.skipBytes(input.readableBytes())
        }
    }
}