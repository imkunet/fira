package us.kunet.fira.netty.pipeline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import io.netty.util.internal.logging.InternalLogger
import io.netty.util.internal.logging.Slf4JLoggerFactory
import us.kunet.fira.protocol.FiraPacket
import us.kunet.fira.protocol.FiraPacketRegistry
import us.kunet.fira.protocol.writeVarInt
import kotlin.reflect.full.companionObjectInstance

class FiraPacketLengthEncoder : MessageToByteEncoder<ByteBuf>() {
    companion object {
        val logger: InternalLogger = Slf4JLoggerFactory.getInstance(FiraPacketLengthEncoder::class.java)
    }

    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: ByteBuf) {
        msg.readableBytes().let { out.writeVarInt(it) }
        logger.debug("Encoded packet length (${msg.readableBytes()})")
        out.writeBytes(msg)
    }
}

class FiraPacketEncoder(private val registry: FiraPacketRegistry) : MessageToByteEncoder<FiraPacket>() {
    companion object {
        val logger: InternalLogger = Slf4JLoggerFactory.getInstance(FiraPacketEncoder::class.java)
    }

    override fun encode(ctx: ChannelHandlerContext, packet: FiraPacket, out: ByteBuf) {
        @Suppress("UNCHECKED_CAST")
        (packet::class.companionObjectInstance as FiraPacket.Companion<FiraPacket>).also { companion ->
            registry.fillInfo(packet)
            logger.debug("Encoded packet State: ${companion.state} ID: ${companion.id} Data: $packet")

            out.writeVarInt(companion.id)

            @Suppress("UNCHECKED_CAST")
            companion.run {
                packet.toWire(out)
            }
        }
    }
}