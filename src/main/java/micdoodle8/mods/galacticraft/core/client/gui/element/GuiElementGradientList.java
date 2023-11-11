package micdoodle8.mods.galacticraft.core.client.gui.element;

import java.util.*;
import org.lwjgl.input.*;
import micdoodle8.mods.galacticraft.core.util.*;
import cpw.mods.fml.client.*;
import net.minecraft.client.gui.*;

public class GuiElementGradientList extends Gui
{
    public static final int BUTTON_HEIGHT = 20;
    public int width;
    public int height;
    public int xPosition;
    public int yPosition;
    public List<ListElement> listContents;
    public int sliderPos;
    private int lastMousePosY;
    private boolean sliderGrabbed;
    private boolean sliderEnabled;
    private int selectedIndex;
    
    public GuiElementGradientList(final int xPos, final int yPos, final int width, final int height) {
        this.listContents = new ArrayList<ListElement>();
        this.selectedIndex = -1;
        this.xPosition = xPos;
        this.yPosition = yPos;
        this.width = width;
        this.height = height;
        this.sliderPos = this.yPosition + 1;
    }
    
    public void updateListContents(final List<ListElement> newContents) {
        this.listContents = newContents;
        this.sliderEnabled = (this.listContents.size() * 20 > this.height);
        if (this.selectedIndex >= this.listContents.size()) {
            this.selectedIndex = -1;
        }
    }
    
    public void draw(final int mousePosX, final int mousePosY) {
        if (this.sliderEnabled) {
            if (this.sliderGrabbed || (mousePosX >= this.xPosition + this.width - 9 && mousePosX < this.xPosition + this.width && mousePosY >= this.yPosition && mousePosY < this.yPosition + this.height)) {
                if (Mouse.isButtonDown(0)) {
                    this.sliderGrabbed = true;
                    if (this.lastMousePosY > 0) {
                        if (mousePosY >= this.sliderPos && mousePosY < this.sliderPos + 15) {
                            final int deltaY = this.lastMousePosY - this.sliderPos;
                            this.sliderPos = mousePosY - deltaY;
                        }
                        else {
                            this.sliderPos = mousePosY - 7;
                        }
                    }
                    this.lastMousePosY = mousePosY;
                }
                else {
                    this.sliderGrabbed = false;
                }
            }
            else {
                this.lastMousePosY = 0;
            }
        }
        if (Mouse.isButtonDown(0) && mousePosX >= this.xPosition && mousePosX < this.xPosition + this.width - 10 && mousePosY >= this.yPosition && mousePosY < this.yPosition + this.height) {
            final int clickPosY = mousePosY - this.yPosition + (int)Math.floor((this.listContents.size() * 20 - this.height) * this.getSliderPercentage());
            this.selectedIndex = clickPosY / 20;
            if (this.selectedIndex < 0 || this.selectedIndex >= this.listContents.size()) {
                this.selectedIndex = -1;
            }
        }
        this.sliderPos = Math.min(Math.max(this.yPosition, this.sliderPos), this.yPosition + this.height - 15);
        this.drawGradientRect(this.xPosition, this.yPosition, this.xPosition + this.width - 10, this.yPosition + this.height, ColorUtil.to32BitColor(255, 30, 30, 30), ColorUtil.to32BitColor(255, 30, 30, 30));
        this.drawGradientRect(this.xPosition + this.width - 9, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, ColorUtil.to32BitColor(255, 50, 50, 50), ColorUtil.to32BitColor(255, 50, 50, 50));
        int sliderColor = this.sliderEnabled ? ColorUtil.to32BitColor(255, 90, 90, 90) : ColorUtil.to32BitColor(255, 40, 40, 40);
        this.drawGradientRect(this.xPosition + this.width - 9, this.sliderPos, this.xPosition + this.width, this.sliderPos + 15, sliderColor, sliderColor);
        Gui.drawRect(this.xPosition + this.width - 1, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, ColorUtil.to32BitColor(255, 0, 0, 0));
        Gui.drawRect(this.xPosition + this.width - 10, this.yPosition, this.xPosition + this.width - 9, this.yPosition + this.height, ColorUtil.to32BitColor(255, 0, 0, 0));
        Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + 1, this.yPosition + this.height, ColorUtil.to32BitColor(255, 0, 0, 0));
        Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + 1, ColorUtil.to32BitColor(255, 0, 0, 0));
        Gui.drawRect(this.xPosition, this.yPosition + this.height - 1, this.xPosition + this.width, this.yPosition + this.height, ColorUtil.to32BitColor(255, 0, 0, 0));
        sliderColor = (this.sliderEnabled ? ColorUtil.to32BitColor(255, 120, 120, 120) : ColorUtil.to32BitColor(255, 60, 60, 60));
        Gui.drawRect(this.xPosition + this.width - 9, this.sliderPos + 1, this.xPosition + this.width - 8, this.sliderPos + 14, sliderColor);
        Gui.drawRect(this.xPosition + this.width - 2, this.sliderPos + 1, this.xPosition + this.width - 1, this.sliderPos + 14, sliderColor);
        Gui.drawRect(this.xPosition + this.width - 9, this.sliderPos, this.xPosition + this.width - 1, this.sliderPos + 1, sliderColor);
        Gui.drawRect(this.xPosition + this.width - 9, this.sliderPos + 15, this.xPosition + this.width - 1, this.sliderPos + 14, sliderColor);
        int currentDrawHeight = this.yPosition + 1 - (int)Math.floor((this.listContents.size() * 20 - this.height) * this.getSliderPercentage());
        final FontRenderer fontRenderer = FMLClientHandler.instance().getClient().fontRenderer;
        for (int i = 0; i < this.listContents.size(); ++i) {
            final ListElement displayButton = this.listContents.get(i);
            if (displayButton != null && displayButton.value != null && !displayButton.value.isEmpty()) {
                int yCoord0 = currentDrawHeight;
                int yCoord2 = currentDrawHeight + 20 - 1;
                if (yCoord2 > this.yPosition && yCoord0 < this.yPosition + this.height) {
                    yCoord0 = Math.max(this.yPosition + 1, yCoord0);
                    yCoord2 = Math.min(this.yPosition + this.height - 1, yCoord2);
                    final int color = (i == this.selectedIndex) ? ColorUtil.to32BitColor(255, 35, 35, 35) : ColorUtil.to32BitColor(255, 25, 25, 25);
                    Gui.drawRect(this.xPosition + 1, yCoord0, this.xPosition + this.width - 10, yCoord2, color);
                    if (currentDrawHeight + 10 - fontRenderer.FONT_HEIGHT / 2 > this.yPosition && currentDrawHeight + 10 + fontRenderer.FONT_HEIGHT / 2 < this.yPosition + this.height) {
                        fontRenderer.drawString(displayButton.value, this.xPosition + (this.width - 10) / 2 - fontRenderer.getStringWidth(displayButton.value) / 2, currentDrawHeight + 10 - fontRenderer.FONT_HEIGHT / 2, displayButton.color);
                    }
                }
                currentDrawHeight += 20;
            }
        }
    }
    
    public void update() {
    }
    
    private float getSliderPercentage() {
        if (!this.sliderEnabled) {
            return 0.0f;
        }
        return (this.sliderPos - this.yPosition) / (float)(this.height - 15);
    }
    
    public ListElement getSelectedElement() {
        if (this.selectedIndex == -1) {
            return null;
        }
        return this.listContents.get(this.selectedIndex);
    }
    
    public static class ListElement
    {
        public String value;
        public int color;
        
        public ListElement(final String value, final int color) {
            this.value = value;
            this.color = color;
        }
    }
}
