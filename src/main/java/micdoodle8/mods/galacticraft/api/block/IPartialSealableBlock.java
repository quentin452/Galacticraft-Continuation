package micdoodle8.mods.galacticraft.api.block;

import net.minecraft.world.*;
import net.minecraftforge.common.util.*;

public interface IPartialSealableBlock
{
    boolean isSealed(final World p0, final int p1, final int p2, final int p3, final ForgeDirection p4);
}
