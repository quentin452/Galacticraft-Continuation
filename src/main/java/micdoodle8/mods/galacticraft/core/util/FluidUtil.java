package micdoodle8.mods.galacticraft.core.util;

import java.lang.reflect.*;
import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.planets.asteroids.items.*;
import net.minecraft.item.*;
import net.minecraftforge.fluids.*;
import net.minecraft.init.*;

public class FluidUtil
{
    private static boolean oldFluidIDMethod;
    private static Class<?> fluidStackClass;
    private static Method getFluidMethod;
    private static Field fluidIdField;
    
    public static boolean isFuelContainerAny(final ItemStack var4) {
        if (var4.getItem() instanceof ItemCanisterGeneric) {
            return var4.getItem() == GCItems.fuelCanister && var4.getItemDamage() < var4.getMaxDamage();
        }
        final FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(var4);
        return liquid != null && testFuel(FluidRegistry.getFluidName(liquid));
    }
    
    public static boolean testFuel(final String name) {
        return name.startsWith("fuel") || (name.contains("rocket") && name.contains("fuel")) || name.equals("rc jet fuel");
    }
    
    public static int fillWithGCFuel(final FluidTank tank, final FluidStack liquid, final boolean doFill) {
        if (liquid != null && testFuel(FluidRegistry.getFluidName(liquid))) {
            final FluidStack liquidInTank = tank.getFluid();
            if (liquidInTank == null) {
                return tank.fill(new FluidStack(GalacticraftCore.fluidFuel, liquid.amount), doFill);
            }
            if (liquidInTank.amount < tank.getCapacity()) {
                return tank.fill(new FluidStack(liquidInTank, liquid.amount), doFill);
            }
        }
        return 0;
    }
    
    public static boolean isOilContainerAny(final ItemStack var4) {
        if (var4.getItem() instanceof ItemCanisterGeneric) {
            return var4.getItem() == GCItems.oilCanister && var4.getItemDamage() < var4.getMaxDamage();
        }
        final FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(var4);
        return liquid != null && FluidRegistry.getFluidName(liquid).startsWith("oil");
    }
    
    public static boolean isMethaneContainerAny(final ItemStack var4) {
        if (var4.getItem() instanceof ItemCanisterGeneric) {
            return var4.getItem() == AsteroidsItems.methaneCanister && var4.getItemDamage() < var4.getMaxDamage();
        }
        final FluidStack stack = FluidContainerRegistry.getFluidForFilledItem(var4);
        return stack != null && stack.getFluid() != null && stack.getFluid().getName().toLowerCase().contains("methane");
    }
    
    public static boolean isFullContainer(final ItemStack var4) {
        if (var4.getItem() instanceof ItemCanisterGeneric) {
            return var4.getItemDamage() == 1;
        }
        final FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(var4);
        return liquid != null;
    }
    
    public static void tryFillContainer(final FluidTank tank, final FluidStack liquid, final ItemStack[] inventory, final int slot, final Item canisterType) {
        final ItemStack slotItem = inventory[slot];
        final boolean isCanister = slotItem.getItem() instanceof ItemCanisterGeneric;
        final int amountToFill = Math.min(liquid.amount, isCanister ? (slotItem.getItemDamage() - 1) : 1000);
        if (amountToFill <= 0 || (isCanister && slotItem.getItem() != canisterType && slotItem.getItemDamage() != 1001)) {
            return;
        }
        if (isCanister) {
            inventory[slot] = new ItemStack(canisterType, 1, slotItem.getItemDamage() - amountToFill);
            tank.drain(amountToFill, true);
        }
        else if (amountToFill == 1000) {
            inventory[slot] = FluidContainerRegistry.fillFluidContainer(liquid, inventory[slot]);
            if (inventory[slot] == null) {
                inventory[slot] = slotItem;
            }
            else {
                tank.drain(amountToFill, true);
            }
        }
    }
    
    public static void tryFillContainerFuel(final FluidTank tank, final ItemStack[] inventory, final int slot) {
        if (isValidContainer(inventory[slot])) {
            FluidStack liquid = tank.getFluid();
            if (liquid != null && liquid.amount > 0) {
                final String liquidname = liquid.getFluid().getName();
                if (liquidname.startsWith("fuel")) {
                    if (!liquidname.equals(GalacticraftCore.fluidFuel.getName())) {
                        liquid = new FluidStack(GalacticraftCore.fluidFuel, liquid.amount);
                    }
                    final ItemStack stack = inventory[slot];
                    if (stack.getItem() instanceof IFluidContainerItem) {
                        final FluidStack existingFluid = ((IFluidContainerItem)stack.getItem()).getFluid(stack);
                        if (existingFluid != null && !existingFluid.getFluid().getName().equals(GalacticraftCore.fluidFuel.getName())) {
                            liquid = new FluidStack(existingFluid, liquid.amount);
                        }
                    }
                    tryFillContainer(tank, liquid, inventory, slot, GCItems.fuelCanister);
                }
            }
        }
    }
    
    public static boolean isEmptyContainer(final ItemStack var4, final Item canisterType) {
        if (var4.getItem() instanceof ItemCanisterGeneric) {
            return var4.getItemDamage() == 1001 || (var4.getItem() == canisterType && var4.getItemDamage() > 1);
        }
        return FluidContainerRegistry.isEmptyContainer(var4);
    }
    
    public static boolean isEmptyContainerFor(final ItemStack var4, final FluidStack targetFluid) {
        if (var4.getItem() instanceof ItemCanisterGeneric) {
            return var4.getItemDamage() == 1001 || (var4.getItemDamage() != 1 && fluidsSame(((ItemCanisterGeneric)var4.getItem()).getFluid(var4), targetFluid));
        }
        return FluidContainerRegistry.isEmptyContainer(var4) || fluidsSame(FluidContainerRegistry.getFluidForFilledItem(var4), targetFluid);
    }
    
    public static boolean fluidsSame(final FluidStack fs1, final FluidStack fs2) {
        if (fs1 == null || fs2 == null) {
            return false;
        }
        final Fluid f1 = fs1.getFluid();
        final Fluid f2 = fs2.getFluid();
        return f1 != null && f2 != null && f1.getName() != null && f1.getName().equals(f2.getName());
    }
    
    public static boolean isEmptyContainer(final ItemStack var4) {
        if (var4.getItem() instanceof ItemCanisterGeneric) {
            return var4.getItemDamage() == 1001;
        }
        return FluidContainerRegistry.isEmptyContainer(var4);
    }
    
    public static boolean isEmptyGasContainer(final ItemStack var4) {
        return false;
    }
    
    public static boolean isFilledContainer(final ItemStack var4) {
        if (var4.getItem() instanceof ItemCanisterGeneric) {
            return var4.getItemDamage() < 1001;
        }
        return FluidContainerRegistry.getFluidForFilledItem(var4) != null;
    }
    
    public static boolean isWaterContainer(final ItemStack var4) {
        final FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(var4);
        return liquid != null && liquid.getFluid() != null && liquid.getFluid().getName().equals("water");
    }
    
    public static boolean isValidContainer(final ItemStack slotItem) {
        return slotItem != null && slotItem.stackSize == 1 && (slotItem.getItem() instanceof ItemCanisterGeneric || FluidContainerRegistry.isContainer(slotItem));
    }
    
    public static ItemStack getUsedContainer(final ItemStack container) {
        if (FluidContainerRegistry.isBucket(container) && FluidContainerRegistry.isFilledContainer(container)) {
            return new ItemStack(Items.bucket, container.stackSize);
        }
        --container.stackSize;
        if (container.stackSize == 0) {
            return null;
        }
        return container;
    }
    
    public static int getFluidID(final FluidStack stack) {
        try {
            if (FluidUtil.oldFluidIDMethod) {
                try {
                    if (FluidUtil.getFluidMethod == null) {
                        if (FluidUtil.fluidStackClass == null) {
                            FluidUtil.fluidStackClass = Class.forName("net.minecraftforge.fluids.FluidStack");
                        }
                        FluidUtil.getFluidMethod = FluidUtil.fluidStackClass.getDeclaredMethod("getFluidID", (Class<?>[])new Class[0]);
                    }
                    return (int)FluidUtil.getFluidMethod.invoke(stack, new Object[0]);
                }
                catch (NoSuchMethodException error) {
                    FluidUtil.oldFluidIDMethod = false;
                    getFluidID(stack);
                    return -1;
                }
            }
            if (FluidUtil.fluidIdField == null) {
                if (FluidUtil.fluidStackClass == null) {
                    FluidUtil.fluidStackClass = Class.forName("net.minecraftforge.fluids.FluidStack");
                }
                FluidUtil.fluidIdField = FluidUtil.fluidStackClass.getDeclaredField("fluidID");
            }
            return FluidUtil.fluidIdField.getInt(stack);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    static {
        FluidUtil.oldFluidIDMethod = true;
        FluidUtil.fluidStackClass = null;
        FluidUtil.getFluidMethod = null;
        FluidUtil.fluidIdField = null;
    }
}
