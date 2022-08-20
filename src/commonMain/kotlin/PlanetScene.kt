import com.soywiz.korge.component.docking.*
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.onClick
import com.soywiz.korim.bitmap.*

class PlanetScene(val gs: GalaxyState) : Scene() {

    private lateinit var farmerReadout: Text

    override suspend fun SContainer.sceneInit() {
        val background = image(resourcesVfs["hs-2012-37-a-large_web.jpg"].readBitmap())
        {
            position(0, 0)
            setSizeScaled(width, height)
        }
        val font = resourcesVfs["fonts/Android.ttf"].readTtfFont()

        println("Active player star: ${gs.activePlayerStar} active player planet: ${gs.activePlayerPlanet}")

        val fileName = when(gs.stars[gs.activePlayerStar]!!.planets[gs.activePlayerPlanet]!!.type) {
            PlanetType.TOXIC -> "planets/planet1.png"
            PlanetType.OCEAN -> "planets/planet2.png"
            PlanetType.TERRAN -> "planets/planet3.png"
            PlanetType.DESSERT -> "planets/planet4.png"
            PlanetType.VOLCANIC -> "planets/planet5.png"
            PlanetType.BARREN -> "planets/planet6.png"
            PlanetType.SUPERTERRAN -> "planets/planet7.png"
            PlanetType.TROPICAL -> "planets/planet10.png"
        }
        val planetImage = image(resourcesVfs[fileName].readBitmap()) {
            position(0, 0)
        }

        text( gs.stars[gs.activePlayerStar]!!.planets[gs.activePlayerPlanet]!!.name, 50.00, Colors.CYAN, font)
        {
            centerXOn(planetImage)
            alignTopToTopOf(planetImage, 12.0)
        }

        var farmerReadoutString = "FARMING: ${gs.stars[gs.activePlayerStar]!!.planets[gs.activePlayerPlanet]!!.farmers}"

        uiVerticalStack(200.00, UI_DEFAULT_PADDING){
            position(300.00, 0.00)
            uiHorizontalStack {
                text(" + ", 50.00,Colors.GOLD)
                {
                    onClick { onWorkerUp(WorkerType.FARMING) }
                }
                farmerReadout = text(farmerReadoutString, 50.00, Colors.CYAN)
                text(" - ", 50.00, Colors.GOLD)
                {
                    onClick { onWorkerDown(WorkerType.FARMING) }
                }
            }
            uiHorizontalStack {
                text(" + ", 50.00,Colors.GOLD)
                {
                    onClick { onWorkerUp(WorkerType.SHIPS) }
                }
                text("SHIPS:", 50.00, Colors.CYAN)
                text(" - ", 50.00, Colors.GOLD)
                {
                    onClick { onWorkerDown(WorkerType.SHIPS) }
                }
            }
            uiHorizontalStack {
                text(" + ", 50.00,Colors.GOLD)
                {
                    onClick { onWorkerUp(WorkerType.DEFENSE) }
                }
                text("DEFENSE:", 50.00, Colors.CYAN)
                text(" - ", 50.00, Colors.GOLD)
                {
                    onClick { onWorkerDown(WorkerType.DEFENSE) }
                }
            }
            uiHorizontalStack {
                text(" + ", 50.00,Colors.GOLD)
                {
                    onClick { onWorkerUp(WorkerType.SCIENCE) }
                }
                text("SCIENCE:", 50.00, Colors.CYAN)
                text(" - ", 50.00, Colors.GOLD)
                {
                    onClick { onWorkerDown(WorkerType.SCIENCE) }
                }
            }
        }


         text("BACK", 50.00,Colors.GOLD)
         {
             position(width/2, 800.00)
             centerXOn(background)
             onClick { sceneContainer.changeTo<PlanetsScene>() }

         }


        //val buttonBgrnd = RoundRect(200.00, 100.00, 2.00, 2.00, Colors.WHITE)
        //buttonBgrnd.position(400, 0)
        //addChild(buttonBgrnd)
        //text("Obama", 50.00, Colors.CYAN)
        //{
        //    centerXOn(buttonBgrnd)
        //   centerYOn(buttonBgrnd)
        //}



}

    private fun onWorkerUp(workertype: WorkerType) {
        gs.stars[gs.activePlayerStar]!!.planets[gs.activePlayerPlanet]!!.increaseWorker(workertype)
        var farmerReadoutString = "FARMING: ${gs.stars[gs.activePlayerStar]!!.planets[gs.activePlayerPlanet]!!.farmers}"
        farmerReadout.text = farmerReadoutString
    }

    private fun onWorkerDown(workertype: WorkerType) {
        gs.stars[gs.activePlayerStar]!!.planets[gs.activePlayerPlanet]!!.decreaseWorker(workertype)
        var farmerReadoutString = "FARMING: ${gs.stars[gs.activePlayerStar]!!.planets[gs.activePlayerPlanet]!!.farmers}"
        farmerReadout.text = farmerReadoutString
    }


}
