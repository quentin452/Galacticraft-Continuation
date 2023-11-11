package micdoodle8.mods.galacticraft.core.client.gui.element;

import net.minecraft.client.*;
import cpw.mods.fml.client.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.client.gui.*;
import org.lwjgl.input.*;
import net.minecraft.util.*;

public class GuiElementTextBox extends GuiButton
{
    public String text;
    public boolean numericOnly;
    public boolean centered;
    private int maxLength;
    public long timeBackspacePressed;
    public int cursorPulse;
    public int backspacePressed;
    public boolean isTextFocused;
    public int incorrectUseTimer;
    private ITextBoxCallback parentGui;
    private Minecraft mc;
    
    public GuiElementTextBox(final int id, final ITextBoxCallback parentGui, final int x, final int y, final int width, final int height, final String initialText, final boolean numericOnly, final int maxLength, final boolean centered) {
        super(id, x, y, width, height, initialText);
        this.isTextFocused = false;
        this.mc = FMLClientHandler.instance().getClient();
        this.parentGui = parentGui;
        this.numericOnly = numericOnly;
        this.maxLength = maxLength;
        this.centered = centered;
    }
    
    public boolean keyTyped(final char keyChar, final int keyID) {
        if (this.isTextFocused) {
            if (keyID == 14) {
                if (this.text.length() > 0) {
                    if (this.parentGui.canPlayerEdit(this, (EntityPlayer)this.mc.thePlayer)) {
                        final String toBeParsed = this.text.substring(0, this.text.length() - 1);
                        if (this.isValid(toBeParsed)) {
                            this.text = toBeParsed;
                            this.timeBackspacePressed = System.currentTimeMillis();
                        }
                        else {
                            this.text = "";
                        }
                    }
                    else {
                        this.incorrectUseTimer = 10;
                        this.parentGui.onIntruderInteraction(this);
                    }
                }
            }
            else if (keyChar == '\u0016') {
                String pastestring = GuiScreen.getClipboardString();
                if (pastestring == null) {
                    pastestring = "";
                }
                if (this.isValid(this.text + pastestring)) {
                    if (this.parentGui.canPlayerEdit(this, (EntityPlayer)this.mc.thePlayer)) {
                        this.text += pastestring;
                        this.text = this.text.substring(0, Math.min(String.valueOf(this.text).length(), this.maxLength));
                    }
                    else {
                        this.incorrectUseTimer = 10;
                        this.parentGui.onIntruderInteraction(this);
                    }
                }
            }
            else if (this.isValid(this.text + keyChar)) {
                if (this.parentGui.canPlayerEdit(this, (EntityPlayer)this.mc.thePlayer)) {
                    this.text += keyChar;
                    this.text = this.text.substring(0, Math.min(this.text.length(), this.maxLength));
                }
                else {
                    this.incorrectUseTimer = 10;
                    this.parentGui.onIntruderInteraction(this);
                }
            }
            this.parentGui.onTextChanged(this, this.text);
            return true;
        }
        return false;
    }
    
    public void drawButton(final Minecraft par1Minecraft, final int par2, final int par3) {
        if (this.text == null) {
            this.text = this.parentGui.getInitialText(this);
            this.parentGui.onTextChanged(this, this.text);
        }
        if (this.visible) {
            Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, ColorUtil.to32BitColor(140, 140, 140, 140));
            Gui.drawRect(this.xPosition + 1, this.yPosition + 1, this.xPosition + this.width - 1, this.yPosition + this.height - 1, ColorUtil.to32BitColor(255, 0, 0, 0));
            ++this.cursorPulse;
            if (this.timeBackspacePressed > 0L) {
                if (Keyboard.isKeyDown(14) && this.text.length() > 0) {
                    if (System.currentTimeMillis() - this.timeBackspacePressed > 200.0f / (1.0f + this.backspacePressed * 0.3f) && this.parentGui.canPlayerEdit(this, (EntityPlayer)this.mc.thePlayer)) {
                        final String toBeParsed = this.text.substring(0, this.text.length() - 1);
                        if (this.isValid(toBeParsed)) {
                            this.text = toBeParsed;
                            this.parentGui.onTextChanged(this, this.text);
                        }
                        else {
                            this.text = "";
                        }
                        this.timeBackspacePressed = System.currentTimeMillis();
                        ++this.backspacePressed;
                    }
                    else if (!this.parentGui.canPlayerEdit(this, (EntityPlayer)this.mc.thePlayer)) {
                        this.incorrectUseTimer = 10;
                        this.parentGui.onIntruderInteraction(this);
                    }
                }
                else {
                    this.timeBackspacePressed = 0L;
                    this.backspacePressed = 0;
                }
            }
            if (this.incorrectUseTimer > 0) {
                --this.incorrectUseTimer;
            }
            int xPos = this.xPosition + 4;
            if (this.centered) {
                xPos = this.xPosition + this.width / 2 - this.mc.fontRenderer.getStringWidth(this.text) / 2;
            }
            this.drawString(this.mc.fontRenderer, this.text + ((this.cursorPulse / 24 % 2 == 0 && this.isTextFocused) ? "_" : ""), xPos, this.yPosition + this.height / 2 - 4, (this.incorrectUseTimer > 0) ? ColorUtil.to32BitColor(255, 255, 20, 20) : this.parentGui.getTextColor(this));
        }
    }
    
    public int getIntegerValue() {
        try {
            return Integer.parseInt(this.text.equals("") ? "0" : this.text);
        }
        catch (Exception e) {
            return -1;
        }
    }
    
    public boolean isValid(final String string) {
        if (this.numericOnly) {
            if (string.length() > 0 && ChatAllowedCharacters.isAllowedCharacter(string.charAt(string.length() - 1))) {
                try {
                    Integer.parseInt(string);
                    return true;
                }
                catch (Exception e) {
                    return false;
                }
            }
            return false;
        }
        return string.length() > 0 && ChatAllowedCharacters.isAllowedCharacter(string.charAt(string.length() - 1));
    }
    
    public boolean mousePressed(final Minecraft par1Minecraft, final int par2, final int par3) {
        if (super.mousePressed(par1Minecraft, par2, par3)) {
            Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, -6250336);
            this.isTextFocused = true;
            this.text = this.parentGui.getInitialText(this);
            this.parentGui.onTextChanged(this, this.text);
            return true;
        }
        return this.isTextFocused = false;
    }
    
    public int getMaxLength() {
        return this.maxLength;
    }
    
    public interface ITextBoxCallback
    {
        boolean canPlayerEdit(final GuiElementTextBox p0, final EntityPlayer p1);
        
        void onTextChanged(final GuiElementTextBox p0, final String p1);
        
        String getInitialText(final GuiElementTextBox p0);
        
        int getTextColor(final GuiElementTextBox p0);
        
        void onIntruderInteraction(final GuiElementTextBox p0);
    }
}
