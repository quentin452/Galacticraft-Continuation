package micdoodle8.mods.galacticraft.core.client.render.tile;

import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.client.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import org.lwjgl.opengl.*;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraftforge.client.model.*;

public class TileEntityDishRenderer extends TileEntitySpecialRenderer
{
    private static final ResourceLocation textureSupport;
    private static final ResourceLocation textureFork;
    private static final ResourceLocation textureDish;
    private static final IModelCustom modelSupport;
    private static final IModelCustom modelFork;
    private static final IModelCustom modelDish;
    private TextureManager renderEngine;
    
    public TileEntityDishRenderer() {
        this.renderEngine = FMLClientHandler.instance().getClient().renderEngine;
    }
    
    public void renderTileEntityAt(final TileEntity var1, final double par2, final double par4, final double par6, final float partialTickTime) {
        final TileEntityDish dish = (TileEntityDish)var1;
        final float time = (dish.ticks + partialTickTime) % 1440.0f;
        final EntityPlayer player = (EntityPlayer)FMLClientHandler.instance().getClient().thePlayer;
        GL11.glPushMatrix();
        GL11.glEnable(32826);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        GL11.glTranslatef(1.0f, 1.0f, 1.0f);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
        this.renderEngine.bindTexture(TileEntityDishRenderer.textureSupport);
        TileEntityDishRenderer.modelSupport.renderAll();
        GL11.glRotatef(time / 4.0f, 0.0f, -1.0f, 0.0f);
        this.renderEngine.bindTexture(TileEntityDishRenderer.textureFork);
        TileEntityDishRenderer.modelFork.renderAll();
        GL11.glTranslatef(0.0f, 2.3f, 0.0f);
        GL11.glRotatef((MathHelper.sin(time / 144.0f) + 1.0f) * 22.5f, 1.0f, 0.0f, 0.0f);
        GL11.glTranslatef(0.0f, -2.3f, 0.0f);
        this.renderEngine.bindTexture(TileEntityDishRenderer.textureDish);
        TileEntityDishRenderer.modelDish.renderAll();
        GL11.glDisable(32826);
        GL11.glPopMatrix();
    }
    
    static {
        textureSupport = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/telesupport.png");
        textureFork = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/telefork.png");
        textureDish = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/teledish.png");
        modelSupport = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/telesupport.obj"));
        modelFork = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/telefork.obj"));
        modelDish = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/teledish.obj"));
    }
}
