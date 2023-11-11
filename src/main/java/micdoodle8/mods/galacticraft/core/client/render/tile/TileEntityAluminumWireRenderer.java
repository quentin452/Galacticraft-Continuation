package micdoodle8.mods.galacticraft.core.client.render.tile;

import net.minecraft.client.renderer.tileentity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraftforge.client.model.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import cpw.mods.fml.client.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import net.minecraft.tileentity.*;

@SideOnly(Side.CLIENT)
public class TileEntityAluminumWireRenderer extends TileEntitySpecialRenderer
{
    private static final ResourceLocation aluminumWireTexture;
    public final IModelCustom model;
    public final IModelCustom model2;
    
    public TileEntityAluminumWireRenderer() {
        this.model = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/aluminumWire.obj"));
        this.model2 = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/aluminumWireHeavy.obj"));
    }
    
    public void renderModelAt(final TileEntityAluminumWire tileEntity, final double d, final double d1, final double d2, final float f) {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TileEntityAluminumWireRenderer.aluminumWireTexture);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d + 0.5f, (float)d1 + 0.5f, (float)d2 + 0.5f);
        GL11.glScalef(1.0f, -1.0f, -1.0f);
        final TileEntity[] adjecentConnections = EnergyUtil.getAdjacentPowerConnections(tileEntity);
        final int metadata = tileEntity.getWorldObj().getBlockMetadata(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
        IModelCustom model = null;
        if (metadata == 0) {
            model = this.model;
        }
        else {
            model = this.model2;
        }
        if (adjecentConnections[0] != null) {
            model.renderPart("Top");
        }
        if (adjecentConnections[1] != null) {
            model.renderPart("Bottom");
        }
        if (adjecentConnections[2] != null) {
            model.renderPart("Front");
        }
        if (adjecentConnections[3] != null) {
            model.renderPart("Back");
        }
        if (adjecentConnections[4] != null) {
            model.renderPart("Right");
        }
        if (adjecentConnections[5] != null) {
            model.renderPart("Left");
        }
        model.renderPart("Middle");
        GL11.glPopMatrix();
    }
    
    public void renderTileEntityAt(final TileEntity tileEntity, final double var2, final double var4, final double var6, final float var8) {
        this.renderModelAt((TileEntityAluminumWire)tileEntity, var2, var4, var6, var8);
    }
    
    static {
        aluminumWireTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/aluminumWire.png");
    }
}
