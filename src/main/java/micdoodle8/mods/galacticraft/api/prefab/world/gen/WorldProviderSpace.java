package micdoodle8.mods.galacticraft.api.prefab.world.gen;

import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.world.biome.*;
import micdoodle8.mods.galacticraft.api.world.*;
import net.minecraft.world.chunk.*;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import java.util.*;
import java.lang.reflect.*;
import net.minecraft.command.*;
import net.minecraft.village.*;
import micdoodle8.mods.galacticraft.core.util.*;

public abstract class WorldProviderSpace extends WorldProvider implements IGalacticraftWorldProvider
{
    long timeCurrentOffset;
    long preTickTime;
    private long saveTCO;
    static Field tickCounter;
    
    public WorldProviderSpace() {
        this.timeCurrentOffset = 0L;
        this.preTickTime = Long.MIN_VALUE;
        this.saveTCO = 0L;
    }
    
    public abstract Vector3 getFogColor();
    
    public abstract Vector3 getSkyColor();
    
    public abstract boolean canRainOrSnow();
    
    public abstract boolean hasSunset();
    
    public abstract long getDayLength();
    
    public abstract Class<? extends IChunkProvider> getChunkProviderClass();
    
    public abstract Class<? extends WorldChunkManager> getWorldChunkManagerClass();
    
    public void setDimension(final int var1) {
        super.setDimension(this.dimensionId = var1);
    }
    
    public String getDimensionName() {
        return this.getCelestialBody().getLocalizedName();
    }
    
    public boolean isGasPresent(final IAtmosphericGas gas) {
        return this.getCelestialBody().atmosphere.contains(gas);
    }
    
    public boolean hasAtmosphere() {
        return this.getCelestialBody().atmosphere.size() > 0;
    }
    
    public void updateWeather() {
        if (!this.worldObj.isRemote) {
            final long newTime = this.worldObj.getWorldInfo().getWorldTime();
            if (this.preTickTime == Long.MIN_VALUE) {
                int savedTick = 0;
                try {
                    WorldProviderSpace.tickCounter.setAccessible(true);
                    savedTick = WorldProviderSpace.tickCounter.getInt(this.worldObj.villageCollectionObj);
                    if (savedTick < 0) {
                        savedTick = 0;
                    }
                }
                catch (Exception ex) {}
                this.timeCurrentOffset = savedTick - newTime;
            }
            else {
                final long diff = newTime - this.preTickTime;
                if (diff > 1L) {
                    this.timeCurrentOffset -= diff - 1L;
                    this.saveTime();
                }
            }
            this.preTickTime = newTime;
            this.saveTCO = 0L;
        }
        if (this.canRainOrSnow()) {
            super.updateWeather();
        }
        else {
            this.worldObj.getWorldInfo().setRainTime(0);
            this.worldObj.getWorldInfo().setRaining(false);
            this.worldObj.getWorldInfo().setThunderTime(0);
            this.worldObj.getWorldInfo().setThundering(false);
            this.worldObj.rainingStrength = 0.0f;
            this.worldObj.thunderingStrength = 0.0f;
        }
    }
    
    public String getSaveFolder() {
        return "DIM" + this.getCelestialBody().getDimensionID();
    }
    
    public String getWelcomeMessage() {
        return "Entering " + this.getCelestialBody().getLocalizedName();
    }
    
    public String getDepartMessage() {
        return "Leaving " + this.getCelestialBody().getLocalizedName();
    }
    
    public boolean canBlockFreeze(final int x, final int y, final int z, final boolean byWater) {
        return this.canRainOrSnow();
    }
    
    public boolean canDoLightning(final Chunk chunk) {
        return this.canRainOrSnow();
    }
    
    public boolean canDoRainSnowIce(final Chunk chunk) {
        return this.canRainOrSnow();
    }
    
    public float[] calcSunriseSunsetColors(final float var1, final float var2) {
        return (float[])(this.hasSunset() ? super.calcSunriseSunsetColors(var1, var2) : null);
    }
    
    public float calculateCelestialAngle(long par1, final float par3) {
        par1 = this.getWorldTime();
        final int j = (int)(par1 % this.getDayLength());
        float f1 = (j + par3) / this.getDayLength() - 0.25f;
        if (f1 < 0.0f) {
            ++f1;
        }
        if (f1 > 1.0f) {
            --f1;
        }
        final float f2 = f1;
        f1 = 0.5f - MathHelper.cos(f1 * 3.1415927f) / 2.0f;
        return f2 + (f1 - f2) / 3.0f;
    }
    
    @SideOnly(Side.CLIENT)
    public Vec3 getFogColor(final float var1, final float var2) {
        final Vector3 fogColor = this.getFogColor();
        return Vec3.createVectorHelper((double)fogColor.floatX(), (double)fogColor.floatY(), (double)fogColor.floatZ());
    }
    
    public Vec3 getSkyColor(final Entity cameraEntity, final float partialTicks) {
        final Vector3 skyColor = this.getSkyColor();
        return Vec3.createVectorHelper((double)skyColor.floatX(), (double)skyColor.floatY(), (double)skyColor.floatZ());
    }
    
    public boolean isSkyColored() {
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
    
    public boolean shouldForceRespawn() {
        return !ConfigManagerCore.forceOverworldRespawn;
    }
    
    public boolean hasBreathableAtmosphere() {
        return this.isGasPresent(IAtmosphericGas.OXYGEN) && !this.isGasPresent(IAtmosphericGas.CO2);
    }
    
    public boolean netherPortalsOperational() {
        return false;
    }
    
    public IChunkProvider createChunkGenerator() {
        try {
            final Class<? extends IChunkProvider> chunkProviderClass = this.getChunkProviderClass();
            final Constructor<?>[] constructors = chunkProviderClass.getConstructors();
            for (int i = 0; i < constructors.length; ++i) {
                final Constructor<?> constr = constructors[i];
                if (Arrays.equals(constr.getParameterTypes(), new Object[] { World.class, Long.TYPE, Boolean.TYPE })) {
                    return (IChunkProvider)constr.newInstance(this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled());
                }
                if (constr.getParameterTypes().length == 0) {
                    return (IChunkProvider)constr.newInstance(new Object[0]);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void registerWorldChunkManager() {
        if (this.getWorldChunkManagerClass() == null) {
            super.registerWorldChunkManager();
        }
        else {
            try {
                final Class<? extends WorldChunkManager> chunkManagerClass = this.getWorldChunkManagerClass();
                final Constructor<?>[] constructors2;
                final Constructor<?>[] constructors = constructors2 = chunkManagerClass.getConstructors();
                for (final Constructor<?> constr : constructors2) {
                    if (Arrays.equals(constr.getParameterTypes(), new Object[] { World.class })) {
                        this.worldChunkMgr = (WorldChunkManager)constr.newInstance(this.worldObj);
                    }
                    else if (constr.getParameterTypes().length == 0) {
                        this.worldChunkMgr = (WorldChunkManager)constr.newInstance(new Object[0]);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean shouldMapSpin(final String entity, final double x, final double y, final double z) {
        return false;
    }
    
    public float getSolarSize() {
        return 1.0f / this.getCelestialBody().getRelativeDistanceFromCenter().unScaledDistance;
    }
    
    public void setWorldTime(final long time) {
        this.worldObj.getWorldInfo().setWorldTime(time);
        if (JavaUtil.instance.isCalledBy(CommandTime.class)) {
            this.timeCurrentOffset = this.saveTCO;
            this.saveTime();
            this.preTickTime = time;
        }
        else {
            long diff = -this.timeCurrentOffset;
            this.timeCurrentOffset = time - this.worldObj.getWorldInfo().getWorldTime();
            diff += this.timeCurrentOffset;
            if (diff != 0L) {
                this.saveTime();
                this.preTickTime = time;
            }
        }
        this.saveTCO = 0L;
    }
    
    public long getWorldTime() {
        if (JavaUtil.instance.isCalledBy(CommandTime.class)) {
            this.saveTCO = this.timeCurrentOffset;
        }
        return this.worldObj.getWorldInfo().getWorldTime() + this.timeCurrentOffset;
    }
    
    public void adjustTimeOffset(final long diff) {
        this.timeCurrentOffset -= diff;
        this.preTickTime += diff;
        if (diff != 0L) {
            this.saveTime();
        }
    }
    
    private void saveTime() {
        try {
            final VillageCollection vc = this.worldObj.villageCollectionObj;
            WorldProviderSpace.tickCounter.setAccessible(true);
            WorldProviderSpace.tickCounter.setInt(vc, (int)this.getWorldTime());
            vc.markDirty();
        }
        catch (Exception ex) {}
    }
    
    static {
        try {
            WorldProviderSpace.tickCounter = VillageCollection.class.getDeclaredField(GCCoreUtil.isDeobfuscated() ? "tickCounter" : "tickCounter");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
