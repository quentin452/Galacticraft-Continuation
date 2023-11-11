package micdoodle8.mods.galacticraft.core.perlin;

import java.util.*;

public class FishyNoise
{
    int[] perm;
    public float[][] grad2d;
    public int[][] grad3d;
    
    public FishyNoise(final long seed) {
        this.perm = new int[512];
        this.grad2d = new float[][] { { 1.0f, 0.0f }, { 0.9239f, 0.3827f }, { 0.707107f, 0.707107f }, { 0.3827f, 0.9239f }, { 0.0f, 1.0f }, { -0.3827f, 0.9239f }, { -0.707107f, 0.707107f }, { -0.9239f, 0.3827f }, { -1.0f, 0.0f }, { -0.9239f, -0.3827f }, { -0.707107f, -0.707107f }, { -0.3827f, -0.9239f }, { 0.0f, -1.0f }, { 0.3827f, -0.9239f }, { 0.707107f, -0.707107f }, { 0.9239f, -0.3827f } };
        this.grad3d = new int[][] { { 1, 1, 0 }, { -1, 1, 0 }, { 1, -1, 0 }, { -1, -1, 0 }, { 1, 0, 1 }, { -1, 0, 1 }, { 1, 0, -1 }, { -1, 0, -1 }, { 0, 1, 1 }, { 0, -1, 1 }, { 0, 1, -1 }, { 0, -1, -1 }, { 1, 1, 0 }, { -1, 1, 0 }, { 0, -1, 1 }, { 0, -1, -1 } };
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
        System.arraycopy(this.perm, 0, this.perm, 256, 256);
    }
    
    private static float lerp(final float x, final float y, final float n) {
        return x + n * (y - x);
    }
    
    private static int fastFloor(final float x) {
        return (x > 0.0f) ? ((int)x) : ((int)x - 1);
    }
    
    private static float fade(final float n) {
        return n * n * n * (n * (n * 6.0f - 15.0f) + 10.0f);
    }
    
    private static float dot2(final float[] grad2, final float x, final float y) {
        return grad2[0] * x + grad2[1] * y;
    }
    
    private static float dot3(final int[] grad3, final float x, final float y, final float z) {
        return grad3[0] * x + grad3[1] * y + grad3[2] * z;
    }
    
    public float noise2d(float x, float y) {
        int largeX = (x > 0.0f) ? ((int)x) : ((int)x - 1);
        int largeY = (y > 0.0f) ? ((int)y) : ((int)y - 1);
        x -= largeX;
        y -= largeY;
        largeX &= 0xFF;
        largeY &= 0xFF;
        final float u = x * x * x * (x * (x * 6.0f - 15.0f) + 10.0f);
        final float v = y * y * y * (y * (y * 6.0f - 15.0f) + 10.0f);
        final int randY = this.perm[largeY] + largeX;
        final int randY2 = this.perm[largeY + 1] + largeX;
        float[] grad2 = this.grad2d[this.perm[randY] & 0xF];
        final float grad3 = grad2[0] * x + grad2[1] * y;
        grad2 = this.grad2d[this.perm[randY2] & 0xF];
        final float grad4 = grad2[0] * x + grad2[1] * (y - 1.0f);
        grad2 = this.grad2d[this.perm[1 + randY2] & 0xF];
        final float grad5 = grad2[0] * (x - 1.0f) + grad2[1] * (y - 1.0f);
        grad2 = this.grad2d[this.perm[1 + randY] & 0xF];
        final float grad6 = grad2[0] * (x - 1.0f) + grad2[1] * y;
        final float lerpX0 = grad3 + u * (grad6 - grad3);
        return lerpX0 + v * (grad4 + u * (grad5 - grad4) - lerpX0);
    }
    
    public float noise3d(float x, float y, float z) {
        int unitX = (x > 0.0f) ? ((int)x) : ((int)x - 1);
        int unitY = (y > 0.0f) ? ((int)y) : ((int)y - 1);
        int unitZ = (z > 0.0f) ? ((int)z) : ((int)z - 1);
        x -= unitX;
        y -= unitY;
        z -= unitZ;
        unitX &= 0xFF;
        unitY &= 0xFF;
        unitZ &= 0xFF;
        final float u = x * x * x * (x * (x * 6.0f - 15.0f) + 10.0f);
        final float v = y * y * y * (y * (y * 6.0f - 15.0f) + 10.0f);
        final float w = z * z * z * (z * (z * 6.0f - 15.0f) + 10.0f);
        final int randZ = this.perm[unitZ] + unitY;
        final int randZ2 = this.perm[unitZ + 1] + unitY;
        final int randYZ = this.perm[randZ] + unitX;
        final int randY1Z = this.perm[1 + randZ] + unitX;
        final int randYZ2 = this.perm[randZ2] + unitX;
        final int randY1Z2 = this.perm[1 + randZ2] + unitX;
        int[] grad3 = this.grad3d[this.perm[randYZ] & 0xF];
        final float grad4 = grad3[0] * x + grad3[1] * y + grad3[2] * z;
        grad3 = this.grad3d[this.perm[1 + randYZ] & 0xF];
        final float grad5 = grad3[0] * (x - 1.0f) + grad3[1] * y + grad3[2] * z;
        grad3 = this.grad3d[this.perm[randY1Z] & 0xF];
        final float grad6 = grad3[0] * x + grad3[1] * (y - 1.0f) + grad3[2] * z;
        grad3 = this.grad3d[this.perm[1 + randY1Z] & 0xF];
        final float grad7 = grad3[0] * (x - 1.0f) + grad3[1] * (y - 1.0f) + grad3[2] * z;
        --z;
        grad3 = this.grad3d[this.perm[randYZ2] & 0xF];
        final float grad8 = grad3[0] * x + grad3[1] * y + grad3[2] * z;
        grad3 = this.grad3d[this.perm[1 + randYZ2] & 0xF];
        final float grad9 = grad3[0] * (x - 1.0f) + grad3[1] * y + grad3[2] * z;
        grad3 = this.grad3d[this.perm[randY1Z2] & 0xF];
        final float grad10 = grad3[0] * x + grad3[1] * (y - 1.0f) + grad3[2] * z;
        grad3 = this.grad3d[this.perm[1 + randY1Z2] & 0xF];
        final float grad11 = grad3[0] * (x - 1.0f) + grad3[1] * (y - 1.0f) + grad3[2] * z;
        final float f1 = grad4 + u * (grad5 - grad4);
        final float f2 = grad6 + u * (grad7 - grad6);
        final float f3 = grad8 + u * (grad9 - grad8);
        final float f4 = grad10 + u * (grad11 - grad10);
        final float lerp1 = f1 + v * (f2 - f1);
        return lerp1 + w * (f3 + v * (f4 - f3) - lerp1);
    }
}
