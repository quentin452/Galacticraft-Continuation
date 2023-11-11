package micdoodle8.mods.galacticraft.core.client.gui.container;

import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.core.client.gui.element.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import micdoodle8.mods.galacticraft.core.util.*;
import org.lwjgl.opengl.*;
import micdoodle8.mods.galacticraft.core.*;

@SideOnly(Side.CLIENT)
public class GuiBuggy extends GuiContainerGC
{
    private static ResourceLocation[] sealerTexture;
    private final IInventory upperChestInventory;
    private final int type;
    
    public GuiBuggy(final IInventory par1IInventory, final IInventory par2IInventory, final int type) {
        super(new ContainerBuggy(par1IInventory, par2IInventory, type));
        this.upperChestInventory = par1IInventory;
        this.allowUserInput = false;
        this.type = type;
        this.ySize = 145 + this.type * 36;
    }
    
    public void initGui() {
        super.initGui();
        final List<String> oxygenDesc = new ArrayList<String>();
        oxygenDesc.add(GCCoreUtil.translate("gui.fuelTank.desc.0"));
        oxygenDesc.add(GCCoreUtil.translate("gui.fuelTank.desc.1"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 71, (this.height - this.ySize) / 2 + 6, 36, 40, oxygenDesc, this.width, this.height, this));
    }
    
    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        this.fontRendererObj.drawString(GCCoreUtil.translate("gui.message.fuel.name"), 8, 5, 4210752);
        this.fontRendererObj.drawString(GCCoreUtil.translate(this.upperChestInventory.getInventoryName()), 8, (this.type == 0) ? 50 : 39, 4210752);
        if (this.mc.thePlayer != null && this.mc.thePlayer.ridingEntity != null && this.mc.thePlayer.ridingEntity instanceof EntityBuggy) {
            this.fontRendererObj.drawString(GCCoreUtil.translate("gui.message.fuel.name") + ":", 125, 18, 4210752);
            final double percentage = ((EntityBuggy)this.mc.thePlayer.ridingEntity).getScaledFuelLevel(100);
            final String color = (percentage > 80.0) ? EnumColor.BRIGHT_GREEN.getCode() : ((percentage > 40.0) ? EnumColor.ORANGE.getCode() : EnumColor.RED.getCode());
            final String str = percentage + "% " + GCCoreUtil.translate("gui.message.full.name");
            this.fontRendererObj.drawString(color + str, 117 - str.length() / 2, 28, 4210752);
        }
    }
    
    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        this.mc.getTextureManager().bindTexture(GuiBuggy.sealerTexture[this.type]);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, 176, this.ySize);
        if (this.mc.thePlayer != null && this.mc.thePlayer.ridingEntity != null && this.mc.thePlayer.ridingEntity instanceof EntityBuggy) {
            final int fuelLevel = ((EntityBuggy)this.mc.thePlayer.ridingEntity).getScaledFuelLevel(38);
            this.drawTexturedModalRect((this.width - this.xSize) / 2 + 72, (this.height - this.ySize) / 2 + 45 - fuelLevel, 176, 38 - fuelLevel, 42, fuelLevel);
        }
    }
    
    static {
        GuiBuggy.sealerTexture = new ResourceLocation[4];
        for (int i = 0; i < 4; ++i) {
            GuiBuggy.sealerTexture[i] = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/buggy_" + i * 18 + ".png");
        }
    }
}
