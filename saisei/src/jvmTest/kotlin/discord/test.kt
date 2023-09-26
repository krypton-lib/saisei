package discord

import file
import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.connect
import dev.kord.core.behavior.getChannelOf
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.voice.AudioFrame
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import log
import naibu.ext.size
import naibu.math.toIntSafe
import naibu.monads.expect
import naibu.platform.Environment
import saisei.container.mkv.block.readBlocks
import saisei.container.mkv.block.readFrames
import kotlin.time.Duration.Companion.seconds

@OptIn(KordVoice::class)
suspend fun testDiscordMKV() {
    val kord = Kord(Environment["DISCORD_TOKEN"].expect("need discord token pls"))

    kord.on<ReadyEvent> {
        lmao(kord)
    }

    kord.login {
        intents {
            +Intent.GuildVoiceStates
        }
    }
}

@OptIn(KordVoice::class)
suspend fun lmao(kord: Kord) = coroutineScope {
    val guild = kord.getGuild(Snowflake(323365823572082690L))
    val channel = guild.getChannelOf<VoiceChannel>(Snowflake(381612756123648000L))
    val (file, stream) = file("carnage")
    val frames = Channel<AudioFrame>(
        ((960.seconds / 48_000).inWholeMilliseconds / 20 + 1).toInt()
    )

    launch {
        val intermediary = ByteArray(4096)
        file.readBlocks(stream) { _, block, _ ->
            log.info { "Reading $block" }
            block.readFrames(stream) { buffer, range ->
                val size = range.size.toIntSafe()

                /* load the frame data into the intermediary buffer. */
                buffer.load(range.first, intermediary, 0..<size)

                /* send the frame data to the frames channel. */
                frames.send(AudioFrame(intermediary.copyOf(size)))
            }
        }
    }

    channel.connect {
        audioProvider { frames.receiveCatching().getOrNull() }
    }
}
