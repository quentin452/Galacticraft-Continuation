package micdoodle8.mods.galacticraft.planets.asteroids.client.render.block;

import cpw.mods.fml.client.registry.*;
import net.minecraft.world.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.client.renderer.*;
import micdoodle8.mods.galacticraft.core.util.*;
import java.util.*;
import net.minecraft.tileentity.*;

public class BlockRendererWalkway implements ISimpleBlockRenderingHandler
{
    final int renderID;
    
    public BlockRendererWalkway(final int var1) {
        this.renderID = var1;
    }
    
    public boolean renderWorldBlock(final IBlockAccess var1, final int var2, final int var3, final int var4, final Block var5, final int var6, final RenderBlocks var7) {
        this.renderWalkway(var7, var5, var1, var2, var3, var4);
        return true;
    }
    
    public boolean shouldRender3DInInventory(final int modelId) {
        return true;
    }
    
    public int getRenderId() {
        return this.renderID;
    }
    
    public static void renderInvNormalBlock(final RenderBlocks var0, final Block var1, final int var2) {
        var0.setOverrideBlockTexture(AsteroidBlocks.blockWalkway.getBlockTextureFromSide(0));
        GL11.glPushMatrix();
        GL11.glRotatef(20.0f, 0.0f, 1.0f, 0.0f);
        var0.setRenderBounds(0.0, 0.8999999761581421, 0.0, 0.10000000149011612, 1.0, 1.0);
        renderStandardBlock(var0, var1, var2);
        var0.setRenderBounds(0.8999999761581421, 0.8999999761581421, 0.0, 1.0, 1.0, 1.0);
        renderStandardBlock(var0, var1, var2);
        var0.setRenderBounds(0.0, 0.8999999761581421, 0.0, 1.0, 1.0, 0.10000000149011612);
        renderStandardBlock(var0, var1, var2);
        var0.setRenderBounds(0.0, 0.8999999761581421, 0.8999999761581421, 1.0, 1.0, 1.0);
        renderStandardBlock(var0, var1, var2);
        var0.setRenderBounds(0.4000000059604645, 0.8999999761581421, 0.4000000059604645, 0.6000000238418579, 1.0, 0.6000000238418579);
        renderStandardBlock(var0, var1, var2);
        var0.setRenderBounds(0.3499999940395355, 0.3499999940395355, 0.3499999940395355, 0.6499999761581421, 0.6499999761581421, 0.6499999761581421);
        renderStandardBlock(var0, var1, var2);
        var0.setRenderBounds(0.44999998807907104, 0.5, 0.44999998807907104, 0.550000011920929, 0.8999999761581421, 0.550000011920929);
        renderStandardBlock(var0, var1, var2);
        if (var1 == AsteroidBlocks.blockWalkwayOxygenPipe) {
            var0.setOverrideBlockTexture(GCBlocks.oxygenPipe.getBlockTextureFromSide(0));
            final float minX = 0.4f;
            final float minY = 0.4f;
            final float minZ = 0.4f;
            final float maxX = 0.6f;
            final float maxY = 0.6f;
            final float maxZ = 0.6f;
            var0.setRenderBounds(0.4000000059604645, 0.4000000059604645, 0.0, 0.6000000238418579, 0.6000000238418579, 0.4000000059604645);
            renderStandardBlock(var0, var1, var2);
            var0.setRenderBounds(0.4000000059604645, 0.4000000059604645, 0.6000000238418579, 0.6000000238418579, 0.6000000238418579, 1.0);
            renderStandardBlock(var0, var1, var2);
            var0.setRenderBounds(0.0, 0.4000000059604645, 0.4000000059604645, 0.4000000059604645, 0.6000000238418579, 0.6000000238418579);
            renderStandardBlock(var0, var1, var2);
            var0.setRenderBounds(0.6000000238418579, 0.4000000059604645, 0.4000000059604645, 1.0, 0.6000000238418579, 0.6000000238418579);
            renderStandardBlock(var0, var1, var2);
        }
        else if (var1 == AsteroidBlocks.blockWalkwayWire) {
            var0.setOverrideBlockTexture(GCBlocks.aluminumWire.getBlockTextureFromSide(0));
            GL11.glPushMatrix();
            GL11.glScalef(1.4f, 2.25f, 1.4f);
            GL11.glTranslatef(0.0f, 0.0f, 0.4f);
            var0.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 0.0);
            renderStandardBlock(var0, var1, var2);
            GL11.glTranslatef(0.0f, 0.0f, 0.2f);
            var0.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 0.0);
            renderStandardBlock(var0, var1, var2);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.4f, 2.25f, 1.4f);
            GL11.glTranslatef(0.4f, 0.0f, 0.0f);
            var0.setRenderBounds(0.0, 0.0, 0.0, 0.0, 1.0, 1.0);
            renderStandardBlock(var0, var1, var2);
            GL11.glTranslatef(0.2f, 0.0f, 0.0f);
            var0.setRenderBounds(0.0, 0.0, 0.0, 0.0, 1.0, 1.0);
            renderStandardBlock(var0, var1, var2);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.4f, 1.4f, 2.25f);
            GL11.glTranslatef(0.0f, 0.4f, 0.0f);
            var0.setRenderBounds(0.0, 0.0, 0.0, 1.0, 0.0, 1.0);
            renderStandardBlock(var0, var1, var2);
            GL11.glTranslatef(0.0f, 0.2f, 0.0f);
            var0.setRenderBounds(0.0, 0.0, 0.0, 1.0, 0.0, 1.0);
            renderStandardBlock(var0, var1, var2);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
            GL11.glScalef(1.4f, 1.4f, 2.25f);
            GL11.glTranslatef(0.0f, 0.4f, 0.0f);
            var0.setRenderBounds(0.0, 0.0, 0.0, 1.0, 0.0, 1.0);
            renderStandardBlock(var0, var1, var2);
            GL11.glTranslatef(0.0f, 0.2f, 0.0f);
            var0.setRenderBounds(0.0, 0.0, 0.0, 1.0, 0.0, 1.0);
            renderStandardBlock(var0, var1, var2);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(2.3f, 2.3f, 2.3f);
            GL11.glTranslatef(0.234f, 0.0f, 0.375f);
            var0.setRenderBounds(0.0, 0.0, 0.0, 0.0, 1.0, 0.1850000023841858);
            renderStandardBlock(var0, var1, var2);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(2.3f, 2.3f, 2.3f);
            GL11.glTranslatef(0.76600003f, 0.0f, 0.375f);
            var0.setRenderBounds(0.0, 0.0, 0.0, 0.0, 1.0, 0.1850000023841858);
            renderStandardBlock(var0, var1, var2);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(2.3f, 2.3f, 2.3f);
            GL11.glTranslatef(0.375f, 0.0f, 0.234f);
            var0.setRenderBounds(0.0, 0.0, 0.0, 0.1850000023841858, 1.0, 0.0);
            renderStandardBlock(var0, var1, var2);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(2.3f, 2.3f, 2.3f);
            GL11.glTranslatef(0.375f, 0.0f, 0.76600003f);
            var0.setRenderBounds(0.0, 0.0, 0.0, 0.1850000023841858, 1.0, 0.0);
            renderStandardBlock(var0, var1, var2);
            GL11.glPopMatrix();
        }
        else {
            var0.setRenderBounds(0.4000000059604645, 0.8999999761581421, 0.0, 0.6000000238418579, 1.0, 0.4000000059604645);
            renderStandardBlock(var0, var1, var2);
            var0.setRenderBounds(0.6000000238418579, 0.8999999761581421, 0.4000000059604645, 1.0, 1.0, 0.6000000238418579);
            renderStandardBlock(var0, var1, var2);
            var0.setRenderBounds(0.0, 0.8999999761581421, 0.4000000059604645, 0.4000000059604645, 1.0, 0.6000000238418579);
            renderStandardBlock(var0, var1, var2);
            var0.setRenderBounds(0.4000000059604645, 0.8999999761581421, 0.6000000238418579, 0.6000000238418579, 1.0, 1.0);
            renderStandardBlock(var0, var1, var2);
            var0.setRenderBounds(0.4000000059604645, 0.4000000059604645, 0.0, 0.6000000238418579, 0.6000000238418579, 0.4000000059604645);
            renderStandardBlock(var0, var1, var2);
            var0.setRenderBounds(0.6000000238418579, 0.4000000059604645, 0.4000000059604645, 1.0, 0.6000000238418579, 0.6000000238418579);
            renderStandardBlock(var0, var1, var2);
            var0.setRenderBounds(0.0, 0.4000000059604645, 0.4000000059604645, 0.4000000059604645, 0.6000000238418579, 0.6000000238418579);
            renderStandardBlock(var0, var1, var2);
            var0.setRenderBounds(0.4000000059604645, 0.4000000059604645, 0.6000000238418579, 0.6000000238418579, 0.6000000238418579, 1.0);
            renderStandardBlock(var0, var1, var2);
        }
        GL11.glPopMatrix();
        var0.clearOverrideBlockTexture();
    }
    
    private static void renderStandardBlock(final RenderBlocks var0, final Block var1, final int var2) {
        GL11.glPushMatrix();
        final Tessellator var3 = Tessellator.instance;
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        var3.startDrawingQuads();
        var3.setNormal(0.0f, -1.0f, 0.0f);
        var0.renderFaceYNeg(var1, 0.0, 0.0, 0.0, var1.getIcon(0, var2));
        var3.draw();
        var3.startDrawingQuads();
        var3.setNormal(0.0f, 1.0f, 0.0f);
        var0.renderFaceYPos(var1, 0.0, 0.0, 0.0, var1.getIcon(1, var2));
        var3.draw();
        var3.startDrawingQuads();
        var3.setNormal(0.0f, 0.0f, -1.0f);
        var0.renderFaceXPos(var1, 0.0, 0.0, 0.0, var1.getIcon(2, var2));
        var3.draw();
        var3.startDrawingQuads();
        var3.setNormal(0.0f, 0.0f, 1.0f);
        var0.renderFaceXNeg(var1, 0.0, 0.0, 0.0, var1.getIcon(3, var2));
        var3.draw();
        var3.startDrawingQuads();
        var3.setNormal(-1.0f, 0.0f, 0.0f);
        var0.renderFaceZNeg(var1, 0.0, 0.0, 0.0, var1.getIcon(4, var2));
        var3.draw();
        var3.startDrawingQuads();
        var3.setNormal(1.0f, 0.0f, 0.0f);
        var0.renderFaceZPos(var1, 0.0, 0.0, 0.0, var1.getIcon(5, var2));
        var3.draw();
        GL11.glPopMatrix();
    }
    
    public void renderInventoryBlock(final Block block, final int metadata, final int modelID, final RenderBlocks renderer) {
        renderInvNormalBlock(renderer, block, metadata);
    }
    
    public void renderWalkway(final RenderBlocks renderBlocks, final Block block, final IBlockAccess var1, final int x, final int y, final int z) {
        renderBlocks.setRenderBounds(0.0, 0.8999999761581421, 0.0, 0.10000000149011612, 1.0, 1.0);
        renderBlocks.renderStandardBlock(block, x, y, z);
        renderBlocks.setRenderBounds(0.8999999761581421, 0.8999999761581421, 0.0, 1.0, 1.0, 1.0);
        renderBlocks.renderStandardBlock(block, x, y, z);
        renderBlocks.setRenderBounds(0.0, 0.8999999761581421, 0.0, 1.0, 1.0, 0.10000000149011612);
        renderBlocks.renderStandardBlock(block, x, y, z);
        renderBlocks.setRenderBounds(0.0, 0.8999999761581421, 0.8999999761581421, 1.0, 1.0, 1.0);
        renderBlocks.renderStandardBlock(block, x, y, z);
        renderBlocks.setRenderBounds(0.39500001072883606, 0.8999999761581421, 0.39500001072883606, 0.6050000190734863, 0.9990000128746033, 0.6050000190734863);
        renderBlocks.renderStandardBlock(block, x, y, z);
        renderBlocks.setRenderBounds(0.3499999940395355, 0.3499999940395355, 0.3499999940395355, 0.6499999761581421, 0.6499999761581421, 0.6499999761581421);
        renderBlocks.renderStandardBlock(block, x, y, z);
        renderBlocks.setRenderBounds(0.44999998807907104, 0.5, 0.44999998807907104, 0.550000011920929, 0.8999999761581421, 0.550000011920929);
        renderBlocks.renderStandardBlock(block, x, y, z);
        final int meta = var1.getBlockMetadata(x, y, z);
        boolean connectedNorth = (meta & 0x1) != 0x0;
        boolean connectedEast = (meta & 0x2) != 0x0;
        boolean connectedSouth = (meta & 0x4) != 0x0;
        boolean connectedWest = (meta & 0x8) != 0x0;
        final boolean connectedNorth2 = connectedNorth;
        final boolean connectedEast2 = connectedEast;
        final boolean connectedSouth2 = connectedSouth;
        final boolean connectedWest2 = connectedWest;
        if (block == AsteroidBlocks.blockWalkwayOxygenPipe) {
            renderBlocks.setOverrideBlockTexture(GCBlocks.oxygenPipe.getBlockTextureFromSide(0));
            final TileEntity tileEntity = var1.getTileEntity(x, y, z);
            final float minX = 0.4f;
            final float minY = 0.4f;
            final float minZ = 0.4f;
            final float maxX = 0.6f;
            final float maxY = 0.6f;
            final float maxZ = 0.6f;
            if (tileEntity != null) {
                final TileEntity[] adjacentOxygenConnections;
                final TileEntity[] connections = adjacentOxygenConnections = OxygenUtil.getAdjacentOxygenConnections(tileEntity);
                for (final TileEntity connection : adjacentOxygenConnections) {
                    if (connection != null) {
                        final int side = Arrays.asList(connections).indexOf(connection);
                        switch (side) {
                            case 0: {
                                renderBlocks.setRenderBounds(0.4000000059604645, 0.0, 0.4000000059604645, 0.6000000238418579, 0.4000000059604645, 0.6000000238418579);
                                renderBlocks.renderStandardBlock(block, x, y, z);
                                break;
                            }
                            case 1: {
                                renderBlocks.setRenderBounds(0.4000000059604645, 0.6000000238418579, 0.4000000059604645, 0.6000000238418579, 1.0, 0.6000000238418579);
                                renderBlocks.renderStandardBlock(block, x, y, z);
                                break;
                            }
                            case 2: {
                                connectedNorth = false;
                                renderBlocks.setRenderBounds(0.4000000059604645, 0.4000000059604645, 0.0, 0.6000000238418579, 0.6000000238418579, 0.4000000059604645);
                                renderBlocks.renderStandardBlock(block, x, y, z);
                                break;
                            }
                            case 3: {
                                connectedSouth = false;
                                renderBlocks.setRenderBounds(0.4000000059604645, 0.4000000059604645, 0.6000000238418579, 0.6000000238418579, 0.6000000238418579, 1.0);
                                renderBlocks.renderStandardBlock(block, x, y, z);
                                break;
                            }
                            case 4: {
                                connectedWest = false;
                                renderBlocks.setRenderBounds(0.0, 0.4000000059604645, 0.4000000059604645, 0.4000000059604645, 0.6000000238418579, 0.6000000238418579);
                                renderBlocks.renderStandardBlock(block, x, y, z);
                                break;
                            }
                            case 5: {
                                connectedEast = false;
                                renderBlocks.setRenderBounds(0.6000000238418579, 0.4000000059604645, 0.4000000059604645, 1.0, 0.6000000238418579, 0.6000000238418579);
                                renderBlocks.renderStandardBlock(block, x, y, z);
                                break;
                            }
                        }
                    }
                }
                renderBlocks.setRenderBounds(0.4000000059604645, 0.4000000059604645, 0.4000000059604645, 0.6000000238418579, 0.6000000238418579, 0.6000000238418579);
                renderBlocks.renderStandardBlock(block, x, y, z);
            }
        }
        renderBlocks.setOverrideBlockTexture(block.getIcon(var1, x, y, z, 0));
        if (connectedNorth2) {
            renderBlocks.setRenderBounds(0.4000000059604645, 0.8999999761581421, 0.0, 0.6000000238418579, 1.0, 0.4000000059604645);
            renderBlocks.renderStandardBlock(block, x, y, z);
        }
        if (connectedEast2) {
            renderBlocks.setRenderBounds(0.6000000238418579, 0.8999999761581421, 0.4000000059604645, 1.0, 1.0, 0.6000000238418579);
            renderBlocks.renderStandardBlock(block, x, y, z);
        }
        if (connectedWest2) {
            renderBlocks.setRenderBounds(0.0, 0.8999999761581421, 0.4000000059604645, 0.4000000059604645, 1.0, 0.6000000238418579);
            renderBlocks.renderStandardBlock(block, x, y, z);
        }
        if (connectedSouth2) {
            renderBlocks.setRenderBounds(0.4000000059604645, 0.8999999761581421, 0.6000000238418579, 0.6000000238418579, 1.0, 1.0);
            renderBlocks.renderStandardBlock(block, x, y, z);
        }
        if (connectedNorth) {
            renderBlocks.setRenderBounds(0.4000000059604645, 0.4000000059604645, 0.0, 0.6000000238418579, 0.6000000238418579, 0.4000000059604645);
            renderBlocks.renderStandardBlock(block, x, y, z);
        }
        if (connectedEast) {
            renderBlocks.setRenderBounds(0.6000000238418579, 0.4000000059604645, 0.4000000059604645, 1.0, 0.6000000238418579, 0.6000000238418579);
            renderBlocks.renderStandardBlock(block, x, y, z);
        }
        if (connectedWest) {
            renderBlocks.setRenderBounds(0.0, 0.4000000059604645, 0.4000000059604645, 0.4000000059604645, 0.6000000238418579, 0.6000000238418579);
            renderBlocks.renderStandardBlock(block, x, y, z);
        }
        if (connectedSouth) {
            renderBlocks.setRenderBounds(0.4000000059604645, 0.4000000059604645, 0.6000000238418579, 0.6000000238418579, 0.6000000238418579, 1.0);
            renderBlocks.renderStandardBlock(block, x, y, z);
        }
        renderBlocks.clearOverrideBlockTexture();
        block.setBlockBoundsForItemRender();
    }
}
