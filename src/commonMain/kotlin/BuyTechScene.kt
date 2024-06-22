import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class BuyTechScene(val es: EmpireState, val techTree: TechTree, val ps: PlayerState)  : BasicScene() {

    private lateinit var researchPoints: Text
    private lateinit var techBought: Tech

    override suspend fun SContainer.sceneMain() {
        val font = resourcesVfs["fonts/bioliquid-Regular.ttf"].readTtfFont()

        val background = image(resourcesVfs["ui/chooseResearch.jpg"].readBitmap()) {
            position(0, 0)
            setSizeScaled(sceneWidth.toDouble(), sceneHeight.toDouble())
        }


        var tree = when(ps.techRealmChosen) {
            TechRealm.COMPUTERS -> techTree.computersTree
            TechRealm.WEAPONS -> techTree.weaponsTree
            TechRealm.DEFENSE -> techTree.defenseTree
            TechRealm.PROPULSION -> techTree.propulsionTree
        }

        uiVerticalStack {
            alignTopToTopOf(background)
            padding = 50.00

            for (tech in tree) {
                 if(!es.empires[Allegiance.Player.ordinal]?.techTags?.contains(tech.id)!!) {
                     uiHorizontalStack {
                         padding = 40.00

                         text(tech.name, 25.00, Colors.CYAN, font)

                         uiButton("BUY") {
                             textColor = Colors.GOLD
                             textFont = font
                             onClick { buyTech(tech) }
                         }

                         uiButton("INFO") {
                             textColor = Colors.GOLD
                             textFont = font
                             onClick { getInfo(tech) }
                         }
                     }
                 }
            }

            researchPoints = text("Research Points left: ${es.empires[Allegiance.Player.ordinal]?.researchPoints}") {
                color = Colors.CYAN
            }

            uiHorizontalStack {
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
            }
        }
    }

    suspend fun buyTech(tech: Tech) {
        if(!es.empires[Allegiance.Player.ordinal]?.canBuyTech(tech)!!) {
            showNoGo("Not enough Research to buy this!")
        } else {
             techBought = tech
            showConfirmDialog(tech.description)
        }
    }

    suspend fun getInfo(tech: Tech) {
        ps.techQueryId = tech.id
        sceneContainer.changeTo<TechInfoScene>()
    }

    override fun actionConfirmed() {
        println("clicked!")
        es.empires[Allegiance.Player.ordinal]?.buyTech(techBought)!!
        researchPoints.text = "Research Points left: ${es.empires[Allegiance.Player.ordinal]?.researchPoints}"
    }
}
