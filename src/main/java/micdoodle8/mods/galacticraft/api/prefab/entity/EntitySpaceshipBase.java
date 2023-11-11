package micdoodle8.mods.galacticraft.api.prefab.entity;

import micdoodle8.mods.galacticraft.api.entity.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraftforge.fluids.*;
import net.minecraft.command.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.entity.item.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.api.world.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraft.tileentity.*;
import io.netty.buffer.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.nbt.*;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.api.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.client.gui.screen.*;
import org.lwjgl.opengl.*;
import net.minecraftforge.event.entity.*;

public abstract class EntitySpaceshipBase extends Entity implements IPacketReceiver, IIgnoreShift, ITelemetry
{
    public int launchPhase;
    protected long ticks;
    protected double dragAir;
    public int timeUntilLaunch;
    public float timeSinceLaunch;
    public float rollAmplitude;
    public float shipDamage;
    private ArrayList<BlockVec3Dim> telemetryList;
    private boolean addToTelemetry;
    public FluidTank fuelTank;
    public static IEntitySelector rocketSelector;
    
    public EntitySpaceshipBase(final World par1World) {
        super(par1World);
        this.ticks = 0L;
        this.telemetryList = new ArrayList<BlockVec3Dim>();
        this.addToTelemetry = false;
        this.fuelTank = new FluidTank(this.getFuelTankCapacity() * ConfigManagerCore.rocketFuelFactor);
        this.launchPhase = EnumLaunchPhase.UNIGNITED.ordinal();
        this.preventEntitySpawning = true;
        this.ignoreFrustumCheck = true;
        this.renderDistanceWeight = 5.0;
    }
    
    public abstract int getFuelTankCapacity();
    
    public abstract int getMaxFuel();
    
    public abstract int getScaledFuelLevel(final int p0);
    
    public abstract int getPreLaunchWait();
    
    protected boolean canTriggerWalking() {
        return false;
    }
    
    protected void entityInit() {
    }
    
    public AxisAlignedBB getCollisionBox(final Entity par1Entity) {
        return null;
    }
    
    public AxisAlignedBB getBoundingBox() {
        return null;
    }
    
    public boolean canBePushed() {
        return false;
    }
    
    public boolean attackEntityFrom(final DamageSource par1DamageSource, final float par2) {
        if (this.worldObj.isRemote || this.isDead) {
            return true;
        }
        final boolean flag = par1DamageSource.getEntity() instanceof EntityPlayer && ((EntityPlayer)par1DamageSource.getEntity()).capabilities.isCreativeMode;
        final Entity e = par1DamageSource.getEntity();
        if (this.isEntityInvulnerable() || this.posY > 300.0 || (e instanceof EntityLivingBase && !(e instanceof EntityPlayer))) {
            return false;
        }
        this.rollAmplitude = 10.0f;
        this.setBeenAttacked();
        this.shipDamage += par2 * 10.0f;
        if (e instanceof EntityPlayer && ((EntityPlayer)e).capabilities.isCreativeMode) {
            this.shipDamage = 100.0f;
        }
        if (flag || (this.shipDamage > 90.0f && !this.worldObj.isRemote)) {
            if (this.riddenByEntity != null) {
                this.riddenByEntity.mountEntity((Entity)null);
            }
            if (flag) {
                this.setDead();
            }
            else {
                this.setDead();
                this.dropShipAsItem();
            }
            return true;
        }
        return true;
    }
    
    public void dropShipAsItem() {
        if (this.worldObj.isRemote) {
            return;
        }
        for (final ItemStack item : this.getItemsDropped(new ArrayList<ItemStack>())) {
            final EntityItem entityItem = this.entityDropItem(item, 0.0f);
            if (item.hasTagCompound()) {
                entityItem.getEntityItem().setTagCompound((NBTTagCompound)item.getTagCompound().copy());
            }
        }
    }
    
    public abstract List<ItemStack> getItemsDropped(final List<ItemStack> p0);
    
    public void performHurtAnimation() {
        this.rollAmplitude = 5.0f;
        this.shipDamage += this.shipDamage * 10.0f;
    }
    
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }
    
    public boolean shouldRiderSit() {
        return false;
    }
    
    public void onUpdate() {
        if (this.ticks >= Long.MAX_VALUE) {
            this.ticks = 0L;
        }
        ++this.ticks;
        super.onUpdate();
        if (this.addToTelemetry) {
            this.addToTelemetry = false;
            for (final BlockVec3Dim vec : new ArrayList<BlockVec3Dim>(this.telemetryList)) {
                final TileEntity t1 = vec.getTileEntityNoLoad();
                if (t1 instanceof TileEntityTelemetry && !t1.isInvalid() && ((TileEntityTelemetry)t1).linkedEntity == this) {
                    ((TileEntityTelemetry)t1).addTrackedEntity(this);
                }
            }
        }
        if (this.riddenByEntity != null) {
            this.riddenByEntity.fallDistance = 0.0f;
        }
        if (this.posY > ((this.worldObj.provider instanceof IExitHeight) ? ((IExitHeight)this.worldObj.provider).getYCoordinateToTeleport() : 1200.0)) {
            this.onReachAtmosphere();
        }
        if (this.rollAmplitude > 0.0f) {
            --this.rollAmplitude;
        }
        if (this.shipDamage > 0.0f) {
            --this.shipDamage;
        }
        if (!this.worldObj.isRemote) {
            if (this.posY < 0.0) {
                this.kill();
            }
            else if (this.posY > ((this.worldObj.provider instanceof IExitHeight) ? ((IExitHeight)this.worldObj.provider).getYCoordinateToTeleport() : 1200.0) + 100.0) {
                if (this.riddenByEntity instanceof EntityPlayerMP) {
                    final GCPlayerStats stats = GCPlayerStats.get((EntityPlayerMP)this.riddenByEntity);
                    if (stats.usingPlanetSelectionGui) {
                        this.kill();
                    }
                }
                else {
                    this.kill();
                }
            }
            if (this.timeSinceLaunch > 50.0f && this.onGround) {
                this.failRocket();
            }
        }
        if (this.launchPhase == EnumLaunchPhase.UNIGNITED.ordinal()) {
            this.timeUntilLaunch = this.getPreLaunchWait();
        }
        if (this.launchPhase == EnumLaunchPhase.LAUNCHED.ordinal()) {
            ++this.timeSinceLaunch;
        }
        else {
            this.timeSinceLaunch = 0.0f;
        }
        if (this.timeUntilLaunch > 0 && this.launchPhase == EnumLaunchPhase.IGNITED.ordinal()) {
            --this.timeUntilLaunch;
        }
        AxisAlignedBB box = null;
        box = this.boundingBox.expand(0.2, 0.2, 0.2);
        final List<?> var15 = (List<?>)this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)this, box);
        if (var15 != null && !var15.isEmpty()) {
            for (int var16 = 0; var16 < var15.size(); ++var16) {
                final Entity var17 = (Entity)var15.get(var16);
                if (var17 != this.riddenByEntity) {
                    var17.applyEntityCollision((Entity)this);
                }
            }
        }
        if (this.timeUntilLaunch == 0 && this.launchPhase == EnumLaunchPhase.IGNITED.ordinal()) {
            this.setLaunchPhase(EnumLaunchPhase.LAUNCHED);
            this.onLaunch();
        }
        if (this.rotationPitch > 90.0f) {
            this.rotationPitch = 90.0f;
        }
        if (this.rotationPitch < -90.0f) {
            this.rotationPitch = -90.0f;
        }
        this.motionX = -(50.0 * Math.cos(this.rotationYaw * 3.141592653589793 / 180.0) * Math.sin(this.rotationPitch * 0.01 * 3.141592653589793 / 180.0));
        this.motionZ = -(50.0 * Math.sin(this.rotationYaw * 3.141592653589793 / 180.0) * Math.sin(this.rotationPitch * 0.01 * 3.141592653589793 / 180.0));
        if (this.launchPhase != EnumLaunchPhase.LAUNCHED.ordinal()) {
            final double motionX = 0.0;
            this.motionZ = motionX;
            this.motionY = motionX;
            this.motionX = motionX;
        }
        if (this.worldObj.isRemote) {
            this.setPosition(this.posX, this.posY, this.posZ);
            if (this.shouldMoveClientSide()) {
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
            }
        }
        else {
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
        }
        this.setRotation(this.rotationYaw, this.rotationPitch);
        if (this.worldObj.isRemote) {
            this.setPosition(this.posX, this.posY, this.posZ);
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (!this.worldObj.isRemote && this.ticks % 3L == 0L) {
            GalacticraftCore.packetPipeline.sendToDimension(new PacketDynamic(this), this.worldObj.provider.dimensionId);
        }
    }
    
    protected boolean shouldMoveClientSide() {
        return true;
    }
    
    public void decodePacketdata(final ByteBuf buffer) {
        if (!this.worldObj.isRemote) {
            new Exception().printStackTrace();
        }
        this.setLaunchPhase(EnumLaunchPhase.values()[buffer.readInt()]);
        this.timeSinceLaunch = buffer.readFloat();
        this.timeUntilLaunch = buffer.readInt();
    }
    
    public void getNetworkedData(final ArrayList<Object> list) {
        if (this.worldObj.isRemote) {
            return;
        }
        list.add(this.launchPhase);
        list.add(this.timeSinceLaunch);
        list.add(this.timeUntilLaunch);
    }
    
    public void turnYaw(final float f) {
        this.rotationYaw += f;
    }
    
    public void turnPitch(final float f) {
        this.rotationPitch += f;
    }
    
    protected void failRocket() {
        if (this.riddenByEntity != null) {
            this.riddenByEntity.attackEntityFrom((DamageSource)DamageSourceGC.spaceshipCrash, 81.0f);
        }
        if (!ConfigManagerCore.disableSpaceshipGrief) {
            this.worldObj.createExplosion((Entity)this, this.posX, this.posY, this.posZ, 5.0f, true);
        }
        this.setDead();
    }
    
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(final double par1, final double par3, final double par5, final float par7, final float par8, final int par9) {
        this.setRotation(par7, par8);
    }
    
    protected void writeEntityToNBT(final NBTTagCompound nbt) {
        nbt.setInteger("launchPhase", this.launchPhase + 1);
        nbt.setInteger("timeUntilLaunch", this.timeUntilLaunch);
        if (this.telemetryList.size() > 0) {
            final NBTTagList teleNBTList = new NBTTagList();
            for (final BlockVec3Dim vec : new ArrayList<BlockVec3Dim>(this.telemetryList)) {
                final NBTTagCompound tag = new NBTTagCompound();
                vec.writeToNBT(tag);
                teleNBTList.appendTag((NBTBase)tag);
            }
            nbt.setTag("telemetryList", (NBTBase)teleNBTList);
        }
    }
    
    protected void readEntityFromNBT(final NBTTagCompound nbt) {
        this.timeUntilLaunch = nbt.getInteger("timeUntilLaunch");
        boolean hasOldTags = false;
        if (nbt.func_150296_c().contains("launched")) {
            hasOldTags = true;
            final boolean launched = nbt.getBoolean("launched");
            if (launched) {
                this.setLaunchPhase(EnumLaunchPhase.LAUNCHED);
            }
        }
        if (nbt.func_150296_c().contains("ignite")) {
            hasOldTags = true;
            final int ignite = nbt.getInteger("ignite");
            if (ignite == 1) {
                this.setLaunchPhase(EnumLaunchPhase.IGNITED);
            }
        }
        if (hasOldTags) {
            if (this.launchPhase != EnumLaunchPhase.LAUNCHED.ordinal() && this.launchPhase != EnumLaunchPhase.IGNITED.ordinal()) {
                this.setLaunchPhase(EnumLaunchPhase.UNIGNITED);
            }
        }
        else {
            this.setLaunchPhase(EnumLaunchPhase.values()[nbt.getInteger("launchPhase") - 1]);
        }
        this.telemetryList.clear();
        if (nbt.hasKey("telemetryList")) {
            final NBTTagList teleNBT = nbt.getTagList("telemetryList", 10);
            if (teleNBT.tagCount() > 0) {
                for (int j = teleNBT.tagCount() - 1; j >= 0; --j) {
                    final NBTTagCompound tag1 = teleNBT.getCompoundTagAt(j);
                    if (tag1 != null) {
                        this.telemetryList.add(BlockVec3Dim.readFromNBT(tag1));
                    }
                }
                this.addToTelemetry = true;
            }
        }
    }
    
    public boolean getLaunched() {
        return this.launchPhase == EnumLaunchPhase.LAUNCHED.ordinal();
    }
    
    public boolean canBeRidden() {
        return false;
    }
    
    public void ignite() {
        this.setLaunchPhase(EnumLaunchPhase.IGNITED);
    }
    
    public double getMountedYOffset() {
        return -0.9;
    }
    
    public double getOnPadYOffset() {
        return 0.0;
    }
    
    public void onLaunch() {
        MinecraftForge.EVENT_BUS.post((Event)new RocketLaunchEvent(this));
    }
    
    public void onReachAtmosphere() {
    }
    
    @SideOnly(Side.CLIENT)
    public void spawnParticle(final String var1, final double var2, final double var4, final double var6, final double var8, final double var10, final double var12) {
    }
    
    public boolean canRiderInteract() {
        return true;
    }
    
    public ResourceLocation getSpaceshipGui() {
        return GalacticraftRegistry.getResouceLocationForDimension((Class)this.worldObj.provider.getClass());
    }
    
    public void setLaunchPhase(final EnumLaunchPhase phase) {
        this.launchPhase = phase.ordinal();
    }
    
    public boolean shouldIgnoreShiftExit() {
        return this.launchPhase == EnumLaunchPhase.LAUNCHED.ordinal();
    }
    
    public void addTelemetry(final TileEntityTelemetry tile) {
        this.telemetryList.add(new BlockVec3Dim(tile));
    }
    
    public ArrayList<TileEntityTelemetry> getTelemetry() {
        final ArrayList<TileEntityTelemetry> returnList = new ArrayList<TileEntityTelemetry>();
        for (final BlockVec3Dim vec : new ArrayList<BlockVec3Dim>(this.telemetryList)) {
            final TileEntity t1 = vec.getTileEntity();
            if (t1 instanceof TileEntityTelemetry && !t1.isInvalid() && ((TileEntityTelemetry)t1).linkedEntity == this) {
                returnList.add((TileEntityTelemetry)t1);
            }
        }
        return returnList;
    }
    
    public void transmitData(final int[] data) {
        data[0] = this.timeUntilLaunch;
        data[1] = (int)this.posY;
        data[3] = this.getScaledFuelLevel(100);
        data[4] = (int)this.rotationPitch;
    }
    
    public void receiveData(final int[] data, final String[] str) {
        final int countdown = data[0];
        str[0] = "";
        str[1] = ((countdown == 400) ? GCCoreUtil.translate("gui.rocket.onLaunchpad") : ((countdown > 0) ? (GCCoreUtil.translate("gui.rocket.countdown") + ": " + countdown / 20) : GCCoreUtil.translate("gui.rocket.launched")));
        str[2] = GCCoreUtil.translate("gui.rocket.height") + ": " + data[1];
        str[3] = GameScreenText.makeSpeedString(data[2]);
        str[4] = GCCoreUtil.translate("gui.message.fuel.name") + ": " + data[3] + "%";
    }
    
    public void adjustDisplay(final int[] data) {
        GL11.glRotatef((float)data[4], -1.0f, 0.0f, 0.0f);
        GL11.glTranslatef(0.0f, this.height / 4.0f, 0.0f);
    }
    
    static {
        EntitySpaceshipBase.rocketSelector = (IEntitySelector)new IEntitySelector() {
            public boolean isEntityApplicable(final Entity e) {
                return e instanceof EntitySpaceshipBase;
            }
        };
    }
    
    public enum EnumLaunchPhase
    {
        UNIGNITED, 
        IGNITED, 
        LAUNCHED;
    }
    
    public static class RocketLaunchEvent extends EntityEvent
    {
        public final EntitySpaceshipBase rocket;
        
        public RocketLaunchEvent(final EntitySpaceshipBase entity) {
            super((Entity)entity);
            this.rocket = entity;
        }
    }
}
