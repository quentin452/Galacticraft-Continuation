package micdoodle8.mods.galacticraft.planets.asteroids.entities;

import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.planets.asteroids.items.*;
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

public class EntityTier3Rocket extends EntityTieredRocket
{
    public EntityTier3Rocket(final World par1World) {
        super(par1World);
        this.setSize(1.8f, 6.0f);
        this.yOffset = 1.5f;
    }
    
    public EntityTier3Rocket(final World par1World, final double par2, final double par4, final double par6, final IRocketType.EnumRocketType rocketType) {
        super(par1World, par2, par4, par6);
        this.rocketType = rocketType;
        this.cargoItems = new ItemStack[this.getSizeInventory()];
        this.yOffset = 1.5f;
    }
    
    public EntityTier3Rocket(final World par1World, final double par2, final double par4, final double par6, final boolean reversed, final IRocketType.EnumRocketType rocketType, final ItemStack[] inv) {
        this(par1World, par2, par4, par6, rocketType);
        this.cargoItems = inv;
    }
    
    public ItemStack getPickedResult(final MovingObjectPosition target) {
        return new ItemStack(AsteroidsItems.tier3Rocket, 1, this.rocketType.getIndex());
    }
    
    protected void entityInit() {
        super.entityInit();
    }
    
    public double getMountedYOffset() {
        return 0.75;
    }
    
    public float getRotateOffset() {
        return 0.35f;
    }
    
    public double getOnPadYOffset() {
        return 1.75;
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
                    d = Math.min(d * 1.2, 2.0);
                }
                else {
                    d = Math.min(d, 1.4);
                }
                if (d != 0.0) {
                    this.motionY = -d * 2.5 * Math.cos((this.rotationPitch - 180.0f) / 57.2957795);
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
            if (this.timeSinceLaunch % MathHelper.floor_double(2.0 * (1.0 / multiplier)) == 0.0f) {
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
            final GCPlayerStats stats = GCPlayerStats.get(playerBase);
            if (this.cargoItems == null || this.cargoItems.length == 0) {
                stats.rocketStacks = new ItemStack[2];
            }
            else {
                stats.rocketStacks = this.cargoItems;
            }
            stats.rocketType = this.rocketType.getIndex();
            stats.rocketItem = AsteroidsItems.tier3Rocket;
            stats.fuelLevel = this.fuelTank.getFluidAmount();
        }
    }
    
    protected void spawnParticles(final boolean launched) {
        if (!this.isDead) {
            double x1 = 3.2 * Math.cos(this.rotationYaw / 57.2957795) * Math.sin(this.rotationPitch / 57.2957795);
            double z1 = 3.2 * Math.sin(this.rotationYaw / 57.2957795) * Math.sin(this.rotationPitch / 57.2957795);
            double y1 = 3.2 * Math.cos((this.rotationPitch - 180.0f) / 57.2957795);
            if (this.landing && this.targetVec != null) {
                double modifier = this.posY - this.targetVec.y;
                modifier = Math.max(modifier, 180.0);
                x1 *= modifier / 200.0;
                y1 *= Math.min(modifier / 200.0, 2.5);
                z1 *= modifier / 200.0;
            }
            final double y2 = this.prevPosY + (this.posY - this.prevPosY) + y1 - 0.75 * this.motionY - 0.3;
            final double x2 = this.posX + x1 + this.motionX;
            final double z2 = this.posZ + z1 + this.motionZ;
            final Vector3 motionVec = new Vector3(x1 + this.motionX, y1 + this.motionY, z1 + this.motionZ);
            final Vector3 d1 = new Vector3(y1 * 0.1, -x1 * 0.1, z1 * 0.1).rotate(315.0f - this.rotationYaw, motionVec);
            final Vector3 d2 = new Vector3(x1 * 0.1, -z1 * 0.1, y1 * 0.1).rotate(315.0f - this.rotationYaw, motionVec);
            final Vector3 d3 = new Vector3(-y1 * 0.1, x1 * 0.1, z1 * 0.1).rotate(315.0f - this.rotationYaw, motionVec);
            final Vector3 d4 = new Vector3(x1 * 0.1, z1 * 0.1, -y1 * 0.1).rotate(315.0f - this.rotationYaw, motionVec);
            final Vector3 mv1 = motionVec.clone().translate(d1);
            final Vector3 mv2 = motionVec.clone().translate(d2);
            final Vector3 mv3 = motionVec.clone().translate(d3);
            final Vector3 mv4 = motionVec.clone().translate(d4);
            this.makeFlame(x2 + d1.x, y2 + d1.y, z2 + d1.z, mv1, this.getLaunched());
            this.makeFlame(x2 + d2.x, y2 + d2.y, z2 + d2.z, mv2, this.getLaunched());
            this.makeFlame(x2 + d3.x, y2 + d3.y, z2 + d3.z, mv3, this.getLaunched());
            this.makeFlame(x2 + d4.x, y2 + d4.y, z2 + d4.z, mv4, this.getLaunched());
        }
    }
    
    private void makeFlame(final double x2, final double y2, final double z2, final Vector3 motionVec, final boolean getLaunched) {
        if (getLaunched) {
            GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2 + 0.4 - this.rand.nextDouble() / 10.0, y2, z2 + 0.4 - this.rand.nextDouble() / 10.0), motionVec, new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2 - 0.4 + this.rand.nextDouble() / 10.0, y2, z2 + 0.4 - this.rand.nextDouble() / 10.0), motionVec, new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2 - 0.4 + this.rand.nextDouble() / 10.0, y2, z2 - 0.4 + this.rand.nextDouble() / 10.0), motionVec, new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2 + 0.4 - this.rand.nextDouble() / 10.0, y2, z2 - 0.4 + this.rand.nextDouble() / 10.0), motionVec, new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2, y2, z2), motionVec, new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2 + 0.4, y2, z2), motionVec, new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2 - 0.4, y2, z2), motionVec, new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2, y2, z2 + 0.4), motionVec, new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2, y2, z2 - 0.4), motionVec, new Object[] { this.riddenByEntity });
            return;
        }
        final double x3 = motionVec.x;
        final double y3 = motionVec.y;
        final double z3 = motionVec.z;
        GalacticraftCore.proxy.spawnParticle("launchFlameIdle", new Vector3(x2 + 0.4 - this.rand.nextDouble() / 10.0, y2, z2 + 0.4 - this.rand.nextDouble() / 10.0), new Vector3(x3 + 0.5, y3 - 0.3, z3 + 0.5), new Object[] { this.riddenByEntity });
        GalacticraftCore.proxy.spawnParticle("launchFlameIdle", new Vector3(x2 - 0.4 + this.rand.nextDouble() / 10.0, y2, z2 + 0.4 - this.rand.nextDouble() / 10.0), new Vector3(x3 - 0.5, y3 - 0.3, z3 + 0.5), new Object[] { this.riddenByEntity });
        GalacticraftCore.proxy.spawnParticle("launchFlameIdle", new Vector3(x2 - 0.4 + this.rand.nextDouble() / 10.0, y2, z2 - 0.4 + this.rand.nextDouble() / 10.0), new Vector3(x3 - 0.5, y3 - 0.3, z3 - 0.5), new Object[] { this.riddenByEntity });
        GalacticraftCore.proxy.spawnParticle("launchFlameIdle", new Vector3(x2 + 0.4 - this.rand.nextDouble() / 10.0, y2, z2 - 0.4 + this.rand.nextDouble() / 10.0), new Vector3(x3 + 0.5, y3 - 0.3, z3 - 0.5), new Object[] { this.riddenByEntity });
        GalacticraftCore.proxy.spawnParticle("launchFlameIdle", new Vector3(x2 + 0.4, y2, z2), new Vector3(x3 + 0.8, y3 - 0.3, z3), new Object[] { this.riddenByEntity });
        GalacticraftCore.proxy.spawnParticle("launchFlameIdle", new Vector3(x2 - 0.4, y2, z2), new Vector3(x3 - 0.8, y3 - 0.3, z3), new Object[] { this.riddenByEntity });
        GalacticraftCore.proxy.spawnParticle("launchFlameIdle", new Vector3(x2, y2, z2 + 0.4), new Vector3(x3, y3 - 0.3, z3 + 0.8), new Object[] { this.riddenByEntity });
        GalacticraftCore.proxy.spawnParticle("launchFlameIdle", new Vector3(x2, y2, z2 - 0.4), new Vector3(x3, y3 - 0.3, z3 - 0.8), new Object[] { this.riddenByEntity });
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
    
    public void onPadDestroyed() {
        if (!this.isDead && this.launchPhase != EntitySpaceshipBase.EnumLaunchPhase.LAUNCHED.ordinal()) {
            this.dropShipAsItem();
            this.setDead();
        }
    }
    
    public int getRocketTier() {
        return 3;
    }
    
    public int getFuelTankCapacity() {
        return 1500;
    }
    
    public int getPreLaunchWait() {
        return 400;
    }
    
    public float getCameraZoom() {
        return 15.0f;
    }
    
    public boolean defaultThirdPerson() {
        return true;
    }
    
    public List<ItemStack> getItemsDropped(final List<ItemStack> droppedItems) {
        super.getItemsDropped((List)droppedItems);
        final ItemStack rocket = new ItemStack(AsteroidsItems.tier3Rocket, 1, this.rocketType.getIndex());
        rocket.setTagCompound(new NBTTagCompound());
        rocket.getTagCompound().setInteger("RocketFuel", this.fuelTank.getFluidAmount());
        droppedItems.add(rocket);
        return droppedItems;
    }
}
