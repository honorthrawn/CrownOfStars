
import com.soywiz.klock.*
import com.soywiz.korau.sound.*
import com.soywiz.korge.scene.*
import com.soywiz.korio.file.std.*

class MusicPlayer {
    private var tunes = mutableListOf<Sound>()
    private var currentTune: SoundChannel? = null
    private var currentTime: Double = 0.0
    private var totalTime: Double = 0.0
    private var currentSong = 0
    private var loadedMusic = false
    private var currentScene: Scene? = null

    private suspend fun loadMusic() {
        println("Loading music")
        tunes.add(resourcesVfs["music/Badlands.mp3"].readMusic())
        tunes.add(resourcesVfs["music/Cassette.mp3"].readMusic())
        tunes.add(resourcesVfs["music/Powerful.mp3"].readMusic())
        tunes.add(resourcesVfs["music/Soprano.mp3"].readMusic())
        tunes.shuffle()
    }

    suspend fun playBackground(scene: Scene) {
        currentScene = scene
        println("Playing background music")
        if(!loadedMusic) {
            loadMusic()
            loadedMusic = true
        }
        if(currentTune == null) {
            playCurrentSong()
        }
    }

    private fun chooseNextSong() {
        if(currentTime < totalTime ) {
            println("song canceled, will resume")
        } else {
            println("Choosing next song")
            currentSong++
            currentTime = 0.0
            if (currentSong >= tunes.count()) {
                currentSong = 0
            }
            println("next song chosen")
        }
        if(currentTune != null && !currentTune!!.playing ) {
            playCurrentSong()
        }
    }

    private fun playCurrentSong() {
        println("Playing current song ${currentSong} @ ${currentTime}")
        val pb = PlaybackParameters(times = PlaybackTimes(1),
            startTime = TimeSpan(currentTime),
            onFinish = {
                println("on finish")
                chooseNextSong()
            },
            onCancel = {
                println("on Cancel")
                currentTime = currentTune?.current?.milliseconds ?: 0.00
                totalTime = currentTune?.total?.milliseconds ?: 0.00
                println("currentTime: ${currentTime} totalTime: ${totalTime}")
                chooseNextSong()
            } )
       // currentTune = tunes[currentSong].play(pb)
        currentTune = tunes[currentSong].play(currentScene!!.coroutineContext, pb )
    }
}
