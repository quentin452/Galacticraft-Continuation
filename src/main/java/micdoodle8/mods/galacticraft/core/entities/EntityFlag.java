package micdoodle8.mods.galacticraft.core.entities;

import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.wrappers.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.util.*;
import net.minecraft.nbt.*;
import net.minecraft.client.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.world.*;
import net.minecraft.block.*;

public class EntityFlag extends Entity
{
    public double xPosition;
    public double yPosition;
    public double zPosition;
    public boolean indestructable;
    public FlagData flagData;
    
    public EntityFlag(final World world) {
        super(world);
        this.indestructable = false;
        this.yOffset = 1.5f;
        this.setSize(0.4f, 3.0f);
        this.ignoreFrustumCheck = true;
    }
    
    public EntityFlag(final World par1World, final double x, final double y, final double z, final int dir) {
        this(par1World);
        this.setFacingAngle(dir);
        this.setPosition(x, y, z);
        this.xPosition = x;
        this.yPosition = y;
        this.zPosition = z;
    }
    
    public boolean attackEntityFrom(final DamageSource par1DamageSource, final float par2) {
        final boolean flag = par1DamageSource.getEntity() instanceof EntityPlayer && ((EntityPlayer)par1DamageSource.getEntity()).capabilities.isCreativeMode;
        if (this.worldObj.isRemote || this.isDead || this.indestructable) {
            return true;
        }
        if (this.isEntityInvulnerable()) {
            return false;
        }
        this.setBeenAttacked();
        this.setDamage(this.getDamage() + par2 * 10.0f);
        this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, Block.soundTypeMetal.getBreakSound(), Block.soundTypeMetal.getVolume(), Block.soundTypeMetal.getPitch() + 1.0f);
        if (par1DamageSource.getEntity() instanceof EntityPlayer && ((EntityPlayer)par1DamageSource.getEntity()).capabilities.isCreativeMode) {
            this.setDamage(100.0f);
        }
        if (flag || this.getDamage() > 40.0f) {
            if (this.riddenByEntity != null) {
                this.riddenByEntity.mountEntity((Entity)this);
            }
            if (flag) {
                this.setDead();
            }
            else {
                this.setDead();
                this.dropItemStack();
            }
        }
        return true;
    }
    
    public ItemStack getPickedResult(final MovingObjectPosition target) {
        return new ItemStack(GCItems.flag, 1, this.getType());
    }
    
    public int getWidth() {
        return 25;
    }
    
    public int getHeight() {
        return 40;
    }
    
    public boolean canBeCollidedWith() {
        return true;
    }
    
    protected boolean canTriggerWalking() {
        return false;
    }
    
    public AxisAlignedBB getCollisionBox(final Entity par1Entity) {
        return par1Entity.boundingBox;
    }
    
    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }
    
    public boolean canBePushed() {
        return false;
    }
    
    protected void entityInit() {
        this.dataWatcher.addObject(17, (Object)new String(""));
        this.dataWatcher.addObject(18, (Object)new Float(0.0f));
        this.dataWatcher.addObject(19, (Object)new Integer(-1));
        this.dataWatcher.addObject(20, (Object)new Integer(-1));
    }
    
    public void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound) {
        this.setOwner(par1NBTTagCompound.getString("Owner"));
        this.setType(par1NBTTagCompound.getInteger("Type"));
        this.indestructable = par1NBTTagCompound.getBoolean("Indestructable");
        this.xPosition = par1NBTTagCompound.getDouble("TileX");
        this.yPosition = par1NBTTagCompound.getDouble("TileY");
        this.zPosition = par1NBTTagCompound.getDouble("TileZ");
        this.setFacingAngle(par1NBTTagCompound.getInteger("AngleI"));
    }
    
    protected void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound) {
        par1NBTTagCompound.setString("Owner", String.valueOf(this.getOwner()));
        par1NBTTagCompound.setInteger("Type", (int)this.getType());
        par1NBTTagCompound.setBoolean("Indestructable", this.indestructable);
        par1NBTTagCompound.setInteger("AngleI", this.getFacingAngle());
        par1NBTTagCompound.setDouble("TileX", this.xPosition);
        par1NBTTagCompound.setDouble("TileY", this.yPosition);
        par1NBTTagCompound.setDouble("TileZ", this.zPosition);
    }
    
    public void dropItemStack() {
        this.entityDropItem(new ItemStack(GCItems.flag, 1, this.getType()), 0.0f);
    }
    
    public void onUpdate() {
        super.onUpdate();
        if ((this.ticksExisted - 1) % 20 == 0 && this.worldObj.isRemote) {
            this.flagData = ClientUtil.updateFlagData(this.getOwner(), Minecraft.getMinecraft().thePlayer.getDistanceToEntity((Entity)this) < 50.0);
        }
        Vector3 vec = new Vector3(this.posX, this.posY, this.posZ);
        vec = vec.translate(new Vector3(0.0, -1.0, 0.0));
        final Block blockAt = vec.getBlock((IBlockAccess)this.worldObj);
        if (blockAt != null) {
            if (!(blockAt instanceof BlockFence)) {
                if (blockAt.isAir((IBlockAccess)this.worldObj, vec.intX(), vec.intY(), vec.intZ())) {
                    this.motionY -= 0.019999999552965164;
                }
            }
        }
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
    }
    
    public boolean interactFirst(final EntityPlayer par1EntityPlayer) {
        if (!this.worldObj.isRemote) {
            this.setFacingAngle(this.getFacingAngle() + 3);
        }
        return true;
    }
    
    public void setOwner(final String par1) {
        this.dataWatcher.updateObject(17, (Object)String.valueOf(par1));
    }
    
    public String getOwner() {
        return this.dataWatcher.getWatchableObjectString(17);
    }
    
    public void setDamage(final float par1) {
        this.dataWatcher.updateObject(18, (Object)par1);
    }
    
    public float getDamage() {
        return this.dataWatcher.getWatchableObjectFloat(18);
    }
    
    public void setType(final int par1) {
        this.dataWatcher.updateObject(19, (Object)par1);
    }
    
    public int getType() {
        return this.dataWatcher.getWatchableObjectInt(19);
    }
    
    public void setFacingAngle(final int par1) {
        this.dataWatcher.updateObject(20, (Object)par1);
    }
    
    public int getFacingAngle() {
        return this.dataWatcher.getWatchableObjectInt(20);
    }
}
