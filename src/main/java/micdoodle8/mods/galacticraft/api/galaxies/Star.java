package micdoodle8.mods.galacticraft.api.galaxies;

public class Star extends CelestialBody
{
    protected SolarSystem parentSolarSystem;
    
    public Star(final String planetName) {
        super(planetName);
        this.parentSolarSystem = null;
    }
    
    public SolarSystem getParentSolarSystem() {
        return this.parentSolarSystem;
    }
    
    public int getID() {
        return this.parentSolarSystem.getID();
    }
    
    public String getUnlocalizedNamePrefix() {
        return "star";
    }
    
    public Star setParentSolarSystem(final SolarSystem galaxy) {
        this.parentSolarSystem = galaxy;
        return this;
    }
}
