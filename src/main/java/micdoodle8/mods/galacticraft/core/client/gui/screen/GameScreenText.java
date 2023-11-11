package micdoodle8.mods.galacticraft.core.client.gui.screen;

import java.nio.*;
import cpw.mods.fml.common.*;
import org.lwjgl.*;
import micdoodle8.mods.galacticraft.api.client.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.client.entity.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.monster.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.client.render.entities.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;

public class GameScreenText implements IGameScreen
{
    private float frameA;
    private float frameBx;
    private float frameBy;
    private int yPos;
    private DoubleBuffer planes;

    public GameScreenText() {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.planes = BufferUtils.createDoubleBuffer(256);
        }
    }

    public void setFrameSize(final float frameSize) {
        this.frameA = frameSize;
    }

    @SideOnly(Side.CLIENT)
    public void render(final int type, final float ticks, final float sizeX, final float sizeY, final IScreenManager scr) {
        final DrawGameScreen screen = (DrawGameScreen)scr;
        this.frameBx = sizeX - this.frameA;
        this.frameBy = sizeY - this.frameA;
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
        this.yPos = 0;
        final TileEntityTelemetry telemeter = TileEntityTelemetry.getNearest(screen.driver);
        String strName = "";
        final String[] str = { GCCoreUtil.translate("gui.display.nolink"), "", "", "", "" };
        Render renderEntity = null;
        Entity entity = null;
        float Xmargin = 0.0f;
        if (telemeter != null && telemeter.clientData.length >= 3) {
            if (telemeter.clientClass != null) {
                if (telemeter.clientClass == screen.telemetryLastClass && (telemeter.clientClass != EntityPlayerMP.class || telemeter.clientName.equals(screen.telemetryLastName))) {
                    entity = screen.telemetryLastEntity;
                    renderEntity = screen.telemetryLastRender;
                    strName = screen.telemetryLastName;
                }
                else {
                    entity = null;
                    if (telemeter.clientClass == EntityPlayerMP.class) {
                        strName = telemeter.clientName;
                        entity = (Entity)new EntityOtherPlayerMP(screen.driver.getWorldObj(), telemeter.clientGameProfile);
                        renderEntity = (Render) RenderManager.instance.entityRenderMap.get(EntityPlayer.class);
                    }
                    else {
                        try {
                            entity = (Entity) telemeter.clientClass.getConstructor(World.class).newInstance(screen.driver.getWorldObj());
                        }
                        catch (Exception ex) {}
                        if (entity != null) {
                            strName = entity.getCommandSenderName();
                        }
                        renderEntity = (Render) RenderManager.instance.entityRenderMap.get(telemeter.clientClass);
                    }
                }
                if (entity instanceof EntityHorse) {
                    ((EntityHorse)entity).setHorseType(telemeter.clientData[3]);
                    ((EntityHorse)entity).setHorseVariant(telemeter.clientData[4]);
                }
                if (entity instanceof EntityVillager) {
                    ((EntityVillager)entity).setProfession(telemeter.clientData[3]);
                    ((EntityVillager)entity).setGrowingAge(telemeter.clientData[4]);
                }
                else if (entity instanceof EntityWolf) {
                    ((EntityWolf)entity).setCollarColor(telemeter.clientData[3]);
                    ((EntityWolf)entity).func_70918_i(telemeter.clientData[4] == 1);
                }
                else if (entity instanceof EntitySheep) {
                    ((EntitySheep)entity).setFleeceColor(telemeter.clientData[3]);
                    ((EntitySheep)entity).setSheared(telemeter.clientData[4] == 1);
                }
                else if (entity instanceof EntityOcelot) {
                    ((EntityOcelot)entity).setTameSkin(telemeter.clientData[3]);
                }
                else if (entity instanceof EntitySkeleton) {
                    ((EntitySkeleton)entity).setSkeletonType(telemeter.clientData[3]);
                }
                else if (entity instanceof EntityZombie) {
                    ((EntityZombie)entity).setVillager(telemeter.clientData[3] == 1);
                    ((EntityZombie)entity).setChild(telemeter.clientData[4] == 1);
                }
            }
            if (entity instanceof ITelemetry) {
                ((ITelemetry)entity).receiveData(telemeter.clientData, str);
            }
            else if (entity instanceof EntityLivingBase) {
                str[0] = ((telemeter.clientData[0] > 0) ? GCCoreUtil.translate("gui.player.ouch") : "");
                if (telemeter.clientData[1] >= 0) {
                    str[1] = GCCoreUtil.translate("gui.player.health") + ": " + telemeter.clientData[1] + "%";
                }
                else {
                    str[1] = "";
                }
                str[2] = "" + telemeter.clientData[2] + " " + GCCoreUtil.translate("gui.player.bpm");
                if (telemeter.clientData[3] > -1) {
                    str[3] = GCCoreUtil.translate("gui.player.food") + ": " + telemeter.clientData[3] + "%";
                }
                if (telemeter.clientData[4] > -1) {
                    int oxygen = telemeter.clientData[4];
                    oxygen = oxygen % 4096 + oxygen / 4096;
                    if (oxygen == 180 || oxygen == 90) {
                        str[4] = GCCoreUtil.translate("gui.oxygenStorage.desc.1") + ": OK";
                    }
                    else {
                        str[4] = GCCoreUtil.translate("gui.oxygenStorage.desc.1") + ": " + this.makeOxygenString(oxygen) + GCCoreUtil.translate("gui.seconds");
                    }
                }
            }
            else if (telemeter.clientData[2] >= 0) {
                str[2] = makeSpeedString(telemeter.clientData[2]);
            }
        }
        else {
            final World w1 = screen.driver.getWorldObj();
            final int time1 = (w1 != null) ? ((int)((w1.getWorldTime() + 6000L) % 24000L)) : 0;
            str[2] = this.makeTimeString(time1 * 360);
        }
        final int textWidthPixels = 155;
        int textHeightPixels = 60;
        if (str[3].isEmpty()) {
            textHeightPixels -= 10;
        }
        if (str[4].isEmpty()) {
            textHeightPixels -= 10;
        }
        float borders = this.frameA * 2.0f + 0.05f * Math.min(sizeX, sizeY);
        float scaleXTest = (sizeX - borders) / textWidthPixels;
        float scaleYTest = (sizeY - borders) / textHeightPixels;
        float scale = sizeX;
        if (scaleYTest < scaleXTest) {
            scale = sizeY;
        }
        borders = this.frameA * 2.0f + 0.05f * scale;
        scaleXTest = (sizeX - borders) / textWidthPixels;
        scaleYTest = (sizeY - borders) / textHeightPixels;
        scale = sizeX;
        float scaleText = scaleXTest;
        if (scaleYTest < scaleXTest) {
            scale = sizeY;
            scaleText = scaleYTest;
        }
        final float border = this.frameA + 0.025f * scale;
        if (entity != null && renderEntity != null) {
            Xmargin = (sizeX - borders) / 2.0f;
        }
        final float Xoffset = (sizeX - borders - textWidthPixels * scaleText) / 2.0f + Xmargin;
        final float Yoffset = (sizeY - borders - textHeightPixels * scaleText) / 2.0f + scaleText;
        GL11.glTranslatef(border + Xoffset, border + Yoffset, 0.0f);
        GL11.glScalef(scaleText, scaleText, 1.0f);
        final int whiteColour = ColorUtil.to32BitColor(255, 240, 216, 255);
        this.drawText(strName, whiteColour);
        this.drawText(str[0], whiteColour);
        this.drawText(str[1], whiteColour);
        this.drawText(str[2], whiteColour);
        this.drawText(str[3], whiteColour);
        this.drawText(str[4], whiteColour);
        if (renderEntity != null && entity != null) {
            GL11.glTranslatef(-Xmargin / 2.0f / scaleText, textHeightPixels / 2 + (-Yoffset + (sizeY - borders) / 2.0f) / scaleText, -5.0E-4f);
            final float scalefactor = 38.0f / (float)Math.pow(Math.max(entity.height, entity.width), 0.65);
            GL11.glScalef(scalefactor, scalefactor, 0.0015f);
            GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
            if (entity instanceof ITelemetry) {
                ((ITelemetry)entity).adjustDisplay(telemeter.clientData);
            }
            RenderPlayerGC.flagThermalOverride = true;
            renderEntity.doRender(entity, 0.0, 0.0, 0.0, 0.0f, 1.0f);
            RenderPlayerGC.flagThermalOverride = false;
            GL11.glEnable(32826);
            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glDisable(3553);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        }
        screen.telemetryLastClass = ((telemeter == null) ? null : telemeter.clientClass);
        screen.telemetryLastEntity = entity;
        screen.telemetryLastRender = renderEntity;
        screen.telemetryLastName = strName;
        GL11.glDisable(12291);
        GL11.glDisable(12290);
        GL11.glDisable(12289);
        GL11.glDisable(12288);
    }

    private String makeTimeString(final int l) {
        final int hrs = l / 360000;
        final int mins = l / 6000 - hrs * 60;
        final int secs = l / 100 - hrs * 3600 - mins * 60;
        final String hrsStr = (hrs > 9) ? ("" + hrs) : ("0" + hrs);
        final String minsStr = (mins > 9) ? ("" + mins) : ("0" + mins);
        final String secsStr = (secs > 9) ? ("" + secs) : ("0" + secs);
        return hrsStr + ":" + minsStr + ":" + secsStr;
    }

    public static String makeSpeedString(final int speed100) {
        final int sp1 = speed100 / 100;
        final int sp2 = speed100 % 100;
        final String spstr1 = GCCoreUtil.translate("gui.rocket.speed") + ": " + sp1;
        final String spstr2 = ((sp2 > 9) ? "" : "0") + sp2;
        return spstr1 + "." + spstr2 + " " + GCCoreUtil.translate("gui.lander.velocityu");
    }

    private String makeHealthString(final int hearts2) {
        final int sp1 = hearts2 / 2;
        final int sp2 = hearts2 % 2 * 5;
        final String spstr1 = "" + sp1;
        final String spstr2 = "" + sp2;
        return spstr1 + "." + spstr2 + " hearts";
    }

    private String makeOxygenString(final int oxygen) {
        final int sp1 = oxygen * 9 / 20;
        final int sp2 = oxygen * 9 % 20 / 2;
        final String spstr1 = "" + sp1;
        final String spstr2 = "" + sp2;
        return spstr1 + "." + spstr2;
    }

    private void drawText(final String str, final int colour) {
        Minecraft.getMinecraft().fontRenderer.drawString(str, 0, this.yPos, colour, false);
        this.yPos += 10;
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

    private void planeEquation(final float x1, final float y1, final float z1, final float x2, final float y2, final float z2, final float x3, final float y3, final float z3) {
        final double[] result = { y1 * (z2 - z3) + y2 * (z3 - z1) + y3 * (z1 - z2), z1 * (x2 - x3) + z2 * (x3 - x1) + z3 * (x1 - x2), x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2), -(x1 * (y2 * z3 - y3 * z2) + x2 * (y3 * z1 - y1 * z3) + x3 * (y1 * z2 - y2 * z1)) };
        this.planes.put(result, 0, 4);
        this.planes.position(0);
    }
}
