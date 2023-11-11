package micdoodle8.mods.galacticraft.core.client.gui.element;

import net.minecraft.client.gui.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.renderer.entity.*;
import micdoodle8.mods.galacticraft.core.client.gui.container.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import micdoodle8.mods.galacticraft.core.util.*;
import cpw.mods.fml.client.*;
import java.util.*;

@SideOnly(Side.CLIENT)
public class GuiElementInfoRegion extends Gui
{
    protected int width;
    protected int height;
    public int xPosition;
    public int yPosition;
    public boolean enabled;
    public boolean drawRegion;
    public boolean withinRegion;
    public List<String> tooltipStrings;
    protected static RenderItem itemRenderer;
    public int parentWidth;
    public int parentHeight;
    public GuiContainerGC parentGui;
    
    public GuiElementInfoRegion(final int xPos, final int yPos, final int width, final int height, final List<String> tooltipStrings, final int parentWidth, final int parentHeight, final GuiContainerGC parentGui) {
        this.width = 200;
        this.height = 20;
        this.enabled = true;
        this.xPosition = xPos;
        this.yPosition = yPos;
        this.width = width;
        this.height = height;
        this.tooltipStrings = tooltipStrings;
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
        this.parentGui = parentGui;
    }
    
    protected int getHoverState(final boolean par1) {
        byte b0 = 1;
        if (!this.enabled) {
            b0 = 0;
        }
        else if (par1) {
            b0 = 2;
        }
        return b0;
    }
    
    public void drawRegion(final int par2, final int par3) {
        GL11.glDisable(32826);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(2896);
        GL11.glDisable(2929);
        this.withinRegion = (par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height);
        if (this.drawRegion) {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            final int k = this.getHoverState(this.withinRegion);
            Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, ColorUtil.to32BitColor(100 * k, 255, 0, 0));
        }
        if (this.tooltipStrings != null && !this.tooltipStrings.isEmpty() && this.withinRegion) {
            int k = 0;
            for (final String s : this.tooltipStrings) {
                final int l = FMLClientHandler.instance().getClient().fontRenderer.getStringWidth(s);
                if (l > k) {
                    k = l;
                }
            }
            int i1 = par2 + 12;
            int j1 = par3 - 12;
            int k2 = 8;
            if (this.tooltipStrings.size() > 1) {
                k2 += (this.tooltipStrings.size() - 1) * 10;
            }
            if (i1 + k > this.parentWidth) {
                i1 -= 28 + k;
            }
            if (this.parentGui.getTooltipOffset(par2, par3) > 0) {
                j1 -= k2 + 9;
            }
            this.zLevel = 300.0f;
            GuiElementInfoRegion.itemRenderer.zLevel = 300.0f;
            final int l2 = -267386864;
            this.drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l2, l2);
            this.drawGradientRect(i1 - 3, j1 + k2 + 3, i1 + k + 3, j1 + k2 + 4, l2, l2);
            this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k2 + 3, l2, l2);
            this.drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k2 + 3, l2, l2);
            this.drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k2 + 3, l2, l2);
            final int i2 = 1347420415;
            final int j2 = (i2 & 0xFEFEFE) >> 1 | (i2 & 0xFF000000);
            this.drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k2 + 3 - 1, i2, j2);
            this.drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k2 + 3 - 1, i2, j2);
            this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2);
            this.drawGradientRect(i1 - 3, j1 + k2 + 2, i1 + k + 3, j1 + k2 + 3, j2, j2);
            for (int k3 = 0; k3 < this.tooltipStrings.size(); ++k3) {
                final String s2 = this.tooltipStrings.get(k3);
                FMLClientHandler.instance().getClient().fontRenderer.drawStringWithShadow(s2, i1, j1, -1);
                j1 += 10;
            }
            this.zLevel = 0.0f;
            GuiElementInfoRegion.itemRenderer.zLevel = 0.0f;
        }
        GL11.glEnable(2896);
        GL11.glEnable(2929);
        RenderHelper.enableStandardItemLighting();
        GL11.glEnable(32826);
    }
    
    static {
        GuiElementInfoRegion.itemRenderer = new RenderItem();
    }
}
