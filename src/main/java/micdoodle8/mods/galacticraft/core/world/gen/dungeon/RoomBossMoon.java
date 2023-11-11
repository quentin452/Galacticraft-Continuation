package micdoodle8.mods.galacticraft.core.world.gen.dungeon;

import java.util.*;
import net.minecraft.util.*;
import net.minecraftforge.common.util.*;
import net.minecraft.block.*;
import net.minecraft.init.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.tileentity.*;

public class RoomBossMoon extends DungeonRoom
{
    public int sizeX;
    public int sizeY;
    public int sizeZ;
    Random rand;
    ChunkCoordinates spawnerCoords;
    
    public RoomBossMoon(final MapGenDungeon dungeon, final int posX, final int posY, final int posZ, final ForgeDirection entranceDir) {
        super(dungeon, posX, posY, posZ, entranceDir);
        if (this.worldObj != null) {
            this.rand = new Random(this.worldObj.getSeed() * posX * posY * 57L * posZ);
            this.sizeX = this.rand.nextInt(6) + 14;
            this.sizeY = this.rand.nextInt(2) + 8;
            this.sizeZ = this.rand.nextInt(6) + 14;
        }
    }
    
    public void generate(final Block[] chunk, final byte[] meta, final int cx, final int cz) {
        for (int i = this.posX - 1; i <= this.posX + this.sizeX; ++i) {
            for (int k = this.posZ - 1; k <= this.posZ + this.sizeZ; ++k) {
                for (int j = this.posY - 1; j <= this.posY + this.sizeY; ++j) {
                    if (i == this.posX - 1 || i == this.posX + this.sizeX || j == this.posY - 1 || j == this.posY + this.sizeY || k == this.posZ - 1 || k == this.posZ + this.sizeZ) {
                        this.placeBlock(chunk, meta, i, j, k, cx, cz, this.dungeonInstance.DUNGEON_WALL_ID, this.dungeonInstance.DUNGEON_WALL_META);
                    }
                    else if ((i == this.posX && k == this.posZ) || (i == this.posX + this.sizeX - 1 && k == this.posZ) || (i == this.posX && k == this.posZ + this.sizeZ - 1) || (i == this.posX + this.sizeX - 1 && k == this.posZ + this.sizeZ - 1)) {
                        this.placeBlock(chunk, meta, i, j, k, cx, cz, (Block)Blocks.flowing_lava, 0);
                    }
                    else if ((j % 3 == 0 && j >= this.posY + 2 && (i == this.posX || i == this.posX + this.sizeX - 1 || k == this.posZ || k == this.posZ + this.sizeZ - 1)) || (i == this.posX + 1 && k == this.posZ) || (i == this.posX && k == this.posZ + 1) || (i == this.posX + this.sizeX - 2 && k == this.posZ) || (i == this.posX + this.sizeX - 1 && k == this.posZ + 1) || (i == this.posX + 1 && k == this.posZ + this.sizeZ - 1) || (i == this.posX && k == this.posZ + this.sizeZ - 2) || (i == this.posX + this.sizeX - 2 && k == this.posZ + this.sizeZ - 1) || (i == this.posX + this.sizeX - 1 && k == this.posZ + this.sizeZ - 2)) {
                        this.placeBlock(chunk, meta, i, j, k, cx, cz, Blocks.iron_bars, 0);
                    }
                    else if (((i == this.posX + 1 && k == this.posZ + 1) || (i == this.posX + this.sizeX - 2 && k == this.posZ + 1) || (i == this.posX + 1 && k == this.posZ + this.sizeZ - 2) || (i == this.posX + this.sizeX - 2 && k == this.posZ + this.sizeZ - 2)) && j % 3 == 0) {
                        this.placeBlock(chunk, meta, i, j, k, cx, cz, Blocks.iron_bars, 0);
                    }
                    else {
                        this.placeBlock(chunk, meta, i, j, k, cx, cz, Blocks.air, 0);
                    }
                }
            }
        }
        final int hx = (this.posX + this.posX + this.sizeX) / 2;
        final int hz = (this.posZ + this.posZ + this.sizeZ) / 2;
        this.spawnerCoords = new ChunkCoordinates(hx, this.posY + 2, hz);
    }
    
    public DungeonBoundingBox getBoundingBox() {
        return new DungeonBoundingBox(this.posX, this.posZ, this.posX + this.sizeX, this.posZ + this.sizeZ);
    }
    
    protected DungeonRoom makeRoom(final MapGenDungeon dungeon, final int x, final int y, final int z, final ForgeDirection dir) {
        return new RoomBossMoon(dungeon, x, y, z, dir);
    }
    
    protected void handleTileEntities(final Random rand) {
        if (this.spawnerCoords == null) {
            return;
        }
        this.worldObj.setBlock(this.spawnerCoords.posX, this.spawnerCoords.posY, this.spawnerCoords.posZ, GCBlocks.blockMoon, 15, 3);
        final TileEntity tile = this.worldObj.getTileEntity(this.spawnerCoords.posX, this.spawnerCoords.posY, this.spawnerCoords.posZ);
        if (tile == null || !(tile instanceof TileEntityDungeonSpawner)) {
            final TileEntityDungeonSpawner spawner = new TileEntityDungeonSpawner();
            spawner.setRoom(new Vector3((double)this.posX, (double)this.posY, (double)this.posZ), new Vector3((double)this.sizeX, (double)this.sizeY, (double)this.sizeZ));
            this.worldObj.setTileEntity(this.spawnerCoords.posX, this.spawnerCoords.posY, this.spawnerCoords.posZ, (TileEntity)spawner);
        }
        else if (tile instanceof TileEntityDungeonSpawner) {
            ((TileEntityDungeonSpawner)tile).setRoom(new Vector3((double)this.posX, (double)this.posY, (double)this.posZ), new Vector3((double)this.sizeX, (double)this.sizeY, (double)this.sizeZ));
        }
    }
}
