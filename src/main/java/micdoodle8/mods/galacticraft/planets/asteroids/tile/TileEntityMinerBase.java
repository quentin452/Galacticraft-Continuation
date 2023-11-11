package micdoodle8.mods.galacticraft.planets.asteroids.tile;

import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import java.lang.ref.*;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.*;
import micdoodle8.mods.galacticraft.api.power.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.planets.asteroids.network.*;
import micdoodle8.mods.galacticraft.core.network.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.planets.asteroids.items.*;
import micdoodle8.mods.galacticraft.planets.*;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;
import net.minecraftforge.common.util.*;
import micdoodle8.mods.galacticraft.core.energy.item.*;
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.*;
import java.util.*;

public class TileEntityMinerBase extends TileBaseElectricBlockWithInventory implements ISidedInventory, IMultiBlock
{
    public static final int HOLDSIZE = 72;
    private ItemStack[] containingItems;
    private int[] slotArray;
    public boolean isMaster;
    public int facing;
    private BlockVec3 mainBlockPosition;
    private LinkedList<BlockVec3> targetPoints;
    private WeakReference<TileEntityMinerBase> masterTile;
    public boolean updateClientFlag;
    public boolean findTargetPointsFlag;
    public int linkCountDown;
    public int numUsingPlayers;
    private int ticksSinceSync;
    private boolean spawnedMiner;
    public EntityAstroMiner linkedMiner;
    public UUID linkedMinerID;
    
    public TileEntityMinerBase() {
        this.containingItems = new ItemStack[73];
        this.isMaster = false;
        this.targetPoints = new LinkedList<BlockVec3>();
        this.masterTile = null;
        this.linkCountDown = 0;
        this.spawnedMiner = false;
        this.linkedMiner = null;
        this.linkedMinerID = null;
        this.storage.setMaxExtract(ConfigManagerCore.hardMode ? 20.0f : 12.0f);
        this.slotArray = new int[72];
        for (int i = 0; i < 72; ++i) {
            this.slotArray[i] = i + 1;
        }
    }
    
    public void updateEntity() {
        super.updateEntity();
        if (this.updateClientFlag) {
            this.updateClient();
            this.updateClientFlag = false;
        }
        if (this.findTargetPointsFlag) {
            if (this.isMaster && this.linkedMiner != null) {
                this.findTargetPoints();
            }
            this.findTargetPointsFlag = false;
        }
        if (!this.isMaster) {
            final TileEntityMinerBase master = this.getMaster();
            if (master != null) {
                float energyLimit = master.storage.getCapacityGC() - master.storage.getEnergyStoredGC();
                if (energyLimit < 0.0f) {
                    energyLimit = 0.0f;
                }
                this.storage.setCapacity(energyLimit);
                this.storage.setMaxExtract(energyLimit);
                this.storage.setMaxReceive(energyLimit);
                final float hasEnergy = this.getEnergyStoredGC();
                if (hasEnergy > 0.0f) {
                    this.extractEnergyGC((EnergySource)null, master.receiveEnergyGC((EnergySource)null, hasEnergy, false), false);
                }
            }
        }
        if (this.linkCountDown > 0) {
            --this.linkCountDown;
        }
    }
    
    public boolean spawnMiner(final EntityPlayerMP player) {
        if (!this.isMaster) {
            final TileEntityMinerBase master = this.getMaster();
            return master != null && master.spawnMiner(player);
        }
        if (this.linkedMiner != null && this.linkedMiner.isDead) {
            this.unlinkMiner();
        }
        if (this.linkedMinerID == null && EntityAstroMiner.spawnMinerAtBase(this.worldObj, this.xCoord + 1, this.yCoord + 1, this.zCoord + 1, this.facing + 2 ^ 0x1, new BlockVec3((TileEntity)this), player)) {
            this.findTargetPoints();
            return true;
        }
        return false;
    }
    
    public TileEntityMinerBase getMaster() {
        if (this.mainBlockPosition == null) {
            return null;
        }
        if (this.masterTile == null) {
            final TileEntity tileEntity = this.mainBlockPosition.getTileEntity((IBlockAccess)this.worldObj);
            if (tileEntity != null && tileEntity instanceof TileEntityMinerBase) {
                this.masterTile = new WeakReference<TileEntityMinerBase>((TileEntityMinerBase)tileEntity);
            }
        }
        if (this.masterTile == null) {
            this.worldObj.setBlockToAir(this.mainBlockPosition.x, this.mainBlockPosition.y, this.mainBlockPosition.z);
        }
        else {
            final TileEntityMinerBase master = this.masterTile.get();
            if (master != null) {
                return master;
            }
            this.worldObj.removeTileEntity(this.xCoord, this.yCoord, this.zCoord);
        }
        return null;
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.containingItems = this.readStandardItemsFromNBT(nbt);
        if (!(this.isMaster = nbt.getBoolean("isMaster"))) {
            this.mainBlockPosition = BlockVec3.readFromNBT(nbt.getCompoundTag("masterpos"));
        }
        this.facing = nbt.getInteger("facing");
        this.updateClientFlag = true;
        if (nbt.hasKey("LinkedUUIDMost", 4) && nbt.hasKey("LinkedUUIDLeast", 4)) {
            this.linkedMinerID = new UUID(nbt.getLong("LinkedUUIDMost"), nbt.getLong("LinkedUUIDLeast"));
        }
        else {
            this.linkedMinerID = null;
        }
        if (nbt.hasKey("TargetPoints")) {
            this.targetPoints.clear();
            final NBTTagList mpList = nbt.getTagList("TargetPoints", 10);
            for (int j = 0; j < mpList.tagCount(); ++j) {
                final NBTTagCompound bvTag = mpList.getCompoundTagAt(j);
                this.targetPoints.add(BlockVec3.readFromNBT(bvTag));
            }
        }
        else {
            this.findTargetPointsFlag = this.isMaster;
        }
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        this.writeStandardItemsToNBT(nbt);
        nbt.setBoolean("isMaster", this.isMaster);
        if (!this.isMaster && this.mainBlockPosition != null) {
            final NBTTagCompound masterTag = new NBTTagCompound();
            this.mainBlockPosition.writeToNBT(masterTag);
            nbt.setTag("masterpos", (NBTBase)masterTag);
        }
        nbt.setInteger("facing", this.facing);
        if (this.isMaster && this.linkedMinerID != null) {
            nbt.setLong("LinkedUUIDMost", this.linkedMinerID.getMostSignificantBits());
            nbt.setLong("LinkedUUIDLeast", this.linkedMinerID.getLeastSignificantBits());
        }
        final NBTTagList mpList = new NBTTagList();
        for (int j = 0; j < this.targetPoints.size(); ++j) {
            mpList.appendTag((NBTBase)this.targetPoints.get(j).writeToNBT(new NBTTagCompound()));
        }
        nbt.setTag("TargetPoints", (NBTBase)mpList);
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
    
    public boolean addToInventory(final ItemStack itemstack) {
        boolean flag1 = false;
        int k = 1;
        final int invSize = this.getSizeInventory();
        if (itemstack.isStackable()) {
            while (itemstack.stackSize > 0 && k < invSize) {
                final ItemStack existingStack = this.containingItems[k];
                if (existingStack != null && existingStack.getItem() == itemstack.getItem() && (!itemstack.getHasSubtypes() || itemstack.getItemDamage() == existingStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(itemstack, existingStack)) {
                    final int combined = existingStack.stackSize + itemstack.stackSize;
                    if (combined <= itemstack.getMaxStackSize()) {
                        itemstack.stackSize = 0;
                        existingStack.stackSize = combined;
                        flag1 = true;
                    }
                    else if (existingStack.stackSize < itemstack.getMaxStackSize()) {
                        itemstack.stackSize -= itemstack.getMaxStackSize() - existingStack.stackSize;
                        existingStack.stackSize = itemstack.getMaxStackSize();
                        flag1 = true;
                    }
                }
                ++k;
            }
        }
        if (itemstack.stackSize > 0) {
            for (k = 1; k < invSize; ++k) {
                final ItemStack existingStack = this.containingItems[k];
                if (existingStack == null) {
                    this.containingItems[k] = itemstack.copy();
                    itemstack.stackSize = 0;
                    flag1 = true;
                    break;
                }
            }
        }
        this.markDirty();
        return flag1;
    }
    
    public void validate() {
        super.validate();
        if (this.worldObj.isRemote) {
            GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimpleAsteroids(PacketSimpleAsteroids.EnumSimplePacketAsteroids.S_REQUEST_MINERBASE_FACING, new Object[] { this.xCoord, this.yCoord, this.zCoord }));
        }
    }
    
    public void invalidate() {
        super.invalidate();
        this.updateContainingBlockInfo();
    }
    
    public String getInventoryName() {
        return GCCoreUtil.translate("tile.minerBase.name");
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
    
    protected ItemStack[] getContainingItems() {
        if (this.isMaster) {
            return this.containingItems;
        }
        final TileEntityMinerBase master = this.getMaster();
        if (master != null) {
            return master.getContainingItems();
        }
        return this.containingItems;
    }
    
    public boolean shouldUseEnergy() {
        return false;
    }
    
    public void setMainBlockPos(final int x, final int y, final int z) {
        this.masterTile = null;
        if (this.xCoord == x && this.yCoord == y && this.zCoord == z) {
            this.isMaster = true;
            this.mainBlockPosition = null;
            return;
        }
        this.isMaster = false;
        this.mainBlockPosition = new BlockVec3(x, y, z);
        this.markDirty();
    }
    
    public void onBlockRemoval() {
        if (this.isMaster) {
            this.invalidate();
            this.onDestroy((TileEntity)this);
            return;
        }
        final TileEntityMinerBase master = this.getMaster();
        if (master != null && !master.isInvalid()) {
            this.worldObj.func_147480_a(master.xCoord, master.yCoord, master.zCoord, false);
        }
    }
    
    public boolean onActivated(final EntityPlayer entityPlayer) {
        if (!this.isMaster) {
            final TileEntityMinerBase master = this.getMaster();
            return master != null && master.onActivated(entityPlayer);
        }
        final ItemStack holding = entityPlayer.getCurrentEquippedItem();
        if (holding != null && holding.getItem() == AsteroidsItems.astroMiner) {
            return false;
        }
        entityPlayer.openGui((Object)GalacticraftPlanets.instance, 3, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        return true;
    }
    
    public void onCreate(final BlockVec3 placedPosition) {
    }
    
    public void onDestroy(final TileEntity callingBlock) {
        for (int x = 0; x < 2; ++x) {
            for (int y = 0; y < 2; ++y) {
                for (int z = 0; z < 2; ++z) {
                    this.worldObj.func_147480_a(this.xCoord + x, this.yCoord + y, this.zCoord + z, false);
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, (double)(this.xCoord + 2), (double)(this.yCoord + 2), (double)(this.zCoord + 2));
    }
    
    public void updateFacing() {
        if (this.isMaster && this.linkedMinerID == null) {
            switch (this.facing) {
                case 0: {
                    this.facing = 3;
                    break;
                }
                case 3: {
                    this.facing = 1;
                    break;
                }
                case 1: {
                    this.facing = 2;
                    break;
                }
                case 2: {
                    this.facing = 0;
                    break;
                }
            }
            super.updateFacing();
        }
        else {
            final TileEntityMinerBase master = this.getMaster();
            if (master != null) {
                master.updateFacing();
            }
        }
        if (!this.worldObj.isRemote) {
            this.updateClient();
        }
        this.markDirty();
    }
    
    private void updateClient() {
        int x = this.xCoord;
        int y = this.yCoord;
        int z = this.zCoord;
        if (this.mainBlockPosition != null) {
            x = this.mainBlockPosition.x;
            y = this.mainBlockPosition.y;
            z = this.mainBlockPosition.z;
        }
        final int link = (this.linkedMinerID != null) ? 1 : 0;
        GalacticraftCore.packetPipeline.sendToDimension((IPacket)new PacketSimpleAsteroids(PacketSimpleAsteroids.EnumSimplePacketAsteroids.C_UPDATE_MINERBASE_FACING, new Object[] { this.xCoord, this.yCoord, this.zCoord, this.facing, x, y, z, link }), this.worldObj.provider.dimensionId);
    }
    
    public ForgeDirection getElectricInputDirection() {
        if (this.isMaster) {
            return ForgeDirection.getOrientation(this.facing + 2);
        }
        final TileEntityMinerBase master = this.getMaster();
        if (master != null) {
            return ForgeDirection.getOrientation(master.facing + 2);
        }
        return ForgeDirection.UNKNOWN;
    }
    
    public void linkMiner(final EntityAstroMiner entityAstroMiner) {
        this.linkedMiner = entityAstroMiner;
        this.linkedMinerID = this.linkedMiner.getUniqueID();
        this.updateClientFlag = true;
        this.markDirty();
    }
    
    public void unlinkMiner() {
        this.linkedMiner = null;
        this.linkedMinerID = null;
        this.updateClientFlag = true;
        this.markDirty();
    }
    
    public UUID getLinkedMiner() {
        if (this.isMaster) {
            return this.linkedMinerID;
        }
        final TileEntityMinerBase master = this.getMaster();
        if (master != null) {
            return master.linkedMinerID;
        }
        return null;
    }
    
    public int[] getAccessibleSlotsFromSide(final int side) {
        if (this.isMaster) {
            return (side != this.facing + 2) ? this.slotArray : new int[0];
        }
        final TileEntityMinerBase master = this.getMaster();
        if (master != null) {
            return master.getAccessibleSlotsFromSide(side);
        }
        return new int[0];
    }
    
    public boolean canInsertItem(final int slotID, final ItemStack itemstack, final int side) {
        return false;
    }
    
    public boolean canExtractItem(final int slotID, final ItemStack itemstack, final int side) {
        if (this.isMaster) {
            return side != this.facing + 2 && (slotID > 0 || ItemElectricBase.isElectricItemEmpty(itemstack));
        }
        final TileEntityMinerBase master = this.getMaster();
        return master != null && master.canExtractItem(slotID, itemstack, side);
    }
    
    public boolean isItemValidForSlot(final int slotID, final ItemStack itemstack) {
        if (this.isMaster) {
            return slotID > 0 || ItemElectricBase.isElectricItem(itemstack.getItem());
        }
        final TileEntityMinerBase master = this.getMaster();
        return master != null && master.isItemValidForSlot(slotID, itemstack);
    }
    
    public ItemStack getStackInSlot(final int par1) {
        if (this.isMaster) {
            return super.getStackInSlot(par1);
        }
        final TileEntityMinerBase master = this.getMaster();
        if (master != null) {
            return master.getStackInSlot(par1);
        }
        return null;
    }
    
    public ItemStack decrStackSize(final int par1, final int par2) {
        if (this.isMaster) {
            return super.decrStackSize(par1, par2);
        }
        final TileEntityMinerBase master = this.getMaster();
        if (master != null) {
            return master.decrStackSize(par1, par2);
        }
        return null;
    }
    
    public ItemStack getStackInSlotOnClosing(final int par1) {
        if (this.isMaster) {
            return super.getStackInSlotOnClosing(par1);
        }
        final TileEntityMinerBase master = this.getMaster();
        if (master != null) {
            return master.getStackInSlotOnClosing(par1);
        }
        return null;
    }
    
    public void setInventorySlotContents(final int par1, final ItemStack par2ItemStack) {
        if (this.isMaster) {
            super.setInventorySlotContents(par1, par2ItemStack);
            this.markDirty();
            return;
        }
        final TileEntityMinerBase master = this.getMaster();
        if (master != null) {
            master.setInventorySlotContents(par1, par2ItemStack);
        }
    }
    
    public ItemStack getBatteryInSlot() {
        if (this.isMaster) {
            return this.getStackInSlot(0);
        }
        final TileEntityMinerBase master = this.getMaster();
        if (master != null) {
            master.getBatteryInSlot();
        }
        return null;
    }
    
    public int getSizeInventory() {
        return 73;
    }
    
    public BlockVec3 findNextTarget() {
        if (!this.targetPoints.isEmpty()) {
            final BlockVec3 pos = this.targetPoints.removeFirst();
            this.markDirty();
            if (pos != null) {
                return pos.clone();
            }
        }
        return null;
    }
    
    private void findTargetPoints() {
        this.targetPoints.clear();
        final int baseFacing = this.facing + 2 ^ 0x1;
        final BlockVec3 posnTarget = new BlockVec3((TileEntity)this);
        if (this.worldObj.provider instanceof WorldProviderAsteroids) {
            final ArrayList<BlockVec3> roids = (ArrayList<BlockVec3>)((WorldProviderAsteroids)this.worldObj.provider).getClosestAsteroidsXZ(posnTarget.x, posnTarget.y, posnTarget.z, baseFacing, 100);
            if (roids != null && roids.size() > 0) {
                this.targetPoints.addAll(roids);
                return;
            }
        }
        posnTarget.modifyPositionFromSide(ForgeDirection.getOrientation(baseFacing), this.worldObj.rand.nextInt(16) + 32);
        int miny = Math.min(this.yCoord * 2 - 90, this.yCoord - 22);
        if (miny < 5) {
            miny = 5;
        }
        posnTarget.y = miny + 5 + this.worldObj.rand.nextInt(4);
        this.targetPoints.add(posnTarget);
        ForgeDirection lateral = ForgeDirection.NORTH;
        final ForgeDirection inLine = ForgeDirection.getOrientation(baseFacing);
        if ((baseFacing & 0x6) == 0x2) {
            lateral = ForgeDirection.WEST;
        }
        this.targetPoints.add(posnTarget.clone().modifyPositionFromSide(lateral, 13));
        this.targetPoints.add(posnTarget.clone().modifyPositionFromSide(lateral, -13));
        if (posnTarget.y > 17) {
            this.targetPoints.add(posnTarget.clone().modifyPositionFromSide(lateral, 7).modifyPositionFromSide(ForgeDirection.DOWN, 11));
            this.targetPoints.add(posnTarget.clone().modifyPositionFromSide(lateral, -7).modifyPositionFromSide(ForgeDirection.DOWN, 11));
        }
        else {
            this.targetPoints.add(posnTarget.clone().modifyPositionFromSide(lateral, 26));
            this.targetPoints.add(posnTarget.clone().modifyPositionFromSide(lateral, -26));
        }
        this.targetPoints.add(posnTarget.clone().modifyPositionFromSide(lateral, 7).modifyPositionFromSide(ForgeDirection.UP, 11));
        this.targetPoints.add(posnTarget.clone().modifyPositionFromSide(lateral, -7).modifyPositionFromSide(ForgeDirection.UP, 11));
        if (posnTarget.y < this.yCoord - 38) {
            this.targetPoints.add(posnTarget.clone().modifyPositionFromSide(lateral, 13).modifyPositionFromSide(ForgeDirection.UP, 22));
            this.targetPoints.add(posnTarget.clone().modifyPositionFromSide(ForgeDirection.UP, 22));
            this.targetPoints.add(posnTarget.clone().modifyPositionFromSide(lateral, -13).modifyPositionFromSide(ForgeDirection.UP, 22));
        }
        for (int s = this.targetPoints.size(), i = 0; i < s; ++i) {
            this.targetPoints.add(this.targetPoints.get(i).clone().modifyPositionFromSide(inLine, 30));
        }
        this.markDirty();
    }
    
    public void setDisabled(final int index, final boolean disabled) {
        TileEntityMinerBase master;
        if (!this.isMaster) {
            master = this.getMaster();
            if (master == null) {
                return;
            }
        }
        else {
            master = this;
        }
        if (master.linkedMiner != null) {
            master.linkedMiner.recall();
        }
    }
}
