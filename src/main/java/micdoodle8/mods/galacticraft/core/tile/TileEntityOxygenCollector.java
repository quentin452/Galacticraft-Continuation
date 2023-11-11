package micdoodle8.mods.galacticraft.core.tile;

import net.minecraft.inventory.*;
import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.world.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import net.minecraft.world.chunk.*;
import net.minecraft.block.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.energy.item.*;
import net.minecraftforge.common.util.*;
import mekanism.api.gas.*;
import java.util.*;

public class TileEntityOxygenCollector extends TileEntityOxygen implements IInventory, ISidedInventory
{
    public boolean active;
    public static final int OUTPUT_PER_TICK = 100;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public float lastOxygenCollected;
    private ItemStack[] containingItems;
    private boolean noAtmosphericOxygen;
    private boolean isInitialised;
    private boolean producedLastTick;
    
    public TileEntityOxygenCollector() {
        super(6000.0f, 0.0f);
        this.containingItems = new ItemStack[1];
        this.noAtmosphericOxygen = true;
        this.isInitialised = false;
        this.producedLastTick = false;
        this.noRedstoneControl = true;
    }
    
    public int getCappedScaledOxygenLevel(final int scale) {
        return (int)Math.max(Math.min(Math.floor(this.storedOxygen / (double)this.maxOxygen * scale), scale), 0.0);
    }
    
    public void updateEntity() {
        super.updateEntity();
        if (!this.worldObj.isRemote) {
            this.producedLastTick = (this.storedOxygen < this.maxOxygen);
            this.produceOxygen();
            if (this.worldObj.rand.nextInt(10) == 0) {
                if (this.hasEnoughEnergyToRun) {
                    float nearbyLeaves = 0.0f;
                    if (!this.isInitialised) {
                        this.noAtmosphericOxygen = (this.worldObj.provider instanceof IGalacticraftWorldProvider && !((IGalacticraftWorldProvider)this.worldObj.provider).isGasPresent(IAtmosphericGas.OXYGEN));
                        this.isInitialised = true;
                    }
                    if (this.noAtmosphericOxygen) {
                        if (this.xCoord > -29999995 && this.xCoord < 2999995 && this.zCoord > -29999995 && this.zCoord < 29999995) {
                            int miny = this.yCoord - 5;
                            int maxy = this.yCoord + 5;
                            if (miny < 0) {
                                miny = 0;
                            }
                            if (maxy >= this.worldObj.getHeight()) {
                                maxy = this.worldObj.getHeight() - 1;
                            }
                            for (int x = this.xCoord - 5; x <= this.xCoord + 5; ++x) {
                                final int chunkx = x >> 4;
                                final int intrachunkx = x & 0xF;
                                int chunkz = this.zCoord - 5 >> 4;
                                Chunk chunk = this.worldObj.getChunkFromChunkCoords(chunkx, chunkz);
                                for (int z = this.zCoord - 5; z <= this.zCoord + 5; ++z) {
                                    if (z >> 4 != chunkz) {
                                        chunkz = z >> 4;
                                        chunk = this.worldObj.getChunkFromChunkCoords(chunkx, chunkz);
                                    }
                                    for (int y = miny; y <= maxy; ++y) {
                                        final Block block = chunk.getBlock(intrachunkx, y, z & 0xF);
                                        if (!(block instanceof BlockAir) && (block.isLeaves((IBlockAccess)this.worldObj, x, y, z) || (block instanceof IPlantable && ((IPlantable)block).getPlantType((IBlockAccess)this.worldObj, x, y, z) == EnumPlantType.Crop))) {
                                            nearbyLeaves += 0.75f;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else {
                        nearbyLeaves = 93.0f;
                    }
                    nearbyLeaves = (float)Math.floor(nearbyLeaves);
                    this.lastOxygenCollected = nearbyLeaves / 10.0f;
                    this.storedOxygen = (float)(int)Math.max(Math.min(this.storedOxygen + nearbyLeaves, this.maxOxygen), 0.0f);
                }
                else {
                    this.lastOxygenCollected = 0.0f;
                }
            }
        }
    }
    
    public void readFromNBT(final NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        final NBTTagList var2 = par1NBTTagCompound.getTagList("Items", 10);
        this.containingItems = new ItemStack[this.getSizeInventory()];
        for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
            final NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            final int var5 = var4.getByte("Slot") & 0xFF;
            if (var5 < this.containingItems.length) {
                this.containingItems[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }
    }
    
    public void writeToNBT(final NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        final NBTTagList list = new NBTTagList();
        for (int var3 = 0; var3 < this.containingItems.length; ++var3) {
            if (this.containingItems[var3] != null) {
                final NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.containingItems[var3].writeToNBT(var4);
                list.appendTag((NBTBase)var4);
            }
        }
        par1NBTTagCompound.setTag("Items", (NBTBase)list);
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
        return GCCoreUtil.translate("container.oxygencollector.name");
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public boolean isUseableByPlayer(final EntityPlayer par1EntityPlayer) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && par1EntityPlayer.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5) <= 64.0;
    }
    
    public void openInventory() {
    }
    
    public void closeInventory() {
    }
    
    public int[] getAccessibleSlotsFromSide(final int side) {
        return new int[] { 0 };
    }
    
    public boolean canInsertItem(final int slotID, final ItemStack itemstack, final int side) {
        return this.isItemValidForSlot(slotID, itemstack);
    }
    
    public boolean canExtractItem(final int slotID, final ItemStack itemstack, final int side) {
        return slotID == 0;
    }
    
    public boolean hasCustomInventoryName() {
        return true;
    }
    
    public boolean isItemValidForSlot(final int slotID, final ItemStack itemstack) {
        return slotID == 0 && ItemElectricBase.isElectricItem(itemstack.getItem());
    }
    
    public boolean shouldUseEnergy() {
        return this.storedOxygen > 0.0f && this.producedLastTick;
    }
    
    public ForgeDirection getElectricInputDirection() {
        return ForgeDirection.getOrientation(this.getBlockMetadata() + 2);
    }
    
    public ItemStack getBatteryInSlot() {
        return this.getStackInSlot(0);
    }
    
    public boolean shouldPullOxygen() {
        return false;
    }
    
    public boolean canReceiveGas(final ForgeDirection side, final Gas type) {
        return false;
    }
    
    public int receiveGas(final ForgeDirection side, final GasStack stack, final boolean doTransfer) {
        return 0;
    }
    
    public int receiveGas(final ForgeDirection side, final GasStack stack) {
        return 0;
    }
    
    public boolean shouldUseOxygen() {
        return false;
    }
    
    public EnumSet<ForgeDirection> getOxygenInputDirections() {
        return EnumSet.noneOf(ForgeDirection.class);
    }
    
    public EnumSet<ForgeDirection> getOxygenOutputDirections() {
        return EnumSet.of(this.getElectricInputDirection().getOpposite());
    }
    
    public float getOxygenProvide(final ForgeDirection direction) {
        return this.getOxygenOutputDirections().contains(direction) ? Math.min(500.0f, this.getOxygenStored()) : 0.0f;
    }
}
