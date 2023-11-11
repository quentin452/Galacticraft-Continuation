package micdoodle8.mods.galacticraft.core.client.fx;

import net.minecraft.client.particle.*;
import net.minecraft.world.*;
import net.minecraft.client.renderer.*;

public class EntityFXSparks extends EntityFX
{
    float smokeParticleScale;
    
    public EntityFXSparks(final World par1World, final double par2, final double par4, final double par6, final double par8, final double par12) {
        super(par1World, par2, par4, par6, 0.0, 0.0, 0.0);
        this.motionX *= 0.10000000149011612;
        this.motionY *= 0.10000000149011612;
        this.motionZ *= 0.10000000149011612;
        this.motionX += par8;
        this.motionY += 0.06;
        this.motionZ += par12;
        this.particleRed = 1.0f;
        this.particleGreen = 1.0f;
        this.particleBlue = 0.0f + this.rand.nextFloat() / 6.0f;
        this.particleScale *= 0.15f;
        this.particleScale *= 3.0f;
        this.smokeParticleScale = this.particleScale;
        this.particleMaxAge = 50;
        this.particleMaxAge *= (int)1.0f;
        this.noClip = false;
    }
    
    public void renderParticle(final Tessellator par1Tessellator, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7) {
        float var8 = (this.particleAge + par2) / this.particleMaxAge * 32.0f;
        if (var8 < 0.0f) {
            var8 = 0.0f;
        }
        if (var8 > 1.0f) {
            var8 = 1.0f;
        }
        this.particleScale = this.smokeParticleScale * var8;
        super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
    }
    
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }
        this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        if (this.posY == this.prevPosY) {
            this.motionX *= 1.1;
            this.motionZ *= 1.1;
        }
        this.setParticleTextureIndex(167 - this.particleAge * 8 / this.particleMaxAge);
        this.motionY -= 0.01;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9100000262260437;
        this.motionY *= 0.9100000262260437;
        this.motionZ *= 0.9100000262260437;
    }
}
