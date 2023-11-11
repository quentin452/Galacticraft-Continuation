package micdoodle8.mods.galacticraft.core.client.gui.screen;

import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import micdoodle8.mods.galacticraft.core.client.model.*;
import cpw.mods.fml.client.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import net.minecraft.client.entity.*;
import micdoodle8.mods.galacticraft.core.dimension.*;
import micdoodle8.mods.galacticraft.core.wrappers.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.client.gui.element.*;
import net.minecraft.client.gui.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import micdoodle8.mods.galacticraft.core.util.*;
import org.lwjgl.opengl.*;

public class GuiJoinSpaceRace extends GuiScreen implements GuiElementCheckbox.ICheckBoxCallback, GuiElementTextBox.ITextBoxCallback
{
    protected static final ResourceLocation texture;
    private int ticksPassed;
    private EntityPlayer thePlayer;
    private boolean initialized;
    private int buttonFlag_height;
    private int buttonFlag_xPosition;
    private int buttonFlag_yPosition;
    private EntityFlag dummyFlag;
    private ModelFlag dummyModel;
    private SpaceRace spaceRaceData;
    
    public GuiJoinSpaceRace(final EntityClientPlayerMP player) {
        this.dummyFlag = new EntityFlag((World)FMLClientHandler.instance().getClient().theWorld);
        this.dummyModel = new ModelFlag();
        this.thePlayer = (EntityPlayer)player;
        final GCPlayerStatsClient stats = GCPlayerStatsClient.get((EntityPlayerSP)player);
        final SpaceRace race = SpaceRaceManager.getSpaceRaceFromID(stats.spaceRaceInviteTeamID);
        if (race != null) {
            this.spaceRaceData = race;
        }
        else {
            final List<String> playerList = new ArrayList<String>();
            playerList.add(player.getGameProfile().getName());
            this.spaceRaceData = new SpaceRace(playerList, "gui.spaceRace.unnamed", new FlagData(48, 32), new Vector3(1.0, 1.0, 1.0));
        }
    }
    
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        if (this.initialized) {
            final int var5 = (this.width - this.width / 4) / 2;
            final int var6 = (this.height - this.height / 4) / 2;
            final int buttonFlag_width = 81;
            this.buttonFlag_height = 58;
            this.buttonFlag_xPosition = this.width / 2 - buttonFlag_width / 2;
            this.buttonFlag_yPosition = this.height / 2 - this.height / 3 + 10;
            this.buttonList.add(new GuiElementGradientButton(0, this.width / 2 - this.width / 3 + 15, this.height / 2 - this.height / 4 - 15, 50, 15, GCCoreUtil.translate("gui.spaceRace.create.close.name")));
            final int width = (int)(var5 / 1.0f);
            this.buttonList.add(new GuiElementGradientButton(1, this.width / 2 - width / 2, this.buttonFlag_yPosition + this.buttonFlag_height + 60, width, 20, GCCoreUtil.translateWithFormat("gui.spaceRace.join.name", this.spaceRaceData.getTeamName())));
        }
    }
    
    protected void actionPerformed(final GuiButton buttonClicked) {
        switch (buttonClicked.id) {
            case 0: {
                this.thePlayer.closeScreen();
                break;
            }
            case 1: {
                GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(PacketSimple.EnumSimplePacket.S_ADD_RACE_PLAYER, new Object[] { this.thePlayer.getGameProfile().getName(), this.spaceRaceData.getSpaceRaceID() }));
                this.thePlayer.closeScreen();
                break;
            }
        }
    }
    
    protected void mouseClicked(final int x, final int y, final int clickIndex) {
        super.mouseClicked(x, y, clickIndex);
    }
    
    public void updateScreen() {
        super.updateScreen();
        ++this.ticksPassed;
        if (!this.initialized) {}
    }
    
    public void drawScreen(final int par1, final int par2, final float par3) {
        this.drawDefaultBackground();
        final int var5 = (this.width - this.width / 4) / 2;
        final int var6 = (this.height - this.height / 4) / 2;
        if (this.initialized) {
            this.drawCenteredString(this.fontRendererObj, GCCoreUtil.translate("gui.spaceRace.join.title.name"), this.width / 2, this.height / 2 - this.height / 3 - 15, 16777215);
            this.drawFlagButton(par1, par2);
            this.drawCenteredString(this.fontRendererObj, GCCoreUtil.translate("gui.spaceRace.join.owner.name") + ": " + this.spaceRaceData.getPlayerNames().get(0), this.width / 2, this.buttonFlag_yPosition + this.buttonFlag_height + 25, ColorUtil.to32BitColor(255, 150, 150, 150));
            this.drawCenteredString(this.fontRendererObj, GCCoreUtil.translateWithFormat("gui.spaceRace.join.memberCount.name", this.spaceRaceData.getPlayerNames().size()), this.width / 2, this.buttonFlag_yPosition + this.buttonFlag_height + 40, ColorUtil.to32BitColor(255, 150, 150, 150));
            GL11.glPushMatrix();
            GL11.glTranslatef((float)(this.width / 2), (float)(this.buttonFlag_yPosition + this.buttonFlag_height + 5 + FMLClientHandler.instance().getClient().fontRenderer.FONT_HEIGHT / 2), 0.0f);
            GL11.glScalef(1.5f, 1.5f, 1.0f);
            GL11.glTranslatef((float)(-this.width / 2), (float)(-(this.buttonFlag_yPosition + this.buttonFlag_height + 5) - FMLClientHandler.instance().getClient().fontRenderer.FONT_HEIGHT / 2), 0.0f);
            this.drawCenteredString(this.fontRendererObj, this.spaceRaceData.getTeamName(), this.width / 2, this.buttonFlag_yPosition + this.buttonFlag_height + 5, ColorUtil.to32BitColor(255, 100, 150, 20));
            GL11.glPopMatrix();
        }
        super.drawScreen(par1, par2, par3);
    }
    
    private void drawFlagButton(final int mouseX, final int mouseY) {
        GL11.glPushMatrix();
        GL11.glTranslatef(this.buttonFlag_xPosition + 2.9f, (float)(this.buttonFlag_yPosition + this.buttonFlag_height + 1 - 4), 0.0f);
        GL11.glScalef(74.0f, 74.0f, 1.0f);
        GL11.glTranslatef(0.0f, 0.36f, 1.0f);
        GL11.glScalef(1.0f, 1.0f, -1.0f);
        this.dummyFlag.flagData = this.spaceRaceData.getFlagData();
        this.dummyModel.renderFlag(this.dummyFlag, (float)this.ticksPassed);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
    
    public void drawWorldBackground(final int i) {
        if (this.mc.theWorld != null) {
            final int scaleX = Math.min(this.ticksPassed * 14, this.width / 3);
            final int scaleY = Math.min(this.ticksPassed * 14, this.height / 3);
            if (scaleX == this.width / 3 && scaleY == this.height / 3 && !this.initialized) {
                this.initialized = true;
                this.initGui();
            }
            this.drawGradientRect(this.width / 2 - scaleX, this.height / 2 - scaleY, this.width / 2 + scaleX, this.height / 2 + scaleY, -1072689136, -804253680);
        }
        else {
            this.drawBackground(i);
        }
    }
    
    public void onSelectionChanged(final GuiElementCheckbox checkbox, final boolean newSelected) {
    }
    
    public boolean canPlayerEdit(final GuiElementCheckbox checkbox, final EntityPlayer player) {
        return true;
    }
    
    public boolean getInitiallySelected(final GuiElementCheckbox checkbox) {
        return false;
    }
    
    public void onIntruderInteraction() {
    }
    
    public void onIntruderInteraction(final GuiElementTextBox textBox) {
    }
    
    public boolean canPlayerEdit(final GuiElementTextBox textBox, final EntityPlayer player) {
        return true;
    }
    
    public void onTextChanged(final GuiElementTextBox textBox, final String newText) {
    }
    
    public String getInitialText(final GuiElementTextBox textBox) {
        return "";
    }
    
    public int getTextColor(final GuiElementTextBox textBox) {
        return ColorUtil.to32BitColor(255, 255, 255, 255);
    }
    
    static {
        texture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/gui.png");
    }
}
