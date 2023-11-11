package micdoodle8.mods.galacticraft.planets.asteroids.client.render.entity;

import net.minecraft.client.renderer.entity.*;
import net.minecraft.util.*;
import net.minecraftforge.client.model.*;
import net.minecraft.entity.*;
import org.lwjgl.opengl.*;

public class RenderEntryPod extends Render
{
    public static final ResourceLocation textureEntryPod;
    public static IModelCustom modelEntryPod;
    
    public RenderEntryPod(final IModelCustom model) {
        RenderEntryPod.modelEntryPod = model;
    }
    
    public void doRender(final Entity entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        GL11.glDisable(32826);
        GL11.glPushMatrix();
        final float var24 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * par9;
        final float var25 = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * par9;
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        GL11.glRotatef(180.0f - par8, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(180.0f - var24, 0.0f, 0.0f, 1.0f);
        GL11.glRotatef(-var25, 0.0f, 1.0f, 0.0f);
        this.bindEntityTexture(entity);
        GL11.glScalef(-1.0f, -1.0f, 1.0f);
        GL11.glScalef(0.65f, 0.6f, 0.65f);
        RenderEntryPod.modelEntryPod.renderAll();
        GL11.glPopMatrix();
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return RenderEntryPod.textureEntryPod;
    }
    
    static {
        textureEntryPod = new ResourceLocation("galacticraftasteroids", "textures/model/spacePod.png");
    }
}
