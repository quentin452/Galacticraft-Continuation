package micdoodle8.mods.galacticraft.core.tile;

import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraftforge.fluids.*;
import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.blocks.*;

public class TileEntityParaChest extends TileEntityAdvanced implements IInventorySettable, IScaleableFuelLevel
{
    private final int tankCapacity = 5000;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public FluidTank fuelTank;
    public ItemStack[] chestContents;
    public boolean adjacentChestChecked;
    public float lidAngle;
    public float prevLidAngle;
    public int numUsingPlayers;

    public TileEntityParaChest() {
        this.getClass();
        this.fuelTank = new FluidTank(5000);
        this.chestContents = new ItemStack[3];
        this.adjacentChestChecked = false;
    }

    public void validate() {
        super.validate();
        if (this.worldObj != null && this.worldObj.isRemote) {
            GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketDynamicInventory((TileEntity)this));
        }
    }

    public int getScaledFuelLevel(final int i) {
        final double fuelLevel = (this.fuelTank.getFluid() == null) ? 0.0 : this.fuelTank.getFluid().amount;
        final double n = fuelLevel * i;
        this.getClass();
        return (int)(n / 5000.0);
    }

    public int getSizeInventory() {
        return this.chestContents.length;
    }

    public void setSizeInventory(int size) {
        if ((size - 3) % 18 != 0) {
            size += 18 - (size - 3) % 18;
        }
        this.chestContents = new ItemStack[size];
    }

    public ItemStack getStackInSlot(final int par1) {
        return this.chestContents[par1];
    }

    public ItemStack decrStackSize(final int par1, final int par2) {
        if (this.chestContents[par1] == null) {
            return null;
        }
        if (this.chestContents[par1].stackSize <= par2) {
            final ItemStack itemstack = this.chestContents[par1];
            this.chestContents[par1] = null;
            this.markDirty();
            return itemstack;
        }
        final ItemStack itemstack = this.chestContents[par1].splitStack(par2);
        if (this.chestContents[par1].stackSize == 0) {
            this.chestContents[par1] = null;
        }
        this.markDirty();
        return itemstack;
    }

    public ItemStack getStackInSlotOnClosing(final int par1) {
        if (this.chestContents[par1] != null) {
            final ItemStack itemstack = this.chestContents[par1];
            this.chestContents[par1] = null;
            return itemstack;
        }
        return null;
    }

    public void setInventorySlotContents(final int par1, final ItemStack par2ItemStack) {
        this.chestContents[par1] = par2ItemStack;
        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
        this.markDirty();
    }

    public String getInventoryName() {
        return GCCoreUtil.translate("container.parachest.name");
    }

    public boolean hasCustomInventoryName() {
        return true;
    }

    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        final NBTTagList nbttaglist = nbt.getTagList("Items", 10);
        int size = nbt.getInteger("chestContentLength");
        if ((size - 3) % 18 != 0) {
            size += 18 - (size - 3) % 18;
        }
        this.chestContents = new ItemStack[size];
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            final int j = nbttagcompound1.getByte("Slot") & 0xFF;
            if (j < this.chestContents.length) {
                this.chestContents[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
        if (nbt.hasKey("fuelTank")) {
            this.fuelTank.readFromNBT(nbt.getCompoundTag("fuelTank"));
        }
    }

    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("chestContentLength", this.chestContents.length);
        final NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < this.chestContents.length; ++i) {
            if (this.chestContents[i] != null) {
                final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.chestContents[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag((NBTBase)nbttagcompound1);
            }
        }
        nbt.setTag("Items", (NBTBase)nbttaglist);
        if (this.fuelTank.getFluid() != null) {
            nbt.setTag("fuelTank", (NBTBase)this.fuelTank.writeToNBT(new NBTTagCompound()));
        }
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean isUseableByPlayer(final EntityPlayer par1EntityPlayer) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && par1EntityPlayer.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5) <= 64.0;
    }

    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        this.adjacentChestChecked = false;
    }

    public void updateEntity() {
        super.updateEntity();
        if (!this.worldObj.isRemote && this.numUsingPlayers != 0 && (this.ticks + this.xCoord + this.yCoord + this.zCoord) % 200 == 0) {
            this.numUsingPlayers = 0;
            final float f = 5.0f;
            final List<EntityPlayer> list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox((double)(this.xCoord - f), (double)(this.yCoord - f), (double)(this.zCoord - f), (double)(this.xCoord + 1 + f), (double)(this.yCoord + 1 + f), (double)(this.zCoord + 1 + f)));
            for (final EntityPlayer entityplayer : list) {
                if (entityplayer.openContainer instanceof ContainerParaChest) {
                    ++this.numUsingPlayers;
                }
            }
        }
        this.prevLidAngle = this.lidAngle;
        final float f = 0.1f;
        if (this.numUsingPlayers > 0 && this.lidAngle == 0.0f) {
            final double d1 = this.xCoord + 0.5;
            final double d2 = this.zCoord + 0.5;
            this.worldObj.playSoundEffect(d1, this.yCoord + 0.5, d2, "random.chestopen", 0.5f, this.worldObj.rand.nextFloat() * 0.1f + 0.9f);
        }
        if ((this.numUsingPlayers == 0 && this.lidAngle > 0.0f) || (this.numUsingPlayers > 0 && this.lidAngle < 1.0f)) {
            final float f2 = this.lidAngle;
            if (this.numUsingPlayers > 0) {
                this.lidAngle += f;
            }
            else {
                this.lidAngle -= f;
            }
            if (this.lidAngle > 1.0f) {
                this.lidAngle = 1.0f;
            }
            final float f3 = 0.5f;
            if (this.lidAngle < f3 && f2 >= f3) {
                final double d2 = this.xCoord + 0.5;
                final double d3 = this.zCoord + 0.5;
                this.worldObj.playSoundEffect(d2, this.yCoord + 0.5, d3, "random.chestclosed", 0.5f, this.worldObj.rand.nextFloat() * 0.1f + 0.9f);
            }
            if (this.lidAngle < 0.0f) {
                this.lidAngle = 0.0f;
            }
        }
        if (!this.worldObj.isRemote) {
            this.checkFluidTankTransfer(this.chestContents.length - 1, this.fuelTank);
        }
    }

    private void checkFluidTankTransfer(final int slot, final FluidTank tank) {
        FluidUtil.tryFillContainerFuel(tank, this.chestContents, slot);
    }

    public boolean receiveClientEvent(final int par1, final int par2) {
        if (par1 == 1) {
            this.numUsingPlayers = par2;
            return true;
        }
        return super.receiveClientEvent(par1, par2);
    }

    public void openInventory() {
        if (this.numUsingPlayers < 0) {
            this.numUsingPlayers = 0;
        }
        ++this.numUsingPlayers;
        this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 1, this.numUsingPlayers);
        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType());
    }

    public void closeInventory() {
        if (this.getBlockType() != null && this.getBlockType() instanceof BlockParaChest) {
            --this.numUsingPlayers;
            this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 1, this.numUsingPlayers);
            this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
            this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType());
        }
    }

    public boolean isItemValidForSlot(final int par1, final ItemStack par2ItemStack) {
        return true;
    }

    public void invalidate() {
        super.invalidate();
        this.updateContainingBlockInfo();
    }

    public double getPacketRange() {
        return 12.0;
    }

    public int getPacketCooldown() {
        return 3;
    }

    public boolean isNetworkedTile() {
        return true;
    }
}
