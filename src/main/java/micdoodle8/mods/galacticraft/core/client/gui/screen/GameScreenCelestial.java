package micdoodle8.mods.galacticraft.core.client.gui.screen;

import net.minecraft.client.renderer.texture.*;
import java.nio.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.client.*;
import org.lwjgl.*;
import micdoodle8.mods.galacticraft.api.client.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.api.world.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.world.*;
import net.minecraft.client.renderer.*;
import micdoodle8.mods.galacticraft.api.galaxies.*;
import java.util.*;
import org.lwjgl.util.vector.*;
import micdoodle8.mods.galacticraft.api.event.client.*;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.eventhandler.*;
import micdoodle8.mods.galacticraft.core.client.render.*;

public class GameScreenCelestial implements IGameScreen
{
    private TextureManager renderEngine;
    private float frameA;
    private float frameBx;
    private float frameBy;
    private float centreX;
    private float centreY;
    private float scale;
    private final int lineSegments = 90;
    private final float cos;
    private final float sin;
    private DoubleBuffer planes;
    
    public GameScreenCelestial() {
        this.cos = (float)Math.cos(0.06981317007977318);
        this.sin = (float)Math.sin(0.06981317007977318);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.renderEngine = FMLClientHandler.instance().getClient().renderEngine;
            this.planes = BufferUtils.createDoubleBuffer(256);
        }
    }
    
    public void setFrameSize(final float frameSize) {
        this.frameA = frameSize;
    }
    
    public void render(final int type, final float ticks, final float scaleX, final float scaleY, final IScreenManager scr) {
        this.centreX = scaleX / 2.0f;
        this.centreY = scaleY / 2.0f;
        this.frameBx = scaleX - this.frameA;
        this.frameBy = scaleY - this.frameA;
        this.scale = Math.max(scaleX, scaleY) - 0.2f;
        this.drawBlackBackground(0.0f);
        this.planeEquation(this.frameA, this.frameA, 0.0f, this.frameA, this.frameBy, 0.0f, this.frameA, this.frameBy, 1.0f);
        GL11.glClipPlane(12288, this.planes);
        GL11.glEnable(12288);
        this.planeEquation(this.frameBx, this.frameBy, 0.0f, this.frameBx, this.frameA, 0.0f, this.frameBx, this.frameA, 1.0f);
        GL11.glClipPlane(12289, this.planes);
        GL11.glEnable(12289);
        this.planeEquation(this.frameA, this.frameBy, 0.0f, this.frameBx, this.frameBy, 0.0f, this.frameBx, this.frameBy, 1.0f);
        GL11.glClipPlane(12290, this.planes);
        GL11.glEnable(12290);
        this.planeEquation(this.frameBx, this.frameA, 0.0f, this.frameA, this.frameA, 0.0f, this.frameA, this.frameA, 1.0f);
        GL11.glClipPlane(12291, this.planes);
        GL11.glEnable(12291);
        switch (type) {
            case 2: {
                final WorldProvider wp = scr.getWorldProvider();
                CelestialBody body = null;
                if (wp instanceof IGalacticraftWorldProvider) {
                    body = ((IGalacticraftWorldProvider)wp).getCelestialBody();
                }
                if (body == null) {
                    body = (CelestialBody)GalacticraftCore.planetOverworld;
                }
                this.drawCelestialBodies(body, ticks);
                break;
            }
            case 3: {
                this.drawCelestialBodiesZ((CelestialBody)GalacticraftCore.planetOverworld, ticks);
                break;
            }
            case 4: {
                this.drawPlanetsTest(ticks);
                break;
            }
        }
        GL11.glDisable(12291);
        GL11.glDisable(12290);
        GL11.glDisable(12289);
        GL11.glDisable(12288);
    }
    
    private void drawBlackBackground(final float greyLevel) {
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        final Tessellator tess = Tessellator.instance;
        GL11.glColor4f(greyLevel, greyLevel, greyLevel, 1.0f);
        tess.startDrawingQuads();
        tess.addVertex((double)this.frameA, (double)this.frameBy, 0.004999999888241291);
        tess.addVertex((double)this.frameBx, (double)this.frameBy, 0.004999999888241291);
        tess.addVertex((double)this.frameBx, (double)this.frameA, 0.004999999888241291);
        tess.addVertex((double)this.frameA, (double)this.frameA, 0.004999999888241291);
        tess.draw();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(3553);
    }
    
    private void drawCelestialBodies(final CelestialBody body, final float ticks) {
        Star star = null;
        SolarSystem solarSystem = null;
        if (body instanceof Planet) {
            solarSystem = ((Planet)body).getParentSolarSystem();
        }
        else if (body instanceof Moon) {
            solarSystem = ((Moon)body).getParentPlanet().getParentSolarSystem();
        }
        else if (body instanceof Satellite) {
            solarSystem = ((Satellite)body).getParentPlanet().getParentSolarSystem();
        }
        if (solarSystem == null) {
            solarSystem = GalacticraftCore.solarSystemSol;
        }
        star = solarSystem.getMainStar();
        if (star != null && star.getBodyIcon() != null) {
            this.drawCelestialBody((CelestialBody)star, 0.0f, 0.0f, ticks, 6.0f);
        }
        final String mainSolarSystem = solarSystem.getUnlocalizedName();
        for (final Planet planet : GalaxyRegistry.getRegisteredPlanets().values()) {
            if (planet.getParentSolarSystem() != null && planet.getBodyIcon() != null && planet.getParentSolarSystem().getUnlocalizedName().equalsIgnoreCase(mainSolarSystem)) {
                final Vector3f pos = this.getCelestialBodyPosition((CelestialBody)planet, ticks);
                this.drawCircle((CelestialBody)planet);
                this.drawCelestialBody((CelestialBody)planet, pos.x, pos.y, ticks, (planet.getRelativeDistanceFromCenter().unScaledDistance < 1.5f) ? 2.0f : 2.8f);
            }
        }
    }
    
    private void drawCelestialBodiesZ(final CelestialBody planet, final float ticks) {
        this.drawCelestialBody(planet, 0.0f, 0.0f, ticks, 11.0f);
        for (final Moon moon : GalaxyRegistry.getRegisteredMoons().values()) {
            if (moon.getParentPlanet() == planet && moon.getBodyIcon() != null) {
                final Vector3f pos = this.getCelestialBodyPosition((CelestialBody)moon, ticks);
                this.drawCircle((CelestialBody)moon);
                this.drawCelestialBody((CelestialBody)moon, pos.x, pos.y, ticks, 4.0f);
            }
        }
        for (final Satellite satellite : GalaxyRegistry.getRegisteredSatellites().values()) {
            if (satellite.getParentPlanet() == planet) {
                final Vector3f pos = this.getCelestialBodyPosition((CelestialBody)satellite, ticks);
                this.drawCircle((CelestialBody)satellite);
                this.drawCelestialBody((CelestialBody)satellite, pos.x, pos.y, ticks, 3.0f);
            }
        }
    }
    
    private void drawTexturedRect(final float x, final float y, final float width, final float height) {
        final Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)x, (double)(y + height), 0.0, 0.0, 1.0);
        tessellator.addVertexWithUV((double)(x + width), (double)(y + height), 0.0, 1.0, 1.0);
        tessellator.addVertexWithUV((double)(x + width), (double)y, 0.0, 1.0, 0.0);
        tessellator.addVertexWithUV((double)x, (double)y, 0.0, 0.0, 0.0);
        tessellator.draw();
    }
    
    private void drawCelestialBody(final CelestialBody planet, final float xPos, final float yPos, final float ticks, final float relSize) {
        if (xPos + this.centreX > this.frameBx || xPos + this.centreX < this.frameA) {
            return;
        }
        if (yPos + this.centreY > this.frameBy || yPos + this.centreY < this.frameA) {
            return;
        }
        GL11.glPushMatrix();
        GL11.glTranslatef(xPos + this.centreX, yPos + this.centreY, 0.0f);
        final float alpha = 1.0f;
        final CelestialBodyRenderEvent.Pre preEvent = new CelestialBodyRenderEvent.Pre(planet, planet.getBodyIcon(), 12);
        MinecraftForge.EVENT_BUS.post((Event)preEvent);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
        if (preEvent.celestialBodyTexture != null) {
            this.renderEngine.bindTexture(preEvent.celestialBodyTexture);
        }
        if (!preEvent.isCanceled()) {
            final float size = relSize / 70.0f * this.scale;
            this.drawTexturedRect(-size / 2.0f, -size / 2.0f, size, size);
        }
        final CelestialBodyRenderEvent.Post postEvent = new CelestialBodyRenderEvent.Post(planet);
        MinecraftForge.EVENT_BUS.post((Event)postEvent);
        GL11.glPopMatrix();
    }
    
    private void drawCircle(final CelestialBody cBody) {
        GL11.glPushMatrix();
        GL11.glTranslatef(this.centreX, this.centreY, 0.002f);
        GL11.glDisable(3553);
        final float sd = 0.002514f * this.scale;
        float x = this.getScale(cBody);
        float y = 0.0f;
        final float grey = 0.1f + 0.65f * Math.max(0.0f, 0.5f - x);
        x = x * this.scale / sd;
        GL11.glColor4f(grey, grey, grey, 1.0f);
        GL11.glLineWidth(0.002f);
        GL11.glScalef(sd, sd, sd);
        final CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre preEvent = new CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre(cBody, new Vector3f(0.0f, 0.0f, 0.0f));
        MinecraftForge.EVENT_BUS.post((Event)preEvent);
        if (!preEvent.isCanceled()) {
            GL11.glBegin(2);
            for (int i = 0; i < 90; ++i) {
                GL11.glVertex2f(x, y);
                final float temp = x;
                x = this.cos * x - this.sin * y;
                y = this.sin * temp + this.cos * y;
            }
            GL11.glEnd();
        }
        final CelestialBodyRenderEvent.CelestialRingRenderEvent.Post postEvent = new CelestialBodyRenderEvent.CelestialRingRenderEvent.Post(cBody);
        MinecraftForge.EVENT_BUS.post((Event)postEvent);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
    }
    
    private Vector3f getCelestialBodyPosition(final CelestialBody cBody, final float ticks) {
        final float timeScale = (cBody instanceof Planet) ? 200.0f : 2.0f;
        final float distanceFromCenter = this.getScale(cBody) * this.scale;
        return new Vector3f((float)Math.sin(ticks / (timeScale * cBody.getRelativeOrbitTime()) + cBody.getPhaseShift()) * distanceFromCenter, (float)Math.cos(ticks / (timeScale * cBody.getRelativeOrbitTime()) + cBody.getPhaseShift()) * distanceFromCenter, 0.0f);
    }
    
    private float getScale(final CelestialBody celestialBody) {
        float distance = celestialBody.getRelativeDistanceFromCenter().unScaledDistance;
        if (distance >= 1.375f) {
            if (distance >= 1.5f) {
                distance *= 1.15f;
            }
            else {
                distance += 0.075f;
            }
        }
        return 0.007142857f * distance * ((celestialBody instanceof Planet) ? 25.0f : 3.5f);
    }
    
    private void planeEquation(final float x1, final float y1, final float z1, final float x2, final float y2, final float z2, final float x3, final float y3, final float z3) {
        final double[] result = { y1 * (z2 - z3) + y2 * (z3 - z1) + y3 * (z1 - z2), z1 * (x2 - x3) + z2 * (x3 - x1) + z3 * (x1 - x2), x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2), -(x1 * (y2 * z3 - y3 * z2) + x2 * (y3 * z1 - y1 * z3) + x3 * (y1 * z2 - y2 * z1)) };
        this.planes.put(result, 0, 4);
        this.planes.position(0);
    }
    
    private void drawPlanetsTest(final float ticks) {
        GL11.glPushMatrix();
        GL11.glTranslatef(this.centreX, this.centreY, 0.0f);
        final int id = (int)(ticks / 600.0f) % 5;
        RenderPlanet.renderID(id, this.scale, ticks);
        GL11.glPopMatrix();
    }
}
