import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class IntroScene : BasicScene() {
    override suspend fun SContainer.sceneInit() {
        loadBasicAssets()

        image(resourcesVfs["ui/hs-2012-37-a-large_web.jpg"].readBitmap()) {
            position(0, 0)
            setSizeScaled(sceneWidth.toDouble(), sceneHeight.toDouble())
        }

        val crawlLines = listOf(
            "CROWN OF STARS",
            "",
            "A fragile empire rises",
            "among scattered worlds.",
            "",
            "Fleets cross the void.",
            "Colonies struggle to survive.",
            "And somewhere in the dark,",
            "an enemy crown waits.",
            "",
            "The stars remember every choice."
        )

        val fontSize = 30.0
        val lineGap = 12.0
        val sectionGap = 30.0
        val crawl = container()
        var yPos = 0.0

        val buttonWidth = 48.0
        val buttonHeight = 48.0

        crawlLines.forEach { line ->
            if (line.isBlank()) {
                yPos += sectionGap
            } else {
                val t = crawl.text(
                    line,
                    textSize = fontSize,
                    color = Colors.GOLD,
                    font = gameFont,
                    autoScaling = false
                )

                val xPos = (sceneWidth.toDouble() - t.width) / 2.0
                t.position(xPos, yPos)

                yPos += fontSize + lineGap
            }
        }

        // Start with the title already visible above the button.
        // This avoids the "blank screen, did it crash?" feeling.
        val crawlStartY = scaledHeight - fontSize - 140.00
        crawl.position(0.0, crawlStartY)

        val speed = 35.0

        crawl.addUpdater { dt ->
            val seconds = dt.seconds
            crawl.position(crawl.x, crawl.y - speed * seconds)
        }

        uiButton("Play", width = buttonWidth, height = buttonHeight) {
            textFont = gameFont
            textColor = Colors.GOLD
            onClick {
                sceneContainer.changeTo<StarsScene>()
            }
            x = buttonWidth
            y = buttonHeight
        }
    }
}
