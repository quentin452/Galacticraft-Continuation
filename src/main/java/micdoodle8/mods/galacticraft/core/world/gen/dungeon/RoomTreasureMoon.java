package micdoodle8.mods.galacticraft.core.world.gen.dungeon;

import net.minecraft.util.*;
import net.minecraftforge.common.util.*;
import net.minecraft.block.*;
import net.minecraft.init.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.tileentity.*;
import java.util.*;

public class RoomTreasureMoon extends DungeonRoom
{
    int sizeX;
    int sizeY;
    int sizeZ;
    private final HashSet<ChunkCoordinates> chests;
    
    public RoomTreasureMoon(final MapGenDungeon dungeon, final int posX, final int posY, final int posZ, final ForgeDirection entranceDir) {
        super(dungeon, posX, posY, posZ, entranceDir);
        this.chests = new HashSet<ChunkCoordinates>();
        if (this.worldObj != null) {
            final Random rand = new Random(this.worldObj.getSeed() * posX * posY * 57L * posZ);
            this.sizeX = rand.nextInt(6) + 7;
            this.sizeY = rand.nextInt(2) + 5;
            this.sizeZ = rand.nextInt(6) + 7;
        }
    }
    
    public void generate(final Block[] chunk, final byte[] meta, final int cx, final int cz) {
        for (int i = this.posX - 1; i <= this.posX + this.sizeX; ++i) {
            for (int k = this.posZ - 1; k <= this.posZ + this.sizeZ; ++k) {
                for (int j = this.posY - 1; j <= this.posY + this.sizeY; ++j) {
                    if (i == this.posX - 1 || i == this.posX + this.sizeX || j == this.posY - 1 || j == this.posY + this.sizeY || k == this.posZ - 1 || k == this.posZ + this.sizeZ) {
                        this.placeBlock(chunk, meta, i, j, k, cx, cz, this.dungeonInstance.DUNGEON_WALL_ID, this.dungeonInstance.DUNGEON_WALL_META);
                    }
                    else if ((i == this.posX || i == this.posX + this.sizeX - 1) && (k == this.posZ || k == this.posZ + this.sizeZ - 1)) {
                        this.placeBlock(chunk, meta, i, j, k, cx, cz, Blocks.glowstone, 0);
                    }
                    else {
                        this.placeBlock(chunk, meta, i, j, k, cx, cz, Blocks.air, 0);
                    }
                }
            }
        }
        final int hx = (this.posX + this.posX + this.sizeX) / 2;
        final int hz = (this.posZ + this.posZ + this.sizeZ) / 2;
        if (this.placeBlock(chunk, meta, hx, this.posY, hz, cx, cz, GCBlocks.treasureChestTier1, 0)) {
            this.chests.add(new ChunkCoordinates(hx, this.posY, hz));
        }
    }
    
    public DungeonBoundingBox getBoundingBox() {
        return new DungeonBoundingBox(this.posX, this.posZ, this.posX + this.sizeX, this.posZ + this.sizeZ);
    }
    
    protected DungeonRoom makeRoom(final MapGenDungeon dungeon, final int x, final int y, final int z, final ForgeDirection dir) {
        return new RoomTreasureMoon(dungeon, x, y, z, dir);
    }
    
    protected void handleTileEntities(final Random rand) {
        if (!this.chests.isEmpty()) {
            final HashSet<ChunkCoordinates> removeList = new HashSet<ChunkCoordinates>();
            for (final ChunkCoordinates coords : this.chests) {
                this.worldObj.setBlock(coords.posX, coords.posY, coords.posZ, GCBlocks.treasureChestTier1, 0, 3);
                this.worldObj.setTileEntity(coords.posX, coords.posY, coords.posZ, (TileEntity)new TileEntityTreasureChest(1));
                removeList.add(coords);
            }
            this.chests.removeAll(removeList);
        }
    }
}
