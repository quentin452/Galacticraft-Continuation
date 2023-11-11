package micdoodle8.mods.galacticraft.core.dimension;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.*;
import micdoodle8.mods.galacticraft.api.world.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.biome.*;
import micdoodle8.mods.galacticraft.core.world.gen.*;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.core.event.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.api.galaxies.*;
import micdoodle8.mods.galacticraft.core.*;

public class WorldProviderMoon extends WorldProviderSpace implements IGalacticraftWorldProvider, ISolarLevel
{
    public Vector3 getFogColor() {
        return new Vector3(0.0, 0.0, 0.0);
    }
    
    public Vector3 getSkyColor() {
        return new Vector3(0.0, 0.0, 0.0);
    }
    
    public boolean canRainOrSnow() {
        return false;
    }
    
    public boolean hasSunset() {
        return false;
    }
    
    public long getDayLength() {
        return 192000L;
    }
    
    public boolean shouldForceRespawn() {
        return !ConfigManagerCore.forceOverworldRespawn;
    }
    
    public Class<? extends IChunkProvider> getChunkProviderClass() {
        return (Class<? extends IChunkProvider>)ChunkProviderMoon.class;
    }
    
    public Class<? extends WorldChunkManager> getWorldChunkManagerClass() {
        return (Class<? extends WorldChunkManager>)WorldChunkManagerMoon.class;
    }
    
    @SideOnly(Side.CLIENT)
    public float getStarBrightness(final float par1) {
        final float var2 = this.worldObj.getCelestialAngle(par1);
        float var3 = 1.0f - (MathHelper.cos(var2 * 3.1415927f * 2.0f) * 2.0f + 0.25f);
        if (var3 < 0.0f) {
            var3 = 0.0f;
        }
        if (var3 > 1.0f) {
            var3 = 1.0f;
        }
        return var3 * var3 * 0.5f + 0.3f;
    }
    
    public boolean isSkyColored() {
        return false;
    }
    
    public double getHorizon() {
        return 44.0;
    }
    
    public int getAverageGroundLevel() {
        return 68;
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
        return 0.062f;
    }
    
    public double getMeteorFrequency() {
        return 7.0;
    }
    
    public double getFuelUsageMultiplier() {
        return 0.7;
    }
    
    public double getSolarEnergyMultiplier() {
        return 1.4;
    }
    
    public boolean canSpaceshipTierPass(final int tier) {
        return tier > 0;
    }
    
    public float getFallDamageModifier() {
        return 0.18f;
    }
    
    public float getSoundVolReductionAmount() {
        return 20.0f;
    }
    
    public CelestialBody getCelestialBody() {
        return (CelestialBody)GalacticraftCore.moonMoon;
    }
    
    public boolean hasBreathableAtmosphere() {
        return false;
    }
    
    public float getThermalLevelModifier() {
        return 0.0f;
    }
    
    public float getWindLevel() {
        return 0.0f;
    }
}
