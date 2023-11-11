package micdoodle8.mods.galacticraft.planets.asteroids.client.gui;

import net.minecraft.client.gui.inventory.*;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.planets.asteroids.inventory.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.client.gui.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import micdoodle8.mods.galacticraft.planets.asteroids.items.*;
import net.minecraft.item.*;
import org.lwjgl.opengl.*;

public class GuiSchematicTier3Rocket extends GuiContainer implements ISchematicResultPage
{
    private static final ResourceLocation tier2SchematicTexture;
    private int pageIndex;
    
    public GuiSchematicTier3Rocket(final InventoryPlayer par1InventoryPlayer, final int x, final int y, final int z) {
        super((Container)new ContainerSchematicTier3Rocket(par1InventoryPlayer, x, y, z));
        this.ySize = 238;
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
        this.fontRendererObj.drawString(AsteroidsItems.tier3Rocket.getItemStackDisplayName(new ItemStack(AsteroidsItems.tier3Rocket, 1, 0)), 7, 7, 4210752);
        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, 145, 4210752);
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(GuiSchematicTier3Rocket.tier2SchematicTexture);
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
    }
    
    public void setPageIndex(final int index) {
        this.pageIndex = index;
    }
    
    static {
        tier2SchematicTexture = new ResourceLocation("galacticraftasteroids", "textures/gui/schematic_rocket_T3.png");
    }
}
