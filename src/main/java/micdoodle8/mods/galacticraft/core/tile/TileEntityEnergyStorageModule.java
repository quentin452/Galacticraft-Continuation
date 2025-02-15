package micdoodle8.mods.galacticraft.core.tile;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

import micdoodle8.mods.galacticraft.api.item.IItemElectricBase;
import micdoodle8.mods.galacticraft.api.transmission.NetworkType;
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectricalSource;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

public class TileEntityEnergyStorageModule extends TileBaseUniversalElectricalSource
    implements ISidedInventory, IConnector {

    private static final float BASE_CAPACITY = 500000;
    private static final float TIER2_CAPACITY = 2500000;
    private ItemStack[] containingItems = new ItemStack[2];

    public final Set<EntityPlayer> playersUsing = new HashSet<>();
    public int scaledEnergyLevel;
    public int lastScaledEnergyLevel;
    private float lastEnergy = 0;

    private boolean initialised = false;

    public TileEntityEnergyStorageModule() {
        this(1);
    }

    /*
     * @param tier: 1 = Electric Furnace 2 = Electric Arc Furnace
     */
    public TileEntityEnergyStorageModule(int tier) {
        this.initialised = true;
        if (tier == 1) {
            // Designed so that Tier 1 Energy Storage can power up to 10 Tier 1 machines
            this.storage.setCapacity(BASE_CAPACITY);
            this.storage.setMaxExtract(300);
            return;
        }

        this.setTier2();
    }

    private void setTier2() {
        this.storage.setCapacity(TIER2_CAPACITY);
        this.storage.setMaxExtract(1800);
        this.setTierGC(2);
    }

    @Override
    public void updateEntity() {
        if (!this.initialised) {
            final int metadata = this.getBlockMetadata();

            // for version update compatibility
            final Block b = this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord);
            if (b == GCBlocks.machineBase) {
                this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, GCBlocks.machineTiered, 0, 2);
            } else if (metadata >= 8) {
                this.setTier2();
            }
            this.initialised = true;
        }

        final float energy = this.storage.getEnergyStoredGC();
        if (this.getTierGC() == 1 && !this.worldObj.isRemote
            && this.lastEnergy - energy > this.storage.getMaxExtract() - 1) {
            // Deplete faster if being drained at maximum output
            this.storage.extractEnergyGC(25, false);
        }
        this.lastEnergy = energy;

        super.updateEntity();

        this.scaledEnergyLevel = (int) Math.floor((this.getEnergyStoredGC() + 49) * 16 / this.getMaxEnergyStoredGC());

        if (this.scaledEnergyLevel != this.lastScaledEnergyLevel) {
            this.worldObj.func_147479_m(this.xCoord, this.yCoord, this.zCoord);
        }

        if (!this.worldObj.isRemote) {
            this.recharge(this.containingItems[0]);
            this.discharge(this.containingItems[1]);
        }

        if (!this.worldObj.isRemote) {
            this.produce();
        }

        this.lastScaledEnergyLevel = this.scaledEnergyLevel;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    /**
     * Reads a tile entity from NBT.
     */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        if (this.storage.getEnergyStoredGC() > BASE_CAPACITY) {
            this.setTier2();
            this.initialised = true;
        } else {
            this.initialised = false;
        }

        final NBTTagList var2 = par1NBTTagCompound.getTagList("Items", 10);
        this.containingItems = new ItemStack[this.getSizeInventory()];

        for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
            final NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            final int var5 = var4.getByte("Slot") & 255;

            if (var5 < this.containingItems.length) {
                this.containingItems[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        if (this.tierGC == 1 && this.storage.getEnergyStoredGC() > BASE_CAPACITY) {
            this.storage.setEnergyStored(BASE_CAPACITY);
        }

        super.writeToNBT(par1NBTTagCompound);
        final NBTTagList var2 = new NBTTagList();

        for (int var3 = 0; var3 < this.containingItems.length; ++var3) {
            if (this.containingItems[var3] != null) {
                final NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte) var3);
                this.containingItems[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }

        par1NBTTagCompound.setTag("Items", var2);
    }

    @Override
    public int getSizeInventory() {
        return this.containingItems.length;
    }

    @Override
    public ItemStack getStackInSlot(int par1) {
        return this.containingItems[par1];
    }

    @Override
    public ItemStack decrStackSize(int par1, int par2) {
        if (this.containingItems[par1] == null) {
            return null;
        }
        ItemStack var3;

        if (this.containingItems[par1].stackSize <= par2) {
            var3 = this.containingItems[par1];
            this.containingItems[par1] = null;
        } else {
            var3 = this.containingItems[par1].splitStack(par2);

            if (this.containingItems[par1].stackSize == 0) {
                this.containingItems[par1] = null;
            }
        }
        return var3;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int par1) {
        if (this.containingItems[par1] != null) {
            final ItemStack var2 = this.containingItems[par1];
            this.containingItems[par1] = null;
            return var2;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
        this.containingItems[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public String getInventoryName() {
        return GCCoreUtil.translate(this.tierGC == 1 ? "tile.machine.1.name" : "tile.machine.8.name");
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
            && par1EntityPlayer.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slotID, ItemStack itemstack) {
        return ItemElectricBase.isElectricItem(itemstack.getItem());
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int slotID) {
        return new int[] { 0, 1 };
    }

    @Override
    public boolean canInsertItem(int slotID, ItemStack itemstack, int side) {
        if (itemstack.getItem() instanceof IItemElectricBase) {
            if (slotID == 0) {
                return ((IItemElectricBase) itemstack.getItem()).getTransfer(itemstack) > 0;
            }
            if (slotID == 1) {
                return ((IItemElectricBase) itemstack.getItem()).getElectricityStored(itemstack) > 0;
            }
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slotID, ItemStack itemstack, int side) {
        if (itemstack.getItem() instanceof IItemElectricBase) {
            if (slotID == 0) {
                return ((IItemElectricBase) itemstack.getItem()).getTransfer(itemstack) <= 0;
            }
            if (slotID == 1) {
                return ((IItemElectricBase) itemstack.getItem()).getElectricityStored(itemstack) <= 0
                    || this.getEnergyStoredGC() >= this.getMaxEnergyStoredGC();
            }
        }

        return false;
    }

    @Override
    public EnumSet<ForgeDirection> getElectricalInputDirections() {
        return EnumSet.of(
            ForgeDirection.getOrientation((this.getBlockMetadata() & 3) + 2)
                .getOpposite(),
            ForgeDirection.UNKNOWN);
    }

    @Override
    public EnumSet<ForgeDirection> getElectricalOutputDirections() {
        return EnumSet.of(ForgeDirection.getOrientation((this.getBlockMetadata() & 3) + 2), ForgeDirection.UNKNOWN);
    }

    @Override
    public ForgeDirection getElectricalOutputDirectionMain() {
        return ForgeDirection.getOrientation((this.getBlockMetadata() & 3) + 2);
    }

    @Override
    public boolean canConnect(ForgeDirection direction, NetworkType type) {
        if (direction == null || ForgeDirection.UNKNOWN.equals(direction) || type != NetworkType.POWER) {
            return false;
        }

        final int metadata = this.getBlockMetadata() & 3;

        return direction == ForgeDirection.getOrientation(metadata + 2)
            || direction == ForgeDirection.getOrientation(metadata + 2 ^ 1);
    }
}
