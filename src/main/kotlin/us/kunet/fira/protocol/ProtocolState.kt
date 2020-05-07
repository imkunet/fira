package us.kunet.fira.protocol

enum class ProtocolState {
    HANDSHAKE,
    STATUS,
    LOGIN,
    PLAY;

    companion object {
        @JvmStatic
        fun fromId(id: Int): ProtocolState {
            for (value in values()) {
                if (value.ordinal == id) {
                    return value
                }
            }
            throw IllegalArgumentException("Unknown protocol state id $id")
        }
    }
}