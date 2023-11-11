package micdoodle8.mods.galacticraft.core.client.gui.screen;

import micdoodle8.mods.galacticraft.core.util.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.gui.*;
import org.lwjgl.util.glu.*;
import org.lwjgl.input.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.*;

public abstract class GuiStarBackground extends GuiScreen
{
    private static final ResourceLocation backgroundTexture;
    private static final ResourceLocation blackTexture;
    
    public void drawBlackBackground() {
        final ScaledResolution var5 = ClientUtil.getScaledRes(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        final int var6 = var5.getScaledWidth();
        final int var7 = var5.getScaledHeight();
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(3008);
        this.mc.getTextureManager().bindTexture(GuiStarBackground.blackTexture);
        final Tessellator var8 = Tessellator.instance;
        var8.startDrawingQuads();
        var8.addVertexWithUV(0.0, (double)var7, -90.0, 0.0, 1.0);
        var8.addVertexWithUV((double)var6, (double)var7, -90.0, 1.0, 1.0);
        var8.addVertexWithUV((double)var6, 0.0, -90.0, 1.0, 0.0);
        var8.addVertexWithUV(0.0, 0.0, -90.0, 0.0, 0.0);
        var8.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glEnable(3008);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    private void drawPanorama2(final float par1) {
        final Tessellator var4 = Tessellator.instance;
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GLU.gluPerspective(120.0f, 1.0f, 0.05f, 10.0f);
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        GL11.glEnable(3042);
        GL11.glDisable(3008);
        GL11.glDisable(2884);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(770, 1);
        final byte var5 = 1;
        for (int var6 = 0; var6 < 1; ++var6) {
            GL11.glPushMatrix();
            final float var7 = (var6 % 1 / 1.0f - 0.5f) / 128.0f;
            final float var8 = (var6 / 1 / 1.0f - 0.5f) / 128.0f;
            final float var9 = 0.0f;
            float mY;
            if (Mouse.getY() < this.height) {
                mY = (-this.height + Mouse.getY()) / 100.0f;
            }
            else {
                mY = (-this.height + Mouse.getY()) / 100.0f;
            }
            final float mX = (this.width - Mouse.getX()) / 100.0f;
            this.doCustomTranslation(0, var7, var8, 0.0f, mX, mY);
            for (int var10 = 0; var10 < 9; ++var10) {
                GL11.glPushMatrix();
                if (var10 == 1) {
                    GL11.glTranslatef(1.96f, 0.0f, 0.0f);
                }
                if (var10 == 2) {
                    GL11.glTranslatef(-1.96f, 0.0f, 0.0f);
                }
                if (var10 == 3) {
                    GL11.glTranslatef(0.0f, 1.96f, 0.0f);
                }
                if (var10 == 4) {
                    GL11.glTranslatef(0.0f, -1.96f, 0.0f);
                }
                if (var10 == 5) {
                    GL11.glTranslatef(-1.96f, -1.96f, 0.0f);
                }
                if (var10 == 6) {
                    GL11.glTranslatef(-1.96f, 1.96f, 0.0f);
                }
                if (var10 == 7) {
                    GL11.glTranslatef(1.96f, -1.96f, 0.0f);
                }
                if (var10 == 8) {
                    GL11.glTranslatef(1.96f, 1.96f, 0.0f);
                }
                this.mc.getTextureManager().bindTexture(GuiStarBackground.backgroundTexture);
                var4.startDrawingQuads();
                var4.setColorRGBA_I(16777215, 255 / (var6 + 1));
                var4.addVertexWithUV(-1.0, -1.0, 1.0, 1.0, 1.0);
                var4.addVertexWithUV(1.0, -1.0, 1.0, 0.0, 1.0);
                var4.addVertexWithUV(1.0, 1.0, 1.0, 0.0, 0.0);
                var4.addVertexWithUV(-1.0, 1.0, 1.0, 1.0, 0.0);
                var4.draw();
                GL11.glPopMatrix();
            }
            GL11.glPopMatrix();
        }
        var4.setTranslation(0.0, 0.0, 0.0);
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glEnable(2884);
        GL11.glEnable(3008);
        GL11.glEnable(2929);
    }
    
    private void drawPanorama(final float par1) {
        final Tessellator var4 = Tessellator.instance;
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GLU.gluPerspective(120.0f, 1.0f, 0.05f, 10.0f);
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        GL11.glEnable(3042);
        GL11.glDisable(3008);
        GL11.glDisable(2884);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(770, 1);
        final byte var5 = 1;
        for (int var6 = 0; var6 < 1; ++var6) {
            GL11.glPushMatrix();
            final float var7 = (var6 % 1 / 1.0f - 0.5f) / 64.0f;
            final float var8 = (var6 / 1 / 1.0f - 0.5f) / 64.0f;
            final float var9 = 0.0f;
            float mY;
            if (Mouse.getY() < this.height) {
                mY = (-this.height + Mouse.getY()) / 100.0f;
            }
            else {
                mY = (-this.height + Mouse.getY()) / 100.0f;
            }
            final float mX = (this.width - Mouse.getX()) / 100.0f;
            this.doCustomTranslation(1, var7, var8, 0.0f, mX, mY);
            GL11.glRotatef(MathHelper.sin(par1 / 1000.0f) * 25.0f + 20.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(-par1 * 0.005f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(41.0f, 0.0f, 0.0f, 1.0f);
            for (int var10 = 0; var10 < 6; ++var10) {
                GL11.glPushMatrix();
                if (var10 == 1) {
                    GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                }
                if (var10 == 2) {
                    GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                }
                if (var10 == 3) {
                    GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
                }
                if (var10 == 4) {
                    GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                }
                if (var10 == 5) {
                    GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                }
                this.mc.getTextureManager().bindTexture(GuiStarBackground.backgroundTexture);
                var4.startDrawingQuads();
                var4.setColorRGBA_I(16777215, 255 / (var6 + 1));
                var4.addVertexWithUV(-1.0, -1.0, 1.0, 1.0, 1.0);
                var4.addVertexWithUV(1.0, -1.0, 1.0, 0.0, 1.0);
                var4.addVertexWithUV(1.0, 1.0, 1.0, 0.0, 0.0);
                var4.addVertexWithUV(-1.0, 1.0, 1.0, 1.0, 0.0);
                var4.draw();
                GL11.glPopMatrix();
            }
            GL11.glPopMatrix();
        }
        var4.setTranslation(0.0, 0.0, 0.0);
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glEnable(2884);
        GL11.glEnable(3008);
        GL11.glEnable(2929);
    }
    
    private void rotateAndBlurSkybox() {
        this.mc.getTextureManager().bindTexture(GuiStarBackground.backgroundTexture);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColorMask(true, true, true, false);
        GL11.glPushMatrix();
        GL11.glPopMatrix();
        GL11.glColorMask(true, true, true, true);
    }
    
    public void renderSkybox() {
        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GL11.glPushMatrix();
        GL11.glScalef(1.0f, 0.0f, 1.0f);
        this.drawPanorama(1.0f);
        this.drawPanorama2(1.0f);
        GL11.glDisable(3553);
        GL11.glEnable(3553);
        this.rotateAndBlurSkybox();
        final Tessellator var4 = Tessellator.instance;
        var4.startDrawingQuads();
        final float var5 = (this.width > this.height) ? (120.0f / this.width) : (120.0f / this.height);
        final float var6 = this.height * var5 / 256.0f;
        final float var7 = this.width * var5 / 256.0f;
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        var4.setColorRGBA_F(1.0f, 1.0f, 1.0f, 1.0f);
        final int var8 = this.width;
        final int var9 = this.height;
        var4.addVertexWithUV(0.0, (double)var9, (double)this.zLevel, (double)(0.5f - var6), (double)(0.5f + var7));
        var4.addVertexWithUV((double)var8, (double)var9, (double)this.zLevel, (double)(0.5f - var6), (double)(0.5f - var7));
        var4.addVertexWithUV((double)var8, 0.0, (double)this.zLevel, (double)(0.5f + var6), (double)(0.5f - var7));
        var4.addVertexWithUV(0.0, 0.0, (double)this.zLevel, (double)(0.5f + var6), (double)(0.5f + var7));
        var4.draw();
        GL11.glPopMatrix();
    }
    
    public abstract void doCustomTranslation(final int p0, final float p1, final float p2, final float p3, final float p4, final float p5);
    
    static {
        backgroundTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/stars.png");
        blackTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/black.png");
    }
}
