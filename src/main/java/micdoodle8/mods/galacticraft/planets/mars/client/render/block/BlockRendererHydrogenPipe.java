package micdoodle8.mods.galacticraft.planets.mars.client.render.block;

import cpw.mods.fml.client.registry.*;
import net.minecraft.world.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import java.util.*;
import net.minecraft.tileentity.*;
import net.minecraft.client.renderer.*;
import org.lwjgl.opengl.*;

public class BlockRendererHydrogenPipe implements ISimpleBlockRenderingHandler
{
    final int renderID;
    
    public BlockRendererHydrogenPipe(final int var1) {
        this.renderID = var1;
    }
    
    public void renderPipe(final RenderBlocks renderblocks, final IBlockAccess iblockaccess, final Block block, final int x, final int y, final int z) {
        final TileEntity tileEntity = iblockaccess.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityHydrogenPipe) {
            final float minX = 0.4f;
            final float minY = 0.4f;
            final float minZ = 0.4f;
            final float maxX = 0.6f;
            final float maxY = 0.6f;
            final float maxZ = 0.6f;
            final TileEntity[] adjacentHydrogenConnections;
            final TileEntity[] connections = adjacentHydrogenConnections = TileEntityHydrogenPipe.getAdjacentHydrogenConnections(tileEntity);
            for (final TileEntity connection : adjacentHydrogenConnections) {
                if (connection != null) {
                    final int side = Arrays.asList(connections).indexOf(connection);
                    switch (side) {
                        case 0: {
                            renderblocks.setRenderBounds(0.4000000059604645, 0.0, 0.4000000059604645, 0.6000000238418579, 0.4000000059604645, 0.6000000238418579);
                            renderblocks.renderStandardBlock(block, x, y, z);
                            break;
                        }
                        case 1: {
                            renderblocks.setRenderBounds(0.4000000059604645, 0.6000000238418579, 0.4000000059604645, 0.6000000238418579, 1.0, 0.6000000238418579);
                            renderblocks.renderStandardBlock(block, x, y, z);
                            break;
                        }
                        case 2: {
                            renderblocks.setRenderBounds(0.4000000059604645, 0.4000000059604645, 0.0, 0.6000000238418579, 0.6000000238418579, 0.4000000059604645);
                            renderblocks.renderStandardBlock(block, x, y, z);
                            break;
                        }
                        case 3: {
                            renderblocks.setRenderBounds(0.4000000059604645, 0.4000000059604645, 0.6000000238418579, 0.6000000238418579, 0.6000000238418579, 1.0);
                            renderblocks.renderStandardBlock(block, x, y, z);
                            break;
                        }
                        case 4: {
                            renderblocks.setRenderBounds(0.0, 0.4000000059604645, 0.4000000059604645, 0.4000000059604645, 0.6000000238418579, 0.6000000238418579);
                            renderblocks.renderStandardBlock(block, x, y, z);
                            break;
                        }
                        case 5: {
                            renderblocks.setRenderBounds(0.6000000238418579, 0.4000000059604645, 0.4000000059604645, 1.0, 0.6000000238418579, 0.6000000238418579);
                            renderblocks.renderStandardBlock(block, x, y, z);
                            break;
                        }
                    }
                }
            }
            renderblocks.setRenderBounds(0.4000000059604645, 0.4000000059604645, 0.4000000059604645, 0.6000000238418579, 0.6000000238418579, 0.6000000238418579);
            renderblocks.renderStandardBlock(block, x, y, z);
        }
    }
    
    public void renderInventoryBlock(final Block block, final int metadata, final int modelID, final RenderBlocks renderer) {
        final float minSize = 0.4f;
        final float maxSize = 0.6f;
        final Tessellator var3 = Tessellator.instance;
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        renderer.setRenderBounds(0.4000000059604645, 0.4000000059604645, 0.0, 0.6000000238418579, 0.6000000238418579, 1.0);
        var3.startDrawingQuads();
        var3.setNormal(0.0f, -1.0f, 0.0f);
        renderer.renderFaceYNeg(block, 0.0, 0.0, 0.0, block.getIcon(0, metadata));
        var3.draw();
        var3.startDrawingQuads();
        var3.setNormal(0.0f, 1.0f, 0.0f);
        renderer.renderFaceYPos(block, 0.0, 0.0, 0.0, block.getIcon(1, metadata));
        var3.draw();
        var3.startDrawingQuads();
        var3.setNormal(0.0f, 0.0f, -1.0f);
        renderer.renderFaceXPos(block, 0.0, 0.0, 0.0, block.getIcon(2, metadata));
        var3.draw();
        var3.startDrawingQuads();
        var3.setNormal(0.0f, 0.0f, 1.0f);
        renderer.renderFaceXNeg(block, 0.0, 0.0, 0.0, block.getIcon(3, metadata));
        var3.draw();
        var3.startDrawingQuads();
        var3.setNormal(-1.0f, 0.0f, 0.0f);
        renderer.renderFaceZNeg(block, 0.0, 0.0, 0.0, block.getIcon(4, metadata));
        var3.draw();
        var3.startDrawingQuads();
        var3.setNormal(1.0f, 0.0f, 0.0f);
        renderer.renderFaceZPos(block, 0.0, 0.0, 0.0, block.getIcon(5, metadata));
        var3.draw();
    }
    
    public boolean renderWorldBlock(final IBlockAccess world, final int x, final int y, final int z, final Block block, final int modelId, final RenderBlocks renderer) {
        this.renderPipe(renderer, world, block, x, y, z);
        return true;
    }
    
    public boolean shouldRender3DInInventory(final int modelId) {
        return true;
    }
    
    public int getRenderId() {
        return this.renderID;
    }
}
