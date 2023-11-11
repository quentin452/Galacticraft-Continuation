package micdoodle8.mods.galacticraft.core.perlin.generator;

import micdoodle8.mods.galacticraft.core.perlin.*;
import java.util.*;

public class RidgedMulti extends NoiseModule
{
    private final FishyNoise noiseGen;
    private final float offsetX;
    private final float offsetY;
    private final float offsetZ;
    private final int numOctaves;
    
    public RidgedMulti(final long seed, final int nOctaves) {
        this.numOctaves = nOctaves;
        final Random rand = new Random(seed);
        this.offsetX = rand.nextFloat() / 2.0f + 0.01f;
        this.offsetY = rand.nextFloat() / 2.0f + 0.01f;
        this.offsetZ = rand.nextFloat() / 2.0f + 0.01f;
        this.noiseGen = new FishyNoise(seed);
    }
    
    @Override
    public float getNoise(float i) {
        i *= this.frequencyX;
        float val = 0.0f;
        float weight = 1.0f;
        final float offset = 1.0f;
        final float gain = 2.0f;
        for (int n = 0; n < this.numOctaves; ++n) {
            float noise = this.absolute(this.noiseGen.noise2d(i + this.offsetX, this.offsetY));
            noise = 1.0f - noise;
            noise *= noise;
            noise *= weight;
            weight = noise * 2.0f;
            if (weight > 1.0f) {
                weight = 1.0f;
            }
            if (weight < 0.0f) {
                weight = 0.0f;
            }
            val += noise;
            i *= 2.0f;
        }
        return val;
    }
    
    @Override
    public float getNoise(float i, float j) {
        i *= this.frequencyX;
        j *= this.frequencyY;
        float val = 0.0f;
        float weight = 1.0f;
        final float offset = 1.0f;
        final float gain = 2.0f;
        for (int n = 0; n < this.numOctaves; ++n) {
            float noise = this.absolute(this.noiseGen.noise2d(i + this.offsetX, j + this.offsetY));
            noise = 1.0f - noise;
            noise *= noise;
            noise *= weight;
            weight = noise * 2.0f;
            if (weight > 1.0f) {
                weight = 1.0f;
            }
            if (weight < 0.0f) {
                weight = 0.0f;
            }
            val += noise;
            i *= 2.0f;
            j *= 2.0f;
        }
        return val;
    }
    
    @Override
    public float getNoise(float i, float j, float k) {
        i *= this.frequencyX;
        j *= this.frequencyY;
        k *= this.frequencyZ;
        float val = 0.0f;
        float weight = 1.0f;
        final float offset = 1.0f;
        final float gain = 2.0f;
        for (int n = 0; n < this.numOctaves; ++n) {
            float noise = this.absolute(this.noiseGen.noise3d(i + this.offsetX, j + this.offsetY, k + this.offsetZ));
            noise = 1.0f - noise;
            noise *= noise;
            noise *= weight;
            weight = noise * 2.0f;
            if (weight > 1.0f) {
                weight = 1.0f;
            }
            if (weight < 0.0f) {
                weight = 0.0f;
            }
            val += noise;
            i *= 2.0f;
            j *= 2.0f;
            k *= 2.0f;
        }
        return val;
    }
    
    private float absolute(float d) {
        if (d < 0.0f) {
            d = -d;
        }
        return d;
    }
}
