
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korio.file.std.*

open class BasicScene() : Scene() {

    private var notEnoughDialog: RoundRect? = null

    suspend fun showNoGo(requirements: String) {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()
        notEnoughDialog =
            this.sceneContainer.container().roundRect(
                sceneWidth / 2.00, sceneHeight / 4.00, 5.0, 5.0,
                Colors.BLACK ) {
                centerOnStage()
                uiVerticalStack {
                    scaledWidth = sceneWidth / 2.00
                    text(requirements, 50.00, Colors.CYAN, font)
                    uiButton("CLOSE")
                    {
                        textFont = font
                        textColor = Colors.GOLD
                        onClick { closeMessage() }
                    }
                }
            }
    }

    override suspend fun sceneBeforeLeaving() {
        notEnoughDialog?.removeFromParent()
    }

    private fun closeMessage() {
        notEnoughDialog?.removeFromParent()
    }

}
