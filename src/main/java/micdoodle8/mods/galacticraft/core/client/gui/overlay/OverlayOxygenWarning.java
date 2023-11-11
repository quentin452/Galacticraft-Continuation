package micdoodle8.mods.galacticraft.core.client.gui.overlay;

import cpw.mods.fml.relauncher.*;
import net.minecraft.client.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.client.gui.*;
import cpw.mods.fml.client.*;

@SideOnly(Side.CLIENT)
public class OverlayOxygenWarning extends Overlay
{
    private static Minecraft minecraft;
    private static long screenTicks;
    
    public static void renderOxygenWarningOverlay() {
        ++OverlayOxygenWarning.screenTicks;
        final ScaledResolution scaledresolution = ClientUtil.getScaledRes(OverlayOxygenWarning.minecraft, OverlayOxygenWarning.minecraft.displayWidth, OverlayOxygenWarning.minecraft.displayHeight);
        final int width = scaledresolution.getScaledWidth();
        final int height = scaledresolution.getScaledHeight();
        OverlayOxygenWarning.minecraft.entityRenderer.setupOverlayRendering();
        GL11.glEnable(2903);
        RenderHelper.enableStandardItemLighting();
        GL11.glPushMatrix();
        GL11.glScalef(2.0f, 2.0f, 0.0f);
        OverlayOxygenWarning.minecraft.fontRenderer.drawString(GCCoreUtil.translate("gui.warning"), width / 4 - OverlayOxygenWarning.minecraft.fontRenderer.getStringWidth(GCCoreUtil.translate("gui.warning")) / 2, height / 8 - 20, ColorUtil.to32BitColor(255, 255, 0, 0));
        final int alpha = (int)(200.0 * (Math.sin(OverlayOxygenWarning.screenTicks / 20.0f) * 0.5 + 0.5)) + 5;
        OverlayOxygenWarning.minecraft.fontRenderer.drawString(GCCoreUtil.translate("gui.oxygen.warning"), width / 4 - OverlayOxygenWarning.minecraft.fontRenderer.getStringWidth(GCCoreUtil.translate("gui.oxygen.warning")) / 2, height / 8, ColorUtil.to32BitColor(alpha, 255, 0, 0));
        GL11.glPopMatrix();
    }
    
    static {
        OverlayOxygenWarning.minecraft = FMLClientHandler.instance().getClient();
    }
}
