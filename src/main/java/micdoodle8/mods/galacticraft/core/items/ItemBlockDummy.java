package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.block.*;
import net.minecraft.util.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.proxy.*;

public class ItemBlockDummy extends ItemBlock
{
    public ItemBlockDummy(final Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(final int par1) {
        return this.field_150939_a.getIcon(0, par1);
    }
    
    public int getMetadata(final int damage) {
        return damage;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(final ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }
    
    public String getUnlocalizedName(final ItemStack itemstack) {
        final int metadata = itemstack.getItemDamage();
        String blockName = "";
        switch (metadata) {
            case 1: {
                blockName = "spaceStationBase";
                break;
            }
            case 2: {
                blockName = "launchPad";
                break;
            }
            case 3: {
                blockName = "nasaWorkbench";
                break;
            }
            case 4: {
                blockName = "solar";
                break;
            }
            case 5: {
                blockName = "cryogenicChamber";
                break;
            }
            default: {
                blockName = null;
                break;
            }
        }
        return this.field_150939_a.getUnlocalizedName() + "." + blockName;
    }
    
    public String getUnlocalizedName() {
        return this.field_150939_a.getUnlocalizedName() + ".0";
    }
}
