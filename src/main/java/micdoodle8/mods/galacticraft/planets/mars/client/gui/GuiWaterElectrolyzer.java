package micdoodle8.mods.galacticraft.planets.mars.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiContainerGC;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion;
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.RedstoneUtil;
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule;
import micdoodle8.mods.galacticraft.planets.mars.MarsModule;
import micdoodle8.mods.galacticraft.planets.mars.inventory.ContainerElectrolyzer;
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityElectrolyzer;

@SideOnly(Side.CLIENT)
public class GuiWaterElectrolyzer extends GuiContainerGC {

    private static final ResourceLocation refineryTexture = new ResourceLocation(
        MarsModule.ASSET_PREFIX,
        "textures/gui/gasLiquefier.png");

    private static final ResourceLocation gasTextures = new ResourceLocation(
        AsteroidsModule.ASSET_PREFIX,
        "textures/gui/gasesMethaneOxygenNitrogen.png");

    private final TileEntityElectrolyzer tileEntity;

    private GuiButton buttonDisable;

    private final GuiElementInfoRegion fuelTankRegion = new GuiElementInfoRegion(
        (this.width - this.xSize) / 2 + 153,
        (this.height - this.ySize) / 2 + 28,
        16,
        38,
        new ArrayList<String>(),
        this.width,
        this.height,
        this);
    private final GuiElementInfoRegion fuelTank2Region = new GuiElementInfoRegion(
        (this.width - this.xSize) / 2 + 153,
        (this.height - this.ySize) / 2 + 28,
        16,
        38,
        new ArrayList<String>(),
        this.width,
        this.height,
        this);
    private final GuiElementInfoRegion gasTankRegion = new GuiElementInfoRegion(
        (this.width - this.xSize) / 2 + 7,
        (this.height - this.ySize) / 2 + 28,
        16,
        38,
        new ArrayList<String>(),
        this.width,
        this.height,
        this);
    private final GuiElementInfoRegion electricInfoRegion = new GuiElementInfoRegion(
        (this.width - this.xSize) / 2 + 62,
        (this.height - this.ySize) / 2 + 16,
        56,
        9,
        new ArrayList<String>(),
        this.width,
        this.height,
        this);

    public GuiWaterElectrolyzer(InventoryPlayer par1InventoryPlayer, TileEntityElectrolyzer tileEntity) {
        super(new ContainerElectrolyzer(par1InventoryPlayer, tileEntity));
        this.tileEntity = tileEntity;
        this.ySize = 168;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.gasTankRegion.xPosition = (this.width - this.xSize) / 2 + 7;
        this.gasTankRegion.yPosition = (this.height - this.ySize) / 2 + 28;
        this.gasTankRegion.parentWidth = this.width;
        this.gasTankRegion.parentHeight = this.height;
        this.infoRegions.add(this.gasTankRegion);

        final List<String> batterySlotDesc = new ArrayList<>();
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.0"));
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.1"));
        this.infoRegions.add(
            new GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 33,
                (this.height - this.ySize) / 2 + 49,
                18,
                18,
                batterySlotDesc,
                this.width,
                this.height,
                this));

        this.fuelTankRegion.xPosition = (this.width - this.xSize) / 2 + 132;
        this.fuelTankRegion.yPosition = (this.height - this.ySize) / 2 + 28;
        this.fuelTankRegion.parentWidth = this.width;
        this.fuelTankRegion.parentHeight = this.height;
        this.infoRegions.add(this.fuelTankRegion);

        this.fuelTank2Region.xPosition = (this.width - this.xSize) / 2 + 153;
        this.fuelTank2Region.yPosition = (this.height - this.ySize) / 2 + 28;
        this.fuelTank2Region.parentWidth = this.width;
        this.fuelTank2Region.parentHeight = this.height;
        this.infoRegions.add(this.fuelTank2Region);

        // this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 +
        // 152, (this.height - this.ySize)
        // / 2 + 6, 18, 18, fuelSlotDesc, this.width, this.height, this));
        // this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 +
        // 131, (this.height - this.ySize)
        // / 2 + 6, 18, 18, fuelSlotDesc, this.width, this.height, this));

        final List<String> fuelSlotDesc = new ArrayList<>(GCCoreUtil.translateWithSplit("gui.waterBucketSlot.desc"));
        this.infoRegions.add(
            new GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 6,
                (this.height - this.ySize) / 2 + 6,
                18,
                18,
                fuelSlotDesc,
                this.width,
                this.height,
                this));

        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 42;
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 16;
        this.electricInfoRegion.parentWidth = this.width;
        this.electricInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.electricInfoRegion);

        this.addToolTips();

        this.buttonList.add(
            this.buttonDisable = new GuiButton(
                0,
                this.width / 2 - 49,
                this.height / 2 - 56,
                76,
                20,
                GCCoreUtil.translate("gui.button.liquefy.name")));
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        switch (par1GuiButton.id) {
            case 0:
                GalacticraftCore.packetPipeline.sendToServer(
                    new PacketSimple(
                        EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON,
                        new Object[] { this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord, 0 }));
                break;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.tileEntity.getInventoryName(), 30, 5, 4210752);
        String displayText = "";
        final int yOffset = -18;

        if (RedstoneUtil.isBlockReceivingRedstone(
            this.tileEntity.getWorldObj(),
            this.tileEntity.xCoord,
            this.tileEntity.yCoord,
            this.tileEntity.zCoord)) {
            displayText = EnumColor.RED + GCCoreUtil.translate("gui.status.off.name");
        } else if (!this.tileEntity.hasEnoughEnergyToRun) {
            displayText = EnumColor.RED + GCCoreUtil.translate("gui.message.lowEnergy.name");
        } else if (this.tileEntity.waterTank.getFluid() == null || this.tileEntity.waterTank.getFluidAmount() == 0) {
            displayText = EnumColor.RED + GCCoreUtil.translate("gui.message.zeroWater.name");
        } else if (this.tileEntity.waterTank.getFluidAmount() > 0 && this.tileEntity.disabled) {
            displayText = EnumColor.ORANGE + GCCoreUtil.translate("gui.status.ready.name");
        } else if (this.tileEntity.canProcess()) {
            displayText = EnumColor.BRIGHT_GREEN + GCCoreUtil.translate("gui.status.running.name");
        } else if (this.tileEntity.liquidTank.getFluidAmount() == this.tileEntity.liquidTank.getCapacity()
            && this.tileEntity.liquidTank2.getFluidAmount() == this.tileEntity.liquidTank2.getCapacity()) {
                displayText = EnumColor.RED + GCCoreUtil.translate("gui.status.tanksfull.name");
            } else {
                displayText = EnumColor.RED + GCCoreUtil.translate("gui.status.unknown.name");
            }

        this.buttonDisable.enabled = this.tileEntity.disableCooldown == 0;
        this.buttonDisable.displayString = this.tileEntity.processTicks == 0
            ? GCCoreUtil.translate("gui.button.liquefy.name")
            : GCCoreUtil.translate("gui.button.liquefyStop.name");
        this.fontRendererObj
            .drawString(GCCoreUtil.translate("gui.message.status.name") + ":", 56, 45 + 23 + yOffset, 4210752);
        this.fontRendererObj.drawString(displayText, 62, 45 + 33 + yOffset, 4210752);
        // this.fontRendererObj.drawString(ElectricityDisplay.getDisplay(this.tileEntity.ueWattsPerTick
        // * 20,
        // ElectricUnit.WATT), 72, 56 + 23 + yOffset, 4210752);
        // this.fontRendererObj.drawString(ElectricityDisplay.getDisplay(this.tileEntity.getVoltage(),
        // ElectricUnit.VOLTAGE), 72, 68 + 23 + yOffset, 4210752);
        this.fontRendererObj
            .drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 118 + 2 + 23, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        this.mc.renderEngine.bindTexture(GuiWaterElectrolyzer.refineryTexture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        final int edgeLeft = (this.width - this.xSize) / 2;
        final int edgeTop = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(edgeLeft, edgeTop, 0, 0, this.xSize, this.ySize);

        final int waterLevel = this.tileEntity.getScaledGasLevel(38);
        // Water
        this.mc.renderEngine.bindTexture(GuiWaterElectrolyzer.gasTextures);
        this.drawTexturedModalRect(
            edgeLeft + 7,
            edgeTop + 17 + 49 - waterLevel,
            1 + 4 * 17,
            38 - waterLevel,
            16,
            waterLevel);
        // Oxygen
        int displayInt = this.tileEntity.getScaledFuelLevel(38);
        this.drawTexturedModalRect(
            edgeLeft + 132,
            edgeTop + 17 + 49 - displayInt,
            17 + 1,
            38 - displayInt,
            16,
            displayInt);
        // Hydrogen
        displayInt = this.tileEntity.getScaledFuelLevel2(38);
        this.drawTexturedModalRect(
            edgeLeft + 153,
            edgeTop + 17 + 49 - displayInt,
            2 * 17 + 1,
            38 - displayInt,
            16,
            displayInt);

        this.addToolTips();

        this.mc.renderEngine.bindTexture(GuiWaterElectrolyzer.refineryTexture);

        if (this.tileEntity.getEnergyStoredGC() > 0) {
            this.drawTexturedModalRect(edgeLeft + 28, edgeTop + 16, 208, 0, 11, 10);
        }

        this.drawTexturedModalRect(
            edgeLeft + 42,
            edgeTop + 17,
            176,
            38,
            Math.min(this.tileEntity.getScaledElecticalLevel(54), 54),
            7);
    }

    private void addToolTips() {
        final List<String> gasTankDesc = new ArrayList<>();
        gasTankDesc.add(GCCoreUtil.translate("gui.terraformer.desc.0"));
        this.gasTankRegion.tooltipStrings = gasTankDesc;

        List<String> fuelTankDesc = new ArrayList<>();
        fuelTankDesc.add(GCCoreUtil.translate("gui.gasTank.desc.0"));
        FluidStack gasTankContents = this.tileEntity.liquidTank != null ? this.tileEntity.liquidTank.getFluid() : null;
        if (gasTankContents != null) {
            final String gasname = GCCoreUtil.translate(
                gasTankContents.getFluid()
                    .getUnlocalizedName());
            fuelTankDesc.add("(" + gasname + ")");
        } else {
            fuelTankDesc.add(" ");
        }
        int fuelLevel = gasTankContents != null ? gasTankContents.amount : 0;
        int fuelCapacity = this.tileEntity.liquidTank != null ? this.tileEntity.liquidTank.getCapacity() : 0;
        fuelTankDesc.add(EnumColor.YELLOW + " " + fuelLevel + " / " + fuelCapacity);
        this.fuelTankRegion.tooltipStrings = fuelTankDesc;

        fuelTankDesc = new ArrayList<>();
        fuelTankDesc.add(GCCoreUtil.translate("gui.gasTank.desc.0"));
        gasTankContents = this.tileEntity.liquidTank2 != null ? this.tileEntity.liquidTank2.getFluid() : null;
        if (gasTankContents != null) {
            final String gasname = GCCoreUtil.translate(
                gasTankContents.getFluid()
                    .getUnlocalizedName());
            fuelTankDesc.add("(" + gasname + ")");
        } else {
            fuelTankDesc.add(" ");
        }
        fuelLevel = gasTankContents != null ? gasTankContents.amount : 0;
        fuelCapacity = this.tileEntity.liquidTank2 != null ? this.tileEntity.liquidTank2.getCapacity() : 0;
        fuelTankDesc.add(EnumColor.YELLOW + " " + fuelLevel + " / " + fuelCapacity);
        this.fuelTank2Region.tooltipStrings = fuelTankDesc;

        final List<String> electricityDesc = new ArrayList<>();
        electricityDesc.add(GCCoreUtil.translate("gui.energyStorage.desc.0"));
        // electricityDesc.add(EnumColor.YELLOW +
        // GCCoreUtil.translate("gui.energyStorage.desc.1") + ((int)
        // Math.floor(this.tileEntity.getEnergyStoredGC()) + " / " + (int)
        // Math.floor(this.tileEntity.getMaxEnergyStoredGC())));
        EnergyDisplayHelper.getEnergyDisplayTooltip(
            this.tileEntity.getEnergyStoredGC(),
            this.tileEntity.getMaxEnergyStoredGC(),
            electricityDesc);
        this.electricInfoRegion.tooltipStrings = electricityDesc;
    }
}
