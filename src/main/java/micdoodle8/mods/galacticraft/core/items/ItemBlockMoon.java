package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.block.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;

public class ItemBlockMoon extends ItemBlockDesc
{
    public ItemBlockMoon(final Block block) {
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
                name = "coppermoon";
                break;
            }
            case 1: {
                name = "tinmoon";
                break;
            }
            case 2: {
                name = "cheesestone";
                break;
            }
            case 3: {
                name = "moondirt";
                break;
            }
            case 4: {
                name = "moonstone";
                break;
            }
            case 5: {
                name = "moongrass";
                break;
            }
            case 14: {
                name = "bricks";
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
