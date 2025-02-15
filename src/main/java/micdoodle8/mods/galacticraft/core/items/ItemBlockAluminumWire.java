package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.core.blocks.BlockAluminumWire;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;

public class ItemBlockAluminumWire extends ItemBlockDesc {

    public ItemBlockAluminumWire(Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int par1) {
        return this.field_150939_a.getIcon(0, par1);
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        int damage = par1ItemStack.getItemDamage();
        String name;
        switch (damage) {
            case 0:
                name = BlockAluminumWire.names[0];
                break;
            case 1:
                name = BlockAluminumWire.names[1];
                break;
            default:
                name = "null";
                break;
        }

        return "tile." + name;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }
}
