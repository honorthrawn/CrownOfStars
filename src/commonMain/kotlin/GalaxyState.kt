class GalaxyState {

    var stars = mutableMapOf<Int, Star>()
    //Index of the player's chosen star or 0 if none
    var activePlayerStar = 0;
    //Index of the player's chosen planet or 0 if none
    var activePlayerPlanet = 0;

    fun rollGalaxy()
    {
        val sol = Star("Sol")
        sol.roll()
        stars[0] = sol
    }

    fun load()
    {

    }
}
