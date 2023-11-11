package micdoodle8.mods.galacticraft.planets.mars.client.gui;

import micdoodle8.mods.galacticraft.planets.mars.entities.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.planets.mars.network.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraft.util.*;
import net.minecraft.client.*;
import net.minecraft.client.audio.*;
import org.lwjgl.opengl.*;
import org.lwjgl.input.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.item.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.*;
import net.minecraft.client.renderer.*;

public class GuiSlimeling extends GuiScreen
{
    private final int xSize;
    private final int ySize;
    private static final ResourceLocation slimelingPanelGui;
    private final EntitySlimeling slimeling;
    public static RenderItem drawItems;
    public long timeBackspacePressed;
    public int cursorPulse;
    public int backspacePressed;
    public boolean isTextFocused;
    public int incorrectUseTimer;
    public GuiButton stayButton;
    public static boolean renderingOnGui;
    private int invX;
    private int invY;
    private final int invWidth = 18;
    private final int invHeight = 18;
    
    public GuiSlimeling(final EntitySlimeling slimeling) {
        this.isTextFocused = false;
        this.slimeling = slimeling;
        this.xSize = 176;
        this.ySize = 147;
    }
    
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        this.stayButton = new GuiButton(0, var5 + 120, var6 + 122, 50, 20, "");
        this.stayButton.enabled = this.slimeling.isOwner((EntityLivingBase)this.mc.thePlayer);
        this.buttonList.add(this.stayButton);
        this.invX = var5 + 151;
        this.invY = var6 + 76;
    }
    
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    protected void keyTyped(final char keyChar, final int keyID) {
        if (!this.isTextFocused) {
            super.keyTyped(keyChar, keyID);
            return;
        }
        if (keyID == 14) {
            if (this.slimeling.getName().length() > 0) {
                if (this.slimeling.isOwner((EntityLivingBase)this.mc.thePlayer)) {
                    this.slimeling.setName(this.slimeling.getName().substring(0, this.slimeling.getName().length() - 1));
                    this.timeBackspacePressed = System.currentTimeMillis();
                }
                else {
                    this.incorrectUseTimer = 10;
                }
            }
        }
        else if (keyChar == '\u0016') {
            String pastestring = GuiScreen.getClipboardString();
            if (pastestring == null) {
                pastestring = "";
            }
            if (this.isValid(this.slimeling.getName() + pastestring)) {
                if (this.slimeling.isOwner((EntityLivingBase)this.mc.thePlayer)) {
                    this.slimeling.setName(this.slimeling.getName() + pastestring);
                    this.slimeling.setName(this.slimeling.getName().substring(0, Math.min(this.slimeling.getName().length(), 16)));
                }
                else {
                    this.incorrectUseTimer = 10;
                }
            }
        }
        else if (this.isValid(this.slimeling.getName() + keyChar)) {
            if (this.mc.thePlayer.getGameProfile().getName().equals(this.slimeling.getOwnerUsername())) {
                this.slimeling.setName(this.slimeling.getName() + keyChar);
                this.slimeling.setName(this.slimeling.getName().substring(0, Math.min(this.slimeling.getName().length(), 16)));
            }
            else {
                this.incorrectUseTimer = 10;
            }
        }
        GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimpleMars(PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_SLIMELING_DATA, new Object[] { this.slimeling.getEntityId(), 1, this.slimeling.getName() }));
        super.keyTyped(keyChar, keyID);
    }
    
    public boolean isValid(final String string) {
        return ChatAllowedCharacters.isAllowedCharacter(string.charAt(string.length() - 1));
    }
    
    protected void actionPerformed(final GuiButton par1GuiButton) {
        if (par1GuiButton.enabled) {
            switch (par1GuiButton.id) {
                case 0: {
                    GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimpleMars(PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_SLIMELING_DATA, new Object[] { this.slimeling.getEntityId(), 0, "" }));
                    break;
                }
            }
        }
    }
    
    protected void mouseClicked(final int px, final int py, final int par3) {
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        final int startX = -20 + var5 + 60;
        final int startY = 65 + var6 - 13;
        final int width = this.xSize - 45;
        final int height = 18;
        if (px >= startX && px < startX + width && py >= startY && py < startY + 18) {
            Gui.drawRect(startX, startY, startX + width, startY + 18, -6250336);
            this.isTextFocused = true;
        }
        else {
            this.isTextFocused = false;
        }
        if (px >= this.invX) {
            final int invX = this.invX;
            this.getClass();
            if (px < invX + 18 && py >= this.invY) {
                final int invY = this.invY;
                this.getClass();
                if (py < invY + 18) {
                    Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0f));
                    GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimpleMars(PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_SLIMELING_DATA, new Object[] { this.slimeling.getEntityId(), 6, "" }));
                }
            }
        }
        super.mouseClicked(px, py, par3);
    }
    
    public void drawScreen(final int par1, final int par2, final float par3) {
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, 0.0f, -70.0f);
        Gui.drawRect(var5, var6, var5 + this.xSize, var6 + this.ySize - 20, -16777216);
        GL11.glPopMatrix();
        final int yOffset = (int)Math.floor(30.0 * (1.0f - this.slimeling.getScale()));
        drawSlimelingOnGui(this, this.slimeling, this.width / 2, var6 + 62 - yOffset, 70, (float)(var5 + 51 - par1), (float)(var6 + 75 - 50 - par2));
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, 0.0f, 150.0f);
        this.mc.renderEngine.bindTexture(GuiSlimeling.slimelingPanelGui);
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        this.drawTexturedModalRect(var5 + this.xSize - 15, var6 + 9, 176, 0, 9, 9);
        this.drawTexturedModalRect(var5 + this.xSize - 15, var6 + 22, 185, 0, 9, 9);
        this.drawTexturedModalRect(var5 + this.xSize - 15, var6 + 35, 194, 0, 9, 9);
        String str = "" + Math.round(this.slimeling.getColorRed() * 1000.0f) / 10.0f + "% ";
        this.drawString(this.fontRendererObj, str, var5 + this.xSize - 15 - this.fontRendererObj.getStringWidth(str), var6 + 10, ColorUtil.to32BitColor(255, 255, 0, 0));
        str = "" + Math.round(this.slimeling.getColorGreen() * 1000.0f) / 10.0f + "% ";
        this.drawString(this.fontRendererObj, str, var5 + this.xSize - 15 - this.fontRendererObj.getStringWidth(str), var6 + 23, ColorUtil.to32BitColor(255, 0, 255, 0));
        str = "" + Math.round(this.slimeling.getColorBlue() * 1000.0f) / 10.0f + "% ";
        this.drawString(this.fontRendererObj, str, var5 + this.xSize - 15 - this.fontRendererObj.getStringWidth(str), var6 + 36, ColorUtil.to32BitColor(255, 0, 0, 255));
        this.mc.renderEngine.bindTexture(GuiSlimeling.slimelingPanelGui);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        final int invX = this.invX;
        final int invY = this.invY;
        final int n = 176;
        final int n2 = 9;
        this.getClass();
        final int n3 = 18;
        this.getClass();
        this.drawTexturedModalRect(invX, invY, n, n2, n3, 18);
        super.drawScreen(par1, par2, par3);
        ++this.cursorPulse;
        if (this.timeBackspacePressed > 0L) {
            if (Keyboard.isKeyDown(14) && this.slimeling.getName().length() > 0) {
                if (System.currentTimeMillis() - this.timeBackspacePressed > 200.0f / (1.0f + this.backspacePressed * 0.3f) && this.slimeling.isOwner((EntityLivingBase)this.mc.thePlayer)) {
                    this.slimeling.setName(this.slimeling.getName().substring(0, this.slimeling.getName().length() - 1));
                    GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimpleMars(PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_SLIMELING_DATA, new Object[] { this.slimeling.getEntityId(), 1, this.slimeling.getName() }));
                    this.timeBackspacePressed = System.currentTimeMillis();
                    ++this.backspacePressed;
                }
                else if (!this.slimeling.isOwner((EntityLivingBase)this.mc.thePlayer)) {
                    this.incorrectUseTimer = 10;
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
        final int dX = -45;
        final int dY = 65;
        final int startX = -20 + var5 + 60;
        final int startY = 65 + var6 - 10;
        final int width = this.xSize - 60;
        final int height = 15;
        Gui.drawRect(startX, startY, startX + width, startY + 15, -6250336);
        Gui.drawRect(startX + 1, startY + 1, startX + width - 1, startY + 15 - 1, -16777216);
        this.drawString(this.fontRendererObj, this.slimeling.getName() + ((this.cursorPulse / 24 % 2 == 0 && this.isTextFocused) ? "_" : ""), startX + 4, startY + 4, (this.incorrectUseTimer > 0) ? ColorUtil.to32BitColor(255, 255, 20, 20) : 14737632);
        this.stayButton.displayString = (this.slimeling.isSitting() ? GCCoreUtil.translate("gui.slimeling.button.follow") : GCCoreUtil.translate("gui.slimeling.button.sit"));
        this.fontRendererObj.drawString(GCCoreUtil.translate("gui.slimeling.name") + ": ", -45 + var5 + 55, 65 + var6 - 6, 4210752);
        this.fontRendererObj.drawString(GCCoreUtil.translate("gui.slimeling.owner") + ": " + this.slimeling.getOwnerUsername(), -45 + var5 + 55, 65 + var6 + 7, 4210752);
        this.fontRendererObj.drawString(GCCoreUtil.translate("gui.slimeling.kills") + ": " + this.slimeling.getKillCount(), -45 + var5 + 55, 65 + var6 + 20, 4210752);
        final FontRenderer fontRendererObj = this.fontRendererObj;
        final StringBuilder append = new StringBuilder().append(GCCoreUtil.translate("gui.slimeling.scale")).append(": ");
        final float n4 = (float)this.slimeling.getAge();
        this.slimeling.getClass();
        fontRendererObj.drawString(append.append(Math.round(n4 / 100000.0f * 1000.0f) / 10.0f).append("%").toString(), -45 + var5 + 55, 65 + var6 + 33, 4210752);
        str = "" + (this.slimeling.isSitting() ? GCCoreUtil.translate("gui.slimeling.sitting") : GCCoreUtil.translate("gui.slimeling.following"));
        this.fontRendererObj.drawString(str, var5 + 145 - this.fontRendererObj.getStringWidth(str) / 2, var6 + 112, 4210752);
        str = GCCoreUtil.translate("gui.slimeling.damage") + ": " + Math.round(this.slimeling.getDamage() * 100.0f) / 100.0f;
        this.fontRendererObj.drawString(str, -45 + var5 + 55, 65 + var6 + 33 + 13, 4210752);
        str = GCCoreUtil.translate("gui.slimeling.food") + ": ";
        this.fontRendererObj.drawString(str, -45 + var5 + 55, 65 + var6 + 46 + 13, 4210752);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(32826);
        GuiSlimeling.drawItems.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.renderEngine, new ItemStack(this.slimeling.getFavoriteFood()), -45 + var5 + 55 + this.fontRendererObj.getStringWidth(str), 65 + var6 + 41 + 14);
        GL11.glDisable(2896);
        GL11.glPopMatrix();
        try {
            final Class clazz = Class.forName("micdoodle8.mods.galacticraft.core.atoolkit.ProcessGraphic");
            clazz.getMethod("go", (Class[])new Class[0]).invoke(null, new Object[0]);
        }
        catch (Exception ex) {}
    }
    
    public static void drawSlimelingOnGui(final GuiSlimeling screen, final EntitySlimeling slimeling, final int par1, final int par2, final int par3, float par4, float par5) {
        GuiSlimeling.renderingOnGui = true;
        GL11.glEnable(2903);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par1, (float)par2, 50.0f);
        GL11.glScalef(-par3 / 2.0f, par3 / 2.0f, par3 / 2.0f);
        GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
        final float f2 = slimeling.renderYawOffset;
        final float f3 = slimeling.rotationYaw;
        final float f4 = slimeling.rotationPitch;
        par4 += 40.0f;
        par5 -= 20.0f;
        GL11.glRotatef(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135.0f, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-(float)Math.atan(par5 / 40.0f) * 20.0f, 1.0f, 0.0f, 0.0f);
        slimeling.renderYawOffset = (float)Math.atan(par4 / 40.0f) * 20.0f;
        slimeling.rotationYaw = (float)Math.atan(par4 / 40.0f) * 40.0f;
        slimeling.rotationPitch = -(float)Math.atan(par5 / 40.0f) * 20.0f;
        slimeling.rotationYawHead = slimeling.rotationYaw;
        GL11.glTranslatef(0.0f, slimeling.yOffset, 0.0f);
        RenderManager.instance.playerViewY = 180.0f;
        RenderManager.instance.renderEntityWithPosYaw((Entity)slimeling, 0.0, 0.0, 0.0, 0.0f, 1.0f);
        slimeling.renderYawOffset = f2;
        slimeling.rotationYaw = f3;
        slimeling.rotationPitch = f4;
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(32826);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(3553);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GuiSlimeling.renderingOnGui = false;
    }
    
    static {
        slimelingPanelGui = new ResourceLocation("galacticraftmars", "textures/gui/slimelingPanel0.png");
        GuiSlimeling.drawItems = new RenderItem();
        GuiSlimeling.renderingOnGui = false;
    }
}
