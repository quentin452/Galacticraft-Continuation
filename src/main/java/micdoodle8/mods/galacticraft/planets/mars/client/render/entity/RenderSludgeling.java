package micdoodle8.mods.galacticraft.planets.mars.client.render.entity;

import net.minecraft.client.renderer.entity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.planets.mars.client.model.*;
import net.minecraft.client.model.*;
import micdoodle8.mods.galacticraft.planets.mars.entities.*;
import net.minecraft.entity.*;

@SideOnly(Side.CLIENT)
public class RenderSludgeling extends RenderLiving
{
    private static final ResourceLocation sludgelingTexture;
    
    public RenderSludgeling() {
        super((ModelBase)new ModelSludgeling(), 0.3f);
    }
    
    protected ResourceLocation func_110779_a(final EntitySludgeling par1EntityArrow) {
        return RenderSludgeling.sludgelingTexture;
    }
    
    protected ResourceLocation getEntityTexture(final Entity par1Entity) {
        return this.func_110779_a((EntitySludgeling)par1Entity);
    }
    
    public void renderSludgeling(final EntitySludgeling sludgeling, final double par2, final double par4, final double par6, final float par8, final float par9) {
        super.doRender((EntityLiving)sludgeling, par2, par4, par6, par8, par9);
    }
    
    protected float getDeathMaxRotation(final EntityLivingBase par1EntityLiving) {
        return 180.0f;
    }
    
    protected int shouldRenderPass(final EntityLivingBase par1EntityLiving, final int par2, final float par3) {
        return -1;
    }
    
    public void doRender(final EntityLiving par1EntityLiving, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.renderSludgeling((EntitySludgeling)par1EntityLiving, par2, par4, par6, par8, par9);
    }
    
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.renderSludgeling((EntitySludgeling)par1Entity, par2, par4, par6, par8, par9);
    }
    
    static {
        sludgelingTexture = new ResourceLocation("galacticraftmars", "textures/model/sludgeling.png");
    }
}
