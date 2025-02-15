package micdoodle8.mods.galacticraft.planets.asteroids.entities;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.PlayerUtil;
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems;

public class EntityTier3Rocket extends EntityTieredRocket {

    public EntityTier3Rocket(World par1World) {
        super(par1World);
        this.setSize(1.8F, 6F);
        this.yOffset = 1.5F;
    }

    public EntityTier3Rocket(World par1World, double par2, double par4, double par6, EnumRocketType rocketType) {
        super(par1World, par2, par4, par6);
        this.rocketType = rocketType;
        this.cargoItems = new ItemStack[this.getSizeInventory()];
        this.yOffset = 1.5F;
    }

    public EntityTier3Rocket(World par1World, double par2, double par4, double par6, boolean reversed,
        EnumRocketType rocketType, ItemStack[] inv) {
        this(par1World, par2, par4, par6, rocketType);
        this.cargoItems = inv;
    }

    @Override
    public ItemStack getPickedResult(MovingObjectPosition target) {
        return new ItemStack(AsteroidsItems.tier3Rocket, 1, this.rocketType.getIndex());
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    public double getMountedYOffset() {
        return 0.75D;
    }

    @Override
    public float getRotateOffset() {
        return 0.35F;
    }

    @Override
    public double getOnPadYOffset() {
        return 1.75D;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        int i;

        if (this.timeUntilLaunch >= 100) {
            i = Math.abs(this.timeUntilLaunch / 100);
        } else {
            i = 1;
        }

        if ((this.getLaunched() || this.launchPhase == EnumLaunchPhase.IGNITED.ordinal() && this.rand.nextInt(i) == 0)
            && !ConfigManagerCore.disableSpaceshipParticles
            && this.hasValidFuel()) {
            if (this.worldObj.isRemote) {
                this.spawnParticles(this.getLaunched());
            }
        }

        if (this.launchPhase == EnumLaunchPhase.LAUNCHED.ordinal() && this.hasValidFuel()) {
            if (!this.landing) {
                double d = this.timeSinceLaunch / 150;

                if (this.worldObj.provider instanceof WorldProviderSpace
                    && !((WorldProviderSpace) this.worldObj.provider).hasAtmosphere()) {
                    d = Math.min(d * 1.2, 2);
                } else {
                    d = Math.min(d, 1.4);
                }

                if (d != 0.0) {
                    this.motionY = -d * 2.5D * Math.cos((this.rotationPitch - 180) / (180D / Math.PI));
                }
            } else {
                this.motionY -= 0.008D;
            }

            double multiplier = 1.0D;

            if (this.worldObj.provider instanceof IGalacticraftWorldProvider) {
                multiplier = ((IGalacticraftWorldProvider) this.worldObj.provider).getFuelUsageMultiplier();

                if (multiplier <= 0) {
                    multiplier = 1;
                }
            }

            if (this.timeSinceLaunch % MathHelper.floor_double(2 * (1 / multiplier)) == 0) {
                this.removeFuel(1);
                if (!this.hasValidFuel()) {
                    this.stopRocketSound();
                }
            }
        } else if (!this.hasValidFuel() && this.getLaunched()
            && !this.worldObj.isRemote
            && Math.abs(Math.sin(this.timeSinceLaunch / 1000)) / 10 != 0.0) {
                this.motionY -= Math.abs(Math.sin(this.timeSinceLaunch / 1000)) / 20;
            }
    }

    @Override
    public void onTeleport(EntityPlayerMP player) {
        final EntityPlayerMP playerBase = PlayerUtil.getPlayerBaseServerFromPlayer(player, false);

        if (playerBase != null) {
            final GCPlayerStats stats = GCPlayerStats.get(playerBase);

            if (this.cargoItems == null || this.cargoItems.length == 0) {
                stats.rocketStacks = new ItemStack[2];
            } else {
                stats.rocketStacks = this.cargoItems;
            }

            stats.rocketType = this.rocketType.getIndex();
            stats.rocketItem = AsteroidsItems.tier3Rocket;
            stats.fuelLevel = this.fuelTank.getFluidAmount();
        }
    }

    protected void spawnParticles(boolean launched) {
        if (!this.isDead) {
            double x1 = 3.2 * Math.cos(this.rotationYaw / (180D / Math.PI))
                * Math.sin(this.rotationPitch / (180D / Math.PI));
            double z1 = 3.2 * Math.sin(this.rotationYaw / (180D / Math.PI))
                * Math.sin(this.rotationPitch / (180D / Math.PI));
            double y1 = 3.2 * Math.cos((this.rotationPitch - 180) / (180D / Math.PI));
            if (this.landing && this.targetVec != null) {
                double modifier = this.posY - this.targetVec.y;
                modifier = Math.max(modifier, 180.0);
                x1 *= modifier / 200.0D;
                y1 *= Math.min(modifier / 200.0D, 2.5D);
                z1 *= modifier / 200.0D;
            }

            final double y2 = this.prevPosY + (this.posY - this.prevPosY) + y1 - 0.75 * this.motionY - 0.3;

            final double x2 = this.posX + x1 + this.motionX;
            final double z2 = this.posZ + z1 + this.motionZ;
            final Vector3 motionVec = new Vector3(x1 + this.motionX, y1 + this.motionY, z1 + this.motionZ);
            final Vector3 d1 = new Vector3(y1 * 0.1D, -x1 * 0.1D, z1 * 0.1D).rotate(315 - this.rotationYaw, motionVec);
            final Vector3 d2 = new Vector3(x1 * 0.1D, -z1 * 0.1D, y1 * 0.1D).rotate(315 - this.rotationYaw, motionVec);
            final Vector3 d3 = new Vector3(-y1 * 0.1D, x1 * 0.1D, z1 * 0.1D).rotate(315 - this.rotationYaw, motionVec);
            final Vector3 d4 = new Vector3(x1 * 0.1D, z1 * 0.1D, -y1 * 0.1D).rotate(315 - this.rotationYaw, motionVec);
            final Vector3 mv1 = motionVec.clone()
                .translate(d1);
            final Vector3 mv2 = motionVec.clone()
                .translate(d2);
            final Vector3 mv3 = motionVec.clone()
                .translate(d3);
            final Vector3 mv4 = motionVec.clone()
                .translate(d4);
            // T3 - Four flameballs which spread
            this.makeFlame(x2 + d1.x, y2 + d1.y, z2 + d1.z, mv1, this.getLaunched());
            this.makeFlame(x2 + d2.x, y2 + d2.y, z2 + d2.z, mv2, this.getLaunched());
            this.makeFlame(x2 + d3.x, y2 + d3.y, z2 + d3.z, mv3, this.getLaunched());
            this.makeFlame(x2 + d4.x, y2 + d4.y, z2 + d4.z, mv4, this.getLaunched());
        }
    }

    private void makeFlame(double x2, double y2, double z2, Vector3 motionVec, boolean getLaunched) {
        if (getLaunched) {
            GalacticraftCore.proxy.spawnParticle(
                "launchFlameLaunched",
                new Vector3(x2 + 0.4 - this.rand.nextDouble() / 10, y2, z2 + 0.4 - this.rand.nextDouble() / 10),
                motionVec,
                new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle(
                "launchFlameLaunched",
                new Vector3(x2 - 0.4 + this.rand.nextDouble() / 10, y2, z2 + 0.4 - this.rand.nextDouble() / 10),
                motionVec,
                new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle(
                "launchFlameLaunched",
                new Vector3(x2 - 0.4 + this.rand.nextDouble() / 10, y2, z2 - 0.4 + this.rand.nextDouble() / 10),
                motionVec,
                new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle(
                "launchFlameLaunched",
                new Vector3(x2 + 0.4 - this.rand.nextDouble() / 10, y2, z2 - 0.4 + this.rand.nextDouble() / 10),
                motionVec,
                new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle(
                "launchFlameLaunched",
                new Vector3(x2, y2, z2),
                motionVec,
                new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle(
                "launchFlameLaunched",
                new Vector3(x2 + 0.4, y2, z2),
                motionVec,
                new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle(
                "launchFlameLaunched",
                new Vector3(x2 - 0.4, y2, z2),
                motionVec,
                new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle(
                "launchFlameLaunched",
                new Vector3(x2, y2, z2 + 0.4D),
                motionVec,
                new Object[] { this.riddenByEntity });
            GalacticraftCore.proxy.spawnParticle(
                "launchFlameLaunched",
                new Vector3(x2, y2, z2 - 0.4D),
                motionVec,
                new Object[] { this.riddenByEntity });
            return;
        }

        final double x1 = motionVec.x;
        final double y1 = motionVec.y;
        final double z1 = motionVec.z;
        GalacticraftCore.proxy.spawnParticle(
            "launchFlameIdle",
            new Vector3(x2 + 0.4 - this.rand.nextDouble() / 10, y2, z2 + 0.4 - this.rand.nextDouble() / 10),
            new Vector3(x1 + 0.5D, y1 - 0.3D, z1 + 0.5D),
            new Object[] { this.riddenByEntity });
        GalacticraftCore.proxy.spawnParticle(
            "launchFlameIdle",
            new Vector3(x2 - 0.4 + this.rand.nextDouble() / 10, y2, z2 + 0.4 - this.rand.nextDouble() / 10),
            new Vector3(x1 - 0.5D, y1 - 0.3D, z1 + 0.5D),
            new Object[] { this.riddenByEntity });
        GalacticraftCore.proxy.spawnParticle(
            "launchFlameIdle",
            new Vector3(x2 - 0.4 + this.rand.nextDouble() / 10, y2, z2 - 0.4 + this.rand.nextDouble() / 10),
            new Vector3(x1 - 0.5D, y1 - 0.3D, z1 - 0.5D),
            new Object[] { this.riddenByEntity });
        GalacticraftCore.proxy.spawnParticle(
            "launchFlameIdle",
            new Vector3(x2 + 0.4 - this.rand.nextDouble() / 10, y2, z2 - 0.4 + this.rand.nextDouble() / 10),
            new Vector3(x1 + 0.5D, y1 - 0.3D, z1 - 0.5D),
            new Object[] { this.riddenByEntity });
        GalacticraftCore.proxy.spawnParticle(
            "launchFlameIdle",
            new Vector3(x2 + 0.4, y2, z2),
            new Vector3(x1 + 0.8D, y1 - 0.3D, z1),
            new Object[] { this.riddenByEntity });
        GalacticraftCore.proxy.spawnParticle(
            "launchFlameIdle",
            new Vector3(x2 - 0.4, y2, z2),
            new Vector3(x1 - 0.8D, y1 - 0.3D, z1),
            new Object[] { this.riddenByEntity });
        GalacticraftCore.proxy.spawnParticle(
            "launchFlameIdle",
            new Vector3(x2, y2, z2 + 0.4D),
            new Vector3(x1, y1 - 0.3D, z1 + 0.8D),
            new Object[] { this.riddenByEntity });
        GalacticraftCore.proxy.spawnParticle(
            "launchFlameIdle",
            new Vector3(x2, y2, z2 - 0.4D),
            new Vector3(x1, y1 - 0.3D, z1 - 0.8D),
            new Object[] { this.riddenByEntity });
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
        return !this.isDead && par1EntityPlayer.getDistanceSqToEntity(this) <= 64.0D;
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
    }

    // @RuntimeInterface(clazz = "icbm.api.IMissileLockable", modID =
    // "ICBM|Explosion")
    // public boolean canLock(IMissile missile)
    // {
    // return true;
    // }
    //
    // @RuntimeInterface(clazz = "icbm.api.IMissileLockable", modID =
    // "ICBM|Explosion")
    // public Vector3 getPredictedPosition(int ticks)
    // {
    // return new Vector3(this);
    // } TODO Re-implement when ICBM is ready

    @Override
    public void onPadDestroyed() {
        if (!this.isDead && this.launchPhase != EnumLaunchPhase.LAUNCHED.ordinal()) {
            this.dropShipAsItem();
            this.setDead();
        }
    }

    // @RuntimeInterface(clazz = "icbm.api.sentry.IAATarget", modID =
    // "ICBM|Explosion")
    // public void destroyCraft()
    // {
    // this.setDead();
    // }
    //
    // @RuntimeInterface(clazz = "icbm.api.sentry.IAATarget", modID =
    // "ICBM|Explosion")
    // public int doDamage(int damage)
    // {
    // this.shipDamage += damage;
    // return damage;
    // }
    //
    // @RuntimeInterface(clazz = "icbm.api.sentry.IAATarget", modID =
    // "ICBM|Explosion")
    // public boolean canBeTargeted(Object entity)
    // {
    // return this.launchPhase == EnumLaunchPhase.LAUNCHED.getPhase() &&
    // this.timeSinceLaunch > 50;
    // } TODO Fix when ICBM is ready

    @Override
    public int getRocketTier() {
        return 3;
    }

    @Override
    public int getFuelTankCapacity() {
        return 1500;
    }

    @Override
    public int getPreLaunchWait() {
        return 400;
    }

    @Override
    public float getCameraZoom() {
        return 15.0F;
    }

    @Override
    public boolean defaultThirdPerson() {
        return true;
    }

    @Override
    public List<ItemStack> getItemsDropped(List<ItemStack> droppedItems) {
        super.getItemsDropped(droppedItems);
        final ItemStack rocket = new ItemStack(AsteroidsItems.tier3Rocket, 1, this.rocketType.getIndex());
        rocket.setTagCompound(new NBTTagCompound());
        rocket.getTagCompound()
            .setInteger("RocketFuel", this.fuelTank.getFluidAmount());
        droppedItems.add(rocket);
        return droppedItems;
    }
}
