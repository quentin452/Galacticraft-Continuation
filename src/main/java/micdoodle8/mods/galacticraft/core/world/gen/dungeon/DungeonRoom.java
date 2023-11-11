package micdoodle8.mods.galacticraft.core.world.gen.dungeon;

import net.minecraft.world.*;
import net.minecraftforge.common.util.*;
import net.minecraft.block.*;
import java.util.*;

public abstract class DungeonRoom
{
    public final MapGenDungeon dungeonInstance;
    public World worldObj;
    public int posX;
    public int posY;
    public int posZ;
    public ForgeDirection entranceDir;
    
    public DungeonRoom(final MapGenDungeon dungeon, final int posX, final int posY, final int posZ, final ForgeDirection entranceDir) {
        this.dungeonInstance = dungeon;
        this.worldObj = ((dungeon != null) ? dungeon.worldObj : null);
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.entranceDir = entranceDir;
    }
    
    public abstract void generate(final Block[] p0, final byte[] p1, final int p2, final int p3);
    
    public abstract DungeonBoundingBox getBoundingBox();
    
    protected abstract DungeonRoom makeRoom(final MapGenDungeon p0, final int p1, final int p2, final int p3, final ForgeDirection p4);
    
    protected abstract void handleTileEntities(final Random p0);
    
    public static DungeonRoom makeRoom(final MapGenDungeon dungeon, final Random rand, final int x, final int y, final int z, final ForgeDirection dir) {
        return dungeon.otherRooms.get(rand.nextInt(dungeon.otherRooms.size())).makeRoom(dungeon, x, y, z, dir);
    }
    
    public static DungeonRoom makeBossRoom(final MapGenDungeon dungeon, final Random rand, final int x, final int y, final int z, final ForgeDirection dir) {
        return dungeon.bossRooms.get(rand.nextInt(dungeon.bossRooms.size())).makeRoom(dungeon, x, y, z, dir);
    }
    
    public static DungeonRoom makeTreasureRoom(final MapGenDungeon dungeon, final Random rand, final int x, final int y, final int z, final ForgeDirection dir) {
        return dungeon.treasureRooms.get(rand.nextInt(dungeon.treasureRooms.size())).makeRoom(dungeon, x, y, z, dir);
    }
    
    protected boolean placeBlock(final Block[] blocks, final byte[] metas, int x, final int y, int z, int cx, int cz, final Block id, final int meta) {
        if (MapGenDungeon.useArrays) {
            cx *= 16;
            cz *= 16;
            x -= cx;
            z -= cz;
            if (x < 0 || x >= 16 || z < 0 || z >= 16) {
                return false;
            }
            final int index = this.getIndex(x, y, z);
            blocks[index] = id;
            metas[index] = (byte)meta;
        }
        else {
            this.worldObj.setBlock(x, y, z, id, meta, 0);
        }
        return true;
    }
    
    private int getIndex(final int x, final int y, final int z) {
        return (x * 16 + z) * 256 + y;
    }
}
