package micdoodle8.mods.galacticraft.api.galaxies;

import net.minecraft.world.*;

public class Satellite extends CelestialBody implements IChildBody
{
    protected Planet parentCelestialBody;
    protected int dimensionIdStatic;
    
    public Satellite(final String satelliteName) {
        super(satelliteName);
        this.parentCelestialBody = null;
        this.dimensionIdStatic = 0;
    }
    
    public Planet getParentPlanet() {
        return this.parentCelestialBody;
    }
    
    public Satellite setParentBody(final Planet parentCelestialBody) {
        this.parentCelestialBody = parentCelestialBody;
        return this;
    }
    
    @Deprecated
    public CelestialBody setDimensionInfo(final int providerId, final Class<? extends WorldProvider> providerClass, final boolean autoRegister) {
        throw new UnsupportedOperationException("Satellite registered using an outdated method (setDimensionInfo)! Tell Galacticraft addon authors to update to the latest API.");
    }
    
    public CelestialBody setDimensionInfo(final int providerIdDynamic, final int providerIdStatic, final Class<? extends WorldProvider> providerClass) {
        this.dimensionID = providerIdDynamic;
        this.dimensionIdStatic = providerIdStatic;
        this.providerClass = providerClass;
        this.autoRegisterDimension = false;
        this.isReachable = true;
        return this;
    }
    
    public int getID() {
        return GalaxyRegistry.getSatelliteID(this.bodyName);
    }
    
    public String getUnlocalizedNamePrefix() {
        return "satellite";
    }
    
    public int getDimensionIdStatic() {
        return this.dimensionIdStatic;
    }
}
