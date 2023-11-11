package micdoodle8.mods.galacticraft.core.client.gui.container;

import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.client.gui.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.*;

public class GuiSchematicTier1Rocket extends GuiContainerGC implements ISchematicResultPage
{
    private static final ResourceLocation rocketBenchTexture;
    private int pageIndex;
    
    public GuiSchematicTier1Rocket(final InventoryPlayer par1InventoryPlayer, final int x, final int y, final int z) {
        super((Container)new ContainerSchematicTier1Rocket(par1InventoryPlayer, x, y, z));
        this.ySize = 221;
    }
    
    public void initGui() {
        super.initGui();
        final GuiButton backButton;
        this.buttonList.add(backButton = new GuiButton(0, this.width / 2 - 130, this.height / 2 - 30 + 27 - 12, 40, 20, GCCoreUtil.translate("gui.button.back.name")));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 130, this.height / 2 - 30 + 27 + 12, 40, 20, GCCoreUtil.translate("gui.button.next.name")));
        backButton.enabled = false;
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
        this.fontRendererObj.drawString(GCCoreUtil.translate("schematic.rocketT1.name"), 7, 7, 4210752);
        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, 127, 4210752);
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(GuiSchematicTier1Rocket.rocketBenchTexture);
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
    }
    
    public void setPageIndex(final int index) {
        this.pageIndex = index;
    }
    
    static {
        rocketBenchTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/rocketbench.png");
    }
}
