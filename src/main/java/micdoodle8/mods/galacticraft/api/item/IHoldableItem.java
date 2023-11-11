package micdoodle8.mods.galacticraft.api.item;

import net.minecraft.entity.player.*;

public interface IHoldableItem
{
    boolean shouldHoldLeftHandUp(final EntityPlayer p0);
    
    boolean shouldHoldRightHandUp(final EntityPlayer p0);
    
    boolean shouldCrouch(final EntityPlayer p0);
}
