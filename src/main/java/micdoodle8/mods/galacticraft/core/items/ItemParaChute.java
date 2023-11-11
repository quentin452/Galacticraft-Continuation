package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import java.util.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;

public class ItemParaChute extends Item
{
    public static final String[] names;
    protected IIcon[] icons;
    
    public ItemParaChute(final String assetName) {
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
        for (int i = 0; i < ItemParaChute.names.length; ++i) {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }
    
    public int getMetadata(final int par1) {
        return par1;
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister iconRegister) {
        int i = 0;
        this.icons = new IIcon[ItemParaChute.names.length];
        for (final String name : ItemParaChute.names) {
            this.icons[i++] = iconRegister.registerIcon(this.getIconString() + "_" + name);
        }
    }
    
    public String getUnlocalizedName(final ItemStack itemStack) {
        return this.getUnlocalizedName() + "_" + ItemParaChute.names[itemStack.getItemDamage()];
    }
    
    public IIcon getIconFromDamage(final int damage) {
        if (this.icons.length > damage) {
            return this.icons[damage];
        }
        return super.getIconFromDamage(damage);
    }
    
    public static int getParachuteDamageValueFromDye(final int meta) {
        switch (meta) {
            case 0: {
                return 1;
            }
            case 1: {
                return 13;
            }
            case 2: {
                return 7;
            }
            case 3: {
                return 4;
            }
            case 4: {
                return 5;
            }
            case 5: {
                return 12;
            }
            case 6: {
                return 14;
            }
            case 7: {
                return 8;
            }
            case 8: {
                return 6;
            }
            case 9: {
                return 11;
            }
            case 10: {
                return 3;
            }
            case 11: {
                return 15;
            }
            case 12: {
                return 2;
            }
            case 13: {
                return 9;
            }
            case 14: {
                return 10;
            }
            case 15: {
                return 0;
            }
            default: {
                return -1;
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    static {
        names = new String[] { "plain", "black", "blue", "lime", "brown", "darkblue", "darkgray", "darkgreen", "gray", "magenta", "orange", "pink", "purple", "red", "teal", "yellow" };
    }
}
