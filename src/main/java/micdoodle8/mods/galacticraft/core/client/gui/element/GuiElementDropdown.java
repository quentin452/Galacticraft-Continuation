package micdoodle8.mods.galacticraft.core.client.gui.element;

import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.client.gui.screen.*;
import cpw.mods.fml.client.*;
import net.minecraft.client.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.client.gui.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.*;

public class GuiElementDropdown extends GuiButton
{
    protected static final ResourceLocation texture;
    public boolean dropdownClicked;
    public String[] optionStrings;
    public int selectedOption;
    public SmallFontRenderer font;
    private IDropboxCallback parentClass;
    
    public GuiElementDropdown(final int id, final IDropboxCallback parentClass, final int x, final int y, final String... text) {
        super(id, x, y, 13, 13, "");
        this.selectedOption = -1;
        final Minecraft mc = FMLClientHandler.instance().getClient();
        this.parentClass = parentClass;
        this.font = new SmallFontRenderer(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.renderEngine, false);
        this.optionStrings = text;
        int largestString = Integer.MIN_VALUE;
        for (final String element : text) {
            largestString = Math.max(largestString, this.font.getStringWidth(element));
        }
        this.width = largestString + 8;
    }
    
    public void drawButton(final Minecraft par1Minecraft, final int par2, final int par3) {
        if (this.selectedOption == -1) {
            this.selectedOption = this.parentClass.getInitialSelection(this);
        }
        if (this.visible) {
            GL11.glPushMatrix();
            this.zLevel = 300.0f;
            GL11.glTranslatef(0.0f, 0.0f, 500.0f);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.field_146123_n = (par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height);
            Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + (this.dropdownClicked ? (this.height * this.optionStrings.length) : this.height), ColorUtil.to32BitColor(255, 200, 200, 200));
            Gui.drawRect(this.xPosition + 1, this.yPosition + 1, this.xPosition + this.width - 1, this.yPosition + (this.dropdownClicked ? (this.height * this.optionStrings.length) : this.height) - 1, ColorUtil.to32BitColor(255, 0, 0, 0));
            if (this.dropdownClicked && par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height * this.optionStrings.length) {
                final int hoverPos = (par3 - this.yPosition) / this.height;
                Gui.drawRect(this.xPosition + 1, this.yPosition + this.height * hoverPos + 1, this.xPosition + this.width - 1, this.yPosition + this.height * (hoverPos + 1) - 1, ColorUtil.to32BitColor(255, 100, 100, 100));
            }
            this.mouseDragged(par1Minecraft, par2, par3);
            if (this.dropdownClicked) {
                for (int i = 0; i < this.optionStrings.length; ++i) {
                    this.font.drawStringWithShadow(this.optionStrings[i], this.xPosition + this.width / 2 - this.font.getStringWidth(this.optionStrings[i]) / 2, this.yPosition + (this.height - 8) / 2 + this.height * i, ColorUtil.to32BitColor(255, 255, 255, 255));
                }
            }
            else {
                this.font.drawStringWithShadow(this.optionStrings[this.selectedOption], this.xPosition + this.width / 2 - this.font.getStringWidth(this.optionStrings[this.selectedOption]) / 2, this.yPosition + (this.height - 8) / 2, ColorUtil.to32BitColor(255, 255, 255, 255));
            }
            GL11.glPopMatrix();
            this.zLevel = 0.0f;
        }
    }
    
    public boolean mousePressed(final Minecraft par1Minecraft, final int par2, final int par3) {
        if (!this.dropdownClicked) {
            if (this.enabled && this.visible && par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height) {
                if (this.parentClass.canBeClickedBy(this, (EntityPlayer)par1Minecraft.thePlayer)) {
                    return this.dropdownClicked = true;
                }
                this.parentClass.onIntruderInteraction();
            }
        }
        else if (this.enabled && this.visible && par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height * this.optionStrings.length) {
            if (this.parentClass.canBeClickedBy(this, (EntityPlayer)par1Minecraft.thePlayer)) {
                final int optionClicked = (par3 - this.yPosition) / this.height;
                this.selectedOption = optionClicked % this.optionStrings.length;
                this.dropdownClicked = false;
                this.parentClass.onSelectionChanged(this, this.selectedOption);
                return true;
            }
            this.parentClass.onIntruderInteraction();
        }
        else {
            this.dropdownClicked = false;
        }
        return false;
    }
    
    static {
        texture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/gui.png");
    }
    
    public interface IDropboxCallback
    {
        boolean canBeClickedBy(final GuiElementDropdown p0, final EntityPlayer p1);
        
        void onSelectionChanged(final GuiElementDropdown p0, final int p1);
        
        int getInitialSelection(final GuiElementDropdown p0);
        
        void onIntruderInteraction();
    }
}
