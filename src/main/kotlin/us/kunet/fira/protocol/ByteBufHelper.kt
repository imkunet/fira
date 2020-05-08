package us.kunet.fira.protocol

import io.netty.buffer.ByteBuf
import java.io.IOException
import java.nio.charset.StandardCharsets
import kotlin.experimental.and
import kotlin.experimental.or

@Throws(IllegalArgumentException::class)
fun ByteBuf.readVarInt(): Int {
    var value = 0
    var i = 0
    var b: Int
    while (this.readByte().also { b = it.toInt() } and 0x80.toByte() != 0.toByte()) {
        value = value or (b and 0x7F shl i)
        i += 7
        require(i <= 35) { "Variable length quantity is too long" }
    }
    return value or (b shl i)
}

fun ByteBuf.writeVarInt(write: Int) {
    var value = write
    while ((value and -0x80).toLong() != 0L) {
        this.writeByte(value and 0x7F or 0x80)
        value = value ushr 7
    }
    this.writeByte(value and 0x7F)
}

@Throws(IOException::class)
fun ByteBuf.readUTF8(): String {
    val len = this.readVarInt()
    val bytes = ByteArray(len)
    this.readBytes(bytes)
    return String(bytes, StandardCharsets.UTF_8)
}

@Throws(IOException::class)
fun ByteBuf.writeUTF8(value: String) {
    val bytes = value.toByteArray(StandardCharsets.UTF_8)
    if (bytes.size >= Short.MAX_VALUE) {
        throw IOException("Attempt to write a string with a length greater than Short.MAX_VALUE to ByteBuf!")
    }

    this.writeVarInt(bytes.size)
    this.writeBytes(bytes)
}

@Throws(IOException::class)
fun ByteBuf.readVarLong(): Long {
    var out: Long = 0
    var bytes = 0
    var read: Byte
    do {
        read = this.readByte()
        out = out or (((read and 0x7F).toInt() shl bytes++ * 7).toLong())
        if (bytes > 10) {
            throw IOException("Attempt to read long bigger than allowed for a varlong!")
        }
    } while ((read and 0x80.toByte()).toInt() == 0x80)
    return out
}

fun ByteBuf.writeVarLong(write: Long) {
    var value = write
    var part: Byte
    do {
        part = (value and 0x7F).toByte()
        value = value ushr 7
        if (value != 0L) {
            part = part or 0x80.toByte()
        }
        this.writeByte(part.toInt())
    } while (value != 0L)
}