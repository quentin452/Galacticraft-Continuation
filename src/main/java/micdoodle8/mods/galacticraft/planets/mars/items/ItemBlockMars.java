package micdoodle8.mods.galacticraft.planets.mars.items;

import net.minecraft.block.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;

public class ItemBlockMars extends ItemBlock
{
    public ItemBlockMars(final Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
    
    public int getMetadata(final int meta) {
        return meta;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public String getUnlocalizedName(final ItemStack itemstack) {
        String name = "";
        switch (itemstack.getItemDamage()) {
            case 0: {
                name = "coppermars";
                break;
            }
            case 1: {
                name = "tinmars";
                break;
            }
            case 3: {
                name = "ironmars";
                break;
            }
            case 2: {
                name = "deshmars";
                break;
            }
            case 4: {
                name = "marscobblestone";
                break;
            }
            case 5: {
                name = "marsgrass";
                break;
            }
            case 6: {
                name = "marsdirt";
                break;
            }
            case 7: {
                name = "marsdungeon";
                break;
            }
            case 8: {
                name = "marsdeco";
                break;
            }
            case 9: {
                name = "marsstone";
                break;
            }
            default: {
                name = "null";
                break;
            }
        }
        return this.field_150939_a.getUnlocalizedName() + "." + name;
    }
    
    public String getUnlocalizedName() {
        return this.field_150939_a.getUnlocalizedName() + ".0";
    }
}
