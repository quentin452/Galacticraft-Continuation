package micdoodle8.mods.galacticraft.core.client.render.tile;

import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.client.model.block.*;
import net.minecraft.tileentity.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.*;

public class TileEntityNasaWorkbenchRenderer extends TileEntitySpecialRenderer
{
    private static final ResourceLocation assemblyTableTexture;
    public ModelNasaWorkbench model;
    
    public TileEntityNasaWorkbenchRenderer() {
        this.model = new ModelNasaWorkbench();
    }
    
    public void renderTileEntityAt(final TileEntity var1, final double par2, final double par4, final double par6, final float var8) {
        GL11.glPushMatrix();
        GL11.glEnable(32826);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        GL11.glTranslatef(0.5f, 3.0f, 0.5f);
        GL11.glScalef(1.3f, -1.3f, -1.3f);
        this.bindTexture(TileEntityNasaWorkbenchRenderer.assemblyTableTexture);
        this.model.renderAll();
        GL11.glDisable(32826);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    static {
        assemblyTableTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/assembly.png");
    }
}
