package micdoodle8.mods.galacticraft.core.client.render;

import net.minecraft.client.renderer.texture.*;
import org.lwjgl.opengl.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.core.*;

public class RenderPlanet
{
    private static TextureManager renderEngine;
    private static ResourceLocation textureEuropa;
    private static ResourceLocation textureGanymede;
    private static ResourceLocation textureIo;
    private static ResourceLocation textureSaturn;
    private static ResourceLocation textureJupiterInner;
    private static ResourceLocation textureJupiterUpper;
    
    public static void renderPlanet(final int textureId, final float scale, float ticks, final float relSize) {
        GL11.glBindTexture(3553, textureId);
        final float size = relSize / 70.0f * scale;
        ticks = System.nanoTime() / 5.0E7f;
        drawTexturedRectUV(-size / 2.0f, -size / 2.0f, size, size, ticks);
    }
    
    public static void renderPlanet(final ResourceLocation texture, final float scale, float ticks, final float relSize) {
        RenderPlanet.renderEngine.bindTexture(texture);
        final float size = relSize / 70.0f * scale;
        ticks = System.nanoTime() / 5.0E7f;
        drawTexturedRectUV(-size / 2.0f, -size / 2.0f, size, size, ticks);
    }
    
    public static void renderID(final int id, final float scale, final float ticks) {
        ResourceLocation texture = null;
        switch (id) {
            case 0: {
                texture = RenderPlanet.textureEuropa;
                break;
            }
            case 1: {
                texture = RenderPlanet.textureGanymede;
                break;
            }
            case 2: {
                texture = RenderPlanet.textureIo;
                break;
            }
            case 3: {
                texture = RenderPlanet.textureJupiterInner;
                break;
            }
            case 4: {
                texture = RenderPlanet.textureSaturn;
                break;
            }
            default: {
                texture = RenderPlanet.textureGanymede;
                break;
            }
        }
        if (id == 3) {
            final float relSize = 48.0f;
            float size = relSize / 70.0f * scale;
            RenderPlanet.renderEngine.bindTexture(texture);
            drawTexturedRectUV(-size / 2.0f, -size / 2.0f, size, size, ticks);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glTranslatef(0.0f, 0.0f, -0.001f);
            RenderPlanet.renderEngine.bindTexture(RenderPlanet.textureJupiterUpper);
            size *= 1.001f;
            drawTexturedRectUV(-size / 2.0f, -size / 2.0f, size, size, ticks * 0.85f);
        }
        else {
            renderPlanet(texture, scale, ticks, 8.0f);
        }
    }
    
    public static void drawTexturedRectUV(final float x, final float y, final float width, final float height, final float ticks) {
        for (int ysect = 0; ysect < 6; ++ysect) {
            final float factor = 1.0f + MathHelper.cos((7.5f + 10.0f * ysect) / 62.0f);
            drawTexturedRectUVSixth(x, y, width, height, ticks / 1100.0f % 1.0f - (1.0f - factor) * 0.15f, ysect / 6.0f, 0.16f * factor);
        }
    }
    
    public static void drawTexturedRectUVSixth(final float x, final float y, final float width, final float height, float prog, float y0, final float span) {
        y0 /= 2.0f;
        if (prog < 0.0f) {
            ++prog;
        }
        prog = 1.0f - prog;
        final float y2 = y0 + 0.083333336f;
        final float y3 = 1.0f - y2;
        final float y4 = 1.0f - y0;
        final float yaa = y + height * y0;
        final float yab = y + height * y2;
        final float yba = y + height * y3;
        final float ybb = y + height * y4;
        final Tessellator tessellator = Tessellator.instance;
        if (prog <= 1.0f - span) {
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV((double)x, (double)yab, 0.0, (double)prog, (double)y2);
            tessellator.addVertexWithUV((double)(x + width), (double)yab, 0.0, (double)(prog + span), (double)y2);
            tessellator.addVertexWithUV((double)(x + width), (double)yaa, 0.0, (double)(prog + span), (double)y0);
            tessellator.addVertexWithUV((double)x, (double)yaa, 0.0, (double)prog, (double)y0);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV((double)x, (double)ybb, 0.0, (double)prog, (double)y4);
            tessellator.addVertexWithUV((double)(x + width), (double)ybb, 0.0, (double)(prog + span), (double)y4);
            tessellator.addVertexWithUV((double)(x + width), (double)yba, 0.0, (double)(prog + span), (double)y3);
            tessellator.addVertexWithUV((double)x, (double)yba, 0.0, (double)prog, (double)y3);
            tessellator.draw();
        }
        else {
            final double xp = x + width * (1.0f - prog) / span;
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV((double)x, (double)yab, 0.0, (double)prog, (double)y2);
            tessellator.addVertexWithUV(xp, (double)yab, 0.0, 1.0, (double)y2);
            tessellator.addVertexWithUV(xp, (double)yaa, 0.0, 1.0, (double)y0);
            tessellator.addVertexWithUV((double)x, (double)yaa, 0.0, (double)prog, (double)y0);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV((double)x, (double)ybb, 0.0, (double)prog, (double)y4);
            tessellator.addVertexWithUV(xp, (double)ybb, 0.0, 1.0, (double)y4);
            tessellator.addVertexWithUV(xp, (double)yba, 0.0, 1.0, (double)y3);
            tessellator.addVertexWithUV((double)x, (double)yba, 0.0, (double)prog, (double)y3);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(xp, (double)yab, 0.0, 0.0, (double)y2);
            tessellator.addVertexWithUV((double)(x + width), (double)yab, 0.0, (double)(prog - 1.0f + span), (double)y2);
            tessellator.addVertexWithUV((double)(x + width), (double)yaa, 0.0, (double)(prog - 1.0f + span), (double)y0);
            tessellator.addVertexWithUV(xp, (double)yaa, 0.0, 0.0, (double)y0);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(xp, (double)ybb, 0.0, 0.0, (double)y4);
            tessellator.addVertexWithUV((double)(x + width), (double)ybb, 0.0, (double)(prog - 1.0f + span), (double)y4);
            tessellator.addVertexWithUV((double)(x + width), (double)yba, 0.0, (double)(prog - 1.0f + span), (double)y3);
            tessellator.addVertexWithUV(xp, (double)yba, 0.0, 0.0, (double)y3);
            tessellator.draw();
        }
    }
    
    static {
        RenderPlanet.renderEngine = FMLClientHandler.instance().getClient().renderEngine;
        RenderPlanet.textureEuropa = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/misc/planets/europa.png");
        RenderPlanet.textureGanymede = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/misc/planets/ganymede.png");
        RenderPlanet.textureIo = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/misc/planets/io.png");
        RenderPlanet.textureSaturn = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/misc/planets/saturn.png");
        RenderPlanet.textureJupiterInner = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/misc/planets/jupiterInner.png");
        RenderPlanet.textureJupiterUpper = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/misc/planets/jupiterUpper.png");
    }
}
