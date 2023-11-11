package micdoodle8.mods.galacticraft.planets.mars.client.gui;

import micdoodle8.mods.galacticraft.core.client.gui.container.*;
import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import micdoodle8.mods.galacticraft.core.client.gui.element.*;
import micdoodle8.mods.galacticraft.planets.mars.inventory.*;
import net.minecraft.inventory.*;
import cpw.mods.fml.client.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.*;
import java.util.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.energy.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.planets.mars.network.*;

public class GuiLaunchController extends GuiContainerGC implements GuiElementDropdown.IDropboxCallback, GuiElementTextBox.ITextBoxCallback, GuiElementCheckbox.ICheckBoxCallback
{
    private static final ResourceLocation launchControllerGui;
    private TileEntityLaunchController launchController;
    private GuiButton enableControllerButton;
    private GuiButton hideDestinationFrequency;
    private GuiElementCheckbox enablePadRemovalButton;
    private GuiElementCheckbox launchWhenCheckbox;
    private GuiElementDropdown dropdownTest;
    private GuiElementTextBox frequency;
    private GuiElementTextBox destinationFrequency;
    private GuiElementInfoRegion electricInfoRegion;
    private GuiElementInfoRegion waterTankInfoRegion;
    private int cannotEditTimer;

    public GuiLaunchController(final InventoryPlayer playerInventory, final TileEntityLaunchController launchController) {
        super((Container)new ContainerLaunchController(playerInventory, launchController));
        this.electricInfoRegion = new GuiElementInfoRegion(0, 0, 52, 9, (List)null, 0, 0, (GuiContainerGC)this);
        this.waterTankInfoRegion = new GuiElementInfoRegion(0, 0, 41, 28, (List)null, 0, 0, (GuiContainerGC)this);
        this.ySize = 209;
        this.launchController = launchController;
    }

    public void drawScreen(final int par1, final int par2, final float par3) {
        if (this.launchController.disableCooldown > 0) {
            this.enableControllerButton.enabled = false;
            this.enablePadRemovalButton.enabled = false;
            this.hideDestinationFrequency.enabled = false;
        }
        else {
            final boolean isOwner = FMLClientHandler.instance().getClient().thePlayer.getGameProfile().getName().equals(this.launchController.getOwnerName());
            this.enableControllerButton.enabled = isOwner;
            this.enablePadRemovalButton.enabled = isOwner;
            this.hideDestinationFrequency.enabled = isOwner;
        }
        this.enableControllerButton.displayString = (this.launchController.getDisabled(0) ? GCCoreUtil.translate("gui.button.enable.name") : GCCoreUtil.translate("gui.button.disable.name"));
        this.hideDestinationFrequency.displayString = (this.launchController.getDisabled(2) ? GCCoreUtil.translate("gui.button.hideDest.name") : GCCoreUtil.translate("gui.button.unhideDest.name"));
        final List<GuiButton> buttonList = new ArrayList<>(this.buttonList);
        final List<GuiLabel> labelList = new ArrayList<>(this.labelList);
        final List<GuiElementInfoRegion> infoRegions = new ArrayList<>(this.infoRegions);
        this.buttonList.clear();
        this.labelList.clear();
        this.infoRegions.clear();
        super.drawScreen(par1, par2, par3);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glDisable(32826);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(2896);
        GL11.glDisable(2929);
        for (GuiButton button : buttonList) {
            button.drawButton(this.mc, par1, par2);
        }
        for (GuiLabel label : labelList) {
            label.func_146159_a(this.mc, par1, par2);
        }
        for (GuiElementInfoRegion infoRegion : infoRegions) {
            infoRegion.drawRegion(par1, par2);
        }
        this.buttonList = buttonList;
        this.labelList = labelList;
        this.infoRegions = infoRegions;
        GL11.glEnable(2896);
        GL11.glEnable(2929);
        RenderHelper.enableStandardItemLighting();
        if (Math.random() < 0.025 && !this.destinationFrequency.isTextFocused) {
            if (!Minecraft.getMinecraft().thePlayer.getGameProfile().getName().equals(this.launchController.getOwnerName()) && !this.launchController.getDisabled(2)) {
                final Random r = new Random();
                String fakefrequency = "";
                for (int i = 0; i < this.destinationFrequency.getMaxLength(); ++i) {
                    fakefrequency += (char)(r.nextInt(93) + 33);
                }
                this.destinationFrequency.text = fakefrequency;
            }
            else {
                this.destinationFrequency.text = String.valueOf(this.launchController.destFrequency);
            }
        }
    }

    protected void keyTyped(final char keyChar, final int keyID) {
        if (keyID != 1 && keyID != this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            if (this.frequency.keyTyped(keyChar, keyID)) {
                return;
            }
            if (this.destinationFrequency.keyTyped(keyChar, keyID)) {
                return;
            }
        }
        super.keyTyped(keyChar, keyID);
    }

    public boolean isValid(final String string) {
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

    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        this.enableControllerButton = new GuiButton(0, var5 + 70 + 124 - 72, var6 + 16, 48, 20, GCCoreUtil.translate("gui.button.enable.name"));
        this.enablePadRemovalButton = new GuiElementCheckbox(1, (GuiElementCheckbox.ICheckBoxCallback)this, this.width / 2 - 78, var6 + 59, GCCoreUtil.translate("gui.message.removePad.name"));
        this.launchWhenCheckbox = new GuiElementCheckbox(2, (GuiElementCheckbox.ICheckBoxCallback)this, this.width / 2 - 78, var6 + 77, GCCoreUtil.translate("gui.message.launchWhen.name") + ": ");
        this.dropdownTest = new GuiElementDropdown(3, (GuiElementDropdown.IDropboxCallback)this, var5 + 95, var6 + 77, new String[] { EntityAutoRocket.EnumAutoLaunch.CARGO_IS_UNLOADED.getTitle(), EntityAutoRocket.EnumAutoLaunch.CARGO_IS_FULL.getTitle(), EntityAutoRocket.EnumAutoLaunch.ROCKET_IS_FUELED.getTitle(), EntityAutoRocket.EnumAutoLaunch.INSTANT.getTitle(), EntityAutoRocket.EnumAutoLaunch.TIME_10_SECONDS.getTitle(), EntityAutoRocket.EnumAutoLaunch.TIME_30_SECONDS.getTitle(), EntityAutoRocket.EnumAutoLaunch.TIME_1_MINUTE.getTitle(), EntityAutoRocket.EnumAutoLaunch.REDSTONE_SIGNAL.getTitle() });
        this.frequency = new GuiElementTextBox(4, (GuiElementTextBox.ITextBoxCallback)this, var5 + 66, var6 + 16, 48, 20, "", true, 6, false);
        this.destinationFrequency = new GuiElementTextBox(5, (GuiElementTextBox.ITextBoxCallback)this, var5 + 45, var6 + 16 + 22, 48, 20, "", true, 6, false);
        this.hideDestinationFrequency = new GuiButton(6, var5 + 95, var6 + 16 + 22, 39, 20, GCCoreUtil.translate("gui.button.hideDest.name"));
        this.buttonList.add(this.enableControllerButton);
        this.buttonList.add(this.enablePadRemovalButton);
        this.buttonList.add(this.launchWhenCheckbox);
        this.buttonList.add(this.dropdownTest);
        this.buttonList.add(this.frequency);
        this.buttonList.add(this.destinationFrequency);
        this.buttonList.add(this.hideDestinationFrequency);
        this.electricInfoRegion.tooltipStrings = new ArrayList();
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 98;
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 113;
        this.electricInfoRegion.parentWidth = this.width;
        this.electricInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.electricInfoRegion);
        List<String> batterySlotDesc = new ArrayList<String>();
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.0"));
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.1"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 151, (this.height - this.ySize) / 2 + 104, 18, 18, (List)batterySlotDesc, this.width, this.height, (GuiContainerGC)this));
        batterySlotDesc = new ArrayList<String>();
        batterySlotDesc.addAll(GCCoreUtil.translateWithSplit("gui.launchController.desc.0"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 5, (this.height - this.ySize) / 2 + 20, 109, 13, (List)batterySlotDesc, this.width, this.height, (GuiContainerGC)this));
        batterySlotDesc = new ArrayList<String>();
        batterySlotDesc.addAll(GCCoreUtil.translateWithSplit("gui.launchController.desc.1"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 5, (this.height - this.ySize) / 2 + 42, 88, 13, (List)batterySlotDesc, this.width, this.height, (GuiContainerGC)this));
        batterySlotDesc = new ArrayList<String>();
        batterySlotDesc.addAll(GCCoreUtil.translateWithSplit("gui.launchController.desc.2"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 10, (this.height - this.ySize) / 2 + 59, 78, 13, (List)batterySlotDesc, this.width, this.height, (GuiContainerGC)this));
        batterySlotDesc = new ArrayList<String>();
        batterySlotDesc.addAll(GCCoreUtil.translateWithSplit("gui.launchController.desc.3"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 10, (this.height - this.ySize) / 2 + 77, 82, 13, (List)batterySlotDesc, this.width, this.height, (GuiContainerGC)this));
        batterySlotDesc = new ArrayList<String>();
        batterySlotDesc.addAll(GCCoreUtil.translateWithSplit("gui.launchController.desc.4"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 95, (this.height - this.ySize) / 2 + 38, 38, 20, (List)batterySlotDesc, this.width, this.height, (GuiContainerGC)this));
    }

    protected void mouseClicked(final int px, final int py, final int par3) {
        super.mouseClicked(px, py, par3);
    }

    protected void actionPerformed(final GuiButton par1GuiButton) {
        if (!FMLClientHandler.instance().getClient().thePlayer.getGameProfile().getName().equals(this.launchController.getOwnerName())) {
            this.onIntruderInteraction();
            return;
        }
        if (par1GuiButton.enabled) {
            switch (par1GuiButton.id) {
                case 0: {
                    GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON, new Object[] { this.launchController.xCoord, this.launchController.yCoord, this.launchController.zCoord, 0 }));
                    break;
                }
                case 6: {
                    GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON, new Object[] { this.launchController.xCoord, this.launchController.yCoord, this.launchController.zCoord, 2 }));
                    break;
                }
            }
        }
    }

    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        String displayString = this.launchController.getOwnerName() + "'s " + this.launchController.getInventoryName();
        this.fontRendererObj.drawString(displayString, this.xSize / 2 - this.fontRendererObj.getStringWidth(displayString) / 2, 5, 4210752);
        if (this.cannotEditTimer > 0) {
            this.fontRendererObj.drawString(this.launchController.getOwnerName(), this.xSize / 2 - this.fontRendererObj.getStringWidth(displayString) / 2, 5, (this.cannotEditTimer % 30 < 15) ? ColorUtil.to32BitColor(255, 255, 100, 100) : 4210752);
            --this.cannotEditTimer;
        }
        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, 115, 4210752);
        displayString = this.getStatus();
        this.fontRendererObj.drawSplitString(displayString, 60 - this.fontRendererObj.getStringWidth(displayString) / 2, 94, 60, 4210752);
        this.fontRendererObj.drawString(GCCoreUtil.translate("gui.message.frequency.name") + ":", 7, 22, 4210752);
        this.fontRendererObj.drawString(GCCoreUtil.translate("gui.message.destFrequency.name") + ":", 7, 44, 4210752);
    }

    private String getStatus() {
        if (!this.launchController.frequencyValid) {
            return EnumColor.RED + GCCoreUtil.translate("gui.message.invalidFreq.name");
        }
        if (this.launchController.getEnergyStoredGC() <= 0.0f) {
            return EnumColor.RED + GCCoreUtil.translate("gui.message.noEnergy.name");
        }
        if (this.launchController.getDisabled(0)) {
            return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.disabled.name");
        }
        return EnumColor.BRIGHT_GREEN + GCCoreUtil.translate("gui.status.active.name");
    }

    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(GuiLaunchController.launchControllerGui);
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        final List<String> electricityDesc = new ArrayList<String>();
        electricityDesc.add(GCCoreUtil.translate("gui.energyStorage.desc.0"));
        EnergyDisplayHelper.getEnergyDisplayTooltip(this.launchController.getEnergyStoredGC(), this.launchController.getMaxEnergyStoredGC(), (List)electricityDesc);
        this.electricInfoRegion.tooltipStrings = electricityDesc;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.launchController.getEnergyStoredGC() > 0.0f) {
            final int scale = this.launchController.getScaledElecticalLevel(54);
            this.drawTexturedModalRect(var5 + 99, var6 + 114, 176, 0, Math.min(scale, 54), 7);
        }
        GL11.glPopMatrix();
    }

    public boolean canBeClickedBy(final GuiElementDropdown dropdown, final EntityPlayer player) {
        return dropdown.equals(this.dropdownTest) && player.getGameProfile().getName().equals(this.launchController.getOwnerName());
    }

    public void onSelectionChanged(final GuiElementDropdown dropdown, final int selection) {
        if (dropdown.equals(this.dropdownTest)) {
            this.launchController.launchDropdownSelection = selection;
            GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimpleMars(PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_ADVANCED_GUI, new Object[] { 1, this.launchController.xCoord, this.launchController.yCoord, this.launchController.zCoord, this.launchController.launchDropdownSelection }));
        }
    }

    public int getInitialSelection(final GuiElementDropdown dropdown) {
        if (dropdown.equals(this.dropdownTest)) {
            return this.launchController.launchDropdownSelection;
        }
        return 0;
    }

    public boolean canPlayerEdit(final GuiElementTextBox textBox, final EntityPlayer player) {
        return player.getGameProfile().getName().equals(this.launchController.getOwnerName());
    }

    public void onTextChanged(final GuiElementTextBox textBox, final String newText) {
        if (FMLClientHandler.instance().getClient().thePlayer.getGameProfile().getName().equals(this.launchController.getOwnerName())) {
            if (textBox.equals(this.frequency)) {
                this.launchController.frequency = textBox.getIntegerValue();
                GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimpleMars(PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_ADVANCED_GUI, new Object[] { 0, this.launchController.xCoord, this.launchController.yCoord, this.launchController.zCoord, this.launchController.frequency }));
            }
            else if (textBox.equals(this.destinationFrequency)) {
                this.launchController.destFrequency = textBox.getIntegerValue();
                GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimpleMars(PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_ADVANCED_GUI, new Object[] { 2, this.launchController.xCoord, this.launchController.yCoord, this.launchController.zCoord, this.launchController.destFrequency }));
            }
        }
    }

    public String getInitialText(final GuiElementTextBox textBox) {
        if (textBox.equals(this.frequency)) {
            return String.valueOf(this.launchController.frequency);
        }
        if (!textBox.equals(this.destinationFrequency)) {
            return "";
        }
        if (Minecraft.getMinecraft().thePlayer.getGameProfile().getName().equals(this.launchController.getOwnerName()) || this.launchController.getDisabled(2)) {
            return String.valueOf(this.launchController.destFrequency);
        }
        final Random r = new Random();
        String fakefrequency = "";
        for (int i = 0; i < this.destinationFrequency.getMaxLength(); ++i) {
            fakefrequency += (char)(r.nextInt(93) + 33);
        }
        return fakefrequency;
    }

    public int getTextColor(final GuiElementTextBox textBox) {
        if (textBox.equals(this.frequency)) {
            return this.launchController.frequencyValid ? ColorUtil.to32BitColor(255, 20, 255, 20) : ColorUtil.to32BitColor(255, 255, 25, 25);
        }
        if (textBox.equals(this.destinationFrequency)) {
            return this.launchController.destFrequencyValid ? ColorUtil.to32BitColor(255, 20, 255, 20) : ColorUtil.to32BitColor(255, 255, 25, 25);
        }
        return 0;
    }

    public void onSelectionChanged(final GuiElementCheckbox checkbox, final boolean newSelected) {
        if (checkbox.equals(this.enablePadRemovalButton)) {
            this.launchController.launchPadRemovalDisabled = !newSelected;
            GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimpleMars(PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_ADVANCED_GUI, new Object[] { 3, this.launchController.xCoord, this.launchController.yCoord, this.launchController.zCoord, this.launchController.launchPadRemovalDisabled ? 1 : 0 }));
        }
        else if (checkbox.equals(this.launchWhenCheckbox)) {
            this.launchController.launchSchedulingEnabled = newSelected;
            GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimpleMars(PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_ADVANCED_GUI, new Object[] { 4, this.launchController.xCoord, this.launchController.yCoord, this.launchController.zCoord, this.launchController.launchSchedulingEnabled ? 1 : 0 }));
        }
    }

    public boolean canPlayerEdit(final GuiElementCheckbox checkbox, final EntityPlayer player) {
        return player.getGameProfile().getName().equals(this.launchController.getOwnerName());
    }

    public boolean getInitiallySelected(final GuiElementCheckbox checkbox) {
        if (checkbox.equals(this.enablePadRemovalButton)) {
            return !this.launchController.launchPadRemovalDisabled;
        }
        return checkbox.equals(this.launchWhenCheckbox) && this.launchController.launchSchedulingEnabled;
    }

    public void onIntruderInteraction() {
        this.cannotEditTimer = 50;
    }

    public void onIntruderInteraction(final GuiElementTextBox textBox) {
        this.cannotEditTimer = 50;
    }

    static {
        launchControllerGui = new ResourceLocation("galacticraftmars", "textures/gui/launchController.png");
    }
}
