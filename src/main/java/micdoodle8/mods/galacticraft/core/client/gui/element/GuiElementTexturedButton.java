package micdoodle8.mods.galacticraft.core.client.gui.element;

import net.minecraft.util.*;
import net.minecraft.client.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.gui.*;

public class GuiElementTexturedButton extends GuiButton
{
    private final ResourceLocation texture;
    private final int bWidth;
    private final int bHeight;
    
    public GuiElementTexturedButton(final int par1, final int par2, final int par3, final int par4, final int par5, final ResourceLocation texture, final int width, final int height) {
        super(par1, par2, par3, par4, par5, "");
        this.texture = texture;
        this.bWidth = width;
        this.bHeight = height;
    }
    
    public void drawButton(final Minecraft par1Minecraft, final int par2, final int par3) {
        if (this.visible) {
            final FontRenderer var4 = par1Minecraft.fontRenderer;
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.getHoverState(this.field_146123_n = (par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height));
            par1Minecraft.renderEngine.bindTexture(this.texture);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 0, this.bWidth, this.bHeight);
            this.mouseDragged(par1Minecraft, par2, par3);
            int var5 = 14737632;
            if (!this.enabled) {
                var5 = -6250336;
            }
            else if (this.field_146123_n) {
                var5 = 16777120;
            }
            this.drawCenteredString(var4, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, var5);
        }
    }
}
