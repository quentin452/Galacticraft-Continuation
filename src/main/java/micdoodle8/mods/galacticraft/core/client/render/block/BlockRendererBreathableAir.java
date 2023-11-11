package micdoodle8.mods.galacticraft.core.client.render.block;

import cpw.mods.fml.client.registry.*;
import net.minecraft.world.*;
import net.minecraft.block.*;
import net.minecraft.client.renderer.*;
import org.lwjgl.opengl.*;

public class BlockRendererBreathableAir implements ISimpleBlockRenderingHandler
{
    final int renderID;
    
    public BlockRendererBreathableAir(final int var1) {
        this.renderID = var1;
    }
    
    public boolean renderWorldBlock(final IBlockAccess var1, final int var2, final int var3, final int var4, final Block var5, final int var6, final RenderBlocks var7) {
        renderBreathableAir(var7, var5, var1, var2, var3, var4);
        return true;
    }
    
    public boolean shouldRender3DInInventory(final int modelId) {
        return true;
    }
    
    public int getRenderId() {
        return this.renderID;
    }
    
    public void renderInventoryBlock(final Block block, final int metadata, final int modelID, final RenderBlocks renderer) {
        renderInvNormalBlock(renderer, block, metadata);
    }
    
    public static void renderInvNormalBlock(final RenderBlocks var0, final Block var1, final int var2) {
        final Tessellator var3 = Tessellator.instance;
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        var0.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
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
    }
    
    public static void renderBreathableAir(final RenderBlocks renderBlocks, final Block par1Block, final IBlockAccess var1, final int par2, final int par3, final int par4) {
        final Tessellator var2 = Tessellator.instance;
        final int var3 = par1Block.colorMultiplier(var1, par2, par3, par4);
        final float var4 = (var3 >> 16 & 0xFF) / 255.0f;
        final float var5 = (var3 >> 8 & 0xFF) / 255.0f;
        final float var6 = (var3 & 0xFF) / 255.0f;
        final boolean var7 = par1Block.shouldSideBeRendered(var1, par2, par3 + 1, par4, 1);
        final boolean var8 = par1Block.shouldSideBeRendered(var1, par2, par3 - 1, par4, 0);
        final boolean[] var9 = { par1Block.shouldSideBeRendered(var1, par2, par3, par4 - 1, 2), par1Block.shouldSideBeRendered(var1, par2, par3, par4 + 1, 3), par1Block.shouldSideBeRendered(var1, par2 - 1, par3, par4, 4), par1Block.shouldSideBeRendered(var1, par2 + 1, par3, par4, 5) };
        if (var7 || var8 || var9[0] || var9[1] || var9[2] || var9[3]) {
            final float var10 = 0.5f;
            final float var11 = 1.0f;
            final float var12 = 0.8f;
            final float var13 = 0.6f;
            var1.getBlockMetadata(par2, par3, par4);
            final double var14 = 1.0;
            final double var15 = 1.0;
            final double var16 = 1.0;
            final double var17 = 1.0;
            final double var18 = 0.0010000000474974513;
            if (renderBlocks.renderAllFaces || var8) {
                var2.setBrightness(par1Block.getMixedBrightnessForBlock(var1, par2, par3 - 1, par4));
                final float var19 = 1.0f;
                var2.setColorOpaque_F(0.5f, 0.5f, 0.5f);
                renderBlocks.renderFaceYNeg(par1Block, (double)par2, par3 + 0.0010000000474974513, (double)par4, par1Block.getBlockTextureFromSide(0));
            }
            for (int var20 = 0; var20 < 4; ++var20) {
                int var21 = par2;
                int var22 = par4;
                if (var20 == 0) {
                    var22 = par4 - 1;
                }
                if (var20 == 1) {
                    ++var22;
                }
                if (var20 == 2) {
                    var21 = par2 - 1;
                }
                if (var20 == 3) {
                    ++var21;
                }
                if (renderBlocks.renderAllFaces || var9[var20]) {
                    double var23;
                    double var24;
                    double var25;
                    double var26;
                    double var27;
                    double var28;
                    if (var20 == 0) {
                        var23 = 1.0;
                        var24 = 1.0;
                        var25 = par2;
                        var26 = par2 + 1;
                        var27 = par4 + 0.0010000000474974513;
                        var28 = par4 + 0.0010000000474974513;
                    }
                    else if (var20 == 1) {
                        var23 = 1.0;
                        var24 = 1.0;
                        var25 = par2 + 1;
                        var26 = par2;
                        var27 = par4 + 1 - 0.0010000000474974513;
                        var28 = par4 + 1 - 0.0010000000474974513;
                    }
                    else if (var20 == 2) {
                        var23 = 1.0;
                        var24 = 1.0;
                        var25 = par2 + 0.0010000000474974513;
                        var26 = par2 + 0.0010000000474974513;
                        var27 = par4 + 1;
                        var28 = par4;
                    }
                    else {
                        var23 = 1.0;
                        var24 = 1.0;
                        var25 = par2 + 1 - 0.0010000000474974513;
                        var26 = par2 + 1 - 0.0010000000474974513;
                        var27 = par4;
                        var28 = par4 + 1;
                    }
                    final double var29 = 0.0;
                    final double var30 = 0.0624609375;
                    final double var31 = (0.0 + (1.0 - var23) * 16.0) / 256.0;
                    final double var32 = (0.0 + (1.0 - var24) * 16.0) / 256.0;
                    final double var33 = 0.0624609375;
                    var2.setBrightness(par1Block.getMixedBrightnessForBlock(var1, var21, par3, var22));
                    float var34 = 1.0f;
                    if (var20 < 2) {
                        var34 *= 0.8f;
                    }
                    else {
                        var34 *= 0.6f;
                    }
                    var2.setColorOpaque_F(1.0f * var34 * var4, 1.0f * var34 * var5, 1.0f * var34 * var6);
                    var2.addVertexWithUV(var25, par3 + var23, var27, 0.0, var31);
                    var2.addVertexWithUV(var26, par3 + var24, var28, 0.0624609375, var32);
                    var2.addVertexWithUV(var26, (double)par3, var28, 0.0624609375, 0.0624609375);
                    var2.addVertexWithUV(var25, (double)par3, var27, 0.0, 0.0624609375);
                }
            }
        }
    }
}
