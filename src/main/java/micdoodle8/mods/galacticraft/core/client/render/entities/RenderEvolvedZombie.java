package micdoodle8.mods.galacticraft.core.client.render.entities;

import net.minecraft.client.renderer.entity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.client.model.*;
import net.minecraft.client.model.*;
import net.minecraft.entity.*;
import org.lwjgl.opengl.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.client.*;
import net.minecraft.client.entity.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.*;

@SideOnly(Side.CLIENT)
public class RenderEvolvedZombie extends RenderBiped
{
    private static final ResourceLocation zombieTexture;
    private static final ResourceLocation powerTexture;
    private final ModelBase model;
    
    public RenderEvolvedZombie() {
        super((ModelBiped)new ModelEvolvedZombie(true), 0.5f);
        this.model = (ModelBase)new ModelEvolvedZombie(0.2f, false, true);
    }
    
    protected void func_82421_b() {
        this.field_82423_g = (ModelBiped)new ModelEvolvedZombie(1.0f, true, false);
        this.field_82425_h = (ModelBiped)new ModelEvolvedZombie(0.5f, true, false);
    }
    
    protected ResourceLocation getEntityTexture(final Entity par1Entity) {
        return RenderEvolvedZombie.zombieTexture;
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
        if (helmetSlot != null && helmetSlot.getItem() instanceof ItemSensorGlasses && minecraft.currentScreen == null) {
            if (par2 == 1) {
                final float var4 = par1EntityLiving.ticksExisted * 2 + par3;
                this.bindTexture(RenderEvolvedZombie.powerTexture);
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
        return super.shouldRenderPass(par1EntityLiving, par2, (float)par2);
    }
    
    static {
        zombieTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/zombie.png");
        powerTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/power.png");
    }
}
