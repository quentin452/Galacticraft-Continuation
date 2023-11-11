package micdoodle8.mods.galacticraft.api.world;

public interface IOrbitDimension extends IGalacticraftWorldProvider
{
    String getPlanetToOrbit();
    
    int getYCoordToTeleportToPlanet();
}
