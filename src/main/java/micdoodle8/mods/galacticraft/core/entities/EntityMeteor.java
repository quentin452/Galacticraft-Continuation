package micdoodle8.mods.galacticraft.core.entities;

import net.minecraft.entity.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.block.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import net.minecraft.nbt.*;

public class EntityMeteor extends Entity
{
    public EntityLiving shootingEntity;
    public int size;

    public EntityMeteor(final World world) {
        super(world);
        this.setSize(1.0f, 1.0f);
    }

    public EntityMeteor(final World world, final double x, final double y, final double z, final double motX, final double motY, final double motZ, final int size) {
        this(world);
        this.size = size;
        this.setSize(1.0f, 1.0f);
        this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
        this.setPosition(x, y, z);
        this.motionX = motX;
        this.motionY = motY;
        this.motionZ = motZ;
        this.setSize(size);
    }

    public void onUpdate() {
        this.setRotation(this.rotationYaw + 2.0f, this.rotationPitch + 2.0f);
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= 0.03999999910593033;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        if (this.worldObj.isRemote) {
            this.spawnParticles();
        }
        Vec3 var15 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
        Vec3 var16 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        MovingObjectPosition var17 = this.worldObj.func_147447_a(var15, var16, true, true, false);
        var15 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
        var16 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        if (var17 != null) {
            var16 = Vec3.createVectorHelper(var17.hitVec.xCoord, var17.hitVec.yCoord, var17.hitVec.zCoord);
        }
        Entity var18 = null;
        final List<?> var19 = (List<?>)this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(2.0, 2.0, 2.0));
        double var20 = 0.0;
        for (final Entity var22 : (Iterable<Entity>)var19) {
            if (var22.canBeCollidedWith() && !var22.isEntityEqual(this.shootingEntity)) {
                final float var23 = 0.01f;
                final AxisAlignedBB var24 = var22.getBoundingBox().expand(0.009999999776482582, 0.009999999776482582, 0.009999999776482582);
                final MovingObjectPosition var25 = var24.calculateIntercept(var15, var16);
                if (var25 == null) {
                    continue;
                }
                final double var26 = var15.distanceTo(var25.hitVec);
                if (var26 >= var20 && var20 != 0.0) {
                    continue;
                }
                var18 = var22;
                var20 = var26;
            }
        }
        if (var18 != null) {
            var17 = new MovingObjectPosition(var18);
        }
        if (var17 != null) {
            this.onImpact(var17);
        }
        if (this.posY <= -20.0 || this.posY >= 400.0) {
            this.setDead();
        }
    }

    protected void spawnParticles() {
        GalacticraftCore.proxy.spawnParticle("distanceSmoke", new Vector3(this.posX, this.posY + 1.0 + Math.random(), this.posZ), new Vector3(0.0, 0.0, 0.0), new Object[0]);
        GalacticraftCore.proxy.spawnParticle("distanceSmoke", new Vector3(this.posX + Math.random() / 2.0, this.posY + 1.0 + Math.random() / 2.0, this.posZ), new Vector3(0.0, 0.0, 0.0), new Object[0]);
        GalacticraftCore.proxy.spawnParticle("distanceSmoke", new Vector3(this.posX, this.posY + 1.0 + Math.random(), this.posZ + Math.random()), new Vector3(0.0, 0.0, 0.0), new Object[0]);
        GalacticraftCore.proxy.spawnParticle("distanceSmoke", new Vector3(this.posX - Math.random() / 2.0, this.posY + 1.0 + Math.random() / 2.0, this.posZ), new Vector3(0.0, 0.0, 0.0), new Object[0]);
        GalacticraftCore.proxy.spawnParticle("distanceSmoke", new Vector3(this.posX, this.posY + 1.0 + Math.random(), this.posZ - Math.random()), new Vector3(0.0, 0.0, 0.0), new Object[0]);
    }

    protected void onImpact(final MovingObjectPosition movingObjPos) {
        if (!this.worldObj.isRemote) {
            if (movingObjPos != null) {
                final Block b = this.worldObj.getBlock(movingObjPos.blockX, movingObjPos.blockY + 1, movingObjPos.blockZ);
                if (b != null && b.isAir((IBlockAccess)this.worldObj, movingObjPos.blockX, movingObjPos.blockY + 1, movingObjPos.blockZ)) {
                    this.worldObj.setBlock(movingObjPos.blockX, movingObjPos.blockY + 1, movingObjPos.blockZ, GCBlocks.fallenMeteor, 0, 3);
                }
                if (movingObjPos.entityHit != null) {
                    movingObjPos.entityHit.attackEntityFrom(causeMeteorDamage(this, (Entity)this.shootingEntity), ConfigManagerCore.hardMode ? 12.0f : 6.0f);
                }
            }
            this.worldObj.newExplosion((Entity)this, this.posX, this.posY, this.posZ, (float)(this.size / 3 + 2), false, true);
        }
        this.setDead();
    }

    public boolean func_145774_a(final Explosion p_145774_1_, final World p_145774_2_, final int p_145774_3_, final int p_145774_4_, final int p_145774_5_, final Block p_145774_6_, final float p_145774_7_) {
        return ConfigManagerCore.meteorBlockDamageEnabled;
    }

    public static DamageSource causeMeteorDamage(final EntityMeteor par0EntityMeteor, final Entity par1Entity) {
        if (par1Entity != null && par1Entity instanceof EntityPlayer) {
            StatCollector.translateToLocalFormatted("death.meteor", new Object[] { ((EntityPlayer)par1Entity).getGameProfile().getName() + " was hit by a meteor! That's gotta hurt!" });
        }
        return new EntityDamageSourceIndirect("explosion", (Entity)par0EntityMeteor, par1Entity).setProjectile();
    }

    protected void entityInit() {
        this.dataWatcher.addObject(16, (Object)this.size);
        this.noClip = true;
    }

    public int getSize() {
        return this.dataWatcher.getWatchableObjectInt(16);
    }

    public void setSize(final int par1) {
        this.dataWatcher.updateObject(16, (Object)par1);
    }

    protected void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound) {
    }

    protected void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound) {
    }
}
