package micdoodle8.mods.galacticraft.planets.mars.client.render.entity;

import net.minecraft.client.renderer.entity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.renderer.*;
import micdoodle8.mods.galacticraft.planets.mars.entities.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.init.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;

@SideOnly(Side.CLIENT)
public class RenderProjectileTNT extends Render
{
    private final RenderBlocks renderBlocks;
    
    public RenderProjectileTNT() {
        this.renderBlocks = new RenderBlocks();
        this.shadowSize = 0.5f;
    }
    
    public void renderProjectileTNT(final EntityProjectileTNT tnt, final double par2, final double par4, final double par6, final float par8, final float par9) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par2, (float)par4 + 0.5f, (float)par6);
        this.bindTexture(TextureMap.locationBlocksTexture);
        final Block var10 = Blocks.tnt;
        GL11.glDisable(2896);
        if (var10 != null) {
            this.renderBlocks.setRenderBoundsFromBlock(var10);
            this.renderBlocks.renderBlockSandFalling(var10, tnt.worldObj, MathHelper.floor_double(tnt.posX), MathHelper.floor_double(tnt.posY), MathHelper.floor_double(tnt.posZ), 0);
        }
        GL11.glEnable(2896);
        GL11.glPopMatrix();
    }
    
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
        this.renderProjectileTNT((EntityProjectileTNT)par1Entity, par2, par4, par6, par8, par9);
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        return null;
    }
}
