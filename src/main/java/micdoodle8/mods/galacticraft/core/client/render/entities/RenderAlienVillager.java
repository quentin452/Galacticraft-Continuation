package micdoodle8.mods.galacticraft.core.client.render.entities;

import net.minecraft.client.renderer.entity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.client.model.*;
import net.minecraft.client.model.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import org.lwjgl.opengl.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.*;

@SideOnly(Side.CLIENT)
public class RenderAlienVillager extends RenderLiving
{
    private static final ResourceLocation villagerTexture;
    protected ModelAlienVillager villagerModel;
    
    public RenderAlienVillager() {
        super((ModelBase)new ModelAlienVillager(0.0f), 0.5f);
        this.villagerModel = (ModelAlienVillager)this.mainModel;
    }
    
    protected int shouldVillagerRenderPass(final EntityAlienVillager par1EntityVillager, final int par2, final float par3) {
        return -1;
    }
    
    public void renderVillager(final EntityAlienVillager par1EntityVillager, final double par2, final double par4, final double par6, final float par8, final float par9) {
        super.doRender((EntityLiving)par1EntityVillager, par2, par4, par6, par8, par9);
    }
    
    protected void renderVillagerEquipedItems(final EntityAlienVillager par1EntityVillager, final float par2) {
        super.renderEquippedItems((EntityLivingBase)par1EntityVillager, par2);
    }
    
    protected void preRenderVillager(final EntityAlienVillager par1EntityVillager, final float par2) {
        float f1 = 0.9375f;
        if (par1EntityVillager.getGrowingAge() < 0) {
            f1 *= 0.5;
            this.shadowSize = 0.25f;
        }
        else {
            this.shadowSize = 0.5f;
        }
        GL11.glScalef(f1, f1, f1);
    }
    
    public void doRender(final EntityLiving par1EntityLiving, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.renderVillager((EntityAlienVillager)par1EntityLiving, par2, par4, par6, par8, par9);
    }
    
    protected void preRenderCallback(final EntityLivingBase par1EntityLivingBase, final float par2) {
        this.preRenderVillager((EntityAlienVillager)par1EntityLivingBase, par2);
    }
    
    protected int shouldRenderPass(final EntityLivingBase par1EntityLivingBase, final int par2, final float par3) {
        return this.shouldVillagerRenderPass((EntityAlienVillager)par1EntityLivingBase, par2, par3);
    }
    
    protected void renderEquippedItems(final EntityLivingBase par1EntityLivingBase, final float par2) {
        this.renderVillagerEquipedItems((EntityAlienVillager)par1EntityLivingBase, par2);
    }
    
    public void doRender(final EntityLivingBase par1EntityLivingBase, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.renderVillager((EntityAlienVillager)par1EntityLivingBase, par2, par4, par6, par8, par9);
    }
    
    protected ResourceLocation getEntityTexture(final Entity par1Entity) {
        return RenderAlienVillager.villagerTexture;
    }
    
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.renderVillager((EntityAlienVillager)par1Entity, par2, par4, par6, par8, par9);
    }
    
    static {
        villagerTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/villager.png");
    }
}
