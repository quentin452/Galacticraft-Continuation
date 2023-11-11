package micdoodle8.mods.galacticraft.planets.mars.client;

import net.minecraftforge.client.*;
import micdoodle8.mods.galacticraft.api.world.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.*;
import net.minecraft.entity.*;
import net.minecraft.client.renderer.*;
import cpw.mods.fml.client.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.*;

public class SkyProviderMars extends IRenderHandler
{
    private static final ResourceLocation overworldTexture;
    private static final ResourceLocation sunTexture;
    public int starList;
    public int glSkyList;
    public int glSkyList2;
    private float sunSize;
    
    public SkyProviderMars(final IGalacticraftWorldProvider marsProvider) {
        this.sunSize = 17.5f * marsProvider.getSolarSize();
        final int displayLists = GLAllocation.generateDisplayLists(3);
        this.starList = displayLists;
        this.glSkyList = displayLists + 1;
        this.glSkyList2 = displayLists + 2;
        GL11.glPushMatrix();
        GL11.glNewList(this.starList, 4864);
        this.renderStars();
        GL11.glEndList();
        GL11.glPopMatrix();
        final Tessellator tessellator = Tessellator.instance;
        GL11.glNewList(this.glSkyList, 4864);
        final byte byte2 = 64;
        final int i = 6;
        float f = 16.0f;
        for (int j = -384; j <= 384; j += 64) {
            for (int l = -384; l <= 384; l += 64) {
                tessellator.startDrawingQuads();
                tessellator.addVertex((double)(j + 0), (double)f, (double)(l + 0));
                tessellator.addVertex((double)(j + 64), (double)f, (double)(l + 0));
                tessellator.addVertex((double)(j + 64), (double)f, (double)(l + 64));
                tessellator.addVertex((double)(j + 0), (double)f, (double)(l + 64));
                tessellator.draw();
            }
        }
        GL11.glEndList();
        GL11.glNewList(this.glSkyList2, 4864);
        f = -16.0f;
        tessellator.startDrawingQuads();
        for (int k = -384; k <= 384; k += 64) {
            for (int i2 = -384; i2 <= 384; i2 += 64) {
                tessellator.addVertex((double)(k + 64), (double)f, (double)(i2 + 0));
                tessellator.addVertex((double)(k + 0), (double)f, (double)(i2 + 0));
                tessellator.addVertex((double)(k + 0), (double)f, (double)(i2 + 64));
                tessellator.addVertex((double)(k + 64), (double)f, (double)(i2 + 64));
            }
        }
        tessellator.draw();
        GL11.glEndList();
    }
    
    public void render(final float partialTicks, final WorldClient world, final Minecraft mc) {
        GL11.glDisable(3553);
        GL11.glDisable(32826);
        final Vec3 vec3 = world.getSkyColor((Entity)mc.renderViewEntity, partialTicks);
        float f1 = (float)vec3.xCoord;
        float f2 = (float)vec3.yCoord;
        float f3 = (float)vec3.zCoord;
        if (mc.gameSettings.anaglyph) {
            final float f4 = (f1 * 30.0f + f2 * 59.0f + f3 * 11.0f) / 100.0f;
            final float f5 = (f1 * 30.0f + f2 * 70.0f) / 100.0f;
            final float f6 = (f1 * 30.0f + f3 * 70.0f) / 100.0f;
            f1 = f4;
            f2 = f5;
            f3 = f6;
        }
        GL11.glColor3f(f1, f2, f3);
        final Tessellator tessellator1 = Tessellator.instance;
        GL11.glDepthMask(false);
        GL11.glEnable(2912);
        GL11.glColor3f(f1, f2, f3);
        GL11.glCallList(this.glSkyList);
        GL11.glDisable(2912);
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        RenderHelper.disableStandardItemLighting();
        float f7 = world.getStarBrightness(partialTicks);
        if (f7 > 0.0f) {
            GL11.glColor4f(f7, f7, f7, f7);
            GL11.glCallList(this.starList);
        }
        final float[] afloat = new float[4];
        GL11.glDisable(3553);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0f, 1.0f, 0.0f, 0.0f);
        afloat[0] = 1.0f;
        afloat[1] = 0.7607843f;
        afloat[2] = 0.7058824f;
        afloat[3] = 0.3f;
        float f6 = afloat[0];
        float f8 = afloat[1];
        float f9 = afloat[2];
        if (mc.gameSettings.anaglyph) {
            final float f10 = (f6 * 30.0f + f8 * 59.0f + f9 * 11.0f) / 100.0f;
            final float f11 = (f6 * 30.0f + f8 * 70.0f) / 100.0f;
            final float f12 = (f6 * 30.0f + f9 * 70.0f) / 100.0f;
            f6 = f10;
            f8 = f11;
            f9 = f12;
        }
        f7 = 1.0f - f7;
        tessellator1.startDrawing(6);
        tessellator1.setColorRGBA_F(f6 * f7, f8 * f7, f9 * f7, afloat[3] * 2.0f / f7);
        tessellator1.addVertex(0.0, 100.0, 0.0);
        final byte b0 = 16;
        tessellator1.setColorRGBA_F(afloat[0] * f7, afloat[1] * f7, afloat[2] * f7, 0.0f);
        float f11 = 20.0f;
        tessellator1.addVertex((double)(-f11), 100.0, (double)(-f11));
        tessellator1.addVertex(0.0, 100.0, -f11 * 1.5);
        tessellator1.addVertex((double)f11, 100.0, (double)(-f11));
        tessellator1.addVertex(f11 * 1.5, 100.0, 0.0);
        tessellator1.addVertex((double)f11, 100.0, (double)f11);
        tessellator1.addVertex(0.0, 100.0, f11 * 1.5);
        tessellator1.addVertex((double)(-f11), 100.0, (double)f11);
        tessellator1.addVertex(-f11 * 1.5, 100.0, 0.0);
        tessellator1.addVertex((double)(-f11), 100.0, (double)(-f11));
        tessellator1.draw();
        tessellator1.startDrawing(6);
        tessellator1.setColorRGBA_F(f6 * f7, f8 * f7, f9 * f7, afloat[3] * f7);
        tessellator1.addVertex(0.0, 100.0, 0.0);
        tessellator1.setColorRGBA_F(afloat[0] * f7, afloat[1] * f7, afloat[2] * f7, 0.0f);
        f11 = 40.0f;
        tessellator1.addVertex((double)(-f11), 100.0, (double)(-f11));
        tessellator1.addVertex(0.0, 100.0, -f11 * 1.5);
        tessellator1.addVertex((double)f11, 100.0, (double)(-f11));
        tessellator1.addVertex(f11 * 1.5, 100.0, 0.0);
        tessellator1.addVertex((double)f11, 100.0, (double)f11);
        tessellator1.addVertex(0.0, 100.0, f11 * 1.5);
        tessellator1.addVertex((double)(-f11), 100.0, (double)f11);
        tessellator1.addVertex(-f11 * 1.5, 100.0, 0.0);
        tessellator1.addVertex((double)(-f11), 100.0, (double)(-f11));
        tessellator1.draw();
        GL11.glPopMatrix();
        GL11.glShadeModel(7424);
        GL11.glEnable(3553);
        OpenGlHelper.glBlendFunc(770, 1, 1, 0);
        GL11.glPushMatrix();
        f8 = 0.0f;
        f9 = 0.0f;
        float f10 = 0.0f;
        GL11.glTranslatef(f8, f9, f10);
        GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0f, 1.0f, 0.0f, 0.0f);
        GL11.glDisable(3553);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        f11 = this.sunSize / 3.5f;
        tessellator1.startDrawingQuads();
        tessellator1.addVertex((double)(-f11), 99.9, (double)(-f11));
        tessellator1.addVertex((double)f11, 99.9, (double)(-f11));
        tessellator1.addVertex((double)f11, 99.9, (double)f11);
        tessellator1.addVertex((double)(-f11), 99.9, (double)f11);
        tessellator1.draw();
        GL11.glEnable(3553);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        f11 = this.sunSize;
        mc.renderEngine.bindTexture(SkyProviderMars.sunTexture);
        tessellator1.startDrawingQuads();
        tessellator1.addVertexWithUV((double)(-f11), 100.0, (double)(-f11), 0.0, 0.0);
        tessellator1.addVertexWithUV((double)f11, 100.0, (double)(-f11), 1.0, 0.0);
        tessellator1.addVertexWithUV((double)f11, 100.0, (double)f11, 1.0, 1.0);
        tessellator1.addVertexWithUV((double)(-f11), 100.0, (double)f11, 0.0, 1.0);
        tessellator1.draw();
        f11 = 0.5f;
        GL11.glScalef(0.6f, 0.6f, 0.6f);
        GL11.glRotatef(40.0f, 0.0f, 0.0f, 1.0f);
        GL11.glRotatef(200.0f, 1.0f, 0.0f, 0.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(SkyProviderMars.overworldTexture);
        tessellator1.startDrawingQuads();
        tessellator1.addVertexWithUV((double)(-f11), -100.0, (double)f11, 0.0, 1.0);
        tessellator1.addVertexWithUV((double)f11, -100.0, (double)f11, 1.0, 1.0);
        tessellator1.addVertexWithUV((double)f11, -100.0, (double)(-f11), 1.0, 0.0);
        tessellator1.addVertexWithUV((double)(-f11), -100.0, (double)(-f11), 0.0, 0.0);
        tessellator1.draw();
        GL11.glDisable(3553);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glEnable(2912);
        GL11.glPopMatrix();
        GL11.glDisable(3553);
        GL11.glColor3f(0.0f, 0.0f, 0.0f);
        final double d0 = mc.thePlayer.getPosition(partialTicks).yCoord - world.getHorizon();
        if (d0 < 0.0) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0f, 12.0f, 0.0f);
            GL11.glCallList(this.glSkyList2);
            GL11.glPopMatrix();
            f9 = 1.0f;
            f10 = -(float)(d0 + 65.0);
            f11 = -f9;
            tessellator1.startDrawingQuads();
            tessellator1.setColorRGBA_I(0, 255);
            tessellator1.addVertex((double)(-f9), (double)f10, (double)f9);
            tessellator1.addVertex((double)f9, (double)f10, (double)f9);
            tessellator1.addVertex((double)f9, (double)f11, (double)f9);
            tessellator1.addVertex((double)(-f9), (double)f11, (double)f9);
            tessellator1.addVertex((double)(-f9), (double)f11, (double)(-f9));
            tessellator1.addVertex((double)f9, (double)f11, (double)(-f9));
            tessellator1.addVertex((double)f9, (double)f10, (double)(-f9));
            tessellator1.addVertex((double)(-f9), (double)f10, (double)(-f9));
            tessellator1.addVertex((double)f9, (double)f11, (double)(-f9));
            tessellator1.addVertex((double)f9, (double)f11, (double)f9);
            tessellator1.addVertex((double)f9, (double)f10, (double)f9);
            tessellator1.addVertex((double)f9, (double)f10, (double)(-f9));
            tessellator1.addVertex((double)(-f9), (double)f10, (double)(-f9));
            tessellator1.addVertex((double)(-f9), (double)f10, (double)f9);
            tessellator1.addVertex((double)(-f9), (double)f11, (double)f9);
            tessellator1.addVertex((double)(-f9), (double)f11, (double)(-f9));
            tessellator1.addVertex((double)(-f9), (double)f11, (double)(-f9));
            tessellator1.addVertex((double)(-f9), (double)f11, (double)f9);
            tessellator1.addVertex((double)f9, (double)f11, (double)f9);
            tessellator1.addVertex((double)f9, (double)f11, (double)(-f9));
            tessellator1.draw();
        }
        if (world.provider.isSkyColored()) {
            GL11.glColor3f(f1 * 0.2f + 0.04f, f2 * 0.2f + 0.04f, f3 * 0.6f + 0.1f);
        }
        else {
            GL11.glColor3f(f1, f2, f3);
        }
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, -(float)(d0 - 16.0), 0.0f);
        GL11.glCallList(this.glSkyList2);
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glEnable(32826);
        GL11.glEnable(2903);
        GL11.glDisable(2912);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDepthMask(true);
    }
    
    private void renderStars() {
        final Random rand = new Random(10842L);
        final Tessellator var2 = Tessellator.instance;
        var2.startDrawingQuads();
        for (int starIndex = 0; starIndex < (ConfigManagerCore.moreStars ? 35000 : 6000); ++starIndex) {
            double var3 = rand.nextFloat() * 2.0f - 1.0f;
            double var4 = rand.nextFloat() * 2.0f - 1.0f;
            double var5 = rand.nextFloat() * 2.0f - 1.0f;
            final double var6 = 0.15f + rand.nextFloat() * 0.1f;
            double var7 = var3 * var3 + var4 * var4 + var5 * var5;
            if (var7 < 1.0 && var7 > 0.01) {
                var7 = 1.0 / Math.sqrt(var7);
                var3 *= var7;
                var4 *= var7;
                var5 *= var7;
                final double var8 = var3 * (ConfigManagerCore.moreStars ? (rand.nextDouble() * 150.0 + 130.0) : 100.0);
                final double var9 = var4 * (ConfigManagerCore.moreStars ? (rand.nextDouble() * 150.0 + 130.0) : 100.0);
                final double var10 = var5 * (ConfigManagerCore.moreStars ? (rand.nextDouble() * 150.0 + 130.0) : 100.0);
                final double var11 = Math.atan2(var3, var5);
                final double var12 = Math.sin(var11);
                final double var13 = Math.cos(var11);
                final double var14 = Math.atan2(Math.sqrt(var3 * var3 + var5 * var5), var4);
                final double var15 = Math.sin(var14);
                final double var16 = Math.cos(var14);
                final double var17 = rand.nextDouble() * 3.141592653589793 * 2.0;
                final double var18 = Math.sin(var17);
                final double var19 = Math.cos(var17);
                for (int var20 = 0; var20 < 4; ++var20) {
                    final double var21 = 0.0;
                    final double var22 = ((var20 & 0x2) - 1) * var6;
                    final double var23 = ((var20 + 1 & 0x2) - 1) * var6;
                    final double var24 = var22 * var19 - var23 * var18;
                    final double var25 = var23 * var19 + var22 * var18;
                    final double var26 = var24 * var15 + 0.0 * var16;
                    final double var27 = 0.0 * var15 - var24 * var16;
                    final double var28 = var27 * var12 - var25 * var13;
                    final double var29 = var25 * var12 + var27 * var13;
                    var2.addVertex(var8 + var28, var9 + var26, var10 + var29);
                }
            }
        }
        var2.draw();
    }
    
    private Vec3 getCustomSkyColor() {
        return Vec3.createVectorHelper(0.26796875, 0.1796875, 0.0);
    }
    
    public float getSkyBrightness(final float par1) {
        final float var2 = FMLClientHandler.instance().getClient().theWorld.getCelestialAngle(par1);
        float var3 = 1.0f - (MathHelper.sin(var2 * 3.1415927f * 2.0f) * 2.0f + 0.25f);
        if (var3 < 0.0f) {
            var3 = 0.0f;
        }
        if (var3 > 1.0f) {
            var3 = 1.0f;
        }
        return var3 * var3 * 1.0f;
    }
    
    static {
        overworldTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/earth.png");
        sunTexture = new ResourceLocation("textures/environment/sun.png");
    }
}
