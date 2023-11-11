package micdoodle8.mods.galacticraft.planets.mars.inventory;

import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import net.minecraft.init.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.entity.player.*;
import java.util.*;
import net.minecraft.block.*;
import net.minecraft.item.*;

public class ContainerTerraformer extends Container
{
    private final TileEntityTerraformer tileEntity;
    private static LinkedList<ItemStack> saplingList;

    public ContainerTerraformer(final InventoryPlayer par1InventoryPlayer, final TileEntityTerraformer tileEntity) {
        this.tileEntity = tileEntity;
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)tileEntity, 0, 25, 19, new ItemStack[] { new ItemStack(Items.water_bucket), new ItemStack(Items.bucket) }));
        this.addSlotToContainer((Slot)new SlotSpecific((IInventory)tileEntity, 1, 25, 39, new Class[] { IItemElectric.class }));
        for (int var6 = 0; var6 < 3; ++var6) {
            final List<ItemStack> stacks = new ArrayList<ItemStack>();
            if (var6 == 0) {
                stacks.add(new ItemStack(Items.dye, 1, 15));
            }
            else if (var6 == 1) {
                if (ContainerTerraformer.saplingList == null) {
                    initSaplingList();
                }
                stacks.addAll(ContainerTerraformer.saplingList);
            }
            else if (var6 == 2) {
                stacks.add(new ItemStack(Items.wheat_seeds));
            }
            for (int var7 = 0; var7 < 4; ++var7) {
                this.addSlotToContainer((Slot)new SlotSpecific((IInventory)tileEntity, var7 + var6 * 4 + 2, 25 + var7 * 18, 63 + var6 * 24, (ItemStack[])stacks.toArray(new ItemStack[stacks.size()])));
            }
        }
        for (int var6 = 0; var6 < 3; ++var6) {
            for (int var7 = 0; var7 < 9; ++var7) {
                this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var7 + var6 * 9 + 9, 8 + var7 * 18, 155 + var6 * 18));
            }
        }
        for (int var6 = 0; var6 < 9; ++var6) {
            this.addSlotToContainer(new Slot((IInventory)par1InventoryPlayer, var6, 8 + var6 * 18, 213));
        }
        tileEntity.openInventory();
    }

    public void onContainerClosed(final EntityPlayer entityplayer) {
        super.onContainerClosed(entityplayer);
        this.tileEntity.closeInventory();
    }

    public boolean canInteractWith(final EntityPlayer par1EntityPlayer) {
        return this.tileEntity.isUseableByPlayer(par1EntityPlayer);
    }

    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int par1) {
        ItemStack var2 = null;
        final Slot slot = (Slot) this.inventorySlots.get(par1);
        final int b = this.inventorySlots.size();
        if (slot != null && slot.getHasStack()) {
            final ItemStack var3 = slot.getStack();
            var2 = var3.copy();
            if (par1 < b - 36) {
                if (!this.mergeItemStack(var3, b - 36, b, true)) {
                    return null;
                }
            }
            else if (var3.getItem() instanceof IItemElectric) {
                if (!this.mergeItemStack(var3, 1, 2, false)) {
                    return null;
                }
            }
            else if (var3.getItem() == Items.water_bucket) {
                if (!this.mergeItemStack(var3, 0, 1, false)) {
                    return null;
                }
            }
            else if (var3.getItem() == Items.dye && var3.getItemDamage() == 15) {
                if (!this.mergeItemStack(var3, 2, 6, false)) {
                    return null;
                }
            }
            else if (this.getSlot(6).isItemValid(var3)) {
                if (!this.mergeItemStack(var3, 6, 10, false)) {
                    return null;
                }
            }
            else if (var3.getItem() == Items.wheat_seeds) {
                if (!this.mergeItemStack(var3, 10, 14, false)) {
                    return null;
                }
            }
            else if (par1 < b - 9) {
                if (!this.mergeItemStack(var3, b - 9, b, false)) {
                    return null;
                }
            }
            else if (!this.mergeItemStack(var3, b - 36, b - 9, false)) {
                return null;
            }
            if (var3.stackSize == 0) {
                slot.putStack((ItemStack)null);
            }
            else {
                slot.onSlotChanged();
            }
            if (var3.stackSize == var2.stackSize) {
                return null;
            }
            slot.onPickupFromSlot(par1EntityPlayer, var3);
        }
        return var2;
    }

    public static boolean isOnSaplingList(final ItemStack stack) {
        if (ContainerTerraformer.saplingList == null) {
            initSaplingList();
        }
        for (final ItemStack sapling : ContainerTerraformer.saplingList) {
            if (sapling.isItemEqual(stack)) {
                return true;
            }
        }
        return false;
    }

    private static void initSaplingList() {
        ContainerTerraformer.saplingList = new LinkedList<ItemStack>();
        final Iterator iterator = Block.blockRegistry.getKeys().iterator();
        while (iterator.hasNext()) {
            final Block b = (Block)Block.blockRegistry.getObject((String)iterator.next());
            if (b instanceof BlockBush) {
                try {
                    final Item item = Item.getItemFromBlock(b);
                    if (item == null) {
                        continue;
                    }
                    ContainerTerraformer.saplingList.add(new ItemStack(item, 1, 0));
                    final String basicName = item.getUnlocalizedName(new ItemStack(item, 1, 0));
                    for (int i = 1; i < 16; ++i) {
                        final ItemStack testStack = new ItemStack(item, 1, i);
                        final String testName = item.getUnlocalizedName(testStack);
                        if (testName == null || testName.equals("")) {
                            break;
                        }
                        if (testName.equals(basicName)) {
                            break;
                        }
                        ContainerTerraformer.saplingList.add(testStack);
                    }
                }
                catch (Exception ex) {}
            }
        }
    }

    static {
        ContainerTerraformer.saplingList = null;
    }
}
