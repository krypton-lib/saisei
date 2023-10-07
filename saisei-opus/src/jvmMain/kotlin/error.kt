package saisei.codec.opus

import tomp2p.opuswrapper.Opus

public actual fun Int.readException(): OpusException? {
    if (this == Opus.OPUS_OK) return null
    return OpusException(this, Opus.INSTANCE.opus_strerror(this))
}