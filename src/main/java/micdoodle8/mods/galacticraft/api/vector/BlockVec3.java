package micdoodle8.mods.galacticraft.api.vector;

import net.minecraft.world.chunk.*;
import net.minecraft.entity.*;
import net.minecraft.tileentity.*;
import net.minecraft.block.*;
import net.minecraft.crash.*;
import net.minecraft.init.*;
import net.minecraft.world.*;
import net.minecraftforge.common.util.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraft.world.gen.*;

public class BlockVec3 implements Cloneable
{
    public int x;
    public int y;
    public int z;
    public int sideDoneBits;
    private static Chunk chunkCached;
    public static int chunkCacheDim;
    private static int chunkCacheX;
    private static int chunkCacheZ;
    public static final BlockVec3 INVALID_VECTOR;
    
    public BlockVec3() {
        this(0, 0, 0);
    }
    
    public BlockVec3(final int x, final int y, final int z) {
        this.sideDoneBits = 0;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public BlockVec3(final Entity par1) {
        this.sideDoneBits = 0;
        this.x = (int)Math.floor(par1.posX);
        this.y = (int)Math.floor(par1.posY);
        this.z = (int)Math.floor(par1.posZ);
    }
    
    public BlockVec3(final TileEntity par1) {
        this.sideDoneBits = 0;
        this.x = par1.xCoord;
        this.y = par1.yCoord;
        this.z = par1.zCoord;
    }
    
    public final BlockVec3 clone() {
        return new BlockVec3(this.x, this.y, this.z);
    }
    
    public Block getBlockID(final World world) {
        if (this.y < 0 || this.y >= 256 || this.x < -30000000 || this.z < -30000000 || this.x >= 30000000 || this.z >= 30000000) {
            return null;
        }
        final int chunkx = this.x >> 4;
        final int chunkz = this.z >> 4;
        try {
            if (BlockVec3.chunkCacheX == chunkx && BlockVec3.chunkCacheZ == chunkz && BlockVec3.chunkCacheDim == world.provider.dimensionId && BlockVec3.chunkCached.isChunkLoaded) {
                return BlockVec3.chunkCached.getBlock(this.x & 0xF, this.y, this.z & 0xF);
            }
            Chunk chunk = null;
            chunk = (BlockVec3.chunkCached = world.getChunkFromChunkCoords(chunkx, chunkz));
            BlockVec3.chunkCacheDim = world.provider.dimensionId;
            BlockVec3.chunkCacheX = chunkx;
            BlockVec3.chunkCacheZ = chunkz;
            return chunk.getBlock(this.x & 0xF, this.y, this.z & 0xF);
        }
        catch (Throwable throwable) {
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Oxygen Sealer thread: Exception getting block type in world");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Requested block coordinates");
            crashreportcategory.addCrashSection("Location", (Object)CrashReportCategory.getLocationInfo(this.x, this.y, this.z));
            throw new ReportedException(crashreport);
        }
    }
    
    public Block getBlockID_noChunkLoad(final World world) {
        if (this.y < 0 || this.y >= 256 || this.x < -30000000 || this.z < -30000000 || this.x >= 30000000 || this.z >= 30000000) {
            return null;
        }
        final int chunkx = this.x >> 4;
        final int chunkz = this.z >> 4;
        try {
            if (!world.getChunkProvider().chunkExists(chunkx, chunkz)) {
                return Blocks.bedrock;
            }
            if (BlockVec3.chunkCacheX == chunkx && BlockVec3.chunkCacheZ == chunkz && BlockVec3.chunkCacheDim == world.provider.dimensionId && BlockVec3.chunkCached.isChunkLoaded) {
                return BlockVec3.chunkCached.getBlock(this.x & 0xF, this.y, this.z & 0xF);
            }
            Chunk chunk = null;
            chunk = (BlockVec3.chunkCached = world.getChunkFromChunkCoords(chunkx, chunkz));
            BlockVec3.chunkCacheDim = world.provider.dimensionId;
            BlockVec3.chunkCacheX = chunkx;
            BlockVec3.chunkCacheZ = chunkz;
            return chunk.getBlock(this.x & 0xF, this.y, this.z & 0xF);
        }
        catch (Throwable throwable) {
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Oxygen Sealer thread: Exception getting block type in world");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Requested block coordinates");
            crashreportcategory.addCrashSection("Location", (Object)CrashReportCategory.getLocationInfo(this.x, this.y, this.z));
            throw new ReportedException(crashreport);
        }
    }
    
    public Block getBlock(final IBlockAccess par1iBlockAccess) {
        return par1iBlockAccess.getBlock(this.x, this.y, this.z);
    }
    
    public Block getBlockIDsafe_noChunkLoad(final World world) {
        if (this.y < 0 || this.y >= 256) {
            return null;
        }
        final int chunkx = this.x >> 4;
        final int chunkz = this.z >> 4;
        try {
            if (!world.getChunkProvider().chunkExists(chunkx, chunkz)) {
                return Blocks.bedrock;
            }
            if (BlockVec3.chunkCacheX == chunkx && BlockVec3.chunkCacheZ == chunkz && BlockVec3.chunkCacheDim == world.provider.dimensionId && BlockVec3.chunkCached.isChunkLoaded) {
                return BlockVec3.chunkCached.getBlock(this.x & 0xF, this.y, this.z & 0xF);
            }
            Chunk chunk = null;
            chunk = (BlockVec3.chunkCached = world.getChunkFromChunkCoords(chunkx, chunkz));
            BlockVec3.chunkCacheDim = world.provider.dimensionId;
            BlockVec3.chunkCacheX = chunkx;
            BlockVec3.chunkCacheZ = chunkz;
            return chunk.getBlock(this.x & 0xF, this.y, this.z & 0xF);
        }
        catch (Throwable throwable) {
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Oxygen Sealer thread: Exception getting block type in world");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Requested block coordinates");
            crashreportcategory.addCrashSection("Location", (Object)CrashReportCategory.getLocationInfo(this.x, this.y, this.z));
            throw new ReportedException(crashreport);
        }
    }
    
    public BlockVec3 add(final BlockVec3 par1) {
        this.x += par1.x;
        this.y += par1.y;
        this.z += par1.z;
        return this;
    }
    
    public BlockVec3 translate(final BlockVec3 par1) {
        this.x += par1.x;
        this.y += par1.y;
        this.z += par1.z;
        return this;
    }
    
    public BlockVec3 translate(final int par1x, final int par1y, final int par1z) {
        this.x += par1x;
        this.y += par1y;
        this.z += par1z;
        return this;
    }
    
    public static BlockVec3 add(final BlockVec3 par1, final BlockVec3 a) {
        return new BlockVec3(par1.x + a.x, par1.y + a.y, par1.z + a.z);
    }
    
    public BlockVec3 subtract(final BlockVec3 par1) {
        this.x -= par1.x;
        this.y -= par1.y;
        this.z -= par1.z;
        return this;
    }
    
    public BlockVec3 scale(final int par1) {
        this.x *= par1;
        this.y *= par1;
        this.z *= par1;
        return this;
    }
    
    public BlockVec3 modifyPositionFromSide(final ForgeDirection side, final int amount) {
        switch (side.ordinal()) {
            case 0: {
                this.y -= amount;
                break;
            }
            case 1: {
                this.y += amount;
                break;
            }
            case 2: {
                this.z -= amount;
                break;
            }
            case 3: {
                this.z += amount;
                break;
            }
            case 4: {
                this.x -= amount;
                break;
            }
            case 5: {
                this.x += amount;
                break;
            }
        }
        return this;
    }
    
    public BlockVec3 newVecSide(final int side) {
        final BlockVec3 vec = new BlockVec3(this.x, this.y, this.z);
        vec.sideDoneBits = (1 << (side ^ 0x1)) + (side << 6);
        switch (side) {
            case 0: {
                final BlockVec3 blockVec3 = vec;
                --blockVec3.y;
                return vec;
            }
            case 1: {
                final BlockVec3 blockVec4 = vec;
                ++blockVec4.y;
                return vec;
            }
            case 2: {
                final BlockVec3 blockVec5 = vec;
                --blockVec5.z;
                return vec;
            }
            case 3: {
                final BlockVec3 blockVec6 = vec;
                ++blockVec6.z;
                return vec;
            }
            case 4: {
                final BlockVec3 blockVec7 = vec;
                --blockVec7.x;
                return vec;
            }
            case 5: {
                final BlockVec3 blockVec8 = vec;
                ++blockVec8.x;
                return vec;
            }
            default: {
                return vec;
            }
        }
    }
    
    public BlockVec3 modifyPositionFromSide(final ForgeDirection side) {
        return this.modifyPositionFromSide(side, 1);
    }
    
    @Override
    public int hashCode() {
        return ((this.y * 379 + this.x) * 373 + this.z) * 7;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof BlockVec3) {
            final BlockVec3 vector = (BlockVec3)o;
            return this.x == vector.x && this.y == vector.y && this.z == vector.z;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "[" + this.x + "," + this.y + "," + this.z + "]";
    }
    
    public TileEntity getTileEntity(final IBlockAccess world) {
        return world.getTileEntity(this.x, this.y, this.z);
    }
    
    public TileEntity getTileEntityOnSide(final World world, final ForgeDirection side) {
        int x = this.x;
        int y = this.y;
        int z = this.z;
        switch (side.ordinal()) {
            case 0: {
                --y;
                break;
            }
            case 1: {
                ++y;
                break;
            }
            case 2: {
                --z;
                break;
            }
            case 3: {
                ++z;
                break;
            }
            case 4: {
                --x;
                break;
            }
            case 5: {
                ++x;
                break;
            }
            default: {
                return null;
            }
        }
        if (world.blockExists(x, y, z)) {
            return world.getTileEntity(x, y, z);
        }
        return null;
    }
    
    public TileEntity getTileEntityOnSide(final World world, final int side) {
        int x = this.x;
        int y = this.y;
        int z = this.z;
        switch (side) {
            case 0: {
                --y;
                break;
            }
            case 1: {
                ++y;
                break;
            }
            case 2: {
                --z;
                break;
            }
            case 3: {
                ++z;
                break;
            }
            case 4: {
                --x;
                break;
            }
            case 5: {
                ++x;
                break;
            }
            default: {
                return null;
            }
        }
        if (world.blockExists(x, y, z)) {
            return world.getTileEntity(x, y, z);
        }
        return null;
    }
    
    public boolean blockOnSideHasSolidFace(final World world, final int side) {
        int x = this.x;
        int y = this.y;
        int z = this.z;
        switch (side) {
            case 0: {
                --y;
                break;
            }
            case 1: {
                ++y;
                break;
            }
            case 2: {
                --z;
                break;
            }
            case 3: {
                ++z;
                break;
            }
            case 4: {
                --x;
                break;
            }
            case 5: {
                ++x;
                break;
            }
            default: {
                return false;
            }
        }
        return world.getBlock(x, y, z).isSideSolid((IBlockAccess)world, x, y, z, ForgeDirection.getOrientation(side ^ 0x1));
    }
    
    public Block getBlockOnSide(final World world, final int side) {
        int x = this.x;
        int y = this.y;
        int z = this.z;
        switch (side) {
            case 0: {
                --y;
                break;
            }
            case 1: {
                ++y;
                break;
            }
            case 2: {
                --z;
                break;
            }
            case 3: {
                ++z;
                break;
            }
            case 4: {
                --x;
                break;
            }
            case 5: {
                ++x;
                break;
            }
            default: {
                return null;
            }
        }
        if (world.blockExists(x, y, z)) {
            return world.getBlock(x, y, z);
        }
        return null;
    }
    
    public int getBlockMetadata(final IBlockAccess world) {
        return world.getBlockMetadata(this.x, this.y, this.z);
    }
    
    public static BlockVec3 readFromNBT(final NBTTagCompound nbtCompound) {
        final BlockVec3 tempVector = new BlockVec3();
        tempVector.x = nbtCompound.getInteger("x");
        tempVector.y = nbtCompound.getInteger("y");
        tempVector.z = nbtCompound.getInteger("z");
        return tempVector;
    }
    
    public int distanceTo(final BlockVec3 vector) {
        final int var2 = vector.x - this.x;
        final int var3 = vector.y - this.y;
        final int var4 = vector.z - this.z;
        return MathHelper.floor_double(Math.sqrt(var2 * var2 + var3 * var3 + var4 * var4));
    }
    
    public int distanceSquared(final BlockVec3 vector) {
        final int var2 = vector.x - this.x;
        final int var3 = vector.y - this.y;
        final int var4 = vector.z - this.z;
        return var2 * var2 + var3 * var3 + var4 * var4;
    }
    
    public NBTTagCompound writeToNBT(final NBTTagCompound par1NBTTagCompound) {
        par1NBTTagCompound.setInteger("x", this.x);
        par1NBTTagCompound.setInteger("y", this.y);
        par1NBTTagCompound.setInteger("z", this.z);
        return par1NBTTagCompound;
    }
    
    public BlockVec3(final NBTTagCompound par1NBTTagCompound) {
        this.sideDoneBits = 0;
        this.x = par1NBTTagCompound.getInteger("x");
        this.y = par1NBTTagCompound.getInteger("y");
        this.z = par1NBTTagCompound.getInteger("z");
    }
    
    public NBTTagCompound writeToNBT(final NBTTagCompound par1NBTTagCompound, final String prefix) {
        par1NBTTagCompound.setInteger(prefix + "_x", this.x);
        par1NBTTagCompound.setInteger(prefix + "_y", this.y);
        par1NBTTagCompound.setInteger(prefix + "_z", this.z);
        return par1NBTTagCompound;
    }
    
    public static BlockVec3 readFromNBT(final NBTTagCompound par1NBTTagCompound, final String prefix) {
        final Integer readX = par1NBTTagCompound.getInteger(prefix + "_x");
        if (readX == null) {
            return null;
        }
        final Integer readY = par1NBTTagCompound.getInteger(prefix + "_y");
        if (readY == null) {
            return null;
        }
        final Integer readZ = par1NBTTagCompound.getInteger(prefix + "_z");
        if (readZ == null) {
            return null;
        }
        return new BlockVec3(readX, readY, readZ);
    }
    
    public double getMagnitude() {
        return Math.sqrt(this.getMagnitudeSquared());
    }
    
    public int getMagnitudeSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }
    
    public void setBlock(final World worldObj, final Block block) {
        worldObj.setBlock(this.x, this.y, this.z, block, 0, 3);
    }
    
    public boolean blockExists(final World world) {
        return world.blockExists(this.x, this.y, this.z);
    }
    
    public void setSideDone(final int side) {
        this.sideDoneBits |= 1 << side;
    }
    
    public TileEntity getTileEntityForce(final World world) {
        final int chunkx = this.x >> 4;
        final int chunkz = this.z >> 4;
        if (world.getChunkProvider().chunkExists(chunkx, chunkz)) {
            return world.getTileEntity(this.x, this.y, this.z);
        }
        final Chunk chunk = ((ChunkProviderServer)world.getChunkProvider()).originalLoadChunk(chunkx, chunkz);
        return chunk.func_150806_e(this.x & 0xF, this.y, this.z & 0xF);
    }
    
    static {
        BlockVec3.chunkCacheDim = Integer.MAX_VALUE;
        BlockVec3.chunkCacheX = 1876000;
        BlockVec3.chunkCacheZ = 1876000;
        INVALID_VECTOR = new BlockVec3(-1, -1, -1);
    }
}
