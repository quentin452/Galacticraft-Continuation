package micdoodle8.mods.galacticraft.planets.asteroids.dimension;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.*;
import micdoodle8.mods.galacticraft.api.world.*;
import micdoodle8.mods.galacticraft.api.galaxies.*;
import micdoodle8.mods.galacticraft.planets.asteroids.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.biome.*;
import micdoodle8.mods.galacticraft.planets.asteroids.world.gen.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.core.event.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class WorldProviderAsteroids extends WorldProviderSpace implements ISolarLevel
{
    private HashSet<AsteroidData> asteroids;
    private boolean dataNotLoaded;
    private AsteroidSaveData datafile;
    private double solarMultiplier;
    
    public WorldProviderAsteroids() {
        this.asteroids = new HashSet<AsteroidData>();
        this.dataNotLoaded = true;
        this.solarMultiplier = -1.0;
    }
    
    public CelestialBody getCelestialBody() {
        return (CelestialBody)AsteroidsModule.planetAsteroids;
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
        return 0L;
    }
    
    public boolean isDaytime() {
        return true;
    }
    
    public Class<? extends IChunkProvider> getChunkProviderClass() {
        return (Class<? extends IChunkProvider>)ChunkProviderAsteroids.class;
    }
    
    public Class<? extends WorldChunkManager> getWorldChunkManagerClass() {
        return (Class<? extends WorldChunkManager>)WorldChunkManagerAsteroids.class;
    }
    
    public boolean shouldForceRespawn() {
        return !ConfigManagerCore.forceOverworldRespawn;
    }
    
    public float calculateCelestialAngle(final long par1, final float par3) {
        return 0.25f;
    }
    
    @SideOnly(Side.CLIENT)
    public float getStarBrightness(final float par1) {
        return 1.0f;
    }
    
    public double getHorizon() {
        return 44.0;
    }
    
    public int getAverageGroundLevel() {
        return 96;
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
        return 0.072f;
    }
    
    public double getMeteorFrequency() {
        return 10.0;
    }
    
    public double getFuelUsageMultiplier() {
        return 0.9;
    }
    
    public boolean canSpaceshipTierPass(final int tier) {
        return tier >= 3;
    }
    
    public float getFallDamageModifier() {
        return 0.1f;
    }
    
    public float getSoundVolReductionAmount() {
        return 10.0f;
    }
    
    public boolean hasBreathableAtmosphere() {
        return false;
    }
    
    public float getThermalLevelModifier() {
        return -1.5f;
    }
    
    public void addAsteroid(final int x, final int y, final int z, final int size, final int core) {
        final AsteroidData coords = new AsteroidData(x, y, z, size, core);
        if (!this.asteroids.contains(coords)) {
            if (this.dataNotLoaded) {
                this.loadAsteroidSavedData();
            }
            if (!this.asteroids.contains(coords)) {
                this.addToNBT(this.datafile.datacompound, coords);
                this.asteroids.add(coords);
            }
        }
    }
    
    public void removeAsteroid(final int x, final int y, final int z) {
        final AsteroidData coords = new AsteroidData(x, y, z);
        if (this.asteroids.contains(coords)) {
            this.asteroids.remove(coords);
            if (this.dataNotLoaded) {
                this.loadAsteroidSavedData();
            }
            this.writeToNBT(this.datafile.datacompound);
        }
    }
    
    private void loadAsteroidSavedData() {
        this.datafile = (AsteroidSaveData)this.worldObj.loadItemData((Class)AsteroidSaveData.class, "GCAsteroidData");
        if (this.datafile == null) {
            this.datafile = new AsteroidSaveData("");
            this.worldObj.setItemData("GCAsteroidData", (WorldSavedData)this.datafile);
            this.writeToNBT(this.datafile.datacompound);
        }
        else {
            this.readFromNBT(this.datafile.datacompound);
        }
        this.dataNotLoaded = false;
    }
    
    private void readFromNBT(final NBTTagCompound nbt) {
        final NBTTagList coordList = nbt.getTagList("coords", 10);
        if (coordList.tagCount() > 0) {
            for (int j = 0; j < coordList.tagCount(); ++j) {
                final NBTTagCompound tag1 = coordList.getCompoundTagAt(j);
                if (tag1 != null) {
                    this.asteroids.add(AsteroidData.readFromNBT(tag1));
                }
            }
        }
    }
    
    private void writeToNBT(final NBTTagCompound nbt) {
        final NBTTagList coordList = new NBTTagList();
        for (final AsteroidData coords : this.asteroids) {
            final NBTTagCompound tag = new NBTTagCompound();
            coords.writeToNBT(tag);
            coordList.appendTag((NBTBase)tag);
        }
        nbt.setTag("coords", (NBTBase)coordList);
        this.datafile.markDirty();
    }
    
    private void addToNBT(final NBTTagCompound nbt, final AsteroidData coords) {
        final NBTTagList coordList = nbt.getTagList("coords", 10);
        final NBTTagCompound tag = new NBTTagCompound();
        coords.writeToNBT(tag);
        coordList.appendTag((NBTBase)tag);
        nbt.setTag("coords", (NBTBase)coordList);
        this.datafile.markDirty();
    }
    
    public BlockVec3 getClosestAsteroidXZ(final int x, final int y, final int z) {
        if (this.dataNotLoaded) {
            this.loadAsteroidSavedData();
        }
        if (this.asteroids.size() == 0) {
            return null;
        }
        BlockVec3 result = null;
        AsteroidData resultRoid = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (final AsteroidData test : this.asteroids) {
            if ((test.sizeAndLandedFlag & 0x80) > 0) {
                continue;
            }
            final int dx = x - test.centre.x;
            final int dz = z - test.centre.z;
            final int a = dx * dx + dz * dz;
            if (a >= lowestDistance) {
                continue;
            }
            lowestDistance = a;
            result = test.centre;
            resultRoid = test;
        }
        if (result == null) {
            return null;
        }
        final AsteroidData asteroidData = resultRoid;
        asteroidData.sizeAndLandedFlag |= 0x80;
        this.writeToNBT(this.datafile.datacompound);
        return result.clone();
    }
    
    public ArrayList<BlockVec3> getClosestAsteroidsXZ(final int x, final int y, final int z, final int facing, final int count) {
        if (this.dataNotLoaded) {
            this.loadAsteroidSavedData();
        }
        if (this.asteroids.size() == 0) {
            return null;
        }
        final TreeMap<Integer, BlockVec3> targets = new TreeMap<Integer, BlockVec3>();
        for (final AsteroidData roid : this.asteroids) {
            final BlockVec3 test = roid.centre;
            switch (facing) {
                case 2: {
                    if (z - 16 < test.z) {
                        continue;
                    }
                    break;
                }
                case 3: {
                    if (z + 16 > test.z) {
                        continue;
                    }
                    break;
                }
                case 4: {
                    if (x - 16 < test.x) {
                        continue;
                    }
                    break;
                }
                case 5: {
                    if (x + 16 > test.x) {
                        continue;
                    }
                    break;
                }
            }
            final int dx = x - test.x;
            final int dz = z - test.z;
            final int a = dx * dx + dz * dz;
            if (a < 262144) {
                targets.put(a, test);
            }
        }
        final int max = Math.max(count, targets.size());
        if (max <= 0) {
            return null;
        }
        final ArrayList<BlockVec3> returnValues = new ArrayList<BlockVec3>();
        int i = 0;
        final int offset = 6;
        for (final BlockVec3 target : targets.values()) {
            final BlockVec3 coords = target.clone();
            GCLog.debug("Found nearby asteroid at " + target.toString());
            switch (facing) {
                case 2: {
                    final BlockVec3 blockVec3 = coords;
                    blockVec3.z += offset;
                    break;
                }
                case 3: {
                    final BlockVec3 blockVec4 = coords;
                    blockVec4.z -= offset;
                    break;
                }
                case 4: {
                    final BlockVec3 blockVec5 = coords;
                    blockVec5.x += offset;
                    break;
                }
                case 5: {
                    final BlockVec3 blockVec6 = coords;
                    blockVec6.x -= offset;
                    break;
                }
            }
            returnValues.add(coords);
            if (++i >= count) {
                break;
            }
        }
        return returnValues;
    }
    
    public float getWindLevel() {
        return 0.05f;
    }
    
    public int getActualHeight() {
        return 256;
    }
    
    public void registerWorldChunkManager() {
        super.registerWorldChunkManager();
        this.hasNoSky = true;
    }
    
    public double getSolarEnergyMultiplier() {
        if (this.solarMultiplier < 0.0) {
            final double s = this.getSolarSize();
            this.solarMultiplier = s * s * s * ConfigManagerCore.spaceStationEnergyScalar;
        }
        return this.solarMultiplier;
    }
    
    private static class AsteroidData
    {
        protected BlockVec3 centre;
        protected int sizeAndLandedFlag;
        protected int coreAndSpawnedFlag;
        
        public AsteroidData(final int x, final int y, final int z) {
            this.sizeAndLandedFlag = 15;
            this.coreAndSpawnedFlag = -2;
            this.centre = new BlockVec3(x, y, z);
        }
        
        public AsteroidData(final int x, final int y, final int z, final int size, final int core) {
            this.sizeAndLandedFlag = 15;
            this.coreAndSpawnedFlag = -2;
            this.centre = new BlockVec3(x, y, z);
            this.sizeAndLandedFlag = size;
            this.coreAndSpawnedFlag = core;
        }
        
        public AsteroidData(final BlockVec3 bv) {
            this.sizeAndLandedFlag = 15;
            this.coreAndSpawnedFlag = -2;
            this.centre = bv;
        }
        
        @Override
        public int hashCode() {
            if (this.centre != null) {
                return this.centre.hashCode();
            }
            return 0;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o instanceof AsteroidData) {
                final BlockVec3 vector = ((AsteroidData)o).centre;
                return this.centre.x == vector.x && this.centre.y == vector.y && this.centre.z == vector.z;
            }
            if (o instanceof BlockVec3) {
                final BlockVec3 vector = (BlockVec3)o;
                return this.centre.x == vector.x && this.centre.y == vector.y && this.centre.z == vector.z;
            }
            return false;
        }
        
        public NBTTagCompound writeToNBT(final NBTTagCompound tag) {
            tag.setInteger("x", this.centre.x);
            tag.setInteger("y", this.centre.y);
            tag.setInteger("z", this.centre.z);
            tag.setInteger("coreAndFlag", this.coreAndSpawnedFlag);
            tag.setInteger("sizeAndFlag", this.sizeAndLandedFlag);
            return tag;
        }
        
        public static AsteroidData readFromNBT(final NBTTagCompound tag) {
            final BlockVec3 tempVector = new BlockVec3();
            tempVector.x = tag.getInteger("x");
            tempVector.y = tag.getInteger("y");
            tempVector.z = tag.getInteger("z");
            final AsteroidData roid = new AsteroidData(tempVector);
            if (tag.hasKey("coreAndFlag")) {
                roid.coreAndSpawnedFlag = tag.getInteger("coreAndFlag");
            }
            if (tag.hasKey("sizeAndFlag")) {
                roid.sizeAndLandedFlag = tag.getInteger("sizeAndFlag");
            }
            return roid;
        }
    }
}
