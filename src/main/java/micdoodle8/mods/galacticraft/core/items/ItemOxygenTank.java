package micdoodle8.mods.galacticraft.core.items;

import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import java.util.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class ItemOxygenTank extends Item
{
    public ItemOxygenTank(final int tier, final String assetName) {
        this.setMaxStackSize(1);
        this.setMaxDamage(tier * 900);
        this.setUnlocalizedName(assetName);
        this.setTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setNoRepair();
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubItems(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        par3List.add(new ItemStack(par1, 1, 0));
        par3List.add(new ItemStack(par1, 1, this.getMaxDamage()));
    }
    
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public void addInformation(final ItemStack par1ItemStack, final EntityPlayer player, final List par2List, final boolean b) {
        par2List.add(GCCoreUtil.translate("gui.tank.oxygenRemaining") + ": " + (par1ItemStack.getMaxDamage() - par1ItemStack.getItemDamage()));
    }
}
