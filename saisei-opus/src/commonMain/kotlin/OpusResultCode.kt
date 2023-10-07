package saisei.codec.opus

public sealed class OpusResultCode(public val value: Int) {
    public companion object {
        public val ALL: List<OpusResultCode> =
            listOf(Ok, BadArg, BufferTooSmall, InternalError, InvalidPacket, Unimplemented, InvalidState, AllocFail)

        public fun valueOf(value: Int): OpusResultCode {
            return ALL.find { it.value == value } ?: Unknown(value)
        }
    }

    /**
     * No error... very smart
     */
    public object Ok : OpusResultCode(0) {
        override fun toString(): String = "OpusErrorCode::Ok"
    }

    /**
     * One or more invalid/out of range arguments.
     */
    public object BadArg : OpusResultCode(-1) {
        override fun toString(): String = "OpusErrorCode::BadArg"
    }

    /**
     * The mode struct passed is invalid.
     */
    public object BufferTooSmall : OpusResultCode(-2) {
        override fun toString(): String = "OpusErrorCode::BufferTooSmall"
    }

    /**
     * An internal error was detected.
     */
    public object InternalError : OpusResultCode(-3) {
        override fun toString(): String = "OpusErrorCode::InternalError"
    }

    /**
     * The compressed data passed is corrupted.
     */
    public object InvalidPacket : OpusResultCode(-4) {
        override fun toString(): String = "OpusErrorCode::InvalidPacket"
    }

    /**
     * Invalid/unsupported request number.
     */
    public object Unimplemented : OpusResultCode(-5) {
        override fun toString(): String = "OpusErrorCode::Unimplemented"
    }

    /**
     * An encoder or decoder structure is invalid or already freed.
     */
    public object InvalidState : OpusResultCode(-6) {
        override fun toString(): String = "OpusErrorCode::InvalidState"
    }

    /**
     * Memory allocation has failed.
     */
    public object AllocFail : OpusResultCode(-7) {
        override fun toString(): String = "OpusErrorCode::AllocFail"
    }

    /**
     * An invalid pointer was passed.
     */
    public object InvalidPointer : OpusResultCode(-8) {
        override fun toString(): String = "OpusErrorCode::InvalidPointer"
    }

    public class Unknown(value: Int) : OpusResultCode(value) {
        override fun toString(): String = "OpusErrorCode::Unknown(value=$value)"
    }
}