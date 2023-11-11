package micdoodle8.mods.galacticraft.core.client.render.entities;

import net.minecraft.client.renderer.entity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.model.*;
import micdoodle8.mods.galacticraft.core.client.model.*;
import net.minecraft.entity.monster.*;
import net.minecraft.util.*;
import org.lwjgl.opengl.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.client.*;
import net.minecraft.client.entity.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.*;

@SideOnly(Side.CLIENT)
public class RenderEvolvedCreeper extends RenderCreeper
{
    private static final ResourceLocation creeperTexture;
    private static final ResourceLocation powerTexture;
    private final ModelBase creeperModel;
    
    public RenderEvolvedCreeper() {
        this.creeperModel = (ModelBase)new ModelEvolvedCreeper(0.2f);
        this.mainModel = (ModelBase)new ModelEvolvedCreeper();
    }
    
    protected ResourceLocation func_110779_a(final EntityCreeper par1EntityArrow) {
        return RenderEvolvedCreeper.creeperTexture;
    }
    
    protected ResourceLocation getEntityTexture(final Entity par1Entity) {
        return this.func_110779_a((EntityCreeper)par1Entity);
    }
    
    protected void updateCreeperScale(final EntityCreeper par1GCEntityCreeper, final float par2) {
        float var4 = par1GCEntityCreeper.getCreeperFlashIntensity(par2);
        final float var5 = 1.0f + MathHelper.sin(var4 * 100.0f) * var4 * 0.01f;
        if (var4 < 0.0f) {
            var4 = 0.0f;
        }
        if (var4 > 1.0f) {
            var4 = 1.0f;
        }
        var4 *= var4;
        var4 *= var4;
        final float var6 = (1.0f + var4 * 0.4f) * var5;
        final float var7 = (1.0f + var4 * 0.1f) / var5;
        GL11.glScalef(0.2f + var6, 0.2f + var7, 0.2f + var6);
    }
    
    protected int updateCreeperColorMultiplier(final EntityCreeper par1GCEntityCreeper, final float par2, final float par3) {
        final float var5 = par1GCEntityCreeper.getCreeperFlashIntensity(par3);
        if ((int)(var5 * 10.0f) % 2 == 0) {
            return 0;
        }
        int var6 = (int)(var5 * 0.2f * 255.0f);
        if (var6 < 0) {
            var6 = 0;
        }
        if (var6 > 255) {
            var6 = 255;
        }
        final short var7 = 255;
        final short var8 = 255;
        final short var9 = 255;
        return var6 << 24 | 0xFF0000 | 0xFF00 | 0xFF;
    }
    
    protected int func_77061_b(final EntityCreeper par1GCEntityCreeper, final int par2, final float par3) {
        return -1;
    }
    
    protected void preRenderCallback(final EntityLivingBase par1EntityLiving, final float par2) {
        this.updateCreeperScale((EntityCreeper)par1EntityLiving, par2);
    }
    
    protected int getColorMultiplier(final EntityLivingBase par1EntityLiving, final float par2, final float par3) {
        return this.updateCreeperColorMultiplier((EntityCreeper)par1EntityLiving, par2, par3);
    }
    
    protected int shouldRenderPass(final EntityLivingBase par1EntityLiving, final int par2, final float par3) {
        final EntityEvolvedCreeper creeper = (EntityEvolvedCreeper)par1EntityLiving;
        final Minecraft minecraft = FMLClientHandler.instance().getClient();
        final EntityPlayerSP player = (EntityPlayerSP)minecraft.thePlayer;
        ItemStack helmetSlot = null;
        if (player != null && player.inventory.armorItemInSlot(3) != null) {
            helmetSlot = player.inventory.armorItemInSlot(3);
        }
        if (helmetSlot != null && helmetSlot.getItem() instanceof ItemSensorGlasses && minecraft.currentScreen == null) {
            if (par2 == 1) {
                final float var4 = creeper.ticksExisted * 2 + par3;
                this.bindTexture(RenderEvolvedCreeper.powerTexture);
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
        return super.shouldRenderPass((EntityCreeper)creeper, par2, par3);
    }
    
    protected int inheritRenderPass(final EntityLivingBase par1EntityLiving, final int par2, final float par3) {
        return this.func_77061_b((EntityCreeper)par1EntityLiving, par2, par3);
    }
    
    static {
        creeperTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/creeper.png");
        powerTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/power.png");
    }
}
