package micdoodle8.mods.galacticraft.planets.mars.world.gen;

import net.minecraft.world.gen.feature.*;
import net.minecraft.block.*;
import net.minecraft.world.*;
import java.util.*;

public class WorldGenEggs extends WorldGenerator
{
    private Block eggBlock;
    
    public WorldGenEggs(final Block egg) {
        this.eggBlock = egg;
    }
    
    public boolean generate(final World par1World, final Random par2Random, final int x, final int y, final int z) {
        final int i1 = x + par2Random.nextInt(8) - par2Random.nextInt(8);
        final int j1 = y + par2Random.nextInt(4) - par2Random.nextInt(4);
        final int k1 = z + par2Random.nextInt(8) - par2Random.nextInt(8);
        if (!par1World.blockExists(i1, j1, k1)) {
            return false;
        }
        if (par1World.isAirBlock(i1, j1, k1) && (!par1World.provider.hasNoSky || j1 < 127) && this.eggBlock.canBlockStay(par1World, i1, j1, k1)) {
            par1World.setBlock(i1, j1, k1, this.eggBlock, par2Random.nextInt(3), 2);
        }
        return true;
    }
}
