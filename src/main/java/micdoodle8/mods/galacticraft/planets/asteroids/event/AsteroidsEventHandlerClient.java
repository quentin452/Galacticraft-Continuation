package micdoodle8.mods.galacticraft.planets.asteroids.event;

import cpw.mods.fml.common.gameevent.*;
import net.minecraft.client.*;
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.*;
import micdoodle8.mods.galacticraft.api.world.*;
import micdoodle8.mods.galacticraft.planets.asteroids.client.*;
import net.minecraftforge.client.*;
import micdoodle8.mods.galacticraft.core.client.*;
import net.minecraft.client.multiplayer.*;
import cpw.mods.fml.relauncher.*;
import cpw.mods.fml.common.eventhandler.*;
import micdoodle8.mods.galacticraft.api.event.client.*;
import micdoodle8.mods.galacticraft.planets.asteroids.*;
import micdoodle8.mods.galacticraft.core.client.gui.screen.*;
import cpw.mods.fml.client.*;
import org.lwjgl.opengl.*;
import org.lwjgl.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.*;
import net.minecraft.world.*;

public class AsteroidsEventHandlerClient
{
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        final Minecraft minecraft = Minecraft.getMinecraft();
        final WorldClient world = minecraft.theWorld;
        if (world != null && world.provider instanceof WorldProviderAsteroids) {
            if (world.provider.getSkyRenderer() == null) {
                world.provider.setSkyRenderer((IRenderHandler)new SkyProviderAsteroids((IGalacticraftWorldProvider)world.provider));
            }
            if (world.provider.getCloudRenderer() == null) {
                world.provider.setCloudRenderer((IRenderHandler)new CloudRenderer());
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRingRender(final CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre renderEvent) {
        if (renderEvent.celestialBody.equals((Object)AsteroidsModule.planetAsteroids)) {
            if (FMLClientHandler.instance().getClient().currentScreen instanceof GuiCelestialSelection) {
                GL11.glColor4f(0.7f, 0.0f, 0.0f, 0.5f);
            }
            else {
                GL11.glColor4f(0.3f, 0.1f, 0.1f, 1.0f);
            }
            renderEvent.setCanceled(true);
            GL11.glBegin(2);
            final float theta = 0.06981317f;
            final float cos = (float)Math.cos(0.06981316953897476);
            final float sin = (float)Math.sin(0.06981316953897476);
            final float min = 72.0f;
            final float max = 78.0f;
            float x = max * renderEvent.celestialBody.getRelativeDistanceFromCenter().unScaledDistance;
            float y = 0.0f;
            for (int i = 0; i < 90; ++i) {
                GL11.glVertex2f(x, y);
                final float temp = x;
                x = cos * x - sin * y;
                y = sin * temp + cos * y;
            }
            GL11.glEnd();
            GL11.glBegin(2);
            x = min * renderEvent.celestialBody.getRelativeDistanceFromCenter().unScaledDistance;
            y = 0.0f;
            for (int i = 0; i < 90; ++i) {
                GL11.glVertex2f(x, y);
                final float temp = x;
                x = cos * x - sin * y;
                y = sin * temp + cos * y;
            }
            GL11.glEnd();
            GL11.glColor4f(0.7f, 0.0f, 0.0f, 0.1f);
            GL11.glBegin(7);
            x = min * renderEvent.celestialBody.getRelativeDistanceFromCenter().unScaledDistance;
            y = 0.0f;
            float x2 = max * renderEvent.celestialBody.getRelativeDistanceFromCenter().unScaledDistance;
            float y2 = 0.0f;
            for (int j = 0; j < 90; ++j) {
                GL11.glVertex2f(x2, y2);
                GL11.glVertex2f(x, y);
                float temp = x;
                x = cos * x - sin * y;
                y = sin * temp + cos * y;
                temp = x2;
                x2 = cos * x2 - sin * y2;
                y2 = sin * temp + cos * y2;
                GL11.glVertex2f(x, y);
                GL11.glVertex2f(x2, y2);
            }
            GL11.glEnd();
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onBodyRender(final CelestialBodyRenderEvent.Pre renderEvent) {
        if (renderEvent.celestialBody.equals((Object)AsteroidsModule.planetAsteroids)) {
            GL11.glRotatef(Sys.getTime() / 10.0f % 360.0f, 0.0f, 0.0f, 1.0f);
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onSpecialRender(final ClientProxyCore.EventSpecialRender event) {
        NetworkRenderer.renderNetworks((World)FMLClientHandler.instance().getClient().theWorld, event.partialTicks);
    }
}
