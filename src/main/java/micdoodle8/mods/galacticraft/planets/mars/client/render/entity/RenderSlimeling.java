package micdoodle8.mods.galacticraft.planets.mars.client.render.entity;

import net.minecraft.client.renderer.entity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.planets.mars.client.model.*;
import net.minecraft.client.model.*;
import micdoodle8.mods.galacticraft.planets.mars.entities.*;
import net.minecraft.entity.*;
import org.lwjgl.opengl.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.planets.mars.client.gui.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.gui.*;

@SideOnly(Side.CLIENT)
public class RenderSlimeling extends RenderLiving
{
    private static final ResourceLocation landerTexture;
    
    public RenderSlimeling() {
        super((ModelBase)new ModelSlimeling(16.0f), 0.5f);
        this.renderPassModel = (ModelBase)new ModelSlimeling(0.0f);
    }
    
    protected ResourceLocation func_110779_a(final EntitySlimeling par1EntityArrow) {
        return RenderSlimeling.landerTexture;
    }
    
    protected ResourceLocation getEntityTexture(final Entity par1Entity) {
        return this.func_110779_a((EntitySlimeling)par1Entity);
    }
    
    protected void preRenderCallback(final EntityLivingBase par1EntityLivingBase, final float par2) {
        super.preRenderCallback(par1EntityLivingBase, par2);
        GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
        final EntitySlimeling slimeling = (EntitySlimeling)par1EntityLivingBase;
        GL11.glColor3f(slimeling.getColorRed(), slimeling.getColorGreen(), slimeling.getColorBlue());
        GL11.glScalef(slimeling.getScale(), slimeling.getScale(), slimeling.getScale());
        GL11.glTranslatef(0.0f, 1.1f, 0.0f);
    }
    
    protected int shouldRenderPass(final EntityLivingBase par1EntityLivingBase, final int par2, final float par3) {
        if (par1EntityLivingBase.isInvisible()) {
            return 0;
        }
        if (par2 == 0) {
            this.setRenderPassModel(this.renderPassModel);
            GL11.glEnable(2977);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            return 1;
        }
        if (par2 == 1) {
            GL11.glDisable(3042);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        return -1;
    }
    
    protected void passSpecialRender(final EntityLivingBase par1EntityLivingBase, final double par2, final double par4, final double par6) {
        final Minecraft mc = FMLClientHandler.instance().getClient();
        if (!mc.gameSettings.hideGUI && !par1EntityLivingBase.isInvisible() && (mc.currentScreen == null || (!(mc.currentScreen instanceof GuiSlimeling) && !(mc.currentScreen instanceof GuiSlimelingInventory)) || !GuiSlimeling.renderingOnGui)) {
            this.renderLivingLabelWithColor(par1EntityLivingBase, ((EntitySlimeling)par1EntityLivingBase).getName(), par2, par4 + 0.33, par6, 64, 0.0f, 0.0f, 0.0f);
            int health = (int)Math.floor(((EntitySlimeling)par1EntityLivingBase).getHealth() + 0.6);
            final int maxHealth = (int)((EntitySlimeling)par1EntityLivingBase).getMaxHealth();
            if (health > maxHealth) {
                health = maxHealth;
            }
            final float difference = health / (float)maxHealth;
            if (difference < 0.33333f) {
                this.renderLivingLabelWithColor(par1EntityLivingBase, "" + health + " / " + maxHealth, par2, par4, par6, 64, 1.0f, 0.0f, 0.0f);
            }
            else if (difference < 0.66666f) {
                this.renderLivingLabelWithColor(par1EntityLivingBase, "" + health + " / " + maxHealth, par2, par4, par6, 64, 1.0f, 1.0f, 0.0f);
            }
            else {
                this.renderLivingLabelWithColor(par1EntityLivingBase, "" + health + " / " + maxHealth, par2, par4, par6, 64, 0.0f, 1.0f, 0.0f);
            }
        }
        super.passSpecialRender(par1EntityLivingBase, par2, par4, par6);
        GL11.glDisable(2977);
        GL11.glDisable(3042);
    }
    
    protected void renderLivingLabelWithColor(final EntityLivingBase par1EntityLivingBase, final String par2Str, final double par3, final double par5, final double par7, final int par9, final float cR, final float cG, final float cB) {
        final double d3 = par1EntityLivingBase.getDistanceSqToEntity((Entity)this.renderManager.livingPlayer);
        if (d3 <= par9 * par9) {
            final FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
            final float f = 1.6f;
            final float f2 = 0.016666668f * f;
            GL11.glPushMatrix();
            GL11.glTranslatef((float)par3 + 0.0f, (float)par5 + par1EntityLivingBase.height + 0.55f, (float)par7);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            GL11.glRotatef(-this.renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(this.renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
            GL11.glScalef(-f2, -f2, f2);
            GL11.glDisable(2896);
            GL11.glDepthMask(false);
            GL11.glDisable(2929);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            final Tessellator tessellator = Tessellator.instance;
            byte b0 = 0;
            if (par2Str.equals("deadmau5")) {
                b0 = -10;
            }
            GL11.glDisable(3553);
            tessellator.startDrawingQuads();
            final int j = fontrenderer.getStringWidth(par2Str) / 2;
            tessellator.setColorRGBA_F(cR, cG, cB, 0.25f);
            tessellator.addVertex((double)(-j - 1), (double)(-1 + b0), 0.0);
            tessellator.addVertex((double)(-j - 1), (double)(8 + b0), 0.0);
            tessellator.addVertex((double)(j + 1), (double)(8 + b0), 0.0);
            tessellator.addVertex((double)(j + 1), (double)(-1 + b0), 0.0);
            tessellator.draw();
            GL11.glEnable(3553);
            fontrenderer.drawString(par2Str, -fontrenderer.getStringWidth(par2Str) / 2, (int)b0, 553648127);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            fontrenderer.drawString(par2Str, -fontrenderer.getStringWidth(par2Str) / 2, (int)b0, -1);
            GL11.glEnable(2896);
            GL11.glDisable(3042);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
    }
    
    static {
        landerTexture = new ResourceLocation("galacticraftmars", "textures/model/slimeling/green.png");
    }
}
