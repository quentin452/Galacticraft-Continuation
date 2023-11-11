package micdoodle8.mods.galacticraft.core.client.render.entities;

import net.minecraft.client.renderer.entity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.client.model.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraft.entity.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.*;

@SideOnly(Side.CLIENT)
public class RenderMeteor extends Render
{
    private static final ResourceLocation meteorTexture;
    private final ModelMeteor modelMeteor;
    
    public RenderMeteor() {
        this.shadowSize = 1.0f;
        this.modelMeteor = new ModelMeteor();
    }
    
    protected ResourceLocation func_110779_a(final EntityMeteor entity) {
        return RenderMeteor.meteorTexture;
    }
    
    protected ResourceLocation getEntityTexture(final Entity par1Entity) {
        return this.func_110779_a((EntityMeteor)par1Entity);
    }
    
    public void doRenderMeteor(final EntityMeteor entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        GL11.glRotatef(par8, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(par8, 1.0f, 0.0f, 0.0f);
        final float f = (float)entity.getSize();
        GL11.glScalef(f / 2.0f, f / 2.0f, f / 2.0f);
        this.bindEntityTexture((Entity)entity);
        this.modelMeteor.render((Entity)entity, 0.0f, 0.0f, -0.5f, 0.0f, 0.0f, 0.1f);
        GL11.glPopMatrix();
    }
    
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.doRenderMeteor((EntityMeteor)par1Entity, par2, par4, par6, par8, par9);
    }
    
    static {
        meteorTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/meteor.png");
    }
}
