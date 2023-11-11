package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;

public class ItemBlockSolar extends ItemBlockDesc
{
    public ItemBlockSolar(final Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
    
    public String getUnlocalizedName(final ItemStack par1ItemStack) {
        final int index = Math.min(Math.max(par1ItemStack.getItemDamage() / 4, 0), BlockSolar.names.length);
        final String name = BlockSolar.names[index];
        return this.field_150939_a.getUnlocalizedName() + "." + name;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public int getMetadata(final int damage) {
        return damage;
    }
}
