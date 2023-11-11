package micdoodle8.mods.galacticraft.core.entities;

import net.minecraft.inventory.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.tile.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.client.model.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.*;
import cpw.mods.fml.client.*;
import cpw.mods.fml.common.network.*;
import io.netty.buffer.*;
import java.io.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.tick.*;
import net.minecraft.client.settings.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraftforge.fluids.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import java.util.*;

public class EntityBuggy extends Entity implements IInventory, IPacketReceiver, IDockable, IControllableEntity, PacketEntityUpdate.IEntityFullSync
{
    public static final int tankCapacity = 1000;
    public FluidTank buggyFuelTank;
    protected long ticks;
    public int buggyType;
    public int currentDamage;
    public int timeSinceHit;
    public int rockDirection;
    public double speed;
    public float wheelRotationZ;
    public float wheelRotationX;
    float maxSpeed;
    float accel;
    float turnFactor;
    public String texture;
    ItemStack[] cargoItems;
    public double boatX;
    public double boatY;
    public double boatZ;
    public double boatYaw;
    public double boatPitch;
    public int boatPosRotationIncrements;
    private IFuelDock landingPad;
    private int timeClimbing;
    private boolean shouldClimb;
    
    public EntityBuggy(final World var1) {
        super(var1);
        this.buggyFuelTank = new FluidTank(1000);
        this.ticks = 0L;
        this.maxSpeed = 0.5f;
        this.accel = 0.2f;
        this.turnFactor = 3.0f;
        this.cargoItems = new ItemStack[60];
        this.setSize(0.98f, 1.0f);
        this.yOffset = 2.5f;
        this.currentDamage = 18;
        this.timeSinceHit = 19;
        this.rockDirection = 20;
        this.speed = 0.0;
        this.preventEntitySpawning = true;
        this.dataWatcher.addObject(this.currentDamage, (Object)new Integer(0));
        this.dataWatcher.addObject(this.timeSinceHit, (Object)new Integer(0));
        this.dataWatcher.addObject(this.rockDirection, (Object)new Integer(1));
        this.ignoreFrustumCheck = true;
        this.isImmuneToFire = true;
        if (var1 != null && var1.isRemote) {
            GalacticraftCore.packetPipeline.sendToServer(new PacketDynamic(this));
        }
    }
    
    public EntityBuggy(final World var1, final double var2, final double var4, final double var6, final int type) {
        this(var1);
        this.setPosition(var2, var4 + this.yOffset, var6);
        this.setBuggyType(type);
        this.cargoItems = new ItemStack[this.buggyType * 18];
    }
    
    public int getScaledFuelLevel(final int i) {
        final double fuelLevel = (this.buggyFuelTank.getFluid() == null) ? 0.0 : this.buggyFuelTank.getFluid().amount;
        return (int)(fuelLevel * i / 1000.0);
    }
    
    public ModelBase getModel() {
        return null;
    }
    
    public ItemStack getPickedResult(final MovingObjectPosition target) {
        return new ItemStack(GCItems.buggy, 1, this.buggyType);
    }
    
    public int getType() {
        return this.buggyType;
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
        return this.height - 3.0;
    }
    
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }
    
    public void setBuggyType(final int par1) {
        this.buggyType = par1;
    }
    
    public void updateRiderPosition() {
        if (this.riddenByEntity != null) {
            final double var1 = Math.cos(this.rotationYaw * 3.141592653589793 / 180.0 + 114.8) * -0.5;
            final double var2 = Math.sin(this.rotationYaw * 3.141592653589793 / 180.0 + 114.8) * -0.5;
            this.riddenByEntity.setPosition(this.posX + var1, this.posY - 2.0 + this.riddenByEntity.getYOffset(), this.posZ + var2);
        }
    }
    
    public void setPositionRotationAndMotion(final double x, final double y, final double z, final float yaw, final float pitch, final double motX, final double motY, final double motZ, final boolean onGround) {
        if (this.worldObj.isRemote) {
            this.boatX = x;
            this.boatY = y;
            this.boatZ = z;
            this.boatYaw = yaw;
            this.boatPitch = pitch;
            this.motionX = motX;
            this.motionY = motY;
            this.motionZ = motZ;
            this.boatPosRotationIncrements = 5;
        }
        else {
            this.setPosition(x, y, z);
            this.setRotation(yaw, pitch);
            this.motionX = motX;
            this.motionY = motY;
            this.motionZ = motZ;
        }
    }
    
    public void performHurtAnimation() {
        this.dataWatcher.updateObject(this.rockDirection, (Object)(-this.dataWatcher.getWatchableObjectInt(this.rockDirection)));
        this.dataWatcher.updateObject(this.timeSinceHit, (Object)10);
        this.dataWatcher.updateObject(this.currentDamage, (Object)(this.dataWatcher.getWatchableObjectInt(this.currentDamage) * 5));
    }
    
    public boolean attackEntityFrom(final DamageSource var1, final float var2) {
        if (this.isDead || var1.equals(DamageSource.cactus)) {
            return true;
        }
        final Entity e = var1.getEntity();
        final boolean flag = var1.getEntity() instanceof EntityPlayer && ((EntityPlayer)var1.getEntity()).capabilities.isCreativeMode;
        if (this.isEntityInvulnerable() || (e instanceof EntityLivingBase && !(e instanceof EntityPlayer))) {
            return false;
        }
        this.dataWatcher.updateObject(this.rockDirection, (Object)(-this.dataWatcher.getWatchableObjectInt(this.rockDirection)));
        this.dataWatcher.updateObject(this.timeSinceHit, (Object)10);
        this.dataWatcher.updateObject(this.currentDamage, (Object)(int)(this.dataWatcher.getWatchableObjectInt(this.currentDamage) + var2 * 10.0f));
        this.setBeenAttacked();
        if (e instanceof EntityPlayer && ((EntityPlayer)e).capabilities.isCreativeMode) {
            this.dataWatcher.updateObject(this.currentDamage, (Object)100);
        }
        if (flag || this.dataWatcher.getWatchableObjectInt(this.currentDamage) > 2) {
            if (this.riddenByEntity != null) {
                this.riddenByEntity.mountEntity((Entity)this);
            }
            if (!this.worldObj.isRemote && this.riddenByEntity != null) {
                this.riddenByEntity.mountEntity((Entity)this);
            }
            if (flag) {
                this.setDead();
            }
            else {
                this.setDead();
                if (!this.worldObj.isRemote) {
                    this.dropBuggyAsItem();
                }
            }
            this.setDead();
        }
        return true;
    }
    
    public void dropBuggyAsItem() {
        final List<ItemStack> dropped = this.getItemsDropped();
        if (dropped == null) {
            return;
        }
        for (final ItemStack item : dropped) {
            final EntityItem entityItem = this.entityDropItem(item, 0.0f);
            if (item.hasTagCompound()) {
                entityItem.getEntityItem().setTagCompound((NBTTagCompound)item.getTagCompound().copy());
            }
        }
    }
    
    public List<ItemStack> getItemsDropped() {
        final List<ItemStack> items = new ArrayList<ItemStack>();
        final ItemStack buggy = new ItemStack(GCItems.buggy, 1, this.buggyType);
        buggy.setTagCompound(new NBTTagCompound());
        buggy.getTagCompound().setInteger("BuggyFuel", this.buggyFuelTank.getFluidAmount());
        items.add(buggy);
        for (final ItemStack item : this.cargoItems) {
            if (item != null) {
                items.add(item);
            }
        }
        return items;
    }
    
    public void setPositionAndRotation2(final double d, final double d1, final double d2, final float f, final float f1, final int i) {
        if (this.riddenByEntity != null) {
            if (!(this.riddenByEntity instanceof EntityPlayer) || !FMLClientHandler.instance().getClient().thePlayer.equals((Object)this.riddenByEntity)) {
                this.boatPosRotationIncrements = i + 5;
                this.boatX = d;
                this.boatY = d1 + ((this.riddenByEntity == null) ? 1 : 0);
                this.boatZ = d2;
                this.boatYaw = f;
                this.boatPitch = f1;
            }
        }
    }
    
    public void onUpdate() {
        if (this.ticks >= Long.MAX_VALUE) {
            this.ticks = 1L;
        }
        ++this.ticks;
        super.onUpdate();
        if (this.worldObj.isRemote) {
            this.wheelRotationX += (float)(Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * 150.0 * ((this.speed < 0.0) ? 1 : -1));
            this.wheelRotationX %= 360.0f;
            this.wheelRotationZ = Math.max(-30.0f, Math.min(30.0f, this.wheelRotationZ * 0.9f));
        }
        if (this.worldObj.isRemote && !FMLClientHandler.instance().getClient().thePlayer.equals((Object)this.worldObj.getClosestPlayerToEntity((Entity)this, -1.0))) {
            if (this.boatPosRotationIncrements > 0) {
                final double x = this.posX + (this.boatX - this.posX) / this.boatPosRotationIncrements;
                final double y = this.posY + (this.boatY - this.posY) / this.boatPosRotationIncrements;
                final double z = this.posZ + (this.boatZ - this.posZ) / this.boatPosRotationIncrements;
                final double var12 = MathHelper.wrapAngleTo180_double(this.boatYaw - this.rotationYaw);
                this.rotationYaw += (float)(var12 / this.boatPosRotationIncrements);
                this.rotationPitch += (float)((this.boatPitch - this.rotationPitch) / this.boatPosRotationIncrements);
                --this.boatPosRotationIncrements;
                this.setPosition(x, y, z);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            }
            else {
                final double x = this.posX + this.motionX;
                final double y = this.posY + this.motionY;
                final double z = this.posZ + this.motionZ;
                if (this.riddenByEntity != null) {
                    this.setPosition(x, y, z);
                }
                if (this.onGround) {
                    this.motionX *= 0.5;
                    this.motionY *= 0.5;
                    this.motionZ *= 0.5;
                }
                this.motionX *= 0.9900000095367432;
                this.motionY *= 0.949999988079071;
                this.motionZ *= 0.9900000095367432;
            }
            return;
        }
        if (this.dataWatcher.getWatchableObjectInt(this.timeSinceHit) > 0) {
            this.dataWatcher.updateObject(this.timeSinceHit, (Object)(this.dataWatcher.getWatchableObjectInt(this.timeSinceHit) - 1));
        }
        if (this.dataWatcher.getWatchableObjectInt(this.currentDamage) > 0) {
            this.dataWatcher.updateObject(this.currentDamage, (Object)(this.dataWatcher.getWatchableObjectInt(this.currentDamage) - 1));
        }
        if (!this.onGround) {
            this.motionY -= WorldUtil.getGravityForEntity(this) * 0.5;
        }
        if (this.inWater && this.speed > 0.2) {
            this.worldObj.playSoundEffect((double)(float)this.posX, (double)(float)this.posY, (double)(float)this.posZ, "random.fizz", 0.5f, 2.6f + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.8f);
        }
        this.speed *= 0.98;
        if (this.speed > this.maxSpeed) {
            this.speed = this.maxSpeed;
        }
        if (this.isCollidedHorizontally && this.shouldClimb) {
            this.speed *= 0.9;
            this.motionY = 0.15 * (-Math.pow(this.timeClimbing - 1, 2.0) / 250.0) + 0.15000000596046448;
            this.motionY = Math.max(-0.15, this.motionY);
            this.shouldClimb = false;
        }
        if ((this.motionX == 0.0 || this.motionZ == 0.0) && !this.onGround) {
            ++this.timeClimbing;
        }
        else {
            this.timeClimbing = 0;
        }
        if (this.worldObj.isRemote && this.buggyFuelTank.getFluid() != null && this.buggyFuelTank.getFluid().amount > 0) {
            this.motionX = -(this.speed * Math.cos((this.rotationYaw - 90.0f) * 3.141592653589793 / 180.0));
            this.motionZ = -(this.speed * Math.sin((this.rotationYaw - 90.0f) * 3.141592653589793 / 180.0));
        }
        if (this.worldObj.isRemote) {
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
        }
        if (!this.worldObj.isRemote && Math.abs(this.motionX * this.motionZ) > 1.0E-6) {
            final double d = this.motionX * this.motionX + this.motionZ * this.motionZ;
            if (d != 0.0 && this.ticks % (MathHelper.floor_double(2.0 / d) + 1) == 0L) {
                this.removeFuel(1);
            }
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.worldObj.isRemote) {
            GalacticraftCore.packetPipeline.sendToServer(new PacketEntityUpdate(this));
        }
        else if (this.ticks % 5L == 0L) {
            GalacticraftCore.packetPipeline.sendToAllAround(new PacketEntityUpdate(this), new NetworkRegistry.TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 50.0));
            GalacticraftCore.packetPipeline.sendToAllAround(new PacketDynamic(this), new NetworkRegistry.TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 50.0));
        }
    }
    
    public void getNetworkedData(final ArrayList<Object> sendData) {
        if (this.worldObj.isRemote) {
            return;
        }
        sendData.add(this.buggyType);
        sendData.add(this.buggyFuelTank);
    }
    
    public void decodePacketdata(final ByteBuf buffer) {
        this.buggyType = buffer.readInt();
        try {
            this.buggyFuelTank = NetworkUtil.readFluidTank(buffer);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void handlePacketData(final Side side, final EntityPlayer player) {
    }
    
    protected void readEntityFromNBT(final NBTTagCompound var1) {
        this.buggyType = var1.getInteger("buggyType");
        final NBTTagList var2 = var1.getTagList("Items", 10);
        this.cargoItems = new ItemStack[this.getSizeInventory()];
        if (var1.hasKey("fuelTank")) {
            this.buggyFuelTank.readFromNBT(var1.getCompoundTag("fuelTank"));
        }
        for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
            final NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            final int var5 = var4.getByte("Slot") & 0xFF;
            if (var5 < this.cargoItems.length) {
                this.cargoItems[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }
    }
    
    protected void writeEntityToNBT(final NBTTagCompound var1) {
        var1.setInteger("buggyType", this.buggyType);
        final NBTTagList var2 = new NBTTagList();
        if (this.buggyFuelTank.getFluid() != null) {
            var1.setTag("fuelTank", (NBTBase)this.buggyFuelTank.writeToNBT(new NBTTagCompound()));
        }
        for (int var3 = 0; var3 < this.cargoItems.length; ++var3) {
            if (this.cargoItems[var3] != null) {
                final NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.cargoItems[var3].writeToNBT(var4);
                var2.appendTag((NBTBase)var4);
            }
        }
        var1.setTag("Items", (NBTBase)var2);
    }
    
    public int getSizeInventory() {
        return this.buggyType * 18;
    }
    
    public ItemStack getStackInSlot(final int var1) {
        return this.cargoItems[var1];
    }
    
    public ItemStack decrStackSize(final int var1, final int var2) {
        if (this.cargoItems[var1] == null) {
            return null;
        }
        if (this.cargoItems[var1].stackSize <= var2) {
            final ItemStack var3 = this.cargoItems[var1];
            this.cargoItems[var1] = null;
            return var3;
        }
        final ItemStack var3 = this.cargoItems[var1].splitStack(var2);
        if (this.cargoItems[var1].stackSize == 0) {
            this.cargoItems[var1] = null;
        }
        return var3;
    }
    
    public ItemStack getStackInSlotOnClosing(final int var1) {
        if (this.cargoItems[var1] != null) {
            final ItemStack var2 = this.cargoItems[var1];
            this.cargoItems[var1] = null;
            return var2;
        }
        return null;
    }
    
    public void setInventorySlotContents(final int var1, final ItemStack var2) {
        this.cargoItems[var1] = var2;
        if (var2 != null && var2.stackSize > this.getInventoryStackLimit()) {
            var2.stackSize = this.getInventoryStackLimit();
        }
    }
    
    public String getInventoryName() {
        return "Buggy";
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public boolean isUseableByPlayer(final EntityPlayer var1) {
        return !this.isDead && var1.getDistanceSqToEntity((Entity)this) <= 64.0;
    }
    
    public void markDirty() {
    }
    
    public void openInventory() {
    }
    
    public void closeInventory() {
    }
    
    public boolean interactFirst(final EntityPlayer var1) {
        if (this.worldObj.isRemote) {
            if (this.riddenByEntity == null) {
                var1.addChatMessage((IChatComponent)new ChatComponentText(GameSettings.getKeyDisplayString(KeyHandlerClient.leftKey.getKeyCode()) + " / " + GameSettings.getKeyDisplayString(KeyHandlerClient.rightKey.getKeyCode()) + "  - " + GCCoreUtil.translate("gui.buggy.turn.name")));
                var1.addChatMessage((IChatComponent)new ChatComponentText(GameSettings.getKeyDisplayString(KeyHandlerClient.accelerateKey.getKeyCode()) + "       - " + GCCoreUtil.translate("gui.buggy.accel.name")));
                var1.addChatMessage((IChatComponent)new ChatComponentText(GameSettings.getKeyDisplayString(KeyHandlerClient.decelerateKey.getKeyCode()) + "       - " + GCCoreUtil.translate("gui.buggy.decel.name")));
                var1.addChatMessage((IChatComponent)new ChatComponentText(GameSettings.getKeyDisplayString(KeyHandlerClient.openFuelGui.getKeyCode()) + "       - " + GCCoreUtil.translate("gui.buggy.inv.name")));
            }
            return true;
        }
        if (this.riddenByEntity != null) {
            if (this.riddenByEntity == var1) {
                var1.mountEntity((Entity)null);
            }
            return true;
        }
        var1.mountEntity((Entity)this);
        return true;
    }
    
    public boolean pressKey(final int key) {
        if (this.worldObj.isRemote && (key == 6 || key == 8 || key == 9)) {
            GalacticraftCore.packetPipeline.sendToServer(new PacketControllableEntity(key));
            return true;
        }
        switch (key) {
            case 0: {
                this.speed += this.accel / 20.0;
                return this.shouldClimb = true;
            }
            case 1: {
                this.speed -= this.accel / 20.0;
                return this.shouldClimb = true;
            }
            case 2: {
                this.rotationYaw -= 0.5f * this.turnFactor;
                this.wheelRotationZ = Math.max(-30.0f, Math.min(30.0f, this.wheelRotationZ + 0.5f));
                return true;
            }
            case 3: {
                this.rotationYaw += 0.5f * this.turnFactor;
                this.wheelRotationZ = Math.max(-30.0f, Math.min(30.0f, this.wheelRotationZ - 0.5f));
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean isItemValidForSlot(final int i, final ItemStack itemstack) {
        return false;
    }
    
    public int addFuel(final FluidStack liquid, final boolean doDrain) {
        if (this.landingPad != null) {
            return FluidUtil.fillWithGCFuel(this.buggyFuelTank, liquid, doDrain);
        }
        return 0;
    }
    
    public FluidStack removeFuel(final int amount) {
        return this.buggyFuelTank.drain(amount, true);
    }
    
    public ICargoEntity.EnumCargoLoadingState addCargo(final ItemStack stack, final boolean doAdd) {
        if (this.buggyType == 0) {
            return ICargoEntity.EnumCargoLoadingState.NOINVENTORY;
        }
        int count = 0;
        count = 0;
        while (count < this.cargoItems.length) {
            final ItemStack stackAt = this.cargoItems[count];
            if (stackAt != null && stackAt.getItem() == stack.getItem() && stackAt.getItemDamage() == stack.getItemDamage() && stackAt.stackSize < stackAt.getMaxStackSize()) {
                if (stackAt.stackSize + stack.stackSize <= stackAt.getMaxStackSize()) {
                    if (doAdd) {
                        final ItemStack itemStack = this.cargoItems[count];
                        itemStack.stackSize += stack.stackSize;
                        this.markDirty();
                    }
                    return ICargoEntity.EnumCargoLoadingState.SUCCESS;
                }
                final int origSize = stackAt.stackSize;
                final int surplus = origSize + stack.stackSize - stackAt.getMaxStackSize();
                if (doAdd) {
                    this.cargoItems[count].stackSize = stackAt.getMaxStackSize();
                    this.markDirty();
                }
                stack.stackSize = surplus;
                if (this.addCargo(stack, doAdd) == ICargoEntity.EnumCargoLoadingState.SUCCESS) {
                    return ICargoEntity.EnumCargoLoadingState.SUCCESS;
                }
                this.cargoItems[count].stackSize = origSize;
                return ICargoEntity.EnumCargoLoadingState.FULL;
            }
            else {
                ++count;
            }
        }
        for (count = 0; count < this.cargoItems.length; ++count) {
            final ItemStack stackAt = this.cargoItems[count];
            if (stackAt == null) {
                if (doAdd) {
                    this.cargoItems[count] = stack;
                    this.markDirty();
                }
                return ICargoEntity.EnumCargoLoadingState.SUCCESS;
            }
        }
        return ICargoEntity.EnumCargoLoadingState.FULL;
    }
    
    public ICargoEntity.RemovalResult removeCargo(final boolean doRemove) {
        for (int i = 0; i < this.cargoItems.length; ++i) {
            final ItemStack stackAt = this.cargoItems[i];
            if (stackAt != null) {
                final ItemStack resultStack = stackAt.copy();
                resultStack.stackSize = 1;
                if (doRemove) {
                    final ItemStack itemStack = stackAt;
                    if (--itemStack.stackSize <= 0) {
                        this.cargoItems[i] = null;
                    }
                }
                if (doRemove) {
                    this.markDirty();
                }
                return new ICargoEntity.RemovalResult(ICargoEntity.EnumCargoLoadingState.SUCCESS, resultStack);
            }
        }
        return new ICargoEntity.RemovalResult(ICargoEntity.EnumCargoLoadingState.EMPTY, (ItemStack)null);
    }
    
    public void setPad(final IFuelDock pad) {
        this.landingPad = pad;
    }
    
    public IFuelDock getLandingPad() {
        return this.landingPad;
    }
    
    public void onPadDestroyed() {
    }
    
    public boolean isDockValid(final IFuelDock dock) {
        return dock instanceof TileEntityBuggyFueler;
    }
    
    public boolean hasCustomInventoryName() {
        return true;
    }
    
    public UUID getOwnerUUID() {
        if (this.riddenByEntity != null && !(this.riddenByEntity instanceof EntityPlayer)) {
            return null;
        }
        return (this.riddenByEntity != null) ? ((EntityPlayer)this.riddenByEntity).getPersistentID() : null;
    }
}
