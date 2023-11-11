package micdoodle8.mods.galacticraft.api.prefab.entity;

import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.item.*;
import net.minecraft.server.gui.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraft.tileentity.*;
import cpw.mods.fml.common.*;
import net.minecraft.world.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.api.tile.*;
import micdoodle8.mods.galacticraft.core.client.sounds.*;
import io.netty.buffer.*;
import net.minecraftforge.fluids.*;
import cpw.mods.fml.common.network.*;
import cpw.mods.fml.client.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import micdoodle8.mods.galacticraft.api.world.*;
import micdoodle8.mods.galacticraft.core.event.*;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.eventhandler.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import java.util.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.entity.*;
import net.minecraft.client.audio.*;

public abstract class EntityAutoRocket extends EntitySpaceshipBase implements ILandable, IInventory, IEntityNoisy
{
    public int destinationFrequency;
    public BlockVec3 targetVec;
    public int targetDimension;
    protected ItemStack[] cargoItems;
    private IFuelDock landingPad;
    public boolean landing;
    public EnumAutoLaunch autoLaunchSetting;
    public int autoLaunchCountdown;
    private BlockVec3 activeLaunchController;
    public String statusMessage;
    public String statusColour;
    public int statusMessageCooldown;
    public int lastStatusMessageCooldown;
    public boolean statusValid;
    protected double lastMotionY;
    protected double lastLastMotionY;
    private boolean waitForPlayer;
    protected IUpdatePlayerListBox rocketSoundUpdater;
    private boolean rocketSoundToStop;
    private static Class<?> controllerClass;
    
    public EntityAutoRocket(final World world) {
        super(world);
        this.destinationFrequency = -1;
        this.rocketSoundToStop = false;
        this.yOffset = 0.0f;
        if (world != null && world.isRemote) {
            GalacticraftCore.packetPipeline.sendToServer(new PacketDynamic(this));
        }
    }
    
    public EntityAutoRocket(final World world, final double posX, final double posY, final double posZ) {
        this(world);
        this.setSize(0.98f, 2.0f);
        this.yOffset = 0.0f;
        this.setPosition(posX, posY, posZ);
        this.motionX = 0.0;
        this.motionY = 0.0;
        this.motionZ = 0.0;
        this.prevPosX = posX;
        this.prevPosY = posY;
        this.prevPosZ = posZ;
    }
    
    public boolean checkLaunchValidity() {
        this.statusMessageCooldown = 40;
        if (!this.hasValidFuel()) {
            this.destinationFrequency = -1;
            this.statusMessage = StatCollector.translateToLocal("gui.message.notEnough.name") + "#" + StatCollector.translateToLocal("gui.message.fuel.name");
            this.statusColour = "�c";
            return false;
        }
        if (this.launchPhase != EnumLaunchPhase.UNIGNITED.ordinal() || this.worldObj.isRemote) {
            this.destinationFrequency = -1;
            return false;
        }
        if (!this.setFrequency()) {
            this.destinationFrequency = -1;
            this.statusMessage = StatCollector.translateToLocal("gui.message.frequency.name") + "#" + StatCollector.translateToLocal("gui.message.notSet.name");
            this.statusColour = "�c";
            return false;
        }
        this.statusMessage = StatCollector.translateToLocal("gui.message.success.name");
        this.statusColour = "�a";
        return true;
    }
    
    public boolean setFrequency() {
        if (!GalacticraftCore.isPlanetsLoaded || EntityAutoRocket.controllerClass == null) {
            return false;
        }
        if (this.activeLaunchController != null) {
            final TileEntity launchController = this.activeLaunchController.getTileEntity((IBlockAccess)this.worldObj);
            if (EntityAutoRocket.controllerClass.isInstance(launchController)) {
                try {
                    final Boolean b = (Boolean)EntityAutoRocket.controllerClass.getMethod("validFrequency", (Class<?>[])new Class[0]).invoke(launchController, new Object[0]);
                    if (b != null && b) {
                        final int controllerFrequency = EntityAutoRocket.controllerClass.getField("destFrequency").getInt(launchController);
                        final boolean foundPad = this.setTarget(false, controllerFrequency);
                        if (foundPad) {
                            this.destinationFrequency = controllerFrequency;
                            GCLog.debug("Rocket under launch control: going to target frequency " + controllerFrequency);
                            return true;
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        this.destinationFrequency = -1;
        return false;
    }
    
    protected boolean setTarget(final boolean doSet, final int destFreq) {
        if (FMLCommonHandler.instance().getMinecraftServerInstance() == null || FMLCommonHandler.instance().getMinecraftServerInstance().worldServers == null || !GalacticraftCore.isPlanetsLoaded || EntityAutoRocket.controllerClass == null) {
            return false;
        }
        final WorldServer[] servers = FMLCommonHandler.instance().getMinecraftServerInstance().worldServers;
        for (int i = 0; i < servers.length; ++i) {
            final WorldServer world = servers[i];
            try {
                for (TileEntity tile : new ArrayList<TileEntity>(world.loadedTileEntityList)) {
                    if (!EntityAutoRocket.controllerClass.isInstance(tile)) {
                        continue;
                    }
                    tile = world.getTileEntity(tile.xCoord, tile.yCoord, tile.zCoord);
                    if (!EntityAutoRocket.controllerClass.isInstance(tile)) {
                        continue;
                    }
                    final int controllerFrequency = EntityAutoRocket.controllerClass.getField("frequency").getInt(tile);
                    if (destFreq != controllerFrequency) {
                        continue;
                    }
                    boolean targetSet = false;
                Label_0285:
                    for (int x = -2; x <= 2; ++x) {
                        for (int z = -2; z <= 2; ++z) {
                            final Block block = world.getBlock(tile.xCoord + x, tile.yCoord, tile.zCoord + z);
                            if (block instanceof BlockLandingPadFull) {
                                if (doSet) {
                                    this.targetVec = new BlockVec3(tile.xCoord + x, tile.yCoord, tile.zCoord + z);
                                }
                                targetSet = true;
                                break Label_0285;
                            }
                        }
                    }
                    if (doSet) {
                        this.targetDimension = tile.getWorldObj().provider.dimensionId;
                    }
                    if (!targetSet) {
                        if (doSet) {
                            this.targetVec = null;
                        }
                        return false;
                    }
                    return true;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    @Override
    public int getScaledFuelLevel(final int scale) {
        if (this.getFuelTankCapacity() <= 0) {
            return 0;
        }
        return this.fuelTank.getFluidAmount() * scale / this.getFuelTankCapacity() / ConfigManagerCore.rocketFuelFactor;
    }
    
    @Override
    public void onUpdate() {
        if (this.landing && this.launchPhase == EnumLaunchPhase.LAUNCHED.ordinal() && this.hasValidFuel() && this.targetVec != null) {
            final double yDiff = this.posY - this.getOnPadYOffset() - this.targetVec.y;
            this.motionY = Math.max(-2.0, (yDiff - 0.04) / -70.0);
            double diff = this.posX - this.targetVec.x - 0.5;
            double motX;
            if (diff > 0.0) {
                motX = Math.max(-0.1, diff / -100.0);
            }
            else if (diff < 0.0) {
                motX = Math.min(0.1, diff / -100.0);
            }
            else {
                motX = 0.0;
            }
            diff = this.posZ - this.targetVec.z - 0.5;
            double motZ;
            if (diff > 0.0) {
                motZ = Math.max(-0.1, diff / -100.0);
            }
            else if (diff < 0.0) {
                motZ = Math.min(0.1, diff / -100.0);
            }
            else {
                motZ = 0.0;
            }
            if (motZ != 0.0 || motX != 0.0) {
                final double angleYaw = Math.atan(motZ / motX);
                final double signed = (motX < 0.0) ? 50.0 : -50.0;
                final double anglePitch = Math.atan(Math.sqrt(motZ * motZ + motX * motX) / signed) * 100.0;
                this.rotationYaw = (float)angleYaw * 57.29578f;
                this.rotationPitch = (float)anglePitch * 57.29578f;
            }
            else {
                this.rotationPitch = 0.0f;
            }
            if (yDiff > 1.0 && yDiff < 4.0) {
                for (final Object o : this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)this, this.boundingBox.copy().offset(0.0, -3.0, 0.0), EntitySpaceshipBase.rocketSelector)) {
                    if (o instanceof EntitySpaceshipBase) {
                        ((EntitySpaceshipBase)o).dropShipAsItem();
                        ((EntitySpaceshipBase)o).setDead();
                    }
                }
            }
            if (yDiff < 0.04) {
                final int yMin = MathHelper.floor_double(this.boundingBox.minY - this.getOnPadYOffset() - 0.45) - 2;
                final int yMax = MathHelper.floor_double(this.boundingBox.maxY) + 1;
                final int zMin = MathHelper.floor_double(this.posZ) - 1;
                final int zMax = MathHelper.floor_double(this.posZ) + 1;
                for (int x = MathHelper.floor_double(this.posX) - 1; x <= MathHelper.floor_double(this.posX) + 1; ++x) {
                    for (int z = zMin; z <= zMax; ++z) {
                        for (int y = yMin; y <= yMax; ++y) {
                            if (this.worldObj.getTileEntity(x, y, z) instanceof IFuelDock) {
                                this.rotationPitch = 0.0f;
                                this.failRocket();
                            }
                        }
                    }
                }
            }
        }
        super.onUpdate();
        if (!this.worldObj.isRemote) {
            if (this.statusMessageCooldown > 0) {
                --this.statusMessageCooldown;
            }
            if (this.statusMessageCooldown == 0 && this.lastStatusMessageCooldown > 0 && this.statusValid) {
                this.autoLaunch();
            }
            if (this.autoLaunchCountdown > 0 && (!(this instanceof EntityTieredRocket) || this.riddenByEntity != null) && --this.autoLaunchCountdown <= 0) {
                this.autoLaunch();
            }
            if (this.autoLaunchSetting == EnumAutoLaunch.ROCKET_IS_FUELED && this.fuelTank.getFluidAmount() == this.fuelTank.getCapacity() && (!(this instanceof EntityTieredRocket) || this.riddenByEntity != null)) {
                this.autoLaunch();
            }
            if (this.autoLaunchSetting == EnumAutoLaunch.INSTANT && this.autoLaunchCountdown == 0 && (!(this instanceof EntityTieredRocket) || this.riddenByEntity != null)) {
                this.autoLaunch();
            }
            if (this.autoLaunchSetting == EnumAutoLaunch.REDSTONE_SIGNAL && this.ticks % 11L == 0L && this.activeLaunchController != null && this.worldObj.isBlockIndirectlyGettingPowered(this.activeLaunchController.x, this.activeLaunchController.y, this.activeLaunchController.z)) {
                this.autoLaunch();
            }
            if (this.launchPhase == EnumLaunchPhase.LAUNCHED.ordinal()) {
                this.setPad(null);
            }
            else if (this.launchPhase == EnumLaunchPhase.UNIGNITED.ordinal() && this.landingPad != null && this.ticks % 17L == 0L) {
                this.updateControllerSettings(this.landingPad);
            }
            this.lastStatusMessageCooldown = this.statusMessageCooldown;
        }
        if (this.launchPhase == EnumLaunchPhase.IGNITED.ordinal() || this.getLaunched()) {
            if (this.rocketSoundUpdater != null) {
                this.rocketSoundUpdater.update();
                this.rocketSoundToStop = true;
            }
        }
        else if (this.rocketSoundToStop) {
            this.stopRocketSound();
        }
    }
    
    @Override
    protected boolean shouldMoveClientSide() {
        return false;
    }
    
    private void autoLaunch() {
        if (this.autoLaunchSetting != null) {
            if (this.activeLaunchController != null) {
                final TileEntity tile = this.activeLaunchController.getTileEntity((IBlockAccess)this.worldObj);
                if (EntityAutoRocket.controllerClass.isInstance(tile)) {
                    Boolean autoLaunchEnabled = null;
                    try {
                        autoLaunchEnabled = EntityAutoRocket.controllerClass.getField("controlEnabled").getBoolean(tile);
                    }
                    catch (Exception ex) {}
                    if (autoLaunchEnabled != null && autoLaunchEnabled) {
                        if (this.fuelTank.getFluidAmount() > this.fuelTank.getCapacity() * 2 / 5) {
                            this.ignite();
                        }
                        else {
                            this.failMessageInsufficientFuel();
                        }
                    }
                    else {
                        this.failMessageLaunchController();
                    }
                }
            }
            this.autoLaunchSetting = null;
            return;
        }
        this.ignite();
    }
    
    public boolean igniteWithResult() {
        if (this.setFrequency()) {
            super.ignite();
            this.activeLaunchController = null;
            return true;
        }
        if (this.isPlayerRocket()) {
            super.ignite();
            this.activeLaunchController = null;
            return true;
        }
        this.activeLaunchController = null;
        return false;
    }
    
    @Override
    public void ignite() {
        this.igniteWithResult();
    }
    
    public abstract boolean isPlayerRocket();
    
    public void landEntity(final int x, final int y, final int z) {
        final TileEntity tile = this.worldObj.getTileEntity(x, y, z);
        if (tile instanceof IFuelDock) {
            final IFuelDock dock = (IFuelDock)tile;
            if (this.isDockValid(dock)) {
                if (!this.worldObj.isRemote) {
                    if (dock.getDockedEntity() instanceof EntitySpaceshipBase && dock.getDockedEntity() != this) {
                        ((EntitySpaceshipBase)dock.getDockedEntity()).dropShipAsItem();
                        ((EntitySpaceshipBase)dock.getDockedEntity()).setDead();
                    }
                    this.setPad(dock);
                }
                this.onRocketLand(x, y, z);
            }
        }
    }
    
    public void updateControllerSettings(final IFuelDock dock) {
        final HashSet<ILandingPadAttachable> connectedTiles = dock.getConnectedTiles();
        try {
            for (final ILandingPadAttachable updatedTile : connectedTiles) {
                if (EntityAutoRocket.controllerClass.isInstance(updatedTile)) {
                    final Boolean autoLaunchEnabled = EntityAutoRocket.controllerClass.getField("controlEnabled").getBoolean(updatedTile);
                    this.activeLaunchController = new BlockVec3((TileEntity)updatedTile);
                    if (autoLaunchEnabled) {
                        this.autoLaunchSetting = EnumAutoLaunch.values()[EntityAutoRocket.controllerClass.getField("launchDropdownSelection").getInt(updatedTile)];
                        switch (this.autoLaunchSetting) {
                            case INSTANT: {
                                if (this.autoLaunchCountdown <= 0 || this.autoLaunchCountdown > 12) {
                                    this.autoLaunchCountdown = 12;
                                    continue;
                                }
                                continue;
                            }
                            case TIME_10_SECONDS: {
                                if (this.autoLaunchCountdown <= 0 || this.autoLaunchCountdown > 200) {
                                    this.autoLaunchCountdown = 200;
                                    continue;
                                }
                                continue;
                            }
                            case TIME_30_SECONDS: {
                                if (this.autoLaunchCountdown <= 0 || this.autoLaunchCountdown > 600) {
                                    this.autoLaunchCountdown = 600;
                                    continue;
                                }
                                continue;
                            }
                            case TIME_1_MINUTE: {
                                if (this.autoLaunchCountdown <= 0 || this.autoLaunchCountdown > 1200) {
                                    this.autoLaunchCountdown = 1200;
                                    continue;
                                }
                                continue;
                            }
                            default: {
                                continue;
                            }
                        }
                    }
                    else {
                        this.autoLaunchSetting = null;
                        this.autoLaunchCountdown = 0;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected void onRocketLand(final int x, final int y, final int z) {
        this.setPositionAndRotation(x + 0.5, y + 0.4 + this.getOnPadYOffset(), z + 0.5, this.rotationYaw, 0.0f);
        this.stopRocketSound();
    }
    
    public void stopRocketSound() {
        if (this.rocketSoundUpdater != null) {
            ((SoundUpdaterRocket)this.rocketSoundUpdater).stopRocketSound();
        }
        this.rocketSoundToStop = false;
    }
    
    public void setDead() {
        super.setDead();
        if (this.rocketSoundUpdater != null) {
            this.rocketSoundUpdater.update();
        }
    }
    
    @Override
    public void decodePacketdata(final ByteBuf buffer) {
        super.decodePacketdata(buffer);
        this.fuelTank.setFluid(new FluidStack(GalacticraftCore.fluidFuel, buffer.readInt()));
        this.landing = buffer.readBoolean();
        this.destinationFrequency = buffer.readInt();
        if (buffer.readBoolean()) {
            this.targetVec = new BlockVec3(buffer.readInt(), buffer.readInt(), buffer.readInt());
        }
        this.motionX = buffer.readDouble() / 8000.0;
        this.motionY = buffer.readDouble() / 8000.0;
        this.motionZ = buffer.readDouble() / 8000.0;
        this.lastMotionY = buffer.readDouble() / 8000.0;
        this.lastLastMotionY = buffer.readDouble() / 8000.0;
        if (this.cargoItems == null) {
            this.cargoItems = new ItemStack[this.getSizeInventory()];
        }
        this.setWaitForPlayer(buffer.readBoolean());
        this.statusMessage = ByteBufUtils.readUTF8String(buffer);
        this.statusMessage = (this.statusMessage.equals("") ? null : this.statusMessage);
        this.statusMessageCooldown = buffer.readInt();
        this.lastStatusMessageCooldown = buffer.readInt();
        this.statusValid = buffer.readBoolean();
        if (this.worldObj.isRemote) {
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
        this.statusColour = ByteBufUtils.readUTF8String(buffer);
        if (this.statusColour.equals("")) {
            this.statusColour = null;
        }
    }
    
    public void handlePacketData(final Side side, final EntityPlayer player) {
    }
    
    @Override
    public void getNetworkedData(final ArrayList<Object> list) {
        if (this.worldObj.isRemote) {
            return;
        }
        super.getNetworkedData(list);
        list.add(this.fuelTank.getFluidAmount());
        list.add(this.landing);
        list.add(this.destinationFrequency);
        list.add(this.targetVec != null);
        if (this.targetVec != null) {
            list.add(this.targetVec.x);
            list.add(this.targetVec.y);
            list.add(this.targetVec.z);
        }
        list.add(this.motionX * 8000.0);
        list.add(this.motionY * 8000.0);
        list.add(this.motionZ * 8000.0);
        list.add(this.lastMotionY * 8000.0);
        list.add(this.lastLastMotionY * 8000.0);
        list.add(this.getWaitForPlayer());
        list.add((this.statusMessage != null) ? this.statusMessage : "");
        list.add(this.statusMessageCooldown);
        list.add(this.lastStatusMessageCooldown);
        list.add(this.statusValid);
        if (!this.worldObj.isRemote) {
            list.add((this.riddenByEntity == null) ? -1 : this.riddenByEntity.getEntityId());
        }
        list.add((this.statusColour != null) ? this.statusColour : "");
    }
    
    @Override
    protected void failRocket() {
        if (this.shouldCancelExplosion()) {
            for (int i = -3; i <= 3; ++i) {
                if (this.landing && this.targetVec != null && this.worldObj.getTileEntity((int)Math.floor(this.posX), (int)Math.floor(this.posY + i), (int)Math.floor(this.posZ)) instanceof IFuelDock && this.posY - this.targetVec.y < 5.0) {
                    for (int x = MathHelper.floor_double(this.posX) - 1; x <= MathHelper.floor_double(this.posX) + 1; ++x) {
                        for (int y = MathHelper.floor_double(this.posY - 3.0); y <= MathHelper.floor_double(this.posY) + 1; ++y) {
                            for (int z = MathHelper.floor_double(this.posZ) - 1; z <= MathHelper.floor_double(this.posZ) + 1; ++z) {
                                final TileEntity tile = this.worldObj.getTileEntity(x, y, z);
                                if (tile instanceof IFuelDock) {
                                    this.landEntity(x, y, z);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (this.launchPhase == EnumLaunchPhase.LAUNCHED.ordinal()) {
            super.failRocket();
        }
    }
    
    protected boolean shouldCancelExplosion() {
        return this.hasValidFuel();
    }
    
    public boolean hasValidFuel() {
        return this.fuelTank.getFluidAmount() > 0;
    }
    
    public void cancelLaunch() {
        this.setLaunchPhase(EnumLaunchPhase.UNIGNITED);
        this.timeUntilLaunch = 0;
        if (!this.worldObj.isRemote && this.riddenByEntity instanceof EntityPlayerMP) {
            ((EntityPlayerMP)this.riddenByEntity).addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.rocket.warning.nogyroscope")));
        }
    }
    
    public void failMessageLaunchController() {
        if (!this.worldObj.isRemote && this.riddenByEntity instanceof EntityPlayerMP) {
            ((EntityPlayerMP)this.riddenByEntity).addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.rocket.warning.launchcontroller")));
        }
    }
    
    public void failMessageInsufficientFuel() {
        if (!this.worldObj.isRemote && this.riddenByEntity instanceof EntityPlayerMP) {
            ((EntityPlayerMP)this.riddenByEntity).addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.rocket.warning.fuelinsufficient")));
        }
    }
    
    @Override
    public void onLaunch() {
        if (this.worldObj.provider.dimensionId != GalacticraftCore.planetOverworld.getDimensionID() && !(this.worldObj.provider instanceof IGalacticraftWorldProvider)) {
            if (ConfigManagerCore.disableRocketLaunchAllNonGC) {
                this.cancelLaunch();
                return;
            }
            for (int i = ConfigManagerCore.disableRocketLaunchDimensions.length - 1; i >= 0; --i) {
                if (ConfigManagerCore.disableRocketLaunchDimensions[i] == this.worldObj.provider.dimensionId) {
                    this.cancelLaunch();
                    return;
                }
            }
        }
        super.onLaunch();
        if (!this.worldObj.isRemote) {
            GCPlayerStats stats = null;
            if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayerMP) {
                stats = GCPlayerStats.get((EntityPlayerMP)this.riddenByEntity);
                if (!(this.worldObj.provider instanceof IOrbitDimension)) {
                    stats.coordsTeleportedFromX = this.riddenByEntity.posX;
                    stats.coordsTeleportedFromZ = this.riddenByEntity.posZ;
                }
            }
            int amountRemoved = 0;
        Label_0342:
            for (int x = MathHelper.floor_double(this.posX) - 1; x <= MathHelper.floor_double(this.posX) + 1; ++x) {
                for (int y = MathHelper.floor_double(this.posY) - 3; y <= MathHelper.floor_double(this.posY) + 1; ++y) {
                    int z = MathHelper.floor_double(this.posZ) - 1;
                    while (z <= MathHelper.floor_double(this.posZ) + 1) {
                        final Block block = this.worldObj.getBlock(x, y, z);
                        if (block != null && block instanceof BlockLandingPadFull && amountRemoved < 9) {
                            final EventLandingPadRemoval event = new EventLandingPadRemoval(this.worldObj, x, y, z);
                            MinecraftForge.EVENT_BUS.post((Event)event);
                            if (event.allow) {
                                this.worldObj.setBlockToAir(x, y, z);
                                amountRemoved = 9;
                                break Label_0342;
                            }
                            break Label_0342;
                        }
                        else {
                            ++z;
                        }
                    }
                }
            }
            if (stats != null) {
                stats.launchpadStack = ((amountRemoved == 9) ? new ItemStack(GCBlocks.landingPad, 9, 0) : null);
            }
            this.playSound("random.pop", 0.2f, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
        }
    }
    
    @Override
    protected void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        if (this.fuelTank.getFluid() != null) {
            nbt.setTag("fuelTank", (NBTBase)this.fuelTank.writeToNBT(new NBTTagCompound()));
        }
        if (this.getSizeInventory() > 0) {
            final NBTTagList var2 = new NBTTagList();
            for (int var3 = 0; var3 < this.cargoItems.length; ++var3) {
                if (this.cargoItems[var3] != null) {
                    final NBTTagCompound var4 = new NBTTagCompound();
                    var4.setByte("Slot", (byte)var3);
                    this.cargoItems[var3].writeToNBT(var4);
                    var2.appendTag((NBTBase)var4);
                }
            }
            nbt.setTag("Items", (NBTBase)var2);
        }
        nbt.setBoolean("TargetValid", this.targetVec != null);
        if (this.targetVec != null) {
            nbt.setDouble("targetTileX", (double)this.targetVec.x);
            nbt.setDouble("targetTileY", (double)this.targetVec.y);
            nbt.setDouble("targetTileZ", (double)this.targetVec.z);
        }
        nbt.setBoolean("WaitingForPlayer", this.getWaitForPlayer());
        nbt.setBoolean("Landing", this.landing);
        nbt.setInteger("AutoLaunchSetting", (this.autoLaunchSetting != null) ? this.autoLaunchSetting.getIndex() : -1);
        nbt.setInteger("TimeUntilAutoLaunch", this.autoLaunchCountdown);
        nbt.setInteger("DestinationFrequency", this.destinationFrequency);
        if (this.activeLaunchController != null) {
            this.activeLaunchController.writeToNBT(nbt, "ALCat");
        }
    }
    
    @Override
    protected void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("fuelTank")) {
            this.fuelTank.readFromNBT(nbt.getCompoundTag("fuelTank"));
        }
        if (this.getSizeInventory() > 0) {
            final NBTTagList var2 = nbt.getTagList("Items", 10);
            this.cargoItems = new ItemStack[this.getSizeInventory()];
            for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
                final NBTTagCompound var4 = var2.getCompoundTagAt(var3);
                final int var5 = var4.getByte("Slot") & 0xFF;
                if (var5 < this.cargoItems.length) {
                    this.cargoItems[var5] = ItemStack.loadItemStackFromNBT(var4);
                }
            }
        }
        if (nbt.getBoolean("TargetValid") && nbt.hasKey("targetTileX")) {
            this.targetVec = new BlockVec3(MathHelper.floor_double(nbt.getDouble("targetTileX")), MathHelper.floor_double(nbt.getDouble("targetTileY")), MathHelper.floor_double(nbt.getDouble("targetTileZ")));
        }
        this.setWaitForPlayer(nbt.getBoolean("WaitingForPlayer"));
        this.landing = nbt.getBoolean("Landing");
        final int autoLaunchValue = nbt.getInteger("AutoLaunchSetting");
        this.autoLaunchSetting = ((autoLaunchValue == -1) ? null : EnumAutoLaunch.values()[autoLaunchValue]);
        this.autoLaunchCountdown = nbt.getInteger("TimeUntilAutoLaunch");
        this.destinationFrequency = nbt.getInteger("DestinationFrequency");
        this.activeLaunchController = BlockVec3.readFromNBT(nbt, "ALCat");
    }
    
    public int addFuel(final FluidStack liquid, final boolean doFill) {
        return FluidUtil.fillWithGCFuel(this.fuelTank, liquid, doFill);
    }
    
    public FluidStack removeFuel(final int amount) {
        return this.fuelTank.drain(amount * ConfigManagerCore.rocketFuelFactor, true);
    }
    
    public void setPad(final IFuelDock pad) {
        this.landingPad = pad;
        if (pad != null) {
            pad.dockEntity((IDockable)this);
            if (this.launchPhase != EnumLaunchPhase.IGNITED.ordinal()) {
                this.setLaunchPhase(EnumLaunchPhase.UNIGNITED);
                this.targetVec = null;
                if (GalacticraftCore.isPlanetsLoaded) {
                    this.updateControllerSettings(pad);
                }
            }
            this.landing = false;
        }
    }
    
    public IFuelDock getLandingPad() {
        return this.landingPad;
    }
    
    @Override
    public int getMaxFuel() {
        return this.fuelTank.getCapacity();
    }
    
    public boolean isDockValid(final IFuelDock dock) {
        return dock instanceof TileEntityLandingPad;
    }
    
    public ICargoEntity.EnumCargoLoadingState addCargo(final ItemStack stack, final boolean doAdd) {
        if (this.getSizeInventory() <= 3) {
            if (this.autoLaunchSetting == EnumAutoLaunch.CARGO_IS_FULL) {
                this.autoLaunch();
            }
            return ICargoEntity.EnumCargoLoadingState.NOINVENTORY;
        }
        int count = 0;
        count = 0;
        while (count < this.cargoItems.length - 2) {
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
                if (this.autoLaunchSetting == EnumAutoLaunch.CARGO_IS_FULL) {
                    this.autoLaunch();
                }
                return ICargoEntity.EnumCargoLoadingState.FULL;
            }
            else {
                ++count;
            }
        }
        for (count = 0; count < this.cargoItems.length - 2; ++count) {
            final ItemStack stackAt = this.cargoItems[count];
            if (stackAt == null) {
                if (doAdd) {
                    this.cargoItems[count] = stack;
                    this.markDirty();
                }
                return ICargoEntity.EnumCargoLoadingState.SUCCESS;
            }
        }
        if (this.autoLaunchSetting == EnumAutoLaunch.CARGO_IS_FULL) {
            this.autoLaunch();
        }
        return ICargoEntity.EnumCargoLoadingState.FULL;
    }
    
    public ICargoEntity.RemovalResult removeCargo(final boolean doRemove) {
        for (int i = 0; i < this.cargoItems.length - 2; ++i) {
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
        if (this.autoLaunchSetting == EnumAutoLaunch.CARGO_IS_UNLOADED) {
            this.autoLaunch();
        }
        return new ICargoEntity.RemovalResult(ICargoEntity.EnumCargoLoadingState.EMPTY, (ItemStack)null);
    }
    
    public ItemStack getStackInSlot(final int par1) {
        if (this.cargoItems == null) {
            return null;
        }
        return this.cargoItems[par1];
    }
    
    public ItemStack decrStackSize(final int par1, final int par2) {
        if (this.cargoItems[par1] == null) {
            return null;
        }
        if (this.cargoItems[par1].stackSize <= par2) {
            final ItemStack var3 = this.cargoItems[par1];
            this.cargoItems[par1] = null;
            return var3;
        }
        final ItemStack var3 = this.cargoItems[par1].splitStack(par2);
        if (this.cargoItems[par1].stackSize == 0) {
            this.cargoItems[par1] = null;
        }
        return var3;
    }
    
    public ItemStack getStackInSlotOnClosing(final int par1) {
        if (this.cargoItems[par1] != null) {
            final ItemStack var2 = this.cargoItems[par1];
            this.cargoItems[par1] = null;
            return var2;
        }
        return null;
    }
    
    public void setInventorySlotContents(final int par1, final ItemStack par2ItemStack) {
        this.cargoItems[par1] = par2ItemStack;
        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }
    
    public String getInventoryName() {
        return GCCoreUtil.translate("container.spaceship.name");
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public void openInventory() {
    }
    
    public void closeInventory() {
    }
    
    public boolean hasCustomInventoryName() {
        return true;
    }
    
    public boolean isUseableByPlayer(final EntityPlayer entityplayer) {
        return !this.isDead && entityplayer.getDistanceSqToEntity((Entity)this) <= 64.0;
    }
    
    public boolean isItemValidForSlot(final int i, final ItemStack itemstack) {
        return false;
    }
    
    public void markDirty() {
    }
    
    public void onPadDestroyed() {
        if (!this.isDead && this.launchPhase != EnumLaunchPhase.LAUNCHED.ordinal()) {
            this.dropShipAsItem();
            this.setDead();
        }
    }
    
    @Override
    public List<ItemStack> getItemsDropped(final List<ItemStack> droppedItemList) {
        if (this.cargoItems != null) {
            for (final ItemStack item : this.cargoItems) {
                if (item != null) {
                    droppedItemList.add(item);
                }
            }
        }
        return droppedItemList;
    }
    
    public boolean getWaitForPlayer() {
        return this.waitForPlayer;
    }
    
    public void setWaitForPlayer(final boolean waitForPlayer) {
        this.waitForPlayer = waitForPlayer;
    }
    
    @SideOnly(Side.CLIENT)
    public IUpdatePlayerListBox getSoundUpdater() {
        return this.rocketSoundUpdater;
    }
    
    @SideOnly(Side.CLIENT)
    public ISound setSoundUpdater(final EntityPlayerSP player) {
        this.rocketSoundUpdater = (IUpdatePlayerListBox)new SoundUpdaterRocket(player, this);
        return (ISound)this.rocketSoundUpdater;
    }
    
    static {
        EntityAutoRocket.controllerClass = null;
        try {
            EntityAutoRocket.controllerClass = Class.forName("micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityLaunchController");
        }
        catch (ClassNotFoundException e) {
            GCLog.info("Galacticraft-Planets' LaunchController not present, rockets will not be launch controlled.");
        }
    }
    
    public enum EnumAutoLaunch
    {
        CARGO_IS_UNLOADED(0, "cargoUnloaded"), 
        CARGO_IS_FULL(1, "cargoFull"), 
        ROCKET_IS_FUELED(2, "fullyFueled"), 
        INSTANT(3, "instant"), 
        TIME_10_SECONDS(4, "tenSec"), 
        TIME_30_SECONDS(5, "thirtySec"), 
        TIME_1_MINUTE(6, "oneMin"), 
        REDSTONE_SIGNAL(7, "redstoneSig");
        
        private final int index;
        private String title;
        
        private EnumAutoLaunch(final int index, final String title) {
            this.index = index;
            this.title = title;
        }
        
        public int getIndex() {
            return this.index;
        }
        
        public String getTitle() {
            return GCCoreUtil.translate("gui.message." + this.title + ".name");
        }
    }
}
