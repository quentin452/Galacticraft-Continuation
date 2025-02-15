package micdoodle8.mods.galacticraft.core.entities.player;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import com.google.common.collect.Maps;

import micdoodle8.mods.galacticraft.api.recipe.ISchematicPage;
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import micdoodle8.mods.galacticraft.core.command.CommandGCInv;
import micdoodle8.mods.galacticraft.core.inventory.InventoryExtended;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;

public class GCPlayerStats implements IExtendedEntityProperties {

    public static final String GC_PLAYER_PROP = "GCPlayerStats";

    public WeakReference<EntityPlayerMP> player;

    public InventoryExtended extendedInventory = new InventoryExtended();

    public int airRemaining;
    public int airRemaining2;

    public int thermalLevel;
    public boolean thermalLevelNormalising;

    public int damageCounter;

    // temporary data while player is in planet selection GUI
    public int spaceshipTier = 1;
    public ItemStack[] rocketStacks = new ItemStack[2];
    public int rocketType;
    public int fuelLevel;
    public Item rocketItem;
    public ItemStack launchpadStack;
    public int astroMinerCount = 0;

    public boolean usingParachute;

    public ItemStack parachuteInSlot;
    public ItemStack lastParachuteInSlot;

    public ItemStack frequencyModuleInSlot;
    public ItemStack lastFrequencyModuleInSlot;

    public ItemStack maskInSlot;
    public ItemStack lastMaskInSlot;

    public ItemStack gearInSlot;
    public ItemStack lastGearInSlot;

    public ItemStack tankInSlot1;
    public ItemStack lastTankInSlot1;

    public ItemStack tankInSlot2;
    public ItemStack lastTankInSlot2;

    public ItemStack thermalHelmetInSlot;
    public ItemStack lastThermalHelmetInSlot;

    public ItemStack thermalChestplateInSlot;
    public ItemStack lastThermalChestplateInSlot;

    public ItemStack thermalLeggingsInSlot;
    public ItemStack lastThermalLeggingsInSlot;

    public ItemStack thermalBootsInSlot;
    public ItemStack lastThermalBootsInSlot;

    public int launchAttempts = 0;

    public int spaceRaceInviteTeamID;

    public boolean usingPlanetSelectionGui;
    public GuiCelestialSelection.MapMode currentMapMode;
    public String savedPlanetList = "";
    public int openPlanetSelectionGuiCooldown;
    public boolean hasOpenedPlanetSelectionGui = false;

    public int chestSpawnCooldown;
    public micdoodle8.mods.galacticraft.api.vector.Vector3 chestSpawnVector;

    public int teleportCooldown;

    public int chatCooldown;

    public double distanceSinceLastStep;
    public int lastStep;

    public double coordsTeleportedFromX;
    public double coordsTeleportedFromZ;

    public HashMap<Integer, Integer> spaceStationDimensionData = Maps.newHashMap();

    public boolean oxygenSetupValid;
    public boolean lastOxygenSetupValid;

    public boolean touchedGround;
    public boolean lastOnGround;
    public boolean inLander;
    public boolean justLanded;

    public ArrayList<ISchematicPage> unlockedSchematics = new ArrayList<>();
    public ArrayList<ISchematicPage> lastUnlockedSchematics = new ArrayList<>();

    public int cryogenicChamberCooldown;

    public boolean receivedSoundWarning;
    public boolean receivedBedWarning;
    public boolean openedSpaceRaceManager = false;
    public boolean sentFlags = false;
    public boolean newInOrbit = true;
    public boolean newAdventureSpawn;
    public int buildFlags = 0;

    public int incrementalDamage = 0;

    public String startDimension = "";

    public GCPlayerStats(EntityPlayerMP player) {
        this.player = new WeakReference<>(player);
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        nbt.setTag("ExtendedInventoryGC", this.extendedInventory.writeToNBT(new NBTTagList()));
        nbt.setInteger("playerAirRemaining", this.airRemaining);
        nbt.setInteger("damageCounter", this.damageCounter);
        nbt.setBoolean("OxygenSetupValid", this.oxygenSetupValid);
        nbt.setBoolean("usingParachute2", this.usingParachute);
        nbt.setBoolean("usingPlanetSelectionGui", this.usingPlanetSelectionGui);
        nbt.setInteger("currentMapMode", this.currentMapMode != null ? this.currentMapMode.ordinal() : -1);
        nbt.setInteger("teleportCooldown", this.teleportCooldown);
        nbt.setDouble("coordsTeleportedFromX", this.coordsTeleportedFromX);
        nbt.setDouble("coordsTeleportedFromZ", this.coordsTeleportedFromZ);
        nbt.setString("startDimension", this.startDimension);
        nbt.setString("spaceStationDimensionInfo", WorldUtil.spaceStationDataToString(this.spaceStationDimensionData));
        nbt.setInteger("thermalLevel", this.thermalLevel);

        Collections.sort(this.unlockedSchematics);

        final NBTTagList tagList = new NBTTagList();

        for (final ISchematicPage page : this.unlockedSchematics) {
            if (page != null) {
                final NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setInteger("UnlockedPage", page.getPageID());
                tagList.appendTag(nbttagcompound);
            }
        }

        nbt.setTag("Schematics", tagList);

        nbt.setInteger("rocketStacksLength", this.rocketStacks.length);
        nbt.setInteger("SpaceshipTier", this.spaceshipTier);
        nbt.setInteger("FuelLevel", this.fuelLevel);
        if (this.rocketItem != null) {
            final ItemStack returnRocket = new ItemStack(this.rocketItem, 1, this.rocketType);
            nbt.setTag("ReturnRocket", returnRocket.writeToNBT(new NBTTagCompound()));
        }

        final NBTTagList var2 = new NBTTagList();

        for (int var3 = 0; var3 < this.rocketStacks.length; ++var3) {
            if (this.rocketStacks[var3] != null) {
                final NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte) var3);
                this.rocketStacks[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }

        nbt.setTag("RocketItems", var2);
        final NBTTagCompound var4 = new NBTTagCompound();
        if (this.launchpadStack != null) {
            nbt.setTag("LaunchpadStack", this.launchpadStack.writeToNBT(var4));
        } else {
            nbt.setTag("LaunchpadStack", var4);
        }

        nbt.setInteger("CryogenicChamberCooldown", this.cryogenicChamberCooldown);
        nbt.setBoolean("ReceivedSoundWarning", this.receivedSoundWarning);
        nbt.setBoolean("ReceivedBedWarning", this.receivedBedWarning);
        nbt.setInteger("BuildFlags", this.buildFlags);
        nbt.setBoolean("ShownSpaceRace", this.openedSpaceRaceManager);
        nbt.setInteger("AstroMinerCount", this.astroMinerCount);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        this.airRemaining = nbt.getInteger("playerAirRemaining");
        this.damageCounter = nbt.getInteger("damageCounter");
        this.oxygenSetupValid = this.lastOxygenSetupValid = nbt.getBoolean("OxygenSetupValid");
        this.thermalLevel = nbt.getInteger("thermalLevel");

        // Backwards compatibility
        final NBTTagList nbttaglist = nbt.getTagList("Inventory", 10);
        this.extendedInventory.readFromNBTOld(nbttaglist);

        if (nbt.hasKey("ExtendedInventoryGC")) {
            this.extendedInventory.readFromNBT(nbt.getTagList("ExtendedInventoryGC", 10));
        }

        // Added for GCInv command - if tried to load an offline player's
        // inventory, load it now
        // (if there was no offline load, then the dontload flag in doLoad()
        // will make sure nothing happens)
        final EntityPlayerMP p = this.player.get();
        if (p != null) {
            final ItemStack[] saveinv = CommandGCInv.getSaveData(
                p.getGameProfile()
                    .getName()
                    .toLowerCase());
            if (saveinv != null) {
                CommandGCInv.doLoad(p);
            }
        }

        if (nbt.hasKey("SpaceshipTier")) {
            this.spaceshipTier = nbt.getInteger("SpaceshipTier");
        }

        // New keys in version 3.0.5.220
        if (nbt.hasKey("FuelLevel")) {
            this.fuelLevel = nbt.getInteger("FuelLevel");
        }
        if (nbt.hasKey("ReturnRocket")) {
            final ItemStack returnRocket = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("ReturnRocket"));
            if (returnRocket != null) {
                this.rocketItem = returnRocket.getItem();
                this.rocketType = returnRocket.getItemDamage();
            }
        }

        this.usingParachute = nbt.getBoolean("usingParachute2");
        this.usingPlanetSelectionGui = nbt.getBoolean("usingPlanetSelectionGui");
        if (nbt.getInteger("currentMapMode") != -1) {
            this.currentMapMode = GuiCelestialSelection.MapMode.fromInteger(nbt.getInteger("currentMapMode"));
        } else {
            this.currentMapMode = null;
        }

        this.teleportCooldown = nbt.getInteger("teleportCooldown");
        this.coordsTeleportedFromX = nbt.getDouble("coordsTeleportedFromX");
        this.coordsTeleportedFromZ = nbt.getDouble("coordsTeleportedFromZ");
        this.startDimension = nbt.hasKey("startDimension") ? nbt.getString("startDimension") : "";
        if (nbt.hasKey("spaceStationDimensionID")) {
            // If loading from an old save file, the home space station is always the
            // overworld, so use 0 as home planet
            this.spaceStationDimensionData = WorldUtil
                .stringToSpaceStationData("0$" + nbt.getInteger("spaceStationDimensionID"));
        } else {
            this.spaceStationDimensionData = WorldUtil
                .stringToSpaceStationData(nbt.getString("spaceStationDimensionInfo"));
        }

        if (nbt.getBoolean("usingPlanetSelectionGui")) {
            this.openPlanetSelectionGuiCooldown = 20;
        }

        if (nbt.hasKey("RocketItems") && nbt.hasKey("rocketStacksLength")) {
            final NBTTagList var23 = nbt.getTagList("RocketItems", 10);
            final int length = nbt.getInteger("rocketStacksLength");

            this.rocketStacks = new ItemStack[length];

            for (int var3 = 0; var3 < var23.tagCount(); ++var3) {
                final NBTTagCompound var4 = var23.getCompoundTagAt(var3);
                final int var5 = var4.getByte("Slot") & 255;

                if (var5 < this.rocketStacks.length) {
                    this.rocketStacks[var5] = ItemStack.loadItemStackFromNBT(var4);
                }
            }
        }

        this.unlockedSchematics = new ArrayList<>();

        if (p != null) {
            for (int i = 0; i < nbt.getTagList("Schematics", 10)
                .tagCount(); ++i) {
                final NBTTagCompound nbttagcompound = nbt.getTagList("Schematics", 10)
                    .getCompoundTagAt(i);

                final int j = nbttagcompound.getInteger("UnlockedPage");

                SchematicRegistry.addUnlockedPage(p, SchematicRegistry.getMatchingRecipeForID(j));
            }
        }

        Collections.sort(this.unlockedSchematics);

        this.cryogenicChamberCooldown = nbt.getInteger("CryogenicChamberCooldown");

        if (nbt.hasKey("ReceivedSoundWarning")) {
            this.receivedSoundWarning = nbt.getBoolean("ReceivedSoundWarning");
        }
        if (nbt.hasKey("ReceivedBedWarning")) {
            this.receivedBedWarning = nbt.getBoolean("ReceivedBedWarning");
        }

        if (nbt.hasKey("LaunchpadStack")) {
            this.launchpadStack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("LaunchpadStack"));
            if (this.launchpadStack != null && this.launchpadStack.stackSize == 0) {
                this.launchpadStack = null;
            }
        } else {
            // for backwards compatibility with saves which don't have this tag - players
            // can't lose launchpads
            this.launchpadStack = new ItemStack(GCBlocks.landingPad, 9, 0);
        }

        if (nbt.hasKey("BuildFlags")) {
            this.buildFlags = nbt.getInteger("BuildFlags");
        }

        if (nbt.hasKey("ShownSpaceRace")) {
            this.openedSpaceRaceManager = nbt.getBoolean("ShownSpaceRace");
        }

        if (nbt.hasKey("AstroMinerCount")) {
            this.astroMinerCount = nbt.getInteger("AstroMinerCount");
        }

        this.sentFlags = false;
        if (ConfigManagerCore.enableDebug) {
            GCLog.info(
                "Loading GC player data for " + this.player.get()
                    .getGameProfile()
                    .getName() + " : " + this.buildFlags);
        }
    }

    @Override
    public void init(Entity entity, World world) {}

    public static void register(EntityPlayerMP player) {
        player.registerExtendedProperties(GCPlayerStats.GC_PLAYER_PROP, new GCPlayerStats(player));
    }

    public static GCPlayerStats get(EntityPlayerMP player) {
        return (GCPlayerStats) player.getExtendedProperties(GCPlayerStats.GC_PLAYER_PROP);
    }

    public static void tryBedWarning(EntityPlayerMP player) {
        final GCPlayerStats GCPlayer = GCPlayerStats.get(player);
        if (!GCPlayer.receivedBedWarning) {
            player.addChatMessage(new ChatComponentText(GCCoreUtil.translate("gui.bedFail.message")));
            GCPlayer.receivedBedWarning = true;
        }
    }

    public void copyFrom(GCPlayerStats oldData, boolean keepInv) {
        if (keepInv) {
            this.extendedInventory.copyInventory(oldData.extendedInventory);
        }

        this.spaceStationDimensionData = oldData.spaceStationDimensionData;
        this.unlockedSchematics = oldData.unlockedSchematics;
        this.receivedSoundWarning = oldData.receivedSoundWarning;
        this.receivedBedWarning = oldData.receivedBedWarning;
        this.openedSpaceRaceManager = oldData.openedSpaceRaceManager;
        this.spaceRaceInviteTeamID = oldData.spaceRaceInviteTeamID;
        this.buildFlags = oldData.buildFlags;
        this.astroMinerCount = oldData.astroMinerCount;
        this.sentFlags = false;
    }

    public void startAdventure(String worldName) {
        this.startDimension = worldName;
    }
}
