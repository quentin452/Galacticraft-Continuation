package micdoodle8.mods.galacticraft.api.vector;

import net.minecraft.entity.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraftforge.common.util.*;
import net.minecraft.nbt.*;

public class VectorD3 implements Cloneable
{
    public Vector3 position;
    public int dimensionID;
    
    public VectorD3(final double x, final double y, final double z, final int dimID) {
        this.position = new Vector3(x, y, z);
        this.dimensionID = dimID;
    }
    
    public VectorD3() {
        this(0.0, 0.0, 0.0, 0);
    }
    
    public VectorD3(final VectorD3 vector) {
        this(vector.position.x, vector.position.y, vector.position.z, vector.dimensionID);
    }
    
    public VectorD3(final Vector3 vector, final int dimID) {
        this(vector.x, vector.y, vector.z, dimID);
    }
    
    public VectorD3(final double amount) {
        this(amount, amount, amount, 0);
    }
    
    public VectorD3(final Entity par1) {
        this(par1.posX, par1.posY, par1.posZ, 0);
    }
    
    public VectorD3(final TileEntity par1) {
        this(par1.xCoord, par1.yCoord, par1.zCoord, 0);
    }
    
    public VectorD3(final Vec3 par1) {
        this(par1.xCoord, par1.yCoord, par1.zCoord, 0);
    }
    
    public VectorD3(final MovingObjectPosition par1) {
        this(par1.blockX, par1.blockY, par1.blockZ, 0);
    }
    
    public VectorD3(final ChunkCoordinates par1) {
        this(par1.posX, par1.posY, par1.posZ, 0);
    }
    
    public VectorD3(final ForgeDirection direction) {
        this(direction.offsetX, direction.offsetY, direction.offsetZ, 0);
    }
    
    public VectorD3(final NBTTagCompound nbt) {
        this(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"), nbt.getInteger("dimID"));
    }
    
    public VectorD3(final float rotationYaw, final float rotationPitch) {
        this(Math.cos(Math.toRadians(rotationYaw + 90.0f)), Math.sin(Math.toRadians(-rotationPitch)), Math.sin(Math.toRadians(rotationYaw + 90.0f)), 0);
    }
    
    public NBTTagCompound writeToNBT(final NBTTagCompound nbt) {
        this.position.writeToNBT(nbt);
        nbt.setInteger("dimID", this.dimensionID);
        return nbt;
    }
    
    public final VectorD3 clone() {
        return new VectorD3(this);
    }
}
