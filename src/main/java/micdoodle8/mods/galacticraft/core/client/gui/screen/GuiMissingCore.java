package micdoodle8.mods.galacticraft.core.client.gui.screen;

import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.client.gui.*;
import cpw.mods.fml.client.*;
import java.net.*;

@SideOnly(Side.CLIENT)
public class GuiMissingCore extends GuiScreen
{
    private int urlX;
    private int urlY;
    private int urlWidth;
    private int urlHeight;
    
    public void initGui() {
        super.initGui();
    }
    
    public void drawScreen(final int par1, final int par2, final float par3) {
        this.drawDefaultBackground();
        int offset = this.height / 2 - 50;
        this.drawCenteredString(this.fontRendererObj, GCCoreUtil.translate("gui.missingCore.name.0"), this.width / 2, offset, 16733525);
        offset += 25;
        this.drawCenteredString(this.fontRendererObj, GCCoreUtil.translate("gui.missingCore.name.1"), this.width / 2, offset, 16733525);
        offset += 20;
        this.drawCenteredString(this.fontRendererObj, GCCoreUtil.translate("gui.missingCore.name.2"), this.width / 2, offset, 10066329);
        offset += 20;
        final String s = EnumChatFormatting.UNDERLINE + GCCoreUtil.translate("gui.missingCore.name.3");
        this.urlX = this.width / 2 - this.fontRendererObj.getStringWidth(s) / 2 - 10;
        this.urlY = offset - 2;
        this.urlWidth = this.fontRendererObj.getStringWidth(s) + 20;
        this.urlHeight = 14;
        Gui.drawRect(this.urlX, this.urlY, this.urlX + this.urlWidth, this.urlY + this.urlHeight, ColorUtil.to32BitColor(50, 0, 0, 255));
        this.drawCenteredString(this.fontRendererObj, s, this.width / 2, offset, 10066329);
    }
    
    protected void keyTyped(final char par1, final int par2) {
    }
    
    public void actionPerformed() {
        this.actionPerformed(null);
    }
    
    protected void actionPerformed(final GuiButton par1GuiButton) {
        FMLClientHandler.instance().getClient().displayGuiScreen((GuiScreen)null);
    }
    
    protected void mouseClicked(final int x, final int y, final int which) {
        if (x > this.urlX && x < this.urlX + this.urlWidth && y > this.urlY && y < this.urlY + this.urlHeight) {
            try {
                final Class<?> oclass = Class.forName("java.awt.Desktop");
                final Object object = oclass.getMethod("getDesktop", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
                oclass.getMethod("browse", URI.class).invoke(object, new URI("http://micdoodle8.com/mods/galacticraft/downloads"));
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
