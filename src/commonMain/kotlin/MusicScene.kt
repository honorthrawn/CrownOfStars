
import com.soywiz.korau.sound.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korio.file.std.*

//TODO: Do I want to have a separate selection(s) for combat music?
class MusicScene : Scene() {

    private var tunes = mutableListOf<Sound>()
    private var currentTune: SoundChannel? = null
    private var currentSong = 0
    override suspend fun SContainer.sceneInit() {
        println("Loading music")
        tunes.add(resourcesVfs["music/Badlands.mp3"].readMusic())
        tunes.add(resourcesVfs["music/Cassette.mp3"].readMusic())
        tunes.add(resourcesVfs["music/Powerful.mp3"].readMusic())
        tunes.add(resourcesVfs["music/Soprano.mp3"].readMusic())
        tunes.shuffle()
    }

    private fun chooseNextSong() {
        println("Choosing next song")
        currentSong++
        if (currentSong >= tunes.count()) {
            currentSong = 0
        }
        val pb = PlaybackParameters(times = PlaybackTimes(1),
            onFinish = {
                println("on finish")
                chooseNextSong()
            })
        currentTune = tunes[currentSong].play(this.coroutineContext, pb)
    }

    override suspend fun SContainer.sceneMain() {
        chooseNextSong()
    }
}
