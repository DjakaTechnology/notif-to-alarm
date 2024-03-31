package id.djaka.notiftoalarm.shared.ui.alarm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.LineUnavailableException
import kotlin.math.sin


object SoundUtils {
    private var SAMPLE_RATE = 8000f
    @JvmOverloads
    @Throws(LineUnavailableException::class)
    fun tone(hz: Int, msecs: Int, vol: Double = 1.0) {
        val buf = ByteArray(1)
        val af = AudioFormat(
            SAMPLE_RATE,  // sampleRate
            8,  // sampleSizeInBits
            1,  // channels
            true,  // signed
            false
        ) // bigEndian
        val sdl = AudioSystem.getSourceDataLine(af)
        sdl.open(af)
        sdl.start()
        for (i in 0 until msecs * 8) {
            val angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI
            buf[0] = (sin(angle) * 127.0 * vol).toInt().toByte()
            sdl.write(buf, 0, 1)
        }
        sdl.drain()
        sdl.stop()
        sdl.close()
    }


    suspend fun play() {
        tone(1000, 100)
        delay(1000)
        tone(100, 1000)
        delay(1000)
        tone(5000, 100)
        delay(1000)
        tone(400, 500)
        delay(1000)
        tone(400, 500, 0.2)
    }
}
@Composable
actual fun soundPlayer(): SoundPlayer {
    val coroutineScope = rememberCoroutineScope()
    return remember {
        object : SoundPlayer {
            override fun play() {
                coroutineScope.launch(Dispatchers.Default) {
                    SoundUtils.play()
                }
            }

            override fun stop() {
                // Do nothing
            }
        }
    }
}