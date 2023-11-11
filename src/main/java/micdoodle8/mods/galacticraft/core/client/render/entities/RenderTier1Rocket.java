package micdoodle8.mods.galacticraft.core.client.render.entities;

import net.minecraft.client.renderer.entity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.model.*;
import net.minecraftforge.client.model.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;
import org.lwjgl.opengl.*;
import net.minecraft.util.*;

@SideOnly(Side.CLIENT)
public class RenderTier1Rocket extends Render
{
    private ResourceLocation spaceshipTexture;
    protected ModelBase modelSpaceship;
    protected IModelCustom modelSpaceshipObj;
    
    public RenderTier1Rocket(final ModelBase spaceshipModel, final String textureDomain, final String texture) {
        this(new ResourceLocation(textureDomain, "textures/model/" + texture + ".png"));
        this.modelSpaceship = spaceshipModel;
    }
    
    public RenderTier1Rocket(final IModelCustom spaceshipModel, final String textureDomain, final String texture) {
        this(new ResourceLocation(textureDomain, "textures/model/" + texture + ".png"));
        this.modelSpaceshipObj = spaceshipModel;
    }
    
    private RenderTier1Rocket(final ResourceLocation texture) {
        this.spaceshipTexture = texture;
        this.shadowSize = 2.0f;
    }
    
    protected ResourceLocation func_110779_a(final Entity par1EntityArrow) {
        return this.spaceshipTexture;
    }
    
    protected ResourceLocation getEntityTexture(final Entity par1Entity) {
        return this.func_110779_a(par1Entity);
    }
    
    public void renderSpaceship(final EntitySpaceshipBase entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        GL11.glPushMatrix();
        final float var24 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * par9;
        final float var25 = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * par9;
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        GL11.glRotatef(180.0f - par8, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-var24, 0.0f, 0.0f, 1.0f);
        GL11.glRotatef(-var25, 0.0f, 1.0f, 0.0f);
        final float var26 = entity.rollAmplitude - par9;
        float var27 = entity.shipDamage - par9;
        if (var27 < 0.0f) {
            var27 = 0.0f;
        }
        if (var26 > 0.0f) {
            final float i = entity.getLaunched() ? ((5 - MathHelper.floor_double((double)(entity.timeUntilLaunch / 85))) / 10.0f) : 0.3f;
            GL11.glRotatef(MathHelper.sin(var26) * var26 * i * par9, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(MathHelper.sin(var26) * var26 * i * par9, 1.0f, 0.0f, 1.0f);
        }
        this.bindEntityTexture((Entity)entity);
        GL11.glScalef(-1.0f, -1.0f, 1.0f);
        if (this.modelSpaceshipObj != null) {
            this.modelSpaceshipObj.renderAll();
        }
        else {
            this.modelSpaceship.render((Entity)entity, 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        }
        GL11.glPopMatrix();
    }
    
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.renderSpaceship((EntitySpaceshipBase)par1Entity, par2, par4, par6, par8, par9);
    }
}
