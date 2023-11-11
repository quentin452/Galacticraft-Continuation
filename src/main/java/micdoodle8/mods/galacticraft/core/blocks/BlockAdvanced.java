package micdoodle8.mods.galacticraft.core.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import java.lang.reflect.*;

public abstract class BlockAdvanced extends BlockContainer
{
    public BlockAdvanced(final Material material) {
        super(material);
        this.setHardness(0.6f);
        this.setResistance(2.5f);
    }
    
    public boolean onBlockActivated(final World world, final int x, final int y, final int z, final EntityPlayer entityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        if (this.isUsableWrench(entityPlayer, entityPlayer.inventory.getCurrentItem(), x, y, z)) {
            this.damageWrench(entityPlayer, entityPlayer.inventory.getCurrentItem(), x, y, z);
            if (entityPlayer.isSneaking() && this.onSneakUseWrench(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ)) {
                return true;
            }
            if (this.onUseWrench(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ)) {
                return true;
            }
        }
        return (entityPlayer.isSneaking() && this.onSneakMachineActivated(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ)) || this.onMachineActivated(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ);
    }
    
    public boolean isUsableWrench(final EntityPlayer entityPlayer, final ItemStack itemStack, final int x, final int y, final int z) {
        if (entityPlayer != null && itemStack != null) {
            final Class<? extends Item> wrenchClass = itemStack.getItem().getClass();
            try {
                final Method methodCanWrench = wrenchClass.getMethod("canWrench", EntityPlayer.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
                return (boolean)methodCanWrench.invoke(itemStack.getItem(), entityPlayer, x, y, z);
            }
            catch (NoClassDefFoundError noClassDefFoundError) {}
            catch (Exception ex) {}
            try {
                if (wrenchClass == Class.forName("ic2.core.item.tool.ItemToolWrench") || wrenchClass == Class.forName("ic2.core.item.tool.ItemToolWrenchElectric")) {
                    return itemStack.getItemDamage() < itemStack.getMaxDamage();
                }
            }
            catch (Exception ex2) {}
        }
        return false;
    }
    
    public boolean damageWrench(final EntityPlayer entityPlayer, final ItemStack itemStack, final int x, final int y, final int z) {
        if (this.isUsableWrench(entityPlayer, itemStack, x, y, z)) {
            final Class<? extends Item> wrenchClass = itemStack.getItem().getClass();
            try {
                final Method methodWrenchUsed = wrenchClass.getMethod("wrenchUsed", EntityPlayer.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
                methodWrenchUsed.invoke(itemStack.getItem(), entityPlayer, x, y, z);
                return true;
            }
            catch (Exception ex) {
                try {
                    if (wrenchClass == Class.forName("ic2.core.item.tool.ItemToolWrench") || wrenchClass == Class.forName("ic2.core.item.tool.ItemToolWrenchElectric")) {
                        final Method methodWrenchDamage = wrenchClass.getMethod("damage", ItemStack.class, Integer.TYPE, EntityPlayer.class);
                        methodWrenchDamage.invoke(itemStack.getItem(), itemStack, 1, entityPlayer);
                        return true;
                    }
                    return false;
                }
                catch (Exception ex2) {}
            }
        }
        return false;
    }
    
    public boolean onMachineActivated(final World world, final int x, final int y, final int z, final EntityPlayer entityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        return false;
    }
    
    public boolean onSneakMachineActivated(final World world, final int x, final int y, final int z, final EntityPlayer entityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        return false;
    }
    
    public boolean onUseWrench(final World world, final int x, final int y, final int z, final EntityPlayer entityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        return false;
    }
    
    public boolean onSneakUseWrench(final World world, final int x, final int y, final int z, final EntityPlayer entityPlayer, final int side, final float hitX, final float hitY, final float hitZ) {
        return this.onUseWrench(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ);
    }
}
