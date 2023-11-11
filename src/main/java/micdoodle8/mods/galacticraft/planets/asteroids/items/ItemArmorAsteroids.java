package micdoodle8.mods.galacticraft.planets.asteroids.items;

import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.*;

public class ItemArmorAsteroids extends ItemArmor
{
    public ItemArmorAsteroids(final int armorIndex, final String assetSuffix) {
        super(AsteroidsItems.ARMOR_TITANIUM, GalacticraftCore.proxy.getTitaniumArmorRenderIndex(), armorIndex);
        this.setUnlocalizedName("titanium_" + assetSuffix);
        this.setTextureName("galacticraftasteroids:titanium_" + assetSuffix);
    }
    
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
        if (this.getArmorMaterial() == AsteroidsItems.ARMOR_TITANIUM) {
            if (stack.getItem() == AsteroidsItems.titaniumHelmet) {
                return "galacticraftasteroids:textures/model/armor/titanium_1.png";
            }
            if (stack.getItem() == AsteroidsItems.titaniumChestplate || stack.getItem() == AsteroidsItems.titaniumBoots) {
                return "galacticraftasteroids:textures/model/armor/titanium_2.png";
            }
            if (stack.getItem() == AsteroidsItems.titaniumLeggings) {
                return "galacticraftasteroids:textures/model/armor/titanium_3.png";
            }
        }
        return null;
    }
}
