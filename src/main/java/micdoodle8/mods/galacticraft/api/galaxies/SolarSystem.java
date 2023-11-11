package micdoodle8.mods.galacticraft.api.galaxies;

import micdoodle8.mods.galacticraft.api.vector.*;
import java.util.*;
import net.minecraft.util.*;

public class SolarSystem
{
    protected final String systemName;
    protected String unlocalizedName;
    protected Vector3 mapPosition;
    protected Star mainStar;
    protected String unlocalizedGalaxyName;
    
    public SolarSystem(final String solarSystem, final String parentGalaxy) {
        this.mapPosition = null;
        this.mainStar = null;
        this.systemName = solarSystem.toLowerCase(Locale.ENGLISH);
        this.unlocalizedName = solarSystem;
        this.unlocalizedGalaxyName = parentGalaxy;
    }
    
    public String getName() {
        return this.systemName;
    }
    
    public final int getID() {
        return GalaxyRegistry.getSolarSystemID(this.systemName);
    }
    
    public String getLocalizedName() {
        final String s = this.getUnlocalizedName();
        return (s == null) ? "" : StatCollector.translateToLocal(s);
    }
    
    public String getUnlocalizedName() {
        return "solarsystem." + this.unlocalizedName;
    }
    
    public Vector3 getMapPosition() {
        return this.mapPosition;
    }
    
    public SolarSystem setMapPosition(final Vector3 mapPosition) {
        mapPosition.scale(500.0);
        this.mapPosition = mapPosition;
        return this;
    }
    
    public Star getMainStar() {
        return this.mainStar;
    }
    
    public SolarSystem setMainStar(final Star star) {
        this.mainStar = star;
        return this;
    }
    
    public String getLocalizedParentGalaxyName() {
        final String s = this.getUnlocalizedParentGalaxyName();
        return (s == null) ? "" : StatCollector.translateToLocal(s);
    }
    
    public String getUnlocalizedParentGalaxyName() {
        return "galaxy." + this.unlocalizedGalaxyName;
    }
}
