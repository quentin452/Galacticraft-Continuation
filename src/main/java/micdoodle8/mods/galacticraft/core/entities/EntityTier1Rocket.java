package micdoodle8.mods.galacticraft.core.entities;

import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.*;
import micdoodle8.mods.galacticraft.api.world.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.entity.*;
import net.minecraft.nbt.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.tile.*;
import micdoodle8.mods.galacticraft.core.tile.*;

public class EntityTier1Rocket extends EntityTieredRocket
{
    public EntityTier1Rocket(final World par1World) {
        super(par1World);
        this.setSize(1.2f, 3.5f);
        this.yOffset = 1.5f;
    }

    public EntityTier1Rocket(final World par1World, final double par2, final double par4, final double par6, final IRocketType.EnumRocketType rocketType) {
        super(par1World, par2, par4, par6);
        this.rocketType = rocketType;
        this.cargoItems = new ItemStack[this.getSizeInventory()];
        this.setSize(1.2f, 3.5f);
        this.yOffset = 1.5f;
    }

    public float getRotateOffset() {
        return -1.5f;
    }

    public ItemStack getPickedResult(final MovingObjectPosition target) {
        return new ItemStack(GCItems.rocketTier1, 1, this.rocketType.getIndex());
    }

    public void onUpdate() {
        super.onUpdate();
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
        if (this.launchPhase == EntitySpaceshipBase.EnumLaunchPhase.LAUNCHED.ordinal() && this.hasValidFuel()) {
            if (!this.landing) {
                double d = this.timeSinceLaunch / 150.0f;
                if (this.worldObj.provider instanceof WorldProviderSpace && !((WorldProviderSpace)this.worldObj.provider).hasAtmosphere()) {
                    d = Math.min(d * 1.2, 1.6);
                }
                else {
                    d = Math.min(d, 1.0);
                }
                if (d != 0.0) {
                    this.motionY = -d * Math.cos((this.rotationPitch - 180.0f) * 3.141592653589793 / 180.0);
                }
            }
            else {
                this.motionY -= 0.008;
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
        else if (!this.hasValidFuel() && this.getLaunched() && !this.worldObj.isRemote && Math.abs(Math.sin(this.timeSinceLaunch / 1000.0f)) / 10.0 != 0.0) {
            this.motionY -= Math.abs(Math.sin(this.timeSinceLaunch / 1000.0f)) / 20.0;
        }
    }

    public void onTeleport(final EntityPlayerMP player) {
        final EntityPlayerMP playerBase = PlayerUtil.getPlayerBaseServerFromPlayer((EntityPlayer)player, false);
        if (playerBase != null) {
            final GCPlayerStats stats = GCPlayerStats.get(player);
            if (this.cargoItems == null || this.cargoItems.length == 0) {
                stats.rocketStacks = new ItemStack[2];
            }
            else {
                stats.rocketStacks = this.cargoItems;
            }
            stats.rocketType = this.rocketType.getIndex();
            stats.rocketItem = GCItems.rocketTier1;
            stats.fuelLevel = this.fuelTank.getFluidAmount();
        }
    }

    protected void spawnParticles(final boolean launched) {
        if (!this.isDead) {
            double x1 = 2.0 * Math.cos(this.rotationYaw * 3.141592653589793 / 180.0) * Math.sin(this.rotationPitch * 3.141592653589793 / 180.0);
            double z1 = 2.0 * Math.sin(this.rotationYaw * 3.141592653589793 / 180.0) * Math.sin(this.rotationPitch * 3.141592653589793 / 180.0);
            double y1 = 2.0 * Math.cos((this.rotationPitch - 180.0f) * 3.141592653589793 / 180.0);
            if (this.landing && this.targetVec != null) {
                double modifier = this.posY - this.targetVec.y;
                modifier = Math.min(Math.max(modifier, 120.0), 300.0);
                x1 *= modifier / 100.0;
                y1 *= modifier / 100.0;
                z1 *= modifier / 100.0;
            }
            final double y2 = this.prevPosY + (this.posY - this.prevPosY) + y1 - this.motionY;
            final double x2 = this.posX + x1 - this.motionX;
            final double z2 = this.posZ + z1 - this.motionZ;
            final EntityLivingBase riddenByEntity = (this.riddenByEntity instanceof EntityLivingBase) ? (EntityLivingBase) this.riddenByEntity : null;
            if (this.getLaunched()) {
                final Vector3 motionVec = new Vector3(x1, y1, z1);
                GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2 + 0.4 - this.rand.nextDouble() / 10.0, y2, z2 + 0.4 - this.rand.nextDouble() / 10.0), motionVec, new Object[] { riddenByEntity });
                GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2 - 0.4 + this.rand.nextDouble() / 10.0, y2, z2 + 0.4 - this.rand.nextDouble() / 10.0), motionVec, new Object[] { riddenByEntity });
                GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2 - 0.4 + this.rand.nextDouble() / 10.0, y2, z2 - 0.4 + this.rand.nextDouble() / 10.0), motionVec, new Object[] { riddenByEntity });
                GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2 + 0.4 - this.rand.nextDouble() / 10.0, y2, z2 - 0.4 + this.rand.nextDouble() / 10.0), motionVec, new Object[] { riddenByEntity });
                GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2, y2, z2), motionVec, new Object[] { riddenByEntity });
                GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2 + 0.4, y2, z2), motionVec, new Object[] { riddenByEntity });
                GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2 - 0.4, y2, z2), motionVec, new Object[] { riddenByEntity });
                GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2, y2, z2 + 0.4), motionVec, new Object[] { riddenByEntity });
                GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2, y2, z2 - 0.4), motionVec, new Object[] { riddenByEntity });
            }
            else {
                GalacticraftCore.proxy.spawnParticle("launchFlameIdle", new Vector3(x2 + 0.4 - this.rand.nextDouble() / 10.0, y2, z2 + 0.4 - this.rand.nextDouble() / 10.0), new Vector3(x1 + 0.7, y1 - 1.0, z1 + 0.7), new Object[] { riddenByEntity });
                GalacticraftCore.proxy.spawnParticle("launchFlameIdle", new Vector3(x2 - 0.4 + this.rand.nextDouble() / 10.0, y2, z2 + 0.4 - this.rand.nextDouble() / 10.0), new Vector3(x1 - 0.7, y1 - 1.0, z1 + 0.7), new Object[] { riddenByEntity });
                GalacticraftCore.proxy.spawnParticle("launchFlameIdle", new Vector3(x2 - 0.4 + this.rand.nextDouble() / 10.0, y2, z2 - 0.4 + this.rand.nextDouble() / 10.0), new Vector3(x1 - 0.7, y1 - 1.0, z1 - 0.7), new Object[] { riddenByEntity });
                GalacticraftCore.proxy.spawnParticle("launchFlameIdle", new Vector3(x2 + 0.4 - this.rand.nextDouble() / 10.0, y2, z2 - 0.4 + this.rand.nextDouble() / 10.0), new Vector3(x1 + 0.7, y1 - 1.0, z1 - 0.7), new Object[] { riddenByEntity });
            }
        }
    }

    public boolean isUseableByPlayer(final EntityPlayer par1EntityPlayer) {
        return !this.isDead && par1EntityPlayer.getDistanceSqToEntity((Entity)this) <= 64.0;
    }

    protected void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);
    }

    protected void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
    }

    public int getPreLaunchWait() {
        return 400;
    }

    public List<ItemStack> getItemsDropped(final List<ItemStack> droppedItems) {
        super.getItemsDropped((List)droppedItems);
        final ItemStack rocket = new ItemStack(GCItems.rocketTier1, 1, this.rocketType.getIndex());
        rocket.setTagCompound(new NBTTagCompound());
        rocket.getTagCompound().setInteger("RocketFuel", this.fuelTank.getFluidAmount());
        droppedItems.add(rocket);
        return droppedItems;
    }

    public boolean hasCustomInventoryName() {
        return false;
    }

    public boolean isItemValidForSlot(final int i, final ItemStack itemstack) {
        return false;
    }

    public void onPadDestroyed() {
        if (!this.isDead && this.launchPhase != EntitySpaceshipBase.EnumLaunchPhase.LAUNCHED.ordinal()) {
            this.dropShipAsItem();
            this.setDead();
        }
    }

    public boolean isDockValid(final IFuelDock dock) {
        return dock instanceof TileEntityLandingPad;
    }

    public int getRocketTier() {
        return 1;
    }

    public int getFuelTankCapacity() {
        return 1000;
    }

    public float getCameraZoom() {
        return 15.0f;
    }

    public boolean defaultThirdPerson() {
        return true;
    }

    public double getOnPadYOffset() {
        return 1.5;
    }
}
