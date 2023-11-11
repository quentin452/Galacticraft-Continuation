package micdoodle8.mods.galacticraft.core.items;

import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;
import java.util.*;

public class ItemKey extends Item implements IKeyItem
{
    public static String[] keyTypes;
    
    public ItemKey(final String assetName) {
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setUnlocalizedName(assetName);
        this.setTextureName("arrow");
    }
    
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    public String getUnlocalizedName(final ItemStack itemStack) {
        return this.getUnlocalizedName() + "." + ItemKey.keyTypes[itemStack.getItemDamage()];
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public void getSubItems(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        for (int i = 0; i < ItemKey.keyTypes.length; ++i) {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }
    
    public int getMetadata(final int par1) {
        return par1;
    }
    
    public int getTier(final ItemStack keyStack) {
        return 1;
    }
    
    static {
        ItemKey.keyTypes = new String[] { "T1" };
    }
}
