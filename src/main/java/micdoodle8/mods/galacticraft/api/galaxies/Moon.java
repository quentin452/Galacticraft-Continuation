package micdoodle8.mods.galacticraft.api.galaxies;

public class Moon extends CelestialBody implements IChildBody
{
    protected Planet parentPlanet;
    
    public Moon(final String moonName) {
        super(moonName);
        this.parentPlanet = null;
    }
    
    public Moon setParentPlanet(final Planet planet) {
        this.parentPlanet = planet;
        return this;
    }
    
    public int getID() {
        return GalaxyRegistry.getMoonID(this.bodyName);
    }
    
    public String getUnlocalizedNamePrefix() {
        return "moon";
    }
    
    public Planet getParentPlanet() {
        return this.parentPlanet;
    }
}
