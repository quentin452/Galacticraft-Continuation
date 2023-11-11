package micdoodle8.mods.galacticraft.core.client.render.block;

import cpw.mods.fml.client.registry.*;
import net.minecraft.world.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.api.tile.*;
import net.minecraft.init.*;
import net.minecraft.client.renderer.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.util.*;

public class BlockRendererLandingPad implements ISimpleBlockRenderingHandler
{
    final int renderID;
    
    public BlockRendererLandingPad(final int var1) {
        this.renderID = var1;
    }
    
    public boolean renderWorldBlock(final IBlockAccess var1, final int var2, final int var3, final int var4, final Block var5, final int var6, final RenderBlocks var7) {
        this.renderBlockLandingPad(var7, var5, var1, var2, var3, var4);
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
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        var0.setRenderBounds(0.15000000596046448, 0.15000000596046448, 0.15000000596046448, 0.8500000238418579, 0.8500000238418579, 0.8500000238418579);
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
    
    public void renderBlockLandingPad(final RenderBlocks renderBlocks, final Block par1Block, final IBlockAccess var1, final int par2, final int par3, final int par4) {
        renderBlocks.setRenderBounds(-1.0, 0.0, -1.0, 2.0, 0.20000000298023224, 2.0);
        renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
        if (var1.getBlockMetadata(par2, par3, par4) == 0) {
            renderBlocks.setRenderBounds(-0.5, 0.20000000298023224, -0.5, 1.5, 0.30000001192092896, 1.5);
            renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
            renderBlocks.setRenderBounds(0.0, 0.30000001192092896, 0.0, 1.0, 0.4000000059604645, 1.0);
            renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
        }
        final IFuelDock landingPad = (IFuelDock)var1.getTileEntity(par2, par3, par4);
        if (landingPad != null) {
            if (landingPad.isBlockAttachable(var1, par2 + 2, par3, par4 - 1)) {
                renderBlocks.setRenderBounds(1.5, 0.20000000298023224, -0.8999999761581421, 2.0, 0.9010000228881836, -0.10000000149011612);
                renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
            }
            if (landingPad.isBlockAttachable(var1, par2 + 2, par3, par4)) {
                renderBlocks.setRenderBounds(1.5, 0.20000000298023224, 0.10000000149011612, 2.0, 0.8999999761581421, 0.8999999761581421);
                renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
            }
            if (landingPad.isBlockAttachable(var1, par2 + 2, par3, par4 + 1)) {
                renderBlocks.setRenderBounds(1.5, 0.20000000298023224, 1.100000023841858, 2.0, 0.8999999761581421, 1.899999976158142);
                renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
            }
            if (landingPad.isBlockAttachable(var1, par2 + 1, par3, par4 + 2)) {
                renderBlocks.setRenderBounds(1.100000023841858, 0.20000000298023224, 1.5, 1.899999976158142, 0.9010000228881836, 2.0);
                renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
            }
            if (landingPad.isBlockAttachable(var1, par2, par3, par4 + 2)) {
                renderBlocks.setRenderBounds(0.10000000149011612, 0.20000000298023224, 1.5, 0.8999999761581421, 0.9010000228881836, 2.0);
                renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
            }
            if (landingPad.isBlockAttachable(var1, par2 - 1, par3, par4 + 2)) {
                renderBlocks.setRenderBounds(-0.8999999761581421, 0.20000000298023224, 1.5, -0.10000000149011612, 0.8999999761581421, 2.0);
                renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
            }
            if (landingPad.isBlockAttachable(var1, par2 - 2, par3, par4 + 1)) {
                renderBlocks.setRenderBounds(-1.0, 0.20000000298023224, 1.100000023841858, -0.5, 0.9010000228881836, 1.899999976158142);
                renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
            }
            if (landingPad.isBlockAttachable(var1, par2 - 2, par3, par4)) {
                renderBlocks.setRenderBounds(-1.0, 0.20000000298023224, 0.10000000149011612, -0.5, 0.8999999761581421, 0.8999999761581421);
                renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
            }
            if (landingPad.isBlockAttachable(var1, par2 - 2, par3, par4 - 1)) {
                renderBlocks.setRenderBounds(-1.0, 0.20000000298023224, -0.8999999761581421, -0.5, 0.8999999761581421, -0.10000000149011612);
                renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
            }
            if (landingPad.isBlockAttachable(var1, par2 + 1, par3, par4 - 2)) {
                renderBlocks.setRenderBounds(1.100000023841858, 0.20000000298023224, -1.0, 1.899999976158142, 0.8999999761581421, -0.5);
                renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
            }
            if (landingPad.isBlockAttachable(var1, par2, par3, par4 - 2)) {
                renderBlocks.setRenderBounds(0.10000000149011612, 0.20000000298023224, -1.0, 0.8999999761581421, 0.8999999761581421, -0.5);
                renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
            }
            if (landingPad.isBlockAttachable(var1, par2 - 1, par3, par4 - 2)) {
                renderBlocks.setRenderBounds(-0.8999999761581421, 0.20000000298023224, -1.0, -0.10000000149011612, 0.9010000228881836, -0.5);
                renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
            }
        }
        if (var1.getBlockMetadata(par2, par3, par4) == 2) {
            final Tessellator tessellator = Tessellator.instance;
            tessellator.setBrightness(Blocks.hopper.getMixedBrightnessForBlock(var1, par2, par3, par4));
            final float f1 = 1.0f;
            final int j1 = Blocks.hopper.colorMultiplier(var1, par2, par3, par4);
            float f2 = (j1 >> 16 & 0xFF) / 255.0f;
            float f3 = (j1 >> 8 & 0xFF) / 255.0f;
            float f4 = (j1 & 0xFF) / 255.0f;
            if (EntityRenderer.anaglyphEnable) {
                final float f5 = (f2 * 30.0f + f3 * 59.0f + f4 * 11.0f) / 100.0f;
                final float f6 = (f2 * 30.0f + f3 * 70.0f) / 100.0f;
                final float f7 = (f2 * 30.0f + f4 * 70.0f) / 100.0f;
                f2 = f5;
                f3 = f6;
                f4 = f7;
            }
            tessellator.setColorOpaque_F(f1 * f2, f1 * f3, f1 * f4);
            renderBlocks.clearOverrideBlockTexture();
            final IIcon icon = BlockHopper.getHopperIcon("hopper");
            BlockHopper.getHopperIcon("hopper_inside");
            f2 = 0.125f;
            final double d0 = 0.625;
            renderBlocks.setOverrideBlockTexture(icon);
            renderBlocks.setRenderBounds(0.0, d0, 0.0, 1.0, 0.9, 1.0);
            renderBlocks.renderStandardBlock((Block)Blocks.hopper, par2, par3, par4);
            renderBlocks.setOverrideBlockTexture(((BlockLandingPadFull)par1Block).getIcon(0, 0));
            renderBlocks.setRenderBounds(-0.1, 0.0, -0.1, 0.0, 1.0, 0.0);
            renderBlocks.renderStandardBlock((Block)Blocks.hopper, par2, par3, par4);
            renderBlocks.setRenderBounds(-0.1, 0.0, 1.0, 0.0, 1.0, 1.1);
            renderBlocks.renderStandardBlock((Block)Blocks.hopper, par2, par3, par4);
            renderBlocks.setRenderBounds(1.0, 0.0, -0.1, 1.1, 1.0, 0.0);
            renderBlocks.renderStandardBlock((Block)Blocks.hopper, par2, par3, par4);
            renderBlocks.setRenderBounds(1.0, 0.0, 1.0, 1.1, 1.0, 1.1);
            renderBlocks.renderStandardBlock((Block)Blocks.hopper, par2, par3, par4);
            renderBlocks.setRenderBounds(0.0, 0.9, 0.0, 1.0, 1.0, 1.0);
            renderBlocks.renderStandardBlock(par1Block, par2, par3, par4);
            renderBlocks.setOverrideBlockTexture(icon);
            final double d2 = 0.1;
            final double d3 = 0.1;
            renderBlocks.setRenderBounds(d2, d3, d2, 1.0 - d2, d0 - 0.002, 1.0 - d2);
            renderBlocks.renderStandardBlock((Block)Blocks.hopper, par2, par3, par4);
        }
        renderBlocks.clearOverrideBlockTexture();
        par1Block.setBlockBoundsForItemRender();
        renderBlocks.uvRotateTop = 0;
    }
}
