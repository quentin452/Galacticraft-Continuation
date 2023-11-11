package micdoodle8.mods.galacticraft.planets.asteroids.items;

import net.minecraft.block.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.relauncher.*;

public class ItemBlockAsteroids extends ItemBlock
{
    public ItemBlockAsteroids(final Block block) {
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
                name = "asteroid0";
                break;
            }
            case 1: {
                name = "asteroid1";
                break;
            }
            case 2: {
                name = "asteroid2";
                break;
            }
            case 3: {
                name = "oreAluminum";
                break;
            }
            case 4: {
                name = "oreIlmenite";
                break;
            }
            case 5: {
                name = "oreIron";
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
