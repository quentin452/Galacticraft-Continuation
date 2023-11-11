package micdoodle8.mods.galacticraft.core.world.gen.dungeon;

import net.minecraftforge.common.util.*;
import java.util.*;
import net.minecraft.block.*;
import net.minecraft.init.*;
import net.minecraft.tileentity.*;
import net.minecraft.item.*;
import net.minecraftforge.common.*;
import net.minecraft.util.*;
import net.minecraft.inventory.*;

public class RoomChestsMoon extends DungeonRoom
{
    int sizeX;
    int sizeY;
    int sizeZ;
    private final ArrayList<ChunkCoordinates> chests;
    
    public RoomChestsMoon(final MapGenDungeon dungeon, final int posX, final int posY, final int posZ, final ForgeDirection entranceDir) {
        super(dungeon, posX, posY, posZ, entranceDir);
        this.chests = new ArrayList<ChunkCoordinates>();
        if (this.worldObj != null) {
            final Random rand = new Random(this.worldObj.getSeed() * posX * posY * 57L * posZ);
            this.sizeX = rand.nextInt(5) + 6;
            this.sizeY = rand.nextInt(2) + 4;
            this.sizeZ = rand.nextInt(5) + 6;
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
                    }
                }
            }
        }
        final int hx = (this.posX + this.posX + this.sizeX) / 2;
        final int hz = (this.posZ + this.posZ + this.sizeZ) / 2;
        if (this.placeBlock(chunk, meta, hx, this.posY, hz, cx, cz, (Block)Blocks.chest, 0)) {
            this.chests.add(new ChunkCoordinates(hx, this.posY, hz));
        }
    }
    
    public DungeonBoundingBox getBoundingBox() {
        return new DungeonBoundingBox(this.posX, this.posZ, this.posX + this.sizeX, this.posZ + this.sizeZ);
    }
    
    protected DungeonRoom makeRoom(final MapGenDungeon dungeon, final int x, final int y, final int z, final ForgeDirection dir) {
        return new RoomChestsMoon(dungeon, x, y, z, dir);
    }
    
    protected void handleTileEntities(final Random rand) {
        if (!this.chests.isEmpty()) {
            this.worldObj.setBlock(this.chests.get(0).posX, this.chests.get(0).posY, this.chests.get(0).posZ, (Block)Blocks.chest, 0, 2);
            final TileEntityChest chest = (TileEntityChest)this.worldObj.getTileEntity(this.chests.get(0).posX, this.chests.get(0).posY, this.chests.get(0).posZ);
            if (chest != null) {
                for (int i = 0; i < chest.getSizeInventory(); ++i) {
                    chest.setInventorySlotContents(i, (ItemStack)null);
                }
                final ChestGenHooks info = ChestGenHooks.getInfo("dungeonChest");
                WeightedRandomChestContent.generateChestContents(rand, info.getItems(rand), (IInventory)chest, info.getCount(rand));
            }
            this.chests.clear();
        }
    }
}
