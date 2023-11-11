package micdoodle8.mods.galacticraft.core.items;

import micdoodle8.mods.galacticraft.api.recipe.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import java.util.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class ItemSchematic extends Item implements ISchematicItem
{
    public static final String[] names;
    protected IIcon[] icons;
    
    public ItemSchematic(final String assetName) {
        this.icons = new IIcon[ItemSchematic.names.length];
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
        this.setUnlocalizedName(assetName);
        this.setTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
    }
    
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    public void getSubItems(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        for (int i = 0; i < ItemSchematic.names.length; ++i) {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public int getMetadata(final int par1) {
        return par1;
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister iconRegister) {
        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = iconRegister.registerIcon(this.getIconString() + "_" + ItemSchematic.names[i]);
        }
    }
    
    public IIcon getIconFromDamage(final int damage) {
        if (this.icons.length > damage) {
            return this.icons[damage];
        }
        return super.getIconFromDamage(damage);
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final List par3List, final boolean par4) {
        if (par2EntityPlayer.worldObj.isRemote) {
            switch (par1ItemStack.getItemDamage()) {
                case 0: {
                    par3List.add(GCCoreUtil.translate("schematic.moonbuggy.name"));
                    break;
                }
                case 1: {
                    par3List.add(GCCoreUtil.translate("schematic.rocketT2.name"));
                    if (!GalacticraftCore.isPlanetsLoaded) {
                        par3List.add(EnumColor.DARK_AQUA + "\"Galacticraft: Planets\" Not Installed!");
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    static {
        names = new String[] { "buggy", "rocketT2" };
    }
}
