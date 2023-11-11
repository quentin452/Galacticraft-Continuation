package micdoodle8.mods.galacticraft.core.entities.player;

import net.minecraftforge.common.*;
import java.lang.ref.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import com.google.common.collect.*;
import net.minecraft.nbt.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.command.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.entity.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.api.inventory.*;

public class GCPlayerStats implements IExtendedEntityProperties
{
    public static final String GC_PLAYER_PROP = "GCPlayerStats";
    public WeakReference<EntityPlayerMP> player;
    public InventoryExtended extendedInventory;
    public int airRemaining;
    public int airRemaining2;
    public int thermalLevel;
    public boolean thermalLevelNormalising;
    public int damageCounter;
    public int spaceshipTier;
    public ItemStack[] rocketStacks;
    public int rocketType;
    public int fuelLevel;
    public Item rocketItem;
    public ItemStack launchpadStack;
    public int astroMinerCount;
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
    public int launchAttempts;
    public int spaceRaceInviteTeamID;
    public boolean usingPlanetSelectionGui;
    public String savedPlanetList;
    public int openPlanetSelectionGuiCooldown;
    public boolean hasOpenedPlanetSelectionGui;
    public int chestSpawnCooldown;
    public Vector3 chestSpawnVector;
    public int teleportCooldown;
    public int chatCooldown;
    public double distanceSinceLastStep;
    public int lastStep;
    public double coordsTeleportedFromX;
    public double coordsTeleportedFromZ;
    public HashMap<Integer, Integer> spaceStationDimensionData;
    public boolean oxygenSetupValid;
    public boolean lastOxygenSetupValid;
    public boolean touchedGround;
    public boolean lastOnGround;
    public boolean inLander;
    public boolean justLanded;
    public ArrayList<ISchematicPage> unlockedSchematics;
    public ArrayList<ISchematicPage> lastUnlockedSchematics;
    public int cryogenicChamberCooldown;
    public boolean receivedSoundWarning;
    public boolean receivedBedWarning;
    public boolean openedSpaceRaceManager;
    public boolean sentFlags;
    public boolean newInOrbit;
    public boolean newAdventureSpawn;
    public int buildFlags;
    public int incrementalDamage;
    public String startDimension;

    public GCPlayerStats(final EntityPlayerMP player) {
        this.extendedInventory = new InventoryExtended();
        this.spaceshipTier = 1;
        this.rocketStacks = new ItemStack[2];
        this.astroMinerCount = 0;
        this.launchAttempts = 0;
        this.savedPlanetList = "";
        this.hasOpenedPlanetSelectionGui = false;
        this.spaceStationDimensionData = Maps.newHashMap();
        this.unlockedSchematics = new ArrayList<ISchematicPage>();
        this.lastUnlockedSchematics = new ArrayList<ISchematicPage>();
        this.openedSpaceRaceManager = false;
        this.sentFlags = false;
        this.newInOrbit = true;
        this.buildFlags = 0;
        this.incrementalDamage = 0;
        this.startDimension = "";
        this.player = new WeakReference<EntityPlayerMP>(player);
    }

    public void saveNBTData(final NBTTagCompound nbt) {
        nbt.setTag("ExtendedInventoryGC", (NBTBase)this.extendedInventory.writeToNBT(new NBTTagList()));
        nbt.setInteger("playerAirRemaining", this.airRemaining);
        nbt.setInteger("damageCounter", this.damageCounter);
        nbt.setBoolean("OxygenSetupValid", this.oxygenSetupValid);
        nbt.setBoolean("usingParachute2", this.usingParachute);
        nbt.setBoolean("usingPlanetSelectionGui", this.usingPlanetSelectionGui);
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
                tagList.appendTag((NBTBase)nbttagcompound);
            }
        }
        nbt.setTag("Schematics", (NBTBase)tagList);
        nbt.setInteger("rocketStacksLength", this.rocketStacks.length);
        nbt.setInteger("SpaceshipTier", this.spaceshipTier);
        nbt.setInteger("FuelLevel", this.fuelLevel);
        if (this.rocketItem != null) {
            final ItemStack returnRocket = new ItemStack(this.rocketItem, 1, this.rocketType);
            nbt.setTag("ReturnRocket", (NBTBase)returnRocket.writeToNBT(new NBTTagCompound()));
        }
        final NBTTagList var2 = new NBTTagList();
        for (int var3 = 0; var3 < this.rocketStacks.length; ++var3) {
            if (this.rocketStacks[var3] != null) {
                final NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.rocketStacks[var3].writeToNBT(var4);
                var2.appendTag((NBTBase)var4);
            }
        }
        nbt.setTag("RocketItems", (NBTBase)var2);
        final NBTTagCompound var5 = new NBTTagCompound();
        if (this.launchpadStack != null) {
            nbt.setTag("LaunchpadStack", (NBTBase)this.launchpadStack.writeToNBT(var5));
        }
        else {
            nbt.setTag("LaunchpadStack", (NBTBase)var5);
        }
        nbt.setInteger("CryogenicChamberCooldown", this.cryogenicChamberCooldown);
        nbt.setBoolean("ReceivedSoundWarning", this.receivedSoundWarning);
        nbt.setBoolean("ReceivedBedWarning", this.receivedBedWarning);
        nbt.setInteger("BuildFlags", this.buildFlags);
        nbt.setBoolean("ShownSpaceRace", this.openedSpaceRaceManager);
        nbt.setInteger("AstroMinerCount", this.astroMinerCount);
    }

    public void loadNBTData(final NBTTagCompound nbt) {
        this.airRemaining = nbt.getInteger("playerAirRemaining");
        this.damageCounter = nbt.getInteger("damageCounter");
        final boolean getBoolean = nbt.getBoolean("OxygenSetupValid");
        this.lastOxygenSetupValid = getBoolean;
        this.oxygenSetupValid = getBoolean;
        this.thermalLevel = nbt.getInteger("thermalLevel");
        final NBTTagList nbttaglist = nbt.getTagList("Inventory", 10);
        this.extendedInventory.readFromNBTOld(nbttaglist);
        if (nbt.hasKey("ExtendedInventoryGC")) {
            this.extendedInventory.readFromNBT(nbt.getTagList("ExtendedInventoryGC", 10));
        }
        final EntityPlayerMP p = this.player.get();
        if (p != null) {
            final ItemStack[] saveinv = CommandGCInv.getSaveData(p.getGameProfile().getName().toLowerCase());
            if (saveinv != null) {
                CommandGCInv.doLoad(p);
            }
        }
        if (nbt.hasKey("SpaceshipTier")) {
            this.spaceshipTier = nbt.getInteger("SpaceshipTier");
        }
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
        this.teleportCooldown = nbt.getInteger("teleportCooldown");
        this.coordsTeleportedFromX = nbt.getDouble("coordsTeleportedFromX");
        this.coordsTeleportedFromZ = nbt.getDouble("coordsTeleportedFromZ");
        this.startDimension = (nbt.hasKey("startDimension") ? nbt.getString("startDimension") : "");
        if (nbt.hasKey("spaceStationDimensionID")) {
            this.spaceStationDimensionData = WorldUtil.stringToSpaceStationData("0$" + nbt.getInteger("spaceStationDimensionID"));
        }
        else {
            this.spaceStationDimensionData = WorldUtil.stringToSpaceStationData(nbt.getString("spaceStationDimensionInfo"));
        }
        if (nbt.getBoolean("usingPlanetSelectionGui")) {
            this.openPlanetSelectionGuiCooldown = 20;
        }
        if (nbt.hasKey("RocketItems") && nbt.hasKey("rocketStacksLength")) {
            final NBTTagList var23 = nbt.getTagList("RocketItems", 10);
            final int length = nbt.getInteger("rocketStacksLength");
            this.rocketStacks = new ItemStack[length];
            for (int var24 = 0; var24 < var23.tagCount(); ++var24) {
                final NBTTagCompound var25 = var23.getCompoundTagAt(var24);
                final int var26 = var25.getByte("Slot") & 0xFF;
                if (var26 < this.rocketStacks.length) {
                    this.rocketStacks[var26] = ItemStack.loadItemStackFromNBT(var25);
                }
            }
        }
        this.unlockedSchematics = new ArrayList<ISchematicPage>();
        if (p != null) {
            for (int i = 0; i < nbt.getTagList("Schematics", 10).tagCount(); ++i) {
                final NBTTagCompound nbttagcompound = nbt.getTagList("Schematics", 10).getCompoundTagAt(i);
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
        }
        else {
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
            GCLog.info("Loading GC player data for " + this.player.get().getGameProfile().getName() + " : " + this.buildFlags);
        }
    }

    public void init(final Entity entity, final World world) {
    }

    public static void register(final EntityPlayerMP player) {
        player.registerExtendedProperties("GCPlayerStats", (IExtendedEntityProperties)new GCPlayerStats(player));
    }

    public static GCPlayerStats get(final EntityPlayerMP player) {
        return (GCPlayerStats)player.getExtendedProperties("GCPlayerStats");
    }

    public static void tryBedWarning(final EntityPlayerMP player) {
        final GCPlayerStats GCPlayer = get(player);
        if (!GCPlayer.receivedBedWarning) {
            player.addChatMessage((IChatComponent)new ChatComponentText(GCCoreUtil.translate("gui.bedFail.message")));
            GCPlayer.receivedBedWarning = true;
        }
    }

    public void copyFrom(final GCPlayerStats oldData, final boolean keepInv) {
        if (keepInv) {
            this.extendedInventory.copyInventory((IInventoryGC)oldData.extendedInventory);
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

    public void startAdventure(final String worldName) {
        this.startDimension = worldName;
    }
}
