
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class WeaponsResearchScene(val es: EmpireState, val techTree: TechTree)  : BasicScene() {

    private lateinit var researchPoints: Text

    override suspend fun SContainer.sceneMain() {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()

        val background = image(resourcesVfs["ui/chooseResearch.jpg"].readBitmap()) {
            position(0, 0)
            setSizeScaled(sceneWidth.toDouble(), sceneHeight.toDouble())
        }

        uiVerticalStack {
            alignTopToTopOf(background)
            centerXOnStage()
            centerYOnStage()
            padding = 50.00

            for (weapon in techTree.weaponsTree) {
                uiButton(weapon.name) {
                    textColor = Colors.GOLD
                    textFont = font
                    onClick { buyTech(weapon) }
                }
            }

            uiButton("BACK") {
                textColor = Colors.GOLD
                textFont = font
                onClick { sceneContainer.changeTo<ChooseResearchRealm>() }
            }

            uiButton("MAP") {
                textColor = Colors.GOLD
                textFont = font
                onClick { sceneContainer.changeTo<StarsScene>() }
            }

            researchPoints = text("Research Points left: ${es.empires[Allegiance.Player.ordinal]?.researchPoints}") {
                color = Colors.CYAN
            }

        }
    }

    suspend fun buyTech(weapon: WeaponsTech) {
        if(!es.empires[Allegiance.Player.ordinal]?.canBuyTech(weapon)!!) {
            showNoGo("Not enough Research to buy this!")
        } else{
       //      if(showConfirmDialog(weapon.description, resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont())) {
         //       es.empires[Allegiance.Player.ordinal]?.buyTech(weapon)!!
           //     researchPoints.text = "Research Points left: ${es.empires[Allegiance.Player.ordinal]?.researchPoints}"
           // }
        }
    }
}
