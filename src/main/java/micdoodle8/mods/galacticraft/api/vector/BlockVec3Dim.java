package micdoodle8.mods.galacticraft.api.vector;

import net.minecraft.world.chunk.*;
import net.minecraft.entity.*;
import net.minecraft.tileentity.*;
import net.minecraft.block.*;
import net.minecraft.crash.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.init.*;
import net.minecraftforge.common.util.*;
import net.minecraft.nbt.*;
import cpw.mods.fml.common.*;
import net.minecraft.server.*;
import cpw.mods.fml.client.*;
import cpw.mods.fml.relauncher.*;

public class BlockVec3Dim implements Cloneable
{
    public int x;
    public int y;
    public int z;
    public int dim;
    private static Chunk chunkCached;
    public static int chunkCacheDim;
    private static int chunkCacheX;
    private static int chunkCacheZ;
    public static final BlockVec3Dim INVALID_VECTOR;
    
    public BlockVec3Dim() {
        this(0, 0, 0, 0);
    }
    
    public BlockVec3Dim(final int x, final int y, final int z, final int d) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = d;
    }
    
    public BlockVec3Dim(final Entity par1) {
        this.x = (int)Math.floor(par1.posX);
        this.y = (int)Math.floor(par1.posY);
        this.z = (int)Math.floor(par1.posZ);
        this.dim = par1.dimension;
    }
    
    public BlockVec3Dim(final TileEntity par1) {
        this.x = par1.xCoord;
        this.y = par1.yCoord;
        this.z = par1.zCoord;
        this.dim = par1.getWorldObj().provider.dimensionId;
    }
    
    public final BlockVec3Dim clone() {
        return new BlockVec3Dim(this.x, this.y, this.z, this.dim);
    }
    
    public Block getBlockID() {
        if (this.y < 0 || this.y >= 256 || this.x < -30000000 || this.z < -30000000 || this.x >= 30000000 || this.z >= 30000000) {
            return null;
        }
        final World world = this.getWorldForId(this.dim);
        if (world == null) {
            return null;
        }
        final int chunkx = this.x >> 4;
        final int chunkz = this.z >> 4;
        try {
            if (BlockVec3Dim.chunkCacheX == chunkx && BlockVec3Dim.chunkCacheZ == chunkz && BlockVec3Dim.chunkCacheDim == world.provider.dimensionId && BlockVec3Dim.chunkCached.isChunkLoaded) {
                return BlockVec3Dim.chunkCached.getBlock(this.x & 0xF, this.y, this.z & 0xF);
            }
            Chunk chunk = null;
            chunk = (BlockVec3Dim.chunkCached = world.getChunkFromChunkCoords(chunkx, chunkz));
            BlockVec3Dim.chunkCacheDim = world.provider.dimensionId;
            BlockVec3Dim.chunkCacheX = chunkx;
            BlockVec3Dim.chunkCacheZ = chunkz;
            return chunk.getBlock(this.x & 0xF, this.y, this.z & 0xF);
        }
        catch (Throwable throwable) {
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Oxygen Sealer thread: Exception getting block type in world");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Requested block coordinates");
            crashreportcategory.addCrashSection("Location", (Object)CrashReportCategory.getLocationInfo(this.x, this.y, this.z));
            throw new ReportedException(crashreport);
        }
    }
    
    public Block getBlockID_noChunkLoad() {
        if (this.y < 0 || this.y >= 256 || this.x < -30000000 || this.z < -30000000 || this.x >= 30000000 || this.z >= 30000000) {
            return null;
        }
        final World world = this.getWorldForId(this.dim);
        if (world == null) {
            return null;
        }
        final int chunkx = this.x >> 4;
        final int chunkz = this.z >> 4;
        try {
            if (!world.getChunkProvider().chunkExists(chunkx, chunkz)) {
                return Blocks.bedrock;
            }
            if (BlockVec3Dim.chunkCacheX == chunkx && BlockVec3Dim.chunkCacheZ == chunkz && BlockVec3Dim.chunkCacheDim == world.provider.dimensionId && BlockVec3Dim.chunkCached.isChunkLoaded) {
                return BlockVec3Dim.chunkCached.getBlock(this.x & 0xF, this.y, this.z & 0xF);
            }
            Chunk chunk = null;
            chunk = (BlockVec3Dim.chunkCached = world.getChunkFromChunkCoords(chunkx, chunkz));
            BlockVec3Dim.chunkCacheDim = world.provider.dimensionId;
            BlockVec3Dim.chunkCacheX = chunkx;
            BlockVec3Dim.chunkCacheZ = chunkz;
            return chunk.getBlock(this.x & 0xF, this.y, this.z & 0xF);
        }
        catch (Throwable throwable) {
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Oxygen Sealer thread: Exception getting block type in world");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Requested block coordinates");
            crashreportcategory.addCrashSection("Location", (Object)CrashReportCategory.getLocationInfo(this.x, this.y, this.z));
            throw new ReportedException(crashreport);
        }
    }
    
    public Block getBlock() {
        final World world = this.getWorldForId(this.dim);
        if (world == null) {
            return null;
        }
        return world.getBlock(this.x, this.y, this.z);
    }
    
    public BlockVec3Dim modifyPositionFromSide(final ForgeDirection side, final int amount) {
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
    
    public BlockVec3Dim newVecSide(final int side) {
        final BlockVec3Dim vec = new BlockVec3Dim(this.x, this.y, this.z, this.dim);
        switch (side) {
            case 0: {
                final BlockVec3Dim blockVec3Dim = vec;
                --blockVec3Dim.y;
                return vec;
            }
            case 1: {
                final BlockVec3Dim blockVec3Dim2 = vec;
                ++blockVec3Dim2.y;
                return vec;
            }
            case 2: {
                final BlockVec3Dim blockVec3Dim3 = vec;
                --blockVec3Dim3.z;
                return vec;
            }
            case 3: {
                final BlockVec3Dim blockVec3Dim4 = vec;
                ++blockVec3Dim4.z;
                return vec;
            }
            case 4: {
                final BlockVec3Dim blockVec3Dim5 = vec;
                --blockVec3Dim5.x;
                return vec;
            }
            case 5: {
                final BlockVec3Dim blockVec3Dim6 = vec;
                ++blockVec3Dim6.x;
                return vec;
            }
            default: {
                return vec;
            }
        }
    }
    
    public BlockVec3Dim modifyPositionFromSide(final ForgeDirection side) {
        return this.modifyPositionFromSide(side, 1);
    }
    
    @Override
    public int hashCode() {
        return (((this.z * 431 + this.x) * 379 + this.y) * 373 + this.dim) * 7;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof BlockVec3Dim) {
            final BlockVec3Dim vector = (BlockVec3Dim)o;
            return this.x == vector.x && this.y == vector.y && this.z == vector.z && this.dim == vector.dim;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "BlockVec3 " + this.dim + ":[" + this.x + "," + this.y + "," + this.z + "]";
    }
    
    public TileEntity getTileEntity() {
        final World world = this.getWorldForId(this.dim);
        if (world == null) {
            return null;
        }
        return world.getTileEntity(this.x, this.y, this.z);
    }
    
    public TileEntity getTileEntityNoLoad() {
        final World world = this.getWorldForId(this.dim);
        if (world == null) {
            return null;
        }
        if (world.blockExists(this.x, this.y, this.z)) {
            return world.getTileEntity(this.x, this.y, this.z);
        }
        return null;
    }
    
    public int getBlockMetadata() {
        final World world = this.getWorldForId(this.dim);
        if (world == null) {
            return 0;
        }
        return world.getBlockMetadata(this.x, this.y, this.z);
    }
    
    public static BlockVec3Dim readFromNBT(final NBTTagCompound nbtCompound) {
        final BlockVec3Dim tempVector = new BlockVec3Dim();
        tempVector.x = nbtCompound.getInteger("x");
        tempVector.y = nbtCompound.getInteger("y");
        tempVector.z = nbtCompound.getInteger("z");
        tempVector.dim = nbtCompound.getInteger("dim");
        return tempVector;
    }
    
    public NBTTagCompound writeToNBT(final NBTTagCompound par1NBTTagCompound) {
        par1NBTTagCompound.setInteger("x", this.x);
        par1NBTTagCompound.setInteger("y", this.y);
        par1NBTTagCompound.setInteger("z", this.z);
        par1NBTTagCompound.setInteger("dim", this.dim);
        return par1NBTTagCompound;
    }
    
    public BlockVec3Dim(final NBTTagCompound par1NBTTagCompound) {
        this.x = par1NBTTagCompound.getInteger("x");
        this.y = par1NBTTagCompound.getInteger("y");
        this.z = par1NBTTagCompound.getInteger("z");
        this.dim = par1NBTTagCompound.getInteger("dim");
    }
    
    public double getMagnitude() {
        return Math.sqrt(this.getMagnitudeSquared());
    }
    
    public int getMagnitudeSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }
    
    public void setBlock(final Block block) {
        final World world = this.getWorldForId(this.dim);
        if (world == null) {
            return;
        }
        world.setBlock(this.x, this.y, this.z, block, 0, 3);
    }
    
    public boolean blockExists() {
        final World world = this.getWorldForId(this.dim);
        return world != null && world.blockExists(this.x, this.y, this.z);
    }
    
    public int distanceSquared(final BlockVec3 vector) {
        final int var2 = vector.x - this.x;
        final int var3 = vector.y - this.y;
        final int var4 = vector.z - this.z;
        return var2 * var2 + var3 * var3 + var4 * var4;
    }
    
    private World getWorldForId(final int dimensionID) {
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER) {
            return this.getWorldForIdClient(dimensionID);
        }
        final MinecraftServer theServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (theServer == null) {
            return null;
        }
        return (World)theServer.worldServerForDimension(dimensionID);
    }
    
    @SideOnly(Side.CLIENT)
    private World getWorldForIdClient(final int dimensionID) {
        final World world = (World)FMLClientHandler.instance().getClient().theWorld;
        if (world != null && world.provider.dimensionId == dimensionID) {
            return world;
        }
        return null;
    }
    
    static {
        BlockVec3Dim.chunkCacheDim = Integer.MAX_VALUE;
        BlockVec3Dim.chunkCacheX = 1876000;
        BlockVec3Dim.chunkCacheZ = 1876000;
        INVALID_VECTOR = new BlockVec3Dim(-1, -1, -1, -2);
    }
}
