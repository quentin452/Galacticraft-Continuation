package micdoodle8.mods.galacticraft.core.tile;

import cpw.mods.fml.relauncher.Side;
import java.util.Iterator;
import java.util.List;
import micdoodle8.mods.galacticraft.api.item.IKeyable;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.BlockT1TreasureChest;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.miccore.Annotations.NetworkedField;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityTreasureChest extends TileEntityAdvanced implements IInventory, IKeyable {
    private ItemStack[] chestContents;
    public boolean adjacentChestChecked;
    public TileEntityTreasureChest adjacentChestZNeg;
    public TileEntityTreasureChest adjacentChestXPos;
    public TileEntityTreasureChest adjacentChestXNeg;
    public TileEntityTreasureChest adjacentChestZPos;
    public float lidAngle;
    public float prevLidAngle;
    public int numUsingPlayers;
    private int ticksSinceSync;
    @NetworkedField(
        targetSide = Side.CLIENT
    )
    public boolean locked;
    public int tier;

    public TileEntityTreasureChest() {
        this(1);
    }

    public TileEntityTreasureChest(int tier) {
        this.chestContents = new ItemStack[27];
        this.adjacentChestChecked = false;
        this.locked = true;
        this.tier = 1;
        this.tier = tier;
    }

    public int getSizeInventory() {
        return 27;
    }

    public ItemStack getStackInSlot(int par1) {
        return this.chestContents[par1];
    }

    public ItemStack decrStackSize(int par1, int par2) {
        if (this.chestContents[par1] != null) {
            ItemStack itemstack;
            if (this.chestContents[par1].stackSize <= par2) {
                itemstack = this.chestContents[par1];
                this.chestContents[par1] = null;
                this.markDirty();
                return itemstack;
            } else {
                itemstack = this.chestContents[par1].splitStack(par2);
                if (this.chestContents[par1].stackSize == 0) {
                    this.chestContents[par1] = null;
                }

                this.markDirty();
                return itemstack;
            }
        } else {
            return null;
        }
    }

    public ItemStack getStackInSlotOnClosing(int par1) {
        if (this.chestContents[par1] != null) {
            ItemStack itemstack = this.chestContents[par1];
            this.chestContents[par1] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
        this.chestContents[par1] = par2ItemStack;
        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }

        this.markDirty();
    }

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.locked = nbt.getBoolean("isLocked");
        this.tier = nbt.getInteger("tier");
        NBTTagList nbttaglist = nbt.getTagList("Items", 10);
        this.chestContents = new ItemStack[this.getSizeInventory()];

        for(int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound1.getByte("Slot") & 255;
            if (j < this.chestContents.length) {
                this.chestContents[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }

    }

    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("isLocked", this.locked);
        nbt.setInteger("tier", this.tier);
        NBTTagList nbttaglist = new NBTTagList();

        for(int i = 0; i < this.chestContents.length; ++i) {
            if (this.chestContents[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.chestContents[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        nbt.setTag("Items", nbttaglist);
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && par1EntityPlayer.getDistanceSq((double)this.xCoord + 0.5, (double)this.yCoord + 0.5, (double)this.zCoord + 0.5) <= 64.0;
    }

    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        this.adjacentChestChecked = false;
    }

    private void func_90009_a(TileEntityTreasureChest par1TileEntityChest, int par2) {
        if (par1TileEntityChest.isInvalid()) {
            this.adjacentChestChecked = false;
        } else if (this.adjacentChestChecked) {
            switch (par2) {
                case 0:
                    if (this.adjacentChestZPos != par1TileEntityChest) {
                        this.adjacentChestChecked = false;
                    }
                    break;
                case 1:
                    if (this.adjacentChestXNeg != par1TileEntityChest) {
                        this.adjacentChestChecked = false;
                    }
                    break;
                case 2:
                    if (this.adjacentChestZNeg != par1TileEntityChest) {
                        this.adjacentChestChecked = false;
                    }
                    break;
                case 3:
                    if (this.adjacentChestXPos != par1TileEntityChest) {
                        this.adjacentChestChecked = false;
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
                this.adjacentChestXNeg = (TileEntityTreasureChest)this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord);
            }

            if (this.func_94044_a(this.xCoord + 1, this.yCoord, this.zCoord)) {
                this.adjacentChestXPos = (TileEntityTreasureChest)this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord);
            }

            if (this.func_94044_a(this.xCoord, this.yCoord, this.zCoord - 1)) {
                this.adjacentChestZNeg = (TileEntityTreasureChest)this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1);
            }

            if (this.func_94044_a(this.xCoord, this.yCoord, this.zCoord + 1)) {
                this.adjacentChestZPos = (TileEntityTreasureChest)this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1);
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

    private boolean func_94044_a(int par1, int par2, int par3) {
        Block block = this.worldObj.getBlock(par1, par2, par3);
        return block != null && block instanceof BlockT1TreasureChest;
    }

    public void updateEntity() {
        super.updateEntity();
        this.checkForAdjacentChests();
        ++this.ticksSinceSync;
        float f;
        if (!this.worldObj.isRemote && this.numUsingPlayers != 0 && (this.ticksSinceSync + this.xCoord + this.yCoord + this.zCoord) % 200 == 0) {
            this.numUsingPlayers = 0;
            f = 5.0F;
            List<?> list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox((double)((float)this.xCoord - f), (double)((float)this.yCoord - f), (double)((float)this.zCoord - f), (double)((float)(this.xCoord + 1) + f), (double)((float)(this.yCoord + 1) + f), (double)((float)(this.zCoord + 1) + f)));
            Iterator<?> iterator = list.iterator();

            label93:
            while(true) {
                IInventory iinventory;
                do {
                    EntityPlayer entityplayer;
                    do {
                        if (!iterator.hasNext()) {
                            break label93;
                        }

                        entityplayer = (EntityPlayer)iterator.next();
                    } while(!(entityplayer.openContainer instanceof ContainerChest));

                    iinventory = ((ContainerChest)entityplayer.openContainer).getLowerChestInventory();
                } while(iinventory != this && (!(iinventory instanceof InventoryLargeChest) || !((InventoryLargeChest)iinventory).isPartOfLargeChest(this)));

                ++this.numUsingPlayers;
            }
        }

        this.prevLidAngle = this.lidAngle;
        f = 0.05F;
        double d0;
        if (this.numUsingPlayers > 0 && this.lidAngle == 0.0F && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
            double d1 = (double)this.xCoord + 0.5;
            d0 = (double)this.zCoord + 0.5;
            if (this.adjacentChestZPos != null) {
                d0 += 0.5;
            }

            if (this.adjacentChestXPos != null) {
                d1 += 0.5;
            }

            this.worldObj.playSoundEffect(d1, (double)this.yCoord + 0.5, d0, "random.chestopen", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.6F);
        }

        if (this.numUsingPlayers == 0 && this.lidAngle > 0.0F || this.numUsingPlayers > 0 && this.lidAngle < 1.0F) {
            float f1 = this.lidAngle;
            if (this.numUsingPlayers > 0) {
                this.lidAngle += f;
            } else {
                this.lidAngle -= f;
            }

            if (this.lidAngle > 1.0F) {
                this.lidAngle = 1.0F;
            }

            float f2 = 0.5F;
            if (this.lidAngle < 0.5F && f1 >= 0.5F && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
                d0 = (double)this.xCoord + 0.5;
                double d2 = (double)this.zCoord + 0.5;
                if (this.adjacentChestZPos != null) {
                    d2 += 0.5;
                }

                if (this.adjacentChestXPos != null) {
                    d0 += 0.5;
                }

                this.worldObj.playSoundEffect(d0, (double)this.yCoord + 0.5, d2, "random.chestclosed", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.6F);
            }

            if (this.lidAngle < 0.0F) {
                this.lidAngle = 0.0F;
            }
        }

    }

    public boolean receiveClientEvent(int par1, int par2) {
        if (par1 == 1) {
            this.numUsingPlayers = par2;
            return true;
        } else {
            return super.receiveClientEvent(par1, par2);
        }
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
        if (this.getBlockType() != null && this.getBlockType() instanceof BlockT1TreasureChest) {
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

    public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack) {
        return true;
    }

    public int getTierOfKeyRequired() {
        return this.tier;
    }

    public boolean onValidKeyActivated(EntityPlayer player, ItemStack key, int face) {
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

                if (!player.capabilities.isCreativeMode && --player.inventory.getCurrentItem().stackSize == 0) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
                }

                return true;
            }
        }

        return false;
    }

    public boolean onActivatedWithoutKey(EntityPlayer player, int face) {
        if (this.locked) {
            if (player.worldObj.isRemote) {
                GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(EnumSimplePacket.S_ON_FAILED_CHEST_UNLOCK, new Object[]{this.getTierOfKeyRequired()}));
            }

            return true;
        } else {
            return false;
        }
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
