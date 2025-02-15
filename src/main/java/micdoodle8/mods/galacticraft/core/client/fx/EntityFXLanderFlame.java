package micdoodle8.mods.galacticraft.core.client.fx;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityFXLanderFlame extends EntityFX {

    private final float smokeParticleScale;
    private final EntityLivingBase ridingEntity;

    public EntityFXLanderFlame(World world, double x, double y, double z, double mX, double mY, double mZ,
        EntityLivingBase ridingEntity) {
        super(world, x, y, z, mX, mY, mZ);
        this.motionX *= 0.10000000149011612D;
        this.motionZ *= 0.10000000149011612D;
        this.motionX += mX;
        this.motionY = mY;
        this.motionZ += mZ;
        this.particleRed = 200F / 255F;
        this.particleGreen = 200F / 255F;
        this.particleBlue = 200F / 255F + this.rand.nextFloat() / 3;
        this.particleScale *= 8F * 1.0F;
        this.smokeParticleScale = this.particleScale;
        this.particleMaxAge = (int) 5.0D;
        this.noClip = false;
        this.ridingEntity = ridingEntity;
    }

    @Override
    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6,
        float par7) {
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        float var8 = (this.particleAge + par2) / this.particleMaxAge * 32.0F;

        if (var8 < 0.0F) {
            var8 = 0.0F;
        }

        if (var8 > 1.0F) {
            var8 = 1.0F;
        }

        this.particleScale = this.smokeParticleScale * var8;
        super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }

        this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        this.particleGreen -= 0.09F;
        this.particleRed -= 0.09F;

        if (this.posY == this.prevPosY) {
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
        }

        this.particleScale *= 0.9599999785423279D;

        this.motionX *= 0.9599999785423279D;
        this.motionY *= 0.9599999785423279D;
        this.motionZ *= 0.9599999785423279D;
    }

    @Override
    public int getBrightnessForRender(float par1) {
        return 15728880;
    }

    @Override
    public float getBrightness(float par1) {
        return 1.0F;
    }
}
