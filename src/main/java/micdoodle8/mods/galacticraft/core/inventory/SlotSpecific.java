package micdoodle8.mods.galacticraft.core.inventory;

import net.minecraft.item.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.api.item.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import java.util.*;

public class SlotSpecific extends Slot
{
    public ItemStack[] validItemStacks;
    public Class[] validClasses;
    public boolean isInverted;
    public boolean isMetadataSensitive;
    
    public SlotSpecific(final IInventory par2IInventory, final int par3, final int par4, final int par5, final ItemStack... itemStacks) {
        super(par2IInventory, par3, par4, par5);
        this.validItemStacks = new ItemStack[0];
        this.validClasses = new Class[0];
        this.isInverted = false;
        this.isMetadataSensitive = false;
        this.setItemStacks(itemStacks);
    }
    
    public SlotSpecific(final IInventory par2IInventory, final int par3, final int par4, final int par5, Class... validClasses) {
        super(par2IInventory, par3, par4, par5);
        this.validItemStacks = new ItemStack[0];
        this.validClasses = new Class[0];
        this.isInverted = false;
        this.isMetadataSensitive = false;
        if (validClasses != null && Arrays.asList((Class[])validClasses).contains(IItemElectric.class)) {
            if (EnergyConfigHandler.isRFAPILoaded()) {
                try {
                    final Class<?> itemElectricRF = Class.forName("cofh.api.energy.IEnergyContainerItem");
                    final ArrayList<Class> existing = new ArrayList<Class>(Arrays.asList((Class[])validClasses));
                    existing.add(itemElectricRF);
                    validClasses = existing.toArray(new Class[existing.size()]);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (EnergyConfigHandler.isIndustrialCraft2Loaded()) {
                try {
                    final Class<?> itemElectricIC2a = Class.forName("ic2.api.item.IElectricItem");
                    final Class<?> itemElectricIC2b = Class.forName("ic2.api.item.ISpecialElectricItem");
                    final ArrayList<Class> existing2 = new ArrayList<Class>(Arrays.asList((Class[])validClasses));
                    existing2.add(itemElectricIC2a);
                    existing2.add(itemElectricIC2b);
                    validClasses = existing2.toArray(new Class[existing2.size()]);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (EnergyConfigHandler.isMekanismLoaded()) {
                try {
                    final Class<?> itemElectricMek = Class.forName("mekanism.api.energy.IEnergizedItem");
                    final ArrayList<Class> existing = new ArrayList<Class>(Arrays.asList((Class[])validClasses));
                    existing.add(itemElectricMek);
                    validClasses = existing.toArray(new Class[existing.size()]);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        this.setClasses(validClasses);
    }
    
    public SlotSpecific setMetadataSensitive() {
        this.isMetadataSensitive = true;
        return this;
    }
    
    public SlotSpecific setItemStacks(final ItemStack... validItemStacks) {
        this.validItemStacks = validItemStacks;
        return this;
    }
    
    public SlotSpecific setClasses(final Class... validClasses) {
        this.validClasses = validClasses;
        return this;
    }
    
    public SlotSpecific toggleInverted() {
        this.isInverted = !this.isInverted;
        return this;
    }
    
    public boolean isItemValid(final ItemStack compareStack) {
        boolean returnValue = false;
        for (final ItemStack itemStack : this.validItemStacks) {
            if (compareStack.isItemEqual(itemStack) || (!this.isMetadataSensitive && compareStack == itemStack)) {
                returnValue = true;
                break;
            }
        }
        if (!returnValue) {
            for (final Class clazz : this.validClasses) {
                if (clazz.isInstance(compareStack.getItem())) {
                    returnValue = true;
                    break;
                }
            }
        }
        if (this.isInverted) {
            return !returnValue;
        }
        return returnValue;
    }
}
