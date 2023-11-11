package micdoodle8.mods.galacticraft.core.client.render.tile;

import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.client.model.block.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.*;

public class TileEntitySolarPanelRenderer extends TileEntitySpecialRenderer
{
    private static final ResourceLocation solarPanelTexture;
    private static final ResourceLocation solarPanelAdvTexture;
    public ModelSolarPanel model;
    
    public TileEntitySolarPanelRenderer() {
        this.model = new ModelSolarPanel();
    }
    
    public void renderTileEntityAt(final TileEntity var1, final double par2, final double par4, final double par6, final float var8) {
        final TileEntitySolar panel = (TileEntitySolar)var1;
        if (var1.getBlockMetadata() >= 4) {
            this.bindTexture(TileEntitySolarPanelRenderer.solarPanelAdvTexture);
        }
        else {
            this.bindTexture(TileEntitySolarPanelRenderer.solarPanelTexture);
        }
        GL11.glPushMatrix();
        GL11.glEnable(32826);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        GL11.glTranslatef(0.5f, 1.0f, 0.5f);
        this.model.renderPole();
        GL11.glTranslatef(0.0f, 1.5f, 0.0f);
        GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
        GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        final float celestialAngle = (panel.getWorldObj().getCelestialAngle(1.0f) - 0.78469056f) * 360.0f;
        final float celestialAngle2 = panel.getWorldObj().getCelestialAngle(1.0f) * 360.0f;
        GL11.glRotatef(panel.currentAngle - (celestialAngle - celestialAngle2), 1.0f, 0.0f, 0.0f);
        this.model.renderPanel();
        GL11.glDisable(32826);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    static {
        solarPanelTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/solarPanelBasic.png");
        solarPanelAdvTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/solarPanelAdvanced.png");
    }
}
