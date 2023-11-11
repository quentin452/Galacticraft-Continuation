package micdoodle8.mods.galacticraft.core.items;

import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.*;

public class ItemArmorGC extends ItemArmor
{
    public ItemArmorGC(final int armorIndex, final String assetSuffix) {
        super(GCItems.ARMOR_STEEL, GalacticraftCore.proxy.getTitaniumArmorRenderIndex(), armorIndex);
        this.setUnlocalizedName("steel_" + assetSuffix);
        this.setTextureName(GalacticraftCore.TEXTURE_PREFIX + "steel_" + assetSuffix);
    }
    
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
        if (this.getArmorMaterial() == GCItems.ARMOR_STEEL) {
            if (stack.getItem() == GCItems.steelHelmet) {
                return GalacticraftCore.TEXTURE_PREFIX + "textures/model/armor/steel_1.png";
            }
            if (stack.getItem() == GCItems.steelChestplate || stack.getItem() == GCItems.steelBoots) {
                return GalacticraftCore.TEXTURE_PREFIX + "textures/model/armor/steel_2.png";
            }
            if (stack.getItem() == GCItems.steelLeggings) {
                return GalacticraftCore.TEXTURE_PREFIX + "textures/model/armor/steel_3.png";
            }
        }
        return null;
    }
}
