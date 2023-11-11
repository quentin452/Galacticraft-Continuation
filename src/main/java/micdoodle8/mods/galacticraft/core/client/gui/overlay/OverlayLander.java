package micdoodle8.mods.galacticraft.core.client.gui.overlay;

import cpw.mods.fml.relauncher.*;
import net.minecraft.client.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.tick.*;
import net.minecraft.client.settings.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraft.client.gui.*;
import cpw.mods.fml.client.*;

@SideOnly(Side.CLIENT)
public class OverlayLander extends Overlay
{
    private static Minecraft minecraft;
    private static long screenTicks;
    
    public static void renderLanderOverlay() {
        ++OverlayLander.screenTicks;
        final ScaledResolution scaledresolution = ClientUtil.getScaledRes(OverlayLander.minecraft, OverlayLander.minecraft.displayWidth, OverlayLander.minecraft.displayHeight);
        final int width = scaledresolution.getScaledWidth();
        final int height = scaledresolution.getScaledHeight();
        OverlayLander.minecraft.entityRenderer.setupOverlayRendering();
        GL11.glPushMatrix();
        GL11.glScalef(2.0f, 2.0f, 0.0f);
        if (OverlayLander.minecraft.thePlayer.ridingEntity.motionY < -2.0) {
            OverlayLander.minecraft.fontRenderer.drawString(GCCoreUtil.translate("gui.warning"), width / 4 - OverlayLander.minecraft.fontRenderer.getStringWidth(GCCoreUtil.translate("gui.warning")) / 2, height / 8 - 20, ColorUtil.to32BitColor(255, 255, 0, 0));
            final int alpha = (int)(255.0 * Math.sin(OverlayLander.screenTicks / 20.0f));
            final String press1 = GCCoreUtil.translate("gui.lander.warning2");
            final String press2 = GCCoreUtil.translate("gui.lander.warning3");
            OverlayLander.minecraft.fontRenderer.drawString(press1 + GameSettings.getKeyDisplayString(KeyHandlerClient.spaceKey.getKeyCode()) + press2, width / 4 - OverlayLander.minecraft.fontRenderer.getStringWidth(press1 + GameSettings.getKeyDisplayString(KeyHandlerClient.spaceKey.getKeyCode()) + press2) / 2, height / 8, ColorUtil.to32BitColor(alpha, alpha, alpha, alpha));
        }
        GL11.glPopMatrix();
        if (OverlayLander.minecraft.thePlayer.ridingEntity.motionY != 0.0) {
            final String string = GCCoreUtil.translate("gui.lander.velocity") + ": " + Math.round(((EntityLander)OverlayLander.minecraft.thePlayer.ridingEntity).motionY * 1000.0) / 100.0 + " " + GCCoreUtil.translate("gui.lander.velocityu");
            final int color = ColorUtil.to32BitColor(255, (int)Math.floor(Math.abs(OverlayLander.minecraft.thePlayer.ridingEntity.motionY) * 51.0), 255 - (int)Math.floor(Math.abs(OverlayLander.minecraft.thePlayer.ridingEntity.motionY) * 51.0), 0);
            OverlayLander.minecraft.fontRenderer.drawString(string, width / 2 - OverlayLander.minecraft.fontRenderer.getStringWidth(string) / 2, height / 3, color);
        }
    }
    
    static {
        OverlayLander.minecraft = FMLClientHandler.instance().getClient();
    }
}
