package micdoodle8.mods.galacticraft.core.client.render.block;

import cpw.mods.fml.client.registry.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.client.renderer.*;
import net.minecraft.world.*;
import net.minecraft.block.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.tileentity.*;

public class BlockRendererNasaWorkbench implements ISimpleBlockRenderingHandler
{
    final int renderID;
    private final TileEntityNasaWorkbench table;
    
    public BlockRendererNasaWorkbench(final int var1) {
        this.table = new TileEntityNasaWorkbench();
        this.renderID = var1;
    }
    
    public void renderNasaBench(final RenderBlocks renderBlocks, final IBlockAccess iblockaccess, final Block par1Block, final int par2, final int par3, final int par4) {
        renderBlocks.setRenderBounds(0.0, 0.0, 0.0, 1.0, 0.9200000166893005, 1.0);
        renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
        renderBlocks.clearOverrideBlockTexture();
    }
    
    public void renderInventoryBlock(final Block block, final int metadata, final int modelID, final RenderBlocks renderer) {
        GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(-0.5f, -1.1f, -0.1f);
        GL11.glScalef(0.7f, 0.6f, 0.7f);
        TileEntityRendererDispatcher.instance.renderTileEntityAt((TileEntity)this.table, 0.0, 0.0, 0.0, 0.0f);
        GL11.glEnable(32826);
    }
    
    public boolean renderWorldBlock(final IBlockAccess world, final int x, final int y, final int z, final Block block, final int modelId, final RenderBlocks renderer) {
        this.renderNasaBench(renderer, world, block, x, y, z);
        return true;
    }
    
    public boolean shouldRender3DInInventory(final int modelId) {
        return true;
    }
    
    public int getRenderId() {
        return this.renderID;
    }
}
