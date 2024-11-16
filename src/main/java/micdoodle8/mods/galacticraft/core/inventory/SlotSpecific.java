package micdoodle8.mods.galacticraft.core.inventory;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import micdoodle8.mods.galacticraft.api.item.IItemElectric;
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler;

/**
 * Creates a slot with a specific amount of items that matches the slot's requirements. Allows easy shift right clicking
 * management and slot blocking in classes. In your container you can use this.getSlot(i).isItemValid to justify the
 * player's shift clicking actions to match the slot.
 *
 * @author Calclavia
 */
public class SlotSpecific extends Slot {

    public ItemStack[] validItemStacks = {};

    public Class<?>[] validClasses = new Class[0];

    public boolean isInverted = false;
    public boolean isMetadataSensitive = false;

    public SlotSpecific(IInventory par2IInventory, int par3, int par4, int par5, ItemStack... itemStacks) {
        super(par2IInventory, par3, par4, par5);
        this.setItemStacks(itemStacks);
    }

    public SlotSpecific(IInventory par2IInventory, int par3, int par4, int par5, Class<?>... validClasses) {
        super(par2IInventory, par3, par4, par5);
        if (validClasses != null && Arrays.asList(validClasses)
            .contains(IItemElectric.class)) {
            if (EnergyConfigHandler.isRFAPILoaded()) {
                try {
                    final Class<?> itemElectricRF = Class.forName("cofh.api.energy.IEnergyContainerItem");
                    final ArrayList<Class<?>> existing = new ArrayList<>(Arrays.asList(validClasses));
                    existing.add(itemElectricRF);
                    validClasses = existing.toArray(new Class[existing.size()]);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
            if (EnergyConfigHandler.isMekanismLoaded()) {
                try {
                    final Class<?> itemElectricMek = Class.forName("mekanism.api.energy.IEnergizedItem");
                    final ArrayList<Class<?>> existing = new ArrayList<>(Arrays.asList(validClasses));
                    existing.add(itemElectricMek);
                    validClasses = existing.toArray(new Class[existing.size()]);
                } catch (final Exception e) {
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

    public SlotSpecific setItemStacks(ItemStack... validItemStacks) {
        this.validItemStacks = validItemStacks;
        return this;
    }

    public SlotSpecific setClasses(Class<?>... validClasses) {
        this.validClasses = validClasses;
        return this;
    }

    public SlotSpecific toggleInverted() {
        this.isInverted = !this.isInverted;
        return this;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    @Override
    public boolean isItemValid(ItemStack compareStack) {
        boolean returnValue = false;

        for (final ItemStack itemStack : this.validItemStacks) {
            if (compareStack.isItemEqual(itemStack) || !this.isMetadataSensitive && compareStack == itemStack) {
                returnValue = true;
                break;
            }
        }

        if (!returnValue) {
            for (final Class<?> clazz : this.validClasses) {
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
