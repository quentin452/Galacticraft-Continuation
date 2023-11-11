package micdoodle8.mods.galacticraft.planets.mars.client.render.entity;

import net.minecraft.client.renderer.entity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import net.minecraftforge.client.model.*;
import micdoodle8.mods.galacticraft.planets.mars.entities.*;
import net.minecraft.entity.*;
import org.lwjgl.opengl.*;

@SideOnly(Side.CLIENT)
public class RenderCargoRocket extends Render
{
    private static final ResourceLocation cargoRocketTexture;
    protected IModelCustom rocketModel;
    
    public RenderCargoRocket(final IModelCustom model) {
        this.shadowSize = 0.5f;
        this.rocketModel = model;
    }
    
    protected ResourceLocation func_110779_a(final EntityCargoRocket par1EntityArrow) {
        return RenderCargoRocket.cargoRocketTexture;
    }
    
    protected ResourceLocation getEntityTexture(final Entity par1Entity) {
        return this.func_110779_a((EntityCargoRocket)par1Entity);
    }
    
    public void renderBuggy(final EntityCargoRocket entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        GL11.glPushMatrix();
        final float var24 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * par9;
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        GL11.glScalef(0.4f, 0.4f, 0.4f);
        GL11.glRotatef(180.0f - par8, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-var24, 0.0f, 0.0f, 1.0f);
        this.bindTexture(RenderCargoRocket.cargoRocketTexture);
        this.rocketModel.renderAll();
        GL11.glPopMatrix();
    }
    
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.renderBuggy((EntityCargoRocket)par1Entity, par2, par4, par6, par8, par9);
    }
    
    static {
        cargoRocketTexture = new ResourceLocation("galacticraftmars", "textures/model/cargoRocket.png");
    }
}
