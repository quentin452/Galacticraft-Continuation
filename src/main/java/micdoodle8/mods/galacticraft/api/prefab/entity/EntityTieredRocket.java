package micdoodle8.mods.galacticraft.api.prefab.entity;

import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.galaxies.*;
import net.minecraft.entity.*;
import cpw.mods.fml.common.*;
import net.minecraft.server.*;
import io.netty.buffer.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.api.world.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraft.nbt.*;

public abstract class EntityTieredRocket extends EntityAutoRocket implements IRocketType, IDockable, IInventory, IWorldTransferCallback, ICameraZoomEntity
{
    public IRocketType.EnumRocketType rocketType;
    public float rumble;
    public int launchCooldown;
    private ArrayList<BlockVec3> preGenList;
    private Iterator<BlockVec3> preGenIterator;
    static boolean preGenInProgress;
    
    public EntityTieredRocket(final World par1World) {
        super(par1World);
        this.preGenList = new ArrayList<BlockVec3>();
        this.preGenIterator = null;
        this.setSize(0.98f, 4.0f);
        this.yOffset = this.height / 2.0f;
    }
    
    public EntityTieredRocket(final World world, final double posX, final double posY, final double posZ) {
        super(world, posX, posY, posZ);
        this.preGenList = new ArrayList<BlockVec3>();
        this.preGenIterator = null;
    }
    
    protected void entityInit() {
        super.entityInit();
    }
    
    public void setDead() {
        if (!this.isDead) {
            super.setDead();
        }
    }
    
    public void igniteCheckingCooldown() {
        if (!this.worldObj.isRemote && this.launchCooldown <= 0) {
            this.initiatePlanetsPreGen(this.chunkCoordX, this.chunkCoordZ);
            this.ignite();
        }
    }
    
    private void initiatePlanetsPreGen(final int cx, final int cz) {
        this.preGenList.clear();
        if (this.destinationFrequency == -1 && !EntityTieredRocket.preGenInProgress) {
            final ArrayList<Integer> toPreGen = new ArrayList<Integer>();
            for (final Planet planet : GalaxyRegistry.getRegisteredPlanets().values()) {
                if (planet.getDimensionID() == this.dimension) {
                    continue;
                }
                if (!planet.getReachable() || planet.getTierRequirement() > this.getRocketTier()) {
                    continue;
                }
                toPreGen.add(planet.getDimensionID());
            }
            if (toPreGen.size() > 0) {
                for (final Integer dimID : toPreGen) {
                    this.preGenList.add(new BlockVec3(cx, dimID, cz));
                    if (ConfigManagerCore.enableDebug) {
                        GCLog.info("Starting terrain pregen for dimension " + dimID + " at " + (cx * 16 + 8) + ", " + (cz * 16 + 8));
                    }
                }
                for (int r = 1; r < 12; ++r) {
                    final int xmin = cx - r;
                    final int xmax = cx + r;
                    final int zmin = cz - r;
                    final int zmax = cz + r;
                    for (int i = -r; i < r; ++i) {
                        for (final Integer dimID2 : toPreGen) {
                            this.preGenList.add(new BlockVec3(xmin, dimID2, cz + i));
                            this.preGenList.add(new BlockVec3(xmax, dimID2, cz - i));
                            this.preGenList.add(new BlockVec3(cx - i, dimID2, zmin));
                            this.preGenList.add(new BlockVec3(cx + i, dimID2, zmax));
                        }
                    }
                }
                this.preGenIterator = this.preGenList.iterator();
                EntityTieredRocket.preGenInProgress = true;
            }
        }
        else {
            this.preGenIterator = null;
        }
    }
    
    public void onUpdate() {
        if (this.getWaitForPlayer()) {
            if (this.riddenByEntity != null) {
                if (this.ticks >= 40L) {
                    if (!this.worldObj.isRemote) {
                        final Entity e = this.riddenByEntity;
                        e.mountEntity((Entity)null);
                        e.mountEntity((Entity)this);
                        if (ConfigManagerCore.enableDebug) {
                            GCLog.info("Remounting player in rocket.");
                        }
                    }
                    this.setWaitForPlayer(false);
                    this.motionY = -0.5;
                }
                else {
                    final double motionX = 0.0;
                    this.motionZ = motionX;
                    this.motionY = motionX;
                    this.motionX = motionX;
                    final Entity riddenByEntity = this.riddenByEntity;
                    final Entity riddenByEntity2 = this.riddenByEntity;
                    final Entity riddenByEntity3 = this.riddenByEntity;
                    final double motionX2 = 0.0;
                    riddenByEntity3.motionZ = motionX2;
                    riddenByEntity2.motionY = motionX2;
                    riddenByEntity.motionX = motionX2;
                }
            }
            else {
                final double motionX3 = 0.0;
                this.motionZ = motionX3;
                this.motionY = motionX3;
                this.motionX = motionX3;
            }
        }
        super.onUpdate();
        if (!this.worldObj.isRemote) {
            if (this.launchCooldown > 0) {
                --this.launchCooldown;
            }
            if (this.preGenIterator != null) {
                if (this.preGenIterator.hasNext()) {
                    final MinecraftServer mcserver = FMLCommonHandler.instance().getMinecraftServerInstance();
                    if (mcserver != null) {
                        BlockVec3 coords = this.preGenIterator.next();
                        World w = (World)mcserver.worldServerForDimension(coords.y);
                        if (w != null) {
                            w.getChunkFromChunkCoords(coords.x, coords.z);
                            if (this.launchPhase != EntitySpaceshipBase.EnumLaunchPhase.LAUNCHED.ordinal() && this.preGenIterator.hasNext()) {
                                coords = this.preGenIterator.next();
                                w = (World)mcserver.worldServerForDimension(coords.y);
                                w.getChunkFromChunkCoords(coords.x, coords.z);
                            }
                        }
                    }
                }
                else {
                    this.preGenIterator = null;
                    EntityTieredRocket.preGenInProgress = false;
                }
            }
        }
        if (this.rumble > 0.0f) {
            --this.rumble;
        }
        else if (this.rumble < 0.0f) {
            ++this.rumble;
        }
        if (this.riddenByEntity != null) {
            final double rumbleAmount = this.rumble / (double)(37 - 5 * Math.max(this.getRocketTier(), 5));
            final Entity riddenByEntity4 = this.riddenByEntity;
            riddenByEntity4.posX += rumbleAmount;
            final Entity riddenByEntity5 = this.riddenByEntity;
            riddenByEntity5.posZ += rumbleAmount;
        }
        if (this.launchPhase == EntitySpaceshipBase.EnumLaunchPhase.IGNITED.ordinal() || this.launchPhase == EntitySpaceshipBase.EnumLaunchPhase.LAUNCHED.ordinal()) {
            this.performHurtAnimation();
            this.rumble = this.rand.nextInt(3) - 3.0f;
        }
        if (!this.worldObj.isRemote) {
            this.lastLastMotionY = this.lastMotionY;
            this.lastMotionY = this.motionY;
        }
    }
    
    public void decodePacketdata(final ByteBuf buffer) {
        this.rocketType = IRocketType.EnumRocketType.values()[buffer.readInt()];
        super.decodePacketdata(buffer);
        if (buffer.readBoolean()) {
            this.posX = buffer.readDouble() / 8000.0;
            this.posY = buffer.readDouble() / 8000.0;
            this.posZ = buffer.readDouble() / 8000.0;
        }
    }
    
    public void getNetworkedData(final ArrayList<Object> list) {
        if (this.worldObj.isRemote) {
            return;
        }
        list.add((this.rocketType != null) ? this.rocketType.getIndex() : 0);
        super.getNetworkedData((ArrayList)list);
        final boolean sendPosUpdates = this.ticks < 25L || this.launchPhase != EntitySpaceshipBase.EnumLaunchPhase.LAUNCHED.ordinal() || this.landing;
        list.add(sendPosUpdates);
        if (sendPosUpdates) {
            list.add(this.posX * 8000.0);
            list.add(this.posY * 8000.0);
            list.add(this.posZ * 8000.0);
        }
    }
    
    public void onReachAtmosphere() {
        if (this.destinationFrequency != -1) {
            if (this.worldObj.isRemote) {
                this.stopRocketSound();
                return;
            }
            this.setTarget(true, this.destinationFrequency);
            if (this.targetVec == null) {
                GCLog.info("Error: the launch controlled rocket failed to find a valid landing spot when it reached space.");
                this.fuelTank.drain(Integer.MAX_VALUE, true);
                this.posY = Math.max(255.0, ((this.worldObj.provider instanceof IExitHeight) ? ((IExitHeight)this.worldObj.provider).getYCoordinateToTeleport() : 1200.0) - 200.0);
                return;
            }
            if (this.targetDimension == this.worldObj.provider.dimensionId) {
                this.setPosition((double)(this.targetVec.x + 0.5f), (double)(this.targetVec.y + 800), (double)(this.targetVec.z + 0.5f));
                final double n = 0.0;
                this.motionZ = n;
                this.motionX = n;
                this.motionY = 0.1;
                if (this.riddenByEntity != null) {
                    WorldUtil.forceMoveEntityToPos(this.riddenByEntity, (WorldServer)this.worldObj, new Vector3(this.targetVec.x + 0.5f, this.targetVec.y + 800, this.targetVec.z + 0.5f), false);
                    this.setWaitForPlayer(true);
                    if (ConfigManagerCore.enableDebug) {
                        GCLog.info("Rocket repositioned, waiting for player");
                    }
                }
                this.landing = true;
                return;
            }
            final WorldProvider targetDim = WorldUtil.getProviderForDimensionServer(this.targetDimension);
            if (targetDim != null && targetDim.worldObj instanceof WorldServer) {
                boolean dimensionAllowed = this.targetDimension == ConfigManagerCore.idDimensionOverworld;
                Label_0175: {
                    if (targetDim instanceof IGalacticraftWorldProvider) {
                        dimensionAllowed = ((IGalacticraftWorldProvider)targetDim).canSpaceshipTierPass(this.getRocketTier());
                    }
                    else {
                        if (this.targetDimension <= 1) {
                            if (this.targetDimension >= -1) {
                                break Label_0175;
                            }
                        }
                        try {
                            final Class<?> marsConfig = Class.forName("micdoodle8.mods.galacticraft.planets.mars.ConfigManagerMars");
                            if (marsConfig.getField("launchControllerAllDims").getBoolean(null)) {
                                dimensionAllowed = true;
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (dimensionAllowed) {
                    if (this.riddenByEntity != null) {
                        WorldUtil.transferEntityToDimension(this.riddenByEntity, this.targetDimension, (WorldServer)targetDim.worldObj, false, this);
                    }
                    else {
                        final Entity e2 = WorldUtil.transferEntityToDimension((Entity)this, this.targetDimension, (WorldServer)targetDim.worldObj, false, null);
                        if (e2 instanceof EntityAutoRocket) {
                            e2.setPosition((double)(this.targetVec.x + 0.5f), (double)(this.targetVec.y + 800), (double)(this.targetVec.z + 0.5f));
                            ((EntityAutoRocket)e2).landing = true;
                            ((EntityAutoRocket)e2).setWaitForPlayer(false);
                        }
                        else {
                            GCLog.info("Error: failed to recreate the unmanned rocket in landing mode on target planet.");
                            e2.setDead();
                            this.setDead();
                        }
                    }
                    return;
                }
            }
        }
        if (!this.worldObj.isRemote) {
            if (this.riddenByEntity instanceof EntityPlayerMP) {
                final EntityPlayerMP player = (EntityPlayerMP)this.riddenByEntity;
                this.onTeleport(player);
                final GCPlayerStats stats = GCPlayerStats.get(player);
                WorldUtil.toCelestialSelection(player, stats, this.getRocketTier());
            }
            this.setDead();
        }
    }
    
    protected boolean shouldCancelExplosion() {
        return this.hasValidFuel() && Math.abs(this.lastLastMotionY) < 4.0;
    }
    
    public void onTeleport(final EntityPlayerMP player) {
    }
    
    protected void onRocketLand(final int x, final int y, final int z) {
        super.onRocketLand(x, y, z);
        this.launchCooldown = 40;
    }
    
    public void onLaunch() {
        super.onLaunch();
    }
    
    protected boolean shouldMoveClientSide() {
        return true;
    }
    
    public boolean interactFirst(final EntityPlayer par1EntityPlayer) {
        if (this.launchPhase == EntitySpaceshipBase.EnumLaunchPhase.LAUNCHED.ordinal()) {
            return false;
        }
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayerMP) {
            if (!this.worldObj.isRemote && this.riddenByEntity == par1EntityPlayer) {
                GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_RESET_THIRD_PERSON, new Object[0]), (EntityPlayerMP)par1EntityPlayer);
                final GCPlayerStats stats = GCPlayerStats.get((EntityPlayerMP)par1EntityPlayer);
                stats.chatCooldown = 0;
                par1EntityPlayer.mountEntity((Entity)null);
            }
            return true;
        }
        if (par1EntityPlayer instanceof EntityPlayerMP) {
            if (!this.worldObj.isRemote) {
                GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_DISPLAY_ROCKET_CONTROLS, new Object[0]), (EntityPlayerMP)par1EntityPlayer);
                final GCPlayerStats stats = GCPlayerStats.get((EntityPlayerMP)par1EntityPlayer);
                stats.chatCooldown = 0;
                par1EntityPlayer.mountEntity((Entity)this);
            }
            return true;
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
            return 2;
        }
        return this.rocketType.getInventorySpace();
    }
    
    public void onWorldTransferred(final World world) {
        if (this.targetVec != null) {
            this.setPosition((double)(this.targetVec.x + 0.5f), (double)(this.targetVec.y + 800), (double)(this.targetVec.z + 0.5f));
            this.setWaitForPlayer(this.landing = true);
            final double motionX = 0.0;
            this.motionZ = motionX;
            this.motionY = motionX;
            this.motionX = motionX;
        }
        else {
            this.setDead();
        }
    }
    
    public void updateRiderPosition() {
        if (this.riddenByEntity != null) {
            this.riddenByEntity.setPosition(this.posX, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ);
        }
    }
    
    public float getRotateOffset() {
        return -1.5f;
    }
    
    public boolean isPlayerRocket() {
        return true;
    }
    
    static {
        EntityTieredRocket.preGenInProgress = false;
    }
}
