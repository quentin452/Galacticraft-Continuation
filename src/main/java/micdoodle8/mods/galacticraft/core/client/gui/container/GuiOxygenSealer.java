package micdoodle8.mods.galacticraft.core.client.gui.container;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion;
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper;
import micdoodle8.mods.galacticraft.core.inventory.ContainerOxygenSealer;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.oxygen.OxygenPressureProtocol;
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenSealer;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

public class GuiOxygenSealer extends GuiContainerGC {

    private static final ResourceLocation sealerTexture = new ResourceLocation(
        GalacticraftCore.ASSET_PREFIX,
        "textures/gui/oxygen_sealer.png");

    private final TileEntityOxygenSealer sealer;
    private GuiButton buttonDisable;

    private final GuiElementInfoRegion oxygenInfoRegion = new GuiElementInfoRegion(
        (this.width - this.xSize) / 2 + 112,
        (this.height - this.ySize) / 2 + 24,
        56,
        9,
        new ArrayList<String>(),
        this.width,
        this.height,
        this);
    private final GuiElementInfoRegion electricInfoRegion = new GuiElementInfoRegion(
        (this.width - this.xSize) / 2 + 112,
        (this.height - this.ySize) / 2 + 37,
        56,
        9,
        new ArrayList<String>(),
        this.width,
        this.height,
        this);

    public GuiOxygenSealer(InventoryPlayer par1InventoryPlayer, TileEntityOxygenSealer par2TileEntityAirDistributor) {
        super(new ContainerOxygenSealer(par1InventoryPlayer, par2TileEntityAirDistributor));
        this.sealer = par2TileEntityAirDistributor;
        this.ySize = 200;
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        switch (par1GuiButton.id) {
            case 0:
                GalacticraftCore.packetPipeline.sendToServer(
                    new PacketSimple(
                        EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON,
                        new Object[] { this.sealer.xCoord, this.sealer.yCoord, this.sealer.zCoord, 0 }));
                break;
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        final List<String> batterySlotDesc = new ArrayList<>();
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.0"));
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.1"));
        this.infoRegions.add(
            new GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 32,
                (this.height - this.ySize) / 2 + 26,
                18,
                18,
                batterySlotDesc,
                this.width,
                this.height,
                this));
        final List<String> oxygenSlotDesc = new ArrayList<>();
        oxygenSlotDesc.add(GCCoreUtil.translate("gui.oxygenSlot.desc.0"));
        oxygenSlotDesc.add(GCCoreUtil.translate("gui.oxygenSlot.desc.1"));
        this.infoRegions.add(
            new GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 9,
                (this.height - this.ySize) / 2 + 26,
                18,
                18,
                oxygenSlotDesc,
                this.width,
                this.height,
                this));
        final List<String> ambientThermalDesc = new ArrayList<>();
        ambientThermalDesc.add(GCCoreUtil.translate("gui.thermalSlot.desc.0"));
        ambientThermalDesc.add(GCCoreUtil.translate("gui.thermalSlot.desc.1"));
        ambientThermalDesc.add(GCCoreUtil.translate("gui.thermalSlot.desc.2"));
        this.infoRegions.add(
            new GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 55,
                (this.height - this.ySize) / 2 + 26,
                18,
                18,
                ambientThermalDesc,
                this.width,
                this.height,
                this));
        final List<String> oxygenDesc = new ArrayList<>();
        oxygenDesc.add(GCCoreUtil.translate("gui.oxygenStorage.desc.0"));
        oxygenDesc.add(
            EnumColor.YELLOW + GCCoreUtil.translate("gui.oxygenStorage.desc.1")
                + ": "
                + ((int) Math.floor(this.sealer.storedOxygen) + " / " + (int) Math.floor(this.sealer.maxOxygen)));
        this.oxygenInfoRegion.tooltipStrings = oxygenDesc;
        this.oxygenInfoRegion.xPosition = (this.width - this.xSize) / 2 + 112;
        this.oxygenInfoRegion.yPosition = (this.height - this.ySize) / 2 + 23;
        this.oxygenInfoRegion.parentWidth = this.width;
        this.oxygenInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.oxygenInfoRegion);
        final List<String> electricityDesc = new ArrayList<>();
        electricityDesc.add(GCCoreUtil.translate("gui.energyStorage.desc.0"));
        electricityDesc.add(
            EnumColor.YELLOW + GCCoreUtil.translate("gui.energyStorage.desc.1")
                + ": "
                + ((int) Math.floor(this.sealer.getEnergyStoredGC()) + " / "
                    + (int) Math.floor(this.sealer.getMaxEnergyStoredGC())));
        this.electricInfoRegion.tooltipStrings = electricityDesc;
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 112;
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 36;
        this.electricInfoRegion.parentWidth = this.width;
        this.electricInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.electricInfoRegion);
        this.buttonList.add(
            this.buttonDisable = new GuiButton(
                0,
                this.width / 2 - 38,
                this.height / 2 - 30 + 21,
                76,
                20,
                GCCoreUtil.translate("gui.button.enableseal.name")));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.sealer.getInventoryName(), 8, 10, 4210752);
        GCCoreUtil.drawStringRightAligned(
            GCCoreUtil.translate("gui.message.in.name") + ":",
            99,
            26,
            4210752,
            this.fontRendererObj);
        GCCoreUtil.drawStringRightAligned(
            GCCoreUtil.translate("gui.message.in.name") + ":",
            99,
            38,
            4210752,
            this.fontRendererObj);
        String status = GCCoreUtil.translate("gui.message.status.name") + ": " + this.getStatus();
        this.buttonDisable.enabled = this.sealer.disableCooldown == 0;
        this.buttonDisable.displayString = this.sealer.disabled ? GCCoreUtil.translate("gui.button.enableseal.name")
            : GCCoreUtil.translate("gui.button.disableseal.name");
        this.fontRendererObj
            .drawString(status, this.xSize / 2 - this.fontRendererObj.getStringWidth(status) / 2, 50, 4210752);
        int adjustedOxygenPerTick = (int) (this.sealer.oxygenPerTick * 20);
        if (this.sealer.disabled || this.sealer.getEnergyStoredGC() < this.sealer.storage.getMaxExtract()) {
            adjustedOxygenPerTick = 0;
        }
        status = GCCoreUtil.translate("gui.oxygenUse.desc") + ": "
            + adjustedOxygenPerTick
            + GCCoreUtil.translate("gui.perSecond");
        this.fontRendererObj
            .drawString(status, this.xSize / 2 - this.fontRendererObj.getStringWidth(status) / 2, 60, 4210752);
        status = GCCoreUtil.translate("gui.message.thermalStatus.name") + ": " + this.getThermalStatus();
        this.fontRendererObj
            .drawString(status, this.xSize / 2 - this.fontRendererObj.getStringWidth(status) / 2, 70, 4210752);
        // status = ElectricityDisplay.getDisplay(this.sealer.ueWattsPerTick * 20,
        // ElectricUnit.WATT);
        // this.fontRendererObj.drawString(status, this.xSize / 2 -
        // this.fontRendererObj.getStringWidth(status) / 2,
        // 70, 4210752);
        // status = ElectricityDisplay.getDisplay(this.sealer.getVoltage(),
        // ElectricUnit.VOLTAGE);
        // this.fontRendererObj.drawString(status, this.xSize / 2 -
        // this.fontRendererObj.getStringWidth(status) / 2,
        // 80, 4210752);
        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 90 + 3, 4210752);
    }

    private String getThermalStatus() {
        final Block blockAbove = this.sealer.getWorldObj()
            .getBlock(this.sealer.xCoord, this.sealer.yCoord + 1, this.sealer.zCoord);
        final int metadata = this.sealer.getWorldObj()
            .getBlockMetadata(this.sealer.xCoord, this.sealer.yCoord + 1, this.sealer.zCoord);

        if ((blockAbove == GCBlocks.breatheableAir || blockAbove == GCBlocks.brightBreatheableAir) && metadata == 1) {
            return EnumColor.DARK_GREEN + GCCoreUtil.translate("gui.status.on.name");
        }

        if (this.sealer.thermalControlEnabled()) {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.notAvailable.name");
        }

        return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.off.name");
    }

    private String getStatus() {
        final Block blockAbove = this.sealer.getWorldObj()
            .getBlock(this.sealer.xCoord, this.sealer.yCoord + 1, this.sealer.zCoord);

        if (blockAbove != null
            && !blockAbove
                .isAir(this.sealer.getWorldObj(), this.sealer.xCoord, this.sealer.yCoord + 1, this.sealer.zCoord)
            && !OxygenPressureProtocol.canBlockPassAir(
                this.sealer.getWorldObj(),
                blockAbove,
                new BlockVec3(this.sealer.xCoord, this.sealer.yCoord + 1, this.sealer.zCoord),
                1)) {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.sealerblocked.name");
        }

        // if (RedstoneUtil.isBlockReceivingRedstone(this.sealer.getWorldObj(),
        // this.sealer.xCoord,
        // this.sealer.yCoord, this.sealer.zCoord))
        // {
        // return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.off.name");
        // }

        if (this.sealer.disabled) {
            return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.disabled.name");
        }

        if (this.sealer.getEnergyStoredGC() == 0) {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.missingpower.name");
        }

        if (this.sealer.getEnergyStoredGC() < this.sealer.storage.getMaxExtract()) {
            return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.missingpower.name");
        }

        if (this.sealer.storedOxygen < 1) {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.missingoxygen.name");
        }

        if (this.sealer.calculatingSealed) {
            return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.checkingSeal.name") + "...";
        }

        final int threadCooldown = this.sealer.getScaledThreadCooldown(25);

        if (threadCooldown < 15) {
            if (threadCooldown >= 4) {
                return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.checkPending.name");
            }
            StringBuilder elipsis = new StringBuilder();
            for (int i = 0; i < (23 - threadCooldown) % 4; i++) {
                elipsis.append(".");
            }

            return EnumColor.ORANGE + GCCoreUtil.translate("gui.status.checkStarting.name") + elipsis.toString();
        }
        if (!this.sealer.sealed) {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.unsealed.name");
        }
        return EnumColor.DARK_GREEN + GCCoreUtil.translate("gui.status.sealed.name");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager()
            .bindTexture(GuiOxygenSealer.sealerTexture);
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6 + 5, 0, 0, this.xSize, this.ySize);

        if (this.sealer != null) {
            final List<String> oxygenDesc = new ArrayList<>();
            oxygenDesc.add(GCCoreUtil.translate("gui.oxygenStorage.desc.0"));
            oxygenDesc.add(
                EnumColor.YELLOW + GCCoreUtil.translate("gui.oxygenStorage.desc.1")
                    + ": "
                    + ((int) Math.floor(this.sealer.storedOxygen) + " / " + (int) Math.floor(this.sealer.maxOxygen)));
            this.oxygenInfoRegion.tooltipStrings = oxygenDesc;

            final List<String> electricityDesc = new ArrayList<>();
            electricityDesc.add(GCCoreUtil.translate("gui.energyStorage.desc.0"));
            EnergyDisplayHelper.getEnergyDisplayTooltip(
                this.sealer.getEnergyStoredGC(),
                this.sealer.getMaxEnergyStoredGC(),
                electricityDesc);
            // electricityDesc.add(EnumColor.YELLOW +
            // GCCoreUtil.translate("gui.energyStorage.desc.1") + ": " + ((int)
            // Math.floor(this.sealer.getEnergyStoredGC()) + " / " + (int)
            // Math.floor(this.sealer.getMaxEnergyStoredGC())));
            this.electricInfoRegion.tooltipStrings = electricityDesc;

            int scale = this.sealer.getCappedScaledOxygenLevel(54);
            this.drawTexturedModalRect(var5 + 113, var6 + 24, 197, 7, Math.min(scale, 54), 7);
            scale = this.sealer.getScaledElecticalLevel(54);
            this.drawTexturedModalRect(var5 + 113, var6 + 37, 197, 0, Math.min(scale, 54), 7);
            scale = 25 - this.sealer.getScaledThreadCooldown(25);
            this.drawTexturedModalRect(var5 + 148, var6 + 60, 176, 14, 10, 27);
            if (scale != 0) {
                this.drawTexturedModalRect(var5 + 149, var6 + 61 + scale, 186, 14, 8, 25 - scale);
            }

            if (this.sealer.getEnergyStoredGC() > 0) {
                this.drawTexturedModalRect(var5 + 99, var6 + 36, 176, 0, 11, 10);
            }

            if (this.sealer.storedOxygen > 0) {
                this.drawTexturedModalRect(var5 + 100, var6 + 23, 187, 0, 10, 10);
            }
        }
    }
}
