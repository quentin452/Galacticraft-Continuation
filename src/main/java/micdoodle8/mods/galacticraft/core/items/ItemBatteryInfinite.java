package micdoodle8.mods.galacticraft.core.items;

import micdoodle8.mods.galacticraft.core.energy.item.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.player.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.item.*;

public class ItemBatteryInfinite extends ItemElectricBase
{
    public ItemBatteryInfinite(final String assetName) {
        this.setUnlocalizedName(assetName);
        this.setTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
    }
    
    protected void setMaxTransfer() {
        this.transferMax = 1000.0f;
    }
    
    public int getTierGC(final ItemStack itemStack) {
        return 2;
    }
    
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public void addInformation(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final List par3List, final boolean par4) {
        par3List.add("�2" + GCCoreUtil.translate("gui.infiniteBattery.desc"));
    }
    
    public float getElectricityStored(final ItemStack itemStack) {
        return this.getMaxElectricityStored(itemStack);
    }
    
    public void setElectricity(final ItemStack itemStack, final float joules) {
    }
    
    public float getMaxElectricityStored(final ItemStack itemStack) {
        return Float.POSITIVE_INFINITY;
    }
    
    public float getTransfer(final ItemStack itemStack) {
        return 0.0f;
    }
    
    public float recharge(final ItemStack theItem, final float energy, final boolean doReceive) {
        return 0.0f;
    }
    
    public float discharge(final ItemStack theItem, final float energy, final boolean doTransfer) {
        return energy;
    }
    
    public void getSubItems(final Item par1, final CreativeTabs par2CreativeTabs, final List par3List) {
        par3List.add(new ItemStack(par1, 1, 0));
    }
}
