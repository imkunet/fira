package us.kunet.fira.protocol

enum class ProtocolState {
    HANDSHAKE,
    STATUS,
    LOGIN,
    PLAY;

    companion object {
        @JvmStatic
        fun fromId(id: Int): ProtocolState {
            return values().find { it.ordinal == id } ?: throw IllegalArgumentException("Unknown protocol state id $id")
        }
    }
}