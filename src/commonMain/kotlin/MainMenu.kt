
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class MainMenu(val gs: GalaxyState, val es: EmpireState, val ps: PlayerState, val ts: TechTree) : BasicScene() {

    override suspend fun SContainer.sceneInit() {
        loadBasicAssets()
        addDefaultBackground()

        image(resourcesVfs["ui/CrownOfStarsTitle.png"].readBitmap()) {
            scaledHeight = 375.0
            scaledWidth = 375.0
            y = 0.0
            centerXOnStage()
        }

        uiButton("New Game") {
            position(width/2, 400.00)
            centerXOnStage()
            onClick { newGame() }
            textFont = gameFont
            textColor = Colors.GOLD
        }

        uiButton("Load Game") {
            position(width/2, 500.00)
            centerXOnStage()
            onClick { loadGame() }
            textFont = gameFont
            textColor = Colors.GOLD
        }

        uiButton("Credits") {
            position(width/2, 600.00)
            centerXOnStage()
            onClick { sceneContainer.changeTo<CreditsScene>() }
            textFont = gameFont
            textColor = Colors.GOLD
        }

        uiButton("Quit Game")  {
            position(width/2, 700.00)
            centerXOnStage()
            onClick {
                kotlin.system.exitProcess(0)
             }
            textFont = gameFont
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
