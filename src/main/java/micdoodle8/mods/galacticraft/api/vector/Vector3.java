package micdoodle8.mods.galacticraft.api.vector;

import net.minecraft.entity.*;
import net.minecraft.tileentity.*;
import net.minecraftforge.common.util.*;
import net.minecraft.nbt.*;
import net.minecraft.block.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import java.util.*;
import org.apache.commons.lang3.builder.*;
import org.lwjgl.util.vector.*;

public class Vector3 implements Cloneable
{
    public double x;
    public double y;
    public double z;
    
    public Vector3(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3() {
        this(0.0, 0.0, 0.0);
    }
    
    public Vector3(final Vector3 vector) {
        this(vector.x, vector.y, vector.z);
    }
    
    public Vector3(final double amount) {
        this(amount, amount, amount);
    }
    
    public Vector3(final Entity par1) {
        this(par1.posX, par1.posY, par1.posZ);
    }
    
    public Vector3(final TileEntity par1) {
        this(par1.xCoord, par1.yCoord, par1.zCoord);
    }
    
    public Vector3(final Vec3 par1) {
        this(par1.xCoord, par1.yCoord, par1.zCoord);
    }
    
    public Vector3(final MovingObjectPosition par1) {
        this(par1.blockX, par1.blockY, par1.blockZ);
    }
    
    public Vector3(final ChunkCoordinates par1) {
        this(par1.posX, par1.posY, par1.posZ);
    }
    
    public Vector3(final ForgeDirection direction) {
        this(direction.offsetX, direction.offsetY, direction.offsetZ);
    }
    
    public Vector3(final NBTTagCompound nbt) {
        this(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
    }
    
    @Deprecated
    public Vector3(final float rotationYaw, final float rotationPitch) {
        this(Math.cos(Math.toRadians(rotationYaw + 90.0f)), Math.sin(Math.toRadians(-rotationPitch)), Math.sin(Math.toRadians(rotationYaw + 90.0f)));
    }
    
    public int intX() {
        return (int)Math.floor(this.x);
    }
    
    public int intY() {
        return (int)Math.floor(this.y);
    }
    
    public int intZ() {
        return (int)Math.floor(this.z);
    }
    
    public float floatX() {
        return (float)this.x;
    }
    
    public float floatY() {
        return (float)this.y;
    }
    
    public float floatZ() {
        return (float)this.z;
    }
    
    public final Vector3 clone() {
        return new Vector3(this);
    }
    
    public Block getBlock(final IBlockAccess world) {
        return world.getBlock(this.intX(), this.intY(), this.intZ());
    }
    
    public int getBlockMetadata(final IBlockAccess world) {
        return world.getBlockMetadata(this.intX(), this.intY(), this.intZ());
    }
    
    public TileEntity getTileEntity(final IBlockAccess world) {
        return world.getTileEntity(this.intX(), this.intY(), this.intZ());
    }
    
    public boolean setBlock(final World world, final Block id, final int metadata, final int notify) {
        return world.setBlock(this.intX(), this.intY(), this.intZ(), id, metadata, notify);
    }
    
    public boolean setBlock(final World world, final Block id, final int metadata) {
        return this.setBlock(world, id, metadata, 3);
    }
    
    public boolean setBlock(final World world, final Block id) {
        return this.setBlock(world, id, 0);
    }
    
    public Vector2 toVector2() {
        return new Vector2(this.x, this.z);
    }
    
    public Vec3 toVec3() {
        return Vec3.createVectorHelper(this.x, this.y, this.z);
    }
    
    public ForgeDirection toForgeDirection() {
        for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            if (this.x == direction.offsetX && this.y == direction.offsetY && this.z == direction.offsetZ) {
                return direction;
            }
        }
        return ForgeDirection.UNKNOWN;
    }
    
    public double getMagnitude() {
        return Math.sqrt(this.getMagnitudeSquared());
    }
    
    public double getMagnitudeSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }
    
    public Vector3 normalize() {
        final double d = this.getMagnitude();
        if (d != 0.0) {
            this.scale(1.0 / d);
        }
        return this;
    }
    
    public static double distance(final Vector3 vec1, final Vector3 vec2) {
        return vec1.distance(vec2);
    }
    
    @Deprecated
    public double distanceTo(final Vector3 vector3) {
        return this.distance(vector3);
    }
    
    public double distance(final Vector3 compare) {
        final Vector3 difference = this.clone().difference(compare);
        return difference.getMagnitude();
    }
    
    public Vector3 invert() {
        this.scale(-1.0);
        return this;
    }
    
    public Vector3 translate(final Vector3 par1) {
        this.x += par1.x;
        this.y += par1.y;
        this.z += par1.z;
        return this;
    }
    
    public Vector3 translate(final double par1) {
        this.x += par1;
        this.y += par1;
        this.z += par1;
        return this;
    }
    
    public static Vector3 translate(final Vector3 translate, final Vector3 par1) {
        translate.x += par1.x;
        translate.y += par1.y;
        translate.z += par1.z;
        return translate;
    }
    
    public static Vector3 translate(final Vector3 translate, final double par1) {
        translate.x += par1;
        translate.y += par1;
        translate.z += par1;
        return translate;
    }
    
    @Deprecated
    public Vector3 add(final Vector3 amount) {
        return this.translate(amount);
    }
    
    @Deprecated
    public Vector3 add(final double amount) {
        return this.translate(amount);
    }
    
    @Deprecated
    public Vector3 subtract(final Vector3 amount) {
        return this.translate(amount.clone().invert());
    }
    
    @Deprecated
    public Vector3 subtract(final double amount) {
        return this.translate(-amount);
    }
    
    public Vector3 difference(final Vector3 amount) {
        return this.translate(amount.clone().invert());
    }
    
    public Vector3 difference(final double amount) {
        return this.translate(-amount);
    }
    
    public Vector3 scale(final double amount) {
        this.x *= amount;
        this.y *= amount;
        this.z *= amount;
        return this;
    }
    
    public Vector3 scale(final Vector3 amount) {
        this.x *= amount.x;
        this.y *= amount.y;
        this.z *= amount.z;
        return this;
    }
    
    public static Vector3 scale(final Vector3 vec, final double amount) {
        return vec.scale(amount);
    }
    
    public static Vector3 scale(final Vector3 vec, final Vector3 amount) {
        return vec.scale(amount);
    }
    
    @Deprecated
    public Vector3 multiply(final double amount) {
        return this.scale(amount);
    }
    
    @Deprecated
    public Vector3 multiply(final Vector3 amount) {
        return this.scale(amount);
    }
    
    public static Vector3 subtract(final Vector3 par1, final Vector3 par2) {
        return new Vector3(par1.x - par2.x, par1.y - par2.y, par1.z - par2.z);
    }
    
    @Deprecated
    public static Vector3 add(final Vector3 par1, final Vector3 par2) {
        return new Vector3(par1.x + par2.x, par1.y + par2.y, par1.z + par2.z);
    }
    
    @Deprecated
    public static Vector3 add(final Vector3 par1, final double par2) {
        return new Vector3(par1.x + par2, par1.y + par2, par1.z + par2);
    }
    
    @Deprecated
    public static Vector3 multiply(final Vector3 vec1, final Vector3 vec2) {
        return new Vector3(vec1.x * vec2.x, vec1.y * vec2.y, vec1.z * vec2.z);
    }
    
    @Deprecated
    public static Vector3 multiply(final Vector3 vec1, final double vec2) {
        return new Vector3(vec1.x * vec2, vec1.y * vec2, vec1.z * vec2);
    }
    
    public Vector3 round() {
        return new Vector3((double)Math.round(this.x), (double)Math.round(this.y), (double)Math.round(this.z));
    }
    
    public Vector3 ceil() {
        return new Vector3(Math.ceil(this.x), Math.ceil(this.y), Math.ceil(this.z));
    }
    
    public Vector3 floor() {
        return new Vector3(Math.floor(this.x), Math.floor(this.y), Math.floor(this.z));
    }
    
    public Vector3 toRound() {
        this.x = (double)Math.round(this.x);
        this.y = (double)Math.round(this.y);
        this.z = (double)Math.round(this.z);
        return this;
    }
    
    public Vector3 toCeil() {
        this.x = Math.ceil(this.x);
        this.y = Math.ceil(this.y);
        this.z = Math.ceil(this.z);
        return this;
    }
    
    public Vector3 toFloor() {
        this.x = Math.floor(this.x);
        this.y = Math.floor(this.y);
        this.z = Math.floor(this.z);
        return this;
    }
    
    public List<Entity> getEntitiesWithin(final World worldObj, final Class<? extends Entity> par1Class) {
        return (List<Entity>)worldObj.getEntitiesWithinAABB((Class)par1Class, AxisAlignedBB.getBoundingBox((double)this.intX(), (double)this.intY(), (double)this.intZ(), (double)(this.intX() + 1), (double)(this.intY() + 1), (double)(this.intZ() + 1)));
    }
    
    public Vector3 modifyPositionFromSide(final ForgeDirection side, final double amount) {
        return this.translate(new Vector3(side).scale(amount));
    }
    
    public Vector3 modifyPositionFromSide(final ForgeDirection side) {
        this.modifyPositionFromSide(side, 1.0);
        return this;
    }
    
    public Vector3 toCrossProduct(final Vector3 compare) {
        final double newX = this.y * compare.z - this.z * compare.y;
        final double newY = this.z * compare.x - this.x * compare.z;
        final double newZ = this.x * compare.y - this.y * compare.x;
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        return this;
    }
    
    public Vector3 crossProduct(final Vector3 compare) {
        return this.clone().toCrossProduct(compare);
    }
    
    public Vector3 xCrossProduct() {
        return new Vector3(0.0, this.z, -this.y);
    }
    
    public Vector3 zCrossProduct() {
        return new Vector3(-this.y, this.x, 0.0);
    }
    
    public double dotProduct(final Vector3 vec2) {
        return this.x * vec2.x + this.y * vec2.y + this.z * vec2.z;
    }
    
    public Vector3 getPerpendicular() {
        if (this.z == 0.0) {
            return this.zCrossProduct();
        }
        return this.xCrossProduct();
    }
    
    public boolean isZero() {
        return this.x == 0.0 && this.y == 0.0 && this.z == 0.0;
    }
    
    public Vector3 rotate(final float angle, final Vector3 axis) {
        return translateMatrix(getRotationMatrix(angle, axis), this.clone());
    }
    
    public double[] getRotationMatrix(float angle) {
        final double[] matrix = new double[16];
        final Vector3 axis = this.clone().normalize();
        final double x = axis.x;
        final double y = axis.y;
        final double z = axis.z;
        angle *= (float)0.0174532925;
        final float cos = (float)Math.cos(angle);
        final float ocos = 1.0f - cos;
        final float sin = (float)Math.sin(angle);
        matrix[0] = x * x * ocos + cos;
        matrix[1] = y * x * ocos + z * sin;
        matrix[2] = x * z * ocos - y * sin;
        matrix[4] = x * y * ocos - z * sin;
        matrix[5] = y * y * ocos + cos;
        matrix[6] = y * z * ocos + x * sin;
        matrix[8] = x * z * ocos + y * sin;
        matrix[9] = y * z * ocos - x * sin;
        matrix[10] = z * z * ocos + cos;
        matrix[15] = 1.0;
        return matrix;
    }
    
    public static Vector3 translateMatrix(final double[] matrix, final Vector3 translation) {
        final double x = translation.x * matrix[0] + translation.y * matrix[1] + translation.z * matrix[2] + matrix[3];
        final double y = translation.x * matrix[4] + translation.y * matrix[5] + translation.z * matrix[6] + matrix[7];
        final double z = translation.x * matrix[8] + translation.y * matrix[9] + translation.z * matrix[10] + matrix[11];
        translation.x = x;
        translation.y = y;
        translation.z = z;
        return translation;
    }
    
    public static double[] getRotationMatrix(final float angle, final Vector3 axis) {
        return axis.getRotationMatrix(angle);
    }
    
    public void rotate(final double yaw, final double pitch, final double roll) {
        final double yawRadians = Math.toRadians(yaw);
        final double pitchRadians = Math.toRadians(pitch);
        final double rollRadians = Math.toRadians(roll);
        final double x = this.x;
        final double y = this.y;
        final double z = this.z;
        this.x = x * Math.cos(yawRadians) * Math.cos(pitchRadians) + z * (Math.cos(yawRadians) * Math.sin(pitchRadians) * Math.sin(rollRadians) - Math.sin(yawRadians) * Math.cos(rollRadians)) + y * (Math.cos(yawRadians) * Math.sin(pitchRadians) * Math.cos(rollRadians) + Math.sin(yawRadians) * Math.sin(rollRadians));
        this.z = x * Math.sin(yawRadians) * Math.cos(pitchRadians) + z * (Math.sin(yawRadians) * Math.sin(pitchRadians) * Math.sin(rollRadians) + Math.cos(yawRadians) * Math.cos(rollRadians)) + y * (Math.sin(yawRadians) * Math.sin(pitchRadians) * Math.cos(rollRadians) - Math.cos(yawRadians) * Math.sin(rollRadians));
        this.y = -x * Math.sin(pitchRadians) + z * Math.cos(pitchRadians) * Math.sin(rollRadians) + y * Math.cos(pitchRadians) * Math.cos(rollRadians);
    }
    
    public void rotate(final double yaw, final double pitch) {
        this.rotate(yaw, pitch, 0.0);
    }
    
    public void rotate(final double yaw) {
        final double yawRadians = Math.toRadians(yaw);
        final double x = this.x;
        final double z = this.z;
        if (yaw != 0.0) {
            this.x = x * Math.cos(yawRadians) - z * Math.sin(yawRadians);
            this.z = x * Math.sin(yawRadians) + z * Math.cos(yawRadians);
        }
    }
    
    public static Vector3 getDeltaPositionFromRotation(final float rotationYaw, final float rotationPitch) {
        return new Vector3(rotationYaw, rotationPitch);
    }
    
    public double getAngle(final Vector3 vec2) {
        return anglePreNorm(this.clone().normalize(), vec2.clone().normalize());
    }
    
    public static double getAngle(final Vector3 vec1, final Vector3 vec2) {
        return vec1.getAngle(vec2);
    }
    
    public double anglePreNorm(final Vector3 vec2) {
        return Math.acos(this.dotProduct(vec2));
    }
    
    public static double anglePreNorm(final Vector3 vec1, final Vector3 vec2) {
        return Math.acos(vec1.clone().dotProduct(vec2));
    }
    
    @Deprecated
    public static Vector3 readFromNBT(final NBTTagCompound nbt) {
        return new Vector3(nbt);
    }
    
    public NBTTagCompound writeToNBT(final NBTTagCompound nbt) {
        nbt.setDouble("x", this.x);
        nbt.setDouble("y", this.y);
        nbt.setDouble("z", this.z);
        return nbt;
    }
    
    public static Vector3 UP() {
        return new Vector3(0.0, 1.0, 0.0);
    }
    
    public static Vector3 DOWN() {
        return new Vector3(0.0, -1.0, 0.0);
    }
    
    public static Vector3 NORTH() {
        return new Vector3(0.0, 0.0, -1.0);
    }
    
    public static Vector3 SOUTH() {
        return new Vector3(0.0, 0.0, 1.0);
    }
    
    public static Vector3 WEST() {
        return new Vector3(-1.0, 0.0, 0.0);
    }
    
    public static Vector3 EAST() {
        return new Vector3(1.0, 0.0, 0.0);
    }
    
    @Deprecated
    public MovingObjectPosition rayTraceEntities(final World world, final float rotationYaw, final float rotationPitch, final boolean collisionFlag, final double reachDistance) {
        return this.rayTraceEntities(world, rotationYaw, rotationPitch, reachDistance);
    }
    
    public MovingObjectPosition rayTraceEntities(final World world, final float rotationYaw, final float rotationPitch, final double reachDistance) {
        return this.rayTraceEntities(world, getDeltaPositionFromRotation(rotationYaw, rotationPitch).scale(reachDistance));
    }
    
    public MovingObjectPosition rayTraceEntities(final World world, final Vector3 target) {
        MovingObjectPosition pickedEntity = null;
        final Vec3 startingPosition = this.toVec3();
        final Vec3 look = target.toVec3();
        final double reachDistance = this.distance(target);
        final Vec3 reachPoint = Vec3.createVectorHelper(startingPosition.xCoord + look.xCoord * reachDistance, startingPosition.yCoord + look.yCoord * reachDistance, startingPosition.zCoord + look.zCoord * reachDistance);
        final double checkBorder = 1.1 * reachDistance;
        final AxisAlignedBB boxToScan = AxisAlignedBB.getBoundingBox(-checkBorder, -checkBorder, -checkBorder, checkBorder, checkBorder, checkBorder).offset(this.x, this.y, this.z);
        final List<Entity> entitiesHit = (List<Entity>)world.getEntitiesWithinAABBExcludingEntity((Entity)null, boxToScan);
        double closestEntity = reachDistance;
        if (entitiesHit == null || entitiesHit.isEmpty()) {
            return null;
        }
        for (final Entity entityHit : entitiesHit) {
            if (entityHit != null && entityHit.canBeCollidedWith() && entityHit.boundingBox != null) {
                final float border = entityHit.getCollisionBorderSize();
                final AxisAlignedBB aabb = entityHit.boundingBox.expand((double)border, (double)border, (double)border);
                final MovingObjectPosition hitMOP = aabb.calculateIntercept(startingPosition, reachPoint);
                if (hitMOP == null) {
                    continue;
                }
                if (aabb.isVecInside(startingPosition)) {
                    if (0.0 >= closestEntity && closestEntity != 0.0) {
                        continue;
                    }
                    pickedEntity = new MovingObjectPosition(entityHit);
                    if (pickedEntity == null) {
                        continue;
                    }
                    pickedEntity.hitVec = hitMOP.hitVec;
                    closestEntity = 0.0;
                }
                else {
                    final double distance = startingPosition.distanceTo(hitMOP.hitVec);
                    if (distance >= closestEntity && closestEntity != 0.0) {
                        continue;
                    }
                    pickedEntity = new MovingObjectPosition(entityHit);
                    pickedEntity.hitVec = hitMOP.hitVec;
                    closestEntity = distance;
                }
            }
        }
        return pickedEntity;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.x).append(this.y).append(this.z).hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof Vector3) {
            final Vector3 vector3 = (Vector3)o;
            return new EqualsBuilder().append(this.x, vector3.x).append(this.y, vector3.y).append(this.z, vector3.z).isEquals();
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "Vector3 [" + this.x + "," + this.y + "," + this.z + "]";
    }
    
    public Vector3f toVector3f() {
        return new Vector3f((float)this.x, (float)this.y, (float)this.z);
    }
}
