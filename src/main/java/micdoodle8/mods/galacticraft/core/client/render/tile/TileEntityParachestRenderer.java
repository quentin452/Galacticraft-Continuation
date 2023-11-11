package micdoodle8.mods.galacticraft.core.client.render.tile;

import net.minecraft.client.renderer.tileentity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.client.model.block.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import org.lwjgl.opengl.*;
import net.minecraft.block.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.*;

@SideOnly(Side.CLIENT)
public class TileEntityParachestRenderer extends TileEntitySpecialRenderer
{
    private static final ResourceLocation parachestTexture;
    private final ModelParaChestTile chestModel;
    
    public TileEntityParachestRenderer() {
        this.chestModel = new ModelParaChestTile();
    }
    
    public void renderGCTileEntityTreasureChestAt(final TileEntityParaChest tile, final double par2, final double par4, final double par6, final float par8) {
        int var9;
        if (!tile.hasWorldObj()) {
            var9 = 0;
        }
        else {
            final Block var10 = tile.getBlockType();
            var9 = tile.getBlockMetadata();
            if (var10 != null && var9 == 0) {
                var9 = tile.getBlockMetadata();
            }
        }
        this.bindTexture(TileEntityParachestRenderer.parachestTexture);
        GL11.glPushMatrix();
        GL11.glEnable(32826);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glTranslatef((float)par2, (float)par4 + 1.0f, (float)par6 + 1.0f);
        GL11.glScalef(1.0f, -1.0f, -1.0f);
        GL11.glTranslatef(0.5f, 0.5f, 0.5f);
        short var11 = 0;
        if (var9 == 2) {
            var11 = 180;
        }
        if (var9 == 3) {
            var11 = 0;
        }
        if (var9 == 4) {
            var11 = 90;
        }
        if (var9 == 5) {
            var11 = -90;
        }
        GL11.glRotatef((float)var11, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
        float var12 = tile.prevLidAngle + (tile.lidAngle - tile.prevLidAngle) * par8;
        var12 = 1.0f - var12;
        var12 = 1.0f - var12 * var12 * var12;
        this.chestModel.chestLid.rotateAngleX = -(var12 * 3.1415927f / 4.0f);
        this.chestModel.renderAll(var12 == 0.0f);
        GL11.glDisable(32826);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public void renderTileEntityAt(final TileEntity par1TileEntity, final double par2, final double par4, final double par6, final float par8) {
        this.renderGCTileEntityTreasureChestAt((TileEntityParaChest)par1TileEntity, par2, par4, par6, par8);
    }
    
    static {
        parachestTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/parachest.png");
    }
}
