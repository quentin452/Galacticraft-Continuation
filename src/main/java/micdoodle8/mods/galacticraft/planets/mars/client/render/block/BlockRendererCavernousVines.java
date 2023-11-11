package micdoodle8.mods.galacticraft.planets.mars.client.render.block;

import cpw.mods.fml.client.registry.*;
import net.minecraft.world.*;
import net.minecraft.block.*;
import net.minecraft.client.renderer.*;

public class BlockRendererCavernousVines implements ISimpleBlockRenderingHandler
{
    final int renderID;
    
    public BlockRendererCavernousVines(final int var1) {
        this.renderID = var1;
    }
    
    public boolean renderWorldBlock(final IBlockAccess var1, final int var2, final int var3, final int var4, final Block var5, final int var6, final RenderBlocks var7) {
        this.renderBlockMeteor(var7, var5, var1, var2, var3, var4);
        return true;
    }
    
    public boolean shouldRender3DInInventory(final int modelId) {
        return false;
    }
    
    public int getRenderId() {
        return this.renderID;
    }
    
    public void renderInventoryBlock(final Block block, final int metadata, final int modelID, final RenderBlocks renderer) {
    }
    
    public void renderBlockMeteor(final RenderBlocks renderBlocks, final Block par1Block, final IBlockAccess var1, final int par2, final int par3, final int par4) {
        final Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(var1, par2, par3, par4));
        final float f = 1.0f;
        final int l = par1Block.colorMultiplier(var1, par2, par3, par4);
        float f2 = (l >> 16 & 0xFF) / 255.0f;
        float f3 = (l >> 8 & 0xFF) / 255.0f;
        float f4 = (l & 0xFF) / 255.0f;
        if (EntityRenderer.anaglyphEnable) {
            final float f5 = (f2 * 30.0f + f3 * 59.0f + f4 * 11.0f) / 100.0f;
            final float f6 = (f2 * 30.0f + f3 * 70.0f) / 100.0f;
            final float f7 = (f2 * 30.0f + f4 * 70.0f) / 100.0f;
            f2 = f5;
            f3 = f6;
            f4 = f7;
        }
        tessellator.setColorOpaque_F(f * f2, f * f3, f * f4);
        renderBlocks.drawCrossedSquares(par1Block.getIcon(0, var1.getBlockMetadata(par2, par3, par4)), (double)par2, (double)par3, (double)par4, 1.0f);
    }
}
