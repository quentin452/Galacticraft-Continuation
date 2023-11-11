package micdoodle8.mods.galacticraft.core.client.fx;

import net.minecraft.client.particle.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.client.renderer.*;

@SideOnly(Side.CLIENT)
public class EntityFXEntityOxygen extends EntityFX
{
    private final float portalParticleScale;
    private final double portalPosX;
    private final double portalPosY;
    private final double portalPosZ;
    
    public EntityFXEntityOxygen(final World par1World, final Vector3 position, final Vector3 motion, final Vector3 color) {
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
        final float n = 0.1f;
        this.particleScale = n;
        this.portalParticleScale = n;
        this.particleRed = color.floatX();
        this.particleGreen = color.floatY();
        this.particleBlue = color.floatZ();
        this.particleMaxAge = (int)(Math.random() * 10.0) + 40;
        this.noClip = true;
        this.setParticleTextureIndex((int)(Math.random() * 8.0));
    }
    
    public void renderParticle(final Tessellator par1Tessellator, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7) {
        float var8 = (this.particleAge + par2) / this.particleMaxAge;
        var8 = 1.0f - var8;
        var8 *= var8;
        var8 = 1.0f - var8;
        this.particleScale = this.portalParticleScale * var8;
        super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
    }
    
    public int getBrightnessForRender(final float par1) {
        final int var2 = super.getBrightnessForRender(par1);
        float var3 = this.particleAge / (float)this.particleMaxAge;
        var3 *= var3;
        var3 *= var3;
        final int var4 = var2 & 0xFF;
        int var5 = var2 >> 16 & 0xFF;
        var5 += (int)(var3 * 15.0f * 16.0f);
        if (var5 > 240) {
            var5 = 240;
        }
        return var4 | var5 << 16;
    }
    
    public float getBrightness(final float par1) {
        final float var2 = super.getBrightness(par1);
        float var3 = this.particleAge / (float)this.particleMaxAge;
        var3 *= var3 * var3 * var3;
        return var2 * (1.0f - var3) + var3;
    }
    
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        final float var2;
        float var1 = var2 = this.particleAge / (float)this.particleMaxAge;
        var1 = -var1 + var1 * var1 * 2.0f;
        var1 = 1.0f - var1;
        this.posX = this.portalPosX + this.motionX * var1;
        this.posY = this.portalPosY + this.motionY * var1 + (1.0f - var2);
        this.posZ = this.portalPosZ + this.motionZ * var1;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }
    }
}
