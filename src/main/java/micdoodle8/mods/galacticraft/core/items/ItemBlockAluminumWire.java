package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.block.*;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;

public class ItemBlockAluminumWire extends ItemBlockDesc
{
    public ItemBlockAluminumWire(final Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(final int par1) {
        return this.field_150939_a.getIcon(0, par1);
    }
    
    public String getUnlocalizedName(final ItemStack par1ItemStack) {
        String name = "";
        switch (par1ItemStack.getItemDamage()) {
            case 0: {
                name = BlockAluminumWire.names[0];
                break;
            }
            case 1: {
                name = BlockAluminumWire.names[1];
                break;
            }
            default: {
                name = "null";
                break;
            }
        }
        return "tile." + name;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public int getMetadata(final int damage) {
        return damage;
    }
}
