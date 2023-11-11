package micdoodle8.mods.galacticraft.planets.mars.client.render.block;

import cpw.mods.fml.client.registry.*;
import net.minecraft.world.*;
import net.minecraft.block.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;

public class BlockRendererEgg implements ISimpleBlockRenderingHandler
{
    final int renderID;
    
    public BlockRendererEgg(final int var1) {
        this.renderID = var1;
    }
    
    public boolean renderWorldBlock(final IBlockAccess var1, final int var2, final int var3, final int var4, final Block var5, final int var6, final RenderBlocks var7) {
        this.renderBlockMeteor(var7, var5, var1, var2, var3, var4);
        return true;
    }
    
    public boolean shouldRender3DInInventory(final int modelId) {
        return true;
    }
    
    public int getRenderId() {
        return this.renderID;
    }
    
    public static void renderInvNormalBlock(final RenderBlocks renderBlocks, final Block par1Block, final int var2) {
        renderBlocks.setRenderBounds(0.20000000298023224, 0.0, 0.20000000298023224, 0.800000011920929, 0.5, 0.800000011920929);
        renderStandardBlock(renderBlocks, par1Block, var2);
        renderBlocks.setRenderBounds(0.5199999809265137, 0.0, 0.4000000059604645, 0.6800000071525574, 0.6800000071525574, 0.6000000238418579);
        renderStandardBlock(renderBlocks, par1Block, var2);
        renderBlocks.setRenderBounds(0.25, 0.0, 0.25, 0.75, 0.6499999761581421, 0.75);
        renderStandardBlock(renderBlocks, par1Block, var2);
        renderBlocks.setRenderBounds(0.15000000596046448, 0.0, 0.25, 0.20000000298023224, 0.4000000059604645, 0.75);
        renderStandardBlock(renderBlocks, par1Block, var2);
        renderBlocks.setRenderBounds(0.800000011920929, 0.0, 0.25, 0.8500000238418579, 0.4000000059604645, 0.75);
        renderStandardBlock(renderBlocks, par1Block, var2);
        renderBlocks.setRenderBounds(0.25, 0.0, 0.10000000149011612, 0.75, 0.4000000059604645, 0.20000000298023224);
        renderStandardBlock(renderBlocks, par1Block, var2);
        renderBlocks.setRenderBounds(0.25, 0.0, 0.800000011920929, 0.75, 0.4000000059604645, 0.8999999761581421);
        renderStandardBlock(renderBlocks, par1Block, var2);
        renderBlocks.clearOverrideBlockTexture();
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
    
    public void renderBlockMeteor(final RenderBlocks renderBlocks, final Block par1Block, final IBlockAccess var1, final int par2, final int par3, final int par4) {
        var1.getBlockMetadata(par2, par3, par4);
        renderBlocks.setRenderBounds(0.20000000298023224, 0.0, 0.20000000298023224, 0.800000011920929, 0.5, 0.800000011920929);
        renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
        renderBlocks.setRenderBounds(0.5199999809265137, 0.0, 0.4000000059604645, 0.6800000071525574, 0.6800000071525574, 0.6000000238418579);
        renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
        renderBlocks.setRenderBounds(0.25, 0.0, 0.25, 0.75, 0.6499999761581421, 0.75);
        renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
        renderBlocks.setRenderBounds(0.15000000596046448, 0.0, 0.25, 0.20000000298023224, 0.4000000059604645, 0.75);
        renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
        renderBlocks.setRenderBounds(0.800000011920929, 0.0, 0.25, 0.8500000238418579, 0.4000000059604645, 0.75);
        renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
        renderBlocks.setRenderBounds(0.25, 0.0, 0.10000000149011612, 0.75, 0.4000000059604645, 0.20000000298023224);
        renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
        renderBlocks.setRenderBounds(0.25, 0.0, 0.800000011920929, 0.75, 0.4000000059604645, 0.8999999761581421);
        renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
        renderBlocks.clearOverrideBlockTexture();
        par1Block.setBlockBoundsForItemRender();
        renderBlocks.uvRotateTop = 0;
    }
}
