package micdoodle8.mods.galacticraft.core.client;

import net.minecraftforge.client.*;
import net.minecraft.client.*;
import cpw.mods.fml.client.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.entity.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.*;

public class SkyProviderOrbit extends IRenderHandler
{
    private static final ResourceLocation moonTexture;
    private static final ResourceLocation sunTexture;
    public int starGLCallList;
    public int glSkyList;
    public int glSkyList2;
    private final ResourceLocation planetToRender;
    private final boolean renderMoon;
    private final boolean renderSun;
    private float spinAngle;
    public float spinDeltaPerTick;
    private float prevPartialTicks;
    private long prevTick;
    private final Minecraft minecraft;
    
    public SkyProviderOrbit(final ResourceLocation planet, final boolean renderMoon, final boolean renderSun) {
        this.starGLCallList = GLAllocation.generateDisplayLists(3);
        this.spinAngle = 0.0f;
        this.spinDeltaPerTick = 0.0f;
        this.prevPartialTicks = 0.0f;
        this.minecraft = FMLClientHandler.instance().getClient();
        this.planetToRender = planet;
        this.renderMoon = renderMoon;
        this.renderSun = renderSun;
        GL11.glPushMatrix();
        GL11.glNewList(this.starGLCallList, 4864);
        this.renderStars();
        GL11.glEndList();
        GL11.glPopMatrix();
        final Tessellator tessellator = Tessellator.instance;
        GL11.glNewList(this.glSkyList = this.starGLCallList + 1, 4864);
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
        GL11.glNewList(this.glSkyList2 = this.starGLCallList + 2, 4864);
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
        final float var20 = 400.0f + (float)this.minecraft.thePlayer.posY / 2.0f;
        GL11.glDisable(3553);
        GL11.glDisable(32826);
        final Vec3 var21 = this.minecraft.theWorld.getSkyColor((Entity)this.minecraft.renderViewEntity, partialTicks);
        float var22 = (float)var21.xCoord;
        float var23 = (float)var21.yCoord;
        float var24 = (float)var21.zCoord;
        if (this.minecraft.gameSettings.anaglyph) {
            final float var25 = (var22 * 30.0f + var23 * 59.0f + var24 * 11.0f) / 100.0f;
            final float var26 = (var22 * 30.0f + var23 * 70.0f) / 100.0f;
            final float var27 = (var22 * 30.0f + var24 * 70.0f) / 100.0f;
            var22 = var25;
            var23 = var26;
            var24 = var27;
        }
        GL11.glColor3f(var22, var23, var24);
        final Tessellator var28 = Tessellator.instance;
        GL11.glDepthMask(false);
        GL11.glEnable(2912);
        GL11.glColor3f(var22, var23, var24);
        GL11.glCallList(this.glSkyList);
        GL11.glDisable(2912);
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        RenderHelper.disableStandardItemLighting();
        final float[] var29 = this.minecraft.theWorld.provider.calcSunriseSunsetColors(this.minecraft.theWorld.getCelestialAngle(partialTicks), partialTicks);
        if (var29 != null) {
            GL11.glDisable(3553);
            GL11.glShadeModel(7425);
            GL11.glPushMatrix();
            GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef((MathHelper.sin(this.minecraft.theWorld.getCelestialAngleRadians(partialTicks)) < 0.0f) ? 180.0f : 0.0f, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
            float var27 = var29[0];
            float var30 = var29[1];
            float var31 = var29[2];
            if (this.minecraft.gameSettings.anaglyph) {
                final float var32 = (var27 * 30.0f + var30 * 59.0f + var31 * 11.0f) / 100.0f;
                final float var33 = (var27 * 30.0f + var30 * 70.0f) / 100.0f;
                final float var34 = (var27 * 30.0f + var31 * 70.0f) / 100.0f;
                var27 = var32;
                var30 = var33;
                var31 = var34;
            }
            var28.startDrawing(6);
            var28.setColorRGBA_F(var27, var30, var31, var29[3]);
            var28.addVertex(0.0, 100.0, 0.0);
            final byte var35 = 16;
            var28.setColorRGBA_F(var29[0], var29[1], var29[2], 0.0f);
            for (int var36 = 0; var36 <= 16; ++var36) {
                final float var34 = var36 * 3.1415927f * 2.0f / 16.0f;
                final float var37 = MathHelper.sin(var34);
                final float var38 = MathHelper.cos(var34);
                var28.addVertex((double)(var37 * 120.0f), (double)(var38 * 120.0f), (double)(-var38 * 40.0f * var29[3]));
            }
            var28.draw();
            GL11.glPopMatrix();
            GL11.glShadeModel(7424);
        }
        GL11.glBlendFunc(770, 1);
        GL11.glPushMatrix();
        float var27 = 1.0f - this.minecraft.theWorld.getRainStrength(partialTicks);
        float var30 = 0.0f;
        float var31 = 0.0f;
        final float var32 = 0.0f;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, var27);
        GL11.glTranslatef(var30, var31, var32);
        GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        float deltaTick = partialTicks - this.prevPartialTicks;
        this.prevPartialTicks = partialTicks;
        final long curTick = this.minecraft.theWorld.getTotalWorldTime();
        final int tickDiff = (int)(curTick - this.prevTick);
        this.prevTick = curTick;
        if (tickDiff > 0 && tickDiff < 20) {
            deltaTick += tickDiff;
        }
        this.spinAngle -= this.spinDeltaPerTick * deltaTick;
        while (this.spinAngle < -180.0f) {
            this.spinAngle += 360.0f;
        }
        GL11.glRotatef(this.spinAngle, 0.0f, 1.0f, 0.0f);
        GL11.glColor4f(0.8f, 0.8f, 0.8f, 0.8f);
        GL11.glCallList(this.starGLCallList);
        GL11.glEnable(3553);
        GL11.glPushMatrix();
        final float celestialAngle = this.minecraft.theWorld.getCelestialAngle(partialTicks);
        GL11.glRotatef(celestialAngle * 360.0f, 1.0f, 0.0f, 0.0f);
        if (this.renderSun) {
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3553);
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
            float var33 = 8.0f;
            var28.startDrawingQuads();
            var28.addVertex((double)(-var33), 99.9, (double)(-var33));
            var28.addVertex((double)var33, 99.9, (double)(-var33));
            var28.addVertex((double)var33, 99.9, (double)var33);
            var28.addVertex((double)(-var33), 99.9, (double)var33);
            var28.draw();
            GL11.glEnable(3553);
            GL11.glBlendFunc(770, 1);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            var33 = 28.0f;
            this.minecraft.renderEngine.bindTexture(SkyProviderOrbit.sunTexture);
            var28.startDrawingQuads();
            var28.addVertexWithUV((double)(-var33), 100.0, (double)(-var33), 0.0, 0.0);
            var28.addVertexWithUV((double)var33, 100.0, (double)(-var33), 1.0, 0.0);
            var28.addVertexWithUV((double)var33, 100.0, (double)var33, 1.0, 1.0);
            var28.addVertexWithUV((double)(-var33), 100.0, (double)var33, 0.0, 1.0);
            var28.draw();
        }
        if (this.renderMoon) {
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3553);
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
            float var33 = 11.3f;
            var28.startDrawingQuads();
            var28.addVertex((double)(-var33), -99.9, (double)var33);
            var28.addVertex((double)var33, -99.9, (double)var33);
            var28.addVertex((double)var33, -99.9, (double)(-var33));
            var28.addVertex((double)(-var33), -99.9, (double)(-var33));
            var28.draw();
            GL11.glEnable(3553);
            GL11.glBlendFunc(770, 1);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            var33 = 40.0f;
            this.minecraft.renderEngine.bindTexture(SkyProviderOrbit.moonTexture);
            final float var39 = (float)this.minecraft.theWorld.getMoonPhase();
            final int var40 = (int)(var39 % 4.0f);
            final int var41 = (int)(var39 / 4.0f % 2.0f);
            final float var42 = (var40 + 0) / 4.0f;
            final float var43 = (var41 + 0) / 2.0f;
            final float var44 = (var40 + 1) / 4.0f;
            final float var45 = (var41 + 1) / 2.0f;
            var28.startDrawingQuads();
            var28.addVertexWithUV((double)(-var33), -100.0, (double)var33, (double)var44, (double)var45);
            var28.addVertexWithUV((double)var33, -100.0, (double)var33, (double)var42, (double)var45);
            var28.addVertexWithUV((double)var33, -100.0, (double)(-var33), (double)var42, (double)var43);
            var28.addVertexWithUV((double)(-var33), -100.0, (double)(-var33), (double)var44, (double)var43);
            var28.draw();
        }
        GL11.glPopMatrix();
        GL11.glDisable(3042);
        if (this.planetToRender != null) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0f, -var20 / 10.0f, 0.0f);
            float scale = 100.0f * (0.3f - var20 / 10000.0f);
            scale = Math.max(scale, 0.2f);
            GL11.glScalef(scale, 0.0f, scale);
            GL11.glTranslatef(0.0f, -var20, 0.0f);
            GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
            this.minecraft.renderEngine.bindTexture(this.planetToRender);
            var31 = 1.0f;
            final float alpha = 0.5f;
            GL11.glColor4f(Math.min(0.5f, 1.0f), Math.min(0.5f, 1.0f), Math.min(0.5f, 1.0f), Math.min(0.5f, 1.0f));
            var28.startDrawingQuads();
            var28.addVertexWithUV((double)(-var31), 0.0, (double)var31, 0.0, 1.0);
            var28.addVertexWithUV((double)var31, 0.0, (double)var31, 1.0, 1.0);
            var28.addVertexWithUV((double)var31, 0.0, (double)(-var31), 1.0, 0.0);
            var28.addVertexWithUV((double)(-var31), 0.0, (double)(-var31), 0.0, 0.0);
            var28.draw();
            GL11.glPopMatrix();
        }
        GL11.glDisable(3553);
        GL11.glPopMatrix();
        GL11.glEnable(3008);
        GL11.glColor3f(0.0f, 0.0f, 0.0f);
        GL11.glEnable(3553);
        GL11.glEnable(2903);
        GL11.glDepthMask(true);
    }
    
    private void renderStars() {
        final Random var1 = new Random(10842L);
        final Tessellator var2 = Tessellator.instance;
        var2.startDrawingQuads();
        for (int var3 = 0; var3 < (ConfigManagerCore.moreStars ? 20000 : 6000); ++var3) {
            double var4 = var1.nextFloat() * 2.0f - 1.0f;
            double var5 = var1.nextFloat() * 2.0f - 1.0f;
            double var6 = var1.nextFloat() * 2.0f - 1.0f;
            final double var7 = 0.07f + var1.nextFloat() * 0.06f;
            double var8 = var4 * var4 + var5 * var5 + var6 * var6;
            if (var8 < 1.0 && var8 > 0.01) {
                var8 = 1.0 / Math.sqrt(var8);
                var4 *= var8;
                var5 *= var8;
                var6 *= var8;
                final double var9 = var4 * (ConfigManagerCore.moreStars ? (var1.nextDouble() * 50.0 + 75.0) : 50.0);
                final double var10 = var5 * (ConfigManagerCore.moreStars ? (var1.nextDouble() * 50.0 + 75.0) : 50.0);
                final double var11 = var6 * (ConfigManagerCore.moreStars ? (var1.nextDouble() * 50.0 + 75.0) : 50.0);
                final double var12 = Math.atan2(var4, var6);
                final double var13 = Math.sin(var12);
                final double var14 = Math.cos(var12);
                final double var15 = Math.atan2(Math.sqrt(var4 * var4 + var6 * var6), var5);
                final double var16 = Math.sin(var15);
                final double var17 = Math.cos(var15);
                final double var18 = var1.nextDouble() * 3.141592653589793 * 2.0;
                final double var19 = Math.sin(var18);
                final double var20 = Math.cos(var18);
                for (int var21 = 0; var21 < 4; ++var21) {
                    final double var22 = 0.0;
                    final double var23 = ((var21 & 0x2) - 1) * var7;
                    final double var24 = ((var21 + 1 & 0x2) - 1) * var7;
                    final double var25 = var23 * var20 - var24 * var19;
                    final double var26 = var24 * var20 + var23 * var19;
                    final double var27 = var25 * var16 + 0.0 * var17;
                    final double var28 = 0.0 * var16 - var25 * var17;
                    final double var29 = var28 * var13 - var26 * var14;
                    final double var30 = var26 * var13 + var28 * var14;
                    var2.addVertex(var9 + var29, var10 + var27, var11 + var30);
                }
            }
        }
        var2.draw();
    }
    
    static {
        moonTexture = new ResourceLocation("textures/environment/moon_phases.png");
        sunTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/planets/orbitalsun.png");
    }
}
