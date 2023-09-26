package local

import naibu.platform.Platform
import naibu.platform.isLittleEndian
import saisei.container.mkv.MatroskaTrack
import javax.sound.sampled.AudioFormat

fun MatroskaTrack.toAudioFormat(): AudioFormat = AudioFormat(
    AudioFormat.Encoding.PCM_SIGNED,
    audio!!.samplingFrequency.toFloat(),
    16,
    audio!!.channelCount,
    audio!!.channelCount * 2,
    audio!!.samplingFrequency.toFloat(),
    !Platform.isLittleEndian
)