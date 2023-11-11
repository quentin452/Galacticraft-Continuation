package micdoodle8.mods.galacticraft.planets.mars.client.gui;

import net.minecraft.client.gui.inventory.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.planets.mars.entities.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.planets.mars.inventory.*;
import net.minecraft.inventory.*;
import net.minecraft.client.*;
import net.minecraft.client.audio.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.gui.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.planets.mars.items.*;
import net.minecraft.item.*;

public class GuiSlimelingInventory extends GuiContainer
{
    private static final ResourceLocation slimelingPanelGui;
    private final EntitySlimeling slimeling;
    public static RenderItem drawItems;
    private int invX;
    private int invY;
    private final int invWidth = 18;
    private final int invHeight = 18;
    
    public GuiSlimelingInventory(final EntityPlayer player, final EntitySlimeling slimeling) {
        super((Container)new ContainerSlimeling(player.inventory, slimeling));
        this.slimeling = slimeling;
        this.xSize = 176;
        this.ySize = 210;
    }
    
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        this.invX = var5 + 151;
        this.invY = var6 + 108;
    }
    
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    protected void actionPerformed(final GuiButton par1GuiButton) {
        if (par1GuiButton.enabled) {
            switch (par1GuiButton.id) {
            }
        }
    }
    
    protected void mouseClicked(final int px, final int py, final int par3) {
        if (px >= this.invX) {
            final int invX = this.invX;
            this.getClass();
            if (px < invX + 18 && py >= this.invY) {
                final int invY = this.invY;
                this.getClass();
                if (py < invY + 18) {
                    Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0f));
                    this.mc.displayGuiScreen((GuiScreen)new GuiSlimeling(this.slimeling));
                }
            }
        }
        super.mouseClicked(px, py, par3);
    }
    
    public static void drawSlimelingOnGui(final GuiSlimelingInventory screen, final EntitySlimeling slimeling, final int par1, final int par2, final int par3, float par4, float par5) {
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
    
    public void drawScreen(final int par1, final int par2, final float par3) {
        GL11.glPushMatrix();
        super.drawScreen(par1, par2, par3);
        GL11.glPopMatrix();
    }
    
    protected void drawGuiContainerBackgroundLayer(final float f, final int i, final int j) {
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        GL11.glPushMatrix();
        Gui.drawRect(var5, var6, var5 + this.xSize, var6 + this.ySize, -16777216);
        GL11.glPopMatrix();
        final int yOffset = (int)Math.floor(30.0 * (1.0f - this.slimeling.getScale()));
        drawSlimelingOnGui(this, this.slimeling, this.width / 2, var6 + 62 - yOffset, 70, (float)(var5 + 51 - i), (float)(var6 + 75 - 50 - j));
        GL11.glTranslatef(0.0f, 0.0f, 100.0f);
        GL11.glPushMatrix();
        this.mc.renderEngine.bindTexture(GuiSlimelingInventory.slimelingPanelGui);
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
        this.mc.renderEngine.bindTexture(GuiSlimelingInventory.slimelingPanelGui);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        final int invX = this.invX;
        final int invY = this.invY;
        final int n = 176;
        final int n2 = 27;
        this.getClass();
        final int n3 = 18;
        this.getClass();
        this.drawTexturedModalRect(invX, invY, n, n2, n3, 18);
        this.drawTexturedModalRect(var5 + 8, var6 + 8, 176, 9, 18, 18);
        this.drawTexturedModalRect(var5 + 8, var6 + 29, 176, 9, 18, 18);
        final ItemStack stack = this.slimeling.getCargoSlot();
        if (stack != null && stack.getItem() == MarsItems.marsItemBasic && stack.getItemDamage() == 4) {
            final int offsetX = 7;
            final int offsetY = 53;
            for (int y = 0; y < 3; ++y) {
                for (int x = 0; x < 9; ++x) {
                    this.drawTexturedModalRect(var5 + offsetX + x * 18, var6 + offsetY + y * 18, 176, 9, 18, 18);
                }
            }
        }
        GL11.glPopMatrix();
    }
    
    static {
        slimelingPanelGui = new ResourceLocation("galacticraftmars", "textures/gui/slimelingPanel2.png");
        GuiSlimelingInventory.drawItems = new RenderItem();
    }
}
