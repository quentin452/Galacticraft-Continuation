package micdoodle8.mods.galacticraft.planets.asteroids.tile;

import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.item.*;
import micdoodle8.mods.miccore.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.*;
import net.minecraft.block.*;
import net.minecraft.util.*;
import net.minecraft.inventory.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;

public class TileEntityTreasureChestAsteroids extends TileEntityAdvanced implements IInventory, IKeyable
{
    private ItemStack[] chestContents;
    public boolean adjacentChestChecked;
    public TileEntityTreasureChestAsteroids adjacentChestZNeg;
    public TileEntityTreasureChestAsteroids adjacentChestXPos;
    public TileEntityTreasureChestAsteroids adjacentChestXNeg;
    public TileEntityTreasureChestAsteroids adjacentChestZPos;
    public float lidAngle;
    public float prevLidAngle;
    public int numUsingPlayers;
    private int ticksSinceSync;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean locked;
    public int tier;

    public TileEntityTreasureChestAsteroids() {
        this(3);
    }

    public TileEntityTreasureChestAsteroids(final int tier) {
        this.chestContents = new ItemStack[36];
        this.adjacentChestChecked = false;
        this.locked = true;
        this.tier = 3;
        this.tier = tier;
    }

    public int getSizeInventory() {
        return 27;
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

    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.locked = nbt.getBoolean("isLocked");
        this.tier = nbt.getInteger("tier");
        final NBTTagList nbttaglist = nbt.getTagList("Items", 10);
        this.chestContents = new ItemStack[this.getSizeInventory()];
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            final int j = nbttagcompound1.getByte("Slot") & 0xFF;
            if (j < this.chestContents.length) {
                this.chestContents[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
    }

    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("isLocked", this.locked);
        nbt.setInteger("tier", this.tier);
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

    private void func_90009_a(final TileEntityTreasureChestAsteroids par1TileEntityChest, final int par2) {
        if (par1TileEntityChest.isInvalid()) {
            this.adjacentChestChecked = false;
        }
        else if (this.adjacentChestChecked) {
            switch (par2) {
                case 0: {
                    if (this.adjacentChestZPos != par1TileEntityChest) {
                        this.adjacentChestChecked = false;
                        break;
                    }
                    break;
                }
                case 1: {
                    if (this.adjacentChestXNeg != par1TileEntityChest) {
                        this.adjacentChestChecked = false;
                        break;
                    }
                    break;
                }
                case 2: {
                    if (this.adjacentChestZNeg != par1TileEntityChest) {
                        this.adjacentChestChecked = false;
                        break;
                    }
                    break;
                }
                case 3: {
                    if (this.adjacentChestXPos != par1TileEntityChest) {
                        this.adjacentChestChecked = false;
                        break;
                    }
                    break;
                }
            }
        }
    }

    public void checkForAdjacentChests() {
        if (!this.adjacentChestChecked) {
            this.adjacentChestChecked = true;
            this.adjacentChestZNeg = null;
            this.adjacentChestXPos = null;
            this.adjacentChestXNeg = null;
            this.adjacentChestZPos = null;
            if (this.func_94044_a(this.xCoord - 1, this.yCoord, this.zCoord)) {
                this.adjacentChestXNeg = (TileEntityTreasureChestAsteroids)this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord);
            }
            if (this.func_94044_a(this.xCoord + 1, this.yCoord, this.zCoord)) {
                this.adjacentChestXPos = (TileEntityTreasureChestAsteroids)this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord);
            }
            if (this.func_94044_a(this.xCoord, this.yCoord, this.zCoord - 1)) {
                this.adjacentChestZNeg = (TileEntityTreasureChestAsteroids)this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1);
            }
            if (this.func_94044_a(this.xCoord, this.yCoord, this.zCoord + 1)) {
                this.adjacentChestZPos = (TileEntityTreasureChestAsteroids)this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1);
            }
            if (this.adjacentChestZNeg != null) {
                this.adjacentChestZNeg.func_90009_a(this, 0);
            }
            if (this.adjacentChestZPos != null) {
                this.adjacentChestZPos.func_90009_a(this, 2);
            }
            if (this.adjacentChestXPos != null) {
                this.adjacentChestXPos.func_90009_a(this, 1);
            }
            if (this.adjacentChestXNeg != null) {
                this.adjacentChestXNeg.func_90009_a(this, 3);
            }
        }
    }

    private boolean func_94044_a(final int par1, final int par2, final int par3) {
        final Block block = this.worldObj.getBlock(par1, par2, par3);
        return block != null && block instanceof BlockTier3TreasureChest;
    }

    public void updateEntity() {
        super.updateEntity();
        this.checkForAdjacentChests();
        ++this.ticksSinceSync;
        if (!this.worldObj.isRemote && this.numUsingPlayers != 0 && (this.ticksSinceSync + this.xCoord + this.yCoord + this.zCoord) % 200 == 0) {
            this.numUsingPlayers = 0;
            final float f = 5.0f;
            final List<EntityPlayer> list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(this.xCoord - f, (double)(this.yCoord - f), (double)(this.zCoord - f), (double)(this.xCoord + 1 + f), (double)(this.yCoord + 1 + f), (double)(this.zCoord + 1 + f)));            for (final EntityPlayer entityplayer : list) {
                if (entityplayer.openContainer instanceof ContainerChest) {
                    final IInventory iinventory = ((ContainerChest) entityplayer.openContainer).getLowerChestInventory();
                    if (iinventory != this && (!(iinventory instanceof InventoryLargeChest) || !((InventoryLargeChest) iinventory).isPartOfLargeChest((IInventory) this))) {
                        continue;
                    }
                    ++this.numUsingPlayers;
                }
            }
        }
        this.prevLidAngle = this.lidAngle;
        final float f = 0.05f;
        if (this.numUsingPlayers > 0 && this.lidAngle == 0.0f && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
            double d1 = this.xCoord + 0.5;
            double d2 = this.zCoord + 0.5;
            if (this.adjacentChestZPos != null) {
                d2 += 0.5;
            }
            if (this.adjacentChestXPos != null) {
                d1 += 0.5;
            }
            this.worldObj.playSoundEffect(d1, this.yCoord + 0.5, d2, "random.chestopen", 0.5f, this.worldObj.rand.nextFloat() * 0.1f + 0.6f);
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
            if (this.lidAngle < 0.5f && f2 >= 0.5f && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
                double d2 = this.xCoord + 0.5;
                double d3 = this.zCoord + 0.5;
                if (this.adjacentChestZPos != null) {
                    d3 += 0.5;
                }
                if (this.adjacentChestXPos != null) {
                    d2 += 0.5;
                }
                this.worldObj.playSoundEffect(d2, this.yCoord + 0.5, d3, "random.chestclosed", 0.5f, this.worldObj.rand.nextFloat() * 0.1f + 0.6f);
            }
            if (this.lidAngle < 0.0f) {
                this.lidAngle = 0.0f;
            }
        }
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
        if (this.getBlockType() != null && this.getBlockType() instanceof BlockTier3TreasureChest) {
            --this.numUsingPlayers;
            this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 1, this.numUsingPlayers);
            this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
            this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType());
        }
    }

    public boolean hasCustomInventoryName() {
        return true;
    }

    public void invalidate() {
        super.invalidate();
        this.updateContainingBlockInfo();
        this.checkForAdjacentChests();
    }

    public String getInventoryName() {
        return GCCoreUtil.translate("container.treasurechest.name");
    }

    public boolean isItemValidForSlot(final int par1, final ItemStack par2ItemStack) {
        return true;
    }

    public int getTierOfKeyRequired() {
        return this.tier;
    }

    public boolean onValidKeyActivated(final EntityPlayer player, final ItemStack key, final int face) {
        if (this.locked) {
            this.locked = false;
            if (!this.worldObj.isRemote) {
                if (this.adjacentChestXNeg != null) {
                    this.adjacentChestXNeg.locked = false;
                }
                if (this.adjacentChestXPos != null) {
                    this.adjacentChestXPos.locked = false;
                }
                if (this.adjacentChestZNeg != null) {
                    this.adjacentChestZNeg.locked = false;
                }
                if (this.adjacentChestZPos != null) {
                    this.adjacentChestZPos.locked = false;
                }
                if (!player.capabilities.isCreativeMode) {
                    final ItemStack getCurrentItem = player.inventory.getCurrentItem();
                    if (--getCurrentItem.stackSize == 0) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean onActivatedWithoutKey(final EntityPlayer player, final int face) {
        if (this.locked) {
            if (player.worldObj.isRemote) {
                GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.S_ON_FAILED_CHEST_UNLOCK, new Object[] { this.getTierOfKeyRequired() }));
            }
            return true;
        }
        return false;
    }

    public boolean canBreak() {
        return false;
    }

    public double getPacketRange() {
        return 20.0;
    }

    public int getPacketCooldown() {
        return 3;
    }

    public boolean isNetworkedTile() {
        return true;
    }
}
