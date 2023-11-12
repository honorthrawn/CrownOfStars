
import com.soywiz.korge.*
import com.soywiz.korge.scene.*

suspend fun main() = Korge(title = "Crown of Stars",
    width =  2000,
    height = 1200,
    virtualWidth = 768,
    virtualHeight =  1024,
    iconPath = "ui/CrownOfStars.jpg"
   ) {
    injector.mapInstance(GalaxyState())
    injector.mapInstance(EmpireState())
    injector.mapInstance(PlayerState())
    injector.mapPrototype { AICore(get(), get())}
    injector.mapPrototype { MainMenu(get(), get(), get()) }
    injector.mapPrototype { PlanetsScene(get(), get(), get()) }
    injector.mapPrototype { PlanetScene(get(), get(), get()) }
    injector.mapPrototype { StarsScene(get(), get(), get(), get()) }
    injector.mapPrototype { BuyShipScene(get(), get(), get()) }
    injector.mapPrototype { DeployShipsScene(get(), get(), get()) }
    injector.mapPrototype { ColonyScene(get(), get(), get()) }
    injector.mapPrototype { TerraformingScene(get(), get(), get()) }
    injector.mapPrototype { BombardScene(get(), get(), get()) }
    injector.mapPrototype { CreditsScene() }
    injector.mapPrototype { MusicScene() }
    val musicSceneContainer = sceneContainer().changeTo<MusicScene>()
    val mainSceneContainer = sceneContainer().changeTo<MainMenu>()
}
