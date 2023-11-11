package micdoodle8.mods.galacticraft.core.client.gui.screen;

import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.api.client.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import micdoodle8.mods.galacticraft.core.client.render.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraft.client.renderer.*;

public class GameScreenBasic implements IGameScreen
{
    private TextureManager renderEngine;
    private float frameA;
    private float frameBx;
    private float frameBy;
    private float textureAx;
    private float textureAy;
    private float textureBx;
    private float textureBy;
    
    public GameScreenBasic() {
        this.textureAx = 0.0f;
        this.textureAy = 0.0f;
        this.textureBx = 1.0f;
        this.textureBy = 1.0f;
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.renderEngine = FMLClientHandler.instance().getClient().renderEngine;
        }
    }
    
    public void setFrameSize(final float frameSize) {
        this.frameA = frameSize;
    }
    
    public void render(final int type, final float ticks, final float scaleX, final float scaleY, final IScreenManager scr) {
        this.frameBx = scaleX - this.frameA;
        this.frameBy = scaleY - this.frameA;
        if (scaleX == scaleY) {
            this.textureAx = 0.0f;
            this.textureAy = 0.0f;
            this.textureBx = 1.0f;
            this.textureBy = 1.0f;
        }
        else if (scaleX < scaleY) {
            this.textureAx = (1.0f - scaleX / scaleY) / 2.0f;
            this.textureAy = 0.0f;
            this.textureBx = 1.0f - this.textureAx;
            this.textureBy = 1.0f;
        }
        else if (scaleY < scaleX) {
            this.textureAx = 0.0f;
            this.textureAy = (1.0f - scaleY / scaleX) / 2.0f;
            this.textureBx = 1.0f;
            this.textureBy = 1.0f - this.textureAy;
        }
        switch (type) {
            case 0: {
                this.drawBlackBackground(0.09f);
                break;
            }
            case 1: {
                if (scr instanceof DrawGameScreen && ((DrawGameScreen)scr).mapDone) {
                    GL11.glBindTexture(3553, DrawGameScreen.reusableMap.getGlTextureId());
                    this.draw2DTexture();
                    break;
                }
                if (ClientProxyCore.overworldTexturesValid) {
                    GL11.glPushMatrix();
                    final float centreX = scaleX / 2.0f;
                    final float centreY = scaleY / 2.0f;
                    GL11.glTranslatef(centreX, centreY, 0.0f);
                    RenderPlanet.renderPlanet(ClientProxyCore.overworldTextureWide.getGlTextureId(), Math.min(scaleX, scaleY) - 0.2f, ticks, 45.0f);
                    GL11.glPopMatrix();
                    break;
                }
                this.renderEngine.bindTexture(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/earth.png"));
                if (!ClientProxyCore.overworldTextureRequestSent) {
                    GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(PacketSimple.EnumSimplePacket.S_REQUEST_OVERWORLD_IMAGE, new Object[0]));
                    ClientProxyCore.overworldTextureRequestSent = true;
                }
                this.draw2DTexture();
                break;
            }
        }
    }
    
    private void draw2DTexture() {
        final Tessellator tess = Tessellator.instance;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        tess.setColorRGBA(255, 255, 255, 255);
        tess.startDrawingQuads();
        tess.addVertexWithUV((double)this.frameA, (double)this.frameBy, 0.0, (double)this.textureAx, (double)this.textureBy);
        tess.addVertexWithUV((double)this.frameBx, (double)this.frameBy, 0.0, (double)this.textureBx, (double)this.textureBy);
        tess.addVertexWithUV((double)this.frameBx, (double)this.frameA, 0.0, (double)this.textureBx, (double)this.textureAy);
        tess.addVertexWithUV((double)this.frameA, (double)this.frameA, 0.0, (double)this.textureAx, (double)this.textureAy);
        tess.draw();
    }
    
    private void drawBlackBackground(final float greyLevel) {
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        final Tessellator tess = Tessellator.instance;
        GL11.glColor4f(greyLevel, greyLevel, greyLevel, 1.0f);
        tess.startDrawingQuads();
        tess.addVertex((double)this.frameA, (double)this.frameBy, 0.004999999888241291);
        tess.addVertex((double)this.frameBx, (double)this.frameBy, 0.004999999888241291);
        tess.addVertex((double)this.frameBx, (double)this.frameA, 0.004999999888241291);
        tess.addVertex((double)this.frameA, (double)this.frameA, 0.004999999888241291);
        tess.draw();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(3553);
    }
}
