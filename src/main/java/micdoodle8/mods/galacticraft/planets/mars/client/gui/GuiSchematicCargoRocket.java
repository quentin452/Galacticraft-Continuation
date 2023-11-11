package micdoodle8.mods.galacticraft.planets.mars.client.gui;

import net.minecraft.client.gui.inventory.*;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.planets.mars.inventory.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.client.gui.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import org.lwjgl.opengl.*;

public class GuiSchematicCargoRocket extends GuiContainer implements ISchematicResultPage
{
    private static final ResourceLocation cargoRocketTexture;
    private int pageIndex;
    
    public GuiSchematicCargoRocket(final InventoryPlayer par1InventoryPlayer, final int x, final int y, final int z) {
        super((Container)new ContainerSchematicCargoRocket(par1InventoryPlayer, x, y, z));
        this.ySize = 220;
    }
    
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 130, this.height / 2 - 30 + 27 - 12, 40, 20, GCCoreUtil.translate("gui.button.back.name")));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 130, this.height / 2 - 30 + 27 + 12, 40, 20, GCCoreUtil.translate("gui.button.next.name")));
    }
    
    protected void actionPerformed(final GuiButton par1GuiButton) {
        if (par1GuiButton.enabled) {
            switch (par1GuiButton.id) {
                case 0: {
                    SchematicRegistry.flipToLastPage(this.pageIndex);
                    break;
                }
                case 1: {
                    SchematicRegistry.flipToNextPage(this.pageIndex);
                    break;
                }
            }
        }
    }
    
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        this.fontRendererObj.drawString(GCCoreUtil.translate("item.spaceshipTier2.cargoRocket.name"), 7, 7, 4210752);
        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, 127, 4210752);
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(GuiSchematicCargoRocket.cargoRocketTexture);
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
    }
    
    public void setPageIndex(final int index) {
        this.pageIndex = index;
    }
    
    static {
        cargoRocketTexture = new ResourceLocation("galacticraftmars", "textures/gui/schematic_rocket_cargo.png");
    }
}
