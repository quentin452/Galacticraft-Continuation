package micdoodle8.mods.galacticraft.core.client.render.entities;

import net.minecraft.client.renderer.entity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.client.model.*;
import net.minecraft.client.model.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import org.lwjgl.opengl.*;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.client.*;
import net.minecraft.client.entity.*;
import micdoodle8.mods.galacticraft.core.*;

@SideOnly(Side.CLIENT)
public class RenderEvolvedSkeletonBoss extends RenderLiving
{
    private static final ResourceLocation skeletonBossTexture;
    private static final ResourceLocation powerTexture;
    private final ModelEvolvedSkeletonBoss model;
    
    public RenderEvolvedSkeletonBoss() {
        super((ModelBase)new ModelEvolvedSkeletonBoss(), 1.0f);
        this.model = new ModelEvolvedSkeletonBoss();
    }
    
    protected ResourceLocation func_110779_a(final EntitySkeletonBoss par1EntityArrow) {
        return RenderEvolvedSkeletonBoss.skeletonBossTexture;
    }
    
    protected ResourceLocation getEntityTexture(final Entity par1Entity) {
        return this.func_110779_a((EntitySkeletonBoss)par1Entity);
    }
    
    protected void preRenderCallback(final EntityLivingBase par1EntityLiving, final float par2) {
        GL11.glScalef(1.2f, 1.2f, 1.2f);
    }
    
    public void doRender(final EntityLiving par1EntityLiving, final double par2, final double par4, final double par6, final float par8, final float par9) {
        BossStatus.setBossStatus((IBossDisplayData)par1EntityLiving, false);
        super.doRender(par1EntityLiving, par2, par4, par6, par8, par9);
    }
    
    protected void renderEquippedItems(final EntityLivingBase par1EntityLiving, final float par2) {
        if (((EntitySkeletonBoss)par1EntityLiving).throwTimer + ((EntitySkeletonBoss)par1EntityLiving).postThrowDelay == 0) {
            GL11.glPushMatrix();
            GL11.glTranslatef(-0.3f, -1.6f, -1.2f);
            GL11.glTranslatef(0.1f, 0.0f, 0.0f);
            GL11.glRotatef(41.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(-20.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(-20.0f, 0.0f, 0.0f, 1.0f);
            GL11.glScalef(0.7f, 0.7f, 0.7f);
            this.renderManager.itemRenderer.renderItem(par1EntityLiving, new ItemStack((Item)Items.bow), 0);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslatef(0.11f, -1.6f, -1.2f);
            GL11.glTranslatef(0.1f, 0.0f, 0.0f);
            GL11.glRotatef(46.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(-20.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(-20.0f, 0.0f, 0.0f, 1.0f);
            GL11.glScalef(0.7f, 0.7f, 0.7f);
            this.renderManager.itemRenderer.renderItem(par1EntityLiving, new ItemStack((Item)Items.bow), 0);
            GL11.glPopMatrix();
        }
    }
    
    protected int shouldRenderPass(final EntityLivingBase par1EntityLiving, final int par2, final float par3) {
        final Minecraft minecraft = FMLClientHandler.instance().getClient();
        final EntityPlayerSP player = (EntityPlayerSP)minecraft.thePlayer;
        ItemStack helmetSlot = null;
        if (player != null && player.inventory.armorItemInSlot(3) != null) {
            helmetSlot = player.inventory.armorItemInSlot(3);
        }
        if (helmetSlot != null && helmetSlot.getItem() instanceof ItemSensorGlasses && minecraft.currentScreen == null) {
            if (par2 == 1) {
                final float var4 = par1EntityLiving.ticksExisted * 2 + par3;
                this.bindTexture(RenderEvolvedSkeletonBoss.powerTexture);
                GL11.glMatrixMode(5890);
                GL11.glLoadIdentity();
                final float var5 = var4 * 0.01f;
                final float var6 = var4 * 0.01f;
                GL11.glTranslatef(var5, var6, 0.0f);
                this.setRenderPassModel((ModelBase)this.model);
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
    
    static {
        skeletonBossTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/skeletonboss.png");
        powerTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/power.png");
    }
}
