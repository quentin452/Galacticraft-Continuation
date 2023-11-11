package micdoodle8.mods.galacticraft.core.client.render.entities;

import net.minecraft.client.renderer.entity.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraftforge.client.model.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraft.entity.*;
import org.lwjgl.opengl.*;

public class RenderMeteorChunk extends Render
{
    private static final ResourceLocation meteorChunkTexture;
    private static final ResourceLocation meteorChunkHotTexture;
    private final IModelCustom meteorChunkModel;
    
    public RenderMeteorChunk() {
        this.meteorChunkModel = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/meteorChunk.obj"));
        this.shadowSize = 0.1f;
    }
    
    protected ResourceLocation func_110779_a(final EntityMeteorChunk par1EntityArrow) {
        return RenderMeteorChunk.meteorChunkTexture;
    }
    
    protected ResourceLocation getEntityTexture(final Entity par1Entity) {
        return this.func_110779_a((EntityMeteorChunk)par1Entity);
    }
    
    public void renderMeteorChunk(final EntityMeteorChunk entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        GL11.glPushMatrix();
        final float var24 = entity.rotationPitch;
        final float var24b = entity.rotationYaw;
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        GL11.glScalef(0.3f, 0.3f, 0.3f);
        GL11.glRotatef(var24b, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(var24, 0.0f, 0.0f, 1.0f);
        if (entity.isHot()) {
            this.bindTexture(RenderMeteorChunk.meteorChunkHotTexture);
        }
        else {
            this.bindTexture(RenderMeteorChunk.meteorChunkTexture);
        }
        this.meteorChunkModel.renderAll();
        GL11.glPopMatrix();
    }
    
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.renderMeteorChunk((EntityMeteorChunk)par1Entity, par2, par4, par6, par8, par9);
    }
    
    static {
        meteorChunkTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/meteorChunk.png");
        meteorChunkHotTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/meteorChunkHot.png");
    }
}
