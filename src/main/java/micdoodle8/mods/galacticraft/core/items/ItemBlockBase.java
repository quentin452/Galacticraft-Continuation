package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.block.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;

public class ItemBlockBase extends ItemBlockDesc
{
    public ItemBlockBase(final Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
    
    public int getMetadata(final int meta) {
        return meta;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public String getUnlocalizedName(final ItemStack itemstack) {
        String name = "";
        switch (itemstack.getItemDamage()) {
            case 3: {
                name = "decoblock1";
                break;
            }
            case 4: {
                name = "decoblock2";
                break;
            }
            case 5: {
                name = "oreCopper";
                break;
            }
            case 6: {
                name = "oreTin";
                break;
            }
            case 7: {
                name = "oreAluminum";
                break;
            }
            case 8: {
                name = "oreSilicon";
                break;
            }
            case 9: {
                name = "copperBlock";
                break;
            }
            case 10: {
                name = "tinBlock";
                break;
            }
            case 11: {
                name = "aluminumBlock";
                break;
            }
            case 12: {
                name = "meteorironBlock";
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
