package micdoodle8.mods.galacticraft.core.client.render.entities;

import net.minecraft.client.renderer.entity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.client.model.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraft.entity.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.*;

@SideOnly(Side.CLIENT)
public class RenderLander extends Render
{
    private static final ResourceLocation landerTexture;
    protected ModelLander landerModel;
    
    public RenderLander() {
        this.shadowSize = 2.0f;
        this.landerModel = new ModelLander();
    }
    
    protected ResourceLocation func_110779_a(final EntityLander par1EntityArrow) {
        return RenderLander.landerTexture;
    }
    
    protected ResourceLocation getEntityTexture(final Entity par1Entity) {
        return this.func_110779_a((EntityLander)par1Entity);
    }
    
    public void renderLander(final EntityLander entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        GL11.glPushMatrix();
        final float var24 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * par9;
        GL11.glTranslatef((float)par2, (float)par4 + 1.55f, (float)par6);
        GL11.glRotatef(180.0f - par8, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-var24, 0.0f, 0.0f, 1.0f);
        final float f6 = entity.timeSinceHit - par9;
        float f7 = entity.currentDamage - par9;
        if (f7 < 0.0f) {
            f7 = 0.0f;
        }
        if (f6 > 0.0f) {
            GL11.glRotatef((float)Math.sin(f6) * 0.2f * f6 * f7 / 25.0f, 1.0f, 0.0f, 0.0f);
        }
        this.bindEntityTexture((Entity)entity);
        GL11.glScalef(-1.0f, -1.0f, 1.0f);
        this.landerModel.render((Entity)entity, 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        GL11.glPopMatrix();
    }
    
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.renderLander((EntityLander)par1Entity, par2, par4, par6, par8, par9);
    }
    
    static {
        landerTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/lander.png");
    }
}
