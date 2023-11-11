package micdoodle8.mods.galacticraft.core.client.render.entities;

import net.minecraft.client.renderer.entity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.client.model.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.*;

@SideOnly(Side.CLIENT)
public class RenderParaChest extends Render
{
    private static final ResourceLocation parachestTexture;
    private final ModelParaChest chestModel;
    
    public RenderParaChest() {
        this.shadowSize = 1.0f;
        this.chestModel = new ModelParaChest();
    }
    
    protected ResourceLocation func_110779_a(final Entity par1EntityArrow) {
        return RenderParaChest.parachestTexture;
    }
    
    protected ResourceLocation getEntityTexture(final Entity par1Entity) {
        return this.func_110779_a(par1Entity);
    }
    
    public void doRenderParaChest(final EntityParachest entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par2 - 0.5f, (float)par4, (float)par6);
        this.bindEntityTexture((Entity)entity);
        if (!entity.isDead) {
            this.chestModel.renderAll();
        }
        GL11.glPopMatrix();
    }
    
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.doRenderParaChest((EntityParachest)par1Entity, par2, par4, par6, par8, par9);
    }
    
    static {
        parachestTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/parachest.png");
    }
}
