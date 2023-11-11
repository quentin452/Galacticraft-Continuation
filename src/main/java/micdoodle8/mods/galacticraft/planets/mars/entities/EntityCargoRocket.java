package micdoodle8.mods.galacticraft.planets.mars.entities;

import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.planets.mars.items.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;
import micdoodle8.mods.galacticraft.api.world.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import io.netty.buffer.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.entity.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.planets.mars.util.*;
import net.minecraft.nbt.*;
import java.util.*;

public class EntityCargoRocket extends EntityAutoRocket implements IRocketType, IInventory, IWorldTransferCallback
{
    public IRocketType.EnumRocketType rocketType;
    public float rumble;
    
    public EntityCargoRocket(final World par1World) {
        super(par1World);
        this.setSize(0.98f, 2.0f);
    }
    
    public EntityCargoRocket(final World par1World, final double par2, final double par4, final double par6, final IRocketType.EnumRocketType rocketType) {
        super(par1World, par2, par4, par6);
        this.rocketType = rocketType;
        this.cargoItems = new ItemStack[this.getSizeInventory()];
        this.setSize(0.98f, 2.0f);
    }
    
    public int getFuelTankCapacity() {
        return 2000;
    }
    
    public float getCargoFilledAmount() {
        float weight = 1.0f;
        for (final ItemStack stack : this.cargoItems) {
            if (stack != null) {
                weight += (float)0.1;
            }
        }
        return weight;
    }
    
    public ItemStack getPickedResult(final MovingObjectPosition target) {
        return new ItemStack(MarsItems.spaceship, 1, this.rocketType.getIndex() + 10);
    }
    
    public void onUpdate() {
        if (this.launchPhase == EntitySpaceshipBase.EnumLaunchPhase.LAUNCHED.ordinal() && this.hasValidFuel()) {
            double motionScalar = this.timeSinceLaunch / 250.0f;
            motionScalar = Math.min(motionScalar, 1.0);
            final double modifier = this.getCargoFilledAmount();
            motionScalar *= 5.0 / modifier;
            if (!this.landing && motionScalar != 0.0) {
                this.motionY = -motionScalar * Math.cos((this.rotationPitch - 180.0f) * 3.141592653589793 / 180.0);
            }
            double multiplier = 1.0;
            if (this.worldObj.provider instanceof IGalacticraftWorldProvider) {
                multiplier = ((IGalacticraftWorldProvider)this.worldObj.provider).getFuelUsageMultiplier();
                if (multiplier <= 0.0) {
                    multiplier = 1.0;
                }
            }
            if (this.timeSinceLaunch % MathHelper.floor_double(3.0 * (1.0 / multiplier)) == 0.0f) {
                this.removeFuel(1);
                if (!this.hasValidFuel()) {
                    this.stopRocketSound();
                }
            }
        }
        else if (!this.hasValidFuel() && this.getLaunched() && Math.abs(Math.sin(this.timeSinceLaunch / 1000.0f)) / 10.0 != 0.0) {
            this.motionY -= Math.abs(Math.sin(this.timeSinceLaunch / 1000.0f)) / 20.0;
        }
        super.onUpdate();
        if (this.rumble > 0.0f) {
            --this.rumble;
        }
        if (this.rumble < 0.0f) {
            ++this.rumble;
        }
        if (this.launchPhase == EntitySpaceshipBase.EnumLaunchPhase.IGNITED.ordinal() || this.launchPhase == EntitySpaceshipBase.EnumLaunchPhase.LAUNCHED.ordinal()) {
            this.performHurtAnimation();
            this.rumble = this.rand.nextInt(3) - 3.0f;
        }
        int i;
        if (this.timeUntilLaunch >= 100) {
            i = Math.abs(this.timeUntilLaunch / 100);
        }
        else {
            i = 1;
        }
        if ((this.getLaunched() || (this.launchPhase == EntitySpaceshipBase.EnumLaunchPhase.IGNITED.ordinal() && this.rand.nextInt(i) == 0)) && !ConfigManagerCore.disableSpaceshipParticles && this.hasValidFuel() && this.worldObj.isRemote) {
            this.spawnParticles(this.getLaunched());
        }
    }
    
    protected boolean shouldMoveClientSide() {
        return true;
    }
    
    protected void spawnParticles(final boolean launched) {
        double x1 = 2.0 * Math.cos(this.rotationYaw * 3.141592653589793 / 180.0) * Math.sin(this.rotationPitch * 3.141592653589793 / 180.0);
        double z1 = 2.0 * Math.sin(this.rotationYaw * 3.141592653589793 / 180.0) * Math.sin(this.rotationPitch * 3.141592653589793 / 180.0);
        double y1 = 2.0 * Math.cos((this.rotationPitch - 180.0f) * 3.141592653589793 / 180.0);
        if (this.landing && this.targetVec != null) {
            double modifier = this.posY - this.targetVec.y;
            modifier = Math.max(modifier, 1.0);
            x1 *= modifier / 60.0;
            y1 *= modifier / 60.0;
            z1 *= modifier / 60.0;
        }
        final double y2 = this.prevPosY + (this.posY - this.prevPosY) - 0.4;
        if (!this.isDead) {
            GalacticraftCore.proxy.spawnParticle(this.getLaunched() ? "launchFlameLaunched" : "launchFlameIdle", new Vector3(this.posX + 0.2 - this.rand.nextDouble() / 10.0 + x1, y2, this.posZ + 0.2 - this.rand.nextDouble() / 10.0 + z1), new Vector3(x1, y1, z1), new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle(this.getLaunched() ? "launchFlameLaunched" : "launchFlameIdle", new Vector3(this.posX - 0.2 + this.rand.nextDouble() / 10.0 + x1, y2, this.posZ + 0.2 - this.rand.nextDouble() / 10.0 + z1), new Vector3(x1, y1, z1), new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle(this.getLaunched() ? "launchFlameLaunched" : "launchFlameIdle", new Vector3(this.posX - 0.2 + this.rand.nextDouble() / 10.0 + x1, y2, this.posZ - 0.2 + this.rand.nextDouble() / 10.0 + z1), new Vector3(x1, y1, z1), new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle(this.getLaunched() ? "launchFlameLaunched" : "launchFlameIdle", new Vector3(this.posX + 0.2 - this.rand.nextDouble() / 10.0 + x1, y2, this.posZ - 0.2 + this.rand.nextDouble() / 10.0 + z1), new Vector3(x1, y1, z1), new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle(this.getLaunched() ? "launchFlameLaunched" : "launchFlameIdle", new Vector3(this.posX + x1, y2, this.posZ + z1), new Vector3(x1, y1, z1), new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle(this.getLaunched() ? "launchFlameLaunched" : "launchFlameIdle", new Vector3(this.posX + 0.2 + x1, y2, this.posZ + z1), new Vector3(x1, y1, z1), new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle(this.getLaunched() ? "launchFlameLaunched" : "launchFlameIdle", new Vector3(this.posX - 0.2 + x1, y2, this.posZ + z1), new Vector3(x1, y1, z1), new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle(this.getLaunched() ? "launchFlameLaunched" : "launchFlameIdle", new Vector3(this.posX + x1, y2, this.posZ + 0.2 + z1), new Vector3(x1, y1, z1), new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle(this.getLaunched() ? "launchFlameLaunched" : "launchFlameIdle", new Vector3(this.posX + x1, y2, this.posZ - 0.2 + z1), new Vector3(x1, y1, z1), new Object[] { this.riddenByEntity });
        }
    }
    
    public void decodePacketdata(final ByteBuf buffer) {
        this.rocketType = IRocketType.EnumRocketType.values()[buffer.readInt()];
        super.decodePacketdata(buffer);
        this.posX = buffer.readDouble() / 8000.0;
        this.posY = buffer.readDouble() / 8000.0;
        this.posZ = buffer.readDouble() / 8000.0;
    }
    
    public void getNetworkedData(final ArrayList<Object> list) {
        if (this.worldObj.isRemote) {
            return;
        }
        list.add((this.rocketType != null) ? this.rocketType.getIndex() : 0);
        super.getNetworkedData((ArrayList)list);
        list.add(this.posX * 8000.0);
        list.add(this.posY * 8000.0);
        list.add(this.posZ * 8000.0);
    }
    
    public void onReachAtmosphere() {
        if (this.worldObj.isRemote) {
            this.stopRocketSound();
            return;
        }
        GCLog.debug("[Serverside] Cargo rocket reached space, heading to " + this.destinationFrequency);
        this.setTarget(true, this.destinationFrequency);
        if (this.targetVec == null) {
            GCLog.info("Error: the cargo rocket failed to find a valid landing spot when it reached space.");
            this.setDead();
            return;
        }
        GCLog.debug("Destination location = " + this.targetVec.toString());
        if (this.targetDimension == this.worldObj.provider.dimensionId) {
            GCLog.debug("Cargo rocket going into landing mode in same destination.");
            this.setPosition((double)(this.targetVec.x + 0.5f), (double)(this.targetVec.y + 800), (double)(this.targetVec.z + 0.5f));
            this.landing = true;
            return;
        }
        GCLog.debug("Destination is in different dimension: " + this.targetDimension);
        final WorldProvider targetDim = WorldUtil.getProviderForDimensionServer(this.targetDimension);
        if (targetDim != null && targetDim.worldObj instanceof WorldServer) {
            GCLog.debug("Loaded destination dimension " + this.targetDimension);
            this.setPosition((double)(this.targetVec.x + 0.5f), (double)(this.targetVec.y + 800), (double)(this.targetVec.z + 0.5f));
            final Entity e = WorldUtil.transferEntityToDimension((Entity)this, this.targetDimension, (WorldServer)targetDim.worldObj, false, null);
            if (e instanceof EntityCargoRocket) {
                GCLog.debug("Cargo rocket arrived at destination dimension, going into landing mode.");
                e.setPosition((double)(this.targetVec.x + 0.5f), (double)(this.targetVec.y + 800), (double)(this.targetVec.z + 0.5f));
                ((EntityCargoRocket)e).landing = true;
            }
            else {
                GCLog.info("Error: failed to recreate the cargo rocket in landing mode on target planet.");
                e.setDead();
                this.setDead();
            }
            return;
        }
        GCLog.info("Error: the server failed to load the dimension the cargo rocket is supposed to land in. Destroying rocket!");
        this.setDead();
    }
    
    public boolean interactFirst(final EntityPlayer par1EntityPlayer) {
        if (!this.worldObj.isRemote && par1EntityPlayer instanceof EntityPlayerMP) {
            MarsUtil.openCargoRocketInventory((EntityPlayerMP)par1EntityPlayer, this);
        }
        return false;
    }
    
    protected void writeEntityToNBT(final NBTTagCompound nbt) {
        nbt.setInteger("Type", this.rocketType.getIndex());
        super.writeEntityToNBT(nbt);
    }
    
    protected void readEntityFromNBT(final NBTTagCompound nbt) {
        this.rocketType = IRocketType.EnumRocketType.values()[nbt.getInteger("Type")];
        super.readEntityFromNBT(nbt);
    }
    
    public IRocketType.EnumRocketType getType() {
        return this.rocketType;
    }
    
    public int getSizeInventory() {
        if (this.rocketType == null) {
            return 0;
        }
        return this.rocketType.getInventorySpace();
    }
    
    public void onWorldTransferred(final World world) {
        if (this.targetVec != null) {
            this.setPosition((double)(this.targetVec.x + 0.5f), (double)(this.targetVec.y + 800), (double)(this.targetVec.z + 0.5f));
            this.landing = true;
        }
        else {
            this.setDead();
        }
    }
    
    public void onPadDestroyed() {
        if (!this.isDead && this.launchPhase != EntitySpaceshipBase.EnumLaunchPhase.LAUNCHED.ordinal()) {
            this.dropShipAsItem();
            this.setDead();
        }
    }
    
    public int getRocketTier() {
        return Integer.MAX_VALUE;
    }
    
    public int getPreLaunchWait() {
        return 20;
    }
    
    public List<ItemStack> getItemsDropped(final List<ItemStack> droppedItemList) {
        super.getItemsDropped((List)droppedItemList);
        final ItemStack rocket = new ItemStack(MarsItems.spaceship, 1, this.rocketType.getIndex() + 10);
        rocket.setTagCompound(new NBTTagCompound());
        rocket.getTagCompound().setInteger("RocketFuel", this.fuelTank.getFluidAmount());
        droppedItemList.add(rocket);
        return droppedItemList;
    }
    
    public boolean isPlayerRocket() {
        return false;
    }
    
    public double getOnPadYOffset() {
        return 0.0;
    }
}
