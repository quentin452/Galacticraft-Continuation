package micdoodle8.mods.galacticraft.core.items;

import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.client.renderer.texture.*;
import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.creativetab.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;

public class ItemCanisterOxygenInfinite extends Item implements IItemOxygenSupply
{
    public ItemCanisterOxygenInfinite(final String assetName) {
        this.setMaxDamage(1001);
        this.setMaxStackSize(1);
        this.setNoRepair();
        this.setUnlocalizedName(assetName);
        this.setContainerItem(GCItems.oilCanister);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "oxygenCanisterInfinite");
    }
    
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    public ItemStack getContainerItem(final ItemStack itemstack) {
        if (super.getContainerItem(itemstack) == null) {
            return null;
        }
        return itemstack;
    }
    
    public float discharge(final ItemStack itemStack, final float amount) {
        return amount;
    }
    
    public int getOxygenStored(final ItemStack par1ItemStack) {
        return par1ItemStack.getMaxDamage();
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
}
