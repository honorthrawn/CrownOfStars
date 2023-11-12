
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class CreditsScene() : Scene() {
    override suspend fun SContainer.sceneInit() {
        val background = image(resourcesVfs["ui/hs-2012-37-a-large_web.jpg"].readBitmap())  {
            position(0, 0)
            setSizeScaled(sceneWidth.toDouble(), sceneHeight.toDouble())
        }

        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()
        uiVerticalStack {
            scaledWidth = sceneWidth.toDouble()
            scaledHeight = sceneHeight.toDouble()
            padding = 20.0
            text("Programming and Game Design", 20.00, Colors.GOLD, font)
            text("Shane Ratledge", 20.00, Colors.CYAN, font)
            text("Game Engine", 20.00, Colors.GOLD, font)
            text("Korge", 20.00, Colors.CYAN, font)
            text("Background Image", 20.00, Colors.GOLD, font)
            text("NASA", 20.00, Colors.CYAN, font)
            text("Stars, Terrains", 20.00, Colors.GOLD, font)
            text("farcodev on OpenGameArt", 20.00, Colors.CYAN, font)
            text("Planets", 20.00, Colors.GOLD, font)
            text("Justin Nichol on OpenGameArt", 20.00, Colors.CYAN, font)
            text("Font", 20.00, Colors.GOLD, font)
            text("Grafito Design on www.dafont.com", 20.00, Colors.CYAN, font)
            text("Ships", 20.00, Colors.GOLD, font)
            text("Skorpio on OpenGameArt", 20.00, Colors.CYAN, font)
            text("Icons", 20.00, Colors.GOLD, font)
            text("FreeOrion Assets on OpenGameArt", 20.00, Colors.CYAN, font)
            uiButton("Main Menu") {
                textFont = font
                textColor = Colors.GOLD
                onClick { sceneContainer.changeTo<MainMenu>() }
            }
        }
    }

}
