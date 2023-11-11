package micdoodle8.mods.galacticraft.core.client.render.entities;

import net.minecraft.client.renderer.entity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import net.minecraft.client.model.*;
import micdoodle8.mods.galacticraft.core.client.model.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.*;
import org.lwjgl.opengl.*;
import cpw.mods.fml.client.*;
import net.minecraft.client.renderer.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.client.*;
import net.minecraft.client.entity.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.*;

@SideOnly(Side.CLIENT)
public class RenderEvolvedSpider extends RenderLiving
{
    private static final ResourceLocation spiderEyesTextures;
    private static final ResourceLocation spiderTexture;
    private static final ResourceLocation powerTexture;
    private final ModelBase model;
    
    public RenderEvolvedSpider() {
        super((ModelBase)new ModelEvolvedSpider(), 1.0f);
        this.model = (ModelBase)new ModelEvolvedSpider(0.2f);
        this.setRenderPassModel((ModelBase)new ModelEvolvedSpider());
    }
    
    protected ResourceLocation func_110779_a(final EntitySpider par1EntityArrow) {
        return RenderEvolvedSpider.spiderTexture;
    }
    
    protected ResourceLocation getEntityTexture(final Entity par1Entity) {
        return this.func_110779_a((EntitySpider)par1Entity);
    }
    
    protected float setSpiderDeathMaxRotation(final EntitySpider par1EntitySpider) {
        return 180.0f;
    }
    
    protected void preRenderCallback(final EntityLivingBase par1EntityLiving, final float par2) {
        GL11.glScalef(1.2f, 1.2f, 1.2f);
    }
    
    protected int shouldRenderPass(final EntityLivingBase par1EntityLiving, final int par2, final float par3) {
        final Minecraft minecraft = FMLClientHandler.instance().getClient();
        final EntityPlayerSP player = (EntityPlayerSP)minecraft.thePlayer;
        ItemStack helmetSlot = null;
        if (player != null && player.inventory.armorItemInSlot(3) != null) {
            helmetSlot = player.inventory.armorItemInSlot(3);
        }
        if (par2 == 3) {
            this.bindTexture(RenderEvolvedSpider.spiderEyesTextures);
            GL11.glEnable(3042);
            GL11.glDisable(3008);
            GL11.glBlendFunc(770, 771);
            if (par1EntityLiving.isInvisible()) {
                GL11.glDepthMask(false);
            }
            else {
                GL11.glDepthMask(true);
            }
            final char c0 = '\uf0f0';
            final int j = c0 % 65536;
            final int k = c0 / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0f, k / 1.0f);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            return 1;
        }
        if (helmetSlot != null && helmetSlot.getItem() instanceof ItemSensorGlasses && minecraft.currentScreen == null) {
            if (par2 == 1) {
                final float var4 = par1EntityLiving.ticksExisted * 2 + par3;
                this.bindTexture(RenderEvolvedSpider.powerTexture);
                GL11.glMatrixMode(5890);
                GL11.glLoadIdentity();
                final float var5 = var4 * 0.01f;
                final float var6 = var4 * 0.01f;
                GL11.glTranslatef(var5, var6, 0.0f);
                this.setRenderPassModel(this.model);
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
    
    protected float getDeathMaxRotation(final EntityLivingBase par1EntityLiving) {
        return this.setSpiderDeathMaxRotation((EntitySpider)par1EntityLiving);
    }
    
    static {
        spiderEyesTextures = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/spider_eyes.png");
        spiderTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/spider.png");
        powerTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/power.png");
    }
}
