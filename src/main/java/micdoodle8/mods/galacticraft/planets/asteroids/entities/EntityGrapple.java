package micdoodle8.mods.galacticraft.planets.asteroids.entities;

import net.minecraft.entity.*;
import net.minecraft.block.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.api.world.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import net.minecraft.block.material.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.planets.asteroids.network.*;
import cpw.mods.fml.common.network.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraft.util.*;
import java.util.*;
import net.minecraft.nbt.*;
import net.minecraft.init.*;
import net.minecraft.item.*;

public class EntityGrapple extends Entity implements IProjectile
{
    private BlockVec3 hitVec;
    private Block hitBlock;
    private int inData;
    private boolean inGround;
    public int canBePickedUp;
    public int arrowShake;
    public EntityPlayer shootingEntity;
    private int ticksInGround;
    private int ticksInAir;
    public float rotationRoll;
    public float prevRotationRoll;
    public boolean pullingPlayer;

    public EntityGrapple(final World par1World) {
        super(par1World);
        this.renderDistanceWeight = 10.0;
        this.ignoreFrustumCheck = false;
        this.yOffset = -1.5f;
        this.setSize(0.75f, 0.75f);
    }

    public EntityGrapple(final World par1World, final EntityPlayer shootingEntity, final float par3) {
        super(par1World);
        this.renderDistanceWeight = 10.0;
        this.shootingEntity = shootingEntity;
        this.setSize(0.75f, 0.75f);
        if (shootingEntity != null) {
            this.canBePickedUp = 1;
            this.setLocationAndAngles(shootingEntity.posX, shootingEntity.posY + shootingEntity.getEyeHeight(), shootingEntity.posZ, shootingEntity.rotationYaw, shootingEntity.rotationPitch);
        }
        this.motionX = -MathHelper.sin(this.rotationYaw / 180.0f * 3.1415927f) * MathHelper.cos(this.rotationPitch / 180.0f * 3.1415927f);
        this.motionZ = MathHelper.cos(this.rotationYaw / 180.0f * 3.1415927f) * MathHelper.cos(this.rotationPitch / 180.0f * 3.1415927f);
        this.motionY = -MathHelper.sin(this.rotationPitch / 180.0f * 3.1415927f);
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        this.yOffset = -1.5f;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, par3 * 1.5f, 1.0f);
    }

    protected void entityInit() {
        this.dataWatcher.addObject(10, (Object)0);
        this.dataWatcher.addObject(11, (Object)0);
    }

    public void setThrowableHeading(double par1, double par3, double par5, final float par7, final float par8) {
        final float f2 = MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5);
        par1 /= f2;
        par3 /= f2;
        par5 /= f2;
        par1 += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * par8;
        par3 += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * par8;
        par5 += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * par8;
        par1 *= par7;
        par3 *= par7;
        par5 *= par7;
        this.motionX = par1;
        this.motionY = par3;
        this.motionZ = par5;
        final float f3 = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
        final float n = (float)(Math.atan2(par1, par5) * 180.0 / 3.141592653589793);
        this.rotationYaw = n;
        this.prevRotationYaw = n;
        final float n2 = (float)(Math.atan2(par3, f3) * 180.0 / 3.141592653589793);
        this.rotationPitch = n2;
        this.prevRotationPitch = n2;
        this.ticksInGround = 0;
    }

    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(final double par1, final double par3, final double par5, final float par7, final float par8, final int par9) {
        this.setPosition(par1, par3, par5);
        this.setRotation(par7, par8);
    }

    public void setPosition(final double x, final double y, final double z) {
        super.setPosition(x, y, z);
    }

    @SideOnly(Side.CLIENT)
    public void setVelocity(final double par1, final double par3, final double par5) {
        this.motionX = par1;
        this.motionY = par3;
        this.motionZ = par5;
        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            final float f = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
            final float n = (float)(Math.atan2(par1, par5) * 180.0 / 3.141592653589793);
            this.rotationYaw = n;
            this.prevRotationYaw = n;
            final float n2 = (float)(Math.atan2(par3, f) * 180.0 / 3.141592653589793);
            this.rotationPitch = n2;
            this.prevRotationPitch = n2;
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.ticksInGround = 0;
        }
    }

    public void onUpdate() {
        super.onUpdate();
        this.prevRotationRoll = this.rotationRoll;
        if (!this.worldObj.isRemote) {
            this.updateShootingEntity();
            if (this.getPullingEntity()) {
                final EntityPlayer shootingEntity = this.getShootingEntity();
                if (shootingEntity != null) {
                    final double deltaPosition = this.getDistanceSqToEntity((Entity)shootingEntity);
                    final Vector3 mot = new Vector3(shootingEntity.motionX, shootingEntity.motionY, shootingEntity.motionZ);
                    if (mot.getMagnitudeSquared() < 0.01 && this.pullingPlayer) {
                        if (deltaPosition < 10.0) {
                            this.onCollideWithPlayer(shootingEntity);
                        }
                        this.updatePullingEntity(false);
                        this.setDead();
                    }
                    this.pullingPlayer = true;
                }
            }
        }
        else if (this.getPullingEntity()) {
            final EntityPlayer shootingEntity = this.getShootingEntity();
            if (shootingEntity != null) {
                shootingEntity.setVelocity((this.posX - shootingEntity.posX) / 12.0, (this.posY - shootingEntity.posY) / 12.0, (this.posZ - shootingEntity.posZ) / 12.0);
                if (shootingEntity.worldObj.isRemote && shootingEntity.worldObj.provider instanceof IZeroGDimension) {
                    FreefallHandler.updateFreefall(shootingEntity);
                }
            }
        }
        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            final float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            final float n = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / 3.141592653589793);
            this.rotationYaw = n;
            this.prevRotationYaw = n;
            final float n2 = (float)(Math.atan2(this.motionY, f) * 180.0 / 3.141592653589793);
            this.rotationPitch = n2;
            this.prevRotationPitch = n2;
        }
        if (this.hitVec != null) {
            final Block block = this.worldObj.getBlock(this.hitVec.x, this.hitVec.y, this.hitVec.z);
            if (block.getMaterial() != Material.air) {
                block.setBlockBoundsBasedOnState((IBlockAccess)this.worldObj, this.hitVec.x, this.hitVec.y, this.hitVec.z);
                final AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(this.worldObj, this.hitVec.x, this.hitVec.y, this.hitVec.z);
                if (axisalignedbb != null && axisalignedbb.isVecInside(Vec3.createVectorHelper(this.posX, this.posY, this.posZ))) {
                    this.inGround = true;
                }
            }
        }
        if (this.arrowShake > 0) {
            --this.arrowShake;
        }
        if (this.inGround) {
            if (this.hitVec != null) {
                final Block block = this.worldObj.getBlock(this.hitVec.x, this.hitVec.y, this.hitVec.z);
                final int j = this.worldObj.getBlockMetadata(this.hitVec.x, this.hitVec.y, this.hitVec.z);
                if (block == this.hitBlock && j == this.inData) {
                    if (this.shootingEntity != null) {
                        this.shootingEntity.motionX = (this.posX - this.shootingEntity.posX) / 16.0;
                        this.shootingEntity.motionY = (this.posY - this.shootingEntity.posY) / 16.0;
                        this.shootingEntity.motionZ = (this.posZ - this.shootingEntity.posZ) / 16.0;
                        if (this.shootingEntity instanceof EntityPlayerMP) {
                            GalacticraftCore.handler.preventFlyingKicks((EntityPlayerMP)this.shootingEntity);
                        }
                    }
                    if (!this.worldObj.isRemote && this.ticksInGround < 5) {
                        this.updatePullingEntity(true);
                    }
                    ++this.ticksInGround;
                    if (this.ticksInGround == 1200) {
                        this.setDead();
                    }
                }
                else {
                    this.inGround = false;
                    this.motionX *= this.rand.nextFloat() * 0.2f;
                    this.motionY *= this.rand.nextFloat() * 0.2f;
                    this.motionZ *= this.rand.nextFloat() * 0.2f;
                    this.ticksInGround = 0;
                    this.ticksInAir = 0;
                }
            }
        }
        else {
            this.rotationRoll += 5.0f;
            ++this.ticksInAir;
            if (!this.worldObj.isRemote) {
                this.updatePullingEntity(false);
            }
            if (this.shootingEntity != null && this.getDistanceSqToEntity((Entity)this.shootingEntity) >= 1600.0) {
                this.setDead();
            }
            Vec3 vec31 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            Vec3 vec32 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition movingobjectposition = this.worldObj.func_147447_a(vec31, vec32, false, true, false);
            vec31 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            vec32 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            if (movingobjectposition != null) {
                vec32 = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
            }
            Entity entity = null;
            final List list = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
            double d0 = 0.0;
            for (int i = 0; i < list.size(); ++i) {
                final Entity entity2 = (Entity) list.get(i);
                if (entity2.canBeCollidedWith() && (entity2 != this.shootingEntity || this.ticksInAir >= 5)) {
                    final float f2 = 0.3f;
                    final AxisAlignedBB axisalignedbb2 = entity2.boundingBox.expand((double)f2, (double)f2, (double)f2);
                    final MovingObjectPosition movingobjectposition2 = axisalignedbb2.calculateIntercept(vec31, vec32);
                    if (movingobjectposition2 != null) {
                        final double d2 = vec31.distanceTo(movingobjectposition2.hitVec);
                        if (d2 < d0 || d0 == 0.0) {
                            entity = entity2;
                            d0 = d2;
                        }
                    }
                }
            }
            if (entity != null) {
                movingobjectposition = new MovingObjectPosition(entity);
            }
            if (movingobjectposition != null && movingobjectposition.entityHit != null && movingobjectposition.entityHit instanceof EntityPlayer) {
                final EntityPlayer entityplayer = (EntityPlayer)movingobjectposition.entityHit;
                if (entityplayer.capabilities.disableDamage || (this.shootingEntity != null && !this.shootingEntity.canAttackPlayer(entityplayer))) {
                    movingobjectposition = null;
                }
            }
            if (movingobjectposition != null && movingobjectposition.entityHit == null) {
                this.hitVec = new BlockVec3(movingobjectposition.blockX, movingobjectposition.blockY, movingobjectposition.blockZ);
                this.hitBlock = this.worldObj.getBlock(this.hitVec.x, this.hitVec.y, this.hitVec.z);
                this.inData = this.worldObj.getBlockMetadata(this.hitVec.x, this.hitVec.y, this.hitVec.z);
                this.motionX = (float)(movingobjectposition.hitVec.xCoord - this.posX);
                this.motionY = (float)(movingobjectposition.hitVec.yCoord - this.posY);
                this.motionZ = (float)(movingobjectposition.hitVec.zCoord - this.posZ);
                final float motion = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                this.posX -= this.motionX / motion * 0.05000000074505806;
                this.posY -= this.motionY / motion * 0.05000000074505806;
                this.posZ -= this.motionZ / motion * 0.05000000074505806;
                this.playSound("random.bowhit", 1.0f, 1.2f / (this.rand.nextFloat() * 0.2f + 0.9f));
                this.inGround = true;
                this.arrowShake = 7;
                if (this.hitBlock.getMaterial() != Material.air) {
                    this.hitBlock.onEntityCollidedWithBlock(this.worldObj, this.hitVec.x, this.hitVec.y, this.hitVec.z, (Entity)this);
                }
            }
            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            final float motion = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / 3.141592653589793);
            this.rotationPitch = (float)(Math.atan2(this.motionY, motion) * 180.0 / 3.141592653589793);
            while (this.rotationPitch - this.prevRotationPitch < -180.0f) {
                this.prevRotationPitch -= 360.0f;
            }
            while (this.rotationPitch - this.prevRotationPitch >= 180.0f) {
                this.prevRotationPitch += 360.0f;
            }
            while (this.rotationYaw - this.prevRotationYaw < -180.0f) {
                this.prevRotationYaw -= 360.0f;
            }
            while (this.rotationYaw - this.prevRotationYaw >= 180.0f) {
                this.prevRotationYaw += 360.0f;
            }
            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2f;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2f;
            float f3 = 0.99f;
            final float f2 = 0.05f;
            if (this.isInWater()) {
                final float f4 = 0.25f;
                for (int l = 0; l < 4; ++l) {
                    this.worldObj.spawnParticle("bubble", this.posX - this.motionX * f4, this.posY - this.motionY * f4, this.posZ - this.motionZ * f4, this.motionX, this.motionY, this.motionZ);
                }
                f3 = 0.8f;
            }
            if (this.isWet()) {
                this.extinguish();
            }
            this.setPosition(this.posX, this.posY, this.posZ);
            this.func_145775_I();
        }
        if (!this.worldObj.isRemote && (this.ticksInGround - 1) % 10 == 0) {
            GalacticraftCore.packetPipeline.sendToAllAround((IPacket)new PacketSimpleAsteroids(PacketSimpleAsteroids.EnumSimplePacketAsteroids.C_UPDATE_GRAPPLE_POS, new Object[] { this.getEntityId(), new Vector3((Entity)this) }), new NetworkRegistry.TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 150.0));
        }
    }

    public void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound) {
        if (this.hitVec != null) {
            par1NBTTagCompound.setShort("xTile", (short)this.hitVec.x);
            par1NBTTagCompound.setShort("yTile", (short)this.hitVec.y);
            par1NBTTagCompound.setShort("zTile", (short)this.hitVec.z);
        }
        par1NBTTagCompound.setShort("life", (short)this.ticksInGround);
        par1NBTTagCompound.setByte("inTile", (byte)Block.getIdFromBlock(this.hitBlock));
        par1NBTTagCompound.setByte("inData", (byte)this.inData);
        par1NBTTagCompound.setByte("shake", (byte)this.arrowShake);
        par1NBTTagCompound.setByte("inGround", (byte)(byte)(this.inGround ? 1 : 0));
        par1NBTTagCompound.setByte("pickup", (byte)this.canBePickedUp);
    }

    public void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound) {
        if (par1NBTTagCompound.hasKey("xTile")) {
            this.hitVec = new BlockVec3((int)par1NBTTagCompound.getShort("xTile"), (int)par1NBTTagCompound.getShort("yTile"), (int)par1NBTTagCompound.getShort("zTile"));
        }
        this.ticksInGround = par1NBTTagCompound.getShort("life");
        this.hitBlock = Block.getBlockById(par1NBTTagCompound.getByte("inTile") & 0xFF);
        this.inData = (par1NBTTagCompound.getByte("inData") & 0xFF);
        this.arrowShake = (par1NBTTagCompound.getByte("shake") & 0xFF);
        this.inGround = (par1NBTTagCompound.getByte("inGround") == 1);
        if (par1NBTTagCompound.hasKey("pickup", 99)) {
            this.canBePickedUp = par1NBTTagCompound.getByte("pickup");
        }
        else if (par1NBTTagCompound.hasKey("player", 99)) {
            this.canBePickedUp = (par1NBTTagCompound.getBoolean("player") ? 1 : 0);
        }
    }

    public void onCollideWithPlayer(final EntityPlayer par1EntityPlayer) {
        if (!this.worldObj.isRemote && this.inGround && this.arrowShake <= 0) {
            boolean flag = this.canBePickedUp == 1 || (this.canBePickedUp == 2 && par1EntityPlayer.capabilities.isCreativeMode);
            if (this.canBePickedUp == 1 && !par1EntityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.string, 1))) {
                flag = false;
            }
            if (flag) {
                this.playSound("random.pop", 0.2f, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
                par1EntityPlayer.onItemPickup((Entity)this, 1);
                this.setDead();
            }
        }
    }

    protected boolean canTriggerWalking() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public float getShadowSize() {
        return 0.0f;
    }

    public boolean canAttackWithItem() {
        return false;
    }

    private void updateShootingEntity() {
        if (this.shootingEntity != null) {
            this.dataWatcher.updateObject(10, (Object)this.shootingEntity.getEntityId());
        }
    }

    public EntityPlayer getShootingEntity() {
        final Entity entity = this.worldObj.getEntityByID(this.dataWatcher.getWatchableObjectInt(10));
        if (entity instanceof EntityPlayer) {
            return (EntityPlayer)entity;
        }
        return null;
    }

    public void updatePullingEntity(final boolean pulling) {
        this.dataWatcher.updateObject(11, (Object)(int)(pulling ? 1 : 0));
    }

    public boolean getPullingEntity() {
        return this.dataWatcher.getWatchableObjectInt(11) == 1;
    }
}
