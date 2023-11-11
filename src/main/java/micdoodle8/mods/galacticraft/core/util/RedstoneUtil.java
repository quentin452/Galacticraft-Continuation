package micdoodle8.mods.galacticraft.core.util;

import net.minecraft.world.*;

public class RedstoneUtil
{
    public static boolean isBlockReceivingRedstone(final World w, final int x, final int y, final int z) {
        return w != null && (isBlockProvidingPowerTo(w, x, y - 1, z, 0) > 0 || isBlockProvidingPowerTo(w, x, y + 1, z, 1) > 0 || isBlockProvidingPowerTo_NoChunkLoad(w, x, y, z - 1, 2) > 0 || isBlockProvidingPowerTo_NoChunkLoad(w, x, y, z + 1, 3) > 0 || isBlockProvidingPowerTo_NoChunkLoad(w, x - 1, y, z, 4) > 0 || isBlockProvidingPowerTo_NoChunkLoad(w, x + 1, y, z, 5) > 0);
    }
    
    public static int isBlockProvidingPowerTo(final World w, final int x, final int y, final int z, final int side) {
        return w.getBlock(x, y, z).isProvidingStrongPower((IBlockAccess)w, x, y, z, side);
    }
    
    public static int isBlockProvidingPowerTo_NoChunkLoad(final World w, final int x, final int y, final int z, final int side) {
        if (!w.blockExists(x, y, z)) {
            return 0;
        }
        return w.getBlock(x, y, z).isProvidingStrongPower((IBlockAccess)w, x, y, z, side);
    }
}
