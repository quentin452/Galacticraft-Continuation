package micdoodle8.mods.galacticraft.core.perlin.generator;

import micdoodle8.mods.galacticraft.core.perlin.*;
import java.util.*;

public class Billowed extends NoiseModule
{
    private final FishyNoise noiseGen;
    private final float offsetX;
    private final float offsetY;
    private final float offsetZ;
    private final int numOctaves;
    private final float persistance;
    
    public Billowed(final long seed, final int nOctaves, final float p) {
        this.numOctaves = nOctaves;
        this.persistance = p;
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
        float curAmplitude = this.amplitude;
        for (int n = 0; n < this.numOctaves; ++n) {
            val += Math.abs(this.noiseGen.noise2d(i + this.offsetX, this.offsetY) * curAmplitude);
            i *= 2.0f;
            curAmplitude *= this.persistance;
        }
        return val;
    }
    
    @Override
    public float getNoise(float i, float j) {
        i *= this.frequencyX;
        j *= this.frequencyY;
        if (this.numOctaves == 2) {
            return Math.abs(this.noiseGen.noise2d(i + this.offsetX, j + this.offsetY) * this.amplitude) + Math.abs(this.noiseGen.noise2d(i + i + this.offsetX, j + j + this.offsetY) * this.amplitude * this.persistance);
        }
        float val = 0.0f;
        float curAmplitude = this.amplitude;
        for (int n = 0; n < this.numOctaves; ++n) {
            val += Math.abs(this.noiseGen.noise2d(i + this.offsetX, j + this.offsetY) * curAmplitude);
            i += i;
            j += j;
            curAmplitude *= this.persistance;
        }
        return val;
    }
    
    @Override
    public float getNoise(float i, float j, float k) {
        i *= this.frequencyX;
        j *= this.frequencyY;
        k *= this.frequencyZ;
        float val = 0.0f;
        for (int n = 0; n < this.numOctaves; ++n) {
            val += Math.abs(this.noiseGen.noise3d(i + this.offsetX, j + this.offsetY, k + this.offsetZ) * this.amplitude);
            i *= 2.0f;
            j *= 2.0f;
            k *= 2.0f;
        }
        return val;
    }
}
