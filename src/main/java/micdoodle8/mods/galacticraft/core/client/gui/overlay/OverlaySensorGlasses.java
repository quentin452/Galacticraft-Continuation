package micdoodle8.mods.galacticraft.core.client.gui.overlay;

import cpw.mods.fml.relauncher.*;
import net.minecraft.client.*;
import net.minecraft.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.gui.*;
import net.minecraft.util.*;
import org.lwjgl.opengl.*;
import cpw.mods.fml.client.*;
import net.minecraft.client.renderer.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import micdoodle8.mods.galacticraft.core.util.*;
import java.util.*;
import net.minecraft.client.entity.*;
import micdoodle8.mods.galacticraft.core.*;

@SideOnly(Side.CLIENT)
public class OverlaySensorGlasses extends Overlay
{
    private static final ResourceLocation hudTexture;
    private static final ResourceLocation indicatorTexture;
    private static Minecraft minecraft;
    private static int zoom;
    
    public static void renderSensorGlassesMain(final ItemStack stack, final EntityPlayer player, final ScaledResolution resolution, final float partialTicks, final boolean hasScreen, final int mouseX, final int mouseY) {
        ++OverlaySensorGlasses.zoom;
        final float f = MathHelper.sin(OverlaySensorGlasses.zoom / 80.0f) * 0.1f + 0.1f;
        final ScaledResolution scaledresolution = ClientUtil.getScaledRes(OverlaySensorGlasses.minecraft, OverlaySensorGlasses.minecraft.displayWidth, OverlaySensorGlasses.minecraft.displayHeight);
        final int i = scaledresolution.getScaledWidth();
        final int k = scaledresolution.getScaledHeight();
        OverlaySensorGlasses.minecraft.entityRenderer.setupOverlayRendering();
        GL11.glEnable(3042);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(3008);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(OverlaySensorGlasses.hudTexture);
        final Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(i / 2 - 2 * k - f * 80.0f), (double)(k + f * 40.0f), -90.0, 0.0, 1.0);
        tessellator.addVertexWithUV((double)(i / 2 + 2 * k + f * 80.0f), (double)(k + f * 40.0f), -90.0, 1.0, 1.0);
        tessellator.addVertexWithUV((double)(i / 2 + 2 * k + f * 80.0f), 0.0 - f * 40.0f, -90.0, 1.0, 0.0);
        tessellator.addVertexWithUV((double)(i / 2 - 2 * k - f * 80.0f), 0.0 - f * 40.0f, -90.0, 0.0, 0.0);
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glEnable(3008);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public static void renderSensorGlassesValueableBlocks(final ItemStack stack, final EntityPlayer player, final ScaledResolution resolution, final float partialTicks, final boolean hasScreen, final int mouseX, final int mouseY) {
        for (final BlockVec3 coords : ClientProxyCore.valueableBlocks) {
            final double var52 = ClientProxyCore.playerPosX - coords.x - 0.5;
            final double var53 = ClientProxyCore.playerPosY - coords.y - 0.5;
            final double var54 = ClientProxyCore.playerPosZ - coords.z - 0.5;
            final float var55 = (float)Math.toDegrees(Math.atan2(var52, var54));
            final double var56 = Math.sqrt(var52 * var52 + var53 * var53 + var54 * var54) * 0.5;
            final double var57 = Math.sqrt(var52 * var52 + var54 * var54) * 0.5;
            final ScaledResolution var58 = ClientUtil.getScaledRes(OverlaySensorGlasses.minecraft, OverlaySensorGlasses.minecraft.displayWidth, OverlaySensorGlasses.minecraft.displayHeight);
            final int var59 = var58.getScaledWidth();
            final int var60 = var58.getScaledHeight();
            boolean var61 = false;
            final EntityClientPlayerMP client = PlayerUtil.getPlayerBaseClientFromPlayer((EntityPlayer)OverlaySensorGlasses.minecraft.thePlayer, false);
            if (client != null) {
                final GCPlayerStatsClient stats = GCPlayerStatsClient.get((EntityPlayerSP)client);
                var61 = stats.usingAdvancedGoggles;
            }
            OverlaySensorGlasses.minecraft.fontRenderer.drawString(GCCoreUtil.translate("gui.sensor.advanced") + ": " + (var61 ? GCCoreUtil.translate("gui.sensor.advancedon") : GCCoreUtil.translate("gui.sensor.advancedoff")), var59 / 2 - 50, 4, 243855);
            try {
                GL11.glPushMatrix();
                if (var56 >= 4.0) {
                    continue;
                }
                GL11.glColor4f(0.0f, 1.0f, 0.7764706f, (float)Math.min(1.0, Math.max(0.2, (var56 - 1.0) * 0.1)));
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(OverlaySensorGlasses.indicatorTexture);
                GL11.glRotatef(-var55 - ClientProxyCore.playerRotationYaw + 180.0f, 0.0f, 0.0f, 1.0f);
                GL11.glTranslated(0.0, var61 ? (-var56 * 16.0) : (-var57 * 16.0), 0.0);
                GL11.glRotatef(-(-var55 - ClientProxyCore.playerRotationYaw + 180.0f), 0.0f, 0.0f, 1.0f);
                Overlay.drawCenteringRectangle((double)(var59 / 2), (double)(var60 / 2), 1.0, 8.0, 8.0);
            }
            finally {
                GL11.glPopMatrix();
            }
        }
    }
    
    static {
        hudTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/hud.png");
        indicatorTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/indicator.png");
        OverlaySensorGlasses.minecraft = FMLClientHandler.instance().getClient();
        OverlaySensorGlasses.zoom = 0;
    }
}
