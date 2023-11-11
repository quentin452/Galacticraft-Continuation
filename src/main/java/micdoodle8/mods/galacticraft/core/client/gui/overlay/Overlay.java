package micdoodle8.mods.galacticraft.core.client.gui.overlay;

import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraft.client.renderer.*;

public class Overlay
{
    protected static int getPlayerPositionY(final EntityPlayer player) {
        if (player.ridingEntity != null && player.ridingEntity instanceof EntityTier1Rocket) {
            return (int)Math.floor(((EntityTier1Rocket)player.ridingEntity).posY);
        }
        return (int)Math.floor(player.posY);
    }
    
    protected static void drawTexturedModalRect(final int par1, final int par2, final int par3, final int par4, final int par5, final int par6) {
        final float var7 = 0.00390625f;
        final float var8 = 0.00390625f;
        final Tessellator var9 = Tessellator.instance;
        var9.startDrawingQuads();
        var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + par6), 0.0, (double)((par3 + 0) * 0.00390625f), (double)((par4 + par6) * 0.00390625f));
        var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + par6), 0.0, (double)((par3 + par5) * 0.00390625f), (double)((par4 + par6) * 0.00390625f));
        var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + 0), 0.0, (double)((par3 + par5) * 0.00390625f), (double)((par4 + 0) * 0.00390625f));
        var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), 0.0, (double)((par3 + 0) * 0.00390625f), (double)((par4 + 0) * 0.00390625f));
        var9.draw();
    }
    
    protected static void drawCenteringRectangle(final double var1, final double var3, final double var5, double var7, double var9) {
        var7 *= 0.5;
        var9 *= 0.5;
        final Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertexWithUV(var1 - var7, var3 + var9, var5, 0.0, 1.0);
        t.addVertexWithUV(var1 + var7, var3 + var9, var5, 1.0, 1.0);
        t.addVertexWithUV(var1 + var7, var3 - var9, var5, 1.0, 0.0);
        t.addVertexWithUV(var1 - var7, var3 - var9, var5, 0.0, 0.0);
        t.draw();
    }
}
