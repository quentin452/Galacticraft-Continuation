package micdoodle8.mods.galacticraft.core.tile;

import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.miccore.*;
import micdoodle8.mods.galacticraft.api.block.*;
import net.minecraft.block.*;
import net.minecraft.server.*;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;
import io.netty.buffer.*;
import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.energy.item.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraftforge.common.util.*;
import java.util.*;

public class TileEntityOxygenDistributor extends TileEntityOxygen implements IInventory, ISidedInventory, IBubbleProvider
{
    public boolean active;
    public boolean lastActive;
    private ItemStack[] containingItems;
    public static HashSet<BlockVec3Dim> loadedTiles;
    public float bubbleSize;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean shouldRenderBubble;
    
    public TileEntityOxygenDistributor() {
        super(6000.0f, 8.0f);
        this.containingItems = new ItemStack[2];
        this.shouldRenderBubble = true;
    }
    
    public void validate() {
        super.validate();
        if (!this.worldObj.isRemote) {
            TileEntityOxygenDistributor.loadedTiles.add(new BlockVec3Dim(this.xCoord, this.yCoord, this.zCoord, this.worldObj.provider.dimensionId));
        }
    }
    
    public void onChunkUnload() {
        TileEntityOxygenDistributor.loadedTiles.remove(new BlockVec3Dim(this.xCoord, this.yCoord, this.zCoord, this.worldObj.provider.dimensionId));
        super.onChunkUnload();
    }
    
    public void invalidate() {
        if (!this.worldObj.isRemote) {
            final int bubbleR = MathHelper.ceiling_double_int((double)this.bubbleSize);
            final int bubbleR2 = (int)(this.bubbleSize * this.bubbleSize);
            for (int x = this.xCoord - bubbleR; x < this.xCoord + bubbleR; ++x) {
                for (int y = this.yCoord - bubbleR; y < this.yCoord + bubbleR; ++y) {
                    for (int z = this.zCoord - bubbleR; z < this.zCoord + bubbleR; ++z) {
                        final Block block = this.worldObj.getBlock(x, y, z);
                        if (block instanceof IOxygenReliantBlock && this.getDistanceFromServer(x, y, z) <= bubbleR2) {
                            this.worldObj.scheduleBlockUpdateWithPriority(x, y, z, block, 1, 0);
                        }
                    }
                }
            }
            TileEntityOxygenDistributor.loadedTiles.remove(new BlockVec3Dim(this.xCoord, this.yCoord, this.zCoord, this.worldObj.provider.dimensionId));
        }
        super.invalidate();
    }
    
    public double getPacketRange() {
        return 64.0;
    }
    
    public void addExtraNetworkedData(final List<Object> networkedList) {
        if (!this.worldObj.isRemote && !this.isInvalid()) {
            if (MinecraftServer.getServer().isDedicatedServer()) {
                networkedList.add(TileEntityOxygenDistributor.loadedTiles.size());
                for (final BlockVec3Dim distributor : TileEntityOxygenDistributor.loadedTiles) {
                    if (distributor == null) {
                        networkedList.add(-1);
                        networkedList.add(-1);
                        networkedList.add(-1);
                        networkedList.add(-1);
                    }
                    else {
                        networkedList.add(distributor.x);
                        networkedList.add(distributor.y);
                        networkedList.add(distributor.z);
                        networkedList.add(distributor.dim);
                    }
                }
            }
            else {
                networkedList.add(-1);
            }
            networkedList.add(this.bubbleSize);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox((double)(this.xCoord - this.bubbleSize), (double)(this.yCoord - this.bubbleSize), (double)(this.zCoord - this.bubbleSize), (double)(this.xCoord + this.bubbleSize), (double)(this.yCoord + this.bubbleSize), (double)(this.zCoord + this.bubbleSize));
    }
    
    public void readExtraNetworkedData(final ByteBuf dataStream) {
        if (this.worldObj.isRemote) {
            final int size = dataStream.readInt();
            if (size >= 0) {
                TileEntityOxygenDistributor.loadedTiles.clear();
                for (int i = 0; i < size; ++i) {
                    final int i2 = dataStream.readInt();
                    final int i3 = dataStream.readInt();
                    final int i4 = dataStream.readInt();
                    final int i5 = dataStream.readInt();
                    if (i2 != -1 || i3 != -1 || i4 != -1 || i5 != -1) {
                        TileEntityOxygenDistributor.loadedTiles.add(new BlockVec3Dim(i2, i3, i4, i5));
                    }
                }
            }
            this.bubbleSize = dataStream.readFloat();
        }
    }
    
    public int getDistanceFromServer(final int par1, final int par3, final int par5) {
        final int d3 = this.xCoord - par1;
        final int d4 = this.yCoord - par3;
        final int d5 = this.zCoord - par5;
        return d3 * d3 + d4 * d4 + d5 * d5;
    }
    
    public void updateEntity() {
        if (!this.worldObj.isRemote) {
            final ItemStack oxygenItemStack = this.getStackInSlot(1);
            if (oxygenItemStack != null && oxygenItemStack.getItem() instanceof IItemOxygenSupply) {
                final IItemOxygenSupply oxygenItem = (IItemOxygenSupply)oxygenItemStack.getItem();
                final float oxygenDraw = Math.min(this.oxygenPerTick * 2.5f, this.maxOxygen - this.storedOxygen);
                this.storedOxygen += oxygenItem.discharge(oxygenItemStack, oxygenDraw);
                if (this.storedOxygen > this.maxOxygen) {
                    this.storedOxygen = this.maxOxygen;
                }
            }
        }
        super.updateEntity();
        if (!this.worldObj.isRemote) {
            if (this.getEnergyStoredGC() > 0.0f && this.hasEnoughEnergyToRun && this.storedOxygen > this.oxygenPerTick) {
                this.bubbleSize += 0.01f;
            }
            else {
                this.bubbleSize -= 0.1f;
            }
            this.bubbleSize = Math.min(Math.max(this.bubbleSize, 0.0f), 10.0f);
        }
        if (!this.worldObj.isRemote) {
            this.active = (this.bubbleSize >= 1.0f && this.hasEnoughEnergyToRun && this.storedOxygen > this.oxygenPerTick);
            if (this.ticks % (this.active ? 20 : 4) == 0) {
                final double size = this.bubbleSize;
                final int bubbleR = MathHelper.floor_double(size) + 4;
                final int bubbleR2 = (int)(size * size);
                for (int x = this.xCoord - bubbleR; x <= this.xCoord + bubbleR; ++x) {
                    for (int y = this.yCoord - bubbleR; y <= this.yCoord + bubbleR; ++y) {
                        for (int z = this.zCoord - bubbleR; z <= this.zCoord + bubbleR; ++z) {
                            final Block block = this.worldObj.getBlock(x, y, z);
                            if (block instanceof IOxygenReliantBlock) {
                                if (this.getDistanceFromServer(x, y, z) <= bubbleR2) {
                                    ((IOxygenReliantBlock)block).onOxygenAdded(this.worldObj, x, y, z);
                                }
                                else {
                                    this.worldObj.scheduleBlockUpdateWithPriority(x, y, z, block, 1, 0);
                                }
                            }
                        }
                    }
                }
            }
        }
        this.lastActive = this.active;
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.func_150296_c().contains("bubbleVisible")) {
            this.setBubbleVisible(nbt.getBoolean("bubbleVisible"));
        }
        if (nbt.func_150296_c().contains("bubbleSize")) {
            this.bubbleSize = nbt.getFloat("bubbleSize");
        }
        final NBTTagList var2 = nbt.getTagList("Items", 10);
        this.containingItems = new ItemStack[this.getSizeInventory()];
        for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
            final NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            final int var5 = var4.getByte("Slot") & 0xFF;
            if (var5 < this.containingItems.length) {
                this.containingItems[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("bubbleVisible", this.shouldRenderBubble);
        nbt.setFloat("bubbleSize", this.bubbleSize);
        final NBTTagList list = new NBTTagList();
        for (int var3 = 0; var3 < this.containingItems.length; ++var3) {
            if (this.containingItems[var3] != null) {
                final NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.containingItems[var3].writeToNBT(var4);
                list.appendTag((NBTBase)var4);
            }
        }
        nbt.setTag("Items", (NBTBase)list);
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
    
    public String getInventoryName() {
        return GCCoreUtil.translate("container.oxygendistributor.name");
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public boolean isUseableByPlayer(final EntityPlayer par1EntityPlayer) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && par1EntityPlayer.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5) <= 64.0;
    }
    
    public int[] getAccessibleSlotsFromSide(final int side) {
        return new int[] { 0, 1 };
    }
    
    public boolean canInsertItem(final int slotID, final ItemStack itemstack, final int side) {
        if (!this.isItemValidForSlot(slotID, itemstack)) {
            return false;
        }
        switch (slotID) {
            case 0: {
                return ItemElectricBase.isElectricItemCharged(itemstack);
            }
            case 1: {
                return itemstack.getItemDamage() < itemstack.getItem().getMaxDamage();
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean canExtractItem(final int slotID, final ItemStack itemstack, final int side) {
        switch (slotID) {
            case 0: {
                return ItemElectricBase.isElectricItemEmpty(itemstack);
            }
            case 1: {
                return FluidUtil.isEmptyContainer(itemstack);
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean hasCustomInventoryName() {
        return true;
    }
    
    public boolean isItemValidForSlot(final int slotID, final ItemStack itemstack) {
        if (itemstack == null) {
            return false;
        }
        if (slotID == 0) {
            return ItemElectricBase.isElectricItem(itemstack.getItem());
        }
        return slotID == 1 && itemstack.getItem() instanceof IItemOxygenSupply;
    }
    
    public void openInventory() {
    }
    
    public void closeInventory() {
    }
    
    public boolean shouldUseEnergy() {
        return this.storedOxygen > this.oxygenPerTick;
    }
    
    public ForgeDirection getElectricInputDirection() {
        return ForgeDirection.getOrientation(this.getBlockMetadata() + 2);
    }
    
    public ItemStack getBatteryInSlot() {
        return this.getStackInSlot(0);
    }
    
    public boolean shouldUseOxygen() {
        return this.hasEnoughEnergyToRun;
    }
    
    public EnumSet<ForgeDirection> getOxygenInputDirections() {
        return EnumSet.of(this.getElectricInputDirection().getOpposite());
    }
    
    public EnumSet<ForgeDirection> getOxygenOutputDirections() {
        return EnumSet.noneOf(ForgeDirection.class);
    }
    
    public boolean inBubble(final double pX, final double pY, final double pZ) {
        double r = this.bubbleSize;
        r *= r;
        double d3 = this.xCoord + 0.5 - pX;
        d3 *= d3;
        if (d3 > r) {
            return false;
        }
        double d4 = this.zCoord + 0.5 - pZ;
        d4 *= d4;
        if (d3 + d4 > r) {
            return false;
        }
        final double d5 = this.yCoord + 0.5 - pY;
        return d3 + d4 + d5 * d5 < r;
    }
    
    public void setBubbleVisible(final boolean shouldRender) {
        this.shouldRenderBubble = shouldRender;
    }
    
    public float getBubbleSize() {
        return this.bubbleSize;
    }
    
    public boolean getBubbleVisible() {
        return this.shouldRenderBubble;
    }
    
    static {
        TileEntityOxygenDistributor.loadedTiles = new HashSet<BlockVec3Dim>();
    }
}
