package micdoodle8.mods.galacticraft.planets.mars.tile;

import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.api.tile.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import micdoodle8.mods.miccore.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.block.*;
import net.minecraft.block.*;
import java.util.*;
import io.netty.buffer.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.energy.item.*;
import net.minecraft.init.*;
import micdoodle8.mods.galacticraft.planets.mars.inventory.*;
import net.minecraftforge.common.util.*;
import net.minecraftforge.fluids.*;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;

public class TileEntityTerraformer extends TileBaseElectricBlockWithInventory implements ISidedInventory, IDisableableMachine, IBubbleProvider, IFluidHandler
{
    private final int tankCapacity = 2000;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public FluidTank waterTank;
    public boolean active;
    public boolean lastActive;
    public static final int WATTS_PER_TICK = 1;
    private ItemStack[] containingItems;
    private ArrayList<BlockVec3> terraformableBlocksList;
    private ArrayList<BlockVec3> grassBlockList;
    private ArrayList<BlockVec3> grownTreesList;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int terraformableBlocksListSize;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public int grassBlocksListSize;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean treesDisabled;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean grassDisabled;
    public final double MAX_SIZE = 15.0;
    private int[] useCount;
    private int saplingIndex;
    public float bubbleSize;
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    public boolean shouldRenderBubble;
    
    public TileEntityTerraformer() {
        this.getClass();
        this.waterTank = new FluidTank(2000);
        this.containingItems = new ItemStack[14];
        this.terraformableBlocksList = new ArrayList<BlockVec3>();
        this.grassBlockList = new ArrayList<BlockVec3>();
        this.grownTreesList = new ArrayList<BlockVec3>();
        this.terraformableBlocksListSize = 0;
        this.grassBlocksListSize = 0;
        this.useCount = new int[2];
        this.saplingIndex = 6;
        this.shouldRenderBubble = true;
        this.storage.setMaxExtract(ConfigManagerCore.hardMode ? 90.0f : 45.0f);
    }
    
    public int getScaledWaterLevel(final int i) {
        final double fuelLevel = (this.waterTank.getFluid() == null) ? 0.0 : this.waterTank.getFluid().amount;
        final double n = fuelLevel * i;
        this.getClass();
        return (int)(n / 2000.0);
    }
    
    public void invalidate() {
        super.invalidate();
    }
    
    public double getDistanceFromServer(final double par1, final double par3, final double par5) {
        final double d3 = this.xCoord + 0.5 - par1;
        final double d4 = this.yCoord + 0.5 - par3;
        final double d5 = this.zCoord + 0.5 - par5;
        return d3 * d3 + d4 * d4 + d5 * d5;
    }
    
    public void updateEntity() {
        super.updateEntity();
        if (!this.worldObj.isRemote) {
            if (this.containingItems[0] != null) {
                final FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(this.containingItems[0]);
                if (liquid != null && liquid.getFluid().getName().equals(FluidRegistry.WATER.getName()) && (this.waterTank.getFluid() == null || this.waterTank.getFluid().amount + liquid.amount <= this.waterTank.getCapacity())) {
                    this.waterTank.fill(liquid, true);
                    this.containingItems[0] = FluidUtil.getUsedContainer(this.containingItems[0]);
                }
            }
            final double n = this.bubbleSize;
            this.getClass();
            this.active = (n == 15.0 && this.hasEnoughEnergyToRun && this.getFirstBonemealStack() != null && this.waterTank.getFluid() != null && this.waterTank.getFluid().amount > 0);
        }
        if (!this.worldObj.isRemote && (this.active != this.lastActive || this.ticks % 60 == 0)) {
            this.terraformableBlocksList.clear();
            this.grassBlockList.clear();
            if (this.active) {
                final int bubbleSize = (int)Math.ceil(this.bubbleSize);
                double bubbleSizeSq = this.bubbleSize;
                bubbleSizeSq *= bubbleSizeSq;
                final boolean doGrass = !this.grassDisabled && this.getFirstSeedStack() != null;
                final boolean doTrees = !this.treesDisabled && this.getFirstSaplingStack() != null;
                for (int x = this.xCoord - bubbleSize; x < this.xCoord + bubbleSize; ++x) {
                    for (int y = this.yCoord - bubbleSize; y < this.yCoord + bubbleSize; ++y) {
                        for (int z = this.zCoord - bubbleSize; z < this.zCoord + bubbleSize; ++z) {
                            final Block blockID = this.worldObj.getBlock(x, y, z);
                            if (blockID != null) {
                                if (!blockID.isAir((IBlockAccess)this.worldObj, x, y, z) && this.getDistanceFromServer(x, y, z) < bubbleSizeSq) {
                                    if (doGrass && blockID instanceof ITerraformableBlock && ((ITerraformableBlock)blockID).isTerraformable(this.worldObj, x, y, z)) {
                                        this.terraformableBlocksList.add(new BlockVec3(x, y, z));
                                    }
                                    else if (doTrees) {
                                        final Block blockIDAbove = this.worldObj.getBlock(x, y + 1, z);
                                        if (blockID == Blocks.grass && (blockIDAbove == null || blockIDAbove.isAir((IBlockAccess)this.worldObj, x, y + 1, z))) {
                                            this.grassBlockList.add(new BlockVec3(x, y, z));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!this.worldObj.isRemote && this.terraformableBlocksList.size() > 0 && this.ticks % 15 == 0) {
            final ArrayList<BlockVec3> terraformableBlocks2 = new ArrayList<BlockVec3>(this.terraformableBlocksList);
            final int randomIndex = this.worldObj.rand.nextInt(this.terraformableBlocksList.size());
            final BlockVec3 vec = terraformableBlocks2.get(randomIndex);
            if (vec.getBlock((IBlockAccess)this.worldObj) instanceof ITerraformableBlock) {
                Block id = null;
                switch (this.worldObj.rand.nextInt(40)) {
                    case 0: {
                        if (this.worldObj.func_147469_q(vec.x - 1, vec.y, vec.z) && this.worldObj.func_147469_q(vec.x + 1, vec.y, vec.z) && this.worldObj.func_147469_q(vec.x, vec.y, vec.z - 1) && this.worldObj.func_147469_q(vec.x, vec.y, vec.z + 1)) {
                            id = (Block)Blocks.flowing_water;
                            break;
                        }
                        id = (Block)Blocks.grass;
                        break;
                    }
                    default: {
                        id = (Block)Blocks.grass;
                        break;
                    }
                }
                this.worldObj.setBlock(vec.x, vec.y, vec.z, id);
                if (id == Blocks.grass) {
                    final int[] useCount = this.useCount;
                    final int n2 = 0;
                    ++useCount[n2];
                    this.waterTank.drain(1, true);
                    this.checkUsage(1);
                }
                else if (id == Blocks.flowing_water) {
                    this.checkUsage(2);
                }
            }
            this.terraformableBlocksList.remove(randomIndex);
        }
        if (!this.worldObj.isRemote && !this.treesDisabled && this.grassBlockList.size() > 0 && this.ticks % 50 == 0) {
            final int randomIndex2 = this.worldObj.rand.nextInt(this.grassBlockList.size());
            final BlockVec3 vecGrass = this.grassBlockList.get(randomIndex2);
            if (vecGrass.getBlock((IBlockAccess)this.worldObj) == Blocks.grass) {
                final BlockVec3 vecSapling = vecGrass.translate(0, 1, 0);
                final ItemStack sapling = this.getFirstSaplingStack();
                boolean flag = false;
                for (final BlockVec3 testVec : this.grownTreesList) {
                    if (testVec.distanceSquared(vecSapling) < 9) {
                        flag = true;
                        break;
                    }
                }
                if (!flag && sapling != null) {
                    final Block b = Block.getBlockFromItem(sapling.getItem());
                    this.worldObj.setBlock(vecSapling.x, vecSapling.y, vecSapling.z, b, sapling.getItemDamage(), 3);
                    if (b instanceof BlockSapling) {
                        if (this.worldObj.getBlockLightValue(vecSapling.x, vecSapling.y, vecSapling.z) >= 9) {
                            ((BlockSapling)b).func_149878_d(this.worldObj, vecSapling.x, vecSapling.y, vecSapling.z, this.worldObj.rand);
                            this.grownTreesList.add(vecSapling.clone());
                        }
                    }
                    else if (b instanceof BlockBush && this.worldObj.getBlockLightValue(vecSapling.x, vecSapling.y, vecSapling.z) >= 5) {
                        for (int j = 0; j < 12; ++j) {
                            if (this.worldObj.getBlock(vecSapling.x, vecSapling.y, vecSapling.z) != b) {
                                this.grownTreesList.add(vecSapling.clone());
                                break;
                            }
                            ((BlockBush)b).updateTick(this.worldObj, vecSapling.x, vecSapling.y, vecSapling.z, this.worldObj.rand);
                        }
                    }
                    final int[] useCount2 = this.useCount;
                    final int n3 = 1;
                    ++useCount2[n3];
                    this.waterTank.drain(50, true);
                    this.checkUsage(0);
                }
            }
            this.grassBlockList.remove(randomIndex2);
        }
        if (!this.worldObj.isRemote) {
            this.terraformableBlocksListSize = this.terraformableBlocksList.size();
            this.grassBlocksListSize = this.grassBlockList.size();
        }
        if (this.hasEnoughEnergyToRun && (!this.grassDisabled || !this.treesDisabled)) {
            final double n4 = Math.max(0.0f, this.bubbleSize + 0.1f);
            this.getClass();
            this.bubbleSize = (float)Math.min(n4, 15.0);
        }
        else {
            final double n5 = Math.max(0.0f, this.bubbleSize - 0.1f);
            this.getClass();
            this.bubbleSize = (float)Math.min(n5, 15.0);
        }
        this.lastActive = this.active;
    }
    
    public void addExtraNetworkedData(final List<Object> networkedList) {
        if (!this.worldObj.isRemote) {
            networkedList.add(this.bubbleSize);
        }
    }
    
    public void readExtraNetworkedData(final ByteBuf dataStream) {
        if (this.worldObj.isRemote) {
            this.bubbleSize = dataStream.readFloat();
        }
    }
    
    private void checkUsage(final int type) {
        ItemStack stack = null;
        if ((this.useCount[0] + this.useCount[1]) % 4 == 0) {
            stack = this.getFirstBonemealStack();
            if (stack != null) {
                final ItemStack itemStack = stack;
                --itemStack.stackSize;
                if (stack.stackSize <= 0) {
                    this.containingItems[this.getSelectiveStack(2, 6)] = null;
                }
            }
        }
        switch (type) {
            case 0: {
                stack = this.containingItems[this.saplingIndex];
                if (stack == null) {
                    break;
                }
                final ItemStack itemStack2 = stack;
                --itemStack2.stackSize;
                if (stack.stackSize <= 0) {
                    this.containingItems[this.saplingIndex] = null;
                    break;
                }
                break;
            }
            case 1: {
                if (this.useCount[0] % 4 != 0) {
                    break;
                }
                stack = this.getFirstSeedStack();
                if (stack == null) {
                    break;
                }
                final ItemStack itemStack3 = stack;
                --itemStack3.stackSize;
                if (stack.stackSize <= 0) {
                    this.containingItems[this.getSelectiveStack(10, 14)] = null;
                    break;
                }
                break;
            }
            case 2: {
                this.waterTank.drain(50, true);
                break;
            }
        }
    }
    
    private int getSelectiveStack(final int start, final int end) {
        for (int i = start; i < end; ++i) {
            final ItemStack stack = this.containingItems[i];
            if (stack != null) {
                return i;
            }
        }
        return -1;
    }
    
    private int getRandomStack(final int start, final int end) {
        int stackcount = 0;
        for (int i = start; i < end; ++i) {
            if (this.containingItems[i] != null) {
                ++stackcount;
            }
        }
        if (stackcount == 0) {
            return -1;
        }
        int random = this.worldObj.rand.nextInt(stackcount);
        for (int j = start; j < end; ++j) {
            if (this.containingItems[j] != null) {
                if (random == 0) {
                    return j;
                }
                --random;
            }
        }
        return -1;
    }
    
    public ItemStack getFirstBonemealStack() {
        final int index = this.getSelectiveStack(2, 6);
        if (index != -1) {
            return this.containingItems[index];
        }
        return null;
    }
    
    public ItemStack getFirstSaplingStack() {
        final int index = this.getRandomStack(6, 10);
        if (index != -1) {
            this.saplingIndex = index;
            return this.containingItems[index];
        }
        return null;
    }
    
    public ItemStack getFirstSeedStack() {
        final int index = this.getSelectiveStack(10, 14);
        if (index != -1) {
            return this.containingItems[index];
        }
        return null;
    }
    
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.containingItems = this.readStandardItemsFromNBT(nbt);
        this.bubbleSize = nbt.getFloat("BubbleSize");
        this.useCount = nbt.getIntArray("UseCountArray");
        if (this.useCount.length == 0) {
            this.useCount = new int[2];
        }
        if (nbt.hasKey("waterTank")) {
            this.waterTank.readFromNBT(nbt.getCompoundTag("waterTank"));
        }
        if (nbt.hasKey("bubbleVisible")) {
            this.setBubbleVisible(nbt.getBoolean("bubbleVisible"));
        }
    }
    
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        this.writeStandardItemsToNBT(nbt);
        nbt.setFloat("BubbleSize", this.bubbleSize);
        nbt.setIntArray("UseCountArray", this.useCount);
        if (this.waterTank.getFluid() != null) {
            nbt.setTag("waterTank", (NBTBase)this.waterTank.writeToNBT(new NBTTagCompound()));
        }
        nbt.setBoolean("bubbleVisible", this.shouldRenderBubble);
    }
    
    public ItemStack[] getContainingItems() {
        return this.containingItems;
    }
    
    public String getInventoryName() {
        return GCCoreUtil.translate("container.tileTerraformer.name");
    }
    
    public int[] getAccessibleSlotsFromSide(final int side) {
        return new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 };
    }
    
    public boolean canInsertItem(final int slotID, final ItemStack itemstack, final int side) {
        return this.isItemValidForSlot(slotID, itemstack);
    }
    
    public boolean canExtractItem(final int slotID, final ItemStack itemstack, final int side) {
        if (slotID == 0) {
            return FluidContainerRegistry.isEmptyContainer(itemstack);
        }
        return slotID == 1 && ItemElectricBase.isElectricItemEmpty(itemstack);
    }
    
    public boolean hasCustomInventoryName() {
        return true;
    }
    
    public boolean isItemValidForSlot(final int slotID, final ItemStack itemstack) {
        switch (slotID) {
            case 0: {
                return FluidContainerRegistry.containsFluid(itemstack, new FluidStack(FluidRegistry.WATER, 1));
            }
            case 1: {
                return ItemElectricBase.isElectricItem(itemstack.getItem());
            }
            case 2:
            case 3:
            case 4:
            case 5: {
                return itemstack.getItem() == Items.dye && itemstack.getItemDamage() == 15;
            }
            case 6:
            case 7:
            case 8:
            case 9: {
                return ContainerTerraformer.isOnSaplingList(itemstack);
            }
            case 10:
            case 11:
            case 12:
            case 13: {
                return itemstack.getItem() == Items.wheat_seeds;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean shouldUseEnergy() {
        return !this.grassDisabled || !this.treesDisabled;
    }
    
    public ItemStack getBatteryInSlot() {
        return this.getStackInSlot(1);
    }
    
    public void setDisabled(final int index, final boolean disabled) {
        if (this.disableCooldown <= 0) {
            switch (index) {
                case 0: {
                    this.treesDisabled = !this.treesDisabled;
                    break;
                }
                case 1: {
                    this.grassDisabled = !this.grassDisabled;
                    break;
                }
            }
            this.disableCooldown = 10;
        }
    }
    
    public boolean getDisabled(final int index) {
        switch (index) {
            case 0: {
                return this.treesDisabled;
            }
            case 1: {
                return this.grassDisabled;
            }
            default: {
                return false;
            }
        }
    }
    
    public void setBubbleVisible(final boolean shouldRender) {
        this.shouldRenderBubble = shouldRender;
    }
    
    public double getPacketRange() {
        return 64.0;
    }
    
    public boolean canDrain(final ForgeDirection from, final Fluid fluid) {
        return false;
    }
    
    public FluidStack drain(final ForgeDirection from, final FluidStack resource, final boolean doDrain) {
        return null;
    }
    
    public FluidStack drain(final ForgeDirection from, final int maxDrain, final boolean doDrain) {
        return null;
    }
    
    public boolean canFill(final ForgeDirection from, final Fluid fluid) {
        return fluid != null && "water".equals(fluid.getName());
    }
    
    public int fill(final ForgeDirection from, final FluidStack resource, final boolean doFill) {
        int used = 0;
        if (resource != null && this.canFill(from, resource.getFluid())) {
            used = this.waterTank.fill(resource, doFill);
        }
        return used;
    }
    
    public FluidTankInfo[] getTankInfo(final ForgeDirection from) {
        return new FluidTankInfo[] { new FluidTankInfo((IFluidTank)this.waterTank) };
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox((double)(this.xCoord - this.bubbleSize), (double)(this.yCoord - this.bubbleSize), (double)(this.zCoord - this.bubbleSize), (double)(this.xCoord + this.bubbleSize), (double)(this.yCoord + this.bubbleSize), (double)(this.zCoord + this.bubbleSize));
    }
    
    public float getBubbleSize() {
        return this.bubbleSize;
    }
    
    public boolean getBubbleVisible() {
        return this.shouldRenderBubble;
    }
}
