package micdoodle8.mods.galacticraft.core.items;

import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.item.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import java.util.*;
import net.minecraft.nbt.*;
import net.minecraftforge.fluids.*;
import cpw.mods.fml.common.*;

public abstract class ItemCanisterGeneric extends ItemFluidContainer
{
    private String allowedFluid;
    public static final int EMPTY = 1001;
    private static boolean isTELoaded;
    
    public ItemCanisterGeneric(final String assetName) {
        super(0, 1000);
        this.allowedFluid = null;
        this.setMaxDamage(1001);
        this.setMaxStackSize(1);
        this.setNoRepair();
        this.setUnlocalizedName(assetName);
        this.setContainerItem(GCItems.oilCanister);
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubItems(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        par3List.add(new ItemStack(par1, 1, 1));
    }
    
    public ItemStack getContainerItem(final ItemStack itemStack) {
        if (ItemCanisterGeneric.isTELoaded) {
            final StackTraceElement[] st = Thread.currentThread().getStackTrace();
            for (int imax = Math.max(st.length, 5), i = 1; i < imax; ++i) {
                final String ste = st[i].getClassName();
                if (ste.equals("thermalexpansion.block.machine.TileTransposer")) {
                    return null;
                }
            }
        }
        return new ItemStack(this.getContainerItem(), 1, 1001);
    }
    
    public void onUpdate(final ItemStack par1ItemStack, final World par2World, final Entity par3Entity, final int par4, final boolean par5) {
        if (1001 == par1ItemStack.getItemDamage()) {
            if (par1ItemStack.getItem() != GCItems.oilCanister) {
                this.replaceEmptyCanisterItem(par1ItemStack, GCItems.oilCanister);
            }
            par1ItemStack.stackTagCompound = null;
        }
        else if (par1ItemStack.getItemDamage() <= 0) {
            par1ItemStack.setItemDamage(1);
        }
    }
    
    public void setAllowedFluid(final String name) {
        this.allowedFluid = new String(name);
    }
    
    public String getAllowedFluid() {
        return this.allowedFluid;
    }
    
    public int fill(final ItemStack container, final FluidStack resource, final boolean doFill) {
        if (resource == null || resource.getFluid() == null || resource.amount == 0 || container == null || container.getItemDamage() <= 1 || !(container.getItem() instanceof ItemCanisterGeneric)) {
            return 0;
        }
        final String fluidName = resource.getFluid().getName();
        if (container.getItemDamage() == 1001) {
            for (final String key : GalacticraftCore.itemList.keySet()) {
                if (key.contains("CanisterFull")) {
                    final Item i = GalacticraftCore.itemList.get(key).getItem();
                    if (!(i instanceof ItemCanisterGeneric) || !fluidName.equalsIgnoreCase(((ItemCanisterGeneric)i).allowedFluid)) {
                        continue;
                    }
                    if (!doFill) {
                        return Math.min(resource.amount, this.capacity);
                    }
                    this.replaceEmptyCanisterItem(container, i);
                    break;
                }
            }
            container.stackTagCompound = null;
        }
        else {
            container.stackTagCompound = null;
            super.fill(container, this.getFluid(container), true);
        }
        if (fluidName.equalsIgnoreCase(((ItemCanisterGeneric)container.getItem()).allowedFluid)) {
            final int added = super.fill(container, resource, doFill);
            if (doFill && added > 0) {
                container.setItemDamage(Math.max(1, container.getItemDamage() - added));
            }
            return added;
        }
        return 0;
    }
    
    public FluidStack drain(final ItemStack container, final int maxDrain, final boolean doDrain) {
        if (this.allowedFluid == null || container.getItemDamage() >= 1001) {
            return null;
        }
        container.stackTagCompound = null;
        super.fill(container, this.getFluid(container), true);
        final FluidStack used = super.drain(container, maxDrain, doDrain);
        if (doDrain && used != null && used.amount > 0) {
            this.setNewDamage(container, container.getItemDamage() + used.amount);
        }
        return used;
    }
    
    protected void setNewDamage(final ItemStack container, int newDamage) {
        newDamage = Math.min(newDamage, 1001);
        if (newDamage == 1001) {
            container.stackTagCompound = null;
            if (container.getItem() != GCItems.oilCanister) {
                this.replaceEmptyCanisterItem(container, GCItems.oilCanister);
                return;
            }
        }
        container.setItemDamage(newDamage);
    }
    
    private void replaceEmptyCanisterItem(final ItemStack container, final Item newItem) {
        final int stackSize = container.stackSize;
        final NBTTagCompound tag = new NBTTagCompound();
        tag.setShort("id", (short)Item.getIdFromItem(newItem));
        tag.setByte("Count", (byte)stackSize);
        tag.setShort("Damage", (short)1001);
        container.readFromNBT(tag);
    }
    
    public FluidStack getFluid(final ItemStack container) {
        final String fluidName = ((ItemCanisterGeneric)container.getItem()).allowedFluid;
        if (fluidName == null || 1001 == container.getItemDamage()) {
            return null;
        }
        final Fluid fluid = FluidRegistry.getFluid(fluidName);
        if (fluid == null) {
            return null;
        }
        return new FluidStack(fluid, 1001 - container.getItemDamage());
    }
    
    static {
        ItemCanisterGeneric.isTELoaded = Loader.isModLoaded("ThermalExpansion");
    }
}
