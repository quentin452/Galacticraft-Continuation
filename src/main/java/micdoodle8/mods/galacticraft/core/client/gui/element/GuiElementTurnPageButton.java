package micdoodle8.mods.galacticraft.core.client.gui.element;

import net.minecraft.client.gui.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import net.minecraft.client.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.*;

@SideOnly(Side.CLIENT)
public class GuiElementTurnPageButton extends GuiButton
{
    private final boolean nextPage;
    private static final ResourceLocation background;
    
    public GuiElementTurnPageButton(final int par1, final int par2, final int par3, final boolean par4) {
        super(par1, par2, par3, 23, 13, "");
        this.nextPage = par4;
    }
    
    public void drawButton(final Minecraft par1Minecraft, final int par2, final int par3) {
        if (this.visible) {
            final boolean var4 = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            par1Minecraft.getTextureManager().bindTexture(GuiElementTurnPageButton.background);
            int var5 = 0;
            int var6 = 192;
            if (var4) {
                var5 += 23;
            }
            if (!this.nextPage) {
                var6 += 13;
            }
            this.drawTexturedModalRect(this.xPosition, this.yPosition, var5, var6, 23, 13);
        }
    }
    
    static {
        background = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/bookleft.png");
    }
}
