package micdoodle8.mods.galacticraft.core.nei;

import net.minecraft.item.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import java.util.*;
import codechicken.nei.api.*;
import codechicken.nei.guihook.*;
import net.minecraft.init.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.block.*;

public class GCNEIHighlightHandler implements IHighlightHandler
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
        if (meta == 8 && b == GCBlocks.basicBlock) {
            return new ItemStack(GCBlocks.basicBlock, 1, 8);
        }
        if (meta == 2 && b == GCBlocks.blockMoon) {
            return new ItemStack(GCBlocks.blockMoon, 1, 2);
        }
        if (b == GCBlocks.fakeBlock && meta == 1) {
            return new ItemStack(GCBlocks.spaceStationBase, 1, 0);
        }
        if (b == GCBlocks.fakeBlock && meta == 2) {
            return new ItemStack(GCBlocks.landingPad, 1, 0);
        }
        if (b == GCBlocks.fakeBlock && meta == 6) {
            return new ItemStack(GCBlocks.landingPad, 1, 1);
        }
        return null;
    }
}
