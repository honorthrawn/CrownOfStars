
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class MainMenu(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState, val ts: TechTree) : BasicScene() {

    override suspend fun SContainer.sceneInit() {
        val background = image(resourcesVfs["ui/CrownofStars.jpg"].readBitmap()) {
            position(0, 0)
            setSizeScaled(sceneWidth.toDouble(), sceneHeight.toDouble())
        }

        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()

        text("Crown of Stars", 60.00, Colors.DARKCYAN, font) {
            position(width/2, 100.00)
            centerXOnStage()
        }

        uiButton("New Game") {
            position(width/2, 200.00)
            centerXOnStage()
            onClick { newGame() }
            textFont = font
            textColor = Colors.GOLD
        }

        uiButton("Load Game") {
            position(width/2, 300.00)
            centerXOnStage()
            onClick { loadGame() }
            textFont = font
            textColor = Colors.GOLD
        }

        uiButton("Credits") {
            position(width/2, 400.00)
            centerXOnStage()
            onClick { sceneContainer.changeTo<CreditsScene>() }
            textFont = font
            textColor = Colors.GOLD
        }

        uiButton("Quit Game")  {
            position(width/2, 500.00)
            centerXOnStage()
            onClick {
                kotlin.system.exitProcess(0)
             }
            textFont = font
            textColor = Colors.GOLD
        }
    }

    private suspend fun loadGame() {
        if (!gs.hasSaveGame()) {
            showNoGo("No Saved Game to Load")
            return
        }

        ts.loadTrees()
        gs.load()
        es.load()
        sceneContainer.changeTo<StarsScene>()
    }

    private suspend fun newGame() {
        ts.loadTrees()
        gs.rollGalaxy()
        es.rollEmpires(ts)
        sceneContainer.changeTo<IntroScene>()
    }
}
