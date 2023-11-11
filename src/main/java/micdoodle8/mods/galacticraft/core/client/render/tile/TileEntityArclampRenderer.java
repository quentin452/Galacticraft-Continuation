package micdoodle8.mods.galacticraft.core.client.render.tile;

import net.minecraft.client.renderer.tileentity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import net.minecraftforge.client.model.obj.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraftforge.client.model.*;

@SideOnly(Side.CLIENT)
public class TileEntityArclampRenderer extends TileEntitySpecialRenderer
{
    public static final ResourceLocation lampTexture;
    public static final ResourceLocation lightTexture;
    public static final IModelCustom lampMetal;
    public static final IModelCustom lampLight;
    public static final IModelCustom lampBase;
    private TextureManager renderEngine;
    
    public TileEntityArclampRenderer() {
        this.renderEngine = FMLClientHandler.instance().getClient().renderEngine;
    }
    
    public void renderModelAt(final TileEntityArclamp tileEntity, final double d, final double d1, final double d2, final float f) {
        final int side = tileEntity.getBlockMetadata();
        int metaFacing = tileEntity.facing;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d + 0.5f, (float)d1 + 0.5f, (float)d2 + 0.5f);
        switch (side) {
            case 1: {
                GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
                if (metaFacing < 2) {
                    metaFacing ^= 0x1;
                    break;
                }
                break;
            }
            case 2: {
                GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                metaFacing ^= 0x1;
                break;
            }
            case 3: {
                GL11.glRotatef(90.0f, -1.0f, 0.0f, 0.0f);
                break;
            }
            case 4: {
                GL11.glRotatef(90.0f, 0.0f, 0.0f, -1.0f);
                metaFacing -= 2;
                if (metaFacing < 0) {
                    metaFacing = 1 - metaFacing;
                    break;
                }
                break;
            }
            case 5: {
                GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                metaFacing += 2;
                if (metaFacing > 3) {
                    metaFacing = 5 - metaFacing;
                    break;
                }
                break;
            }
        }
        GL11.glTranslatef(0.0f, -0.175f, 0.0f);
        switch (metaFacing) {
            case 1: {
                GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
            case 2: {
                GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
            case 3: {
                GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
        }
        this.renderEngine.bindTexture(TileEntityArclampRenderer.lampTexture);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        TileEntityArclampRenderer.lampBase.renderAll();
        GL11.glRotatef(45.0f, -1.0f, 0.0f, 0.0f);
        GL11.glScalef(0.048f, 0.048f, 0.048f);
        TileEntityArclampRenderer.lampMetal.renderAll();
        final int whiteLevel = tileEntity.getEnabled() ? 255 : 26;
        final float lightMapSaveX = OpenGlHelper.lastBrightnessX;
        final float lightMapSaveY = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
        GL11.glDisable(2896);
        this.renderEngine.bindTexture(TileEntityArclampRenderer.lightTexture);
        final Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(7);
        tessellator.setColorRGBA(whiteLevel, whiteLevel, whiteLevel, 255);
        ((WavefrontObject)TileEntityArclampRenderer.lampLight).tessellateAll(tessellator);
        tessellator.draw();
        GL11.glEnable(2896);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightMapSaveX, lightMapSaveY);
        GL11.glPopMatrix();
    }
    
    public void renderTileEntityAt(final TileEntity tileEntity, final double var2, final double var4, final double var6, final float var8) {
        this.renderModelAt((TileEntityArclamp)tileEntity, var2, var4, var6, var8);
    }
    
    static {
        lampTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/misc/underoil.png");
        lightTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/misc/light.png");
        lampMetal = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/arclampMetal.obj"));
        lampLight = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/arclampLight.obj"));
        lampBase = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/arclampBase.obj"));
    }
}
