
import com.soywiz.klock.*
import com.soywiz.korau.sound.*
import com.soywiz.korio.file.std.*

class MusicPlayer {
    private var tunes = mutableListOf<Sound>()
    private var currentTune: SoundChannel? = null
    private var currentTime: Double = 0.0
    private var hasCanceled = false
    private var currentSong = 0
    private var loadedMusic = false

    private suspend fun loadMusic() {
        println("Loading music")
        tunes.add(resourcesVfs["music/Badlands.mp3"].readMusic())
        tunes.add(resourcesVfs["music/Cassette.mp3"].readMusic())
        tunes.add(resourcesVfs["music/Powerful.mp3"].readMusic())
        tunes.add(resourcesVfs["music/Soprano.mp3"].readMusic())
        tunes.shuffle()
    }

    suspend fun playBackground() {
        println("Playing background music")
        if(!loadedMusic) {
            loadMusic()
            loadedMusic = true
        }
        playCurrentSong()
    }

    private fun chooseNextSong() {
        if(hasCanceled) {
            hasCanceled = false
        } else {
            println("Choosing next song")
            currentSong++
            currentTime = 0.0
            if (currentSong >= tunes.count()) {
                currentSong = 0
            }
        }
    }

    private suspend fun playCurrentSong() {
        println("Playing current song")
        val pb = PlaybackParameters(times = PlaybackTimes(1),
            startTime = TimeSpan(currentTime),
            onFinish =  {
                println("on finish")
                chooseNextSong()
                suspend { playCurrentSong() }
            },
            onCancel = {
                println("on Cancel")
                hasCanceled = true
                currentTime = currentTune?.current?.milliseconds ?: 0.00
            } )
        currentTune = tunes[currentSong].play(pb)
    }
}
