package micdoodle8.mods.galacticraft.planets.asteroids.world.gen;

import java.util.*;

public class SpecialAsteroidBlockHandler
{
    ArrayList<SpecialAsteroidBlock> asteroidBlocks;
    
    public SpecialAsteroidBlockHandler(final SpecialAsteroidBlock... asteroidBlocks) {
        this.asteroidBlocks = new ArrayList<SpecialAsteroidBlock>();
        for (final SpecialAsteroidBlock asteroidBlock : this.asteroidBlocks) {
            for (int i = 0; i < asteroidBlock.probability; ++i) {
                this.asteroidBlocks.add(asteroidBlock);
            }
        }
    }
    
    public SpecialAsteroidBlockHandler() {
        this.asteroidBlocks = new ArrayList<SpecialAsteroidBlock>();
    }
    
    public void addBlock(final SpecialAsteroidBlock asteroidBlock) {
        for (int i = 0; i < asteroidBlock.probability; ++i) {
            this.asteroidBlocks.add(asteroidBlock);
        }
    }
    
    public SpecialAsteroidBlock getBlock(final Random rand, final int size) {
        final int s = this.asteroidBlocks.size();
        if (s < 10) {
            return this.asteroidBlocks.get(rand.nextInt(s));
        }
        final Double r = rand.nextDouble();
        final int index = (int)(s * Math.pow(r, (size + 5) * 0.05));
        return this.asteroidBlocks.get(index);
    }
}
