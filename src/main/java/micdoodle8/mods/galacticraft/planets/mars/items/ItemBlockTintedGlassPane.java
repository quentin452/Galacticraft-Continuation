package micdoodle8.mods.galacticraft.planets.mars.items;

import net.minecraft.block.*;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;

public class ItemBlockTintedGlassPane extends ItemBlock
{
    public ItemBlockTintedGlassPane(final Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
    
    public int getMetadata(final int damage) {
        return damage;
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(final int par1) {
        return this.field_150939_a.getIcon(0, par1);
    }
    
    public String getUnlocalizedName(final ItemStack itemstack) {
        return this.field_150939_a.getUnlocalizedName() + "." + ItemDye.field_150921_b[~itemstack.getItemDamage() & 0xF];
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public String getUnlocalizedName() {
        return this.field_150939_a.getUnlocalizedName() + ".0";
    }
}
