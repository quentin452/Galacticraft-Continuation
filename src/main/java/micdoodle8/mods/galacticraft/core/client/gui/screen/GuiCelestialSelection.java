package micdoodle8.mods.galacticraft.core.client.gui.screen;

import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.api.galaxies.*;
import com.google.common.collect.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.api.*;
import micdoodle8.mods.galacticraft.api.world.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import micdoodle8.mods.galacticraft.core.tick.*;
import net.minecraft.client.settings.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import org.lwjgl.input.*;
import net.minecraft.client.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import org.lwjgl.opengl.*;
import org.lwjgl.*;
import org.lwjgl.util.vector.*;
import java.nio.*;
import micdoodle8.mods.galacticraft.api.event.client.*;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.eventhandler.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.item.*;
import java.util.*;
import com.ibm.icu.text.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.gui.*;

public class GuiCelestialSelection extends GuiScreen
{
    protected static final int MAX_SPACE_STATION_NAME_LENGTH = 32;
    protected Matrix4f mainWorldMatrix;
    protected float zoom;
    protected float planetZoom;
    protected boolean doneZooming;
    protected float preSelectZoom;
    protected Vector2f preSelectPosition;
    public static ResourceLocation guiMain0;
    public static ResourceLocation guiMain1;
    protected int ticksSinceSelection;
    protected int ticksSinceUnselection;
    protected int ticksSinceMenuOpen;
    protected int ticksTotal;
    protected Vector2f position;
    protected Map<CelestialBody, Vector3f> planetPosMap;
    protected Map<CelestialBody, Integer> celestialBodyTicks;
    protected CelestialBody selectedBody;
    protected CelestialBody lastSelectedBody;
    protected static int BORDER_WIDTH;
    protected static int BORDER_EDGE_WIDTH;
    protected int canCreateOffset;
    protected EnumSelectionState selectionState;
    protected int selectionCount;
    protected int zoomTooltipPos;
    protected Object selectedParent;
    protected final boolean mapMode;
    public List<CelestialBody> possibleBodies;
    public Map<Integer, Map<String, StationDataGUI>> spaceStationMap;
    public SmallFontRenderer smallFontRenderer;
    protected String selectedStationOwner;
    protected int spaceStationListOffset;
    protected boolean renamingSpaceStation;
    protected String renamingString;
    protected Vector2f translation;
    protected boolean mouseDragging;
    protected int lastMovePosX;
    protected int lastMovePosY;
    protected boolean errorLogged;

    public GuiCelestialSelection(final boolean mapMode, final List<CelestialBody> possibleBodies) {
        this.zoom = 0.0f;
        this.planetZoom = 0.0f;
        this.doneZooming = false;
        this.preSelectZoom = 0.0f;
        this.preSelectPosition = new Vector2f();
        this.ticksSinceSelection = 0;
        this.ticksSinceUnselection = -1;
        this.ticksSinceMenuOpen = 0;
        this.ticksTotal = 0;
        this.position = new Vector2f(0.0f, 0.0f);
        this.planetPosMap = Maps.newHashMap();
        this.celestialBodyTicks = Maps.newHashMap();
        this.canCreateOffset = 0;
        this.selectionState = EnumSelectionState.PREVIEW;
        this.selectionCount = 0;
        this.zoomTooltipPos = 0;
        this.selectedParent = GalacticraftCore.solarSystemSol;
        this.spaceStationMap = Maps.newHashMap();
        this.selectedStationOwner = "";
        this.spaceStationListOffset = 0;
        this.renamingString = "";
        this.translation = new Vector2f();
        this.mouseDragging = false;
        this.lastMovePosX = -1;
        this.lastMovePosY = -1;
        this.errorLogged = false;
        this.translation.x = 0.0f;
        this.translation.y = 0.0f;
        this.mapMode = mapMode;
        this.possibleBodies = possibleBodies;
        this.smallFontRenderer = new SmallFontRenderer(FMLClientHandler.instance().getClient().gameSettings, new ResourceLocation("textures/font/ascii.png"), FMLClientHandler.instance().getClient().renderEngine, false);
    }

    public void initGui() {
        for (final Planet planet : GalaxyRegistry.getRegisteredPlanets().values()) {
            this.celestialBodyTicks.put((CelestialBody)planet, 0);
        }
        for (final Moon moon : GalaxyRegistry.getRegisteredMoons().values()) {
            this.celestialBodyTicks.put((CelestialBody)moon, 0);
        }
        for (final Satellite satellite : GalaxyRegistry.getRegisteredSatellites().values()) {
            this.celestialBodyTicks.put((CelestialBody)satellite, 0);
        }
        GuiCelestialSelection.BORDER_WIDTH = this.width / 65;
        GuiCelestialSelection.BORDER_EDGE_WIDTH = GuiCelestialSelection.BORDER_WIDTH / 4;
    }

    protected String getGrandparentName() {
        if (this.selectedParent instanceof Planet) {
            final SolarSystem parentSolarSystem = ((Planet)this.selectedParent).getParentSolarSystem();
            if (parentSolarSystem != null) {
                return parentSolarSystem.getLocalizedParentGalaxyName();
            }
        }
        else if (this.selectedParent instanceof IChildBody) {
            final Planet parentPlanet = ((IChildBody)this.selectedParent).getParentPlanet();
            if (parentPlanet != null) {
                final SolarSystem parentSolarSystem2 = parentPlanet.getParentSolarSystem();
                if (parentSolarSystem2 != null) {
                    return parentSolarSystem2.getLocalizedName();
                }
            }
        }
        else if (this.selectedParent instanceof Star) {
            final SolarSystem parentSolarSystem = ((Star)this.selectedParent).getParentSolarSystem();
            if (parentSolarSystem != null) {
                return parentSolarSystem.getLocalizedParentGalaxyName();
            }
        }
        else if (this.selectedParent instanceof SolarSystem) {
            return ((SolarSystem)this.selectedParent).getLocalizedParentGalaxyName();
        }
        return "Null";
    }

    protected int getSatelliteParentID(final Satellite satellite) {
        return satellite.getParentPlanet().getDimensionID();
    }

    protected String getParentName() {
        if (this.selectedParent instanceof Planet) {
            final SolarSystem parentSolarSystem = ((Planet)this.selectedParent).getParentSolarSystem();
            if (parentSolarSystem != null) {
                return parentSolarSystem.getLocalizedName();
            }
        }
        else if (this.selectedParent instanceof IChildBody) {
            final Planet parentPlanet = ((IChildBody)this.selectedParent).getParentPlanet();
            if (parentPlanet != null) {
                return parentPlanet.getLocalizedName();
            }
        }
        else {
            if (this.selectedParent instanceof SolarSystem) {
                return ((SolarSystem)this.selectedParent).getLocalizedName();
            }
            if (this.selectedParent instanceof Star) {
                final SolarSystem parentSolarSystem = ((Star)this.selectedParent).getParentSolarSystem();
                if (parentSolarSystem != null) {
                    return parentSolarSystem.getLocalizedName();
                }
            }
        }
        return "Null";
    }

    protected float getScale(final CelestialBody celestialBody) {
        return 3.0f * celestialBody.getRelativeDistanceFromCenter().unScaledDistance * ((celestialBody instanceof Planet) ? 25.0f : 0.2f);
    }

    protected List<CelestialBody> getSiblings(final CelestialBody celestialBody) {
        final List<CelestialBody> bodyList = Lists.newArrayList();
        if (celestialBody instanceof Planet) {
            final SolarSystem solarSystem = ((Planet)celestialBody).getParentSolarSystem();
            for (final Planet planet : GalaxyRegistry.getRegisteredPlanets().values()) {
                final SolarSystem solarSystem2 = planet.getParentSolarSystem();
                if (solarSystem2 != null && solarSystem2.equals(solarSystem)) {
                    bodyList.add((CelestialBody)planet);
                }
            }
        }
        else if (celestialBody instanceof IChildBody) {
            final Planet planet2 = ((IChildBody)celestialBody).getParentPlanet();
            for (final Moon moon : GalaxyRegistry.getRegisteredMoons().values()) {
                final Planet planet3 = moon.getParentPlanet();
                if (planet3 != null && planet3.equals((Object)planet2)) {
                    bodyList.add((CelestialBody)moon);
                }
            }
        }
        Collections.sort(bodyList);
        return bodyList;
    }

    protected List<CelestialBody> getChildren(final Object object) {
        final List<CelestialBody> bodyList = Lists.newArrayList();
        if (object instanceof Planet) {
            final List<Moon> moons = (List<Moon>)GalaxyRegistry.getMoonsForPlanet((Planet)object);
            bodyList.addAll((Collection<? extends CelestialBody>)moons);
        }
        else if (object instanceof SolarSystem) {
            final List<Planet> planets = (List<Planet>)GalaxyRegistry.getPlanetsForSolarSystem((SolarSystem)object);
            bodyList.addAll((Collection<? extends CelestialBody>)planets);
        }
        Collections.sort(bodyList);
        return bodyList;
    }

    protected float lerp(final float v0, final float v1, final float t) {
        return v0 + t * (v1 - v0);
    }

    protected Vector2f lerpVec2(final Vector2f v0, final Vector2f v1, final float t) {
        return new Vector2f(v0.x + t * (v1.x - v0.x), v0.y + t * (v1.y - v0.y));
    }

    protected float getZoomAdvanced() {
        if (this.ticksTotal < 30) {
            final float scale = Math.max(0.0f, Math.min(this.ticksTotal / 30.0f, 1.0f));
            final float lerp = this.lerp(-0.75f, 0.0f, (float)Math.pow(scale, 0.5));
            return lerp;
        }
        if (this.selectedBody == null) {
            if (!this.doneZooming) {
                final float unselectScale = this.lerp(this.zoom, this.preSelectZoom, Math.max(0.0f, Math.min(this.ticksSinceUnselection / 100.0f, 1.0f)));
                if (unselectScale <= this.preSelectZoom + 0.05f) {
                    this.zoom = this.preSelectZoom;
                    this.preSelectZoom = 0.0f;
                    this.ticksSinceUnselection = -1;
                    this.doneZooming = true;
                }
                return unselectScale;
            }
            return this.zoom;
        }
        else {
            if (this.selectionState == EnumSelectionState.PREVIEW && this.selectionCount < 2 && (!(this.lastSelectedBody instanceof Planet) || !(this.selectedBody instanceof Planet))) {
                return this.zoom;
            }
            if (!this.doneZooming) {
                final float f = this.lerp(this.zoom, 12.0f, Math.max(0.0f, Math.min((this.ticksSinceSelection - 20) / 40.0f, 1.0f)));
                if (f >= 11.95f) {
                    this.doneZooming = true;
                }
                return f;
            }
            return 12.0f + this.planetZoom;
        }
    }

    protected Vector2f getTranslationAdvanced(final float partialTicks) {
        if (this.selectedBody == null) {
            if (this.ticksSinceUnselection > 0) {
                final float f0 = Math.max(0.0f, Math.min((this.ticksSinceUnselection + partialTicks) / 100.0f, 1.0f));
                if (f0 >= 0.999999f) {
                    this.ticksSinceUnselection = 0;
                }
                return this.lerpVec2(this.position, this.preSelectPosition, f0);
            }
            return new Vector2f(this.position.x + this.translation.x, this.position.y + this.translation.y);
        }
        else if (this.selectionCount < 2) {
            if (this.selectedBody instanceof IChildBody) {
                final Vector3f posVec = this.getCelestialBodyPosition((CelestialBody)((IChildBody)this.selectedBody).getParentPlanet());
                return new Vector2f(posVec.x, posVec.y);
            }
            return new Vector2f(this.position.x + this.translation.x, this.position.y + this.translation.y);
        }
        else {
            if (this.selectedBody instanceof Planet && this.lastSelectedBody instanceof IChildBody && ((IChildBody)this.lastSelectedBody).getParentPlanet() == this.selectedBody) {
                final Vector3f posVec = this.getCelestialBodyPosition(this.selectedBody);
                return new Vector2f(posVec.x, posVec.y);
            }
            final Vector3f posVec = this.getCelestialBodyPosition(this.selectedBody);
            return this.lerpVec2(this.position, new Vector2f(posVec.x, posVec.y), Math.max(0.0f, Math.min((this.ticksSinceSelection + partialTicks - 18.0f) / 7.5f, 1.0f)));
        }
    }

    protected void keyTyped(final char keyChar, final int keyID) {
        if (this.mapMode) {
            super.keyTyped(keyChar, keyID);
        }
        if (keyID == 1 && this.selectedBody != null) {
            this.unselectCelestialBody();
        }
        if (this.renamingSpaceStation) {
            if (keyID == 14) {
                if (this.renamingString != null && this.renamingString.length() > 0) {
                    final String toBeParsed = this.renamingString.substring(0, this.renamingString.length() - 1);
                    if (this.isValid(toBeParsed)) {
                        this.renamingString = toBeParsed;
                    }
                    else {
                        this.renamingString = "";
                    }
                }
            }
            else if (keyChar == '\u0016') {
                String pastestring = GuiScreen.getClipboardString();
                if (pastestring == null) {
                    pastestring = "";
                }
                if (this.isValid(this.renamingString + pastestring)) {
                    this.renamingString += pastestring;
                    this.renamingString = this.renamingString.substring(0, Math.min(String.valueOf(this.renamingString).length(), 32));
                }
            }
            else if (this.isValid(this.renamingString + keyChar)) {
                this.renamingString += keyChar;
                this.renamingString = this.renamingString.substring(0, Math.min(this.renamingString.length(), 32));
            }
            return;
        }
        if (keyID == 28) {
            this.teleportToSelectedBody();
        }
    }

    public boolean isValid(final String string) {
        return string.length() > 0 && ChatAllowedCharacters.isAllowedCharacter(string.charAt(string.length() - 1));
    }

    protected boolean canCreateSpaceStation(final CelestialBody atBody) {
        if (this.mapMode || ConfigManagerCore.disableSpaceStationCreation) {
            return false;
        }
        if (!atBody.getReachable() || (this.possibleBodies != null && !this.possibleBodies.contains(atBody))) {
            return false;
        }
        boolean foundRecipe = false;
        for (final SpaceStationType type : GalacticraftRegistry.getSpaceStationData()) {
            if (type.getWorldToOrbitID() == atBody.getDimensionID()) {
                foundRecipe = true;
            }
        }
        if (!foundRecipe) {
            return false;
        }
        if (!ClientProxyCore.clientSpaceStationID.containsKey(atBody.getDimensionID())) {
            return true;
        }
        final int resultID = ClientProxyCore.clientSpaceStationID.get(atBody.getDimensionID());
        return resultID == 0 || resultID == -1;
    }

    protected void unselectCelestialBody() {
        this.selectionCount = 0;
        this.ticksSinceUnselection = 0;
        this.lastSelectedBody = this.selectedBody;
        this.selectedBody = null;
        this.doneZooming = false;
        this.selectedStationOwner = "";
    }

    public void updateScreen() {
        ++this.ticksSinceMenuOpen;
        ++this.ticksTotal;
        for (final CelestialBody e : this.celestialBodyTicks.keySet()) {
            Integer i = this.celestialBodyTicks.get(e);
            if (i != null) {
                ++i;
            }
            this.celestialBodyTicks.put(e, i);
        }
        if (this.selectedBody != null) {
            ++this.ticksSinceSelection;
        }
        if (this.selectedBody == null && this.ticksSinceUnselection >= 0) {
            ++this.ticksSinceUnselection;
        }
        if (!this.renamingSpaceStation && (this.selectedBody == null || this.selectionCount < 2)) {
            final GameSettings gameSettings = this.mc.gameSettings;
            if (GameSettings.isKeyDown(KeyHandlerClient.leftKey)) {
                final Vector2f translation = this.translation;
                translation.x -= 2.0f;
                final Vector2f translation2 = this.translation;
                translation2.y -= 2.0f;
            }
            final GameSettings gameSettings2 = this.mc.gameSettings;
            if (GameSettings.isKeyDown(KeyHandlerClient.rightKey)) {
                final Vector2f translation3 = this.translation;
                translation3.x += 2.0f;
                final Vector2f translation4 = this.translation;
                translation4.y += 2.0f;
            }
            final GameSettings gameSettings3 = this.mc.gameSettings;
            if (GameSettings.isKeyDown(KeyHandlerClient.upKey)) {
                final Vector2f translation5 = this.translation;
                translation5.x += 2.0f;
                final Vector2f translation6 = this.translation;
                translation6.y -= 2.0f;
            }
            final GameSettings gameSettings4 = this.mc.gameSettings;
            if (GameSettings.isKeyDown(KeyHandlerClient.downKey)) {
                final Vector2f translation7 = this.translation;
                translation7.x -= 2.0f;
                final Vector2f translation8 = this.translation;
                translation8.y += 2.0f;
            }
        }
    }

    protected boolean teleportToSelectedBody() {
        if (this.selectedBody != null && this.selectedBody.getReachable() && this.possibleBodies != null && this.possibleBodies.contains(this.selectedBody)) {
            try {
                String dimension;
                if (this.selectedBody instanceof Satellite) {
                    if (this.spaceStationMap == null) {
                        GCLog.severe("Please report as a BUG: spaceStationIDs was null.");
                        return false;
                    }
                    final Satellite selectedSatellite = (Satellite)this.selectedBody;
                    final Integer mapping = this.spaceStationMap.get(this.getSatelliteParentID(selectedSatellite)).get(this.selectedStationOwner).getStationDimensionID();
                    if (mapping == null) {
                        GCLog.severe("Problem matching player name in space station check: " + this.selectedStationOwner);
                        return false;
                    }
                    final int spacestationID = mapping;
                    final WorldProvider spacestation = WorldUtil.getProviderForDimensionClient(spacestationID);
                    if (spacestation == null) {
                        GCLog.severe("Failed to find a spacestation with dimension " + spacestationID);
                        return false;
                    }
                    dimension = WorldUtil.getDimensionName(spacestation);
                }
                else {
                    dimension = WorldUtil.getDimensionName(WorldUtil.getProviderForDimensionClient(this.selectedBody.getDimensionID()));
                }
                if (dimension.contains("$")) {
                    this.mc.gameSettings.thirdPersonView = 0;
                }
                GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(PacketSimple.EnumSimplePacket.S_TELEPORT_ENTITY, new Object[] { dimension }));
                this.mc.displayGuiScreen((GuiScreen)null);
                return true;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void handleInput() {
        this.translation.x = 0.0f;
        this.translation.y = 0.0f;
        super.handleInput();
    }

    protected void mouseClickMove(final int x, final int y, final int lastButtonClicked, final long timeSinceMouseClick) {
        super.mouseClickMove(x, y, lastButtonClicked, timeSinceMouseClick);
        if (this.mouseDragging && this.lastMovePosX != -1 && lastButtonClicked == 0) {
            final int deltaX = x - this.lastMovePosX;
            final int deltaY = y - this.lastMovePosY;
            final Vector2f translation = this.translation;
            translation.x += (deltaX - deltaY) * -0.4f * (ConfigManagerCore.invertMapMouseScroll ? -1.0f : 1.0f) * ConfigManagerCore.mapMouseScrollSensitivity;
            final Vector2f translation2 = this.translation;
            translation2.y += (deltaY + deltaX) * -0.4f * (ConfigManagerCore.invertMapMouseScroll ? -1.0f : 1.0f) * ConfigManagerCore.mapMouseScrollSensitivity;
        }
        this.lastMovePosX = x;
        this.lastMovePosY = y;
    }

    protected void mouseMovedOrUp(final int x, final int y, final int button) {
        super.mouseMovedOrUp(x, y, button);
        this.mouseDragging = false;
        this.lastMovePosX = -1;
        this.lastMovePosY = -1;
    }

    protected void mouseClicked(final int x, final int y, final int button) {
        super.mouseClicked(x, y, button);
        boolean clickHandled = false;
        if (this.selectedBody != null && x > GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH && x < GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 88 && y > GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH && y < GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 13) {
            this.unselectCelestialBody();
            return;
        }
        if (!this.mapMode && x >= this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 95 && x < this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH && y > GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 181 + this.canCreateOffset && y < GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 182 + 12 + this.canCreateOffset && this.selectedBody != null) {
            final SpaceStationRecipe recipe = WorldUtil.getSpaceStationRecipe(this.selectedBody.getDimensionID());
            if (recipe != null && this.canCreateSpaceStation(this.selectedBody)) {
                if (recipe.matches((EntityPlayer)this.mc.thePlayer, false) || this.mc.thePlayer.capabilities.isCreativeMode) {
                    GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(PacketSimple.EnumSimplePacket.S_BIND_SPACE_STATION_ID, new Object[] { this.selectedBody.getDimensionID() }));
                    if (this.selectionCount < 2) {
                        this.selectionCount = 2;
                        this.preSelectZoom = this.zoom;
                        this.preSelectPosition = this.position;
                        this.ticksSinceSelection = 0;
                        this.doneZooming = false;
                    }
                }
                clickHandled = true;
            }
        }
        if (this.mapMode && x > this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 88 && x < this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH && y > GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH && y < GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 13) {
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
            clickHandled = true;
        }
        if (this.selectedBody != null && !this.mapMode && x > this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 88 && x < this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH && y > GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH && y < GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 13) {
            if (!(this.selectedBody instanceof Satellite) || !this.selectedStationOwner.equals("")) {
                this.teleportToSelectedBody();
            }
            clickHandled = true;
        }
        final int mouseX = Mouse.getX();
        final int mouseY = Mouse.getY() * -1 + Minecraft.getMinecraft().displayHeight - 1;
        if (this.selectedBody instanceof Satellite) {
            if (this.renamingSpaceStation) {
                if (x >= this.width / 2 - 90 && x <= this.width / 2 + 90 && y >= this.height / 2 - 38 && y <= this.height / 2 + 38) {
                    if (x >= this.width / 2 - 90 + 17 && x <= this.width / 2 - 90 + 17 + 72 && y >= this.height / 2 - 38 + 59 && y <= this.height / 2 - 38 + 59 + 12) {
                        final String strName = this.mc.thePlayer.getGameProfile().getName();
                        final Satellite selectedSatellite = (Satellite)this.selectedBody;
                        Integer spacestationID = this.spaceStationMap.get(this.getSatelliteParentID(selectedSatellite)).get(strName).getStationDimensionID();
                        if (spacestationID == null) {
                            spacestationID = this.spaceStationMap.get(this.getSatelliteParentID(selectedSatellite)).get(strName.toLowerCase()).getStationDimensionID();
                        }
                        if (spacestationID != null) {
                            this.spaceStationMap.get(this.getSatelliteParentID(selectedSatellite)).get(strName).setStationName(this.renamingString);
                            GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(PacketSimple.EnumSimplePacket.S_RENAME_SPACE_STATION, new Object[] { this.renamingString, spacestationID }));
                        }
                        this.renamingSpaceStation = false;
                    }
                    if (x >= this.width / 2 && x <= this.width / 2 + 72 && y >= this.height / 2 - 38 + 59 && y <= this.height / 2 - 38 + 59 + 12) {
                        this.renamingSpaceStation = false;
                    }
                    clickHandled = true;
                }
            }
            else {
                this.drawTexturedModalRect(this.width / 2 - 47, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH, 94, 11, 0, 414, 188, 22, false, false);
                if (x >= this.width / 2 - 47 && x <= this.width / 2 - 47 + 94 && y >= GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH && y <= GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 11 && this.selectedStationOwner.length() != 0 && this.selectedStationOwner.equalsIgnoreCase(this.mc.thePlayer.getGameProfile().getName())) {
                    this.renamingSpaceStation = true;
                    this.renamingString = null;
                    clickHandled = true;
                }
                final Satellite selectedSatellite2 = (Satellite)this.selectedBody;
                final int stationListSize = this.spaceStationMap.get(this.getSatelliteParentID(selectedSatellite2)).size();
                final int max = Math.min(this.height / 2 / 14, stationListSize);
                int xPos = this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 85;
                int yPos = GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 45;
                if (x >= xPos && x <= xPos + 61 && y >= yPos && y <= yPos + 4) {
                    if (this.spaceStationListOffset > 0) {
                        --this.spaceStationListOffset;
                    }
                    clickHandled = true;
                }
                xPos = this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 85;
                yPos = GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 49 + max * 14;
                if (x >= xPos && x <= xPos + 61 && y >= yPos && y <= yPos + 4) {
                    if (max + this.spaceStationListOffset < stationListSize) {
                        ++this.spaceStationListOffset;
                    }
                    clickHandled = true;
                }
                final Iterator<Map.Entry<String, StationDataGUI>> it = this.spaceStationMap.get(this.getSatelliteParentID(selectedSatellite2)).entrySet().iterator();
                int i = 0;
                int j = 0;
                while (it.hasNext() && i < max) {
                    final Map.Entry<String, StationDataGUI> e = it.next();
                    if (j >= this.spaceStationListOffset) {
                        int xOffset = 0;
                        if (e.getKey().equalsIgnoreCase(this.selectedStationOwner)) {
                            xOffset -= 5;
                        }
                        xPos = this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 95 + xOffset;
                        yPos = GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 50 + i * 14;
                        if (x >= xPos && x <= xPos + 93 && y >= yPos && y <= yPos + 12) {
                            this.selectedStationOwner = e.getKey();
                            clickHandled = true;
                        }
                        ++i;
                    }
                    ++j;
                }
            }
        }
        final List<CelestialBody> children = this.getChildren(this.selectedParent);
        for (int k = 0; k < children.size(); ++k) {
            final CelestialBody child = children.get(k);
            int xOffset2 = 0;
            if (child.equals((Object)this.selectedBody)) {
                xOffset2 += 4;
            }
            final int xPos2 = GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 2 + xOffset2;
            final int yPos2 = GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 50 + k * 14;
            if (x >= xPos2 && x <= xPos2 + 93 && y >= yPos2 && y <= yPos2 + 12 && (this.selectedBody != child || this.selectionCount < 2)) {
                if (this.selectedBody == null) {
                    this.preSelectZoom = this.zoom;
                    this.preSelectPosition = this.position;
                }
                final int selectionCountOld = this.selectionCount;
                if (this.selectionCount > 0 && this.selectedBody != child) {
                    this.unselectCelestialBody();
                }
                if (selectionCountOld == 2) {
                    this.selectionCount = 1;
                }
                this.doneZooming = false;
                this.planetZoom = 0.0f;
                if (child != this.selectedBody) {
                    this.lastSelectedBody = this.selectedBody;
                }
                this.selectedBody = child;
                this.ticksSinceSelection = 0;
                ++this.selectionCount;
                clickHandled = true;
                break;
            }
        }
        if (!clickHandled) {
            for (final Map.Entry<CelestialBody, Vector3f> e2 : this.planetPosMap.entrySet()) {
                final CelestialBody bodyClicked = e2.getKey();
                if (this.selectedBody == null && bodyClicked instanceof IChildBody) {
                    continue;
                }
                final float iconSize = e2.getValue().z;
                if (mouseX >= e2.getValue().x - iconSize && mouseX <= e2.getValue().x + iconSize && mouseY >= e2.getValue().y - iconSize && mouseY <= e2.getValue().y + iconSize && (this.selectedBody != bodyClicked || this.selectionCount < 2)) {
                    if (this.selectionCount > 0 && this.selectedBody != bodyClicked) {
                        if (!(this.selectedBody instanceof IChildBody) || ((IChildBody)this.selectedBody).getParentPlanet() != bodyClicked) {
                            this.unselectCelestialBody();
                        }
                        else if (this.selectionCount == 2) {
                            --this.selectionCount;
                        }
                    }
                    this.doneZooming = false;
                    this.planetZoom = 0.0f;
                    if (bodyClicked != this.selectedBody) {
                        this.lastSelectedBody = this.selectedBody;
                    }
                    if (this.selectionCount == 1 && !(bodyClicked instanceof IChildBody)) {
                        this.preSelectZoom = this.zoom;
                        this.preSelectPosition = this.position;
                    }
                    this.selectedBody = bodyClicked;
                    this.ticksSinceSelection = 0;
                    ++this.selectionCount;
                    if (this.selectedBody instanceof Satellite && this.spaceStationMap.get(this.getSatelliteParentID((Satellite)this.selectedBody)).size() == 1) {
                        final Iterator<Map.Entry<String, StationDataGUI>> it = this.spaceStationMap.get(this.getSatelliteParentID((Satellite)this.selectedBody)).entrySet().iterator();
                        this.selectedStationOwner = it.next().getKey();
                    }
                    clickHandled = true;
                    break;
                }
            }
        }
        if (!clickHandled) {
            if (this.selectedBody != null) {
                this.unselectCelestialBody();
            }
            this.mouseDragging = true;
        }
        Object selectedParent = this.selectedParent;
        if (this.selectedBody instanceof IChildBody) {
            selectedParent = ((IChildBody)this.selectedBody).getParentPlanet();
        }
        else if (this.selectedBody instanceof Planet) {
            selectedParent = ((Planet)this.selectedBody).getParentSolarSystem();
        }
        else if (this.selectedBody == null) {
            selectedParent = GalacticraftCore.solarSystemSol;
        }
        if (this.selectedParent != selectedParent) {
            this.selectedParent = selectedParent;
            this.ticksSinceMenuOpen = 0;
        }
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void drawScreen(final int mousePosX, final int mousePosY, final float partialTicks) {
        if (Mouse.hasWheel()) {
            final float wheel = Mouse.getDWheel() / ((this.selectedBody == null) ? 500.0f : 250.0f);
            if (wheel != 0.0f) {
                if (this.selectedBody == null || (this.selectionState == EnumSelectionState.PREVIEW && this.selectionCount < 2)) {
                    this.zoom = Math.min(Math.max(this.zoom + wheel * (this.zoom + 2.0f) / 10.0f, -1.0f), 3.0f);
                }
                else {
                    this.planetZoom = Math.min(Math.max(this.planetZoom + wheel, -4.9f), 5.0f);
                }
            }
        }
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        final Matrix4f camMatrix = new Matrix4f();
        Matrix4f.translate(new Vector3f(0.0f, 0.0f, -9000.0f), camMatrix, camMatrix);
        final Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.m00 = 2.0f / this.width;
        viewMatrix.m11 = 2.0f / -this.height;
        viewMatrix.m22 = -2.2222222E-4f;
        viewMatrix.m30 = -1.0f;
        viewMatrix.m31 = 1.0f;
        viewMatrix.m32 = -2.0f;
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        final FloatBuffer fb = BufferUtils.createFloatBuffer(512);
        fb.rewind();
        viewMatrix.store(fb);
        fb.flip();
        GL11.glMultMatrix(fb);
        fb.clear();
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        fb.rewind();
        camMatrix.store(fb);
        fb.flip();
        fb.clear();
        GL11.glMultMatrix(fb);
        this.setBlackBackground();
        GL11.glPushMatrix();
        final Matrix4f worldMatrix = this.setIsometric(partialTicks);
        this.mainWorldMatrix = worldMatrix;
        final float gridSize = 7000.0f;
        this.drawGrid(gridSize, this.height / 3 / 3.5f);
        this.drawCircles();
        GL11.glPopMatrix();
        final HashMap<CelestialBody, Matrix4f> matrixMap = this.drawCelestialBodies(worldMatrix);
        this.planetPosMap.clear();
        for (final Map.Entry<CelestialBody, Matrix4f> e : matrixMap.entrySet()) {
            final Matrix4f planetMatrix = e.getValue();
            final Matrix4f matrix0 = Matrix4f.mul(viewMatrix, planetMatrix, planetMatrix);
            final int x = (int)Math.floor((matrix0.m30 * 0.5 + 0.5) * Minecraft.getMinecraft().displayWidth);
            final int y = (int)Math.floor(Minecraft.getMinecraft().displayHeight - (matrix0.m31 * 0.5 + 0.5) * Minecraft.getMinecraft().displayHeight);
            final Vector2f vec = new Vector2f((float)x, (float)y);
            final Matrix4f scaleVec = new Matrix4f();
            scaleVec.m00 = matrix0.m00;
            scaleVec.m11 = matrix0.m11;
            scaleVec.m22 = matrix0.m22;
            final Vector4f newVec = Matrix4f.transform(scaleVec, new Vector4f(2.0f, -2.0f, 0.0f, 0.0f), (Vector4f)null);
            final float iconSize = newVec.y * (Minecraft.getMinecraft().displayHeight / 2.0f) * ((e.getKey() instanceof Star) ? 2 : 1) * ((e.getKey() == this.selectedBody) ? 1.5f : 1.0f);
            this.planetPosMap.put(e.getKey(), new Vector3f(vec.x, vec.y, iconSize));
        }
        this.drawSelectionCursor(fb, worldMatrix);
        try {
            this.drawButtons(mousePosX, mousePosY);
        }
        catch (Exception e2) {
            if (!this.errorLogged) {
                this.errorLogged = true;
                GCLog.severe("Problem identifying planet or dimension in an add on for Galacticraft!");
                GCLog.severe("(The problem is likely caused by a dimension ID conflict.  Check configs for dimension clashes.  You can also try disabling Mars space station in configs.)");
                e2.printStackTrace();
            }
        }
        this.drawBorder();
        GL11.glPopMatrix();
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
    }

    protected void drawSelectionCursor(final FloatBuffer fb, final Matrix4f worldMatrix) {
        switch (this.selectionCount) {
            case 1: {
                if (this.selectedBody != null) {
                    GL11.glPushMatrix();
                    final Matrix4f worldMatrix2 = new Matrix4f(worldMatrix);
                    Matrix4f.translate(this.getCelestialBodyPosition(this.selectedBody), worldMatrix2, worldMatrix2);
                    Matrix4f worldMatrix3 = new Matrix4f();
                    Matrix4f.rotate((float)Math.toRadians(45.0), new Vector3f(0.0f, 0.0f, 1.0f), worldMatrix3, worldMatrix3);
                    Matrix4f.rotate((float)Math.toRadians(-55.0), new Vector3f(1.0f, 0.0f, 0.0f), worldMatrix3, worldMatrix3);
                    worldMatrix3 = Matrix4f.mul(worldMatrix2, worldMatrix3, worldMatrix3);
                    fb.rewind();
                    worldMatrix3.store(fb);
                    fb.flip();
                    GL11.glMultMatrix(fb);
                    fb.clear();
                    GL11.glScalef(0.06666667f, 0.06666667f, 1.0f);
                    this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
                    final float colMod = (this.getZoomAdvanced() < 4.9f) ? ((float)(Math.sin(this.ticksSinceSelection / 2.0f) * 0.5 + 0.5)) : 1.0f;
                    GL11.glColor4f(1.0f, 1.0f, 0.0f, 1.0f * colMod);
                    int width = getWidthForCelestialBodyStatic(this.selectedBody);
                    if (this.selectionCount == 1) {
                        width /= 2;
                        width *= 3;
                    }
                    width *= 10;
                    this.drawTexturedModalRect(-width, -width, width * 2, width * 2, 266, 29, 100, 100, false, false);
                    GL11.glPopMatrix();
                    break;
                }
                break;
            }
            case 2: {
                if (this.selectedBody != null) {
                    GL11.glPushMatrix();
                    final Matrix4f worldMatrix2 = new Matrix4f(worldMatrix);
                    Matrix4f.translate(this.getCelestialBodyPosition(this.selectedBody), worldMatrix2, worldMatrix2);
                    Matrix4f worldMatrix3 = new Matrix4f();
                    Matrix4f.rotate((float)Math.toRadians(45.0), new Vector3f(0.0f, 0.0f, 1.0f), worldMatrix3, worldMatrix3);
                    Matrix4f.rotate((float)Math.toRadians(-55.0), new Vector3f(1.0f, 0.0f, 0.0f), worldMatrix3, worldMatrix3);
                    worldMatrix3 = Matrix4f.mul(worldMatrix2, worldMatrix3, worldMatrix3);
                    fb.rewind();
                    worldMatrix3.store(fb);
                    fb.flip();
                    GL11.glMultMatrix(fb);
                    fb.clear();
                    float div = this.zoom + 1.0f - this.planetZoom;
                    final float scale = Math.max(0.3f, 1.5f / (this.ticksSinceSelection / 5.0f)) * 2.0f / div;
                    div = Math.max(div, 1.0E-4f);
                    GL11.glScalef(scale, scale, 1.0f);
                    this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
                    final float colMod2 = (this.getZoomAdvanced() < 4.9f) ? ((float)(Math.sin(this.ticksSinceSelection / 1.0f) * 0.5 + 0.5)) : 1.0f;
                    GL11.glColor4f(0.4f, 0.8f, 1.0f, 1.0f * colMod2);
                    this.drawTexturedModalRect(-50, -50, 100, 100, 266, 29, 100, 100, false, false);
                    GL11.glPopMatrix();
                    break;
                }
                break;
            }
        }
    }

    protected Vector3f getCelestialBodyPosition(final CelestialBody cBody) {
        if (cBody instanceof Star) {
            if (cBody.getUnlocalizedName().equalsIgnoreCase("star.sol")) {
                return new Vector3f();
            }
            return ((Star)cBody).getParentSolarSystem().getMapPosition().toVector3f();
        }
        else {
            final int cBodyTicks = this.celestialBodyTicks.get(cBody);
            final float timeScale = (cBody instanceof Planet) ? 200.0f : 2.0f;
            final float distanceFromCenter = this.getScale(cBody);
            final Vector3f cBodyPos = new Vector3f((float)Math.sin(cBodyTicks / (timeScale * cBody.getRelativeOrbitTime()) + cBody.getPhaseShift()) * distanceFromCenter, (float)Math.cos(cBodyTicks / (timeScale * cBody.getRelativeOrbitTime()) + cBody.getPhaseShift()) * distanceFromCenter, 0.0f);
            if (cBody instanceof Planet) {
                final Vector3f parentVec = this.getCelestialBodyPosition((CelestialBody)((Planet)cBody).getParentSolarSystem().getMainStar());
                return Vector3f.add(cBodyPos, parentVec, (Vector3f)null);
            }
            if (cBody instanceof IChildBody) {
                final Vector3f parentVec = this.getCelestialBodyPosition((CelestialBody)((IChildBody)cBody).getParentPlanet());
                return Vector3f.add(cBodyPos, parentVec, (Vector3f)null);
            }
            if (cBody instanceof Satellite) {
                final Vector3f parentVec = this.getCelestialBodyPosition((CelestialBody)((Satellite)cBody).getParentPlanet());
                return Vector3f.add(cBodyPos, parentVec, (Vector3f)null);
            }
            return cBodyPos;
        }
    }

    public static int getWidthForCelestialBodyStatic(final CelestialBody celestialBody) {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiCelestialSelection && (celestialBody != ((GuiCelestialSelection)Minecraft.getMinecraft().currentScreen).selectedBody || ((GuiCelestialSelection)Minecraft.getMinecraft().currentScreen).selectionCount != 1)) {
            return (celestialBody instanceof Star) ? 8 : ((celestialBody instanceof Planet) ? 4 : ((celestialBody instanceof IChildBody) ? 4 : ((celestialBody instanceof Satellite) ? 4 : 2)));
        }
        return (celestialBody instanceof Star) ? 12 : ((celestialBody instanceof Planet) ? 6 : ((celestialBody instanceof IChildBody) ? 6 : ((celestialBody instanceof Satellite) ? 6 : 2)));
    }

    public HashMap<CelestialBody, Matrix4f> drawCelestialBodies(final Matrix4f worldMatrix) {
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        final FloatBuffer fb = BufferUtils.createFloatBuffer(512);
        final HashMap<CelestialBody, Matrix4f> matrixMap = Maps.newHashMap();
        for (final SolarSystem solarSystem : GalaxyRegistry.getRegisteredSolarSystems().values()) {
            final Star star = solarSystem.getMainStar();
            if (star != null && star.getBodyIcon() != null) {
                GL11.glPushMatrix();
                final Matrix4f worldMatrix2 = new Matrix4f(worldMatrix);
                Matrix4f.translate(this.getCelestialBodyPosition((CelestialBody)star), worldMatrix2, worldMatrix2);
                Matrix4f worldMatrix3 = new Matrix4f();
                Matrix4f.rotate((float)Math.toRadians(45.0), new Vector3f(0.0f, 0.0f, 1.0f), worldMatrix3, worldMatrix3);
                Matrix4f.rotate((float)Math.toRadians(-55.0), new Vector3f(1.0f, 0.0f, 0.0f), worldMatrix3, worldMatrix3);
                worldMatrix3 = Matrix4f.mul(worldMatrix2, worldMatrix3, worldMatrix3);
                fb.rewind();
                worldMatrix3.store(fb);
                fb.flip();
                GL11.glMultMatrix(fb);
                float alpha = 1.0f;
                if (this.selectedBody != null && this.selectedBody != star && this.selectionCount >= 2) {
                    alpha = 1.0f - Math.min(this.ticksSinceSelection / 25.0f, 1.0f);
                }
                if (this.selectedBody != null && this.selectionCount >= 2 && star != this.selectedBody) {
                    alpha = 1.0f - Math.min(this.ticksSinceSelection / 25.0f, 1.0f);
                    if (!(this.lastSelectedBody instanceof Star) && this.lastSelectedBody != null) {
                        alpha = 0.0f;
                    }
                }
                if (alpha != 0.0f) {
                    final CelestialBodyRenderEvent.Pre preEvent = new CelestialBodyRenderEvent.Pre((CelestialBody)star, star.getBodyIcon(), 8);
                    MinecraftForge.EVENT_BUS.post((Event)preEvent);
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
                    if (preEvent.celestialBodyTexture != null) {
                        this.mc.renderEngine.bindTexture(preEvent.celestialBodyTexture);
                    }
                    if (!preEvent.isCanceled()) {
                        int size = getWidthForCelestialBodyStatic((CelestialBody)star);
                        if (star == this.selectedBody && this.selectionCount == 1) {
                            size /= 2;
                            size *= 3;
                        }
                        this.drawTexturedModalRect((float)(-size / 2), (float)(-size / 2), (float)size, (float)size, 0.0f, 0.0f, (float)preEvent.textureSize, (float)preEvent.textureSize, false, false, (float)preEvent.textureSize, (float)preEvent.textureSize);
                        matrixMap.put((CelestialBody)star, worldMatrix3);
                    }
                    final CelestialBodyRenderEvent.Post postEvent = new CelestialBodyRenderEvent.Post((CelestialBody)star);
                    MinecraftForge.EVENT_BUS.post((Event)postEvent);
                }
                fb.clear();
                GL11.glPopMatrix();
            }
        }
        for (final Planet planet : GalaxyRegistry.getRegisteredPlanets().values()) {
            if (planet.getBodyIcon() != null) {
                GL11.glPushMatrix();
                final Matrix4f worldMatrix4 = new Matrix4f(worldMatrix);
                Matrix4f.translate(this.getCelestialBodyPosition((CelestialBody)planet), worldMatrix4, worldMatrix4);
                Matrix4f worldMatrix5 = new Matrix4f();
                Matrix4f.rotate((float)Math.toRadians(45.0), new Vector3f(0.0f, 0.0f, 1.0f), worldMatrix5, worldMatrix5);
                Matrix4f.rotate((float)Math.toRadians(-55.0), new Vector3f(1.0f, 0.0f, 0.0f), worldMatrix5, worldMatrix5);
                worldMatrix5 = Matrix4f.mul(worldMatrix4, worldMatrix5, worldMatrix5);
                fb.rewind();
                worldMatrix5.store(fb);
                fb.flip();
                GL11.glMultMatrix(fb);
                float alpha2 = 1.0f;
                if ((this.selectedBody instanceof IChildBody && ((IChildBody)this.selectedBody).getParentPlanet() != planet) || (this.selectedBody instanceof Planet && this.selectedBody != planet && this.selectionCount >= 2)) {
                    if (this.lastSelectedBody == null && !(this.selectedBody instanceof IChildBody)) {
                        alpha2 = 1.0f - Math.min(this.ticksSinceSelection / 25.0f, 1.0f);
                    }
                    else {
                        alpha2 = 0.0f;
                    }
                }
                if (alpha2 != 0.0f) {
                    final CelestialBodyRenderEvent.Pre preEvent2 = new CelestialBodyRenderEvent.Pre((CelestialBody)planet, planet.getBodyIcon(), 12);
                    MinecraftForge.EVENT_BUS.post((Event)preEvent2);
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha2);
                    if (preEvent2.celestialBodyTexture != null) {
                        this.mc.renderEngine.bindTexture(preEvent2.celestialBodyTexture);
                    }
                    if (!preEvent2.isCanceled()) {
                        final int size2 = getWidthForCelestialBodyStatic((CelestialBody)planet);
                        this.drawTexturedModalRect((float)(-size2 / 2), (float)(-size2 / 2), (float)size2, (float)size2, 0.0f, 0.0f, (float)preEvent2.textureSize, (float)preEvent2.textureSize, false, false, (float)preEvent2.textureSize, (float)preEvent2.textureSize);
                        matrixMap.put((CelestialBody)planet, worldMatrix5);
                    }
                    final CelestialBodyRenderEvent.Post postEvent2 = new CelestialBodyRenderEvent.Post((CelestialBody)planet);
                    MinecraftForge.EVENT_BUS.post((Event)postEvent2);
                }
                fb.clear();
                GL11.glPopMatrix();
            }
        }
        if (this.selectedBody != null) {
            final Matrix4f worldMatrix6 = new Matrix4f(worldMatrix);
            for (final Moon moon : GalaxyRegistry.getRegisteredMoons().values()) {
                if (((moon == this.selectedBody || (moon.getParentPlanet() == this.selectedBody && this.selectionCount != 1)) && (this.ticksSinceSelection > 35 || this.selectedBody == moon || (this.lastSelectedBody instanceof Moon && GalaxyRegistry.getMoonsForPlanet(((Moon)this.lastSelectedBody).getParentPlanet()).contains(moon)))) || this.getSiblings(this.selectedBody).contains(moon)) {
                    GL11.glPushMatrix();
                    final Matrix4f worldMatrix5 = new Matrix4f(worldMatrix6);
                    Matrix4f.translate(this.getCelestialBodyPosition((CelestialBody)moon), worldMatrix5, worldMatrix5);
                    Matrix4f worldMatrix7 = new Matrix4f();
                    Matrix4f.rotate((float)Math.toRadians(45.0), new Vector3f(0.0f, 0.0f, 1.0f), worldMatrix7, worldMatrix7);
                    Matrix4f.rotate((float)Math.toRadians(-55.0), new Vector3f(1.0f, 0.0f, 0.0f), worldMatrix7, worldMatrix7);
                    Matrix4f.scale(new Vector3f(0.25f, 0.25f, 1.0f), worldMatrix7, worldMatrix7);
                    worldMatrix7 = Matrix4f.mul(worldMatrix5, worldMatrix7, worldMatrix7);
                    fb.rewind();
                    worldMatrix7.store(fb);
                    fb.flip();
                    GL11.glMultMatrix(fb);
                    final CelestialBodyRenderEvent.Pre preEvent2 = new CelestialBodyRenderEvent.Pre((CelestialBody)moon, moon.getBodyIcon(), 8);
                    MinecraftForge.EVENT_BUS.post((Event)preEvent2);
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    if (preEvent2.celestialBodyTexture != null) {
                        this.mc.renderEngine.bindTexture(preEvent2.celestialBodyTexture);
                    }
                    if (!preEvent2.isCanceled()) {
                        final int size2 = getWidthForCelestialBodyStatic((CelestialBody)moon);
                        this.drawTexturedModalRect((float)(-size2 / 2), (float)(-size2 / 2), (float)size2, (float)size2, 0.0f, 0.0f, (float)preEvent2.textureSize, (float)preEvent2.textureSize, false, false, (float)preEvent2.textureSize, (float)preEvent2.textureSize);
                        matrixMap.put((CelestialBody)moon, worldMatrix5);
                    }
                    final CelestialBodyRenderEvent.Post postEvent2 = new CelestialBodyRenderEvent.Post((CelestialBody)moon);
                    MinecraftForge.EVENT_BUS.post((Event)postEvent2);
                    fb.clear();
                    GL11.glPopMatrix();
                }
            }
        }
        if (this.selectedBody != null) {
            final Matrix4f worldMatrix6 = new Matrix4f(worldMatrix);
            for (final Satellite satellite : GalaxyRegistry.getRegisteredSatellites().values()) {
                if (this.possibleBodies != null && this.possibleBodies.contains(satellite) && (satellite == this.selectedBody || (satellite.getParentPlanet() == this.selectedBody && this.selectionCount != 1)) && (this.ticksSinceSelection > 35 || this.selectedBody == satellite || (this.lastSelectedBody instanceof Satellite && GalaxyRegistry.getSatellitesForCelestialBody((CelestialBody)((Satellite)this.lastSelectedBody).getParentPlanet()).contains(satellite)))) {
                    GL11.glPushMatrix();
                    final Matrix4f worldMatrix5 = new Matrix4f(worldMatrix6);
                    Matrix4f.translate(this.getCelestialBodyPosition((CelestialBody)satellite), worldMatrix5, worldMatrix5);
                    Matrix4f worldMatrix7 = new Matrix4f();
                    Matrix4f.rotate((float)Math.toRadians(45.0), new Vector3f(0.0f, 0.0f, 1.0f), worldMatrix7, worldMatrix7);
                    Matrix4f.rotate((float)Math.toRadians(-55.0), new Vector3f(1.0f, 0.0f, 0.0f), worldMatrix7, worldMatrix7);
                    Matrix4f.scale(new Vector3f(0.25f, 0.25f, 1.0f), worldMatrix7, worldMatrix7);
                    worldMatrix7 = Matrix4f.mul(worldMatrix5, worldMatrix7, worldMatrix7);
                    fb.rewind();
                    worldMatrix7.store(fb);
                    fb.flip();
                    GL11.glMultMatrix(fb);
                    final CelestialBodyRenderEvent.Pre preEvent2 = new CelestialBodyRenderEvent.Pre((CelestialBody)satellite, satellite.getBodyIcon(), 8);
                    MinecraftForge.EVENT_BUS.post((Event)preEvent2);
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    this.mc.renderEngine.bindTexture(preEvent2.celestialBodyTexture);
                    if (!preEvent2.isCanceled()) {
                        final int size2 = getWidthForCelestialBodyStatic((CelestialBody)satellite);
                        this.drawTexturedModalRect((float)(-size2 / 2), (float)(-size2 / 2), (float)size2, (float)size2, 0.0f, 0.0f, (float)preEvent2.textureSize, (float)preEvent2.textureSize, false, false, (float)preEvent2.textureSize, (float)preEvent2.textureSize);
                        matrixMap.put((CelestialBody)satellite, worldMatrix5);
                    }
                    final CelestialBodyRenderEvent.Post postEvent2 = new CelestialBodyRenderEvent.Post((CelestialBody)satellite);
                    MinecraftForge.EVENT_BUS.post((Event)postEvent2);
                    fb.clear();
                    GL11.glPopMatrix();
                }
            }
        }
        return matrixMap;
    }

    public void drawBorder() {
        Gui.drawRect(0, 0, GuiCelestialSelection.BORDER_WIDTH, this.height, ColorUtil.to32BitColor(255, 100, 100, 100));
        Gui.drawRect(this.width - GuiCelestialSelection.BORDER_WIDTH, 0, this.width, this.height, ColorUtil.to32BitColor(255, 100, 100, 100));
        Gui.drawRect(0, 0, this.width, GuiCelestialSelection.BORDER_WIDTH, ColorUtil.to32BitColor(255, 100, 100, 100));
        Gui.drawRect(0, this.height - GuiCelestialSelection.BORDER_WIDTH, this.width, this.height, ColorUtil.to32BitColor(255, 100, 100, 100));
        Gui.drawRect(GuiCelestialSelection.BORDER_WIDTH, GuiCelestialSelection.BORDER_WIDTH, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH, this.height - GuiCelestialSelection.BORDER_WIDTH, ColorUtil.to32BitColor(255, 40, 40, 40));
        Gui.drawRect(GuiCelestialSelection.BORDER_WIDTH, GuiCelestialSelection.BORDER_WIDTH, this.width - GuiCelestialSelection.BORDER_WIDTH, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH, ColorUtil.to32BitColor(255, 40, 40, 40));
        Gui.drawRect(this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH, GuiCelestialSelection.BORDER_WIDTH, this.width - GuiCelestialSelection.BORDER_WIDTH, this.height - GuiCelestialSelection.BORDER_WIDTH, ColorUtil.to32BitColor(255, 80, 80, 80));
        Gui.drawRect(GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH, this.height - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH, this.width - GuiCelestialSelection.BORDER_WIDTH, this.height - GuiCelestialSelection.BORDER_WIDTH, ColorUtil.to32BitColor(255, 80, 80, 80));
    }

    public void drawButtons(final int mousePosX, final int mousePosY) {
        this.zLevel = 0.0f;
        boolean handledSliderPos = false;
        if (this.selectionState == EnumSelectionState.PROFILE) {
            this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
            GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f);
            this.drawTexturedModalRect(this.width / 2 - 43, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH, 86, 15, 266, 0, 172, 29, false, false);
            String str = GCCoreUtil.translate("gui.message.catalog.name").toUpperCase();
            this.fontRendererObj.drawString(str, this.width / 2 - this.fontRendererObj.getStringWidth(str) / 2, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + this.fontRendererObj.FONT_HEIGHT / 2, ColorUtil.to32BitColor(255, 255, 255, 255));
            if (this.selectedBody != null) {
                this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
                if (mousePosX > GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH && mousePosX < GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 88 && mousePosY > GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH && mousePosY < GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 13) {
                    GL11.glColor3f(3.0f, 0.0f, 0.0f);
                }
                else {
                    GL11.glColor3f(0.9f, 0.2f, 0.2f);
                }
                this.drawTexturedModalRect(GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH, 88, 13, 0, 392, 148, 22, false, false);
                str = GCCoreUtil.translate("gui.message.back.name").toUpperCase();
                this.fontRendererObj.drawString(str, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 45 - this.fontRendererObj.getStringWidth(str) / 2, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + this.fontRendererObj.FONT_HEIGHT / 2 - 2, ColorUtil.to32BitColor(255, 255, 255, 255));
                this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
                if (mousePosX > this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 88 && mousePosX < this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH && mousePosY > GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH && mousePosY < GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 13) {
                    GL11.glColor3f(0.0f, 3.0f, 0.0f);
                }
                else {
                    GL11.glColor3f(0.2f, 0.9f, 0.2f);
                }
                this.drawTexturedModalRect(this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 88, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH, 88, 13, 0, 392, 148, 22, true, false);
                GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f);
                this.drawTexturedModalRect(GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH, this.height - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 13, 88, 13, 0, 392, 148, 22, false, true);
                this.drawTexturedModalRect(this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 88, this.height - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 13, 88, 13, 0, 392, 148, 22, true, true);
                final int menuTopLeft = GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH - 115 + this.height / 2 - 4;
                final int posX = GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + Math.min(this.ticksSinceSelection * 10, 133) - 134;
                final int posX2 = (int)(GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + Math.min(this.ticksSinceSelection * 1.25f, 15.0f) - 15.0f);
                final int fontPosY = menuTopLeft + GuiCelestialSelection.BORDER_EDGE_WIDTH + this.fontRendererObj.FONT_HEIGHT / 2 - 2;
                this.drawTexturedModalRect(posX, menuTopLeft + 12, 133, 196, 0, 0, 266, 392, false, false);
                str = GCCoreUtil.translate("gui.message.daynightcycle.name") + ":";
                this.fontRendererObj.drawString(str, posX + 5, fontPosY + 14, ColorUtil.to32BitColor(255, 150, 200, 255));
                str = GCCoreUtil.translate("gui.message." + this.selectedBody.getName() + ".daynightcycle.0.name");
                this.fontRendererObj.drawString(str, posX + 10, fontPosY + 25, ColorUtil.to32BitColor(255, 255, 255, 255));
                str = GCCoreUtil.translate("gui.message." + this.selectedBody.getName() + ".daynightcycle.1.name");
                if (!str.isEmpty()) {
                    this.fontRendererObj.drawString(str, posX + 10, fontPosY + 36, ColorUtil.to32BitColor(255, 255, 255, 255));
                }
                str = GCCoreUtil.translate("gui.message.surfacegravity.name") + ":";
                this.fontRendererObj.drawString(str, posX + 5, fontPosY + 50, ColorUtil.to32BitColor(255, 150, 200, 255));
                str = GCCoreUtil.translate("gui.message." + this.selectedBody.getName() + ".surfacegravity.0.name");
                this.fontRendererObj.drawString(str, posX + 10, fontPosY + 61, ColorUtil.to32BitColor(255, 255, 255, 255));
                str = GCCoreUtil.translate("gui.message." + this.selectedBody.getName() + ".surfacegravity.1.name");
                if (!str.isEmpty()) {
                    this.fontRendererObj.drawString(str, posX + 10, fontPosY + 72, ColorUtil.to32BitColor(255, 255, 255, 255));
                }
                str = GCCoreUtil.translate("gui.message.surfacecomposition.name") + ":";
                this.fontRendererObj.drawString(str, posX + 5, fontPosY + 88, ColorUtil.to32BitColor(255, 150, 200, 255));
                str = GCCoreUtil.translate("gui.message." + this.selectedBody.getName() + ".surfacecomposition.0.name");
                this.fontRendererObj.drawString(str, posX + 10, fontPosY + 99, ColorUtil.to32BitColor(255, 255, 255, 255));
                str = GCCoreUtil.translate("gui.message." + this.selectedBody.getName() + ".surfacecomposition.1.name");
                if (!str.isEmpty()) {
                    this.fontRendererObj.drawString(str, posX + 10, fontPosY + 110, ColorUtil.to32BitColor(255, 255, 255, 255));
                }
                str = GCCoreUtil.translate("gui.message.atmosphere.name") + ":";
                this.fontRendererObj.drawString(str, posX + 5, fontPosY + 126, ColorUtil.to32BitColor(255, 150, 200, 255));
                str = GCCoreUtil.translate("gui.message." + this.selectedBody.getName() + ".atmosphere.0.name");
                this.fontRendererObj.drawString(str, posX + 10, fontPosY + 137, ColorUtil.to32BitColor(255, 255, 255, 255));
                str = GCCoreUtil.translate("gui.message." + this.selectedBody.getName() + ".atmosphere.1.name");
                if (!str.isEmpty()) {
                    this.fontRendererObj.drawString(str, posX + 10, fontPosY + 148, ColorUtil.to32BitColor(255, 255, 255, 255));
                }
                str = GCCoreUtil.translate("gui.message.meansurfacetemp.name") + ":";
                this.fontRendererObj.drawString(str, posX + 5, fontPosY + 165, ColorUtil.to32BitColor(255, 150, 200, 255));
                str = GCCoreUtil.translate("gui.message." + this.selectedBody.getName() + ".meansurfacetemp.0.name");
                this.fontRendererObj.drawString(str, posX + 10, fontPosY + 176, ColorUtil.to32BitColor(255, 255, 255, 255));
                str = GCCoreUtil.translate("gui.message." + this.selectedBody.getName() + ".meansurfacetemp.1.name");
                if (!str.isEmpty()) {
                    this.fontRendererObj.drawString(str, posX + 10, fontPosY + 187, ColorUtil.to32BitColor(255, 255, 255, 255));
                }
                this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
                GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f);
                this.drawTexturedModalRect(posX2, menuTopLeft + 12, 17, 199, 439, 0, 32, 399, false, false);
            }
        }
        else {
            this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
            GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f);
            this.drawTexturedModalRect(GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH, 74, 11, 0, 392, 148, 22, false, false);
            String str = GCCoreUtil.translate("gui.message.catalog.name").toUpperCase();
            this.fontRendererObj.drawString(str, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 40 - this.fontRendererObj.getStringWidth(str) / 2, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 1, ColorUtil.to32BitColor(255, 255, 255, 255));
            int scale = (int)Math.min(95.0f, this.ticksSinceMenuOpen * 12.0f);
            GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f);
            this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
            this.drawTexturedModalRect(GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH - 95 + scale, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 12, 95, 41, 0, 436, 95, 41, false, false);
            str = this.getParentName();
            this.fontRendererObj.drawString(str, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 9 - 95 + scale, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 34, ColorUtil.to32BitColor(255, 255, 255, 255));
            GL11.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
            this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
            this.drawTexturedModalRect(GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 2 - 95 + scale, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 14, 93, 17, 95, 436, 93, 17, false, false);
            str = this.getGrandparentName();
            this.fontRendererObj.drawString(str, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 7 - 95 + scale, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 16, ColorUtil.to32BitColor(255, 120, 120, 120));
            GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f);
            final List<CelestialBody> children = this.getChildren(this.selectedParent);
            for (int i = 0; i < children.size(); ++i) {
                final CelestialBody child = children.get(i);
                int xOffset = 0;
                if (child.equals((Object)this.selectedBody)) {
                    xOffset += 4;
                }
                scale = (int)Math.min(95.0f, Math.max(0.0f, this.ticksSinceMenuOpen * 25.0f - 95 * i));
                this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
                if (child.getReachable()) {
                    GL11.glColor4f(0.0f, 0.6f, 0.0f, scale / 95.0f);
                }
                else {
                    GL11.glColor4f(0.6f, 0.0f, 0.0f, scale / 95.0f);
                }
                this.drawTexturedModalRect(GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 3 + xOffset, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 51 + i * 14, 86, 10, 0, 489, 86, 10, false, false);
                GL11.glColor4f(0.0f, 0.6f, 1.0f, scale / 95.0f);
                this.drawTexturedModalRect(GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 2 + xOffset, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 50 + i * 14, 93, 12, 95, 464, 93, 12, false, false);
                if (scale > 0) {
                    str = child.getLocalizedName();
                    this.fontRendererObj.drawString(str, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 7 + xOffset, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 52 + i * 14, ColorUtil.to32BitColor(255, 255, 255, 255));
                }
            }
            if (this.mapMode) {
                this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
                GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
                this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
                this.drawTexturedModalRect(this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 74, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH, 74, 11, 0, 392, 148, 22, true, false);
                str = GCCoreUtil.translate("gui.message.exit.name").toUpperCase();
                this.fontRendererObj.drawString(str, this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 40 - this.fontRendererObj.getStringWidth(str) / 2, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 1, ColorUtil.to32BitColor(255, 255, 255, 255));
            }
            if (this.selectedBody != null) {
                this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain1);
                GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f);
                if (this.selectedBody instanceof Satellite) {
                    final Satellite selectedSatellite = (Satellite)this.selectedBody;
                    final int stationListSize = this.spaceStationMap.get(this.getSatelliteParentID(selectedSatellite)).size();
                    this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain1);
                    final int max = Math.min(this.height / 2 / 14, stationListSize);
                    this.drawTexturedModalRect(this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 95, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH, 95, 53, (this.selectedStationOwner.length() == 0) ? 95 : 0, 186, 95, 53, false, false);
                    if (this.spaceStationListOffset <= 0) {
                        GL11.glColor4f(0.65f, 0.65f, 0.65f, 1.0f);
                    }
                    else {
                        GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f);
                    }
                    this.drawTexturedModalRect(this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 85, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 45, 61, 4, 0, 239, 61, 4, false, false);
                    if (max + this.spaceStationListOffset >= stationListSize) {
                        GL11.glColor4f(0.65f, 0.65f, 0.65f, 1.0f);
                    }
                    else {
                        GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f);
                    }
                    this.drawTexturedModalRect(this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 85, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 49 + max * 14, 61, 4, 0, 239, 61, 4, false, true);
                    GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f);
                    if (this.spaceStationMap.get(this.getSatelliteParentID(selectedSatellite)).get(this.selectedStationOwner) == null) {
                        str = GCCoreUtil.translate("gui.message.selectSS.name");
                        this.drawSplitString(str, this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 47, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 20, 91, ColorUtil.to32BitColor(255, 255, 255, 255), false, false);
                    }
                    else {
                        str = GCCoreUtil.translate("gui.message.ssOwner.name");
                        this.fontRendererObj.drawString(str, this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 85, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 18, ColorUtil.to32BitColor(255, 255, 255, 255));
                        str = this.selectedStationOwner;
                        this.smallFontRenderer.drawString(str, this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 47 - this.smallFontRenderer.getStringWidth(str) / 2, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 30, ColorUtil.to32BitColor(255, 255, 255, 255));
                    }
                    final Iterator<Map.Entry<String, StationDataGUI>> it = this.spaceStationMap.get(this.getSatelliteParentID(selectedSatellite)).entrySet().iterator();
                    int j = 0;
                    int k = 0;
                    while (it.hasNext() && j < max) {
                        final Map.Entry<String, StationDataGUI> e = it.next();
                        if (k >= this.spaceStationListOffset) {
                            this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
                            GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f);
                            int xOffset2 = 0;
                            if (e.getKey().equalsIgnoreCase(this.selectedStationOwner)) {
                                xOffset2 -= 5;
                            }
                            this.drawTexturedModalRect(this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 95 + xOffset2, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 50 + j * 14, 93, 12, 95, 464, 93, 12, true, false);
                            str = "";
                            final String str2 = e.getValue().getStationName();
                            for (int point = 0; this.smallFontRenderer.getStringWidth(str) < 80 && point < str2.length(); str += str2.substring(point, point + 1), ++point) {}
                            if (this.smallFontRenderer.getStringWidth(str) >= 80) {
                                str = str.substring(0, str.length() - 3);
                                str += "...";
                            }
                            this.smallFontRenderer.drawString(str, this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 88 + xOffset2, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 51 + j * 14, ColorUtil.to32BitColor(255, 255, 255, 255));
                            ++j;
                        }
                        ++k;
                    }
                }
                else {
                    this.drawTexturedModalRect(this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 96, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH, 96, 139, 63, 0, 96, 139, false, false);
                }
                if (this.canCreateSpaceStation(this.selectedBody) && !(this.selectedBody instanceof Satellite)) {
                    GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f);
                    this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain1);
                    final int canCreateLength = Math.max(0, this.drawSplitString(GCCoreUtil.translate("gui.message.canCreateSpaceStation.name"), 0, 0, 91, 0, true, true) - 2);
                    this.canCreateOffset = canCreateLength * this.smallFontRenderer.FONT_HEIGHT;
                    this.drawTexturedModalRect(this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 95, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 134, 93, 4, 159, 102, 93, 4, false, false);
                    for (int barY = 0; barY < canCreateLength; ++barY) {
                        this.drawTexturedModalRect(this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 95, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 138 + barY * this.smallFontRenderer.FONT_HEIGHT, 93, this.smallFontRenderer.FONT_HEIGHT, 159, 106, 93, this.smallFontRenderer.FONT_HEIGHT, false, false);
                    }
                    this.drawTexturedModalRect(this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 95, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 138 + this.canCreateOffset, 93, 43, 159, 106, 93, 43, false, false);
                    this.drawTexturedModalRect(this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 79, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 129, 61, 4, 0, 170, 61, 4, false, false);
                    final SpaceStationRecipe recipe = WorldUtil.getSpaceStationRecipe(this.selectedBody.getDimensionID());
                    if (recipe != null) {
                        GL11.glColor4f(0.0f, 1.0f, 0.1f, 1.0f);
                        boolean validInputMaterials = true;
                        int l = 0;
                        for (final Map.Entry<Object, Integer> e2 : recipe.getInput().entrySet()) {
                            final Object next = e2.getKey();
                            final int xPos = (int)(this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 95 + l * 93 / (double)recipe.getInput().size() + 5.0);
                            final int yPos = GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 154 + this.canCreateOffset;
                            if (next instanceof ItemStack) {
                                final int amount = this.getAmountInInventory((ItemStack)next);
                                RenderHelper.enableGUIStandardItemLighting();
                                GuiCelestialSelection.itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.renderEngine, ((ItemStack)next).copy(), xPos, yPos);
                                RenderHelper.disableStandardItemLighting();
                                GL11.glEnable(3042);
                                if (mousePosX >= xPos && mousePosX <= xPos + 16 && mousePosY >= yPos && mousePosY <= yPos + 16) {
                                    GL11.glDepthMask(true);
                                    GL11.glEnable(2929);
                                    GL11.glPushMatrix();
                                    GL11.glTranslatef(0.0f, 0.0f, 300.0f);
                                    final int m = this.smallFontRenderer.getStringWidth(((ItemStack)next).getDisplayName());
                                    int j2 = mousePosX - m / 2;
                                    int k2 = mousePosY - 12;
                                    final int i2 = 8;
                                    if (j2 + m > this.width) {
                                        j2 -= j2 - this.width + m;
                                    }
                                    if (k2 + i2 + 6 > this.height) {
                                        k2 = this.height - i2 - 6;
                                    }
                                    final int j3 = ColorUtil.to32BitColor(190, 0, 153, 255);
                                    this.drawGradientRect(j2 - 3, k2 - 4, j2 + m + 3, k2 - 3, j3, j3);
                                    this.drawGradientRect(j2 - 3, k2 + i2 + 3, j2 + m + 3, k2 + i2 + 4, j3, j3);
                                    this.drawGradientRect(j2 - 3, k2 - 3, j2 + m + 3, k2 + i2 + 3, j3, j3);
                                    this.drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i2 + 3, j3, j3);
                                    this.drawGradientRect(j2 + m + 3, k2 - 3, j2 + m + 4, k2 + i2 + 3, j3, j3);
                                    final int k3 = ColorUtil.to32BitColor(170, 0, 153, 255);
                                    final int l2 = (k3 & 0xFEFEFE) >> 1 | (k3 & 0xFF000000);
                                    this.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i2 + 3 - 1, k3, l2);
                                    this.drawGradientRect(j2 + m + 2, k2 - 3 + 1, j2 + m + 3, k2 + i2 + 3 - 1, k3, l2);
                                    this.drawGradientRect(j2 - 3, k2 - 3, j2 + m + 3, k2 - 3 + 1, k3, k3);
                                    this.drawGradientRect(j2 - 3, k2 + i2 + 2, j2 + m + 3, k2 + i2 + 3, l2, l2);
                                    this.smallFontRenderer.drawString(((ItemStack)next).getDisplayName(), j2, k2, ColorUtil.to32BitColor(255, 255, 255, 255));
                                    GL11.glPopMatrix();
                                }
                                str = "" + amount + "/" + e2.getValue();
                                final boolean valid = amount >= e2.getValue();
                                if (!valid && validInputMaterials) {
                                    validInputMaterials = false;
                                }
                                final int color = (valid | this.mc.thePlayer.capabilities.isCreativeMode) ? ColorUtil.to32BitColor(255, 0, 255, 0) : ColorUtil.to32BitColor(255, 255, 0, 0);
                                this.smallFontRenderer.drawString(str, xPos + 8 - this.smallFontRenderer.getStringWidth(str) / 2, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 170 + this.canCreateOffset, color);
                            }
                            else if (next instanceof ArrayList) {
                                final ArrayList<ItemStack> items = (ArrayList<ItemStack>)next;
                                int amount2 = 0;
                                for (final ItemStack stack : items) {
                                    amount2 += this.getAmountInInventory(stack);
                                }
                                RenderHelper.enableGUIStandardItemLighting();
                                final ItemStack stack2 = items.get(this.ticksSinceMenuOpen / 20 % items.size()).copy();
                                GuiCelestialSelection.itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.renderEngine, stack2, xPos, yPos);
                                RenderHelper.disableStandardItemLighting();
                                GL11.glEnable(3042);
                                if (mousePosX >= xPos && mousePosX <= xPos + 16 && mousePosY >= yPos && mousePosY <= yPos + 16) {
                                    GL11.glDepthMask(true);
                                    GL11.glEnable(2929);
                                    GL11.glPushMatrix();
                                    GL11.glTranslatef(0.0f, 0.0f, 300.0f);
                                    final int k4 = this.smallFontRenderer.getStringWidth(stack2.getDisplayName());
                                    int j4 = mousePosX - k4 / 2;
                                    int k5 = mousePosY - 12;
                                    final int i3 = 8;
                                    if (j4 + k4 > this.width) {
                                        j4 -= j4 - this.width + k4;
                                    }
                                    if (k5 + i3 + 6 > this.height) {
                                        k5 = this.height - i3 - 6;
                                    }
                                    final int j5 = ColorUtil.to32BitColor(190, 0, 153, 255);
                                    this.drawGradientRect(j4 - 3, k5 - 4, j4 + k4 + 3, k5 - 3, j5, j5);
                                    this.drawGradientRect(j4 - 3, k5 + i3 + 3, j4 + k4 + 3, k5 + i3 + 4, j5, j5);
                                    this.drawGradientRect(j4 - 3, k5 - 3, j4 + k4 + 3, k5 + i3 + 3, j5, j5);
                                    this.drawGradientRect(j4 - 4, k5 - 3, j4 - 3, k5 + i3 + 3, j5, j5);
                                    this.drawGradientRect(j4 + k4 + 3, k5 - 3, j4 + k4 + 4, k5 + i3 + 3, j5, j5);
                                    final int k6 = ColorUtil.to32BitColor(170, 0, 153, 255);
                                    final int l3 = (k6 & 0xFEFEFE) >> 1 | (k6 & 0xFF000000);
                                    this.drawGradientRect(j4 - 3, k5 - 3 + 1, j4 - 3 + 1, k5 + i3 + 3 - 1, k6, l3);
                                    this.drawGradientRect(j4 + k4 + 2, k5 - 3 + 1, j4 + k4 + 3, k5 + i3 + 3 - 1, k6, l3);
                                    this.drawGradientRect(j4 - 3, k5 - 3, j4 + k4 + 3, k5 - 3 + 1, k6, k6);
                                    this.drawGradientRect(j4 - 3, k5 + i3 + 2, j4 + k4 + 3, k5 + i3 + 3, l3, l3);
                                    this.smallFontRenderer.drawString(stack2.getDisplayName(), j4, k5, ColorUtil.to32BitColor(255, 255, 255, 255));
                                    GL11.glPopMatrix();
                                }
                                str = "" + amount2 + "/" + e2.getValue();
                                final boolean valid2 = amount2 >= e2.getValue();
                                if (!valid2 && validInputMaterials) {
                                    validInputMaterials = false;
                                }
                                final int color2 = (valid2 | this.mc.thePlayer.capabilities.isCreativeMode) ? ColorUtil.to32BitColor(255, 0, 255, 0) : ColorUtil.to32BitColor(255, 255, 0, 0);
                                this.smallFontRenderer.drawString(str, xPos + 8 - this.smallFontRenderer.getStringWidth(str) / 2, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 170 + this.canCreateOffset, color2);
                            }
                            ++l;
                        }
                        if (validInputMaterials || this.mc.thePlayer.capabilities.isCreativeMode) {
                            GL11.glColor4f(0.0f, 1.0f, 0.1f, 1.0f);
                        }
                        else {
                            GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
                        }
                        this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain1);
                        if (!this.mapMode && mousePosX >= this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 95 && mousePosX <= this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH && mousePosY >= GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 182 + this.canCreateOffset && mousePosY <= GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 182 + 12 + this.canCreateOffset) {
                            this.drawTexturedModalRect(this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 95, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 182 + this.canCreateOffset, 93, 12, 0, 174, 93, 12, false, false);
                        }
                        this.drawTexturedModalRect(this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 95, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 182 + this.canCreateOffset, 93, 12, 0, 174, 93, 12, false, false);
                        final int color3 = (int)((Math.sin(this.ticksSinceMenuOpen / 5.0) * 0.5 + 0.5) * 255.0);
                        this.drawSplitString(GCCoreUtil.translate("gui.message.canCreateSpaceStation.name"), this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 48, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 137, 91, ColorUtil.to32BitColor(255, color3, 255, color3), true, false);
                        if (!this.mapMode) {
                            this.drawSplitString(GCCoreUtil.translate("gui.message.createSS.name").toUpperCase(), this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 48, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 185 + this.canCreateOffset, 91, ColorUtil.to32BitColor(255, 255, 255, 255), false, false);
                        }
                    }
                    else {
                        this.drawSplitString(GCCoreUtil.translate("gui.message.cannotCreateSpaceStation.name"), this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 48, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 138, 91, ColorUtil.to32BitColor(255, 255, 255, 255), true, false);
                    }
                }
                this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.3f - Math.min(0.3f, this.ticksSinceSelection / 50.0f));
                this.drawTexturedModalRect(GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH, 74, 11, 0, 392, 148, 22, false, false);
                str = GCCoreUtil.translate("gui.message.catalog.name").toUpperCase();
                this.fontRendererObj.drawString(str, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 40 - this.fontRendererObj.getStringWidth(str) / 2, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 1, ColorUtil.to32BitColor(255, 255, 255, 255));
                this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
                GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f);
                if (this.selectedBody instanceof Satellite) {
                    if (this.selectedStationOwner.length() == 0 || !this.selectedStationOwner.equalsIgnoreCase(this.mc.thePlayer.getGameProfile().getName())) {
                        GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
                    }
                    else {
                        GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
                    }
                    this.drawTexturedModalRect(this.width / 2 - 47, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH, 94, 11, 0, 414, 188, 22, false, false);
                }
                else {
                    this.drawTexturedModalRect(this.width / 2 - 47, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH, 94, 11, 0, 414, 188, 22, false, false);
                }
                if (this.selectedBody.getTierRequirement() >= 0 && !(this.selectedBody instanceof Satellite)) {
                    boolean canReach;
                    if (!this.selectedBody.getReachable() || (this.possibleBodies != null && !this.possibleBodies.contains(this.selectedBody))) {
                        canReach = false;
                        GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
                    }
                    else {
                        canReach = true;
                        GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
                    }
                    this.drawTexturedModalRect(this.width / 2 - 30, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 11, 30, 11, 0, 414, 60, 22, false, false);
                    this.drawTexturedModalRect(this.width / 2, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 11, 30, 11, 128, 414, 60, 22, false, false);
                    str = GCCoreUtil.translateWithFormat("gui.message.tier.name", (this.selectedBody.getTierRequirement() == 0) ? "?" : Integer.valueOf(this.selectedBody.getTierRequirement()));
                    this.fontRendererObj.drawString(str, this.width / 2 - this.fontRendererObj.getStringWidth(str) / 2, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 13, canReach ? ColorUtil.to32BitColor(255, 140, 140, 140) : ColorUtil.to32BitColor(255, 255, 100, 100));
                }
                str = this.selectedBody.getLocalizedName();
                if (this.selectedBody instanceof Satellite) {
                    str = GCCoreUtil.translate("gui.message.rename.name").toUpperCase();
                }
                this.fontRendererObj.drawString(str, this.width / 2 - this.fontRendererObj.getStringWidth(str) / 2, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 2, ColorUtil.to32BitColor(255, 255, 255, 255));
                this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
                GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f);
                this.drawTexturedModalRect(GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 4, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH, 83, 12, 0, 477, 83, 12, false, false);
                if (!this.mapMode) {
                    if (!this.selectedBody.getReachable() || (this.possibleBodies != null && !this.possibleBodies.contains(this.selectedBody)) || (this.selectedBody instanceof Satellite && this.selectedStationOwner.equals(""))) {
                        GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
                    }
                    else {
                        GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
                    }
                    this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
                    this.drawTexturedModalRect(this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 74, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH, 74, 11, 0, 392, 148, 22, true, false);
                    str = GCCoreUtil.translate("gui.message.launch.name").toUpperCase();
                    this.fontRendererObj.drawString(str, this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 40 - this.fontRendererObj.getStringWidth(str) / 2, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 1, ColorUtil.to32BitColor(255, 255, 255, 255));
                }
                if (this.selectionCount == 1 && !(this.selectedBody instanceof Satellite)) {
                    handledSliderPos = true;
                    int sliderPos = this.zoomTooltipPos;
                    if (this.zoomTooltipPos != 38) {
                        sliderPos = Math.min(this.ticksSinceSelection * 2, 38);
                        this.zoomTooltipPos = sliderPos;
                    }
                    GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f);
                    this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
                    this.drawTexturedModalRect(this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 182, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH - 38 + sliderPos, 83, 38, 346, 436, 166, 76, true, false);
                    final boolean flag0 = GalaxyRegistry.getSatellitesForCelestialBody(this.selectedBody).size() > 0;
                    final boolean flag2 = this.selectedBody instanceof Planet && GalaxyRegistry.getMoonsForPlanet((Planet)this.selectedBody).size() > 0;
                    if (flag0 && flag2) {
                        this.drawSplitString(GCCoreUtil.translate("gui.message.clickAgain.0.name"), this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 182 + 41, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 1 - 38 + sliderPos, 79, ColorUtil.to32BitColor(255, 150, 150, 150), false, false);
                    }
                    else if (!flag0 && flag2) {
                        this.drawSplitString(GCCoreUtil.translate("gui.message.clickAgain.1.name"), this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 182 + 41, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 5 - 38 + sliderPos, 79, ColorUtil.to32BitColor(255, 150, 150, 150), false, false);
                    }
                    else if (flag0) {
                        this.drawSplitString(GCCoreUtil.translate("gui.message.clickAgain.2.name"), this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 182 + 41, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 5 - 38 + sliderPos, 79, ColorUtil.to32BitColor(255, 150, 150, 150), false, false);
                    }
                    else {
                        this.drawSplitString(GCCoreUtil.translate("gui.message.clickAgain.3.name"), this.width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 182 + 41, GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 10 - 38 + sliderPos, 79, ColorUtil.to32BitColor(255, 150, 150, 150), false, false);
                    }
                }
                if (this.selectedBody instanceof Satellite && this.renamingSpaceStation) {
                    this.drawDefaultBackground();
                    GL11.glColor4f(0.0f, 0.6f, 1.0f, 1.0f);
                    this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain1);
                    this.drawTexturedModalRect(this.width / 2 - 90, this.height / 2 - 38, 179, 67, 159, 0, 179, 67, false, false);
                    this.drawTexturedModalRect(this.width / 2 - 90 + 4, this.height / 2 - 38 + 2, 171, 10, 159, 92, 171, 10, false, false);
                    this.drawTexturedModalRect(this.width / 2 - 90 + 8, this.height / 2 - 38 + 18, 161, 13, 159, 67, 161, 13, false, false);
                    this.drawTexturedModalRect(this.width / 2 - 90 + 17, this.height / 2 - 38 + 59, 72, 12, 159, 80, 72, 12, true, false);
                    this.drawTexturedModalRect(this.width / 2, this.height / 2 - 38 + 59, 72, 12, 159, 80, 72, 12, false, false);
                    str = GCCoreUtil.translate("gui.message.assignName.name");
                    this.fontRendererObj.drawString(str, this.width / 2 - this.fontRendererObj.getStringWidth(str) / 2, this.height / 2 - 35, ColorUtil.to32BitColor(255, 255, 255, 255));
                    str = GCCoreUtil.translate("gui.message.apply.name");
                    this.fontRendererObj.drawString(str, this.width / 2 - this.fontRendererObj.getStringWidth(str) / 2 - 36, this.height / 2 + 23, ColorUtil.to32BitColor(255, 255, 255, 255));
                    str = GCCoreUtil.translate("gui.message.cancel.name");
                    this.fontRendererObj.drawString(str, this.width / 2 + 36 - this.fontRendererObj.getStringWidth(str) / 2, this.height / 2 + 23, ColorUtil.to32BitColor(255, 255, 255, 255));
                    if (this.renamingString == null) {
                        final Satellite selectedSatellite = (Satellite)this.selectedBody;
                        final String playerName = FMLClientHandler.instance().getClient().thePlayer.getGameProfile().getName();
                        this.renamingString = this.spaceStationMap.get(this.getSatelliteParentID(selectedSatellite)).get(playerName).getStationName();
                        if (this.renamingString == null) {
                            this.renamingString = this.spaceStationMap.get(this.getSatelliteParentID(selectedSatellite)).get(playerName.toLowerCase()).getStationName();
                        }
                        if (this.renamingString == null) {
                            this.renamingString = "";
                        }
                    }
                    str = this.renamingString;
                    String str3 = this.renamingString;
                    if (this.ticksSinceMenuOpen / 10 % 2 == 0) {
                        str3 += "_";
                    }
                    this.fontRendererObj.drawString(str3, this.width / 2 - this.fontRendererObj.getStringWidth(str) / 2, this.height / 2 - 17, ColorUtil.to32BitColor(255, 255, 255, 255));
                }
            }
        }
        if (!handledSliderPos) {
            this.zoomTooltipPos = 0;
        }
    }

    protected int getAmountInInventory(final ItemStack stack) {
        int amountInInv = 0;
        for (int x = 0; x < FMLClientHandler.instance().getClientPlayerEntity().inventory.getSizeInventory(); ++x) {
            final ItemStack slot = FMLClientHandler.instance().getClientPlayerEntity().inventory.getStackInSlot(x);
            if (slot != null && SpaceStationRecipe.checkItemEquals(stack, slot)) {
                amountInInv += slot.stackSize;
            }
        }
        return amountInInv;
    }

    public int drawSplitString(final String par1Str, final int par2, final int par3, final int par4, final int par5, final boolean small, final boolean simulate) {
        return this.renderSplitString(par1Str, par2, par3, par4, false, par5, small, simulate);
    }

    protected int renderSplitString(final String par1Str, final int par2, int par3, final int par4, final boolean par5, final int par6, final boolean small, final boolean simulate) {
        if (small) {
            final List list = this.smallFontRenderer.listFormattedStringToWidth(par1Str, par4);
            for (final Object s1 : list) {
                if (!simulate) {
                    this.renderStringAligned((String) s1, par2, par3, par4, par6, par5, small);
                }
                par3 += this.smallFontRenderer.FONT_HEIGHT;
            }
            return list.size();
        }
        final List list = this.fontRendererObj.listFormattedStringToWidth(par1Str, par4);
        for (final Object s1 : list) {
            if (!simulate) {
                this.renderStringAligned((String) s1, par2, par3, par4, par6, par5, small);
            }
            par3 += this.fontRendererObj.FONT_HEIGHT;
        }
        return list.size();
    }

    protected int renderStringAligned(final String par1Str, int par2, final int par3, final int par4, final int par5, final boolean par6, final boolean small) {
        if (small) {
            if (this.smallFontRenderer.getBidiFlag()) {
                final int i1 = this.smallFontRenderer.getStringWidth(this.bidiReorder(par1Str));
                par2 = par2 + par4 - i1;
            }
            return this.smallFontRenderer.drawString(par1Str, par2 - this.smallFontRenderer.getStringWidth(par1Str) / 2, par3, par5, par6);
        }
        if (this.fontRendererObj.getBidiFlag()) {
            final int i1 = this.fontRendererObj.getStringWidth(this.bidiReorder(par1Str));
            par2 = par2 + par4 - i1;
        }
        return this.fontRendererObj.drawString(par1Str, par2 - this.fontRendererObj.getStringWidth(par1Str) / 2, par3, par5, par6);
    }

    protected String bidiReorder(final String p_147647_1_) {
        try {
            final Bidi bidi = new Bidi(new ArabicShaping(8).shape(p_147647_1_), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(2);
        }
        catch (ArabicShapingException arabicshapingexception) {
            return p_147647_1_;
        }
    }

    public void drawTexturedModalRect(final int x, final int y, final int width, final int height, final int u, final int v, final int uWidth, final int vHeight, final boolean invertX, final boolean invertY) {
        this.drawTexturedModalRect((float)x, (float)y, (float)width, (float)height, (float)u, (float)v, (float)uWidth, (float)vHeight, invertX, invertY, 512.0f, 512.0f);
    }

    public void drawTexturedModalRect(final float x, final float y, final float width, final float height, final float u, final float v, final float uWidth, final float vHeight, final boolean invertX, final boolean invertY, final float texSizeX, final float texSizeY) {
        GL11.glShadeModel(7424);
        GL11.glEnable(3042);
        GL11.glEnable(3008);
        GL11.glEnable(3553);
        final float texModX = 1.0f / texSizeX;
        final float texModY = 1.0f / texSizeY;
        final Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        final float height2 = invertY ? 0.0f : vHeight;
        final float height3 = invertY ? vHeight : 0.0f;
        final float width2 = invertX ? uWidth : 0.0f;
        final float width3 = invertX ? 0.0f : uWidth;
        tessellator.addVertexWithUV((double)x, (double)(y + height), (double)this.zLevel, (double)((u + width2) * texModX), (double)((v + height2) * texModY));
        tessellator.addVertexWithUV((double)(x + width), (double)(y + height), (double)this.zLevel, (double)((u + width3) * texModX), (double)((v + height2) * texModY));
        tessellator.addVertexWithUV((double)(x + width), (double)y, (double)this.zLevel, (double)((u + width3) * texModX), (double)((v + height3) * texModY));
        tessellator.addVertexWithUV((double)x, (double)y, (double)this.zLevel, (double)((u + width2) * texModX), (double)((v + height3) * texModY));
        tessellator.draw();
    }

    public void setBlackBackground() {
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3008);
        GL11.glDisable(3553);
        final Tessellator var3 = Tessellator.instance;
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        var3.startDrawingQuads();
        var3.addVertex(0.0, (double)this.height, -90.0);
        var3.addVertex((double)this.width, (double)this.height, -90.0);
        var3.addVertex((double)this.width, 0.0, -90.0);
        var3.addVertex(0.0, 0.0, -90.0);
        var3.draw();
        GL11.glDisable(2929);
        GL11.glDisable(3008);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public Matrix4f setIsometric(final float partialTicks) {
        final Matrix4f mat0 = new Matrix4f();
        Matrix4f.translate(new Vector3f(this.width / 2.0f, (float)(this.height / 2), 0.0f), mat0, mat0);
        Matrix4f.rotate((float)Math.toRadians(55.0), new Vector3f(1.0f, 0.0f, 0.0f), mat0, mat0);
        Matrix4f.rotate((float)Math.toRadians(-45.0), new Vector3f(0.0f, 0.0f, 1.0f), mat0, mat0);
        final float zoomLocal = this.getZoomAdvanced();
        this.zoom = zoomLocal;
        Matrix4f.scale(new Vector3f(1.1f + zoomLocal, 1.1f + zoomLocal, 1.1f + zoomLocal), mat0, mat0);
        final Vector2f cBodyPos = this.getTranslationAdvanced(partialTicks);
        this.position = this.getTranslationAdvanced(partialTicks);
        Matrix4f.translate(new Vector3f(-cBodyPos.x, -cBodyPos.y, 0.0f), mat0, mat0);
        final FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        fb.rewind();
        mat0.store(fb);
        fb.flip();
        GL11.glMultMatrix(fb);
        return mat0;
    }

    public void drawGrid(float gridSize, final float gridScale) {
        GL11.glColor4f(0.0f, 0.2f, 0.5f, 0.55f);
        GL11.glBegin(1);
        gridSize += gridScale / 2.0f;
        for (float v = -gridSize; v <= gridSize; v += gridScale) {
            GL11.glVertex3f(v, -gridSize, -0.0f);
            GL11.glVertex3f(v, gridSize, -0.0f);
            GL11.glVertex3f(-gridSize, v, -0.0f);
            GL11.glVertex3f(gridSize, v, -0.0f);
        }
        GL11.glEnd();
    }

    public void drawCircles() {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glLineWidth(3.0f);
        int count = 0;
        final float theta = 0.06981317f;
        final float cos = (float)Math.cos(0.06981316953897476);
        final float sin = (float)Math.sin(0.06981316953897476);
        for (final Planet planet : GalaxyRegistry.getRegisteredPlanets().values()) {
            if (planet.getParentSolarSystem() != null) {
                final Vector3f systemOffset = this.getCelestialBodyPosition((CelestialBody)planet.getParentSolarSystem().getMainStar());
                float x = this.getScale((CelestialBody)planet);
                float y = 0.0f;
                float alpha = 1.0f;
                if ((this.selectedBody instanceof IChildBody && ((IChildBody)this.selectedBody).getParentPlanet() != planet) || (this.selectedBody instanceof Planet && this.selectedBody != planet && this.selectionCount >= 2)) {
                    if (this.lastSelectedBody == null && !(this.selectedBody instanceof IChildBody) && !(this.selectedBody instanceof Satellite)) {
                        alpha = 1.0f - Math.min(this.ticksSinceSelection / 25.0f, 1.0f);
                    }
                    else {
                        alpha = 0.0f;
                    }
                }
                if (alpha == 0.0f) {
                    continue;
                }
                switch (count % 2) {
                    case 0: {
                        GL11.glColor4f(0.0f, 0.42857146f, 0.71428573f, alpha / 1.4f);
                        break;
                    }
                    case 1: {
                        GL11.glColor4f(0.2857143f, 0.64285713f, 0.71428573f, alpha / 1.4f);
                        break;
                    }
                }
                final CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre preEvent = new CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre((CelestialBody)planet, systemOffset);
                MinecraftForge.EVENT_BUS.post((Event)preEvent);
                if (!preEvent.isCanceled()) {
                    GL11.glTranslatef(systemOffset.x, systemOffset.y, systemOffset.z);
                    GL11.glBegin(2);
                    for (int i = 0; i < 90; ++i) {
                        GL11.glVertex2f(x, y);
                        final float temp = x;
                        x = cos * x - sin * y;
                        y = sin * temp + cos * y;
                    }
                    GL11.glEnd();
                    GL11.glTranslatef(-systemOffset.x, -systemOffset.y, -systemOffset.z);
                    ++count;
                }
                final CelestialBodyRenderEvent.CelestialRingRenderEvent.Post postEvent = new CelestialBodyRenderEvent.CelestialRingRenderEvent.Post((CelestialBody)planet);
                MinecraftForge.EVENT_BUS.post((Event)postEvent);
            }
        }
        count = 0;
        if (this.selectedBody != null) {
            Vector3f planetPos = this.getCelestialBodyPosition(this.selectedBody);
            if (this.selectedBody instanceof IChildBody) {
                planetPos = this.getCelestialBodyPosition((CelestialBody)((IChildBody)this.selectedBody).getParentPlanet());
            }
            else if (this.selectedBody instanceof Satellite) {
                planetPos = this.getCelestialBodyPosition((CelestialBody)((Satellite)this.selectedBody).getParentPlanet());
            }
            GL11.glTranslatef(planetPos.x, planetPos.y, 0.0f);
            for (final Moon moon : GalaxyRegistry.getRegisteredMoons().values()) {
                if ((moon.getParentPlanet() == this.selectedBody && this.selectionCount != 1) || moon == this.selectedBody || this.getSiblings(this.selectedBody).contains(moon)) {
                    float x = this.getScale((CelestialBody)moon);
                    float y = 0.0f;
                    float alpha = 1.0f;
                    if (this.selectionCount >= 2) {
                        alpha = ((this.selectedBody instanceof IChildBody) ? 1.0f : Math.min(Math.max((this.ticksSinceSelection - 30) / 15.0f, 0.0f), 1.0f));
                        if (this.lastSelectedBody instanceof Moon && GalaxyRegistry.getMoonsForPlanet(((Moon)this.lastSelectedBody).getParentPlanet()).contains(moon)) {
                            alpha = 1.0f;
                        }
                    }
                    if (alpha == 0.0f) {
                        continue;
                    }
                    switch (count % 2) {
                        case 0: {
                            GL11.glColor4f(0.0f, 0.6f, 1.0f, alpha);
                            break;
                        }
                        case 1: {
                            GL11.glColor4f(0.4f, 0.9f, 1.0f, alpha);
                            break;
                        }
                    }
                    final CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre preEvent = new CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre((CelestialBody)moon, new Vector3f(0.0f, 0.0f, 0.0f));
                    MinecraftForge.EVENT_BUS.post((Event)preEvent);
                    if (!preEvent.isCanceled()) {
                        GL11.glBegin(2);
                        for (int i = 0; i < 90; ++i) {
                            GL11.glVertex2f(x, y);
                            final float temp = x;
                            x = cos * x - sin * y;
                            y = sin * temp + cos * y;
                        }
                        GL11.glEnd();
                        ++count;
                    }
                    final CelestialBodyRenderEvent.CelestialRingRenderEvent.Post postEvent = new CelestialBodyRenderEvent.CelestialRingRenderEvent.Post((CelestialBody)moon);
                    MinecraftForge.EVENT_BUS.post((Event)postEvent);
                }
            }
            for (final Satellite satellite : GalaxyRegistry.getRegisteredSatellites().values()) {
                if (this.possibleBodies != null && this.possibleBodies.contains(satellite) && ((satellite.getParentPlanet() == this.selectedBody && this.selectionCount != 1 && this.ticksSinceSelection > 24) || satellite == this.selectedBody || this.lastSelectedBody instanceof IChildBody)) {
                    float x = this.getScale((CelestialBody)satellite);
                    float y = 0.0f;
                    float alpha = 1.0f;
                    if (this.selectionCount >= 2) {
                        alpha = ((this.selectedBody instanceof IChildBody) ? 1.0f : Math.min(Math.max((this.ticksSinceSelection - 30) / 15.0f, 0.0f), 1.0f));
                        if (this.lastSelectedBody instanceof Satellite && GalaxyRegistry.getSatellitesForCelestialBody((CelestialBody)((Satellite)this.lastSelectedBody).getParentPlanet()).contains(satellite)) {
                            alpha = 1.0f;
                        }
                    }
                    if (alpha == 0.0f) {
                        continue;
                    }
                    switch (count % 2) {
                        case 0: {
                            GL11.glColor4f(0.0f, 0.6f, 1.0f, alpha);
                            break;
                        }
                        case 1: {
                            GL11.glColor4f(0.4f, 0.9f, 1.0f, alpha);
                            break;
                        }
                    }
                    final CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre preEvent = new CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre((CelestialBody)satellite, new Vector3f(0.0f, 0.0f, 0.0f));
                    MinecraftForge.EVENT_BUS.post((Event)preEvent);
                    if (!preEvent.isCanceled()) {
                        GL11.glBegin(2);
                        for (int i = 0; i < 90; ++i) {
                            GL11.glVertex2f(x, y);
                            final float temp = x;
                            x = cos * x - sin * y;
                            y = sin * temp + cos * y;
                        }
                        GL11.glEnd();
                        ++count;
                    }
                    final CelestialBodyRenderEvent.CelestialRingRenderEvent.Post postEvent = new CelestialBodyRenderEvent.CelestialRingRenderEvent.Post((CelestialBody)satellite);
                    MinecraftForge.EVENT_BUS.post((Event)postEvent);
                }
            }
        }
        GL11.glLineWidth(1.0f);
    }

    protected void actionPerformed(final GuiButton button) {
        final int id = button.id;
    }

    static {
        GuiCelestialSelection.guiMain0 = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialselection.png");
        GuiCelestialSelection.guiMain1 = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialselection1.png");
        GuiCelestialSelection.BORDER_WIDTH = 0;
        GuiCelestialSelection.BORDER_EDGE_WIDTH = 0;
    }

    protected enum EnumSelectionState
    {
        PREVIEW,
        PROFILE;
    }

    public static class StationDataGUI
    {
        private String stationName;
        private Integer stationDimensionID;

        public StationDataGUI(final String stationName, final Integer stationDimensionID) {
            this.stationName = stationName;
            this.stationDimensionID = stationDimensionID;
        }

        public String getStationName() {
            return this.stationName;
        }

        public void setStationName(final String stationName) {
            this.stationName = stationName;
        }

        public Integer getStationDimensionID() {
            return this.stationDimensionID;
        }

        public void setStationDimensionID(final Integer stationDimensionID) {
            this.stationDimensionID = stationDimensionID;
        }
    }
}
