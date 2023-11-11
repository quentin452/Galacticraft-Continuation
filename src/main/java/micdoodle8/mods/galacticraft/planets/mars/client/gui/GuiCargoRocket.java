package micdoodle8.mods.galacticraft.planets.mars.client.gui;

import micdoodle8.mods.galacticraft.core.client.gui.container.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import micdoodle8.mods.galacticraft.planets.mars.entities.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.planets.mars.network.*;
import micdoodle8.mods.galacticraft.core.network.*;
import micdoodle8.mods.galacticraft.core.client.gui.element.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;
import net.minecraft.client.gui.*;
import org.lwjgl.opengl.*;

@SideOnly(Side.CLIENT)
public class GuiCargoRocket extends GuiContainerGC
{
    private static ResourceLocation[] rocketTextures;
    private final IInventory upperChestInventory;
    private final IRocketType.EnumRocketType rocketType;
    private EntityCargoRocket rocket;
    private GuiButton launchButton;
    
    public GuiCargoRocket(final IInventory par1IInventory, final EntityCargoRocket rocket) {
        this(par1IInventory, rocket, rocket.rocketType);
    }
    
    public GuiCargoRocket(final IInventory par1IInventory, final EntityCargoRocket rocket, final IRocketType.EnumRocketType rocketType) {
        super((Container)new ContainerRocketInventory(par1IInventory, (IInventory)rocket, rocketType));
        this.upperChestInventory = par1IInventory;
        this.rocket = rocket;
        this.allowUserInput = false;
        this.ySize = ((rocketType.getInventorySpace() <= 3) ? 132 : (145 + rocketType.getInventorySpace() * 2));
        this.rocketType = rocketType;
    }
    
    protected void actionPerformed(final GuiButton button) {
        switch (button.id) {
            case 0: {
                GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimpleMars(PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_CARGO_ROCKET_STATUS, new Object[] { this.rocket.getEntityId(), 0 }));
                break;
            }
        }
    }
    
    public void initGui() {
        super.initGui();
        final int var6 = (this.height - this.ySize) / 2;
        final int var7 = (this.width - this.xSize) / 2;
        this.launchButton = new GuiButton(0, var7 + 116, var6 + 26, 50, 20, GCCoreUtil.translate("gui.message.launch.name"));
        this.buttonList.add(this.launchButton);
        final List<String> fuelTankDesc = new ArrayList<String>();
        fuelTankDesc.add(GCCoreUtil.translate("gui.fuelTank.desc.0"));
        fuelTankDesc.add(GCCoreUtil.translate("gui.fuelTank.desc.1"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + ((this.rocket.rocketType.getInventorySpace() == 2) ? 70 : 71), (this.height - this.ySize) / 2 + 6, 36, 40, (List)fuelTankDesc, this.width, this.height, (GuiContainerGC)this));
    }
    
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        if (this.rocket.rocketType.getInventorySpace() == 2) {
            this.fontRendererObj.drawString(GCCoreUtil.translate(this.upperChestInventory.getInventoryName()), 8, 76 + (this.rocket.rocketType.getInventorySpace() - 20) / 9 * 18, 4210752);
        }
        else {
            this.fontRendererObj.drawString(GCCoreUtil.translate(this.upperChestInventory.getInventoryName()), 8, 89 + (this.rocket.rocketType.getInventorySpace() - 20) / 9 * 18, 4210752);
        }
        String str = GCCoreUtil.translate("gui.message.fuel.name") + ":";
        this.fontRendererObj.drawString(str, 140 - this.fontRendererObj.getStringWidth(str) / 2, 5, 4210752);
        final double percentage = this.rocket.getScaledFuelLevel(100);
        final String color = (percentage > 80.0) ? EnumColor.BRIGHT_GREEN.getCode() : ((percentage > 40.0) ? EnumColor.ORANGE.getCode() : EnumColor.RED.getCode());
        str = percentage + "% " + GCCoreUtil.translate("gui.message.full.name");
        this.fontRendererObj.drawString(color + str, 140 - this.fontRendererObj.getStringWidth(str) / 2, 15, 4210752);
        str = GCCoreUtil.translate("gui.message.status.name") + ":";
        this.fontRendererObj.drawString(str, 40 - this.fontRendererObj.getStringWidth(str) / 2, 9, 4210752);
        String[] spltString = { "" };
        String colour = EnumColor.YELLOW.toString();
        if (this.rocket.statusMessageCooldown == 0 || this.rocket.statusMessage == null) {
            spltString = new String[] { GCCoreUtil.translate("gui.cargorocket.status.waiting.0"), GCCoreUtil.translate("gui.cargorocket.status.waiting.1") };
            if (this.rocket.launchPhase != EntitySpaceshipBase.EnumLaunchPhase.UNIGNITED.ordinal()) {
                spltString = new String[] { GCCoreUtil.translate("gui.cargorocket.status.launched.0"), GCCoreUtil.translate("gui.cargorocket.status.launched.1") };
                this.launchButton.enabled = false;
            }
        }
        else {
            spltString = this.rocket.statusMessage.split("#");
            colour = this.rocket.statusColour;
        }
        int y = 2;
        for (final String splitString : spltString) {
            this.fontRendererObj.drawString(colour + splitString, 35 - this.fontRendererObj.getStringWidth(splitString) / 2, 9 * y, 4210752);
            ++y;
        }
        if (this.rocket.statusValid && this.rocket.statusMessageCooldown > 0 && this.rocket.statusMessageCooldown < 4) {
            this.mc.displayGuiScreen((GuiScreen)null);
        }
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        this.mc.getTextureManager().bindTexture(GuiCargoRocket.rocketTextures[(this.rocketType.getInventorySpace() - 2) / 18]);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, 176, this.ySize);
        final int fuelLevel = this.rocket.getScaledFuelLevel(38);
        this.drawTexturedModalRect((this.width - this.xSize) / 2 + ((this.rocket.rocketType.getInventorySpace() == 2) ? 71 : 72), (this.height - this.ySize) / 2 + 45 - fuelLevel, 176, 38 - fuelLevel, 42, fuelLevel);
    }
    
    static {
        GuiCargoRocket.rocketTextures = new ResourceLocation[4];
        for (int i = 0; i < 4; ++i) {
            GuiCargoRocket.rocketTextures[i] = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/rocket_" + i * 18 + ".png");
        }
    }
}
