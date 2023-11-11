package micdoodle8.mods.galacticraft.api.inventory;

import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;

public interface IInventoryGC extends IInventory
{
    void dropExtendedItems(final EntityPlayer p0);
    
    void copyInventory(final IInventoryGC p0);
}
