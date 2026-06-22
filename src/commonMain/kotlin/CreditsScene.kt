
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class CreditsScene : BasicScene() {
    override suspend fun SContainer.sceneInit() {
        loadBasicAssets()
        addDefaultBackground()

        val credits: List<Pair<String, RGBA>> = listOf(
            "Programming and Game Design" to Colors.GOLD,
            "Shane Ratledge" to Colors.CYAN,
            "" to Colors.WHITE,
            "Game Engine" to Colors.GOLD,
            "Built with KorGE" to Colors.CYAN,
            "" to Colors.WHITE,
            "Programming Support" to Colors.GOLD,
            "ChatGPT" to Colors.CYAN,
            "Art Assets" to Colors.GOLD,
            "NightCafe and ChatGPT" to Colors.CYAN,
            "Music" to Colors.GOLD,
            "Soundful" to Colors.CYAN,
            "Background Image" to Colors.GOLD,
            "NASA" to Colors.CYAN,
            "" to Colors.WHITE,
            "Font" to Colors.GOLD,
            "Grafito Design on www.dafont.com" to Colors.CYAN
        )

        val fontSize = 28.0
        val lineGap = 8.0
        val sectionGap = 20.0

        val creditsContainer = container()
        val korgeIcon = resourcesVfs["ui/korge.png"].readBitmap()

        var yPos = 0.0

        credits.forEach { credit: Pair<String, RGBA> ->
            val line = credit.first
            val color = credit.second

            if (line.isBlank()) {
                yPos += sectionGap
            } else {
                val t = creditsContainer.text(
                    line,
                    textSize = fontSize,
                    color = color,
                    font = gameFont,
                    autoScaling = false
                )

                val xPos = (sceneWidth.toDouble() - t.width) / 2.0
                t.position(xPos, yPos)

                yPos += fontSize + lineGap

                if (line == "Built with KorGE") {
                    val iconSize = 56.0
                    val icon = creditsContainer.image(korgeIcon) {
                        setSizeScaled(iconSize, iconSize)
                        position((sceneWidth.toDouble() - iconSize) / 2.0, yPos + 2.0)
                    }

                    yPos = icon.y + icon.scaledHeight + lineGap
                }
            }
        }

        val button = creditsContainer.uiButton("Main Menu") {
            textFont = gameFont
            textColor = Colors.GOLD
            onClick {
                sceneContainer.changeTo<MainMenu>()
            }
        }

        val buttonY = yPos + 24.0
        val buttonX = (sceneWidth.toDouble() - button.width) / 2.0
        button.position(buttonX, buttonY)

        val totalHeight = buttonY + button.height
        val containerY = (sceneHeight.toDouble() - totalHeight) / 2.0

        creditsContainer.position(0.0, containerY)
    }
}
