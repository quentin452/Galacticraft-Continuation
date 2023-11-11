package micdoodle8.mods.galacticraft.core.world.gen.dungeon;

import net.minecraft.block.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.init.*;
import java.util.*;

public class MapGenDungeon
{
    public ArrayList<DungeonRoom> bossRooms;
    public ArrayList<DungeonRoom> treasureRooms;
    public ArrayList<DungeonRoom> otherRooms;
    public final Block DUNGEON_WALL_ID;
    public final int DUNGEON_WALL_META;
    public final int RANGE;
    public final int HALLWAY_LENGTH;
    public final int HALLWAY_HEIGHT;
    public static boolean useArrays;
    public World worldObj;
    private final ArrayList<DungeonRoom> rooms;
    
    public MapGenDungeon(final Block wallID, final int wallMeta, final int range, final int hallwayLength, final int hallwayHeight) {
        this.bossRooms = new ArrayList<DungeonRoom>();
        this.treasureRooms = new ArrayList<DungeonRoom>();
        this.otherRooms = new ArrayList<DungeonRoom>();
        this.rooms = new ArrayList<DungeonRoom>();
        this.DUNGEON_WALL_ID = wallID;
        this.DUNGEON_WALL_META = wallMeta;
        this.RANGE = range;
        this.HALLWAY_LENGTH = hallwayLength;
        this.HALLWAY_HEIGHT = hallwayHeight;
    }
    
    public void generateUsingArrays(final World world, final long seed, final int x, final int y, final int z, final int chunkX, final int chunkZ, final Block[] blocks, final byte[] metas) {
        this.worldObj = world;
        final ChunkCoordinates dungeonCoords = this.getDungeonNear(seed, chunkX, chunkZ);
        if (dungeonCoords != null) {
            this.generate(world, new Random(seed * dungeonCoords.posX * dungeonCoords.posZ * 24789L), dungeonCoords.posX, y, dungeonCoords.posZ, chunkX, chunkZ, blocks, metas, true);
        }
    }
    
    public void generateUsingSetBlock(final World world, final long seed, final int x, final int y, final int z) {
        final ChunkCoordinates dungeonCoords = this.getDungeonNear(seed, x / 16, y / 16);
        if (dungeonCoords != null) {
            this.generate(world, new Random(seed * dungeonCoords.posX * dungeonCoords.posZ * 24789L), x, y, z, x, z, null, null, false);
        }
    }
    
    public void generate(final World world, final Random rand, final int x, final int y, final int z, final int chunkX, final int chunkZ, final Block[] blocks, final byte[] metas, final boolean useArrays) {
        MapGenDungeon.useArrays = useArrays;
        this.worldObj = world;
        final List<DungeonBoundingBox> boundingBoxes = new ArrayList<DungeonBoundingBox>();
        final int length = rand.nextInt(4) + 5;
        DungeonRoom currentRoom = DungeonRoom.makeRoom(this, rand, x, y, z, ForgeDirection.DOWN);
        currentRoom.generate(blocks, metas, chunkX, chunkZ);
        this.rooms.add(currentRoom);
        final DungeonBoundingBox cbb = currentRoom.getBoundingBox();
        boundingBoxes.add(cbb);
        this.generateEntranceCrater(blocks, metas, x + (cbb.maxX - cbb.minX) / 2, y, z + (cbb.maxZ - cbb.minZ) / 2, chunkX, chunkZ);
        for (int i = 0; i <= length; ++i) {
            for (int j = 0; j < 8; ++j) {
                int offsetX = 0;
                int offsetZ = 0;
                ForgeDirection entranceDir;
                final ForgeDirection dir = entranceDir = this.randDir(rand);
                switch (dir) {
                    case EAST: {
                        offsetZ = this.HALLWAY_LENGTH + rand.nextInt(15);
                        if (!rand.nextBoolean()) {
                            break;
                        }
                        if (rand.nextBoolean()) {
                            entranceDir = ForgeDirection.NORTH;
                            offsetX = this.HALLWAY_LENGTH + rand.nextInt(15);
                            break;
                        }
                        entranceDir = ForgeDirection.SOUTH;
                        offsetX = -this.HALLWAY_LENGTH - rand.nextInt(15);
                        break;
                    }
                    case NORTH: {
                        offsetX = this.HALLWAY_LENGTH + rand.nextInt(15);
                        if (!rand.nextBoolean()) {
                            break;
                        }
                        if (rand.nextBoolean()) {
                            entranceDir = ForgeDirection.EAST;
                            offsetZ = this.HALLWAY_LENGTH + rand.nextInt(15);
                            break;
                        }
                        entranceDir = ForgeDirection.WEST;
                        offsetZ = -this.HALLWAY_LENGTH - rand.nextInt(15);
                        break;
                    }
                    case SOUTH: {
                        offsetX = -this.HALLWAY_LENGTH - rand.nextInt(15);
                        if (!rand.nextBoolean()) {
                            break;
                        }
                        if (rand.nextBoolean()) {
                            entranceDir = ForgeDirection.EAST;
                            offsetZ = this.HALLWAY_LENGTH + rand.nextInt(15);
                            break;
                        }
                        entranceDir = ForgeDirection.WEST;
                        offsetZ = -this.HALLWAY_LENGTH - rand.nextInt(15);
                        break;
                    }
                    case WEST: {
                        offsetZ = -this.HALLWAY_LENGTH - rand.nextInt(15);
                        if (!rand.nextBoolean()) {
                            break;
                        }
                        if (rand.nextBoolean()) {
                            entranceDir = ForgeDirection.NORTH;
                            offsetX = this.HALLWAY_LENGTH + rand.nextInt(15);
                            break;
                        }
                        entranceDir = ForgeDirection.SOUTH;
                        offsetX = -this.HALLWAY_LENGTH - rand.nextInt(15);
                        break;
                    }
                }
                DungeonRoom possibleRoom = DungeonRoom.makeRoom(this, rand, currentRoom.posX + offsetX, y, currentRoom.posZ + offsetZ, entranceDir.getOpposite());
                if (i == length - 1) {
                    possibleRoom = DungeonRoom.makeBossRoom(this, rand, currentRoom.posX + offsetX, y, currentRoom.posZ + offsetZ, entranceDir.getOpposite());
                }
                if (i == length) {
                    possibleRoom = DungeonRoom.makeTreasureRoom(this, rand, currentRoom.posX + offsetX, y, currentRoom.posZ + offsetZ, entranceDir.getOpposite());
                }
                final DungeonBoundingBox possibleRoomBb = possibleRoom.getBoundingBox();
                final DungeonBoundingBox currentRoomBb = currentRoom.getBoundingBox();
                if (!this.isIntersecting(possibleRoomBb, boundingBoxes)) {
                    final int curCenterX = (currentRoomBb.minX + currentRoomBb.maxX) / 2;
                    final int curCenterZ = (currentRoomBb.minZ + currentRoomBb.maxZ) / 2;
                    final int possibleCenterX = (possibleRoomBb.minX + possibleRoomBb.maxX) / 2;
                    final int possibleCenterZ = (possibleRoomBb.minZ + possibleRoomBb.maxZ) / 2;
                    final int corridorX = this.clamp((curCenterX + possibleCenterX) / 2, Math.max(currentRoomBb.minX + 1, possibleRoomBb.minX + 1), Math.min(currentRoomBb.maxX - 1, possibleRoomBb.maxX - 1));
                    final int corridorZ = this.clamp((curCenterZ + possibleCenterZ) / 2, Math.max(currentRoomBb.minZ + 1, possibleRoomBb.minZ + 1), Math.min(currentRoomBb.maxZ - 1, possibleRoomBb.maxZ - 1));
                    if (offsetX == 0 || offsetZ == 0) {
                        DungeonBoundingBox corridor1 = null;
                        switch (dir) {
                            case EAST: {
                                corridor1 = new DungeonBoundingBox(corridorX - 1, currentRoomBb.maxZ, corridorX, possibleRoomBb.minZ - 1);
                                break;
                            }
                            case NORTH: {
                                corridor1 = new DungeonBoundingBox(currentRoomBb.maxX, corridorZ - 1, possibleRoomBb.minX - 1, corridorZ);
                                break;
                            }
                            case SOUTH: {
                                corridor1 = new DungeonBoundingBox(possibleRoomBb.maxX, corridorZ - 1, currentRoomBb.minX - 1, corridorZ);
                                break;
                            }
                            case WEST: {
                                corridor1 = new DungeonBoundingBox(corridorX - 1, possibleRoomBb.maxZ, corridorX, currentRoomBb.minZ - 1);
                                break;
                            }
                        }
                        if (corridor1 != null && !this.isIntersecting(corridor1, boundingBoxes) && !corridor1.isOverlapping(possibleRoomBb)) {
                            boundingBoxes.add(possibleRoomBb);
                            boundingBoxes.add(corridor1);
                            currentRoom = possibleRoom;
                            currentRoom.generate(blocks, metas, chunkX, chunkZ);
                            this.rooms.add(currentRoom);
                            this.genCorridor(corridor1, rand, possibleRoom.posY, chunkX, chunkZ, dir, blocks, metas, false);
                            break;
                        }
                    }
                    else {
                        DungeonBoundingBox corridor1 = null;
                        DungeonBoundingBox corridor2 = null;
                        ForgeDirection dir2 = ForgeDirection.EAST;
                        int extraLength = 0;
                        if (rand.nextInt(6) == 0) {
                            extraLength = rand.nextInt(7);
                        }
                        switch (dir) {
                            case EAST: {
                                corridor1 = new DungeonBoundingBox(curCenterX - 1, currentRoomBb.maxZ, curCenterX + 1, possibleCenterZ - 1);
                                if (offsetX > 0) {
                                    corridor2 = new DungeonBoundingBox(corridor1.minX - extraLength, corridor1.maxZ + 1, possibleRoomBb.minX, corridor1.maxZ + 3);
                                    dir2 = ForgeDirection.NORTH;
                                    break;
                                }
                                corridor2 = new DungeonBoundingBox(possibleRoomBb.maxX, corridor1.maxZ + 1, corridor1.maxX + extraLength, corridor1.maxZ + 3);
                                dir2 = ForgeDirection.SOUTH;
                                break;
                            }
                            case NORTH: {
                                corridor1 = new DungeonBoundingBox(currentRoomBb.maxX, curCenterZ - 1, possibleCenterX - 1, curCenterZ + 1);
                                if (offsetZ > 0) {
                                    corridor2 = new DungeonBoundingBox(corridor1.maxX + 1, corridor1.minZ - extraLength, corridor1.maxX + 4, possibleRoomBb.minZ);
                                    dir2 = ForgeDirection.EAST;
                                    break;
                                }
                                corridor2 = new DungeonBoundingBox(corridor1.maxX + 1, possibleRoomBb.maxZ, corridor1.maxX + 4, corridor1.maxZ + extraLength);
                                dir2 = ForgeDirection.WEST;
                                break;
                            }
                            case SOUTH: {
                                corridor1 = new DungeonBoundingBox(possibleCenterX + 1, curCenterZ - 1, currentRoomBb.minX - 1, curCenterZ + 1);
                                if (offsetZ > 0) {
                                    corridor2 = new DungeonBoundingBox(corridor1.minX - 3, corridor1.minZ - extraLength, corridor1.minX - 1, possibleRoomBb.minZ);
                                    dir2 = ForgeDirection.EAST;
                                    break;
                                }
                                corridor2 = new DungeonBoundingBox(corridor1.minX - 3, possibleRoomBb.maxZ, corridor1.minX - 1, corridor1.maxZ + extraLength);
                                dir2 = ForgeDirection.WEST;
                                break;
                            }
                            case WEST: {
                                corridor1 = new DungeonBoundingBox(curCenterX - 1, possibleCenterZ + 1, curCenterX + 1, currentRoomBb.minZ - 1);
                                if (offsetX > 0) {
                                    corridor2 = new DungeonBoundingBox(corridor1.minX - extraLength, corridor1.minZ - 3, possibleRoomBb.minX, corridor1.minZ - 1);
                                    dir2 = ForgeDirection.NORTH;
                                    break;
                                }
                                corridor2 = new DungeonBoundingBox(possibleRoomBb.maxX, corridor1.minZ - 3, corridor1.maxX + extraLength, corridor1.minZ - 1);
                                dir2 = ForgeDirection.SOUTH;
                                break;
                            }
                        }
                        if (corridor1 != null && corridor2 != null && !this.isIntersecting(corridor1, boundingBoxes) && !this.isIntersecting(corridor2, boundingBoxes) && !corridor1.isOverlapping(possibleRoomBb) && !corridor2.isOverlapping(possibleRoomBb)) {
                            boundingBoxes.add(possibleRoomBb);
                            boundingBoxes.add(corridor1);
                            boundingBoxes.add(corridor2);
                            currentRoom = possibleRoom;
                            currentRoom.generate(blocks, metas, chunkX, chunkZ);
                            this.rooms.add(currentRoom);
                            this.genCorridor(corridor2, rand, possibleRoom.posY, chunkX, chunkZ, dir2, blocks, metas, true);
                            this.genCorridor(corridor1, rand, possibleRoom.posY, chunkX, chunkZ, dir, blocks, metas, false);
                            break;
                        }
                    }
                }
            }
        }
    }
    
    private void genCorridor(final DungeonBoundingBox corridor, final Random rand, final int y, final int cx, final int cz, final ForgeDirection dir, final Block[] blocks, final byte[] metas, final boolean doubleCorridor) {
        for (int i = corridor.minX - 1; i <= corridor.maxX + 1; ++i) {
        Label_0839:
            for (int k = corridor.minZ - 1; k <= corridor.maxZ + 1; ++k) {
                for (int j = y - 1; j <= y + this.HALLWAY_HEIGHT; ++j) {
                    boolean flag = false;
                    int flag2 = -1;
                    switch (dir) {
                        case EAST: {
                            if (k == corridor.minZ - 1 && !doubleCorridor) {
                                continue Label_0839;
                            }
                            if (k == corridor.maxZ + 1) {
                                continue Label_0839;
                            }
                            if (doubleCorridor && k == corridor.minZ - 1) {
                                flag = true;
                            }
                            if (i == corridor.minX - 1 || i == corridor.maxX + 1 || j == y - 1 || j == y + this.HALLWAY_HEIGHT) {
                                flag = true;
                            }
                            if ((i == corridor.minX || i == corridor.maxX) && k % 4 == 0 && j == y + 2) {
                                flag2 = ((i == corridor.minX) ? 2 : 1);
                                break;
                            }
                            break;
                        }
                        case WEST: {
                            if (k == corridor.minZ - 1) {
                                continue Label_0839;
                            }
                            if (k == corridor.maxZ + 1 && !doubleCorridor) {
                                continue Label_0839;
                            }
                            if (doubleCorridor && k == corridor.maxX + 1) {
                                flag = true;
                            }
                            if (i == corridor.minX - 1 || i == corridor.maxX + 1 || j == y - 1 || j == y + this.HALLWAY_HEIGHT) {
                                flag = true;
                            }
                            if ((i == corridor.minX || i == corridor.maxX) && k % 4 == 0 && j == y + 2) {
                                flag2 = ((i == corridor.minX) ? 2 : 1);
                                break;
                            }
                            break;
                        }
                        case NORTH: {
                            if (i == corridor.minX - 1 && !doubleCorridor) {
                                continue Label_0839;
                            }
                            if (i == corridor.maxX + 1) {
                                continue Label_0839;
                            }
                            if (i == corridor.minX - 1) {
                                flag = true;
                            }
                            if (k == corridor.minZ - 1 || k == corridor.maxZ + 1 || j == y - 1 || j == y + this.HALLWAY_HEIGHT) {
                                flag = true;
                            }
                            if ((k == corridor.minZ || k == corridor.maxZ) && i % 4 == 0 && j == y + 2) {
                                flag2 = ((k == corridor.minZ) ? 4 : 3);
                                break;
                            }
                            break;
                        }
                        case SOUTH: {
                            if (i == corridor.minX - 1) {
                                continue Label_0839;
                            }
                            if (i == corridor.maxX + 1 && !doubleCorridor) {
                                continue Label_0839;
                            }
                            if (i == corridor.maxX + 1) {
                                flag = true;
                            }
                            if (k == corridor.minZ - 1 || k == corridor.maxZ + 1 || j == y - 1 || j == y + this.HALLWAY_HEIGHT) {
                                flag = true;
                            }
                            if ((k == corridor.minZ || k == corridor.maxZ) && i % 4 == 0 && j == y + 2) {
                                flag2 = ((k == corridor.minZ) ? 4 : 3);
                                break;
                            }
                            break;
                        }
                    }
                    if (!flag) {
                        if (flag2 != -1) {
                            if (OxygenUtil.noAtmosphericCombustion(this.worldObj.provider)) {
                                this.placeBlock(blocks, metas, i, j, k, cx, cz, GCBlocks.unlitTorch, 0);
                                this.worldObj.scheduleBlockUpdateWithPriority(i, j, k, GCBlocks.unlitTorch, 40, 0);
                            }
                            else {
                                this.placeBlock(blocks, metas, i, j, k, cx, cz, Blocks.torch, 0);
                                this.worldObj.scheduleBlockUpdateWithPriority(i, j, k, Blocks.torch, 40, 0);
                            }
                        }
                        else {
                            this.placeBlock(blocks, metas, i, j, k, cx, cz, Blocks.air, 0);
                        }
                    }
                    else {
                        this.placeBlock(blocks, metas, i, j, k, cx, cz, this.DUNGEON_WALL_ID, this.DUNGEON_WALL_META);
                    }
                }
            }
        }
    }
    
    public void handleTileEntities(final Random rand) {
        final ArrayList<DungeonRoom> rooms = new ArrayList<DungeonRoom>();
        rooms.addAll(this.rooms);
        this.rooms.clear();
        for (final DungeonRoom room : rooms) {
            room.handleTileEntities(rand);
        }
    }
    
    protected boolean canGenDungeonAtCoords(final long worldSeed, int i, int j) {
        final byte numChunks = 44;
        final byte offsetChunks = 0;
        final int oldi = i;
        final int oldj = j;
        if (i < 0) {
            i -= 43;
        }
        if (j < 0) {
            j -= 43;
        }
        int randX = i / 44;
        int randZ = j / 44;
        final long dungeonSeed = randX * 341873128712L + randZ * 132897987541L + worldSeed + 4291754L + this.worldObj.provider.dimensionId;
        final Random rand = new Random(dungeonSeed);
        randX *= 44;
        randZ *= 44;
        randX += rand.nextInt(44);
        randZ += rand.nextInt(44);
        return oldi == randX && oldj == randZ;
    }
    
    public void generateEntranceCrater(final Block[] blocks, final byte[] meta, final int x, final int y, final int z, final int cx, final int cz) {
        final int range = 18;
        int maxLevel = 0;
        for (int i = -18; i <= 18; ++i) {
            for (int k = -18; k <= 18; ++k) {
                int j = 200;
                while (j > 0) {
                    --j;
                    final Block block = this.getBlock(blocks, x + i, j, z + k, cx + i / 16, cz + k / 16);
                    if (Blocks.air != block && block != null) {
                        break;
                    }
                }
                maxLevel = Math.max(maxLevel, j);
            }
        }
        for (int i = x - 18; i < x + 18; ++i) {
            for (int k = z - 18; k < z + 18; ++k) {
                final double xDev = (i - x) / 10.0;
                final double zDev = (k - z) / 10.0;
                final double distance = xDev * xDev + zDev * zDev;
                final int depth = (int)Math.abs(1.0 / (distance + 1.0E-5));
                int helper = 0;
                for (int l = maxLevel + 3; l > 0; --l) {
                    if ((Blocks.air != this.getBlock(blocks, i, l - 1, k, cx, cz) || this.getBlock(blocks, i, l, k, cx, cz) == this.DUNGEON_WALL_ID) && helper <= depth) {
                        this.placeBlock(blocks, meta, i, l, k, cx, cz, Blocks.air, 0);
                        ++helper;
                    }
                    if (helper > depth) {
                        break;
                    }
                    if (l <= y + 1) {
                        break;
                    }
                }
            }
        }
    }
    
    public ChunkCoordinates getDungeonNear(final long worldSeed, final int i, final int j) {
        final int range = 16;
        for (int x = i - 16; x <= i + 16; ++x) {
            for (int z = j - 16; z <= j + 16; ++z) {
                if (this.canGenDungeonAtCoords(worldSeed, x, z)) {
                    return new ChunkCoordinates(x * 16 + 8, 0, z * 16 + 8);
                }
            }
        }
        return null;
    }
    
    private void placeBlock(final Block[] blocks, final byte[] metas, int x, final int y, int z, int cx, int cz, final Block id, final int meta) {
        if (MapGenDungeon.useArrays) {
            cx *= 16;
            cz *= 16;
            x -= cx;
            z -= cz;
            if (x < 0 || x >= 16 || z < 0 || z >= 16) {
                return;
            }
            final int index = this.getIndex(x, y, z);
            blocks[index] = id;
            metas[index] = (byte)meta;
        }
        else {
            this.worldObj.setBlock(x, y, z, id, meta, 0);
        }
    }
    
    private Block getBlock(final Block[] blocks, int x, final int y, int z, int cx, int cz) {
        if (!MapGenDungeon.useArrays) {
            return this.worldObj.getBlock(x, y, z);
        }
        cx *= 16;
        cz *= 16;
        x -= cx;
        z -= cz;
        if (x < 0 || x >= 16 || z < 0 || z >= 16) {
            return Blocks.air;
        }
        return blocks[this.getIndex(x, y, z)];
    }
    
    private int getIndex(final int x, final int y, final int z) {
        return (x * 16 + z) * 256 + y;
    }
    
    private ForgeDirection randDir(final Random rand) {
        return ForgeDirection.values()[rand.nextInt(ForgeDirection.VALID_DIRECTIONS.length)];
    }
    
    private boolean isIntersecting(final DungeonBoundingBox bb, final List<DungeonBoundingBox> dungeonBbs) {
        for (final DungeonBoundingBox bb2 : dungeonBbs) {
            if (bb.isOverlapping(bb2)) {
                return true;
            }
        }
        return false;
    }
    
    private int clamp(final int x, final int min, final int max) {
        if (x < min) {
            return min;
        }
        if (x > max) {
            return max;
        }
        return x;
    }
    
    static {
        MapGenDungeon.useArrays = false;
    }
}
