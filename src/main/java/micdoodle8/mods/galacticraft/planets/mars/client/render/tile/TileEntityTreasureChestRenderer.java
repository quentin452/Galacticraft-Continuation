package micdoodle8.mods.galacticraft.planets.mars.client.render.tile;

import net.minecraft.client.renderer.tileentity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.client.model.block.*;
import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import micdoodle8.mods.galacticraft.planets.mars.blocks.*;
import org.lwjgl.opengl.*;
import net.minecraft.block.*;
import net.minecraft.tileentity.*;

@SideOnly(Side.CLIENT)
public class TileEntityTreasureChestRenderer extends TileEntitySpecialRenderer
{
    private static final ResourceLocation treasureChestTexture;
    private static final ResourceLocation treasureLargeChestTexture;
    private final ModelTreasureChest chestModel;
    private final ModelTreasureChestLarge largeChestModel;
    
    public TileEntityTreasureChestRenderer() {
        this.chestModel = new ModelTreasureChest();
        this.largeChestModel = new ModelTreasureChestLarge();
    }
    
    public void renderGCTileEntityTreasureChestAt(final TileEntityTreasureChestMars par1GCTileEntityTreasureChest, final double par2, final double par4, final double par6, final float par8) {
        int var9;
        if (!par1GCTileEntityTreasureChest.hasWorldObj()) {
            var9 = 0;
        }
        else {
            final Block var10 = par1GCTileEntityTreasureChest.getBlockType();
            var9 = par1GCTileEntityTreasureChest.getBlockMetadata();
            if (var10 instanceof BlockTier2TreasureChest && var9 == 0) {
                ((BlockTier2TreasureChest)var10).unifyAdjacentChests(par1GCTileEntityTreasureChest.getWorldObj(), par1GCTileEntityTreasureChest.xCoord, par1GCTileEntityTreasureChest.yCoord, par1GCTileEntityTreasureChest.zCoord);
                var9 = par1GCTileEntityTreasureChest.getBlockMetadata();
            }
            par1GCTileEntityTreasureChest.checkForAdjacentChests();
        }
        if (par1GCTileEntityTreasureChest.adjacentChestZNeg == null && par1GCTileEntityTreasureChest.adjacentChestXNeg == null) {
            ModelTreasureChest var11 = null;
            ModelTreasureChestLarge var14b = null;
            if (par1GCTileEntityTreasureChest.adjacentChestXPos == null && par1GCTileEntityTreasureChest.adjacentChestZPos == null) {
                var11 = this.chestModel;
                this.bindTexture(TileEntityTreasureChestRenderer.treasureChestTexture);
            }
            else {
                var14b = this.largeChestModel;
                this.bindTexture(TileEntityTreasureChestRenderer.treasureLargeChestTexture);
            }
            GL11.glPushMatrix();
            GL11.glEnable(32826);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glTranslatef((float)par2, (float)par4 + 1.0f, (float)par6 + 1.0f);
            GL11.glScalef(1.0f, -1.0f, -1.0f);
            GL11.glTranslatef(0.5f, 0.5f, 0.5f);
            short var12 = 0;
            if (var9 == 2) {
                var12 = 180;
            }
            if (var9 == 3) {
                var12 = 0;
            }
            if (var9 == 4) {
                var12 = 90;
            }
            if (var9 == 5) {
                var12 = -90;
            }
            if (var9 == 2 && par1GCTileEntityTreasureChest.adjacentChestXPos != null) {
                GL11.glTranslatef(1.0f, 0.0f, 0.0f);
            }
            if (var9 == 5 && par1GCTileEntityTreasureChest.adjacentChestZPos != null) {
                GL11.glTranslatef(0.0f, 0.0f, -1.0f);
            }
            GL11.glRotatef((float)var12, 0.0f, 1.0f, 0.0f);
            GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
            float var13 = par1GCTileEntityTreasureChest.prevLidAngle + (par1GCTileEntityTreasureChest.lidAngle - par1GCTileEntityTreasureChest.prevLidAngle) * par8;
            if (par1GCTileEntityTreasureChest.adjacentChestZNeg != null) {
                final float var14 = par1GCTileEntityTreasureChest.adjacentChestZNeg.prevLidAngle + (par1GCTileEntityTreasureChest.adjacentChestZNeg.lidAngle - par1GCTileEntityTreasureChest.adjacentChestZNeg.prevLidAngle) * par8;
                if (var14 > var13) {
                    var13 = var14;
                }
            }
            if (par1GCTileEntityTreasureChest.adjacentChestXNeg != null) {
                final float var14 = par1GCTileEntityTreasureChest.adjacentChestXNeg.prevLidAngle + (par1GCTileEntityTreasureChest.adjacentChestXNeg.lidAngle - par1GCTileEntityTreasureChest.adjacentChestXNeg.prevLidAngle) * par8;
                if (var14 > var13) {
                    var13 = var14;
                }
            }
            var13 = 1.0f - var13;
            var13 = 1.0f - var13 * var13 * var13;
            if (var11 != null) {
                var11.chestLid.rotateAngleX = -(var13 * 3.1415927f / 4.0f);
                var11.renderAll(!par1GCTileEntityTreasureChest.locked);
            }
            if (var14b != null) {
                var14b.chestLid.rotateAngleX = -(var13 * 3.1415927f / 4.0f);
                var14b.renderAll(!par1GCTileEntityTreasureChest.locked);
            }
            GL11.glDisable(32826);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
    
    public void renderTileEntityAt(final TileEntity par1TileEntity, final double par2, final double par4, final double par6, final float par8) {
        this.renderGCTileEntityTreasureChestAt((TileEntityTreasureChestMars)par1TileEntity, par2, par4, par6, par8);
    }
    
    static {
        treasureChestTexture = new ResourceLocation("galacticraftmars", "textures/model/treasure.png");
        treasureLargeChestTexture = new ResourceLocation("galacticraftmars", "textures/model/treasurelarge.png");
    }
}
