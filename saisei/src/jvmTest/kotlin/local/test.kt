package local

import file
import club.minnced.opus.util.OpusLibrary
import log
import naibu.ext.size
import naibu.math.toIntSafe
import saisei.container.mkv.block.readBlocks
import saisei.container.mkv.block.readFrames
import saisei.container.mkv.element.Segment
import tomp2p.opuswrapper.Opus
import java.nio.ByteBuffer
import javax.sound.sampled.AudioSystem
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.milliseconds

suspend fun testLocalMKV() {
    OpusLibrary.loadFromJar()

    /* get the testing file. */
    val (file, stream) = file("autumn")

    /* find the first audio track */
    val audioTrack = file.tracks.first { it.type == Segment.Tracks.TrackEntry.TrackType.audio }
    val opusDec = audioTrack.setupOpusDecoder()

    // create a java sound audio format for playback.
    val format = audioTrack.toAudioFormat()

    // create a new line.
    val srcLine = AudioSystem.getSourceDataLine(format)
    srcLine.open(format)
    srcLine.start()

    /* create an Opus decoder. */

    // start playback?
    srcLine.use {
        file.readBlocks(stream) { _, block, timecode ->
            val intermediary = ByteArray(4096)
            val pcmBytes = ByteBuffer.allocateDirect(960 * 2 * 2)
            val pcm = pcmBytes.asShortBuffer()

            block.readFrames(stream) { buffer, range ->
                pcmBytes.mark()
                intermediary.fill(0)

                // parse the frame size.
                val frameSize =
                    OpusDec.getPacketFrameSize(srcLine.format.sampleRate.toInt(), buffer, range.first, range.size)

                // copy the frame data into the intermediary array for opus decoding.
                buffer.load(range.first, intermediary, 0..<range.size.toIntSafe())

                // decode the data in the intermediary array
                val result = Opus.INSTANCE.opus_decode(
                    opusDec,
                    intermediary,
                    range.size.toIntSafe(),
                    pcm,
                    frameSize,
                    0
                )

                if (result < 0) {
                    log.error { result.readOpusError() }
                    exitProcess(1)
                }

                pcm.position(result * srcLine.format.channels)
                pcm.flip()

                val remaining = pcmBytes.remaining()
                pcmBytes.get(intermediary, 0, remaining)
                srcLine.write(intermediary, 0, remaining)

                pcmBytes.reset()
                println("played ${timecode.milliseconds}")
            }
        }
    }
}
