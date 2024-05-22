
import com.soywiz.korge.*
import com.soywiz.korge.scene.*

//TODO: see if better resolution setting that can look better
suspend fun main() = Korge(title = "Crown of Stars",
    width =  2000,
    height = 1200,
    virtualWidth = 768,
    virtualHeight =  1024,
    //virtualHeight =  1200,
    iconPath = "ui/crown.png",
) {
    injector.mapInstance(GalaxyState())
    injector.mapInstance(EmpireState())
    injector.mapInstance(PlayerState())
    injector.mapInstance(ComputerPlayerState())
    injector.mapInstance(TechTree())
    injector.mapPrototype { ComputerPlayerCombat(get(), get(), get())}
    injector.mapPrototype { ComputerPlayerCore(get(), get())}
    injector.mapPrototype { MainMenu(get(), get(), get(), get()) }
    injector.mapPrototype { PlanetsScene(get(), get(), get()) }
    injector.mapPrototype { PlanetScene(get(), get(), get()) }
    injector.mapPrototype { EnemyPlanetScene(get(), get(), get()) }
    injector.mapPrototype { StarsScene(get(), get(), get(), get(), get()) }
    injector.mapPrototype { BuyShipScene(get(), get(), get()) }
    injector.mapPrototype { DeployShipsScene(get(), get(), get()) }
    injector.mapPrototype { ViewShipsScene(get(),get(),get()) }
    injector.mapPrototype { FleetCombatScene(get(),get(),get(),get()) }
    injector.mapPrototype { LoseFleetCombatScene(get(), get()) }
    injector.mapPrototype { WinFleetCombatScene(get(), get()) }
    injector.mapPrototype { ColonyScene(get(), get(), get()) }
    injector.mapPrototype { TerraformingScene(get(), get(), get()) }
    injector.mapPrototype { BombardScene(get(), get(), get()) }
    injector.mapPrototype { InvadeScene(get(), get(), get()) }
    injector.mapPrototype { ChooseResearchRealm(get(), get()) }
    injector.mapPrototype { BuyTechScene(get(),get(),get()) }
    injector.mapPrototype { TechInfoScene(get(),get()) }
    injector.mapPrototype { CreditsScene() }
    injector.mapPrototype { MusicScene() }
    injector.mapPrototype { WarMusicScene() }
    //This feels like a hack but we need the same scene container so we can stop the regular music and go to
    //war songs when we switch to a battle scene
    var musicSceneContainer = sceneContainer()
    var mainSceneContainer = sceneContainer().changeTo<MainMenu>()
    musicSceneContainer.changeTo<MusicScene>()
    var ps = injector.get<PlayerState>()
    ps.musicSceneContainer = musicSceneContainer
}
