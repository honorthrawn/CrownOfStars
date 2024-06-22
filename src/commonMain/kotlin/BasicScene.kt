
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korio.file.std.*
import com.soywiz.korio.lang.*

open class BasicScene() : Scene() {

    private var notEnoughDialog: RoundRect? = null
    private var confirmationDialog: RoundRect? = null
    private var showingNotEnough = false

    suspend fun showNoGo(requirements: String) {
        if(!showingNotEnough) {
            val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()
            notEnoughDialog =
                this.sceneContainer.container().roundRect(
                    sceneWidth / 2.00, sceneHeight / 4.00, 5.0, 5.0,
                    Colors.BLACK
                ) {
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
            showingNotEnough = true
        }
    }

    suspend fun showConfirmDialog(msg: String){
        val len = msg.length
        var line1 = ""
        var line2 = ""
        if(len > 20 ) {
            line1 = msg.substr(0, 40)
            line2 = msg.substr(41, len - 40)
        } else {
            line1 = msg
        }
            val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()
            confirmationDialog =
                this.sceneContainer.container().roundRect(
                    sceneWidth / 2.00, sceneHeight / 4.00, 5.0, 5.0,
                    Colors.BLACK
                ) {
                    centerOnStage()
                    uiVerticalStack {
                        scaledWidth = sceneWidth / 2.00
                        text(line1, 20.00, Colors.CYAN, font)
                        text(line2, 20.00, Colors.CYAN, font)
                        uiButton("NO") {
                            textFont = font
                            textColor = Colors.GOLD
                            onClick { confirmationDialog?.removeFromParent() }  //don't know why can't just set yesClicked to true but can't
                        }

                        uiButton("YES") {
                            textFont = font
                            textColor = Colors.GOLD
                            onClick {  confirmationDialog?.removeFromParent()
                                actionConfirmed()
                            }
                        }
                    }
                }
    }

   suspend fun showHighlight(img: Image, color: RGBA) : RoundRect {
        var highlight = this.sceneContainer.container().roundRect(img.scaledWidth, img.scaledHeight, 5.00, 5.00,
            Colors.TRANSPARENT_BLACK, color, 5.00 ) {
            x = img.globalX
            y = img.globalY
        }
        return highlight
    }


    override suspend fun sceneBeforeLeaving() {
        showingNotEnough = false
        notEnoughDialog?.removeFromParent()
    }

    private fun closeMessage() {
        showingNotEnough = false
        notEnoughDialog?.removeFromParent()
    }

    open fun actionConfirmed() {
    }
}
