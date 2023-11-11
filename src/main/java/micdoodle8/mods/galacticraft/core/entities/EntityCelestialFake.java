package micdoodle8.mods.galacticraft.core.entities;

import micdoodle8.mods.galacticraft.api.entity.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import io.netty.buffer.*;
import cpw.mods.fml.client.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import java.util.*;
import net.minecraft.client.particle.*;

public class EntityCelestialFake extends EntityAdvancedMotion implements IIgnoreShift
{
    private boolean lastShouldMove;
    private UUID persistantRiderUUID;
    private Boolean shouldMoveClient;
    private Boolean shouldMoveServer;
    private boolean hasReceivedPacket;
    private ArrayList prevData;
    private boolean networkDataChanged;
    
    public EntityCelestialFake(final World var1) {
        this(var1, 0.0f);
    }
    
    public EntityCelestialFake(final World var1, final float yOffset) {
        super(var1, yOffset);
        this.setSize(3.0f, 1.0f);
    }
    
    public void updateRiderPosition() {
        if (this.riddenByEntity != null) {
            this.riddenByEntity.setPosition(this.posX, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ);
        }
    }
    
    public boolean shouldSendAdvancedMotionPacket() {
        return this.shouldMoveClient != null && this.shouldMoveServer != null;
    }
    
    public boolean canSetPositionClient() {
        return this.shouldSendAdvancedMotionPacket();
    }
    
    public EntityCelestialFake(final World var1, final double var2, final double var4, final double var6, final float yOffset) {
        this(var1, yOffset);
        this.setPosition(var2, var4 + this.yOffset, var6);
    }
    
    public EntityCelestialFake(final EntityPlayerMP player, final float yOffset) {
        this(player.worldObj, player.posX, player.posY, player.posZ, yOffset);
        this.setPositionAndRotation(player.posX, player.posY, player.posZ, 0.0f, 0.0f);
        this.riddenByEntity = (Entity)player;
        player.ridingEntity = (Entity)this;
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (this.ticks < 40L && this.posY > 150.0 && this.riddenByEntity == null) {
            final EntityPlayer player = this.worldObj.getClosestPlayerToEntity((Entity)this, 5.0);
            if (player != null && player.ridingEntity == null) {
                player.mountEntity((Entity)this);
            }
        }
        final AxisAlignedBB box = this.boundingBox.expand(0.2, 0.4, 0.2);
        final List<Entity> var15 = (List<Entity>)this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)this, box);
        if (var15 != null && !var15.isEmpty()) {
            for (final Entity entity : var15) {
                if (entity != this.riddenByEntity) {
                    this.pushEntityAway(entity);
                }
            }
        }
    }
    
    private void pushEntityAway(final Entity entityToPush) {
        if (this.riddenByEntity != entityToPush && this.ridingEntity != entityToPush) {
            double d0 = this.posX - entityToPush.posX;
            double d2 = this.posZ - entityToPush.posZ;
            double d3 = MathHelper.abs_max(d0, d2);
            if (d3 >= 0.009999999776482582) {
                d3 = MathHelper.sqrt_double(d3);
                d0 /= d3;
                d2 /= d3;
                double d4 = 1.0 / d3;
                if (d4 > 1.0) {
                    d4 = 1.0;
                }
                d0 *= d4;
                d2 *= d4;
                d0 *= 0.05000000074505806;
                d2 *= 0.05000000074505806;
                d0 *= 1.0f - entityToPush.entityCollisionReduction;
                d2 *= 1.0f - entityToPush.entityCollisionReduction;
                entityToPush.addVelocity(-d0, 0.0, -d2);
            }
        }
    }
    
    protected void readEntityFromNBT(final NBTTagCompound nbt) {
        if (nbt.hasKey("RiderUUID_LSB")) {
            this.persistantRiderUUID = new UUID(nbt.getLong("RiderUUID_LSB"), nbt.getLong("RiderUUID_MSB"));
        }
    }
    
    protected void writeEntityToNBT(final NBTTagCompound nbt) {
        final NBTTagList nbttaglist = new NBTTagList();
        final UUID id = this.getOwnerUUID();
        if (id != null) {
            nbt.setLong("RiderUUID_LSB", id.getLeastSignificantBits());
            nbt.setLong("RiderUUID_MSB", id.getMostSignificantBits());
        }
    }
    
    public boolean shouldMove() {
        return false;
    }
    
    public void tickInAir() {
        if (this.worldObj.isRemote) {
            final double motionY = 0.0;
            this.motionZ = motionY;
            this.motionX = motionY;
            this.motionY = motionY;
            this.lastShouldMove = false;
        }
    }
    
    public ArrayList<Object> getNetworkedData() {
        final ArrayList<Object> objList = new ArrayList<Object>();
        if (this.worldObj.isRemote) {
            objList.add(this.shouldMoveClient = this.shouldMove());
        }
        else {
            objList.add(this.shouldMoveServer = this.shouldMove());
            objList.add((this.riddenByEntity == null) ? -1 : this.riddenByEntity.getEntityId());
        }
        this.networkDataChanged = !objList.equals(this.prevData);
        return (ArrayList<Object>)(this.prevData = objList);
    }
    
    public boolean networkedDataChanged() {
        return this.networkDataChanged;
    }
    
    public boolean canRiderInteract() {
        return true;
    }
    
    public int getPacketTickSpacing() {
        return 2;
    }
    
    public double getPacketSendDistance() {
        return 500.0;
    }
    
    public void readNetworkedData(final ByteBuf buffer) {
        try {
            if (this.worldObj.isRemote) {
                this.hasReceivedPacket = true;
                this.shouldMoveServer = buffer.readBoolean();
                final int shouldBeMountedId = buffer.readInt();
                if (this.riddenByEntity == null) {
                    if (shouldBeMountedId > -1) {
                        final Entity e = FMLClientHandler.instance().getWorldClient().getEntityByID(shouldBeMountedId);
                        if (e != null) {
                            e.mountEntity((Entity)this);
                        }
                    }
                }
                else if (this.riddenByEntity.getEntityId() != shouldBeMountedId) {
                    if (shouldBeMountedId == -1) {
                        this.riddenByEntity.mountEntity((Entity)null);
                    }
                    else {
                        final Entity e = FMLClientHandler.instance().getWorldClient().getEntityByID(shouldBeMountedId);
                        if (e != null) {
                            e.mountEntity((Entity)this);
                        }
                    }
                }
            }
            else {
                this.shouldMoveClient = buffer.readBoolean();
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }
    
    public boolean allowDamageSource(final DamageSource damageSource) {
        return !damageSource.isExplosion();
    }
    
    public List<ItemStack> getItemsDropped() {
        return null;
    }
    
    public boolean isItemValidForSlot(final int var1, final ItemStack var2) {
        return false;
    }
    
    public double getPacketRange() {
        return 50.0;
    }
    
    public UUID getOwnerUUID() {
        if (this.riddenByEntity != null && !(this.riddenByEntity instanceof EntityPlayer)) {
            return null;
        }
        UUID id;
        if (this.riddenByEntity != null) {
            id = ((EntityPlayer)this.riddenByEntity).getPersistentID();
            if (id != null) {
                this.persistantRiderUUID = id;
            }
        }
        else {
            id = this.persistantRiderUUID;
        }
        return id;
    }
    
    public boolean pressKey(final int key) {
        return false;
    }
    
    public int getSizeInventory() {
        return 0;
    }
    
    public String getInventoryName() {
        return null;
    }
    
    public boolean hasCustomInventoryName() {
        return false;
    }
    
    public boolean shouldSpawnParticles() {
        return false;
    }
    
    public Map<Vector3, Vector3> getParticleMap() {
        return null;
    }
    
    public EntityFX getParticle(final Random rand, final double x, final double y, final double z, final double motX, final double motY, final double motZ) {
        return null;
    }
    
    public void tickOnGround() {
        this.tickInAir();
    }
    
    public void onGroundHit() {
    }
    
    public Vector3 getMotionVec() {
        return new Vector3(0.0, 0.0, 0.0);
    }
    
    public boolean shouldIgnoreShiftExit() {
        return true;
    }
}
