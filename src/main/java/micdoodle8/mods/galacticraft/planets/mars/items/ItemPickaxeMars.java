package micdoodle8.mods.galacticraft.planets.mars.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.planets.mars.MarsModule;

public class ItemPickaxeMars extends ItemPickaxe {

    public ItemPickaxeMars(ToolMaterial par2EnumToolMaterial) {
        super(par2EnumToolMaterial);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon(
            this.getUnlocalizedName()
                .replace("item.", MarsModule.TEXTURE_PREFIX));
    }

    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
        par3List.add(new ItemStack(par1, 1, 0));
    }

    @Override
    public int getMetadata(int par1) {
        return par1;
    }
}
