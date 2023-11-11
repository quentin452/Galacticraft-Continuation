package micdoodle8.mods.galacticraft.core.oxygen;

import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.core.tick.*;
import micdoodle8.mods.galacticraft.api.block.*;
import net.minecraftforge.common.util.*;
import net.minecraft.block.material.*;
import net.minecraft.world.*;
import net.minecraft.block.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.api.vector.*;

public class OxygenPressureProtocol
{
    public static final Map<Block, ArrayList<Integer>> nonPermeableBlocks;
    
    public static void updateSealerStatus(final TileEntityOxygenSealer head) {
        try {
            head.threadSeal = new ThreadFindSeal(head);
        }
        catch (IllegalThreadStateException ex) {}
    }
    
    public static void onEdgeBlockUpdated(final World world, final BlockVec3 vec) {
        if (ConfigManagerCore.enableSealerEdgeChecks) {
            TickHandlerServer.scheduleNewEdgeCheck(world.provider.dimensionId, vec);
        }
    }
    
    public static boolean canBlockPassAir(final World world, final Block block, final BlockVec3 vec, final int side) {
        if (block == null) {
            return true;
        }
        if (block instanceof IPartialSealableBlock) {
            return !((IPartialSealableBlock)block).isSealed(world, vec.x, vec.y, vec.z, ForgeDirection.getOrientation(side));
        }
        if (block instanceof BlockLeavesBase) {
            return true;
        }
        if (block.isOpaqueCube()) {
            return block instanceof BlockGravel || block.getMaterial() == Material.cloth || block instanceof BlockSponge;
        }
        if (block instanceof BlockGlass || block instanceof BlockStainedGlass) {
            return false;
        }
        if (OxygenPressureProtocol.nonPermeableBlocks.containsKey(block)) {
            final ArrayList<Integer> metaList = OxygenPressureProtocol.nonPermeableBlocks.get(block);
            if (metaList.contains(-1) || metaList.contains(vec.getBlockMetadata((IBlockAccess)world))) {
                return false;
            }
        }
        if (block instanceof BlockSlab) {
            return (side != 0 || (vec.getBlockMetadata((IBlockAccess)world) & 0x8) != 0x8) && (side != 1 || (vec.getBlockMetadata((IBlockAccess)world) & 0x8) != 0x0);
        }
        if (block instanceof BlockFarmland || block instanceof BlockEnchantmentTable || block instanceof BlockLiquid) {
            return side != 1;
        }
        if (!(block instanceof BlockPistonBase)) {
            return !block.isSideSolid((IBlockAccess)world, vec.x, vec.y, vec.z, ForgeDirection.getOrientation(side ^ 0x1));
        }
        final BlockPistonBase piston = (BlockPistonBase)block;
        final int meta = vec.getBlockMetadata((IBlockAccess)world);
        if (BlockPistonBase.isExtended(meta)) {
            final int facing = BlockPistonBase.getPistonOrientation(meta);
            return side != facing;
        }
        return false;
    }
    
    static {
        nonPermeableBlocks = new HashMap<Block, ArrayList<Integer>>();
        for (final String s : ConfigManagerCore.sealableIDs) {
            try {
                final BlockTuple bt = ConfigManagerCore.stringToBlock(s, "External Sealable IDs", true);
                if (bt != null) {
                    final int meta = bt.meta;
                    if (OxygenPressureProtocol.nonPermeableBlocks.containsKey(bt.block)) {
                        final ArrayList<Integer> list = OxygenPressureProtocol.nonPermeableBlocks.get(bt.block);
                        if (!list.contains(meta)) {
                            list.add(meta);
                        }
                        else {
                            GCLog.info("[config] External Sealable IDs: skipping duplicate entry '" + s + "'.");
                        }
                    }
                    else {
                        final ArrayList<Integer> list = new ArrayList<Integer>();
                        list.add(meta);
                        OxygenPressureProtocol.nonPermeableBlocks.put(bt.block, list);
                    }
                }
            }
            catch (Exception e) {
                GCLog.severe("[config] External Sealable IDs: error parsing '" + s + "'. Must be in the form Blockname or BlockName:metadata");
            }
        }
    }
}
