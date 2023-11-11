package micdoodle8.mods.galacticraft.core.tile;

import micdoodle8.mods.galacticraft.core.items.GCItems;
import net.minecraft.inventory.*;
import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.oxygen.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.energy.item.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraftforge.common.util.*;
import net.minecraft.world.*;
import java.util.*;

public class TileEntityOxygenSealer extends TileEntityOxygen implements IInventory, ISidedInventory
{
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean sealed;
    public boolean lastSealed;
    public boolean lastDisabled;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean active;
    private ItemStack[] containingItems;
    public ThreadFindSeal threadSeal;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int stopSealThreadCooldown;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int threadCooldownTotal;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean calculatingSealed;
    public static int countEntities;
    private static int countTemp;
    private static boolean sealerCheckedThisTick;
    public static ArrayList<TileEntityOxygenSealer> loadedTiles;
    private static final int UNSEALED_OXYGENPERTICK = 12;

    public TileEntityOxygenSealer() {
        super(10000.0f, 12.0f);
        this.lastSealed = false;
        this.lastDisabled = false;
        this.containingItems = new ItemStack[3];
        this.noRedstoneControl = true;
        this.storage.setMaxExtract(5.0f);
        this.storage.setMaxReceive(25.0f);
        this.storage.setCapacity(32000.0f);
    }

    public void validate() {
        super.validate();
        if (!this.worldObj.isRemote && !TileEntityOxygenSealer.loadedTiles.contains(this)) {
            TileEntityOxygenSealer.loadedTiles.add(this);
        }
        this.stopSealThreadCooldown = 126 + TileEntityOxygenSealer.countEntities;
    }

    public void invalidate() {
        if (!this.worldObj.isRemote) {
            TileEntityOxygenSealer.loadedTiles.remove(this);
        }
        super.invalidate();
    }

    public void onChunkUnload() {
        if (!this.worldObj.isRemote) {
            TileEntityOxygenSealer.loadedTiles.remove(this);
        }
        super.onChunkUnload();
    }

    public int getScaledThreadCooldown(final int i) {
        if (this.active) {
            return Math.min(i, (int)Math.floor(this.stopSealThreadCooldown * i / (double)this.threadCooldownTotal));
        }
        return 0;
    }

    public int getFindSealChecks() {
        if (!this.active || this.storedOxygen < this.oxygenPerTick || !this.hasEnoughEnergyToRun) {
            return 0;
        }
        final Block blockAbove = this.worldObj.getBlock(this.xCoord, this.yCoord + 1, this.zCoord);
        if (!(blockAbove instanceof BlockAir) && !OxygenPressureProtocol.canBlockPassAir(this.worldObj, blockAbove, new BlockVec3(this.xCoord, this.yCoord + 1, this.zCoord), 1)) {
            return 0;
        }
        return 1250;
    }

    public boolean thermalControlEnabled() {
        final ItemStack oxygenItemStack = this.getStackInSlot(2);
        return oxygenItemStack != null && oxygenItemStack.getItem() == GCItems.basicItem && oxygenItemStack.getItemDamage() == 20 && this.hasEnoughEnergyToRun && !this.disabled;
    }

    public void updateEntity() {
        if (!this.worldObj.isRemote) {
            final ItemStack oxygenItemStack = this.getStackInSlot(1);
            if (oxygenItemStack != null && oxygenItemStack.getItem() instanceof IItemOxygenSupply) {
                final IItemOxygenSupply oxygenItem = (IItemOxygenSupply)oxygenItemStack.getItem();
                final float oxygenDraw = Math.min(30.0f, this.maxOxygen - this.storedOxygen);
                this.storedOxygen += oxygenItem.discharge(oxygenItemStack, oxygenDraw);
                if (this.storedOxygen > this.maxOxygen) {
                    this.storedOxygen = this.maxOxygen;
                }
            }
            if (this.thermalControlEnabled()) {
                if (this.storage.getMaxExtract() != 20.0f) {
                    this.storage.setMaxExtract(20.0f);
                }
            }
            else if (this.storage.getMaxExtract() != 5.0f) {
                this.storage.setMaxExtract(5.0f);
                this.storage.setMaxReceive(25.0f);
            }
        }
        this.oxygenPerTick = (this.sealed ? 2.0f : 12.0f);
        super.updateEntity();
        if (!this.worldObj.isRemote) {
            ++TileEntityOxygenSealer.countTemp;
            this.active = (this.storedOxygen >= 1.0f && this.hasEnoughEnergyToRun && !this.disabled);
            if (this.stopSealThreadCooldown > 0) {
                --this.stopSealThreadCooldown;
            }
            else if (!TileEntityOxygenSealer.sealerCheckedThisTick) {
                final int n = 75 + TileEntityOxygenSealer.countEntities;
                this.stopSealThreadCooldown = n;
                this.threadCooldownTotal = n;
                if (this.active || this.sealed) {
                    TileEntityOxygenSealer.sealerCheckedThisTick = true;
                    OxygenPressureProtocol.updateSealerStatus(this);
                }
            }
            if (this.threadSeal != null) {
                if (this.threadSeal.looping.get()) {
                    this.calculatingSealed = this.active;
                }
                else {
                    this.calculatingSealed = false;
                    this.sealed = (this.active && this.threadSeal.sealedFinal.get());
                }
            }
            this.lastDisabled = this.disabled;
            this.lastSealed = this.sealed;
        }
    }

    public static void onServerTick() {
        TileEntityOxygenSealer.countEntities = TileEntityOxygenSealer.countTemp;
        TileEntityOxygenSealer.countTemp = 0;
        TileEntityOxygenSealer.sealerCheckedThisTick = false;
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
        return GCCoreUtil.translate("container.oxygensealer.name");
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
            case 2: {
                return itemstack.getItem() == GCItems.basicItem && itemstack.getItemDamage() == 20;
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
        if (slotID == 1) {
            return itemstack.getItem() instanceof IItemOxygenSupply;
        }
        return slotID == 2 && itemstack.getItem() == GCItems.basicItem && itemstack.getItemDamage() == 20;
    }

    public boolean shouldUseEnergy() {
        return this.storedOxygen > this.oxygenPerTick && !this.getDisabled(0);
    }

    public ForgeDirection getElectricInputDirection() {
        return ForgeDirection.getOrientation(this.getBlockMetadata() + 2);
    }

    public ItemStack getBatteryInSlot() {
        return this.getStackInSlot(0);
    }

    public boolean shouldUseOxygen() {
        return this.hasEnoughEnergyToRun && this.active;
    }

    public EnumSet<ForgeDirection> getOxygenInputDirections() {
        return EnumSet.of(this.getElectricInputDirection().getOpposite());
    }

    public EnumSet<ForgeDirection> getOxygenOutputDirections() {
        return EnumSet.noneOf(ForgeDirection.class);
    }

    public static HashMap<BlockVec3, TileEntityOxygenSealer> getSealersAround(final World world, final int x, final int y, final int z, final int rSquared) {
        final HashMap<BlockVec3, TileEntityOxygenSealer> ret = new HashMap<BlockVec3, TileEntityOxygenSealer>();
        for (final TileEntityOxygenSealer tile : new ArrayList<TileEntityOxygenSealer>(TileEntityOxygenSealer.loadedTiles)) {
            if (tile != null && tile.getWorldObj() == world && tile.getDistanceFrom((double)x, (double)y, (double)z) < rSquared) {
                ret.put(new BlockVec3(tile.xCoord, tile.yCoord, tile.zCoord), tile);
            }
        }
        return ret;
    }

    public static TileEntityOxygenSealer getNearestSealer(final World world, final double x, final double y, final double z) {
        TileEntityOxygenSealer ret = null;
        double dist = 9216.0;
        for (final Object tile : world.loadedTileEntityList) {
            if (tile instanceof TileEntityOxygenSealer) {
                final double testDist = ((TileEntityOxygenSealer)tile).getDistanceFrom(x, y, z);
                if (testDist >= dist) {
                    continue;
                }
                dist = testDist;
                ret = (TileEntityOxygenSealer)tile;
            }
        }
        return ret;
    }

    static {
        TileEntityOxygenSealer.countEntities = 0;
        TileEntityOxygenSealer.countTemp = 0;
        TileEntityOxygenSealer.sealerCheckedThisTick = false;
        TileEntityOxygenSealer.loadedTiles = new ArrayList<TileEntityOxygenSealer>();
    }
}
