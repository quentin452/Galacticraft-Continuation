package micdoodle8.mods.galacticraft.core.entities;

import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.client.particle.*;
import cpw.mods.fml.relauncher.*;
import io.netty.buffer.*;
import cpw.mods.fml.client.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import cpw.mods.fml.common.network.*;
import java.util.*;
import net.minecraft.client.*;

public abstract class EntityAdvancedMotion extends InventoryEntity implements IControllableEntity, PacketEntityUpdate.IEntityFullSync
{
    protected long ticks;
    public float currentDamage;
    public int timeSinceHit;
    public int rockDirection;
    public double advancedPositionX;
    public double advancedPositionY;
    public double advancedPositionZ;
    public double advancedYaw;
    public double advancedPitch;
    public int posRotIncrements;
    protected boolean lastOnGround;
    
    public EntityAdvancedMotion(final World world, final float yOffset) {
        super(world);
        this.ticks = 0L;
        this.preventEntitySpawning = true;
        this.ignoreFrustumCheck = true;
        this.isImmuneToFire = true;
        this.yOffset = yOffset;
    }
    
    public EntityAdvancedMotion(final World world, final float yOffset, final double var2, final double var4, final double var6) {
        this(world, yOffset);
        this.yOffset = yOffset;
        this.setPosition(var2, var4 + this.yOffset, var6);
    }
    
    protected void entityInit() {
    }
    
    protected boolean canTriggerWalking() {
        return false;
    }
    
    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }
    
    public boolean canBePushed() {
        return false;
    }
    
    public double getMountedYOffset() {
        return this.height - 1.0;
    }
    
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }
    
    public void updateRiderPosition() {
        if (this.riddenByEntity != null) {
            final double var1 = Math.cos(this.rotationYaw * 3.141592653589793 / 180.0 + 114.8) * -0.5;
            final double var2 = Math.sin(this.rotationYaw * 3.141592653589793 / 180.0 + 114.8) * -0.5;
            this.riddenByEntity.setPosition(this.posX + var1, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + var2);
        }
    }
    
    @Override
    public void setPositionRotationAndMotion(final double x, final double y, final double z, final float yaw, final float pitch, final double motX, final double motY, final double motZ, final boolean onGround) {
        if (this.worldObj.isRemote) {
            this.advancedPositionX = x;
            this.advancedPositionY = y;
            this.advancedPositionZ = z;
            this.advancedYaw = yaw;
            this.advancedPitch = pitch;
            this.motionX = motX;
            this.motionY = motY;
            this.motionZ = motZ;
            this.posRotIncrements = 5;
        }
        else {
            this.setPosition(x, y, z);
            this.setRotation(yaw, pitch);
            this.motionX = motX;
            this.motionY = motY;
            this.motionZ = motZ;
            if (onGround || this.forceGroundUpdate()) {
                this.onGround = onGround;
            }
        }
    }
    
    protected boolean forceGroundUpdate() {
        return true;
    }
    
    public void performHurtAnimation() {
        this.rockDirection = -this.rockDirection;
        this.timeSinceHit = 10;
        this.currentDamage *= 5.0f;
    }
    
    public boolean attackEntityFrom(final DamageSource var1, final float var2) {
        if (this.isDead || var1.equals(DamageSource.cactus) || !this.allowDamageSource(var1)) {
            return true;
        }
        final Entity e = var1.getEntity();
        if (this.isEntityInvulnerable() || this.posY > 300.0 || (e instanceof EntityLivingBase && !(e instanceof EntityPlayer))) {
            return false;
        }
        this.rockDirection = -this.rockDirection;
        this.timeSinceHit = 10;
        this.currentDamage += var2 * 10.0f;
        this.setBeenAttacked();
        if (e instanceof EntityPlayer && ((EntityPlayer)e).capabilities.isCreativeMode) {
            this.currentDamage = 100.0f;
        }
        if (this.currentDamage > 70.0f) {
            if (this.riddenByEntity != null) {
                this.riddenByEntity.mountEntity((Entity)this);
                return false;
            }
            if (!this.worldObj.isRemote) {
                this.dropItems();
                this.setDead();
            }
        }
        return true;
    }
    
    public abstract List<ItemStack> getItemsDropped();
    
    public abstract boolean shouldMove();
    
    public abstract boolean shouldSpawnParticles();
    
    public abstract Map<Vector3, Vector3> getParticleMap();
    
    @SideOnly(Side.CLIENT)
    public abstract EntityFX getParticle(final Random p0, final double p1, final double p2, final double p3, final double p4, final double p5, final double p6);
    
    public abstract void tickInAir();
    
    public abstract void tickOnGround();
    
    public abstract void onGroundHit();
    
    public abstract Vector3 getMotionVec();
    
    public abstract ArrayList<Object> getNetworkedData();
    
    public abstract int getPacketTickSpacing();
    
    public abstract double getPacketSendDistance();
    
    public abstract void readNetworkedData(final ByteBuf p0);
    
    public abstract boolean allowDamageSource(final DamageSource p0);
    
    public void dropItems() {
        if (this.getItemsDropped() == null) {
            return;
        }
        for (final ItemStack item : this.getItemsDropped()) {
            if (item != null) {
                this.entityDropItem(item, 0.0f);
            }
        }
    }
    
    public void setPositionAndRotation2(final double d, final double d1, final double d2, final float f, final float f1, final int i) {
        if (this.riddenByEntity != null) {
            if (!(this.riddenByEntity instanceof EntityPlayer) || !FMLClientHandler.instance().getClient().thePlayer.equals((Object)this.riddenByEntity)) {
                this.posRotIncrements = i + 5;
                this.advancedPositionX = d;
                this.advancedPositionY = d1 + ((this.riddenByEntity == null) ? 1 : 0);
                this.advancedPositionZ = d2;
                this.advancedYaw = f;
                this.advancedPitch = f1;
            }
        }
    }
    
    public void moveEntity(final double par1, final double par3, final double par5) {
        if (this.shouldMove()) {
            super.moveEntity(par1, par3, par5);
        }
    }
    
    public abstract boolean canSetPositionClient();
    
    public abstract boolean shouldSendAdvancedMotionPacket();
    
    @Override
    public void onUpdate() {
        if (this.ticks >= Long.MAX_VALUE) {
            this.ticks = 1L;
        }
        ++this.ticks;
        super.onUpdate();
        if (this.canSetPositionClient() && this.worldObj.isRemote && (this.riddenByEntity == null || !(this.riddenByEntity instanceof EntityPlayer) || !FMLClientHandler.instance().getClient().thePlayer.equals((Object)this.riddenByEntity)) && this.posRotIncrements > 0) {
            final double x = this.posX + (this.advancedPositionX - this.posX) / this.posRotIncrements;
            final double y = this.posY + (this.advancedPositionY - this.posY) / this.posRotIncrements;
            final double z = this.posZ + (this.advancedPositionZ - this.posZ) / this.posRotIncrements;
            final double var12 = MathHelper.wrapAngleTo180_double(this.advancedYaw - this.rotationYaw);
            this.rotationYaw += (float)(var12 / this.posRotIncrements);
            this.rotationPitch += (float)((this.advancedPitch - this.rotationPitch) / this.posRotIncrements);
            --this.posRotIncrements;
            this.setPosition(x, y, z);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
        if (this.timeSinceHit > 0) {
            --this.timeSinceHit;
        }
        if (this.currentDamage > 0.0f) {
            --this.currentDamage;
        }
        if (this.shouldSpawnParticles() && this.worldObj.isRemote) {
            this.spawnParticles(this.getParticleMap());
        }
        if (this.onGround) {
            this.tickOnGround();
        }
        else {
            this.tickInAir();
        }
        if (this.worldObj.isRemote) {
            final Vector3 mot = this.getMotionVec();
            this.motionX = mot.x;
            this.motionY = mot.y;
            this.motionZ = mot.z;
        }
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        if (this.onGround && !this.lastOnGround) {
            this.onGroundHit();
        }
        if (this.shouldSendAdvancedMotionPacket()) {
            if (this.worldObj.isRemote) {
                GalacticraftCore.packetPipeline.sendToServer(new PacketEntityUpdate(this));
            }
            if (!this.worldObj.isRemote && this.ticks % 5L == 0L) {
                GalacticraftCore.packetPipeline.sendToAllAround(new PacketEntityUpdate(this), new NetworkRegistry.TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 50.0));
            }
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.lastOnGround = this.onGround;
    }
    
    public void getNetworkedData(final ArrayList<Object> sendData) {
        sendData.addAll(this.getNetworkedData());
    }
    
    public void decodePacketdata(final ByteBuf buffer) {
        this.readNetworkedData(buffer);
    }
    
    public void handlePacketData(final Side side, final EntityPlayer player) {
    }
    
    @SideOnly(Side.CLIENT)
    public void spawnParticles(final Map<Vector3, Vector3> points) {
        for (final Map.Entry<Vector3, Vector3> vec : points.entrySet()) {
            final Vector3 posVec = vec.getKey();
            final Vector3 motionVec = vec.getValue();
            this.spawnParticle(this.getParticle(this.rand, posVec.x, posVec.y, posVec.z, motionVec.x, motionVec.y, motionVec.z));
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void spawnParticle(final EntityFX fx) {
        final Minecraft mc = FMLClientHandler.instance().getClient();
        if (mc != null && mc.renderViewEntity != null && mc.effectRenderer != null && fx != null) {
            mc.effectRenderer.addEffect(fx);
        }
    }
}
