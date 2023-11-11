package micdoodle8.mods.galacticraft.core.client.gui.overlay;

import cpw.mods.fml.relauncher.*;
import net.minecraft.client.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.client.gui.*;
import cpw.mods.fml.client.*;

@SideOnly(Side.CLIENT)
public class OverlayLaunchCountdown extends Overlay
{
    private static Minecraft minecraft;
    
    public static void renderCountdownOverlay() {
        GL11.glDisable(2896);
        int count = ((EntitySpaceshipBase)OverlayLaunchCountdown.minecraft.thePlayer.ridingEntity).timeUntilLaunch / 2;
        count = (int)Math.floor(count / 10.0f);
        final ScaledResolution scaledresolution = ClientUtil.getScaledRes(OverlayLaunchCountdown.minecraft, OverlayLaunchCountdown.minecraft.displayWidth, OverlayLaunchCountdown.minecraft.displayHeight);
        final int width = scaledresolution.getScaledWidth();
        final int height = scaledresolution.getScaledHeight();
        OverlayLaunchCountdown.minecraft.entityRenderer.setupOverlayRendering();
        GL11.glPushMatrix();
        if (count <= 10) {
            GL11.glScalef(4.0f, 4.0f, 0.0f);
            OverlayLaunchCountdown.minecraft.fontRenderer.drawString(String.valueOf(count), width / 8 - OverlayLaunchCountdown.minecraft.fontRenderer.getStringWidth(String.valueOf(count)) / 2, height / 20, ColorUtil.to32BitColor(255, 255, 0, 0));
        }
        else {
            GL11.glScalef(2.0f, 2.0f, 0.0f);
            OverlayLaunchCountdown.minecraft.fontRenderer.drawString(String.valueOf(count), width / 4 - OverlayLaunchCountdown.minecraft.fontRenderer.getStringWidth(String.valueOf(count)) / 2, height / 8, ColorUtil.to32BitColor(255, 255, 0, 0));
        }
        GL11.glPopMatrix();
        GL11.glEnable(2896);
    }
    
    static {
        OverlayLaunchCountdown.minecraft = FMLClientHandler.instance().getClient();
    }
}
