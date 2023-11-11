package micdoodle8.mods.galacticraft.core.world.gen.dungeon;

import net.minecraft.util.*;
import net.minecraftforge.common.util.*;
import net.minecraft.block.*;
import net.minecraft.init.*;
import net.minecraft.tileentity.*;
import java.util.*;

public class RoomSpawnerMoon extends DungeonRoom
{
    int sizeX;
    int sizeY;
    int sizeZ;
    Random rand;
    private final ArrayList<ChunkCoordinates> spawners;
    
    public RoomSpawnerMoon(final MapGenDungeon dungeon, final int posX, final int posY, final int posZ, final ForgeDirection entranceDir) {
        super(dungeon, posX, posY, posZ, entranceDir);
        this.spawners = new ArrayList<ChunkCoordinates>();
        if (this.worldObj != null) {
            this.rand = new Random(this.worldObj.getSeed() * posX * posY * 57L * posZ);
            this.sizeX = this.rand.nextInt(5) + 6;
            this.sizeY = this.rand.nextInt(2) + 4;
            this.sizeZ = this.rand.nextInt(5) + 6;
        }
    }
    
    public void generate(final Block[] chunk, final byte[] meta, final int cx, final int cz) {
        for (int i = this.posX - 1; i <= this.posX + this.sizeX; ++i) {
            for (int j = this.posY - 1; j <= this.posY + this.sizeY; ++j) {
                for (int k = this.posZ - 1; k <= this.posZ + this.sizeZ; ++k) {
                    if (i == this.posX - 1 || i == this.posX + this.sizeX || j == this.posY - 1 || j == this.posY + this.sizeY || k == this.posZ - 1 || k == this.posZ + this.sizeZ) {
                        this.placeBlock(chunk, meta, i, j, k, cx, cz, this.dungeonInstance.DUNGEON_WALL_ID, this.dungeonInstance.DUNGEON_WALL_META);
                    }
                    else {
                        this.placeBlock(chunk, meta, i, j, k, cx, cz, Blocks.air, 0);
                        if (this.rand.nextFloat() < 0.05f) {
                            this.placeBlock(chunk, meta, i, j, k, cx, cz, Blocks.web, 0);
                        }
                    }
                }
            }
        }
        if (this.placeBlock(chunk, meta, this.posX + 1, this.posY - 1, this.posZ + 1, cx, cz, Blocks.mob_spawner, 0)) {
            this.spawners.add(new ChunkCoordinates(this.posX + 1, this.posY - 1, this.posZ + 1));
        }
        if (this.placeBlock(chunk, meta, this.posX + this.sizeX - 1, this.posY - 1, this.posZ + this.sizeZ - 1, cx, cz, Blocks.mob_spawner, 0)) {
            this.spawners.add(new ChunkCoordinates(this.posX + this.sizeX - 1, this.posY - 1, this.posZ + this.sizeZ - 1));
        }
    }
    
    public DungeonBoundingBox getBoundingBox() {
        return new DungeonBoundingBox(this.posX, this.posZ, this.posX + this.sizeX, this.posZ + this.sizeZ);
    }
    
    protected DungeonRoom makeRoom(final MapGenDungeon dungeon, final int x, final int y, final int z, final ForgeDirection dir) {
        return new RoomSpawnerMoon(dungeon, x, y, z, dir);
    }
    
    protected void handleTileEntities(final Random rand) {
        for (final ChunkCoordinates spawnerCoords : this.spawners) {
            if (this.worldObj.getBlock(spawnerCoords.posX, spawnerCoords.posY, spawnerCoords.posZ) == Blocks.mob_spawner) {
                final TileEntityMobSpawner spawner = (TileEntityMobSpawner)this.worldObj.getTileEntity(spawnerCoords.posX, spawnerCoords.posY, spawnerCoords.posZ);
                if (spawner == null) {
                    continue;
                }
                spawner.func_145881_a().setEntityName(getMob(rand));
            }
        }
    }
    
    private static String getMob(final Random rand) {
        switch (rand.nextInt(4)) {
            case 0: {
                return "GalacticraftCore.EvolvedSpider";
            }
            case 1: {
                return "GalacticraftCore.EvolvedZombie";
            }
            case 2: {
                return "GalacticraftCore.EvolvedCreeper";
            }
            case 3: {
                return "GalacticraftCore.EvolvedSkeleton";
            }
            default: {
                return "GalacticraftCore.EvolvedZombie";
            }
        }
    }
}
