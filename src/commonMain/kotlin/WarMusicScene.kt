
import com.soywiz.korau.sound.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korio.file.std.*

class WarMusicScene() : Scene() {
    private var warTunes = mutableListOf<Sound>()
    private var currentTune: SoundChannel? = null
    private var currentSong = 0
    override suspend fun SContainer.sceneInit() {
        println("Loading music")
       // tunes.shuffle()
        warTunes.add(resourcesVfs["music/ToWar.mp3"].readMusic())
    }

    private fun chooseNextSong() {
        println("Choosing next song")
        currentSong++
        if (currentSong >= warTunes.count()) {
            currentSong = 0
        }
        val pb = PlaybackParameters(times = PlaybackTimes(1),
            onFinish = {
                println("on finish")
                chooseNextSong()
            })
        currentTune = warTunes[currentSong].play(this.coroutineContext, pb)
    }

    override suspend fun SContainer.sceneMain() {
        chooseNextSong()
    }
}
