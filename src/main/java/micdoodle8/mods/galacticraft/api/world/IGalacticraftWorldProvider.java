package micdoodle8.mods.galacticraft.api.world;

import micdoodle8.mods.galacticraft.api.galaxies.*;

public interface IGalacticraftWorldProvider
{
    float getGravity();
    
    double getMeteorFrequency();
    
    double getFuelUsageMultiplier();
    
    boolean canSpaceshipTierPass(final int p0);
    
    float getFallDamageModifier();
    
    float getSoundVolReductionAmount();
    
    boolean hasBreathableAtmosphere();
    
    boolean netherPortalsOperational();
    
    boolean isGasPresent(final IAtmosphericGas p0);
    
    float getThermalLevelModifier();
    
    float getWindLevel();
    
    float getSolarSize();
    
    CelestialBody getCelestialBody();
}
