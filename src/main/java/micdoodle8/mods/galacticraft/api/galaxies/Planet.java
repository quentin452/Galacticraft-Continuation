package micdoodle8.mods.galacticraft.api.galaxies;

public class Planet extends CelestialBody
{
    protected SolarSystem parentSolarSystem;
    
    public Planet(final String planetName) {
        super(planetName);
        this.parentSolarSystem = null;
    }
    
    public SolarSystem getParentSolarSystem() {
        return this.parentSolarSystem;
    }
    
    public int getID() {
        return GalaxyRegistry.getPlanetID(this.bodyName);
    }
    
    public String getUnlocalizedNamePrefix() {
        return "planet";
    }
    
    public Planet setParentSolarSystem(final SolarSystem galaxy) {
        this.parentSolarSystem = galaxy;
        return this;
    }
}
