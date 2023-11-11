package micdoodle8.mods.galacticraft.core.client.render.block;

import cpw.mods.fml.client.registry.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.block.*;
import net.minecraft.client.renderer.*;
import org.lwjgl.opengl.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.tileentity.*;

public class BlockRendererTreasureChest implements ISimpleBlockRenderingHandler
{
    final int renderID;
    private final TileEntityTreasureChest chest;
    
    public BlockRendererTreasureChest(final int var1) {
        this.chest = new TileEntityTreasureChest();
        this.renderID = var1;
    }
    
    public void renderInventoryBlock(final Block var1, final int var2, final int var3, final RenderBlocks var4) {
        GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
        this.renderChest(var1, var2, (float)var3);
        GL11.glEnable(32826);
    }
    
    public boolean renderWorldBlock(final IBlockAccess var1, final int var2, final int var3, final int var4, final Block var5, final int var6, final RenderBlocks var7) {
        return false;
    }
    
    public boolean shouldRender3DInInventory(final int modelId) {
        return true;
    }
    
    public int getRenderId() {
        return this.renderID;
    }
    
    public void renderChest(final Block par1Block, final int par2, final float par3) {
        if (par1Block == GCBlocks.treasureChestTier1) {
            TileEntityRendererDispatcher.instance.renderTileEntityAt((TileEntity)this.chest, 0.0, 0.0, 0.0, 0.0f);
        }
    }
}
