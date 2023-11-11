package micdoodle8.mods.galacticraft.planets.asteroids.client.fx;

import net.minecraft.client.particle.*;
import cpw.mods.fml.relauncher.*;
import java.lang.ref.*;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.client.renderer.*;

@SideOnly(Side.CLIENT)
public class EntityFXTeleport extends EntityFX
{
    private float portalParticleScale;
    private double portalPosX;
    private double portalPosY;
    private double portalPosZ;
    private WeakReference<TileEntityShortRangeTelepad> telepad;
    private boolean direction;
    
    public EntityFXTeleport(final World par1World, final Vector3 position, final Vector3 motion, final TileEntityShortRangeTelepad telepad, final boolean direction) {
        super(par1World, position.x, position.y, position.z, motion.x, motion.y, motion.z);
        this.motionX = motion.x;
        this.motionY = motion.y;
        this.motionZ = motion.z;
        final double x = position.x;
        this.posX = x;
        this.portalPosX = x;
        final double y = position.y;
        this.posY = y;
        this.portalPosY = y;
        final double z = position.z;
        this.posZ = z;
        this.portalPosZ = z;
        final float n = this.rand.nextFloat() * 0.2f + 0.5f;
        this.particleScale = n;
        this.portalParticleScale = n;
        this.particleMaxAge = (int)(Math.random() * 10.0) + 40;
        this.noClip = true;
        this.setParticleTextureIndex((int)(Math.random() * 8.0));
        this.telepad = new WeakReference<TileEntityShortRangeTelepad>(telepad);
        this.direction = direction;
    }
    
    public void renderParticle(final Tessellator par1Tessellator, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7) {
        float f6 = (this.particleAge + par2) / this.particleMaxAge;
        f6 = 1.0f - f6;
        f6 *= f6;
        f6 = 1.0f - f6;
        this.particleScale = this.portalParticleScale * f6;
        super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
    }
    
    public int getBrightnessForRender(final float par1) {
        final int i = super.getBrightnessForRender(par1);
        float f1 = this.particleAge / (float)this.particleMaxAge;
        f1 *= f1;
        f1 *= f1;
        final int j = i & 0xFF;
        int k = i >> 16 & 0xFF;
        k += (int)(f1 * 15.0f * 16.0f);
        if (k > 240) {
            k = 240;
        }
        return j | k << 16;
    }
    
    public float getBrightness(final float par1) {
        final float f1 = super.getBrightness(par1);
        float f2 = this.particleAge / (float)this.particleMaxAge;
        f2 *= f2 * f2 * f2;
        return f1 * (1.0f - f2) + f2;
    }
    
    public void onUpdate() {
        final TileEntityShortRangeTelepad telepad1 = this.telepad.get();
        if (telepad1 != null) {
            final Vector3 color = telepad1.getParticleColor(this.rand, this.direction);
            this.particleRed = color.floatX();
            this.particleGreen = color.floatY();
            this.particleBlue = color.floatZ();
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        final float f2;
        float f = f2 = this.particleAge / (float)this.particleMaxAge;
        f = -f + f * f * 2.0f;
        f = 1.0f - f;
        this.posX = this.portalPosX + this.motionX * f;
        this.posY = this.portalPosY + this.motionY * f + (1.0f - f2);
        this.posZ = this.portalPosZ + this.motionZ * f;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }
    }
}
