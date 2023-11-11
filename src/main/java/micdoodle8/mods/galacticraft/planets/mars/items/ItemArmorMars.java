package micdoodle8.mods.galacticraft.planets.mars.items;

import net.minecraft.entity.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import net.minecraft.client.renderer.texture.*;

public class ItemArmorMars extends ItemArmor
{
    private final ItemArmor.ArmorMaterial material;
    
    public ItemArmorMars(final ItemArmor.ArmorMaterial par2EnumArmorMaterial, final int par3, final int par4) {
        super(par2EnumArmorMaterial, par3, par4);
        this.material = par2EnumArmorMaterial;
    }
    
    public Item setUnlocalizedName(final String par1Str) {
        super.setTextureName(par1Str);
        super.setUnlocalizedName(par1Str);
        return (Item)this;
    }
    
    public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
        if (this.material == MarsItems.ARMORDESH) {
            if (stack.getItem() == MarsItems.deshHelmet) {
                return "galacticraftmars:textures/model/armor/desh_1.png";
            }
            if (stack.getItem() == MarsItems.deshChestplate || stack.getItem() == MarsItems.deshBoots) {
                return "galacticraftmars:textures/model/armor/desh_2.png";
            }
            if (stack.getItem() == MarsItems.deshLeggings) {
                return "galacticraftmars:textures/model/armor/desh_3.png";
            }
        }
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon(this.getUnlocalizedName().replace("item.", "galacticraftmars:"));
    }
}
