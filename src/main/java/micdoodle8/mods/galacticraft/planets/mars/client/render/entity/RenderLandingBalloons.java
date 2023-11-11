package micdoodle8.mods.galacticraft.planets.mars.client.render.entity;

import net.minecraft.client.renderer.entity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.planets.mars.client.model.*;
import net.minecraftforge.client.model.*;
import micdoodle8.mods.galacticraft.planets.mars.entities.*;
import net.minecraft.entity.*;
import org.lwjgl.opengl.*;

@SideOnly(Side.CLIENT)
public class RenderLandingBalloons extends Render
{
    private static final ResourceLocation landerTexture;
    protected IModelCustom landerModel;
    protected ModelBalloonParachute parachuteModel;
    
    public RenderLandingBalloons() {
        this.parachuteModel = new ModelBalloonParachute();
        this.shadowSize = 1.2f;
        this.landerModel = AdvancedModelLoader.loadModel(new ResourceLocation("galacticraftmars", "models/landingBalloon.obj"));
    }
    
    protected ResourceLocation func_110779_a(final EntityLandingBalloons par1EntityArrow) {
        return RenderLandingBalloons.landerTexture;
    }
    
    protected ResourceLocation getEntityTexture(final Entity par1Entity) {
        return this.func_110779_a((EntityLandingBalloons)par1Entity);
    }
    
    public void renderLander(final EntityLandingBalloons entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        GL11.glPushMatrix();
        final float var24 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * par9;
        GL11.glTranslatef((float)par2, (float)par4 + 0.8f, (float)par6);
        GL11.glRotatef(par8, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(var24, 0.0f, 0.0f, 1.0f);
        this.bindEntityTexture((Entity)entity);
        GL11.glScalef(-1.0f, -1.0f, 1.0f);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        this.landerModel.renderAll();
        GL11.glPopMatrix();
        if (entity.posY >= 500.0) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)par2 - 1.25f, (float)par4 - 0.93f, (float)par6 - 0.3f);
            GL11.glScalef(2.5f, 3.0f, 2.5f);
            this.parachuteModel.renderAll();
            GL11.glPopMatrix();
        }
    }
    
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.renderLander((EntityLandingBalloons)par1Entity, par2, par4, par6, par8, par9);
    }
    
    static {
        landerTexture = new ResourceLocation("galacticraftmars", "textures/model/landingBalloon.png");
    }
}
