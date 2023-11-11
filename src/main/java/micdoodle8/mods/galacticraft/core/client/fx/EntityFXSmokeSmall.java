package micdoodle8.mods.galacticraft.core.client.fx;

import net.minecraft.client.particle.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.client.renderer.*;
import org.lwjgl.opengl.*;

@SideOnly(Side.CLIENT)
public class EntityFXSmokeSmall extends EntityFX
{
    float smokeParticleScale;
    
    public EntityFXSmokeSmall(final World par1World, final Vector3 position, final Vector3 motion) {
        super(par1World, position.x, position.y, position.z, 0.0, 0.0, 0.0);
        this.motionX *= 0.01;
        this.motionY *= 0.01;
        this.motionZ *= 0.01;
        this.setSize(0.05f, 0.05f);
        this.motionX += motion.x;
        this.motionY += motion.y;
        this.motionZ += motion.z;
        this.particleAlpha = 0.8f;
        final float particleRed = (float)(Math.random() * 0.2) + 0.7f;
        this.particleBlue = particleRed;
        this.particleGreen = particleRed;
        this.particleRed = particleRed;
        this.particleScale *= 0.3f;
        this.smokeParticleScale = this.particleScale;
        this.particleMaxAge = 110;
        this.noClip = false;
    }
    
    public void renderParticle(final Tessellator par1Tessellator, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7) {
        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glDisable(2929);
        float var8 = (this.particleAge + par2) / this.particleMaxAge * 32.0f;
        if (var8 < 0.0f) {
            var8 = 0.0f;
        }
        if (var8 > 1.0f) {
            var8 = 1.0f;
        }
        this.particleScale = this.smokeParticleScale * var8;
        float f6 = this.particleTextureIndexX / 16.0f;
        float f7 = f6 + 0.0624375f;
        float f8 = this.particleTextureIndexY / 16.0f;
        float f9 = f8 + 0.0624375f;
        final float f10 = 0.1f * this.particleScale;
        if (this.particleIcon != null) {
            f6 = this.particleIcon.getMinU();
            f7 = this.particleIcon.getMaxU();
            f8 = this.particleIcon.getMinV();
            f9 = this.particleIcon.getMaxV();
        }
        final float f11 = (float)(this.prevPosX + (this.posX - this.prevPosX) * par2 - EntityFX.interpPosX);
        final float f12 = (float)(this.prevPosY + (this.posY - this.prevPosY) * par2 - EntityFX.interpPosY);
        final float f13 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * par2 - EntityFX.interpPosZ);
        final float f14 = 1.0f;
        par1Tessellator.setColorRGBA_F(this.particleRed * 1.0f, this.particleGreen * 1.0f, this.particleBlue * 1.0f, this.particleAlpha);
        par1Tessellator.addVertexWithUV((double)(f11 - par3 * f10 - par6 * f10), (double)(f12 - par4 * f10), (double)(f13 - par5 * f10 - par7 * f10), (double)f7, (double)f9);
        par1Tessellator.addVertexWithUV((double)(f11 - par3 * f10 + par6 * f10), (double)(f12 + par4 * f10), (double)(f13 - par5 * f10 + par7 * f10), (double)f7, (double)f8);
        par1Tessellator.addVertexWithUV((double)(f11 + par3 * f10 + par6 * f10), (double)(f12 + par4 * f10), (double)(f13 + par5 * f10 + par7 * f10), (double)f6, (double)f8);
        par1Tessellator.addVertexWithUV((double)(f11 + par3 * f10 - par6 * f10), (double)(f12 - par4 * f10), (double)(f13 + par5 * f10 - par7 * f10), (double)f6, (double)f9);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
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
            this.motionX *= 1.03;
            this.motionZ *= 1.03;
        }
        this.motionX *= 0.99;
        this.motionY *= 0.99;
        this.motionZ *= 0.99;
    }
}
