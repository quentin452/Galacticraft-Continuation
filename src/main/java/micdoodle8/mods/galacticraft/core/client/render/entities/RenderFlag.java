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
public class RenderFlag extends Render
{
    public static ResourceLocation flagTexture;
    protected ModelFlag modelFlag;
    
    public RenderFlag() {
        this.shadowSize = 1.0f;
        this.modelFlag = new ModelFlag();
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return RenderFlag.flagTexture;
    }
    
    public void renderFlag(final EntityFlag entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        GL11.glPushMatrix();
        long var10 = entity.getEntityId() * 493286711L;
        var10 = var10 * var10 * 4392167121L + var10 * 98761L;
        final float var11 = (((var10 >> 16 & 0x7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        final float var12 = (((var10 >> 20 & 0x7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        final float var13 = (((var10 >> 24 & 0x7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        GL11.glTranslatef(var11, var12, var13);
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        GL11.glRotatef(180.0f - entity.getFacingAngle(), 0.0f, 1.0f, 0.0f);
        this.bindEntityTexture((Entity)entity);
        GL11.glScalef(-1.0f, -1.0f, 1.0f);
        this.modelFlag.render((Entity)entity, 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        GL11.glPopMatrix();
    }
    
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.renderFlag((EntityFlag)par1Entity, par2, par4, par6, par8, par9);
    }
    
    static {
        RenderFlag.flagTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/flag.png");
    }
}
