package micdoodle8.mods.galacticraft.core.dimension;

import micdoodle8.mods.galacticraft.api.world.*;
import micdoodle8.mods.galacticraft.api.galaxies.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.biome.*;
import micdoodle8.mods.galacticraft.core.world.gen.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import net.minecraftforge.client.*;
import micdoodle8.mods.galacticraft.core.client.*;

public class WorldProviderOrbit extends WorldProviderSpaceStation implements IOrbitDimension, IZeroGDimension, ISolarLevel, IExitHeight
{
    public int spaceStationDimensionID;
    
    @Override
    public void setDimension(final int var1) {
        super.setDimension(this.spaceStationDimensionID = var1);
    }
    
    public CelestialBody getCelestialBody() {
        return (CelestialBody)GalacticraftCore.satelliteSpaceStation;
    }
    
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
        return 24000L;
    }
    
    public boolean shouldForceRespawn() {
        return !ConfigManagerCore.forceOverworldRespawn;
    }
    
    @Override
    public Class<? extends IChunkProvider> getChunkProviderClass() {
        return (Class<? extends IChunkProvider>)ChunkProviderOrbit.class;
    }
    
    @Override
    public Class<? extends WorldChunkManager> getWorldChunkManagerClass() {
        return (Class<? extends WorldChunkManager>)WorldChunkManagerOrbit.class;
    }
    
    public boolean isDaytime() {
        final float a = this.worldObj.getCelestialAngle(0.0f);
        return a < 0.42f || a > 0.58f;
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
        return 64;
    }
    
    public boolean canCoordinateBeSpawn(final int var1, final int var2) {
        return true;
    }
    
    public boolean isSurfaceWorld() {
        return this.worldObj != null && this.worldObj.isRemote;
    }
    
    public boolean canRespawnHere() {
        return false;
    }
    
    public int getRespawnDimension(final EntityPlayerMP player) {
        return this.shouldForceRespawn() ? this.dimensionId : 0;
    }
    
    public String getDimensionName() {
        return "Space Station " + this.spaceStationDimensionID;
    }
    
    public float getGravity() {
        return 0.075f;
    }
    
    public boolean hasBreathableAtmosphere() {
        return false;
    }
    
    public double getMeteorFrequency() {
        return 0.0;
    }
    
    public double getFuelUsageMultiplier() {
        return 0.5;
    }
    
    public String getPlanetToOrbit() {
        return "Overworld";
    }
    
    public int getYCoordToTeleportToPlanet() {
        return 30;
    }
    
    public String getSaveFolder() {
        return "DIM_SPACESTATION" + this.spaceStationDimensionID;
    }
    
    public double getSolarEnergyMultiplier() {
        return ConfigManagerCore.spaceStationEnergyScalar;
    }
    
    public double getYCoordinateToTeleport() {
        return 1200.0;
    }
    
    public boolean canSpaceshipTierPass(final int tier) {
        return tier > 0;
    }
    
    public float getFallDamageModifier() {
        return 0.4f;
    }
    
    public float getSoundVolReductionAmount() {
        return 50.0f;
    }
    
    public float getThermalLevelModifier() {
        return 0.0f;
    }
    
    public float getWindLevel() {
        return 0.1f;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void setSpinDeltaPerTick(final float angle) {
        final SkyProviderOrbit skyProvider = (SkyProviderOrbit)this.getSkyRenderer();
        if (skyProvider != null) {
            skyProvider.spinDeltaPerTick = angle;
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void createSkyProvider() {
        this.setSkyRenderer((IRenderHandler)new SkyProviderOrbit(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/earth.png"), true, true));
        this.setSpinDeltaPerTick(this.getSpinManager().getSpinRate());
        if (this.getCloudRenderer() == null) {
            this.setCloudRenderer((IRenderHandler)new CloudRenderer());
        }
    }
}
