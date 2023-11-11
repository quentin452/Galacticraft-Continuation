package micdoodle8.mods.galacticraft.core.client.gui.overlay;

import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import net.minecraft.client.*;
import org.lwjgl.opengl.*;
import cpw.mods.fml.client.*;
import net.minecraft.client.renderer.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.client.gui.*;
import micdoodle8.mods.galacticraft.core.*;

@SideOnly(Side.CLIENT)
public class OverlayOxygenTanks extends Overlay
{
    private static final ResourceLocation guiTexture;
    private static Minecraft minecraft;
    
    public static void renderOxygenTankIndicator(final int heatLevel, final int oxygenInTank1, final int oxygenInTank2, final boolean right, final boolean top, final boolean invalid) {
        final ScaledResolution scaledresolution = ClientUtil.getScaledRes(OverlayOxygenTanks.minecraft, OverlayOxygenTanks.minecraft.displayWidth, OverlayOxygenTanks.minecraft.displayHeight);
        final int i = scaledresolution.getScaledWidth();
        final int j = scaledresolution.getScaledHeight();
        OverlayOxygenTanks.minecraft.entityRenderer.setupOverlayRendering();
        GL11.glEnable(3042);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(3008);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(OverlayOxygenTanks.guiTexture);
        final Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(2929);
        GL11.glEnable(3008);
        GL11.glDisable(2896);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        int minLeftX = 0;
        int maxLeftX = 0;
        int minRightX = 0;
        int maxRightX = 0;
        double bottomY = 0.0;
        double topY = 0.0;
        final double zLevel = -190.0;
        if (right) {
            minLeftX = i - 59;
            maxLeftX = i - 40;
            minRightX = i - 39;
            maxRightX = i - 20;
        }
        else {
            minLeftX = 10;
            maxLeftX = 29;
            minRightX = 30;
            maxRightX = 49;
        }
        if (top) {
            topY = 10.5;
        }
        else {
            topY = j - 57;
        }
        bottomY = topY + 46.5;
        final float texMod = 0.00390625f;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)minLeftX, bottomY, zLevel, (double)(66.0f * texMod), (double)(47.0f * texMod));
        tessellator.addVertexWithUV((double)(minLeftX + 9), bottomY, zLevel, (double)(75.0f * texMod), (double)(47.0f * texMod));
        tessellator.addVertexWithUV((double)(minLeftX + 9), topY, zLevel, (double)(75.0f * texMod), (double)(94.0f * texMod));
        tessellator.addVertexWithUV((double)minLeftX, topY, zLevel, (double)(66.0f * texMod), (double)(94.0f * texMod));
        tessellator.draw();
        final int heatLevelScaled = Math.min(Math.max(heatLevel, 1), 45);
        final int heatLeveLScaledMax = Math.min(heatLevelScaled + 2, 45);
        final int heatLevelScaledMin = Math.max(heatLeveLScaledMax - 2, 0);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(minLeftX + 1), bottomY - heatLevelScaledMin, zLevel, (double)(76.0f * texMod), (double)((93 - heatLevelScaled) * texMod));
        tessellator.addVertexWithUV((double)(minLeftX + 8), bottomY - heatLevelScaledMin, zLevel, (double)(83.0f * texMod), (double)((93 - heatLevelScaled) * texMod));
        tessellator.addVertexWithUV((double)(minLeftX + 8), bottomY - heatLeveLScaledMax, zLevel, (double)(83.0f * texMod), (double)((93 - heatLevelScaled) * texMod));
        tessellator.addVertexWithUV((double)(minLeftX + 1), bottomY - heatLeveLScaledMax, zLevel, (double)(76.0f * texMod), (double)((93 - heatLevelScaled) * texMod));
        tessellator.draw();
        if (invalid) {
            GL11.glColor3f(1.0f, 0.0f, 0.0f);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV((double)(minLeftX - 5), bottomY - heatLevelScaledMin + 3.0, zLevel, (double)(84.0f * texMod), (double)(47.0f * texMod));
            tessellator.addVertexWithUV((double)(minLeftX - 1), bottomY - heatLevelScaledMin + 3.0, zLevel, (double)(89.0f * texMod), (double)(47.0f * texMod));
            tessellator.addVertexWithUV((double)(minLeftX - 1), bottomY - heatLeveLScaledMax - 3.0, zLevel, (double)(89.0f * texMod), (double)(56.0f * texMod));
            tessellator.addVertexWithUV((double)(minLeftX - 5), bottomY - heatLeveLScaledMax - 3.0, zLevel, (double)(84.0f * texMod), (double)(56.0f * texMod));
            tessellator.draw();
            GL11.glColor3f(1.0f, 1.0f, 1.0f);
        }
        minLeftX += 10;
        maxLeftX += 10;
        minRightX += 10;
        maxRightX += 10;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)minRightX, bottomY, zLevel, (double)(85.0f * texMod), (double)(47.0f * texMod));
        tessellator.addVertexWithUV((double)maxRightX, bottomY, zLevel, (double)(104.0f * texMod), (double)(47.0f * texMod));
        tessellator.addVertexWithUV((double)maxRightX, topY, zLevel, (double)(104.0f * texMod), (double)(0.0f * texMod));
        tessellator.addVertexWithUV((double)minRightX, topY, zLevel, (double)(85.0f * texMod), (double)(0.0f * texMod));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)minLeftX, bottomY, zLevel, (double)(85.0f * texMod), (double)(47.0f * texMod));
        tessellator.addVertexWithUV((double)maxLeftX, bottomY, zLevel, (double)(104.0f * texMod), (double)(47.0f * texMod));
        tessellator.addVertexWithUV((double)maxLeftX, topY, zLevel, (double)(104.0f * texMod), (double)(0.0f * texMod));
        tessellator.addVertexWithUV((double)minLeftX, topY, zLevel, (double)(85.0f * texMod), (double)(0.0f * texMod));
        tessellator.draw();
        GL11.glDepthMask(true);
        if (oxygenInTank1 > 0) {
            final Tessellator tessellator2 = Tessellator.instance;
            tessellator2.startDrawingQuads();
            tessellator.addVertexWithUV((double)(minLeftX + 1), topY + 1.0 + oxygenInTank1 / 2, zLevel, 0.41015625, (double)(oxygenInTank1 / 2 * 0.00390625f));
            tessellator.addVertexWithUV((double)(maxLeftX - 1), topY + 1.0 + oxygenInTank1 / 2, zLevel, 0.4765625, (double)(oxygenInTank1 / 2 * 0.00390625f));
            tessellator.addVertexWithUV((double)(maxLeftX - 1), topY + 1.0, zLevel, 0.4765625, 0.00390625);
            tessellator.addVertexWithUV((double)(minLeftX + 1), topY + 1.0, zLevel, 0.41015625, 0.00390625);
            tessellator2.draw();
            tessellator2.startDrawingQuads();
            tessellator.addVertexWithUV((double)minLeftX, topY + 1.0 + oxygenInTank1 / 2, zLevel, 0.2578125, (double)(oxygenInTank1 / 2 * 0.00390625f));
            tessellator.addVertexWithUV((double)(maxLeftX - 1), topY + 1.0 + oxygenInTank1 / 2, zLevel, 0.32421875, (double)(oxygenInTank1 / 2 * 0.00390625f));
            tessellator.addVertexWithUV((double)(maxLeftX - 1), topY + 1.0 + oxygenInTank1 / 2 - 1.0, zLevel, 0.32421875, 0.00390625);
            tessellator.addVertexWithUV((double)minLeftX, topY + 1.0 + oxygenInTank1 / 2 - 1.0, zLevel, 0.2578125, 0.00390625);
            tessellator2.draw();
        }
        if (oxygenInTank2 > 0) {
            final Tessellator tessellator2 = Tessellator.instance;
            tessellator2.startDrawingQuads();
            tessellator.addVertexWithUV((double)(minRightX + 1), topY + 1.0 + oxygenInTank2 / 2, 0.0, 0.41015625, (double)(oxygenInTank2 / 2 * 0.00390625f));
            tessellator.addVertexWithUV((double)(maxRightX - 1), topY + 1.0 + oxygenInTank2 / 2, 0.0, 0.4765625, (double)(oxygenInTank2 / 2 * 0.00390625f));
            tessellator.addVertexWithUV((double)(maxRightX - 1), topY + 1.0, 0.0, 0.4765625, 0.00390625);
            tessellator.addVertexWithUV((double)(minRightX + 1), topY + 1.0, 0.0, 0.41015625, 0.00390625);
            tessellator2.draw();
            tessellator2.startDrawingQuads();
            tessellator.addVertexWithUV((double)minRightX, topY + 1.0 + oxygenInTank2 / 2, 0.0, 0.2578125, (double)(oxygenInTank2 / 2 * 0.00390625f));
            tessellator.addVertexWithUV((double)(maxRightX - 1), topY + 1.0 + oxygenInTank2 / 2, 0.0, 0.32421875, (double)(oxygenInTank2 / 2 * 0.00390625f));
            tessellator.addVertexWithUV((double)(maxRightX - 1), topY + 1.0 + oxygenInTank2 / 2 - 1.0, 0.0, 0.32421875, (double)(oxygenInTank2 / 2 * 0.00390625f));
            tessellator.addVertexWithUV((double)minRightX, topY + 1.0 + oxygenInTank2 / 2 - 1.0, 0.0, 0.2578125, (double)(oxygenInTank2 / 2 * 0.00390625f));
            tessellator2.draw();
        }
        if (invalid) {
            final String value = GCCoreUtil.translate("gui.warning.invalidThermal");
            OverlayOxygenTanks.minecraft.fontRenderer.drawString(value, minLeftX - 18 - OverlayOxygenTanks.minecraft.fontRenderer.getStringWidth(value), (int)bottomY - heatLevelScaled - OverlayOxygenTanks.minecraft.fontRenderer.FONT_HEIGHT / 2 - 1, ColorUtil.to32BitColor(255, 255, 10, 10));
        }
    }
    
    static {
        guiTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/gui.png");
        OverlayOxygenTanks.minecraft = FMLClientHandler.instance().getClient();
    }
}
