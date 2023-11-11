package micdoodle8.mods.galacticraft.core.client.gui.element;

import net.minecraft.client.gui.*;
import net.minecraft.util.*;
import net.minecraft.client.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.*;

public class GuiElementCheckbox extends GuiButton
{
    protected static final ResourceLocation texture;
    public Boolean isSelected;
    private ICheckBoxCallback parentGui;
    private int textColor;
    private int texWidth;
    private int texHeight;
    private int texX;
    private int texY;
    private boolean shiftOnHover;
    
    public GuiElementCheckbox(final int id, final ICheckBoxCallback parentGui, final int x, final int y, final String text) {
        this(id, parentGui, x, y, text, 4210752);
    }
    
    public GuiElementCheckbox(final int id, final ICheckBoxCallback parentGui, final int x, final int y, final String text, final int textColor) {
        this(id, parentGui, x, y, 13, 13, 20, 24, text, textColor);
    }
    
    private GuiElementCheckbox(final int id, final ICheckBoxCallback parentGui, final int x, final int y, final int width, final int height, final int texX, final int texY, final String text, final int textColor) {
        this(id, parentGui, x, y, width, height, width, height, texX, texY, text, textColor, true);
    }
    
    public GuiElementCheckbox(final int id, final ICheckBoxCallback parentGui, final int x, final int y, final int width, final int height, final int texWidth, final int texHeight, final int texX, final int texY, final String text, final int textColor, final boolean shiftOnHover) {
        super(id, x, y, width, height, text);
        this.parentGui = parentGui;
        this.textColor = textColor;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
        this.shiftOnHover = shiftOnHover;
        this.texX = texX;
        this.texY = texY;
    }
    
    public void drawButton(final Minecraft par1Minecraft, final int par2, final int par3) {
        if (this.isSelected == null) {
            this.isSelected = this.parentGui.getInitiallySelected(this);
        }
        if (this.visible) {
            par1Minecraft.getTextureManager().bindTexture(GuiElementCheckbox.texture);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.field_146123_n = (par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, ((boolean)this.isSelected) ? (this.texX + this.texWidth) : this.texX, this.field_146123_n ? (this.shiftOnHover ? (this.texY + this.texHeight) : this.texY) : this.texY, this.width, this.height);
            this.mouseDragged(par1Minecraft, par2, par3);
            par1Minecraft.fontRenderer.drawString(this.displayString, this.xPosition + this.width + 3, this.yPosition + (this.height - 6) / 2, this.textColor, false);
        }
    }
    
    public void drawTexturedModalRect(final int par1, final int par2, final int par3, final int par4, final int par5, final int par6) {
        final float f = 0.00390625f;
        final float f2 = 0.00390625f;
        final Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + par6), (double)this.zLevel, (double)((par3 + 0) * f), (double)((par4 + this.texHeight) * f2));
        tessellator.addVertexWithUV((double)(par1 + par5), (double)(par2 + par6), (double)this.zLevel, (double)((par3 + this.texWidth) * f), (double)((par4 + this.texHeight) * f2));
        tessellator.addVertexWithUV((double)(par1 + par5), (double)(par2 + 0), (double)this.zLevel, (double)((par3 + this.texWidth) * f), (double)((par4 + 0) * f2));
        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)this.zLevel, (double)((par3 + 0) * f), (double)((par4 + 0) * f2));
        tessellator.draw();
    }
    
    public boolean mousePressed(final Minecraft par1Minecraft, final int par2, final int par3) {
        if (this.enabled && this.visible && par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height) {
            if (this.parentGui.canPlayerEdit(this, (EntityPlayer)par1Minecraft.thePlayer)) {
                this.isSelected = !this.isSelected;
                this.parentGui.onSelectionChanged(this, this.isSelected);
                return true;
            }
            this.parentGui.onIntruderInteraction();
        }
        return false;
    }
    
    static {
        texture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/gui.png");
    }
    
    public interface ICheckBoxCallback
    {
        void onSelectionChanged(final GuiElementCheckbox p0, final boolean p1);
        
        boolean canPlayerEdit(final GuiElementCheckbox p0, final EntityPlayer p1);
        
        boolean getInitiallySelected(final GuiElementCheckbox p0);
        
        void onIntruderInteraction();
    }
}
