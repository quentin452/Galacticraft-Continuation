package micdoodle8.mods.galacticraft.core.client.fx;

import net.minecraft.client.particle.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.client.renderer.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.network.*;
import java.util.*;

@SideOnly(Side.CLIENT)
public class EntityFXLaunchFlame extends EntityFX
{
    private float smokeParticleScale;
    private boolean spawnSmokeShort;
    private EntityLivingBase ridingEntity;
    
    public EntityFXLaunchFlame(final World par1World, final Vector3 position, final Vector3 motion, final boolean launched, final EntityLivingBase ridingEntity) {
        super(par1World, position.x, position.y, position.z, 0.0, 0.0, 0.0);
        this.motionX *= 0.10000000149011612;
        this.motionY *= 0.10000000149011612;
        this.motionZ *= 0.10000000149011612;
        this.motionX += motion.x;
        this.motionY += motion.y;
        this.motionZ += motion.z;
        this.particleRed = 1.0f;
        this.particleGreen = 0.47058824f + this.rand.nextFloat() / 3.0f;
        this.particleBlue = 0.21568628f;
        this.particleScale *= 2.0f;
        this.particleScale *= 2.0f;
        this.smokeParticleScale = this.particleScale;
        this.particleMaxAge *= (int)1.0f;
        this.noClip = false;
        this.spawnSmokeShort = launched;
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
            GalacticraftCore.proxy.spawnParticle(this.spawnSmokeShort ? "whiteSmokeLaunched" : "whiteSmokeIdle", new Vector3(this.posX, this.posY + this.rand.nextDouble() * 2.0, this.posZ), new Vector3(this.motionX, this.motionY, this.motionZ), new Object[0]);
            GalacticraftCore.proxy.spawnParticle(this.spawnSmokeShort ? "whiteSmokeLargeLaunched" : "whiteSmokeLargeIdle", new Vector3(this.posX, this.posY + this.rand.nextDouble() * 2.0, this.posZ), new Vector3(this.motionX, this.motionY, this.motionZ), new Object[0]);
            if (!this.spawnSmokeShort) {
                GalacticraftCore.proxy.spawnParticle("whiteSmokeIdle", new Vector3(this.posX, this.posY + this.rand.nextDouble() * 2.0, this.posZ), new Vector3(this.motionX, this.motionY, this.motionZ), new Object[0]);
                GalacticraftCore.proxy.spawnParticle("whiteSmokeLargeIdle", new Vector3(this.posX, this.posY + this.rand.nextDouble() * 2.0, this.posZ), new Vector3(this.motionX, this.motionY, this.motionZ), new Object[0]);
            }
            this.setDead();
        }
        this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
        this.motionY += 0.001;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.particleGreen += 0.01f;
        if (this.posY == this.prevPosY) {
            this.motionX *= 1.1;
            this.motionZ *= 1.1;
        }
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
