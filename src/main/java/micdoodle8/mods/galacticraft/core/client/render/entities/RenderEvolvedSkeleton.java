package micdoodle8.mods.galacticraft.core.client.render.entities;

import net.minecraft.client.renderer.entity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.client.model.*;
import net.minecraft.entity.monster.*;
import org.lwjgl.opengl.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.entity.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.client.model.*;
import net.minecraft.client.*;
import net.minecraft.client.entity.*;
import micdoodle8.mods.galacticraft.core.*;

@SideOnly(Side.CLIENT)
public class RenderEvolvedSkeleton extends RenderBiped
{
    private static final ResourceLocation skeletonTexture;
    private static final ResourceLocation powerTexture;
    private final ModelEvolvedSkeleton model;
    private static int isBG2Loaded;
    
    public RenderEvolvedSkeleton() {
        super((ModelBiped)new ModelEvolvedSkeleton(), 1.0f);
        this.model = new ModelEvolvedSkeleton(0.2f);
        try {
            final Class<?> clazz = Class.forName("mods.battlegear2.MobHookContainerClass");
            RenderEvolvedSkeleton.isBG2Loaded = clazz.getField("Skell_Arrow_Datawatcher").getInt(null);
        }
        catch (Exception ex) {}
    }
    
    protected ResourceLocation func_110779_a(final EntitySkeleton par1EntityArrow) {
        return RenderEvolvedSkeleton.skeletonTexture;
    }
    
    protected ResourceLocation getEntityTexture(final Entity par1Entity) {
        return this.func_110779_a((EntitySkeleton)par1Entity);
    }
    
    protected void preRenderCallback(final EntityLivingBase par1EntityLiving, final float par2) {
        GL11.glScalef(1.2f, 1.2f, 1.2f);
    }
    
    protected void renderEquippedItems(final EntityLivingBase par1EntityLiving, final float par2) {
        if (RenderEvolvedSkeleton.isBG2Loaded > 0 && par1EntityLiving.getDataWatcher().getWatchedObject(RenderEvolvedSkeleton.isBG2Loaded) == null) {
            par1EntityLiving.getDataWatcher().addObject(RenderEvolvedSkeleton.isBG2Loaded, (Object)(-1));
        }
        GL11.glPushMatrix();
        GL11.glTranslatef(-0.3f, -0.3f, -0.6f);
        GL11.glTranslatef(0.1f, 0.0f, 0.0f);
        GL11.glRotatef(41.0f, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-20.0f, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(-20.0f, 0.0f, 0.0f, 1.0f);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        this.renderManager.itemRenderer.renderItem(par1EntityLiving, new ItemStack((Item)Items.bow), 0);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(0.11f, -0.3f, -0.6f);
        GL11.glTranslatef(0.1f, 0.0f, 0.0f);
        GL11.glRotatef(46.0f, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-20.0f, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(-20.0f, 0.0f, 0.0f, 1.0f);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        this.renderManager.itemRenderer.renderItem(par1EntityLiving, new ItemStack((Item)Items.bow), 0);
        GL11.glPopMatrix();
        super.renderEquippedItems(par1EntityLiving, par2);
    }
    
    public void doRender(final EntityLiving par1EntityLiving, final double par2, final double par4, final double par6, final float par8, final float par9) {
        super.doRender(par1EntityLiving, par2, par4, par6, par8, par9);
        final ModelBiped field_82423_g = this.field_82423_g;
        final ModelBiped field_82425_h = this.field_82425_h;
        final ModelBiped modelBipedMain = this.modelBipedMain;
        final boolean aimedBow = true;
        modelBipedMain.aimedBow = aimedBow;
        field_82425_h.aimedBow = aimedBow;
        field_82423_g.aimedBow = aimedBow;
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
                this.bindTexture(RenderEvolvedSkeleton.powerTexture);
                GL11.glMatrixMode(5890);
                GL11.glLoadIdentity();
                final float var5 = var4 * 0.01f;
                final float var6 = var4 * 0.01f;
                GL11.glTranslatef(var5, var6, 0.0f);
                this.model.aimedBow = true;
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
        return super.shouldRenderPass(par1EntityLiving, par2, par3);
    }
    
    protected void renderEquippedItems(final EntityLiving par1EntityLiving, final float par2) {
        final ItemStack stack = par1EntityLiving.getLastActiveItems()[0];
        par1EntityLiving.getLastActiveItems()[0] = null;
        super.renderEquippedItems(par1EntityLiving, par2);
        par1EntityLiving.getLastActiveItems()[0] = stack;
    }
    
    static {
        skeletonTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/skeleton.png");
        powerTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/power.png");
        RenderEvolvedSkeleton.isBG2Loaded = 0;
    }
}
