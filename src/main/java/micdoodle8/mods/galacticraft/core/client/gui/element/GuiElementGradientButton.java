package micdoodle8.mods.galacticraft.core.client.gui.element;

import net.minecraft.client.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.client.gui.*;

public class GuiElementGradientButton extends GuiButton
{
    public GuiElementGradientButton(final int id, final int x, final int y, final int width, final int height, final String buttonText) {
        super(id, x, y, width, height, buttonText);
    }
    
    public void drawButton(final Minecraft p_146112_1_, final int p_146112_2_, final int p_146112_3_) {
        if (this.visible) {
            final FontRenderer fontrenderer = p_146112_1_.fontRenderer;
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.field_146123_n = (p_146112_2_ >= this.xPosition && p_146112_3_ >= this.yPosition && p_146112_2_ < this.xPosition + this.width && p_146112_3_ < this.yPosition + this.height);
            final int k = this.getHoverState(this.field_146123_n);
            GL11.glEnable(3042);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(770, 771);
            int color = ColorUtil.to32BitColor(150, 10, 10, 10);
            if (!this.enabled) {
                if (this.field_146123_n) {
                    color = ColorUtil.to32BitColor(150, 30, 30, 30);
                }
                else {
                    color = ColorUtil.to32BitColor(150, 32, 32, 32);
                }
            }
            else if (this.field_146123_n) {
                color = ColorUtil.to32BitColor(150, 30, 30, 30);
            }
            this.drawGradientRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, color, color);
            this.mouseDragged(p_146112_1_, p_146112_2_, p_146112_3_);
            int l = 14737632;
            if (this.packedFGColour != 0) {
                l = this.packedFGColour;
            }
            else if (!this.enabled) {
                l = 10526880;
            }
            else if (this.field_146123_n) {
                l = 16777120;
            }
            this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
        }
    }
}
