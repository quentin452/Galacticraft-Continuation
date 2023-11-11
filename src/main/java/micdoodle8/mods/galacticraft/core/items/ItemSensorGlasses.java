package micdoodle8.mods.galacticraft.core.items;

import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.gui.*;
import micdoodle8.mods.galacticraft.core.client.gui.overlay.*;

public class ItemSensorGlasses extends ItemArmor
{
    public ItemSensorGlasses(final String assetName) {
        super(GCItems.ARMOR_SENSOR_GLASSES, GalacticraftCore.proxy.getSensorArmorRenderIndex(), 0);
        this.setUnlocalizedName(assetName);
        this.setTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
    }
    
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }
    
    public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
        return GalacticraftCore.TEXTURE_PREFIX + "textures/model/armor/sensor_1.png";
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    @SideOnly(Side.CLIENT)
    public void renderHelmetOverlay(final ItemStack stack, final EntityPlayer player, final ScaledResolution resolution, final float partialTicks, final boolean hasScreen, final int mouseX, final int mouseY) {
        OverlaySensorGlasses.renderSensorGlassesMain(stack, player, resolution, partialTicks, hasScreen, mouseX, mouseY);
        OverlaySensorGlasses.renderSensorGlassesValueableBlocks(stack, player, resolution, partialTicks, hasScreen, mouseX, mouseY);
    }
}
