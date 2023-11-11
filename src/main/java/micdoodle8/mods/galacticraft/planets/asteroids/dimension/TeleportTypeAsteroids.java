package micdoodle8.mods.galacticraft.planets.asteroids.dimension;

import micdoodle8.mods.galacticraft.api.world.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import cpw.mods.fml.common.*;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.*;
import net.minecraft.world.gen.*;
import net.minecraft.entity.*;
import java.util.*;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.*;
import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import micdoodle8.mods.galacticraft.planets.mars.items.*;
import micdoodle8.mods.galacticraft.planets.asteroids.items.*;
import net.minecraft.init.*;
import net.minecraft.entity.passive.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.planets.mars.blocks.*;
import micdoodle8.mods.galacticraft.api.entity.*;

public class TeleportTypeAsteroids implements ITeleportType
{
    public boolean useParachute() {
        return false;
    }
    
    public Vector3 getPlayerSpawnLocation(final WorldServer world, final EntityPlayerMP player) {
        if (player != null) {
            final GCPlayerStats stats = GCPlayerStats.get(player);
            int x = MathHelper.floor_double(stats.coordsTeleportedFromX);
            int z = MathHelper.floor_double(stats.coordsTeleportedFromZ);
            final int limit = ConfigManagerCore.otherPlanetWorldBorders - 2;
            if (limit > 20) {
                if (x > limit) {
                    z *= limit / x;
                    x = limit;
                }
                else if (x < -limit) {
                    z *= -limit / x;
                    x = -limit;
                }
                if (z > limit) {
                    x *= limit / z;
                    z = limit;
                }
                else if (z < -limit) {
                    x *= -limit / z;
                    z = -limit;
                }
            }
            int attemptCount = 0;
            this.preGenChunks((World)world, x >> 4, z >> 4);
            do {
                BlockVec3 bv3 = null;
                if (world.provider instanceof WorldProviderAsteroids) {
                    bv3 = ((WorldProviderAsteroids)world.provider).getClosestAsteroidXZ(x, 0, z);
                }
                if (bv3 != null) {
                    if (bv3.distanceSquared(new BlockVec3(x, 128, z)) > 25600) {
                        break;
                    }
                    if (ConfigManagerCore.enableDebug) {
                        GCLog.info("Testing asteroid at x" + bv3.x + " y" + bv3.y + " z" + bv3.z);
                    }
                    this.loadChunksAround(bv3.x, bv3.z, 2, world.theChunkProviderServer);
                    this.loadChunksAround(bv3.x, bv3.z, -3, world.theChunkProviderServer);
                    if (this.goodAsteroidEntry((World)world, bv3.x, bv3.y, bv3.z)) {
                        return new Vector3((double)bv3.x, 310.0, (double)bv3.z);
                    }
                    if (this.goodAsteroidEntry((World)world, bv3.x + 2, bv3.y, bv3.z + 2)) {
                        return new Vector3((double)(bv3.x + 2), 310.0, (double)(bv3.z + 2));
                    }
                    if (this.goodAsteroidEntry((World)world, bv3.x + 2, bv3.y, bv3.z - 2)) {
                        return new Vector3((double)(bv3.x + 2), 310.0, (double)(bv3.z - 2));
                    }
                    if (this.goodAsteroidEntry((World)world, bv3.x - 2, bv3.y, bv3.z - 2)) {
                        return new Vector3((double)(bv3.x - 2), 310.0, (double)(bv3.z - 2));
                    }
                    if (this.goodAsteroidEntry((World)world, bv3.x - 2, bv3.y, bv3.z + 2)) {
                        return new Vector3((double)(bv3.x - 2), 310.0, (double)(bv3.z + 2));
                    }
                    if (ConfigManagerCore.enableDebug) {
                        GCLog.info("Removing drilled out asteroid at x" + bv3.x + " z" + bv3.z);
                    }
                    ((WorldProviderAsteroids)world.provider).removeAsteroid(bv3.x, bv3.y, bv3.z);
                }
            } while (++attemptCount < 5);
            FMLLog.info("Failed to find good large asteroid landing spot! Falling back to making a small one.", new Object[0]);
            this.makeSmallLandingSpot((World)world, x, z);
            return new Vector3((double)x, 310.0, (double)z);
        }
        FMLLog.severe("Null player when teleporting to Asteroids!", new Object[0]);
        return new Vector3(0.0, 310.0, 0.0);
    }
    
    private boolean goodAsteroidEntry(final World world, final int x, final int yorig, final int z) {
        for (int k = 208; k > 48; --k) {
            if (!world.isAirBlock(x, k, z) && Math.abs(k - yorig) <= 20) {
                for (int y = k + 2; y < 256; ++y) {
                    if (world.getBlock(x, y, z) == AsteroidBlocks.blockBasic) {
                        world.setBlockToAir(x, y, z);
                    }
                    if (world.getBlock(x - 1, y, z) == AsteroidBlocks.blockBasic) {
                        world.setBlockToAir(x - 1, y, z);
                    }
                    if (world.getBlock(x, y, z - 1) == AsteroidBlocks.blockBasic) {
                        world.setBlockToAir(x, y, z - 1);
                    }
                    if (world.getBlock(x - 1, y, z - 1) == AsteroidBlocks.blockBasic) {
                        world.setBlockToAir(x - 1, y, z - 1);
                    }
                }
                if (ConfigManagerCore.enableDebug) {
                    GCLog.info("Found asteroid at x" + x + " z" + z);
                }
                return true;
            }
        }
        return false;
    }
    
    private void makeSmallLandingSpot(final World world, final int x, final int z) {
        this.loadChunksAround(x, z, -1, (ChunkProviderServer)world.getChunkProvider());
        for (int k = 255; k > 48; --k) {
            if (!world.isAirBlock(x, k, z)) {
                this.makePlatform(world, x, k - 1, z);
                return;
            }
            if (!world.isAirBlock(x - 1, k, z)) {
                this.makePlatform(world, x - 1, k - 1, z);
                return;
            }
            if (!world.isAirBlock(x - 1, k, z - 1)) {
                this.makePlatform(world, x - 1, k - 1, z - 1);
                return;
            }
            if (!world.isAirBlock(x, k, z - 1)) {
                this.makePlatform(world, x, k - 1, z - 1);
                return;
            }
        }
        this.makePlatform(world, x, 48 + world.rand.nextInt(128), z);
    }
    
    private void loadChunksAround(final int x, final int z, final int i, final ChunkProviderServer cp) {
        cp.loadChunk(x >> 4, z >> 4);
        if (x + i >> 4 != x >> 4) {
            cp.loadChunk(x + i >> 4, z >> 4);
            if (z + i >> 4 != z >> 4) {
                cp.loadChunk(x >> 4, z + i >> 4);
                cp.loadChunk(x + i >> 4, z + i >> 4);
            }
        }
        else if (z + i >> 4 != z >> 4) {
            cp.loadChunk(x >> 4, z + i >> 4);
        }
    }
    
    private void makePlatform(final World world, final int x, final int y, final int z) {
        for (int xx = -3; xx < 3; ++xx) {
            for (int zz = -3; zz < 3; ++zz) {
                if (xx == -3) {
                    if (zz == -3) {
                        continue;
                    }
                    if (zz == 2) {
                        continue;
                    }
                }
                if (xx == 2) {
                    if (zz == -3) {
                        continue;
                    }
                    if (zz == 2) {
                        continue;
                    }
                }
                this.doBlock(world, x + xx, y, z + zz);
            }
        }
        for (int xx = -2; xx < 2; ++xx) {
            for (int zz = -2; zz < 2; ++zz) {
                this.doBlock(world, x + xx, y - 1, z + zz);
            }
        }
        this.doBlock(world, x - 1, y - 2, z - 1);
        this.doBlock(world, x - 1, y - 2, z);
        this.doBlock(world, x, y - 2, z);
        this.doBlock(world, x, y - 2, z - 1);
    }
    
    private void doBlock(final World world, final int x, final int y, final int z) {
        final int meta = (int)(world.rand.nextFloat() * 1.5f);
        if (world.isAirBlock(x, y, z)) {
            world.setBlock(x, y, z, AsteroidBlocks.blockBasic, meta, 2);
        }
    }
    
    public Vector3 getEntitySpawnLocation(final WorldServer world, final Entity entity) {
        return new Vector3(entity.posX, ConfigManagerCore.disableLander ? 250.0 : 900.0, entity.posZ);
    }
    
    public Vector3 getParaChestSpawnLocation(final WorldServer world, final EntityPlayerMP player, final Random rand) {
        return null;
    }
    
    private void preGenChunks(final World w, final int cx, final int cz) {
        this.preGenChunk(w, cx, cz);
        for (int r = 1; r < 3; ++r) {
            final int xmin = cx - r;
            final int xmax = cx + r;
            final int zmin = cz - r;
            final int zmax = cz + r;
            for (int i = -r; i < r; ++i) {
                this.preGenChunk(w, xmin, cz + i);
                this.preGenChunk(w, xmax, cz - i);
                this.preGenChunk(w, cx - i, zmin);
                this.preGenChunk(w, cx + i, zmax);
            }
        }
    }
    
    private void preGenChunk(final World w, final int chunkX, final int chunkZ) {
        w.getChunkFromChunkCoords(chunkX, chunkZ);
    }
    
    public void onSpaceDimensionChanged(final World newWorld, final EntityPlayerMP player, final boolean ridingAutoRocket) {
        if (!ridingAutoRocket && player != null) {
            final GCPlayerStats stats = GCPlayerStats.get(player);
            if (stats.teleportCooldown <= 0) {
                if (player.capabilities.isFlying) {
                    player.capabilities.isFlying = false;
                }
                if (!newWorld.isRemote) {
                    final EntityEntryPod entryPod = new EntityEntryPod(player);
                    newWorld.spawnEntityInWorld((Entity)entryPod);
                }
                stats.teleportCooldown = 10;
            }
        }
    }
    
    public void setupAdventureSpawn(final EntityPlayerMP player) {
        final GCPlayerStats stats = GCPlayerStats.get(player);
        SchematicRegistry.unlockNewPage(player, new ItemStack(GCItems.schematic, 1, 1));
        SchematicRegistry.unlockNewPage(player, new ItemStack(MarsItems.schematic, 1, 0));
        SchematicRegistry.unlockNewPage(player, new ItemStack(MarsItems.schematic, 1, 2));
        stats.rocketStacks = new ItemStack[20];
        stats.fuelLevel = 1000;
        int i = 0;
        stats.rocketStacks[i++] = new ItemStack(GCItems.oxMask);
        stats.rocketStacks[i++] = new ItemStack(GCItems.oxygenGear);
        stats.rocketStacks[i++] = new ItemStack(GCItems.oxTankMedium);
        stats.rocketStacks[i++] = new ItemStack(GCItems.oxTankHeavy);
        stats.rocketStacks[i++] = new ItemStack(GCItems.oxTankHeavy);
        stats.rocketStacks[i++] = new ItemStack(AsteroidsItems.canisterLOX);
        stats.rocketStacks[i++] = new ItemStack(AsteroidsItems.canisterLOX);
        stats.rocketStacks[i++] = new ItemStack(AsteroidsItems.canisterLOX);
        stats.rocketStacks[i++] = new ItemStack(AsteroidsItems.basicItem, 32, 7);
        stats.rocketStacks[i++] = new ItemStack(Blocks.glass_pane, 16);
        stats.rocketStacks[i++] = new ItemStack(Blocks.planks, 32, 2);
        stats.rocketStacks[i++] = new ItemStack(MarsItems.marsItemBasic, 16, 2);
        stats.rocketStacks[i++] = new ItemStack(GCItems.basicItem, 8, 13);
        stats.rocketStacks[i++] = new ItemStack(GCItems.basicItem, 2, 1);
        stats.rocketStacks[i++] = new ItemStack(GCItems.basicItem, 16, 15);
        stats.rocketStacks[i++] = new ItemStack(Items.egg, 12);
        stats.rocketStacks[i++] = new ItemStack(Items.spawn_egg, 2, VersionUtil.getClassToIDMapping(EntityCow.class));
        stats.rocketStacks[i++] = new ItemStack((Item)Items.potionitem, 4, 8262);
        stats.rocketStacks[i++] = new ItemStack(MarsBlocks.machine, 1, 4);
        stats.rocketStacks[i++] = new ItemStack(MarsItems.spaceship, 1, IRocketType.EnumRocketType.INVENTORY36.ordinal());
    }
}
