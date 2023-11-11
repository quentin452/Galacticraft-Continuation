package micdoodle8.mods.galacticraft.planets.asteroids.items;

import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.client.renderer.texture.*;
import java.util.*;

public class ItemThermalPadding extends Item implements IItemThermal
{
    public static String[] names;
    protected IIcon[] icons;
    
    public ItemThermalPadding(final String assetName) {
        this.icons = new IIcon[ItemThermalPadding.names.length];
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
        this.setUnlocalizedName(assetName);
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(final int damage, final int pass) {
        if (pass == 1 && this.icons.length > damage + 4) {
            return this.icons[damage + 4];
        }
        return this.getIconFromDamage(damage);
    }
    
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister iconRegister) {
        int i = 0;
        for (final String name : ItemThermalPadding.names) {
            this.icons[i++] = iconRegister.registerIcon("galacticraftasteroids:" + name);
        }
    }
    
    public IIcon getIconFromDamage(final int damage) {
        if (this.icons.length > damage) {
            return this.icons[damage];
        }
        return super.getIconFromDamage(damage);
    }
    
    public void getSubItems(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        for (int i = 0; i < ItemThermalPadding.names.length / 2; ++i) {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }
    
    public String getUnlocalizedName(final ItemStack par1ItemStack) {
        if (this.icons.length > par1ItemStack.getItemDamage()) {
            return "item." + ItemThermalPadding.names[par1ItemStack.getItemDamage()];
        }
        return "unnamed";
    }
    
    public int getMetadata(final int par1) {
        return par1;
    }
    
    public int getThermalStrength() {
        return 1;
    }
    
    public boolean isValidForSlot(final ItemStack stack, final int armorSlot) {
        return stack.getItemDamage() == armorSlot;
    }
    
    static {
        ItemThermalPadding.names = new String[] { "thermalHelm", "thermalChestplate", "thermalLeggings", "thermalBoots", "thermalHelm0", "thermalChestplate0", "thermalLeggings0", "thermalBoots0" };
    }
}
