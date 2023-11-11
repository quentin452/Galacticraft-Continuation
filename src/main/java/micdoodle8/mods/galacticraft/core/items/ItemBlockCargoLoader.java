package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.block.*;
import net.minecraft.item.*;

public class ItemBlockCargoLoader extends ItemBlockDesc
{
    public ItemBlockCargoLoader(final Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
    
    public String getUnlocalizedName(final ItemStack par1ItemStack) {
        String name = "";
        if (par1ItemStack.getItemDamage() < 4) {
            name = "loader";
        }
        else {
            name = "unloader";
        }
        return this.field_150939_a.getUnlocalizedName() + "." + name;
    }
    
    public int getMetadata(final int damage) {
        return damage;
    }
}
