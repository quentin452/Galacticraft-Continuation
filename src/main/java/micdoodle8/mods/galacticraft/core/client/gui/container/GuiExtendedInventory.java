package micdoodle8.mods.galacticraft.core.client.gui.container;

import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.core.client.gui.screen.*;
import net.minecraft.client.gui.*;
import org.lwjgl.opengl.*;
import tconstruct.client.tabs.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.*;
import net.minecraft.client.renderer.*;
import cpw.mods.fml.common.*;
import micdoodle8.mods.galacticraft.core.*;

public class GuiExtendedInventory extends InventoryEffectRenderer
{
    private static final ResourceLocation inventoryTexture;
    private float xSize_lo_2;
    private float ySize_lo_2;
    private int potionOffsetLast;
    private static float rotation;
    private boolean initWithPotion;

    public GuiExtendedInventory(final EntityPlayer entityPlayer, final InventoryExtended inventory) {
        super((Container)new ContainerExtendedInventory(entityPlayer, inventory));
    }

    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        drawPlayerOnGui(this.mc, 33, 75, 29, 51.0f - this.xSize_lo_2, 25.0f - this.ySize_lo_2);
    }

    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiLeft += this.getPotionOffset();
        this.potionOffsetLast = this.getPotionOffsetNEI();
        final int cornerX = this.guiLeft;
        final int cornerY = this.guiTop;
        TabRegistry.updateTabValues(cornerX, cornerY, (Class)InventoryTabGalacticraft.class);
        TabRegistry.addTabsToList(this.buttonList);
        this.buttonList.add(new GuiButton(0, this.guiLeft + 10, this.guiTop + 71, 7, 7, ""));
        this.buttonList.add(new GuiButton(1, this.guiLeft + 51, this.guiTop + 71, 7, 7, ""));
    }

    protected void actionPerformed(final GuiButton par1GuiButton) {
        switch (par1GuiButton.id) {
            case 0: {
                GuiExtendedInventory.rotation += 10.0f;
                break;
            }
            case 1: {
                GuiExtendedInventory.rotation -= 10.0f;
                break;
            }
        }
    }

    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(GuiExtendedInventory.inventoryTexture);
        final int k = this.guiLeft;
        final int l = this.guiTop;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }

    public void drawScreen(final int par1, final int par2, final float par3) {
        final int newPotionOffset = this.getPotionOffsetNEI();
        if (newPotionOffset < this.potionOffsetLast) {
            final int diff = newPotionOffset - this.potionOffsetLast;
            this.potionOffsetLast = newPotionOffset;
            this.guiLeft += diff;
            for (int k = 0; k < this.buttonList.size(); ++k) {
                final GuiButton b = (GuiButton) this.buttonList.get(k);
                if (!(b instanceof AbstractTab)) {
                    final GuiButton guiButton = b;
                    guiButton.xPosition += diff;
                }
            }
        }
        super.drawScreen(par1, par2, par3);
        this.xSize_lo_2 = (float)par1;
        this.ySize_lo_2 = (float)par2;
    }

    public static void drawPlayerOnGui(final Minecraft par0Minecraft, final int par1, final int par2, final int par3, float par4, final float par5) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par1, (float)par2, 50.0f);
        GL11.glScalef((float)(-par3), (float)par3, (float)par3);
        GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
        final float f2 = par0Minecraft.thePlayer.renderYawOffset;
        final float f3 = par0Minecraft.thePlayer.rotationYaw;
        final float f4 = par0Minecraft.thePlayer.rotationPitch;
        final float f5 = par0Minecraft.thePlayer.rotationYawHead;
        par4 -= 19.0f;
        GL11.glRotatef(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135.0f, 0.0f, 1.0f, 0.0f);
        par0Minecraft.thePlayer.renderYawOffset = GuiExtendedInventory.rotation;
        par0Minecraft.thePlayer.rotationYaw = (float)Math.atan(par4 / 40.0f) * 40.0f;
        par0Minecraft.thePlayer.rotationYaw = GuiExtendedInventory.rotation;
        par0Minecraft.thePlayer.rotationYawHead = par0Minecraft.thePlayer.rotationYaw;
        par0Minecraft.thePlayer.rotationPitch = (float)Math.sin(Minecraft.getSystemTime() / 500.0f) * 3.0f;
        GL11.glTranslatef(0.0f, par0Minecraft.thePlayer.yOffset, 0.0f);
        RenderManager.instance.playerViewY = 180.0f;
        RenderManager.instance.renderEntityWithPosYaw((Entity)par0Minecraft.thePlayer, 0.0, 0.0, 0.0, 0.0f, 1.0f);
        par0Minecraft.thePlayer.renderYawOffset = f2;
        par0Minecraft.thePlayer.rotationYaw = f3;
        par0Minecraft.thePlayer.rotationPitch = f4;
        par0Minecraft.thePlayer.rotationYawHead = f5;
        GL11.glPopMatrix();
        GL11.glEnable(32826);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(3553);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        RenderHelper.enableGUIStandardItemLighting();
    }

    public int getPotionOffset() {
        if (!Minecraft.getMinecraft().thePlayer.getActivePotionEffects().isEmpty()) {
            this.initWithPotion = true;
            return 60 + this.getPotionOffsetNEI();
        }
        this.initWithPotion = false;
        return 0;
    }

    public int getPotionOffsetNEI() {
        if (this.initWithPotion && Loader.isModLoaded("NotEnoughItems")) {
            try {
                final Class<?> c = Class.forName("codechicken.nei.NEIClientConfig");
                final Object hidden = c.getMethod("isHidden", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
                final Object enabled = c.getMethod("isEnabled", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
                if (hidden instanceof Boolean && enabled instanceof Boolean) {
                    if ((boolean)hidden || !(boolean)enabled) {
                        return 0;
                    }
                    return -60;
                }
            }
            catch (Exception ex) {}
        }
        return 0;
    }

    static {
        inventoryTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/inventory.png");
    }
}
