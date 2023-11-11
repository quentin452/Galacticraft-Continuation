package micdoodle8.mods.galacticraft.core.perlin;

public abstract class NoiseModule
{
    public float frequencyX;
    public float frequencyY;
    public float frequencyZ;
    public float amplitude;
    
    public NoiseModule() {
        this.frequencyX = 1.0f;
        this.frequencyY = 1.0f;
        this.frequencyZ = 1.0f;
        this.amplitude = 1.0f;
    }
    
    public abstract float getNoise(final float p0);
    
    public abstract float getNoise(final float p0, final float p1);
    
    public abstract float getNoise(final float p0, final float p1, final float p2);
    
    public void setFrequency(final float frequency) {
        this.frequencyX = frequency;
        this.frequencyY = frequency;
        this.frequencyZ = frequency;
    }
}
