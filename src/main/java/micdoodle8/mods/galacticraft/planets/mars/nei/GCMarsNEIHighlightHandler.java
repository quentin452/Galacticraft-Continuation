package micdoodle8.mods.galacticraft.planets.mars.nei;

import net.minecraft.item.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import java.util.*;
import codechicken.nei.api.*;
import codechicken.nei.guihook.*;
import net.minecraft.init.*;
import micdoodle8.mods.galacticraft.planets.mars.blocks.*;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.*;
import net.minecraft.block.*;

public class GCMarsNEIHighlightHandler implements IHighlightHandler
{
    public List<String> handleTextData(final ItemStack stack, final World world, final EntityPlayer player, final MovingObjectPosition mop, final List<String> currenttip, final ItemInfo.Layout layout) {
        String name = null;
        try {
            final String s = GuiContainerManager.itemDisplayNameShort(stack);
            if (s != null && !s.endsWith("Unnamed")) {
                name = s;
            }
            if (name != null) {
                currenttip.add(name);
            }
        }
        catch (Exception ex) {}
        if (stack.getItem() == Items.redstone) {
            final int md = world.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ);
            String s2 = "" + md;
            if (s2.length() < 2) {
                s2 = " " + s2;
            }
            currenttip.set(currenttip.size() - 1, name + " " + s2);
        }
        return currenttip;
    }
    
    public ItemStack identifyHighlight(final World world, final EntityPlayer player, final MovingObjectPosition mop) {
        final int x = mop.blockX;
        final int y = mop.blockY;
        final int z = mop.blockZ;
        final Block b = world.getBlock(x, y, z);
        final int meta = world.getBlockMetadata(x, y, z);
        if (b == MarsBlocks.marsBlock) {
            if (meta == 2) {
                return new ItemStack(MarsBlocks.marsBlock, 1, 2);
            }
            if (meta == 9) {
                return new ItemStack(MarsBlocks.marsBlock, 1, 9);
            }
        }
        else if (b == AsteroidBlocks.blockBasic && meta == 4) {
            return new ItemStack(AsteroidBlocks.blockBasic, 1, 4);
        }
        return null;
    }
}
