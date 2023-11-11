package micdoodle8.mods.galacticraft.planets.mars.dimension;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.*;
import micdoodle8.mods.galacticraft.api.world.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.biome.*;
import micdoodle8.mods.galacticraft.planets.mars.world.gen.*;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.core.event.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.api.galaxies.*;
import micdoodle8.mods.galacticraft.planets.mars.*;

public class WorldProviderMars extends WorldProviderSpace implements IGalacticraftWorldProvider, ISolarLevel
{
    private double solarMultiplier;
    
    public WorldProviderMars() {
        this.solarMultiplier = -1.0;
    }
    
    public Vector3 getFogColor() {
        final float f = 1.0f - this.getStarBrightness(1.0f);
        return new Vector3((double)(0.8235294f * f), (double)(0.47058824f * f), (double)(0.23137255f * f));
    }
    
    public Vector3 getSkyColor() {
        final float f = 1.0f - this.getStarBrightness(1.0f);
        return new Vector3((double)(0.6039216f * f), (double)(0.44705883f * f), (double)(0.25882354f * f));
    }
    
    public boolean canRainOrSnow() {
        return false;
    }
    
    public boolean hasSunset() {
        return false;
    }
    
    public long getDayLength() {
        return 24660L;
    }
    
    public boolean shouldForceRespawn() {
        return !ConfigManagerCore.forceOverworldRespawn;
    }
    
    public Class<? extends IChunkProvider> getChunkProviderClass() {
        return (Class<? extends IChunkProvider>)ChunkProviderMars.class;
    }
    
    public Class<? extends WorldChunkManager> getWorldChunkManagerClass() {
        return (Class<? extends WorldChunkManager>)WorldChunkManagerMars.class;
    }
    
    @SideOnly(Side.CLIENT)
    public float getStarBrightness(final float par1) {
        final float f1 = this.worldObj.getCelestialAngle(par1);
        float f2 = 1.0f - (MathHelper.cos(f1 * 3.1415927f * 2.0f) * 2.0f + 0.25f);
        if (f2 < 0.0f) {
            f2 = 0.0f;
        }
        if (f2 > 1.0f) {
            f2 = 1.0f;
        }
        return f2 * f2 * 0.75f;
    }
    
    public double getHorizon() {
        return 44.0;
    }
    
    public int getAverageGroundLevel() {
        return 76;
    }
    
    public boolean canCoordinateBeSpawn(final int var1, final int var2) {
        return true;
    }
    
    public boolean isSurfaceWorld() {
        return this.worldObj != null && this.worldObj.isRemote;
    }
    
    public boolean canRespawnHere() {
        if (EventHandlerGC.bedActivated) {
            EventHandlerGC.bedActivated = false;
            return true;
        }
        return false;
    }
    
    public int getRespawnDimension(final EntityPlayerMP player) {
        return this.shouldForceRespawn() ? this.dimensionId : 0;
    }
    
    public float getGravity() {
        return 0.058f;
    }
    
    public double getMeteorFrequency() {
        return 10.0;
    }
    
    public double getFuelUsageMultiplier() {
        return 0.9;
    }
    
    public boolean canSpaceshipTierPass(final int tier) {
        return tier >= 2;
    }
    
    public float getFallDamageModifier() {
        return 0.38f;
    }
    
    public float getSoundVolReductionAmount() {
        return 10.0f;
    }
    
    public CelestialBody getCelestialBody() {
        return (CelestialBody)MarsModule.planetMars;
    }
    
    public boolean hasBreathableAtmosphere() {
        return false;
    }
    
    public float getThermalLevelModifier() {
        return -1.0f;
    }
    
    public float getWindLevel() {
        return 0.3f;
    }
    
    public double getSolarEnergyMultiplier() {
        if (this.solarMultiplier < 0.0) {
            final double s = this.getSolarSize();
            this.solarMultiplier = s * s * s;
        }
        return this.solarMultiplier;
    }
}
