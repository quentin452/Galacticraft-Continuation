package micdoodle8.mods.galacticraft.core.entities;

import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraftforge.fluids.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import io.netty.buffer.*;
import micdoodle8.mods.galacticraft.core.network.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;
import java.util.*;

public abstract class EntityLanderBase extends EntityAdvancedMotion implements IInventorySettable, IScaleableFuelLevel
{
    private final int FUEL_TANK_CAPACITY = 5000;
    public FluidTank fuelTank;
    protected boolean hasReceivedPacket;
    private boolean lastShouldMove;
    private UUID persistantRiderUUID;
    private Boolean shouldMoveClient;
    private Boolean shouldMoveServer;
    private ArrayList prevData;
    private boolean networkDataChanged;
    
    public EntityLanderBase(final World var1, final float yOffset) {
        super(var1, yOffset);
        this.getClass();
        this.fuelTank = new FluidTank(5000);
        this.setSize(3.0f, 3.0f);
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
    
    public int getScaledFuelLevel(final int i) {
        final double fuelLevel = (this.fuelTank.getFluid() == null) ? 0.0 : this.fuelTank.getFluid().amount;
        final double n = fuelLevel * i;
        this.getClass();
        return (int)(n / 5000.0);
    }
    
    public EntityLanderBase(final World var1, final double var2, final double var4, final double var6, final float yOffset) {
        this(var1, yOffset);
        this.setPosition(var2, var4 + this.yOffset, var6);
    }
    
    public EntityLanderBase(final EntityPlayerMP player, final float yOffset) {
        this(player.worldObj, player.posX, player.posY, player.posZ, yOffset);
        final GCPlayerStats stats = GCPlayerStats.get(player);
        this.containedItems = new ItemStack[stats.rocketStacks.length + 1];
        this.fuelTank.setFluid(new FluidStack(GalacticraftCore.fluidFuel, stats.fuelLevel));
        for (int i = 0; i < stats.rocketStacks.length; ++i) {
            if (stats.rocketStacks[i] != null) {
                this.containedItems[i] = stats.rocketStacks[i].copy();
            }
            else {
                this.containedItems[i] = null;
            }
        }
        this.setPositionAndRotation(player.posX, player.posY, player.posZ, 0.0f, 0.0f);
        player.mountEntity((Entity)this);
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (this.ticks < 40L && this.posY > 150.0 && this.riddenByEntity == null) {
            final EntityPlayer player = this.worldObj.getClosestPlayerToEntity((Entity)this, 5.0);
            if (player != null && player.ridingEntity == null) {
                player.mountEntity((Entity)this);
            }
        }
        if (!this.worldObj.isRemote) {
            this.checkFluidTankTransfer(this.containedItems.length - 1, this.fuelTank);
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
    
    private void checkFluidTankTransfer(final int slot, final FluidTank tank) {
        FluidUtil.tryFillContainerFuel(tank, this.containedItems, slot);
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
        final NBTTagList var2 = nbt.getTagList("Items", 10);
        int invSize = nbt.getInteger("rocketStacksLength");
        if (invSize < 3) {
            invSize = 3;
        }
        this.containedItems = new ItemStack[invSize];
        for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
            final NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            final int var5 = var4.getByte("Slot") & 0xFF;
            if (var5 < this.containedItems.length) {
                this.containedItems[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }
        if (nbt.hasKey("fuelTank")) {
            this.fuelTank.readFromNBT(nbt.getCompoundTag("fuelTank"));
        }
        if (nbt.hasKey("RiderUUID_LSB")) {
            this.persistantRiderUUID = new UUID(nbt.getLong("RiderUUID_LSB"), nbt.getLong("RiderUUID_MSB"));
        }
    }
    
    protected void writeEntityToNBT(final NBTTagCompound nbt) {
        final NBTTagList nbttaglist = new NBTTagList();
        nbt.setInteger("rocketStacksLength", this.containedItems.length);
        for (int i = 0; i < this.containedItems.length; ++i) {
            if (this.containedItems[i] != null) {
                final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.containedItems[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag((NBTBase)nbttagcompound1);
            }
        }
        nbt.setTag("Items", (NBTBase)nbttaglist);
        if (this.fuelTank.getFluid() != null) {
            nbt.setTag("fuelTank", (NBTBase)this.fuelTank.writeToNBT(new NBTTagCompound()));
        }
        final UUID id = this.getOwnerUUID();
        if (id != null) {
            nbt.setLong("RiderUUID_LSB", id.getLeastSignificantBits());
            nbt.setLong("RiderUUID_MSB", id.getMostSignificantBits());
        }
    }
    
    public boolean shouldMove() {
        return this.shouldMoveClient != null && this.shouldMoveServer != null && this.ticks >= 40L && !this.onGround;
    }
    
    public abstract double getInitialMotionY();
    
    public void tickInAir() {
        if (this.worldObj.isRemote) {
            if (!this.shouldMove()) {
                final double motionY = 0.0;
                this.motionZ = motionY;
                this.motionX = motionY;
                this.motionY = motionY;
            }
            if (this.shouldMove() && !this.lastShouldMove) {
                this.motionY = this.getInitialMotionY();
            }
            this.lastShouldMove = this.shouldMove();
        }
    }
    
    public ArrayList<Object> getNetworkedData() {
        final ArrayList<Object> objList = new ArrayList<Object>();
        if (!this.worldObj.isRemote) {
            final Integer cargoLength = (this.containedItems != null) ? this.containedItems.length : 0;
            objList.add(cargoLength);
            objList.add((this.fuelTank.getFluid() == null) ? 0 : this.fuelTank.getFluid().amount);
        }
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
        return this.networkDataChanged || this.shouldMoveClient == null || this.shouldMoveServer == null;
    }
    
    public boolean canRiderInteract() {
        return true;
    }
    
    public int getPacketTickSpacing() {
        return 2;
    }
    
    public double getPacketSendDistance() {
        return 250.0;
    }
    
    public void readNetworkedData(final ByteBuf buffer) {
        try {
            if (this.worldObj.isRemote) {
                if (!this.hasReceivedPacket) {
                    GalacticraftCore.packetPipeline.sendToServer(new PacketDynamic((Entity)this));
                    this.hasReceivedPacket = true;
                }
                final int cargoLength = buffer.readInt();
                if (this.containedItems == null || this.containedItems.length == 0) {
                    this.containedItems = new ItemStack[cargoLength];
                    GalacticraftCore.packetPipeline.sendToServer(new PacketDynamicInventory((Entity)this));
                }
                this.fuelTank.setFluid(new FluidStack(GalacticraftCore.fluidFuel, buffer.readInt()));
                this.shouldMoveServer = buffer.readBoolean();
                final int shouldBeMountedId = buffer.readInt();
                if (this.riddenByEntity == null) {
                    if (shouldBeMountedId > -1) {
                        Entity e = FMLClientHandler.instance().getWorldClient().getEntityByID(shouldBeMountedId);
                        if (e != null) {
                            if (e.dimension != this.dimension) {
                                if (e instanceof EntityPlayer) {
                                    e = (Entity)WorldUtil.forceRespawnClient(this.dimension, e.worldObj.difficultySetting.getDifficultyId(), e.worldObj.getWorldInfo().getTerrainType().getWorldTypeName(), ((EntityPlayerMP)e).theItemInWorldManager.getGameType().getID());
                                    e.mountEntity((Entity)this);
                                }
                            }
                            else {
                                e.mountEntity((Entity)this);
                            }
                        }
                    }
                }
                else if (this.riddenByEntity.getEntityId() != shouldBeMountedId) {
                    if (shouldBeMountedId == -1) {
                        this.riddenByEntity.mountEntity((Entity)null);
                    }
                    else {
                        Entity e = FMLClientHandler.instance().getWorldClient().getEntityByID(shouldBeMountedId);
                        if (e != null) {
                            if (e.dimension != this.dimension) {
                                if (e instanceof EntityPlayer) {
                                    e = (Entity)WorldUtil.forceRespawnClient(this.dimension, e.worldObj.difficultySetting.getDifficultyId(), e.worldObj.getWorldInfo().getTerrainType().getWorldTypeName(), ((EntityPlayerMP)e).theItemInWorldManager.getGameType().getID());
                                    e.mountEntity((Entity)this);
                                }
                            }
                            else {
                                e.mountEntity((Entity)this);
                            }
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
        return new ArrayList<ItemStack>(Arrays.asList(this.containedItems));
    }
    
    public int getSizeInventory() {
        return this.containedItems.length;
    }
    
    public void setSizeInventory(final int size) {
        this.containedItems = new ItemStack[size];
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
}
