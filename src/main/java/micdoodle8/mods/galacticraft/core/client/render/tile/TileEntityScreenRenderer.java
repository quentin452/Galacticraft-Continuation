package micdoodle8.mods.galacticraft.core.client.render.tile;

import net.minecraft.client.renderer.tileentity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.texture.*;
import java.nio.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import org.lwjgl.opengl.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraftforge.client.model.*;
import net.minecraft.client.renderer.*;

@SideOnly(Side.CLIENT)
public class TileEntityScreenRenderer extends TileEntitySpecialRenderer
{
    public static final ResourceLocation blockTexture;
    public static final IModelCustom screenModel0;
    public static final IModelCustom screenModel1;
    public static final IModelCustom screenModel2;
    public static final IModelCustom screenModel3;
    public static final IModelCustom screenModel4;
    private TextureManager renderEngine;
    private static FloatBuffer colorBuffer;
    private float yPlane;
    float frame;
    
    public TileEntityScreenRenderer() {
        this.renderEngine = FMLClientHandler.instance().getClient().renderEngine;
        this.yPlane = 0.91f;
        this.frame = 0.098f;
    }
    
    public void renderModelAt(final TileEntityScreen tileEntity, final double d, final double d1, final double d2, final float f) {
        GL11.glPushMatrix();
        this.renderEngine.bindTexture(TileEntityScreenRenderer.blockTexture);
        GL11.glTranslatef((float)d, (float)d1, (float)d2);
        int meta = tileEntity.getBlockMetadata();
        final boolean screenData = meta >= 8;
        meta &= 0x7;
        switch (meta) {
            case 0: {
                GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
                GL11.glTranslatef(0.0f, -1.0f, -1.0f);
            }
            case 2: {
                GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GL11.glTranslatef(0.0f, 0.0f, -1.0f);
                break;
            }
            case 3: {
                GL11.glRotatef(90.0f, -1.0f, 0.0f, 0.0f);
                GL11.glTranslatef(1.0f, -1.0f, 1.0f);
                GL11.glRotatef(180.0f, 0.0f, -1.0f, 0.0f);
                break;
            }
            case 4: {
                GL11.glRotatef(90.0f, 0.0f, 0.0f, -1.0f);
                GL11.glTranslatef(-1.0f, 0.0f, 1.0f);
                GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
            case 5: {
                GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                GL11.glTranslatef(1.0f, -1.0f, 0.0f);
                GL11.glRotatef(90.0f, 0.0f, -1.0f, 0.0f);
                break;
            }
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPushMatrix();
        int count = 0;
        if (tileEntity.connectedDown) {
            ++count;
        }
        if (tileEntity.connectedUp) {
            ++count;
        }
        if (tileEntity.connectedLeft) {
            ++count;
        }
        if (tileEntity.connectedRight) {
            ++count;
        }
        switch (count) {
            case 0: {
                GL11.glTranslatef(-0.001f, -0.001f, -0.001f);
                GL11.glScalef(1.002f, 1.002f, 1.002f);
                TileEntityScreenRenderer.screenModel0.renderAll();
                break;
            }
            case 1: {
                if (tileEntity.connectedUp) {
                    GL11.glRotatef(90.0f, 0.0f, -1.0f, 0.0f);
                    GL11.glTranslatef(0.0f, 0.0f, -1.0f);
                }
                else if (tileEntity.connectedRight) {
                    GL11.glRotatef(180.0f, 0.0f, -1.0f, 0.0f);
                    GL11.glTranslatef(-1.0f, 0.0f, -1.0f);
                }
                else if (tileEntity.connectedDown) {
                    GL11.glRotatef(270.0f, 0.0f, -1.0f, 0.0f);
                    GL11.glTranslatef(-1.0f, 0.0f, 0.0f);
                }
                GL11.glTranslatef(-0.001f, -0.001f, -0.001f);
                GL11.glScalef(1.002f, 1.002f, 1.002f);
                TileEntityScreenRenderer.screenModel1.renderAll();
                break;
            }
            case 2: {
                if (!tileEntity.connectedRight && !tileEntity.connectedDown) {
                    GL11.glRotatef(90.0f, 0.0f, -1.0f, 0.0f);
                    GL11.glTranslatef(0.0f, 0.0f, -1.0f);
                }
                else if (!tileEntity.connectedDown && !tileEntity.connectedLeft) {
                    GL11.glRotatef(180.0f, 0.0f, -1.0f, 0.0f);
                    GL11.glTranslatef(-1.0f, 0.0f, -1.0f);
                }
                else if (!tileEntity.connectedUp && !tileEntity.connectedLeft) {
                    GL11.glRotatef(270.0f, 0.0f, -1.0f, 0.0f);
                    GL11.glTranslatef(-1.0f, 0.0f, 0.0f);
                }
                GL11.glTranslatef(-0.001f, -0.001f, -0.001f);
                GL11.glScalef(1.002f, 1.002f, 1.002f);
                TileEntityScreenRenderer.screenModel2.renderAll();
                break;
            }
            case 3: {
                if (!tileEntity.connectedRight) {
                    GL11.glRotatef(90.0f, 0.0f, -1.0f, 0.0f);
                    GL11.glTranslatef(0.0f, 0.0f, -1.0f);
                }
                else if (!tileEntity.connectedDown) {
                    GL11.glRotatef(180.0f, 0.0f, -1.0f, 0.0f);
                    GL11.glTranslatef(-1.0f, 0.0f, -1.0f);
                }
                else if (!tileEntity.connectedLeft) {
                    GL11.glRotatef(270.0f, 0.0f, -1.0f, 0.0f);
                    GL11.glTranslatef(-1.0f, 0.0f, 0.0f);
                }
                GL11.glTranslatef(-0.001f, -0.001f, -0.001f);
                GL11.glScalef(1.002f, 1.002f, 1.002f);
                TileEntityScreenRenderer.screenModel3.renderAll();
                break;
            }
            case 4: {
                GL11.glTranslatef(-0.001f, -0.001f, -0.001f);
                GL11.glScalef(1.002f, 1.002f, 1.002f);
                TileEntityScreenRenderer.screenModel4.renderAll();
                break;
            }
        }
        GL11.glPopMatrix();
        GL11.glTranslatef((float)(-tileEntity.screenOffsetx), this.yPlane, (float)(-tileEntity.screenOffsetz));
        GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        boolean cornerblock = false;
        if (tileEntity.connectionsLeft == 0 || tileEntity.connectionsRight == 0) {
            cornerblock = (tileEntity.connectionsUp == 0 || tileEntity.connectionsDown == 0);
        }
        final int totalLR = tileEntity.connectionsLeft + tileEntity.connectionsRight;
        final int totalUD = tileEntity.connectionsUp + tileEntity.connectionsDown;
        if (totalLR > 1 && totalUD > 1 && !cornerblock && tileEntity.connectionsLeft == tileEntity.connectionsRight - (totalLR | 0x1) && tileEntity.connectionsUp == tileEntity.connectionsDown - (totalUD | 0x1)) {
            cornerblock = true;
        }
        tileEntity.screen.drawScreen(tileEntity.imageType, f + tileEntity.getWorldObj().getWorldTime(), cornerblock);
        GL11.glPopMatrix();
    }
    
    public void renderTileEntityAt(final TileEntity tileEntity, final double var2, final double var4, final double var6, final float var8) {
        this.renderModelAt((TileEntityScreen)tileEntity, var2, var4, var6, var8);
    }
    
    static {
        blockTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/blocks/screenSide.png");
        screenModel0 = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/screenWhole.obj"));
        screenModel1 = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/screen3Quarters.obj"));
        screenModel2 = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/screen2Quarters.obj"));
        screenModel3 = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/screen1Quarters.obj"));
        screenModel4 = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/screen0Quarters.obj"));
        TileEntityScreenRenderer.colorBuffer = GLAllocation.createDirectFloatBuffer(16);
    }
}
