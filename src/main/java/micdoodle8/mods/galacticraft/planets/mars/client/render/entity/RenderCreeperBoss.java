package micdoodle8.mods.galacticraft.planets.mars.client.render.entity;

import net.minecraft.client.renderer.entity.*;
import net.minecraft.util.*;
import net.minecraft.client.model.*;
import micdoodle8.mods.galacticraft.planets.mars.client.model.*;
import net.minecraft.entity.boss.*;
import micdoodle8.mods.galacticraft.planets.mars.entities.*;
import org.lwjgl.opengl.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.*;

public class RenderCreeperBoss extends RenderLiving
{
    private static final ResourceLocation creeperTexture;
    private static final ResourceLocation powerTexture;
    private final ModelBase creeperModel;
    
    public RenderCreeperBoss() {
        super((ModelBase)new ModelCreeperBoss(), 1.0f);
        this.creeperModel = (ModelBase)new ModelCreeperBoss(2.0f);
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return RenderCreeperBoss.creeperTexture;
    }
    
    public void doRender(final EntityLiving par1EntityLiving, final double par2, final double par4, final double par6, final float par8, final float par9) {
        BossStatus.setBossStatus((IBossDisplayData)par1EntityLiving, false);
        super.doRender(par1EntityLiving, par2, par4, par6, par8, par9);
    }
    
    protected int func_27006_a(final EntityCreeperBoss par1EntityCreeper, final int par2, final float par3) {
        if (par1EntityCreeper.headsRemaining == 1) {
            if (par2 == 1) {
                final float var4 = par1EntityCreeper.ticksExisted + par3;
                this.bindTexture(RenderCreeperBoss.powerTexture);
                GL11.glMatrixMode(5890);
                GL11.glLoadIdentity();
                final float var5 = var4 * 0.01f;
                final float var6 = var4 * 0.01f;
                GL11.glTranslatef(var5, var6, 0.0f);
                this.setRenderPassModel(this.creeperModel);
                GL11.glMatrixMode(5888);
                GL11.glEnable(3042);
                final float var7 = 0.5f;
                GL11.glColor4f(0.5f, 0.5f, 0.5f, 1.0f);
                GL11.glDisable(2896);
                GL11.glBlendFunc(1, 1);
                return 1;
            }
            if (par2 == 2) {
                GL11.glMatrixMode(5890);
                GL11.glLoadIdentity();
                GL11.glMatrixMode(5888);
                GL11.glEnable(2896);
                GL11.glDisable(3042);
            }
        }
        return -1;
    }
    
    protected void preRenderCallback(final EntityLivingBase par1EntityLiving, final float par2) {
        GL11.glScalef(4.0f, 4.0f, 4.0f);
    }
    
    protected int getColorMultiplier(final EntityLivingBase par1EntityLivingBase, final float par2, final float par3) {
        return super.getColorMultiplier(par1EntityLivingBase, par2, par3);
    }
    
    protected int shouldRenderPass(final EntityLivingBase par1EntityLivingBase, final int par2, final float par3) {
        return this.func_27006_a((EntityCreeperBoss)par1EntityLivingBase, par2, par3);
    }
    
    protected int inheritRenderPass(final EntityLivingBase par1EntityLivingBase, final int par2, final float par3) {
        return -1;
    }
    
    static {
        creeperTexture = new ResourceLocation("galacticraftmars", "textures/model/creeper.png");
        powerTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/power.png");
    }
}
