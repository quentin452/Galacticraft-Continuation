package micdoodle8.mods.galacticraft.core.entities;

import net.minecraft.block.*;
import net.minecraft.entity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.world.*;
import net.minecraft.entity.monster.*;
import net.minecraft.enchantment.*;
import net.minecraft.entity.player.*;
import net.minecraft.network.play.server.*;
import net.minecraft.network.*;
import micdoodle8.mods.galacticraft.core.util.*;
import java.util.*;
import net.minecraft.util.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.item.*;

public class EntityMeteorChunk extends Entity implements IProjectile
{
    private int xTile;
    private int yTile;
    private int zTile;
    private Block inTile;
    private int inData;
    public int canBePickedUp;
    public Entity shootingEntity;
    private int ticksInGround;
    private int ticksInAir;
    private boolean inGround;
    private int knockbackStrength;
    public boolean isHot;
    
    public EntityMeteorChunk(final World world) {
        super(world);
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.renderDistanceWeight = 10.0;
        this.setSize(0.5f, 0.5f);
    }
    
    public EntityMeteorChunk(final World world, final double x, final double y, final double z) {
        super(world);
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.renderDistanceWeight = 10.0;
        this.setSize(0.5f, 0.5f);
        this.setPosition(x, y, z);
        this.yOffset = 0.0f;
    }
    
    public EntityMeteorChunk(final World world, final EntityLivingBase shootingEntity, final EntityLivingBase target, final float speed, final float randMod) {
        super(world);
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.renderDistanceWeight = 10.0;
        this.shootingEntity = (Entity)shootingEntity;
        if (shootingEntity instanceof EntityPlayer) {
            this.canBePickedUp = 1;
        }
        this.posY = shootingEntity.posY + shootingEntity.getEyeHeight() - 0.10000000149011612;
        final double d0 = target.posX - shootingEntity.posX;
        final double d2 = target.boundingBox.minY + target.height / 3.0f - this.posY;
        final double d3 = target.posZ - shootingEntity.posZ;
        final double d4 = MathHelper.sqrt_double(d0 * d0 + d3 * d3);
        if (d4 >= 1.0E-7) {
            final float f2 = (float)(Math.atan2(d3, d0) * 180.0 / 3.141592653589793) - 90.0f;
            final float f3 = (float)(-(Math.atan2(d2, d4) * 180.0 / 3.141592653589793));
            final double d5 = d0 / d4;
            final double d6 = d3 / d4;
            this.setLocationAndAngles(shootingEntity.posX + d5, this.posY, shootingEntity.posZ + d6, f2, f3);
            this.yOffset = 0.0f;
            final float f4 = (float)d4 * 0.2f;
            this.setThrowableHeading(d0, d2 + f4, d3, speed, randMod);
        }
    }
    
    public EntityMeteorChunk(final World par1World, final EntityLivingBase par2EntityLivingBase, final float speed) {
        super(par1World);
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.renderDistanceWeight = 10.0;
        this.shootingEntity = (Entity)par2EntityLivingBase;
        if (par2EntityLivingBase instanceof EntityPlayer) {
            this.canBePickedUp = 1;
        }
        this.setSize(0.5f, 0.5f);
        this.setLocationAndAngles(par2EntityLivingBase.posX, par2EntityLivingBase.posY + par2EntityLivingBase.getEyeHeight(), par2EntityLivingBase.posZ, par2EntityLivingBase.rotationYaw, par2EntityLivingBase.rotationPitch);
        this.posX -= MathHelper.cos(this.rotationYaw / 180.0f * 3.1415927f) * 0.16f;
        this.posY -= 0.10000000149011612;
        this.posZ -= MathHelper.sin(this.rotationYaw / 180.0f * 3.1415927f) * 0.16f;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0f;
        this.motionX = -MathHelper.sin(this.rotationYaw / 180.0f * 3.1415927f) * MathHelper.cos(this.rotationPitch / 180.0f * 3.1415927f);
        this.motionZ = MathHelper.cos(this.rotationYaw / 180.0f * 3.1415927f) * MathHelper.cos(this.rotationPitch / 180.0f * 3.1415927f);
        this.motionY = -MathHelper.sin(this.rotationPitch / 180.0f * 3.1415927f);
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, speed * 1.5f, 1.0f);
    }
    
    public void setThrowableHeading(double headingX, double headingY, double headingZ, final float speed, final float randMod) {
        final float f2 = MathHelper.sqrt_double(headingX * headingX + headingY * headingY + headingZ * headingZ);
        headingX /= f2;
        headingY /= f2;
        headingZ /= f2;
        headingX += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * randMod;
        headingY += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * randMod;
        headingZ += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * randMod;
        headingX *= speed;
        headingY *= speed;
        headingZ *= speed;
        this.motionX = headingX;
        this.motionY = headingY;
        this.motionZ = headingZ;
        final float f3 = MathHelper.sqrt_double(headingX * headingX + headingZ * headingZ);
        final float n = (float)(Math.atan2(headingX, headingZ) * 180.0 / 3.141592653589793);
        this.rotationYaw = n;
        this.prevRotationYaw = n;
        final float n2 = (float)(Math.atan2(headingY, f3) * 180.0 / 3.141592653589793);
        this.rotationPitch = n2;
        this.prevRotationPitch = n2;
        this.ticksInGround = 0;
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
        if (this.ticksExisted > 400) {
            if (this.isHot) {
                this.setHot(this.isHot = false);
            }
        }
        else if (!this.worldObj.isRemote) {
            this.setHot(this.isHot);
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
        final Block block = this.worldObj.getBlock(this.xTile, this.yTile, this.zTile);
        if (block != null && !block.isAir((IBlockAccess)this.worldObj, this.xTile, this.yTile, this.zTile)) {
            block.setBlockBoundsBasedOnState((IBlockAccess)this.worldObj, this.xTile, this.yTile, this.zTile);
            final AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(this.worldObj, this.xTile, this.yTile, this.zTile);
            if (axisalignedbb != null && axisalignedbb.isVecInside(Vec3.createVectorHelper(this.posX, this.posY, this.posZ))) {
                this.inGround = true;
            }
        }
        if (this.inGround) {
            final Block j = this.worldObj.getBlock(this.xTile, this.yTile, this.zTile);
            final int k = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
            if (j == this.inTile && k == this.inData) {
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
        else {
            ++this.ticksInAir;
            Vec3 vec3 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            Vec3 vec4 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition movingobjectposition = this.worldObj.func_147447_a(vec3, vec4, false, true, false);
            vec3 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            vec4 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            if (movingobjectposition != null) {
                vec4 = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
            }
            ++this.rotationPitch;
            Entity entity = null;
            final List<Entity> list = (List<Entity>)this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
            double d0 = 0.0;
            for (int l = 0; l < list.size(); ++l) {
                final Entity entity2 = list.get(l);
                if (entity2.canBeCollidedWith() && (entity2 != this.shootingEntity || this.ticksInAir >= 5)) {
                    final float f2 = 0.3f;
                    final AxisAlignedBB axisalignedbb2 = entity2.boundingBox.expand((double)f2, (double)f2, (double)f2);
                    final MovingObjectPosition movingobjectposition2 = axisalignedbb2.calculateIntercept(vec3, vec4);
                    if (movingobjectposition2 != null) {
                        final double d2 = vec3.distanceTo(movingobjectposition2.hitVec);
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
                if (entityplayer.capabilities.disableDamage || (this.shootingEntity instanceof EntityPlayer && !((EntityPlayer)this.shootingEntity).canAttackPlayer(entityplayer))) {
                    movingobjectposition = null;
                }
            }
            final double damage = ConfigManagerCore.hardMode ? 3.2 : 1.6;
            if (movingobjectposition != null) {
                if (movingobjectposition.entityHit != null) {
                    final float f3 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    final int i1 = MathHelper.ceiling_double_int(f3 * damage);
                    DamageSource damagesource = null;
                    if (this.shootingEntity == null) {
                        damagesource = new EntityDamageSourceIndirect("meteorChunk", (Entity)this, (Entity)this).setProjectile();
                    }
                    else {
                        damagesource = new EntityDamageSourceIndirect("meteorChunk", (Entity)this, this.shootingEntity).setProjectile();
                    }
                    if (this.isBurning() && !(movingobjectposition.entityHit instanceof EntityEnderman)) {
                        movingobjectposition.entityHit.setFire(2);
                    }
                    if (movingobjectposition.entityHit.attackEntityFrom(damagesource, (float)i1)) {
                        if (movingobjectposition.entityHit instanceof EntityLivingBase) {
                            final EntityLivingBase entitylivingbase = (EntityLivingBase)movingobjectposition.entityHit;
                            if (!this.worldObj.isRemote) {
                                entitylivingbase.setArrowCountInEntity(entitylivingbase.getArrowCountInEntity() + 1);
                            }
                            if (this.knockbackStrength > 0) {
                                final float f4 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
                                if (f4 > 0.0f) {
                                    movingobjectposition.entityHit.addVelocity(this.motionX * this.knockbackStrength * 0.6000000238418579 / f4, 0.1, this.motionZ * this.knockbackStrength * 0.6000000238418579 / f4);
                                }
                            }
                            if (this.shootingEntity != null) {
                                EnchantmentHelper.func_151384_a(entitylivingbase, this.shootingEntity);
                                EnchantmentHelper.func_151385_b((EntityLivingBase)this.shootingEntity, (Entity)entitylivingbase);
                            }
                            if (this.shootingEntity != null && movingobjectposition.entityHit != this.shootingEntity && movingobjectposition.entityHit instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
                                ((EntityPlayerMP)this.shootingEntity).playerNetServerHandler.sendPacket((Packet)new S2BPacketChangeGameState(6, 0.0f));
                            }
                        }
                        if (!(movingobjectposition.entityHit instanceof EntityEnderman)) {
                            this.setDead();
                        }
                    }
                    else {
                        this.motionX *= -0.10000000149011612;
                        this.motionY *= -0.10000000149011612;
                        this.motionZ *= -0.10000000149011612;
                        this.rotationYaw += 180.0f;
                        this.prevRotationYaw += 180.0f;
                        this.ticksInAir = 0;
                    }
                }
                else {
                    this.xTile = movingobjectposition.blockX;
                    this.yTile = movingobjectposition.blockY;
                    this.zTile = movingobjectposition.blockZ;
                    this.inTile = this.worldObj.getBlock(this.xTile, this.yTile, this.zTile);
                    this.inData = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
                    this.motionX = (float)(movingobjectposition.hitVec.xCoord - this.posX);
                    this.motionY = (float)(movingobjectposition.hitVec.yCoord - this.posY);
                    this.motionZ = (float)(movingobjectposition.hitVec.zCoord - this.posZ);
                    final float f3 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    this.posX -= this.motionX / f3 * 0.05000000074505806;
                    this.posY -= this.motionY / f3 * 0.05000000074505806;
                    this.posZ -= this.motionZ / f3 * 0.05000000074505806;
                    this.inGround = true;
                    if (!this.inTile.isAir((IBlockAccess)this.worldObj, this.xTile, this.yTile, this.zTile)) {
                        this.inTile.onEntityCollidedWithBlock(this.worldObj, this.xTile, this.yTile, this.zTile, (Entity)this);
                    }
                }
            }
            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            final float f3 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            if (!this.onGround) {
                this.rotationPitch += 10.0f;
                this.rotationYaw += 2.0f;
            }
            float f5 = 0.99f;
            final float f2 = 0.05f;
            if (this.isInWater()) {
                for (int j2 = 0; j2 < 4; ++j2) {
                    final float f4 = 0.25f;
                    this.worldObj.spawnParticle("bubble", this.posX - this.motionX * f4, this.posY - this.motionY * f4, this.posZ - this.motionZ * f4, this.motionX, this.motionY, this.motionZ);
                }
                f5 = 0.8f;
            }
            this.motionX *= f5;
            this.motionY *= f5;
            this.motionZ *= f5;
            this.motionY -= WorldUtil.getGravityForEntity(this);
            this.setPosition(this.posX, this.posY, this.posZ);
            this.func_145775_I();
        }
    }
    
    protected void entityInit() {
        this.dataWatcher.addObject(16, (Object)0);
    }
    
    public boolean isHot() {
        return this.dataWatcher.getWatchableObjectInt(16) == 1;
    }
    
    public void setHot(final boolean isHot) {
        this.dataWatcher.updateObject(16, (Object)(int)(isHot ? 1 : 0));
    }
    
    protected void readEntityFromNBT(final NBTTagCompound nbttagcompound) {
    }
    
    protected void writeEntityToNBT(final NBTTagCompound nbttagcompound) {
    }
    
    public void onCollideWithPlayer(final EntityPlayer par1EntityPlayer) {
        if (!this.worldObj.isRemote && this.inGround) {
            boolean flag = this.canBePickedUp == 1 || (this.canBePickedUp == 2 && par1EntityPlayer.capabilities.isCreativeMode);
            if (this.canBePickedUp == 1 && !par1EntityPlayer.inventory.addItemStackToInventory(new ItemStack(GCItems.meteorChunk, 1, 0))) {
                flag = false;
            }
            if (flag) {
                this.playSound("random.pop", 0.2f, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
                par1EntityPlayer.onItemPickup((Entity)this, 1);
                this.setDead();
            }
        }
    }
    
    public boolean canAttackWithItem() {
        return false;
    }
}
