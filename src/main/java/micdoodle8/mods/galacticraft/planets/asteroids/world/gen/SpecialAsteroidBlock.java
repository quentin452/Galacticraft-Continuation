package micdoodle8.mods.galacticraft.planets.asteroids.world.gen;

import net.minecraft.block.*;
import java.util.*;

public class SpecialAsteroidBlock
{
    public Block block;
    public byte meta;
    public int probability;
    public double thickness;
    public int index;
    public static ArrayList<SpecialAsteroidBlock> register;
    
    public SpecialAsteroidBlock(final Block block, final byte meta, final int probability, final double thickness) {
        this.block = block;
        this.meta = meta;
        this.probability = probability;
        this.thickness = thickness;
        this.index = SpecialAsteroidBlock.register.size();
        SpecialAsteroidBlock.register.add(this);
    }
    
    static {
        SpecialAsteroidBlock.register = new ArrayList<SpecialAsteroidBlock>();
    }
}
