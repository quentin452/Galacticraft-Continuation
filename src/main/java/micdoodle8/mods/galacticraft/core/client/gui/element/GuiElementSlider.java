package micdoodle8.mods.galacticraft.core.client.gui.element;

import net.minecraft.client.gui.*;
import micdoodle8.mods.galacticraft.core.client.gui.screen.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.client.*;
import net.minecraft.util.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class GuiElementSlider extends GuiButton
{
    private SmallFontRenderer customFontRenderer;
    private Vector3 firstColor;
    private Vector3 lastColor;
    private final boolean isVertical;
    private int sliderPos;
    
    public GuiElementSlider(final int id, final int x, final int y, final int width, final int height, final boolean vertical, final Vector3 firstColor, final Vector3 lastColor) {
        this(id, x, y, width, height, vertical, firstColor, lastColor, "");
    }
    
    public GuiElementSlider(final int id, final int x, final int y, final int width, final int height, final boolean vertical, final Vector3 firstColor, final Vector3 lastColor, final String displayString) {
        super(id, x, y, width, height, displayString);
        this.isVertical = vertical;
        this.firstColor = firstColor;
        this.lastColor = lastColor;
        this.customFontRenderer = new SmallFontRenderer(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().renderEngine, false);
    }
    
    public void drawButton(final Minecraft par1Minecraft, final int par2, final int par3) {
        if (this.visible) {
            this.field_146123_n = (par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height);
            if (Mouse.isButtonDown(0) && this.field_146123_n) {
                if (this.isVertical) {
                    this.sliderPos = par3 - this.yPosition;
                }
                else {
                    this.sliderPos = par2 - this.xPosition;
                }
            }
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glDisable(3553);
            GL11.glEnable(3042);
            GL11.glDisable(3008);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glShadeModel(7425);
            final Tessellator tessellator = Tessellator.instance;
            if (this.isVertical) {
                tessellator.startDrawingQuads();
                tessellator.setColorRGBA_F(0.0f, 0.0f, 0.0f, 1.0f);
                tessellator.addVertex(this.xPosition + (double)this.width, (double)this.yPosition, (double)this.zLevel);
                tessellator.addVertex((double)this.xPosition, (double)this.yPosition, (double)this.zLevel);
                tessellator.addVertex((double)this.xPosition, this.yPosition + (double)this.height, (double)this.zLevel);
                tessellator.addVertex(this.xPosition + (double)this.width, this.yPosition + (double)this.height, (double)this.zLevel);
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setColorRGBA_F(this.firstColor.floatX(), this.firstColor.floatY(), this.firstColor.floatZ(), 1.0f);
                tessellator.addVertex(this.xPosition + (double)this.width - 1.0, this.yPosition + 1.0, (double)this.zLevel);
                tessellator.addVertex(this.xPosition + 1.0, this.yPosition + 1.0, (double)this.zLevel);
                tessellator.setColorRGBA_F(this.lastColor.floatX(), this.lastColor.floatY(), this.lastColor.floatZ(), 1.0f);
                tessellator.addVertex(this.xPosition + 1.0, this.yPosition + (double)this.height - 1.0, (double)this.zLevel);
                tessellator.addVertex(this.xPosition + (double)this.width - 1.0, this.yPosition + (double)this.height - 1.0, (double)this.zLevel);
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setColorRGBA_F(1.0f, 1.0f, 1.0f, 1.0f);
                tessellator.addVertex(this.xPosition + (double)this.width, this.yPosition + (double)this.sliderPos - 1.0, (double)this.zLevel);
                tessellator.addVertex((double)this.xPosition, this.yPosition + (double)this.sliderPos - 1.0, (double)this.zLevel);
                tessellator.addVertex((double)this.xPosition, this.yPosition + (double)this.sliderPos + 1.0, (double)this.zLevel);
                tessellator.addVertex(this.xPosition + (double)this.width, this.yPosition + (double)this.sliderPos + 1.0, (double)this.zLevel);
                tessellator.draw();
            }
            else {
                tessellator.startDrawingQuads();
                tessellator.setColorRGBA_F(0.0f, 0.0f, 0.0f, 1.0f);
                tessellator.addVertex(this.xPosition + (double)this.width, (double)this.yPosition, (double)this.zLevel);
                tessellator.addVertex((double)this.xPosition, (double)this.yPosition, (double)this.zLevel);
                tessellator.addVertex((double)this.xPosition, this.yPosition + (double)this.height, (double)this.zLevel);
                tessellator.addVertex(this.xPosition + (double)this.width, this.yPosition + (double)this.height, (double)this.zLevel);
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setColorRGBA_F(this.lastColor.floatX(), this.lastColor.floatY(), this.lastColor.floatZ(), 1.0f);
                tessellator.addVertex(this.xPosition + (double)this.width - 1.0, this.yPosition + 1.0, (double)this.zLevel);
                tessellator.setColorRGBA_F(this.firstColor.floatX(), this.firstColor.floatY(), this.firstColor.floatZ(), 1.0f);
                tessellator.addVertex(this.xPosition + 1.0, this.yPosition + 1.0, (double)this.zLevel);
                tessellator.addVertex(this.xPosition + 1.0, this.yPosition + (double)this.height - 1.0, (double)this.zLevel);
                tessellator.setColorRGBA_F(this.lastColor.floatX(), this.lastColor.floatY(), this.lastColor.floatZ(), 1.0f);
                tessellator.addVertex(this.xPosition + (double)this.width - 1.0, this.yPosition + (double)this.height - 1.0, (double)this.zLevel);
                tessellator.draw();
                GL11.glShadeModel(7424);
                GL11.glDisable(3042);
                GL11.glEnable(3008);
                GL11.glEnable(3553);
                if (this.displayString != null && this.displayString.length() > 0) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)(this.xPosition + this.width / 2), (float)(this.yPosition + this.height / 2), 0.0f);
                    GL11.glScalef(0.5f, 0.5f, 1.0f);
                    GL11.glTranslatef((float)(-1 * (this.xPosition + this.width / 2)), (float)(-1 * (this.yPosition + this.height / 2)), 0.0f);
                    this.customFontRenderer.drawString(this.displayString, this.xPosition + this.width / 2 - this.customFontRenderer.getStringWidth(this.displayString) / 2, this.yPosition + this.height / 2 - 3, ColorUtil.to32BitColor(255, 240, 240, 240));
                    GL11.glPopMatrix();
                }
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glDisable(3553);
                GL11.glEnable(3042);
                GL11.glDisable(3008);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                GL11.glShadeModel(7425);
                tessellator.startDrawingQuads();
                tessellator.setColorRGBA_F(1.0f, 1.0f, 1.0f, 1.0f);
                tessellator.addVertex(this.xPosition + (double)this.sliderPos + 1.0, (double)this.yPosition, (double)this.zLevel);
                tessellator.addVertex(this.xPosition + (double)this.sliderPos - 1.0, (double)this.yPosition, (double)this.zLevel);
                tessellator.addVertex(this.xPosition + (double)this.sliderPos - 1.0, this.yPosition + (double)this.height, (double)this.zLevel);
                tessellator.addVertex(this.xPosition + (double)this.sliderPos + 1.0, this.yPosition + (double)this.height, (double)this.zLevel);
                tessellator.draw();
            }
            GL11.glShadeModel(7424);
            GL11.glDisable(3042);
            GL11.glEnable(3008);
            GL11.glEnable(3553);
        }
    }
    
    public void setSliderPos(final float pos) {
        this.sliderPos = (int)Math.floor(this.height * pos);
    }
    
    public int getSliderPos() {
        return this.sliderPos;
    }
    
    public float getNormalizedValue() {
        return this.sliderPos / (float)this.height;
    }
    
    public double getColorValueD() {
        return this.sliderPos * 255.0 / (this.height - 1);
    }
    
    public int getButtonHeight() {
        return this.height;
    }
}
