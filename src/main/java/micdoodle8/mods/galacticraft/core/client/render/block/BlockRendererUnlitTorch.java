package micdoodle8.mods.galacticraft.core.client.render.block;

import cpw.mods.fml.client.registry.*;
import net.minecraft.world.*;
import net.minecraft.block.*;
import net.minecraft.client.renderer.*;
import org.lwjgl.opengl.*;
import net.minecraft.util.*;

public class BlockRendererUnlitTorch implements ISimpleBlockRenderingHandler
{
    final int renderID;
    
    public BlockRendererUnlitTorch(final int var1) {
        this.renderID = var1;
    }
    
    public boolean renderWorldBlock(final IBlockAccess var1, final int var2, final int var3, final int var4, final Block var5, final int var6, final RenderBlocks var7) {
        renderGCUnlitTorch(var7, var5, var1, var2, var3, var4);
        return true;
    }
    
    public boolean shouldRender3DInInventory(final int modelId) {
        return false;
    }
    
    public int getRenderId() {
        return this.renderID;
    }
    
    public void renderInventoryBlock(final Block block, final int metadata, final int modelID, final RenderBlocks renderer) {
        renderTorchAtAngle(renderer, block, 0.0, 0.0, 0.0, 0.0, 0.0);
    }
    
    public static void renderInvNormalBlock(final RenderBlocks var0, final Block var1, final int var2) {
        final Tessellator var3 = Tessellator.instance;
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
    
    public static void renderGCUnlitTorch(final RenderBlocks renderBlocks, final Block par1Block, final IBlockAccess var1, final int par2, final int par3, final int par4) {
        final int var2 = var1.getBlockMetadata(par2, par3, par4);
        final Tessellator var3 = Tessellator.instance;
        var3.setBrightness(par1Block.getMixedBrightnessForBlock(var1, par2, par3, par4));
        var3.setColorOpaque_F(1.0f, 1.0f, 1.0f);
        final double var4 = 0.4000000059604645;
        final double var5 = 0.09999999403953552;
        final double var6 = 0.20000000298023224;
        if (var2 == 1) {
            renderTorchAtAngle(renderBlocks, par1Block, par2 - 0.09999999403953552, par3 + 0.20000000298023224, par4, -0.4000000059604645, 0.0);
        }
        else if (var2 == 2) {
            renderTorchAtAngle(renderBlocks, par1Block, par2 + 0.09999999403953552, par3 + 0.20000000298023224, par4, 0.4000000059604645, 0.0);
        }
        else if (var2 == 3) {
            renderTorchAtAngle(renderBlocks, par1Block, par2, par3 + 0.20000000298023224, par4 - 0.09999999403953552, 0.0, -0.4000000059604645);
        }
        else if (var2 == 4) {
            renderTorchAtAngle(renderBlocks, par1Block, par2, par3 + 0.20000000298023224, par4 + 0.09999999403953552, 0.0, 0.4000000059604645);
        }
        else {
            renderTorchAtAngle(renderBlocks, par1Block, par2, par3, par4, 0.0, 0.0);
        }
    }
    
    public static void renderTorchAtAngle(final RenderBlocks renderBlocks, final Block par1Block, double par2, final double par4, double par6, final double par8, final double par10) {
        final Tessellator tessellator = Tessellator.instance;
        final IIcon icon = renderBlocks.getBlockIconFromSideAndMetadata(par1Block, 0, 0);
        final double d5 = icon.getMinU();
        final double d6 = icon.getMinV();
        final double d7 = icon.getMaxU();
        final double d8 = icon.getMaxV();
        final double d9 = icon.getInterpolatedU(7.0);
        final double d10 = icon.getInterpolatedV(6.0);
        final double d11 = icon.getInterpolatedU(9.0);
        final double d12 = icon.getInterpolatedV(8.0);
        final double d13 = icon.getInterpolatedU(7.0);
        final double d14 = icon.getInterpolatedV(13.0);
        final double d15 = icon.getInterpolatedU(9.0);
        final double d16 = icon.getInterpolatedV(15.0);
        par2 += 0.5;
        par6 += 0.5;
        final double d17 = par2 - 0.5;
        final double d18 = par2 + 0.5;
        final double d19 = par6 - 0.5;
        final double d20 = par6 + 0.5;
        final double d21 = 0.0625;
        final double d22 = 0.625;
        tessellator.addVertexWithUV(par2 + par8 * 0.375 - 0.0625, par4 + 0.625, par6 + par10 * 0.375 - 0.0625, d9, d10);
        tessellator.addVertexWithUV(par2 + par8 * 0.375 - 0.0625, par4 + 0.625, par6 + par10 * 0.375 + 0.0625, d9, d12);
        tessellator.addVertexWithUV(par2 + par8 * 0.375 + 0.0625, par4 + 0.625, par6 + par10 * 0.375 + 0.0625, d11, d12);
        tessellator.addVertexWithUV(par2 + par8 * 0.375 + 0.0625, par4 + 0.625, par6 + par10 * 0.375 - 0.0625, d11, d10);
        tessellator.addVertexWithUV(par2 + 0.0625 + par8, par4, par6 - 0.0625 + par10, d15, d14);
        tessellator.addVertexWithUV(par2 + 0.0625 + par8, par4, par6 + 0.0625 + par10, d15, d16);
        tessellator.addVertexWithUV(par2 - 0.0625 + par8, par4, par6 + 0.0625 + par10, d13, d16);
        tessellator.addVertexWithUV(par2 - 0.0625 + par8, par4, par6 - 0.0625 + par10, d13, d14);
        tessellator.addVertexWithUV(par2 - 0.0625, par4 + 1.0, d19, d5, d6);
        tessellator.addVertexWithUV(par2 - 0.0625 + par8, par4 + 0.0, d19 + par10, d5, d8);
        tessellator.addVertexWithUV(par2 - 0.0625 + par8, par4 + 0.0, d20 + par10, d7, d8);
        tessellator.addVertexWithUV(par2 - 0.0625, par4 + 1.0, d20, d7, d6);
        tessellator.addVertexWithUV(par2 + 0.0625, par4 + 1.0, d20, d5, d6);
        tessellator.addVertexWithUV(par2 + par8 + 0.0625, par4 + 0.0, d20 + par10, d5, d8);
        tessellator.addVertexWithUV(par2 + par8 + 0.0625, par4 + 0.0, d19 + par10, d7, d8);
        tessellator.addVertexWithUV(par2 + 0.0625, par4 + 1.0, d19, d7, d6);
        tessellator.addVertexWithUV(d17, par4 + 1.0, par6 + 0.0625, d5, d6);
        tessellator.addVertexWithUV(d17 + par8, par4 + 0.0, par6 + 0.0625 + par10, d5, d8);
        tessellator.addVertexWithUV(d18 + par8, par4 + 0.0, par6 + 0.0625 + par10, d7, d8);
        tessellator.addVertexWithUV(d18, par4 + 1.0, par6 + 0.0625, d7, d6);
        tessellator.addVertexWithUV(d18, par4 + 1.0, par6 - 0.0625, d5, d6);
        tessellator.addVertexWithUV(d18 + par8, par4 + 0.0, par6 - 0.0625 + par10, d5, d8);
        tessellator.addVertexWithUV(d17 + par8, par4 + 0.0, par6 - 0.0625 + par10, d7, d8);
        tessellator.addVertexWithUV(d17, par4 + 1.0, par6 - 0.0625, d7, d6);
    }
}
