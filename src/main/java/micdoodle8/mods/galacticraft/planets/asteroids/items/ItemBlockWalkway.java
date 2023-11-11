package micdoodle8.mods.galacticraft.planets.asteroids.items;

import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import java.util.*;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import cpw.mods.fml.relauncher.*;

public class ItemBlockWalkway extends ItemBlockDesc
{
    public ItemBlockWalkway(final Block block) {
        super(block);
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer entityPlayer, final List list, final boolean advanced) {
        if (itemStack.getItem() == Item.getItemFromBlock(AsteroidBlocks.blockWalkwayWire)) {
            list.add(EnumColor.AQUA + GCBlocks.aluminumWire.getLocalizedName());
        }
        else if (itemStack.getItem() == Item.getItemFromBlock(AsteroidBlocks.blockWalkwayOxygenPipe)) {
            list.add(EnumColor.AQUA + GCBlocks.oxygenPipe.getLocalizedName());
        }
        super.addInformation(itemStack, entityPlayer, list, advanced);
    }
}
