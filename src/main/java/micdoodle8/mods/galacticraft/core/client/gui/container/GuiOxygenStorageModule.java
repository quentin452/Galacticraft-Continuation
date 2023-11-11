package micdoodle8.mods.galacticraft.core.client.gui.container;

import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.client.gui.element.*;
import java.util.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.*;

@SideOnly(Side.CLIENT)
public class GuiOxygenStorageModule extends GuiContainerGC
{
    private static final ResourceLocation batteryBoxTexture;
    private TileEntityOxygenStorageModule tileEntity;
    
    public GuiOxygenStorageModule(final InventoryPlayer par1InventoryPlayer, final TileEntityOxygenStorageModule storageModule) {
        super((Container)new ContainerOxygenStorageModule(par1InventoryPlayer, storageModule));
        this.tileEntity = storageModule;
    }
    
    public void initGui() {
        super.initGui();
        final List<String> oxygenSlotDesc = new ArrayList<String>();
        oxygenSlotDesc.add(GCCoreUtil.translate("gui.oxygenSlot.desc.0"));
        oxygenSlotDesc.add(GCCoreUtil.translate("gui.oxygenSlot.desc.1"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 16, (this.height - this.ySize) / 2 + 21, 18, 18, oxygenSlotDesc, this.width, this.height, this));
    }
    
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        final String guiTitle = GCCoreUtil.translate("tile.machine2.6.name");
        this.fontRendererObj.drawString(guiTitle, this.xSize / 2 - this.fontRendererObj.getStringWidth(guiTitle) / 2, 6, 4210752);
        final String displayJoules = (int)(this.tileEntity.storedOxygen + 0.5f) + " " + GCCoreUtil.translate("gui.message.of.name");
        final String displayMaxJoules = "" + (int)this.tileEntity.maxOxygen;
        final String maxOutputLabel = GCCoreUtil.translate("gui.maxOutput.desc") + ": " + 10000 + GCCoreUtil.translate("gui.perSecond");
        this.fontRendererObj.drawString(displayJoules, 122 - this.fontRendererObj.getStringWidth(displayJoules) / 2 - 35, 30, 4210752);
        this.fontRendererObj.drawString(displayMaxJoules, 122 - this.fontRendererObj.getStringWidth(displayMaxJoules) / 2 - 35, 40, 4210752);
        this.fontRendererObj.drawString(maxOutputLabel, 122 - this.fontRendererObj.getStringWidth(maxOutputLabel) / 2 - 35, 60, 4210752);
        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        this.mc.renderEngine.bindTexture(GuiOxygenStorageModule.batteryBoxTexture);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int containerWidth = (this.width - this.xSize) / 2;
        final int containerHeight = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
        final int scale = (int)(this.tileEntity.storedOxygen / (double)this.tileEntity.maxOxygen * 72.0);
        this.drawTexturedModalRect(containerWidth + 52, containerHeight + 52, 176, 0, scale, 3);
    }
    
    static {
        batteryBoxTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/oxygenStorageModule.png");
    }
}
