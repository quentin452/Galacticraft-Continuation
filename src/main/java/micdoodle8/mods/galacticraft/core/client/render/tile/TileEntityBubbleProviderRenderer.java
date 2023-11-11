package micdoodle8.mods.galacticraft.core.client.render.tile;

import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraftforge.client.model.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;

public class TileEntityBubbleProviderRenderer extends TileEntitySpecialRenderer
{
    private static final ResourceLocation oxygenBubbleTexture;
    private static IModelCustom sphere;
    private final float colorRed;
    private final float colorGreen;
    private final float colorBlue;
    
    public TileEntityBubbleProviderRenderer(final float colorRed, final float colorGreen, final float colorBlue) {
        TileEntityBubbleProviderRenderer.sphere = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/sphere.obj"));
        this.colorRed = colorRed;
        this.colorGreen = colorGreen;
        this.colorBlue = colorBlue;
    }
    
    public void renderTileEntityAt(final TileEntity tileEntity, final double x, final double y, final double z, final float var8) {
        final IBubbleProvider provider = (IBubbleProvider)tileEntity;
        if (!provider.getBubbleVisible()) {
            return;
        }
        GL11.glPushMatrix();
        GL11.glEnable(32826);
        GL11.glTranslatef((float)x + 0.5f, (float)y + 1.0f, (float)z + 0.5f);
        this.bindTexture(TileEntityBubbleProviderRenderer.oxygenBubbleTexture);
        GL11.glEnable(3042);
        GL11.glDisable(2896);
        GL11.glAlphaFunc(516, 0.1f);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(this.colorRed / 2.0f, this.colorGreen / 2.0f, this.colorBlue / 2.0f, 1.0f);
        GL11.glMatrixMode(5890);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(5888);
        GL11.glDepthMask(false);
        final float lightMapSaveX = OpenGlHelper.lastBrightnessX;
        final float lightMapSaveY = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
        GL11.glScalef(provider.getBubbleSize(), provider.getBubbleSize(), provider.getBubbleSize());
        TileEntityBubbleProviderRenderer.sphere.renderAll();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glMatrixMode(5890);
        GL11.glDepthMask(true);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(5888);
        GL11.glEnable(2896);
        GL11.glDisable(3042);
        GL11.glDepthFunc(515);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightMapSaveX, lightMapSaveY);
        GL11.glDisable(32826);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    static {
        oxygenBubbleTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/bubble.png");
    }
}
