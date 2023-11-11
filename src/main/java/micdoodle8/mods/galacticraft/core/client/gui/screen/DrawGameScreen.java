package micdoodle8.mods.galacticraft.core.client.gui.screen;

import micdoodle8.mods.galacticraft.api.client.*;
import java.nio.*;
import net.minecraft.tileentity.*;
import net.minecraft.entity.*;
import net.minecraft.client.renderer.entity.*;
import cpw.mods.fml.client.*;
import net.minecraft.client.renderer.texture.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.api.*;
import org.lwjgl.opengl.*;
import net.minecraft.world.*;
import net.minecraft.client.renderer.*;

public class DrawGameScreen extends IScreenManager
{
    private TextureManager renderEngine;
    private static FloatBuffer colorBuffer;
    private static int texCount;
    private float tickDrawn;
    public boolean initialise;
    public boolean initialiseLast;
    private boolean readyToInitialise;
    private int tileCount;
    private int callCount;
    private int tickMapDone;
    private float scaleX;
    private float scaleZ;
    public TileEntity driver;
    public Class telemetryLastClass;
    public String telemetryLastName;
    public Entity telemetryLastEntity;
    public Render telemetryLastRender;
    public static DynamicTexture reusableMap;
    public int[] localMap;
    public boolean mapDone;
    public boolean mapFirstTick;
    
    public DrawGameScreen(final float scaleXparam, final float scaleZparam, final TileEntity te) {
        this.renderEngine = FMLClientHandler.instance().getClient().renderEngine;
        this.tickDrawn = -1.0f;
        this.initialise = true;
        this.initialiseLast = false;
        this.readyToInitialise = false;
        this.tileCount = 0;
        this.callCount = 0;
        this.tickMapDone = -1;
        this.localMap = null;
        this.mapDone = false;
        this.mapFirstTick = false;
        this.scaleX = scaleXparam;
        this.scaleZ = scaleZparam;
        this.driver = te;
        this.mapFirstTick = true;
    }
    
    public boolean check(final float scaleXparam, final float scaleZparam) {
        return this.mapDone && this.scaleX == scaleXparam && this.scaleZ == scaleZparam;
    }
    
    private void makeMap() {
        if (this.mapDone || DrawGameScreen.reusableMap == null || this.driver.getWorldObj().provider.dimensionId != 0) {
            return;
        }
        this.localMap = new int[123904];
        final boolean result = MapUtil.getMap(this.localMap, this.driver.getWorldObj(), this.driver.xCoord, this.driver.zCoord);
        if (result) {
            TextureUtil.uploadTexture(DrawGameScreen.reusableMap.getGlTextureId(), this.localMap, 352, 352);
            this.mapDone = true;
            GCLog.debug("Created texture no:" + DrawGameScreen.texCount++);
        }
    }
    
    public void drawScreen(final int type, final float ticks, final boolean cornerBlock) {
        if (type >= GalacticraftRegistry.getMaxScreenTypes()) {
            System.out.println("Wrong gamescreen type detected - this is a bug." + type);
            return;
        }
        if (cornerBlock) {
            if ((this.mapFirstTick || (int)ticks % 400 == 0) && !this.mapDone && this.tickMapDone != (int)ticks) {
                this.tickMapDone = (int)ticks;
                this.makeMap();
                this.mapFirstTick = false;
            }
            this.doDraw(type, ticks);
            this.initialise = true;
            this.initialiseLast = false;
            return;
        }
        if (this.initialise) {
            if (!this.initialiseLast) {
                this.tickDrawn = ticks;
                this.readyToInitialise = false;
                this.initialiseLast = true;
                return;
            }
            if (!this.readyToInitialise && ticks == this.tickDrawn) {
                return;
            }
            if (!this.readyToInitialise) {
                this.readyToInitialise = true;
                this.tickDrawn = ticks;
                this.tileCount = 1;
                return;
            }
            if (ticks == this.tickDrawn) {
                ++this.tileCount;
                return;
            }
            this.initialise = false;
            this.initialiseLast = false;
            this.readyToInitialise = false;
        }
        if (++this.callCount < this.tileCount) {
            if (this.callCount == 1 || this.tickDrawn == ticks) {
                this.tickDrawn = ticks;
                return;
            }
            this.initialise = true;
        }
        if (this.callCount == this.tileCount) {
            this.callCount = 0;
            if (this.tileCount > 1 && ticks != this.tickDrawn) {
                this.initialise = true;
            }
        }
        this.doDraw(type, this.tickDrawn = ticks);
    }
    
    private void doDraw(final int type, final float ticks) {
        final float lightMapSaveX = OpenGlHelper.lastBrightnessX;
        final float lightMapSaveY = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
        if (type > 0) {
            GL11.glDisable(2896);
        }
        GalacticraftRegistry.getGameScreen(type).render(type, ticks, this.scaleX, this.scaleZ, (IScreenManager)this);
        if (type > 0) {
            GL11.glEnable(2896);
        }
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightMapSaveX, lightMapSaveY);
    }
    
    public WorldProvider getWorldProvider() {
        if (this.driver != null) {
            return this.driver.getWorldObj().provider;
        }
        return null;
    }
    
    static {
        DrawGameScreen.colorBuffer = GLAllocation.createDirectFloatBuffer(16);
        DrawGameScreen.texCount = 1;
    }
}
