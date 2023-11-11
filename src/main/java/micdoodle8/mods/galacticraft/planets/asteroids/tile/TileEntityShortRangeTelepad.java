package micdoodle8.mods.galacticraft.planets.asteroids.tile;

import micdoodle8.mods.galacticraft.core.energy.tile.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.miccore.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.planets.asteroids.network.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import net.minecraft.tileentity.*;
import net.minecraft.nbt.*;
import io.netty.buffer.*;
import micdoodle8.mods.galacticraft.planets.*;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.core.energy.item.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;

public class TileEntityShortRangeTelepad extends TileBaseElectricBlock implements IMultiBlock, IInventory, ISidedInventory
{
    public static final int MAX_TELEPORT_TIME = 150;
    public static final int TELEPORTER_RANGE = 256;
    public static final int ENERGY_USE_ON_TELEPORT = 2500;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int address;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean addressValid;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int targetAddress;
    public EnumTelepadSearchResult targetAddressResult;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int teleportTime;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public String owner;
    private ItemStack[] containingItems;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean teleporting;
    
    public TileEntityShortRangeTelepad() {
        this.address = -1;
        this.addressValid = false;
        this.targetAddress = -1;
        this.targetAddressResult = EnumTelepadSearchResult.NOT_FOUND;
        this.teleportTime = 0;
        this.owner = "";
        this.containingItems = new ItemStack[1];
        this.storage.setMaxExtract(ConfigManagerCore.hardMode ? 115.0f : 50.0f);
    }
    
    public int canTeleportHere() {
        if (this.worldObj.isRemote) {
            return -1;
        }
        this.setAddress(this.address);
        this.setTargetAddress(this.targetAddress);
        if (!this.addressValid) {
            return 1;
        }
        if (this.getEnergyStoredGC() < 2500.0f) {
            return 2;
        }
        return 0;
    }
    
    public void updateEntity() {
        if (this.ticks % 40 == 0 && !this.worldObj.isRemote) {
            this.setAddress(this.address);
            this.setTargetAddress(this.targetAddress);
        }
        if (!this.worldObj.isRemote) {
            if (this.targetAddressResult == EnumTelepadSearchResult.VALID && (this.ticks % 5 == 0 || this.teleporting)) {
                final List<EntityLivingBase> containedEntities = (List<EntityLivingBase>)this.worldObj.getEntitiesWithinAABB((Class)EntityLivingBase.class, AxisAlignedBB.getBoundingBox((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, (double)(this.xCoord + 1), (double)(this.yCoord + 2), (double)(this.zCoord + 1)));
                if (containedEntities.size() > 0 && this.getEnergyStoredGC() >= 2500.0f) {
                    final ShortRangeTelepadHandler.TelepadEntry entry = ShortRangeTelepadHandler.getLocationFromAddress(this.targetAddress);
                    if (entry != null) {
                        this.teleporting = true;
                    }
                }
                else {
                    this.teleporting = false;
                }
            }
            if (this.teleporting) {
                ++this.teleportTime;
                if (this.teleportTime >= 150) {
                    final ShortRangeTelepadHandler.TelepadEntry entry2 = ShortRangeTelepadHandler.getLocationFromAddress(this.targetAddress);
                    final BlockVec3 finalPos = (entry2 == null) ? null : entry2.position;
                    if (finalPos != null) {
                        final TileEntity tileAt = finalPos.getTileEntityForce(this.worldObj);
                        final List<EntityLivingBase> containedEntities2 = (List<EntityLivingBase>)this.worldObj.getEntitiesWithinAABB((Class)EntityLivingBase.class, AxisAlignedBB.getBoundingBox((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, (double)(this.xCoord + 1), (double)(this.yCoord + 2), (double)(this.zCoord + 1)));
                        if (tileAt != null && tileAt instanceof TileEntityShortRangeTelepad) {
                            final TileEntityShortRangeTelepad destTelepad = (TileEntityShortRangeTelepad)tileAt;
                            final int teleportResult = destTelepad.canTeleportHere();
                            if (teleportResult == 0) {
                                for (final EntityLivingBase e : containedEntities2) {
                                    e.setPosition((double)(finalPos.x + 0.5f), (double)(finalPos.y + 1.0f), (double)(finalPos.z + 0.5f));
                                    this.worldObj.updateEntityWithOptionalForce((Entity)e, true);
                                    if (e instanceof EntityPlayerMP) {
                                        ((EntityPlayerMP)e).playerNetServerHandler.setPlayerLocation((double)finalPos.x, (double)finalPos.y, (double)finalPos.z, e.rotationYaw, e.rotationPitch);
                                    }
                                    GalacticraftCore.packetPipeline.sendToDimension((IPacket)new PacketSimpleAsteroids(PacketSimpleAsteroids.EnumSimplePacketAsteroids.C_TELEPAD_SEND, new Object[] { finalPos, e.getEntityId() }), this.worldObj.provider.dimensionId);
                                }
                                if (containedEntities2.size() > 0) {
                                    this.storage.setEnergyStored(this.storage.getEnergyStoredGC() - 2500.0f);
                                    destTelepad.storage.setEnergyStored(this.storage.getEnergyStoredGC() - 2500.0f);
                                }
                            }
                            else {
                                switch (teleportResult) {
                                    case -1: {
                                        for (final EntityLivingBase e : containedEntities2) {
                                            if (e instanceof EntityPlayer) {
                                                ((EntityPlayer)e).addChatComponentMessage((IChatComponent)new ChatComponentText("Cannot Send client-side"));
                                            }
                                        }
                                        break;
                                    }
                                    case 1: {
                                        for (final EntityLivingBase e : containedEntities2) {
                                            if (e instanceof EntityPlayer) {
                                                ((EntityPlayer)e).addChatComponentMessage((IChatComponent)new ChatComponentText("Target address invalid"));
                                            }
                                        }
                                        break;
                                    }
                                    case 2: {
                                        for (final EntityLivingBase e : containedEntities2) {
                                            if (e instanceof EntityPlayer) {
                                                ((EntityPlayer)e).addChatComponentMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.message.targetNoEnergy.name")));
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    this.teleportTime = 0;
                    this.teleporting = false;
                }
            }
            else {
                final int teleportTime = this.teleportTime - 1;
                this.teleportTime = teleportTime;
                this.teleportTime = Math.max(teleportTime, 0);
            }
        }
        super.updateEntity();
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        final NBTTagList var2 = nbt.getTagList("Items", 10);
        this.containingItems = new ItemStack[this.getSizeInventory()];
        for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
            final NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            final int var5 = var4.getByte("Slot") & 0xFF;
            if (var5 < this.containingItems.length) {
                this.containingItems[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }
        this.setAddress(nbt.getInteger("Address"));
        this.targetAddress = nbt.getInteger("TargetAddress");
        this.owner = nbt.getString("Owner");
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        final NBTTagList var2 = new NBTTagList();
        for (int var3 = 0; var3 < this.containingItems.length; ++var3) {
            if (this.containingItems[var3] != null) {
                final NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.containingItems[var3].writeToNBT(var4);
                var2.appendTag((NBTBase)var4);
            }
        }
        nbt.setTag("Items", (NBTBase)var2);
        nbt.setInteger("TargetAddress", this.targetAddress);
        nbt.setInteger("Address", this.address);
        nbt.setString("Owner", this.owner);
    }
    
    public void addExtraNetworkedData(final List<Object> networkedList) {
        super.addExtraNetworkedData((List)networkedList);
        networkedList.add(this.targetAddressResult.ordinal());
    }
    
    public void readExtraNetworkedData(final ByteBuf dataStream) {
        super.readExtraNetworkedData(dataStream);
        this.targetAddressResult = EnumTelepadSearchResult.values()[dataStream.readInt()];
    }
    
    public double getPacketRange() {
        return 24.0;
    }
    
    public boolean onActivated(final EntityPlayer entityPlayer) {
        entityPlayer.openGui((Object)GalacticraftPlanets.instance, 3, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        return true;
    }
    
    public void onCreate(final BlockVec3 placedPosition) {
        final int buildHeight = this.worldObj.getHeight() - 1;
        for (int y = 0; y < 3; y += 2) {
            if (placedPosition.y + y > buildHeight) {
                return;
            }
            for (int x = -1; x <= 1; ++x) {
                for (int z = -1; z <= 1; ++z) {
                    final BlockVec3 vecToAdd = new BlockVec3(placedPosition.x + x, placedPosition.y + y, placedPosition.z + z);
                    if (!vecToAdd.equals((Object)placedPosition)) {
                        ((BlockTelepadFake)AsteroidBlocks.fakeTelepad).makeFakeBlock(this.worldObj, vecToAdd, placedPosition, (int)((y == 0) ? 1 : 0));
                    }
                }
            }
        }
    }
    
    public void onDestroy(final TileEntity callingBlock) {
        for (int y = 0; y < 3; y += 2) {
            for (int x = -1; x <= 1; ++x) {
                for (int z = -1; z <= 1; ++z) {
                    this.worldObj.func_147480_a(this.xCoord + x, this.yCoord + y, this.zCoord + z, y == 0 && x == 0 && z == 0);
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox((double)(this.xCoord - 1), (double)this.yCoord, (double)(this.zCoord - 1), (double)(this.xCoord + 2), (double)(this.yCoord + 4), (double)(this.zCoord + 2));
    }
    
    public String getInventoryName() {
        return GCCoreUtil.translate("container.shortRangeTelepad.name");
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public boolean isUseableByPlayer(final EntityPlayer par1EntityPlayer) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && par1EntityPlayer.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5) <= 64.0;
    }
    
    public boolean hasCustomInventoryName() {
        return true;
    }
    
    public boolean isItemValidForSlot(final int slotID, final ItemStack itemStack) {
        return slotID == 0 && ItemElectricBase.isElectricItem(itemStack.getItem());
    }
    
    public int[] getAccessibleSlotsFromSide(final int side) {
        return new int[] { 0 };
    }
    
    public boolean canInsertItem(final int slotID, final ItemStack par2ItemStack, final int par3) {
        return this.isItemValidForSlot(slotID, par2ItemStack);
    }
    
    public boolean canExtractItem(final int slotID, final ItemStack par2ItemStack, final int par3) {
        return slotID == 0;
    }
    
    public EnumSet<ForgeDirection> getElectricalOutputDirections() {
        return EnumSet.noneOf(ForgeDirection.class);
    }
    
    public boolean shouldUseEnergy() {
        return !this.getDisabled(0);
    }
    
    public ForgeDirection getElectricInputDirection() {
        return ForgeDirection.getOrientation((this.getBlockMetadata() & 0x3) + 2);
    }
    
    public ItemStack getBatteryInSlot() {
        return this.getStackInSlot(0);
    }
    
    public void setDisabled(final int index, final boolean disabled) {
        if (this.disableCooldown == 0) {
            switch (index) {
                case 0: {
                    this.disabled = disabled;
                    this.disableCooldown = 10;
                    break;
                }
            }
        }
    }
    
    public boolean getDisabled(final int index) {
        switch (index) {
            case 0: {
                return this.disabled;
            }
            default: {
                return true;
            }
        }
    }
    
    public void setAddress(final int address) {
        if (this.worldObj != null && address != this.address) {
            ShortRangeTelepadHandler.removeShortRangeTeleporter(this);
        }
        this.address = address;
        if (this.address >= 0) {
            final ShortRangeTelepadHandler.TelepadEntry entry = ShortRangeTelepadHandler.getLocationFromAddress(this.address);
            this.addressValid = (entry == null || (this.worldObj != null && entry.dimensionID == this.worldObj.provider.dimensionId && entry.position.x == this.xCoord && entry.position.y == this.yCoord && entry.position.z == this.zCoord));
        }
        else {
            this.addressValid = false;
        }
        if (this.worldObj != null && !this.worldObj.isRemote) {
            ShortRangeTelepadHandler.addShortRangeTelepad(this);
        }
    }
    
    public boolean updateTarget() {
        if (this.targetAddress < 0 || this.worldObj.isRemote) {
            this.targetAddressResult = EnumTelepadSearchResult.NOT_FOUND;
            return false;
        }
        this.targetAddressResult = EnumTelepadSearchResult.NOT_FOUND;
        final ShortRangeTelepadHandler.TelepadEntry addressResult = ShortRangeTelepadHandler.getLocationFromAddress(this.targetAddress);
        if (addressResult == null) {
            this.targetAddressResult = EnumTelepadSearchResult.NOT_FOUND;
            return false;
        }
        if (this.worldObj.provider.dimensionId != addressResult.dimensionID) {
            this.targetAddressResult = EnumTelepadSearchResult.WRONG_DIM;
            return false;
        }
        final double distance = this.getDistanceFrom((double)(addressResult.position.x + 0.5f), (double)(addressResult.position.y + 0.5f), (double)(addressResult.position.z + 0.5f));
        if (distance < 65536.0) {
            this.targetAddressResult = EnumTelepadSearchResult.VALID;
            return true;
        }
        this.targetAddressResult = EnumTelepadSearchResult.TOO_FAR;
        return false;
    }
    
    public void setTargetAddress(final int address) {
        this.targetAddress = address;
        this.updateTarget();
    }
    
    public void openInventory() {
    }
    
    public void closeInventory() {
    }
    
    public int getSizeInventory() {
        return this.containingItems.length;
    }
    
    public ItemStack getStackInSlot(final int par1) {
        return this.containingItems[par1];
    }
    
    public ItemStack decrStackSize(final int par1, final int par2) {
        if (this.containingItems[par1] == null) {
            return null;
        }
        if (this.containingItems[par1].stackSize <= par2) {
            final ItemStack var3 = this.containingItems[par1];
            this.containingItems[par1] = null;
            return var3;
        }
        final ItemStack var3 = this.containingItems[par1].splitStack(par2);
        if (this.containingItems[par1].stackSize == 0) {
            this.containingItems[par1] = null;
        }
        return var3;
    }
    
    public ItemStack getStackInSlotOnClosing(final int par1) {
        if (this.containingItems[par1] != null) {
            final ItemStack var2 = this.containingItems[par1];
            this.containingItems[par1] = null;
            return var2;
        }
        return null;
    }
    
    public void setInventorySlotContents(final int par1, final ItemStack par2ItemStack) {
        this.containingItems[par1] = par2ItemStack;
        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }
    
    public void setOwner(final String owner) {
        this.owner = owner;
    }
    
    public String getOwner() {
        return this.owner;
    }
    
    @SideOnly(Side.CLIENT)
    public String getReceivingStatus() {
        if (!this.addressValid) {
            return EnumColor.RED + GCCoreUtil.translate("gui.message.invalidAddress.name");
        }
        if (this.getEnergyStoredGC() <= 0.0f) {
            return EnumColor.RED + GCCoreUtil.translate("gui.message.noEnergy.name");
        }
        if (this.getEnergyStoredGC() <= 2500.0f) {
            return EnumColor.RED + GCCoreUtil.translate("gui.message.notEnoughEnergy.name");
        }
        if (this.getDisabled(0)) {
            return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.disabled.name");
        }
        return EnumColor.BRIGHT_GREEN + GCCoreUtil.translate("gui.message.receivingActive.name");
    }
    
    @SideOnly(Side.CLIENT)
    public String getSendingStatus() {
        if (!this.addressValid) {
            return EnumColor.RED + GCCoreUtil.translate("gui.message.invalidTargetAddress.name");
        }
        if (this.targetAddressResult == EnumTelepadSearchResult.TOO_FAR) {
            return EnumColor.RED + GCCoreUtil.translateWithFormat("gui.message.telepadTooFar.name", 256);
        }
        if (this.targetAddressResult == EnumTelepadSearchResult.WRONG_DIM) {
            return EnumColor.RED + GCCoreUtil.translate("gui.message.telepadWrongDim.name");
        }
        if (this.targetAddressResult == EnumTelepadSearchResult.NOT_FOUND) {
            return EnumColor.RED + GCCoreUtil.translate("gui.message.telepadNotFound.name");
        }
        if (this.getEnergyStoredGC() <= 0.0f) {
            return EnumColor.RED + GCCoreUtil.translate("gui.message.noEnergy.name");
        }
        if (this.getEnergyStoredGC() <= 2500.0f) {
            return EnumColor.RED + GCCoreUtil.translate("gui.message.notEnoughEnergy.name");
        }
        if (this.getDisabled(0)) {
            return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.disabled.name");
        }
        return EnumColor.BRIGHT_GREEN + GCCoreUtil.translate("gui.message.sendingActive.name");
    }
    
    @SideOnly(Side.CLIENT)
    public Vector3 getParticleColor(final Random rand, final boolean sending) {
        final float teleportTimeScaled = Math.min(1.0f, this.teleportTime / 150.0f);
        final float f = rand.nextFloat() * 0.6f + 0.4f;
        if (sending && this.targetAddressResult != EnumTelepadSearchResult.VALID) {
            return new Vector3((double)f, (double)(f * 0.3f), (double)(f * 0.3f));
        }
        if (!sending && !this.addressValid) {
            return new Vector3((double)f, (double)(f * 0.3f), (double)(f * 0.3f));
        }
        if (this.getEnergyStoredGC() < 2500.0f) {
            return new Vector3((double)f, (double)(f * 0.6f), (double)(f * 0.3f));
        }
        final float r = f * 0.3f;
        final float g = f * (0.3f + teleportTimeScaled * 0.7f);
        final float b = f * (1.0f - teleportTimeScaled * 0.7f);
        return new Vector3((double)r, (double)g, (double)b);
    }
    
    public enum EnumTelepadSearchResult
    {
        VALID, 
        NOT_FOUND, 
        TOO_FAR, 
        WRONG_DIM;
    }
}
