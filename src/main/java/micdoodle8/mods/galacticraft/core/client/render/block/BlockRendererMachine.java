package micdoodle8.mods.galacticraft.core.client.render.block;

import cpw.mods.fml.client.registry.*;
import net.minecraft.world.*;
import net.minecraft.block.*;
import net.minecraft.client.renderer.*;
import org.lwjgl.opengl.*;

public class BlockRendererMachine implements ISimpleBlockRenderingHandler
{
    final int renderID;
    
    public BlockRendererMachine(final int var1) {
        this.renderID = var1;
    }
    
    public boolean renderWorldBlock(final IBlockAccess var1, final int x, final int y, final int z, final Block block, final int var6, final RenderBlocks renderBlocks) {
        renderBlocks.renderStandardBlock(block, x, y, z);
        return true;
    }
    
    public boolean shouldRender3DInInventory(final int modelId) {
        return true;
    }
    
    public int getRenderId() {
        return this.renderID;
    }
    
    public static void renderInvNormalBlock(final RenderBlocks var0, final Block var1, final int var2) {
        final Tessellator var3 = Tessellator.instance;
        GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        var0.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        var3.startDrawingQuads();
        var3.setNormal(0.0f, -0.8f, 0.0f);
        var0.renderFaceYNeg(var1, 0.0, 0.0, 0.0, var1.getIcon(0, var2));
        var3.draw();
        var3.startDrawingQuads();
        var3.setNormal(0.0f, 0.8f, 0.0f);
        var0.renderFaceYPos(var1, 0.0, 0.0, 0.0, var1.getIcon(1, var2));
        var3.draw();
        var3.startDrawingQuads();
        var3.setNormal(0.0f, 0.0f, -0.8f);
        var0.renderFaceXPos(var1, 0.0, 0.0, 0.0, var1.getIcon(2, var2));
        var3.draw();
        var3.startDrawingQuads();
        var3.setNormal(0.0f, 0.0f, 0.8f);
        var0.renderFaceXNeg(var1, 0.0, 0.0, 0.0, var1.getIcon(3, var2));
        var3.draw();
        var3.startDrawingQuads();
        var3.setNormal(-0.8f, 0.0f, 0.0f);
        var0.renderFaceZNeg(var1, 0.0, 0.0, 0.0, var1.getIcon(4, var2));
        var3.draw();
        var3.startDrawingQuads();
        var3.setNormal(0.8f, 0.0f, 0.0f);
        var0.renderFaceZPos(var1, 0.0, 0.0, 0.0, var1.getIcon(5, var2));
        var3.draw();
    }
    
    public void renderInventoryBlock(final Block block, final int metadata, final int modelID, final RenderBlocks renderer) {
        renderInvNormalBlock(renderer, block, metadata);
    }
}
