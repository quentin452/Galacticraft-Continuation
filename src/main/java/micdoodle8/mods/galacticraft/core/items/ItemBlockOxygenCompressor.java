package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.block.*;
import net.minecraft.item.*;

public class ItemBlockOxygenCompressor extends ItemBlockDesc
{
    public ItemBlockOxygenCompressor(final Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
    
    public int getMetadata(final int damage) {
        return damage;
    }
    
    public String getUnlocalizedName(final ItemStack itemstack) {
        int metadata = 0;
        if (itemstack.getItemDamage() >= 4) {
            metadata = 1;
        }
        else if (itemstack.getItemDamage() >= 0) {
            metadata = 0;
        }
        return this.field_150939_a.getUnlocalizedName() + "." + metadata;
    }
    
    public String getUnlocalizedName() {
        return this.field_150939_a.getUnlocalizedName() + ".0";
    }
}
