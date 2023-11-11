package micdoodle8.mods.galacticraft.api.galaxies;

import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.world.*;
import java.util.*;
import net.minecraft.util.*;
import org.apache.commons.lang3.builder.*;

public abstract class CelestialBody implements Comparable<CelestialBody>
{
    protected final String bodyName;
    protected String unlocalizedName;
    protected float relativeSize;
    protected ScalableDistance relativeDistanceFromCenter;
    protected float relativeOrbitTime;
    protected float phaseShift;
    protected int dimensionID;
    protected Class<? extends WorldProvider> providerClass;
    protected boolean autoRegisterDimension;
    protected boolean isReachable;
    protected boolean forceStaticLoad;
    protected int tierRequired;
    public ArrayList<IAtmosphericGas> atmosphere;
    protected ResourceLocation celestialBodyIcon;
    protected float ringColorR;
    protected float ringColorG;
    protected float ringColorB;
    
    public CelestialBody(final String bodyName) {
        this.relativeSize = 1.0f;
        this.relativeDistanceFromCenter = new ScalableDistance(1.0f, 1.0f);
        this.relativeOrbitTime = 1.0f;
        this.phaseShift = 0.0f;
        this.dimensionID = 0;
        this.autoRegisterDimension = false;
        this.isReachable = false;
        this.forceStaticLoad = true;
        this.tierRequired = 0;
        this.atmosphere = new ArrayList<IAtmosphericGas>();
        this.ringColorR = 1.0f;
        this.ringColorG = 1.0f;
        this.ringColorB = 1.0f;
        this.bodyName = bodyName.toLowerCase(Locale.ENGLISH);
        this.unlocalizedName = bodyName;
    }
    
    public abstract int getID();
    
    public abstract String getUnlocalizedNamePrefix();
    
    public String getName() {
        return this.bodyName;
    }
    
    public String getUnlocalizedName() {
        return this.getUnlocalizedNamePrefix() + "." + this.unlocalizedName;
    }
    
    public String getLocalizedName() {
        String s = this.getUnlocalizedName();
        s = ((s == null) ? "" : StatCollector.translateToLocal(s));
        final int comment = s.indexOf(35);
        return (comment > 0) ? s.substring(0, comment).trim() : s;
    }
    
    public float getRelativeSize() {
        return this.relativeSize;
    }
    
    public ScalableDistance getRelativeDistanceFromCenter() {
        return this.relativeDistanceFromCenter;
    }
    
    public float getPhaseShift() {
        return this.phaseShift;
    }
    
    public float getRelativeOrbitTime() {
        return this.relativeOrbitTime;
    }
    
    public int getTierRequirement() {
        return this.tierRequired;
    }
    
    public CelestialBody setTierRequired(final int tierRequired) {
        this.tierRequired = tierRequired;
        return this;
    }
    
    public CelestialBody setRelativeSize(final float relativeSize) {
        this.relativeSize = relativeSize;
        return this;
    }
    
    public CelestialBody setRelativeDistanceFromCenter(final ScalableDistance relativeDistanceFromCenter) {
        this.relativeDistanceFromCenter = relativeDistanceFromCenter;
        return this;
    }
    
    public CelestialBody setPhaseShift(final float phaseShift) {
        this.phaseShift = phaseShift;
        return this;
    }
    
    public CelestialBody setRelativeOrbitTime(final float relativeOrbitTime) {
        this.relativeOrbitTime = relativeOrbitTime;
        return this;
    }
    
    public CelestialBody setDimensionInfo(final int dimID, final Class<? extends WorldProvider> providerClass) {
        return this.setDimensionInfo(dimID, providerClass, true);
    }
    
    public CelestialBody setDimensionInfo(final int providerId, final Class<? extends WorldProvider> providerClass, final boolean autoRegister) {
        this.dimensionID = providerId;
        this.providerClass = providerClass;
        this.autoRegisterDimension = autoRegister;
        this.isReachable = true;
        return this;
    }
    
    public boolean shouldAutoRegister() {
        return this.autoRegisterDimension;
    }
    
    public int getDimensionID() {
        return this.dimensionID;
    }
    
    public Class<? extends WorldProvider> getWorldProvider() {
        return this.providerClass;
    }
    
    public boolean getReachable() {
        return this.isReachable;
    }
    
    public CelestialBody atmosphereComponent(final IAtmosphericGas gas) {
        this.atmosphere.add(gas);
        return this;
    }
    
    public CelestialBody setRingColorRGB(final float ringColorR, final float ringColorG, final float ringColorB) {
        this.ringColorR = ringColorR;
        this.ringColorG = ringColorG;
        this.ringColorB = ringColorB;
        return this;
    }
    
    public float getRingColorR() {
        return this.ringColorR;
    }
    
    public float getRingColorG() {
        return this.ringColorG;
    }
    
    public float getRingColorB() {
        return this.ringColorB;
    }
    
    public ResourceLocation getBodyIcon() {
        return this.celestialBodyIcon;
    }
    
    public CelestialBody setBodyIcon(final ResourceLocation planetIcon) {
        this.celestialBodyIcon = planetIcon;
        return this;
    }
    
    public boolean getForceStaticLoad() {
        return this.forceStaticLoad;
    }
    
    public CelestialBody setForceStaticLoad(final boolean force) {
        this.forceStaticLoad = force;
        return this;
    }
    
    @Override
    public int hashCode() {
        return this.getUnlocalizedName().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof CelestialBody && new EqualsBuilder().append((Object)this.getUnlocalizedName(), (Object)((CelestialBody)other).getUnlocalizedName()).isEquals();
    }
    
    @Override
    public int compareTo(final CelestialBody other) {
        final ScalableDistance thisDistance = this.getRelativeDistanceFromCenter();
        final ScalableDistance otherDistance = other.getRelativeDistanceFromCenter();
        return (otherDistance.unScaledDistance < thisDistance.unScaledDistance) ? 1 : ((otherDistance.unScaledDistance > thisDistance.unScaledDistance) ? -1 : 0);
    }
    
    public void setUnreachable() {
        this.isReachable = false;
    }
    
    public static class ScalableDistance
    {
        public final float unScaledDistance;
        public final float scaledDistance;
        
        public ScalableDistance(final float unScaledDistance, final float scaledDistance) {
            this.unScaledDistance = unScaledDistance;
            this.scaledDistance = scaledDistance;
        }
    }
}
