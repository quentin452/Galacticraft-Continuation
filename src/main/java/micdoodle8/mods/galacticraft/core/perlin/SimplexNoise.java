package micdoodle8.mods.galacticraft.core.perlin;

import java.util.*;

public class SimplexNoise
{
    int[] perm;
    public int[][] grad2d;
    
    public SimplexNoise(final long seed) {
        this.perm = new int[512];
        this.grad2d = new int[][] { { 0, 0 }, { 0, 1 }, { 1, 1 }, { 1, 0 } };
        final Random rand = new Random(seed);
        for (int i = 0; i < 256; ++i) {
            this.perm[i] = i;
        }
        for (int i = 0; i < 256; ++i) {
            final int j = rand.nextInt(256);
            this.perm[i] ^= this.perm[j];
            this.perm[j] ^= this.perm[i];
            this.perm[i] ^= this.perm[j];
        }
        for (int i = 0; i < 256; ++i) {
            this.perm[i + 256] = this.perm[i];
        }
    }
}
