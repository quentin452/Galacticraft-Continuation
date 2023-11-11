package micdoodle8.mods.galacticraft.core.client.fx;

import net.minecraft.client.particle.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.world.*;
import net.minecraft.client.renderer.*;
import org.lwjgl.opengl.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import java.util.*;

@SideOnly(Side.CLIENT)
public class EntityFXLanderFlame extends EntityFX
{
    private float smokeParticleScale;
    private EntityLivingBase ridingEntity;
    
    public EntityFXLanderFlame(final World world, final double x, final double y, final double z, final double mX, final double mY, final double mZ, final EntityLivingBase ridingEntity) {
        super(world, x, y, z, mX, mY, mZ);
        this.motionX *= 0.10000000149011612;
        this.motionZ *= 0.10000000149011612;
        this.motionX += mX;
        this.motionY = mY;
        this.motionZ += mZ;
        this.particleRed = 0.78431374f;
        this.particleGreen = 0.78431374f;
        this.particleBlue = 0.78431374f + this.rand.nextFloat() / 3.0f;
        this.particleScale *= 8.0f;
        this.smokeParticleScale = this.particleScale;
        this.particleMaxAge = 5;
        this.noClip = false;
        this.ridingEntity = ridingEntity;
    }
    
    public void renderParticle(final Tessellator par1Tessellator, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7) {
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
        super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
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
        this.particleGreen -= 0.09f;
        this.particleRed -= 0.09f;
        if (this.posY == this.prevPosY) {
            this.motionX *= 1.1;
            this.motionZ *= 1.1;
        }
        this.particleScale *= 0.9599999785423279;
        this.motionX *= 0.9599999785423279;
        this.motionY *= 0.9599999785423279;
        this.motionZ *= 0.9599999785423279;
        final List<?> var3 = (List<?>)this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)this, this.boundingBox.expand(1.0, 0.5, 1.0));
        if (var3 != null) {
            for (int var4 = 0; var4 < var3.size(); ++var4) {
                final Entity var5 = (Entity)var3.get(var4);
                if (var5 instanceof EntityLivingBase && !var5.isDead && !var5.isBurning() && !var5.equals((Object)this.ridingEntity)) {
                    var5.setFire(3);
                    GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(PacketSimple.EnumSimplePacket.S_SET_ENTITY_FIRE, new Object[] { var5.getEntityId() }));
                }
            }
        }
    }
    
    public int getBrightnessForRender(final float par1) {
        return 15728880;
    }
    
    public float getBrightness(final float par1) {
        return 1.0f;
    }
}
