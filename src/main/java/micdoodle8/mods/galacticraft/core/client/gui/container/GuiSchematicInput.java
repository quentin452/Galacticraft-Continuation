package micdoodle8.mods.galacticraft.core.client.gui.container;

import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.client.gui.element.*;
import net.minecraft.client.gui.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import org.lwjgl.opengl.*;

public class GuiSchematicInput extends GuiContainerGC implements ISchematicResultPage
{
    private static final ResourceLocation schematicInputTexture;
    private int pageIndex;
    
    public GuiSchematicInput(final InventoryPlayer par1InventoryPlayer, final int x, final int y, final int z) {
        super((Container)new ContainerSchematic(par1InventoryPlayer, x, y, z));
    }
    
    public void initGui() {
        super.initGui();
        final List<String> schematicSlotDesc = new ArrayList<String>();
        schematicSlotDesc.add(GCCoreUtil.translate("gui.newSchematic.slot.desc.0"));
        schematicSlotDesc.add(GCCoreUtil.translate("gui.newSchematic.slot.desc.1"));
        schematicSlotDesc.add(GCCoreUtil.translate("gui.newSchematic.slot.desc.2"));
        schematicSlotDesc.add(GCCoreUtil.translate("gui.newSchematic.slot.desc.3"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 79, (this.height - this.ySize) / 2, 18, 18, schematicSlotDesc, this.width, this.height, this));
        this.buttonList.add(new GuiButton(0, this.width / 2 - 130, this.height / 2 - 30 + 27 - 12, 40, 20, GCCoreUtil.translate("gui.button.back.name")));
        final GuiButton nextButton;
        this.buttonList.add(nextButton = new GuiButton(1, this.width / 2 - 130, this.height / 2 - 30 + 27 + 12, 40, 20, GCCoreUtil.translate("gui.button.next.name")));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 46, this.height / 2 - 52, 92, 20, GCCoreUtil.translate("gui.button.unlockschematic.name")));
        nextButton.enabled = false;
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
                case 2: {
                    GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(PacketSimple.EnumSimplePacket.S_UNLOCK_NEW_SCHEMATIC, new Object[0]));
                    break;
                }
            }
        }
    }
    
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        this.fontRendererObj.drawString(GCCoreUtil.translate("gui.message.addnewsch.name"), 7, -22, 4210752);
        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, 56, 4210752);
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(GuiSchematicInput.schematicInputTexture);
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - 220) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, 220);
    }
    
    public void setPageIndex(final int index) {
        this.pageIndex = index;
    }
    
    static {
        schematicInputTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/schematicpage.png");
    }
}
