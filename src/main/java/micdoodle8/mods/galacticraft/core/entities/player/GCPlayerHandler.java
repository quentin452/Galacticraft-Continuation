package micdoodle8.mods.galacticraft.core.entities.player;

import java.util.concurrent.*;
import java.lang.reflect.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.gameevent.*;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import net.minecraftforge.event.entity.*;
import net.minecraft.client.entity.*;
import net.minecraft.client.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import java.lang.ref.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.potion.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;
import micdoodle8.mods.galacticraft.api.event.oxygen.*;
import net.minecraft.entity.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.wrappers.*;
import micdoodle8.mods.galacticraft.core.tick.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import java.util.*;
import cpw.mods.fml.common.network.*;
import net.minecraft.server.*;
import net.minecraft.world.*;
import net.minecraft.network.play.server.*;
import net.minecraft.network.*;
import micdoodle8.mods.galacticraft.api.*;
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraft.entity.item.*;
import net.minecraft.util.*;
import net.minecraft.world.chunk.*;
import micdoodle8.mods.galacticraft.api.world.*;
import micdoodle8.mods.galacticraft.core.dimension.*;
import micdoodle8.mods.galacticraft.core.util.*;

public class GCPlayerHandler
{
    private static final int OXYGENHEIGHTLIMIT = 450;
    private boolean isClient;
    private ConcurrentHashMap<UUID, GCPlayerStats> playerStatsMap;
    private Field ftc;
    private HashMap<Item, Item> torchItems;

    public GCPlayerHandler() {
        this.isClient = FMLCommonHandler.instance().getEffectiveSide().isClient();
        this.playerStatsMap = new ConcurrentHashMap<UUID, GCPlayerStats>();
        this.torchItems = new HashMap<Item, Item>();
    }

    public ConcurrentHashMap<UUID, GCPlayerStats> getServerStatList() {
        return this.playerStatsMap;
    }

    @SubscribeEvent
    public void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            this.onPlayerLogin((EntityPlayerMP)event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(final PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            this.onPlayerLogout((EntityPlayerMP)event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(final PlayerEvent.PlayerRespawnEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            this.onPlayerRespawn((EntityPlayerMP)event.player);
        }
    }

    @SubscribeEvent
    public void onEntityConstructing(final EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayerMP && GCPlayerStats.get((EntityPlayerMP)event.entity) == null) {
            GCPlayerStats.register((EntityPlayerMP)event.entity);
        }
        if (this.isClient) {
            this.onEntityConstructingClient(event);
        }
    }

    @SideOnly(Side.CLIENT)
    public void onEntityConstructingClient(final EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityClientPlayerMP) {
            if (GCPlayerStatsClient.get((EntityPlayerSP)event.entity) == null) {
                GCPlayerStatsClient.register((EntityPlayerSP)event.entity);
            }
            Minecraft.getMinecraft().gameSettings.sendSettingsToServer();
        }
    }

    private void onPlayerLogin(final EntityPlayerMP player) {
        final GCPlayerStats oldData = this.playerStatsMap.remove(player.getPersistentID());
        if (oldData != null) {
            oldData.saveNBTData(player.getEntityData());
        }
        final GCPlayerStats stats = GCPlayerStats.get(player);
        GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_GET_CELESTIAL_BODY_LIST, new Object[0]), player);
        final int repeatCount = stats.buildFlags >> 9;
        if (repeatCount < 3) {
            final GCPlayerStats gcPlayerStats = stats;
            gcPlayerStats.buildFlags &= 0x600;
        }
        GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_STATS, new Object[] { stats.buildFlags }), player);
    }

    private void onPlayerLogout(final EntityPlayerMP player) {
    }

    private void onPlayerRespawn(final EntityPlayerMP player) {
        final GCPlayerStats oldData = this.playerStatsMap.remove(player.getPersistentID());
        final GCPlayerStats stats = GCPlayerStats.get(player);
        if (oldData != null) {
            stats.copyFrom(oldData, false);
        }
        stats.player = new WeakReference<EntityPlayerMP>(player);
    }

    public static void checkGear(final EntityPlayerMP player, final GCPlayerStats GCPlayer, final boolean forceSend) {
        GCPlayer.maskInSlot = GCPlayer.extendedInventory.getStackInSlot(0);
        GCPlayer.gearInSlot = GCPlayer.extendedInventory.getStackInSlot(1);
        GCPlayer.tankInSlot1 = GCPlayer.extendedInventory.getStackInSlot(2);
        GCPlayer.tankInSlot2 = GCPlayer.extendedInventory.getStackInSlot(3);
        GCPlayer.parachuteInSlot = GCPlayer.extendedInventory.getStackInSlot(4);
        GCPlayer.frequencyModuleInSlot = GCPlayer.extendedInventory.getStackInSlot(5);
        GCPlayer.thermalHelmetInSlot = GCPlayer.extendedInventory.getStackInSlot(6);
        GCPlayer.thermalChestplateInSlot = GCPlayer.extendedInventory.getStackInSlot(7);
        GCPlayer.thermalLeggingsInSlot = GCPlayer.extendedInventory.getStackInSlot(8);
        GCPlayer.thermalBootsInSlot = GCPlayer.extendedInventory.getStackInSlot(9);
        if (GCPlayer.frequencyModuleInSlot != GCPlayer.lastFrequencyModuleInSlot || forceSend) {
            if (FMLCommonHandler.instance().getMinecraftServerInstance() != null) {
                if (GCPlayer.frequencyModuleInSlot == null) {
                    sendGearUpdatePacket(player, EnumModelPacket.REMOVE_FREQUENCY_MODULE);
                    TileEntityTelemetry.frequencyModulePlayer(GCPlayer.lastFrequencyModuleInSlot, null);
                }
                else if (GCPlayer.frequencyModuleInSlot.getItem() == GCItems.basicItem && GCPlayer.frequencyModuleInSlot.getItemDamage() == 19 && GCPlayer.lastFrequencyModuleInSlot == null) {
                    sendGearUpdatePacket(player, EnumModelPacket.ADD_FREQUENCY_MODULE);
                    TileEntityTelemetry.frequencyModulePlayer(GCPlayer.frequencyModuleInSlot, player);
                }
            }
            GCPlayer.lastFrequencyModuleInSlot = GCPlayer.frequencyModuleInSlot;
        }
        if (GCPlayer.maskInSlot != GCPlayer.lastMaskInSlot || forceSend) {
            if (GCPlayer.maskInSlot == null) {
                sendGearUpdatePacket(player, EnumModelPacket.REMOVEMASK);
            }
            else if (GCPlayer.maskInSlot.getItem() == GCItems.oxMask && (GCPlayer.lastMaskInSlot == null || forceSend)) {
                sendGearUpdatePacket(player, EnumModelPacket.ADDMASK);
            }
            GCPlayer.lastMaskInSlot = GCPlayer.maskInSlot;
        }
        if (GCPlayer.gearInSlot != GCPlayer.lastGearInSlot || forceSend) {
            if (GCPlayer.gearInSlot == null) {
                sendGearUpdatePacket(player, EnumModelPacket.REMOVEGEAR);
            }
            else if (GCPlayer.gearInSlot.getItem() == GCItems.oxygenGear && (GCPlayer.lastGearInSlot == null || forceSend)) {
                sendGearUpdatePacket(player, EnumModelPacket.ADDGEAR);
            }
            GCPlayer.lastGearInSlot = GCPlayer.gearInSlot;
        }
        if (GCPlayer.tankInSlot1 != GCPlayer.lastTankInSlot1 || forceSend) {
            if (GCPlayer.tankInSlot1 == null) {
                sendGearUpdatePacket(player, EnumModelPacket.REMOVE_LEFT_TANK);
                GCPlayer.airRemaining = 0;
                sendAirRemainingPacket(player, GCPlayer);
            }
            else if (GCPlayer.lastTankInSlot1 == null || forceSend) {
                if (GCPlayer.tankInSlot1.getItem() == GCItems.oxTankLight) {
                    sendGearUpdatePacket(player, EnumModelPacket.ADDLEFTGREENTANK);
                }
                else if (GCPlayer.tankInSlot1.getItem() == GCItems.oxTankMedium) {
                    sendGearUpdatePacket(player, EnumModelPacket.ADDLEFTORANGETANK);
                }
                else if (GCPlayer.tankInSlot1.getItem() == GCItems.oxTankHeavy) {
                    sendGearUpdatePacket(player, EnumModelPacket.ADDLEFTREDTANK);
                }
                if (GCPlayer.maskInSlot != null && GCPlayer.gearInSlot != null) {
                    GCPlayer.airRemaining = GCPlayer.tankInSlot1.getMaxDamage() - GCPlayer.tankInSlot1.getItemDamage();
                    sendAirRemainingPacket(player, GCPlayer);
                }
            }
            else if (GCPlayer.tankInSlot1.getItem() != GCPlayer.lastTankInSlot1.getItem()) {
                if (GCPlayer.tankInSlot1.getItem() == GCItems.oxTankLight) {
                    sendGearUpdatePacket(player, EnumModelPacket.ADDLEFTGREENTANK);
                }
                else if (GCPlayer.tankInSlot1.getItem() == GCItems.oxTankMedium) {
                    sendGearUpdatePacket(player, EnumModelPacket.ADDLEFTORANGETANK);
                }
                else if (GCPlayer.tankInSlot1.getItem() == GCItems.oxTankHeavy) {
                    sendGearUpdatePacket(player, EnumModelPacket.ADDLEFTREDTANK);
                }
                if (GCPlayer.maskInSlot != null && GCPlayer.gearInSlot != null) {
                    GCPlayer.airRemaining = GCPlayer.tankInSlot1.getMaxDamage() - GCPlayer.tankInSlot1.getItemDamage();
                    sendAirRemainingPacket(player, GCPlayer);
                }
            }
            GCPlayer.lastTankInSlot1 = GCPlayer.tankInSlot1;
        }
        if (GCPlayer.tankInSlot2 != GCPlayer.lastTankInSlot2 || forceSend) {
            if (GCPlayer.tankInSlot2 == null) {
                sendGearUpdatePacket(player, EnumModelPacket.REMOVE_RIGHT_TANK);
                GCPlayer.airRemaining2 = 0;
                sendAirRemainingPacket(player, GCPlayer);
            }
            else if (GCPlayer.lastTankInSlot2 == null || forceSend) {
                if (GCPlayer.tankInSlot2.getItem() == GCItems.oxTankLight) {
                    sendGearUpdatePacket(player, EnumModelPacket.ADDRIGHTGREENTANK);
                }
                else if (GCPlayer.tankInSlot2.getItem() == GCItems.oxTankMedium) {
                    sendGearUpdatePacket(player, EnumModelPacket.ADDRIGHTORANGETANK);
                }
                else if (GCPlayer.tankInSlot2.getItem() == GCItems.oxTankHeavy) {
                    sendGearUpdatePacket(player, EnumModelPacket.ADDRIGHTREDTANK);
                }
                if (GCPlayer.maskInSlot != null && GCPlayer.gearInSlot != null) {
                    GCPlayer.airRemaining2 = GCPlayer.tankInSlot2.getMaxDamage() - GCPlayer.tankInSlot2.getItemDamage();
                    sendAirRemainingPacket(player, GCPlayer);
                }
            }
            else if (GCPlayer.tankInSlot2.getItem() != GCPlayer.lastTankInSlot2.getItem()) {
                if (GCPlayer.tankInSlot2.getItem() == GCItems.oxTankLight) {
                    sendGearUpdatePacket(player, EnumModelPacket.ADDRIGHTGREENTANK);
                }
                else if (GCPlayer.tankInSlot2.getItem() == GCItems.oxTankMedium) {
                    sendGearUpdatePacket(player, EnumModelPacket.ADDRIGHTORANGETANK);
                }
                else if (GCPlayer.tankInSlot2.getItem() == GCItems.oxTankHeavy) {
                    sendGearUpdatePacket(player, EnumModelPacket.ADDRIGHTREDTANK);
                }
                if (GCPlayer.maskInSlot != null && GCPlayer.gearInSlot != null) {
                    GCPlayer.airRemaining2 = GCPlayer.tankInSlot2.getMaxDamage() - GCPlayer.tankInSlot2.getItemDamage();
                    sendAirRemainingPacket(player, GCPlayer);
                }
            }
            GCPlayer.lastTankInSlot2 = GCPlayer.tankInSlot2;
        }
        if (GCPlayer.parachuteInSlot != GCPlayer.lastParachuteInSlot || forceSend) {
            if (GCPlayer.usingParachute) {
                if (GCPlayer.parachuteInSlot == null) {
                    sendGearUpdatePacket(player, EnumModelPacket.REMOVE_PARACHUTE);
                }
                else if (GCPlayer.lastParachuteInSlot == null || forceSend) {
                    sendGearUpdatePacket(player, EnumModelPacket.ADD_PARACHUTE, GCPlayer.parachuteInSlot.getItemDamage());
                }
                else if (GCPlayer.parachuteInSlot.getItemDamage() != GCPlayer.lastParachuteInSlot.getItemDamage()) {
                    sendGearUpdatePacket(player, EnumModelPacket.ADD_PARACHUTE, GCPlayer.parachuteInSlot.getItemDamage());
                }
            }
            GCPlayer.lastParachuteInSlot = GCPlayer.parachuteInSlot;
        }
        if (GCPlayer.thermalHelmetInSlot != GCPlayer.lastThermalHelmetInSlot || forceSend) {
            final ThermalArmorEvent armorEvent = new ThermalArmorEvent(0, GCPlayer.thermalHelmetInSlot);
            MinecraftForge.EVENT_BUS.post((Event)armorEvent);
            if (armorEvent.armorResult != ThermalArmorEvent.ArmorAddResult.NOTHING) {
                if (GCPlayer.thermalHelmetInSlot == null || armorEvent.armorResult == ThermalArmorEvent.ArmorAddResult.REMOVE) {
                    sendGearUpdatePacket(player, EnumModelPacket.REMOVE_THERMAL_HELMET);
                }
                else if (armorEvent.armorResult == ThermalArmorEvent.ArmorAddResult.ADD && (GCPlayer.lastThermalHelmetInSlot == null || forceSend)) {
                    sendGearUpdatePacket(player, EnumModelPacket.ADD_THERMAL_HELMET);
                }
            }
            GCPlayer.lastThermalHelmetInSlot = GCPlayer.thermalHelmetInSlot;
        }
        if (GCPlayer.thermalChestplateInSlot != GCPlayer.lastThermalChestplateInSlot || forceSend) {
            final ThermalArmorEvent armorEvent = new ThermalArmorEvent(1, GCPlayer.thermalChestplateInSlot);
            MinecraftForge.EVENT_BUS.post((Event)armorEvent);
            if (armorEvent.armorResult != ThermalArmorEvent.ArmorAddResult.NOTHING) {
                if (GCPlayer.thermalChestplateInSlot == null || armorEvent.armorResult == ThermalArmorEvent.ArmorAddResult.REMOVE) {
                    sendGearUpdatePacket(player, EnumModelPacket.REMOVE_THERMAL_CHESTPLATE);
                }
                else if (armorEvent.armorResult == ThermalArmorEvent.ArmorAddResult.ADD && (GCPlayer.lastThermalChestplateInSlot == null || forceSend)) {
                    sendGearUpdatePacket(player, EnumModelPacket.ADD_THERMAL_CHESTPLATE);
                }
            }
            GCPlayer.lastThermalChestplateInSlot = GCPlayer.thermalChestplateInSlot;
        }
        if (GCPlayer.thermalLeggingsInSlot != GCPlayer.lastThermalLeggingsInSlot || forceSend) {
            final ThermalArmorEvent armorEvent = new ThermalArmorEvent(2, GCPlayer.thermalLeggingsInSlot);
            MinecraftForge.EVENT_BUS.post((Event)armorEvent);
            if (armorEvent.armorResult != ThermalArmorEvent.ArmorAddResult.NOTHING) {
                if (GCPlayer.thermalLeggingsInSlot == null || armorEvent.armorResult == ThermalArmorEvent.ArmorAddResult.REMOVE) {
                    sendGearUpdatePacket(player, EnumModelPacket.REMOVE_THERMAL_LEGGINGS);
                }
                else if (armorEvent.armorResult == ThermalArmorEvent.ArmorAddResult.ADD && (GCPlayer.lastThermalLeggingsInSlot == null || forceSend)) {
                    sendGearUpdatePacket(player, EnumModelPacket.ADD_THERMAL_LEGGINGS);
                }
            }
            GCPlayer.lastThermalLeggingsInSlot = GCPlayer.thermalLeggingsInSlot;
        }
        if (GCPlayer.thermalBootsInSlot != GCPlayer.lastThermalBootsInSlot || forceSend) {
            final ThermalArmorEvent armorEvent = new ThermalArmorEvent(3, GCPlayer.thermalBootsInSlot);
            MinecraftForge.EVENT_BUS.post((Event)armorEvent);
            if (armorEvent.armorResult != ThermalArmorEvent.ArmorAddResult.NOTHING) {
                if (GCPlayer.thermalBootsInSlot == null || armorEvent.armorResult == ThermalArmorEvent.ArmorAddResult.REMOVE) {
                    sendGearUpdatePacket(player, EnumModelPacket.REMOVE_THERMAL_BOOTS);
                }
                else if (armorEvent.armorResult == ThermalArmorEvent.ArmorAddResult.ADD && (GCPlayer.lastThermalBootsInSlot == null || forceSend)) {
                    sendGearUpdatePacket(player, EnumModelPacket.ADD_THERMAL_BOOTS);
                }
            }
            GCPlayer.lastThermalBootsInSlot = GCPlayer.thermalBootsInSlot;
        }
    }

    protected void checkThermalStatus(final EntityPlayerMP player, final GCPlayerStats playerStats) {
        if (player.worldObj.provider instanceof IGalacticraftWorldProvider && !player.capabilities.isCreativeMode && !CompatibilityManager.isAndroid((EntityPlayer)player)) {
            final ItemStack thermalPaddingHelm = playerStats.extendedInventory.getStackInSlot(6);
            final ItemStack thermalPaddingChestplate = playerStats.extendedInventory.getStackInSlot(7);
            final ItemStack thermalPaddingLeggings = playerStats.extendedInventory.getStackInSlot(8);
            final ItemStack thermalPaddingBoots = playerStats.extendedInventory.getStackInSlot(9);
            float lowestThermalStrength = 0.0f;
            if (thermalPaddingHelm != null && thermalPaddingChestplate != null && thermalPaddingLeggings != null && thermalPaddingBoots != null) {
                if (thermalPaddingHelm.getItem() instanceof IItemThermal) {
                    lowestThermalStrength += ((IItemThermal)thermalPaddingHelm.getItem()).getThermalStrength();
                }
                if (thermalPaddingChestplate.getItem() instanceof IItemThermal) {
                    lowestThermalStrength += ((IItemThermal)thermalPaddingChestplate.getItem()).getThermalStrength();
                }
                if (thermalPaddingLeggings.getItem() instanceof IItemThermal) {
                    lowestThermalStrength += ((IItemThermal)thermalPaddingLeggings.getItem()).getThermalStrength();
                }
                if (thermalPaddingBoots.getItem() instanceof IItemThermal) {
                    lowestThermalStrength += ((IItemThermal)thermalPaddingBoots.getItem()).getThermalStrength();
                }
                lowestThermalStrength /= 4.0f;
            }
            final IGalacticraftWorldProvider provider = (IGalacticraftWorldProvider)player.worldObj.provider;
            float thermalLevelMod = provider.getThermalLevelModifier();
            double absThermalLevelMod = Math.abs(thermalLevelMod);
            if (absThermalLevelMod > 0.0) {
                final int thermalLevelCooldownBase = Math.abs(MathHelper.floor_double((double)(200.0f / thermalLevelMod)));
                int normaliseCooldown = Math.abs(MathHelper.floor_double((double)(150.0f / lowestThermalStrength)));
                int thermalLevelTickCooldown = thermalLevelCooldownBase;
                if (thermalLevelTickCooldown < 1) {
                    thermalLevelTickCooldown = 1;
                }
                if (thermalPaddingHelm != null && thermalPaddingChestplate != null && thermalPaddingLeggings != null && thermalPaddingBoots != null) {
                    thermalLevelMod /= Math.max(1.0f, lowestThermalStrength / 2.0f);
                    absThermalLevelMod = Math.abs(thermalLevelMod);
                    normaliseCooldown = MathHelper.floor_double(normaliseCooldown / absThermalLevelMod);
                    if (normaliseCooldown < 1) {
                        normaliseCooldown = 1;
                    }
                    if ((player.ticksExisted - 1) % normaliseCooldown == 0) {
                        this.normaliseThermalLevel(player, playerStats, 1);
                    }
                }
                if (OxygenUtil.isAABBInBreathableAirBlock((EntityLivingBase)player, true)) {
                    playerStats.thermalLevelNormalising = true;
                    this.normaliseThermalLevel(player, playerStats, 1);
                    return;
                }
                if (thermalPaddingHelm != null) {
                    thermalLevelTickCooldown += thermalLevelCooldownBase;
                }
                if (thermalPaddingChestplate != null) {
                    thermalLevelTickCooldown += thermalLevelCooldownBase;
                }
                if (thermalPaddingLeggings != null) {
                    thermalLevelTickCooldown += thermalLevelCooldownBase;
                }
                if (thermalPaddingBoots != null) {
                    thermalLevelTickCooldown += thermalLevelCooldownBase;
                }
                int thermalLevelTickCooldownSingle = MathHelper.floor_double(thermalLevelTickCooldown / absThermalLevelMod);
                if (thermalLevelTickCooldownSingle < 1) {
                    thermalLevelTickCooldownSingle = 1;
                }
                if ((player.ticksExisted - 1) % thermalLevelTickCooldownSingle == 0) {
                    final int last = playerStats.thermalLevel;
                    playerStats.thermalLevel = Math.min(Math.max(playerStats.thermalLevel + ((thermalLevelMod < 0.0f) ? -1 : 1), -22), 22);
                    if (playerStats.thermalLevel != last) {
                        this.sendThermalLevelPacket(player, playerStats);
                    }
                }
                if (!(playerStats.thermalLevelNormalising = (thermalLevelTickCooldownSingle > normaliseCooldown && thermalPaddingHelm != null && thermalPaddingChestplate != null && thermalPaddingLeggings != null && thermalPaddingBoots != null))) {
                    if ((player.ticksExisted - 1) % thermalLevelTickCooldown == 0 && Math.abs(playerStats.thermalLevel) >= 22) {
                        player.attackEntityFrom((DamageSource)DamageSourceGC.thermal, 1.5f);
                    }
                    if (playerStats.thermalLevel < -15) {
                        player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 5, 2, true));
                    }
                    if (playerStats.thermalLevel > 15) {
                        player.addPotionEffect(new PotionEffect(Potion.confusion.id, 5, 2, true));
                    }
                }
            }
            else {
                playerStats.thermalLevelNormalising = true;
                this.normaliseThermalLevel(player, playerStats, 2);
            }
        }
        else {
            playerStats.thermalLevelNormalising = true;
            this.normaliseThermalLevel(player, playerStats, 3);
        }
    }

    public void normaliseThermalLevel(final EntityPlayerMP player, final GCPlayerStats playerStats, final int increment) {
        final int last = playerStats.thermalLevel;
        if (playerStats.thermalLevel < 0) {
            playerStats.thermalLevel += Math.min(increment, -playerStats.thermalLevel);
        }
        else if (playerStats.thermalLevel > 0) {
            playerStats.thermalLevel -= Math.min(increment, playerStats.thermalLevel);
        }
        if (playerStats.thermalLevel != last) {
            this.sendThermalLevelPacket(player, playerStats);
        }
    }

    protected void checkOxygen(final EntityPlayerMP player, final GCPlayerStats playerStats) {
        if ((player.dimension == 0 || player.worldObj.provider instanceof IGalacticraftWorldProvider) && ((player.dimension != 0 && !((IGalacticraftWorldProvider)player.worldObj.provider).hasBreathableAtmosphere()) || player.posY > 450.0) && !player.capabilities.isCreativeMode && !(player.ridingEntity instanceof EntityLanderBase) && !(player.ridingEntity instanceof EntityAutoRocket) && !(player.ridingEntity instanceof EntityCelestialFake) && !CompatibilityManager.isAndroid((EntityPlayer)player)) {
            final ItemStack tankInSlot = playerStats.extendedInventory.getStackInSlot(2);
            final ItemStack tankInSlot2 = playerStats.extendedInventory.getStackInSlot(3);
            final int drainSpacing = OxygenUtil.getDrainSpacing(tankInSlot, tankInSlot2);
            if (tankInSlot == null) {
                playerStats.airRemaining = 0;
            }
            else {
                playerStats.airRemaining = tankInSlot.getMaxDamage() - tankInSlot.getItemDamage();
            }
            if (tankInSlot2 == null) {
                playerStats.airRemaining2 = 0;
            }
            else {
                playerStats.airRemaining2 = tankInSlot2.getMaxDamage() - tankInSlot2.getItemDamage();
            }
            if (drainSpacing > 0) {
                if ((player.ticksExisted - 1) % drainSpacing == 0 && !OxygenUtil.isAABBInBreathableAirBlock((EntityLivingBase)player) && !playerStats.usingPlanetSelectionGui) {
                    int toTake = 1;
                    if (playerStats.airRemaining > 0) {
                        tankInSlot.damageItem(1, (EntityLivingBase)player);
                        --playerStats.airRemaining;
                        toTake = 0;
                    }
                    if (toTake > 0 && playerStats.airRemaining2 > 0) {
                        tankInSlot2.damageItem(1, (EntityLivingBase)player);
                        --playerStats.airRemaining2;
                        toTake = 0;
                    }
                }
            }
            else if ((player.ticksExisted - 1) % 60 == 0) {
                if (OxygenUtil.isAABBInBreathableAirBlock((EntityLivingBase)player)) {
                    if (playerStats.airRemaining < 90 && tankInSlot != null) {
                        playerStats.airRemaining = Math.min(playerStats.airRemaining + 1, tankInSlot.getMaxDamage() - tankInSlot.getItemDamage());
                    }
                    if (playerStats.airRemaining2 < 90 && tankInSlot2 != null) {
                        playerStats.airRemaining2 = Math.min(playerStats.airRemaining2 + 1, tankInSlot2.getMaxDamage() - tankInSlot2.getItemDamage());
                    }
                }
                else {
                    if (playerStats.airRemaining > 0) {
                        --playerStats.airRemaining;
                    }
                    if (playerStats.airRemaining2 > 0) {
                        --playerStats.airRemaining2;
                    }
                }
            }
            final boolean airEmpty = playerStats.airRemaining <= 0 && playerStats.airRemaining2 <= 0;
            if (player.isOnLadder()) {
                playerStats.oxygenSetupValid = playerStats.lastOxygenSetupValid;
            }
            else {
                playerStats.oxygenSetupValid = ((OxygenUtil.hasValidOxygenSetup(player) && !airEmpty) || OxygenUtil.isAABBInBreathableAirBlock((EntityLivingBase)player));
            }
            if (!player.worldObj.isRemote && player.isEntityAlive()) {
                if (!playerStats.oxygenSetupValid) {
                    final GCCoreOxygenSuffocationEvent suffocationEvent = (GCCoreOxygenSuffocationEvent)new GCCoreOxygenSuffocationEvent.Pre((EntityLivingBase)player);
                    MinecraftForge.EVENT_BUS.post((Event)suffocationEvent);
                    if (!suffocationEvent.isCanceled()) {
                        if (playerStats.damageCounter == 0) {
                            playerStats.damageCounter = ConfigManagerCore.suffocationCooldown;
                            player.attackEntityFrom((DamageSource)DamageSourceGC.oxygenSuffocation, (float)(ConfigManagerCore.suffocationDamage * (2 + playerStats.incrementalDamage) / 2));
                            if (ConfigManagerCore.hardMode) {
                                ++playerStats.incrementalDamage;
                            }
                            final GCCoreOxygenSuffocationEvent suffocationEventPost = (GCCoreOxygenSuffocationEvent)new GCCoreOxygenSuffocationEvent.Post((EntityLivingBase)player);
                            MinecraftForge.EVENT_BUS.post((Event)suffocationEventPost);
                        }
                    }
                    else {
                        playerStats.oxygenSetupValid = true;
                    }
                }
                else {
                    playerStats.incrementalDamage = 0;
                }
            }
        }
        else if ((player.ticksExisted - 1) % 20 == 0 && !player.capabilities.isCreativeMode && playerStats.airRemaining < 90) {
            ++playerStats.airRemaining;
            ++playerStats.airRemaining2;
        }
        else if (player.capabilities.isCreativeMode) {
            playerStats.airRemaining = 90;
            playerStats.airRemaining2 = 90;
        }
        else {
            playerStats.oxygenSetupValid = true;
        }
    }

    protected void throwMeteors(final EntityPlayerMP player) {
        final World world = player.worldObj;
        if (world.provider instanceof IGalacticraftWorldProvider && !world.isRemote && ((IGalacticraftWorldProvider)world.provider).getMeteorFrequency() > 0.0 && ConfigManagerCore.meteorSpawnMod > 0.0) {
            final int f = (int)(((IGalacticraftWorldProvider)world.provider).getMeteorFrequency() * 1000.0 * (1.0 / ConfigManagerCore.meteorSpawnMod));
            if (world.rand.nextInt(f) == 0) {
                final EntityPlayer closestPlayer = world.getClosestPlayerToEntity((Entity)player, 100.0);
                if (closestPlayer == null || closestPlayer.getEntityId() <= player.getEntityId()) {
                    final int x = world.rand.nextInt(20) - 10;
                    final int y = world.rand.nextInt(20) + 200;
                    final int z = world.rand.nextInt(20) - 10;
                    final double motX = world.rand.nextDouble() * 5.0;
                    final double motZ = world.rand.nextDouble() * 5.0;
                    final EntityMeteor meteor = new EntityMeteor(world, player.posX + x, player.posY + y, player.posZ + z, motX - 2.5, 0.0, motZ - 2.5, 1);
                    if (!world.isRemote) {
                        world.spawnEntityInWorld((Entity)meteor);
                    }
                }
            }
            if (world.rand.nextInt(f * 3) == 0) {
                final EntityPlayer closestPlayer = world.getClosestPlayerToEntity((Entity)player, 100.0);
                if (closestPlayer == null || closestPlayer.getEntityId() <= player.getEntityId()) {
                    final int x = world.rand.nextInt(20) - 10;
                    final int y = world.rand.nextInt(20) + 200;
                    final int z = world.rand.nextInt(20) - 10;
                    final double motX = world.rand.nextDouble() * 5.0;
                    final double motZ = world.rand.nextDouble() * 5.0;
                    final EntityMeteor meteor = new EntityMeteor(world, player.posX + x, player.posY + y, player.posZ + z, motX - 2.5, 0.0, motZ - 2.5, 6);
                    if (!world.isRemote) {
                        world.spawnEntityInWorld((Entity)meteor);
                    }
                }
            }
        }
    }

    protected void checkCurrentItem(final EntityPlayerMP player) {
        final ItemStack theCurrentItem = player.inventory.getCurrentItem();
        if (theCurrentItem != null) {
            if (OxygenUtil.noAtmosphericCombustion(player.worldObj.provider)) {
                if (this.torchItems.containsValue(theCurrentItem.getItem())) {
                    Item torchItem = null;
                    for (final Item i : this.torchItems.keySet()) {
                        if (this.torchItems.get(i) == theCurrentItem.getItem()) {
                            torchItem = i;
                            break;
                        }
                    }
                    if (torchItem != null) {
                        player.inventory.mainInventory[player.inventory.currentItem] = new ItemStack(torchItem, theCurrentItem.stackSize, 0);
                    }
                }
            }
            else if (this.torchItems.containsKey(theCurrentItem.getItem())) {
                final Item torchItem = this.torchItems.get(theCurrentItem.getItem());
                if (torchItem != null) {
                    player.inventory.mainInventory[player.inventory.currentItem] = new ItemStack(torchItem, theCurrentItem.stackSize, 0);
                }
            }
        }
    }

    public void registerTorchType(final BlockUnlitTorch spaceTorch, final Block vanillaTorch) {
        final Item itemSpaceTorch = Item.getItemFromBlock((Block)spaceTorch);
        final Item itemVanillaTorch = Item.getItemFromBlock(vanillaTorch);
        this.torchItems.put(itemSpaceTorch, itemVanillaTorch);
    }

    public static void setUsingParachute(final EntityPlayerMP player, final GCPlayerStats playerStats, final boolean tf) {
        playerStats.usingParachute = tf;
        if (tf) {
            int subtype = -1;
            if (playerStats.parachuteInSlot != null) {
                subtype = playerStats.parachuteInSlot.getItemDamage();
            }
            sendGearUpdatePacket(player, EnumModelPacket.ADD_PARACHUTE, subtype);
        }
        else {
            sendGearUpdatePacket(player, EnumModelPacket.REMOVE_PARACHUTE);
        }
    }

    protected static void updateFeet(final EntityPlayerMP player, final double motionX, final double motionZ) {
        final double motionSqrd = motionX * motionX + motionZ * motionZ;
        if (motionSqrd > 0.001 && !player.capabilities.isFlying) {
            final int iPosX = MathHelper.floor_double(player.posX);
            final int iPosY = MathHelper.floor_double(player.posY) - 1;
            final int iPosZ = MathHelper.floor_double(player.posZ);
            if (player.worldObj.getBlock(iPosX, iPosY, iPosZ) == GCBlocks.blockMoon && player.worldObj.getBlockMetadata(iPosX, iPosY, iPosZ) == 5) {
                final GCPlayerStats playerStats = GCPlayerStats.get(player);
                if (playerStats.distanceSinceLastStep > 0.35) {
                    Vector3 pos = new Vector3((Entity)player);
                    pos.y = MathHelper.floor_double(player.posY - 1.0) + player.worldObj.rand.nextFloat() / 100.0f;
                    switch (playerStats.lastStep) {
                        case 0: {
                            final float a = (-player.rotationYaw + 90.0f) / 57.29578f;
                            pos.translate(new Vector3((double)(MathHelper.sin(a) * 0.25f), 0.0, (double)(MathHelper.cos(a) * 0.25f)));
                            break;
                        }
                        case 1: {
                            final float a = (-player.rotationYaw - 90.0f) / 57.29578f;
                            pos.translate(new Vector3(MathHelper.sin(a) * 0.25, 0.0, MathHelper.cos(a) * 0.25));
                            break;
                        }
                    }
                    final float rotation = player.rotationYaw - 180.0f;
                    pos = WorldUtil.getFootprintPosition(player.worldObj, rotation, pos, new BlockVec3((Entity)player));
                    final long chunkKey = ChunkCoordIntPair.chunkXZ2Int(pos.intX() >> 4, pos.intZ() >> 4);
                    TickHandlerServer.addFootprint(chunkKey, new Footprint(player.worldObj.provider.dimensionId, pos, rotation, player.getCommandSenderName()), player.worldObj.provider.dimensionId);
                    final GCPlayerStats gcPlayerStats = playerStats;
                    ++gcPlayerStats.lastStep;
                    final GCPlayerStats gcPlayerStats2 = playerStats;
                    gcPlayerStats2.lastStep %= 2;
                    playerStats.distanceSinceLastStep = 0.0;
                }
                else {
                    final GCPlayerStats gcPlayerStats3 = playerStats;
                    gcPlayerStats3.distanceSinceLastStep += motionSqrd;
                }
            }
        }
    }

    protected void updateSchematics(final EntityPlayerMP player, final GCPlayerStats playerStats) {
        SchematicRegistry.addUnlockedPage(player, SchematicRegistry.getMatchingRecipeForID(0));
        SchematicRegistry.addUnlockedPage(player, SchematicRegistry.getMatchingRecipeForID(Integer.MAX_VALUE));
        Collections.sort(playerStats.unlockedSchematics);
        if (player.playerNetServerHandler != null && (playerStats.unlockedSchematics.size() != playerStats.lastUnlockedSchematics.size() || (player.ticksExisted - 1) % 100 == 0)) {
            final Integer[] iArray = new Integer[playerStats.unlockedSchematics.size()];
            for (int i = 0; i < iArray.length; ++i) {
                final ISchematicPage page = playerStats.unlockedSchematics.get(i);
                iArray[i] = ((page == null) ? -2 : page.getPageID());
            }
            final List<Object> objList = new ArrayList<Object>();
            objList.add(iArray);
            GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_SCHEMATIC_LIST, objList), player);
        }
    }

    protected void sendPlanetList(final EntityPlayerMP player, final GCPlayerStats playerStats) {
        HashMap<String, Integer> map;
        if (player.ticksExisted % 50 == 0) {
            map = WorldUtil.getArrayOfPossibleDimensions(playerStats.spaceshipTier, player);
        }
        else {
            map = WorldUtil.getArrayOfPossibleDimensionsAgain(playerStats.spaceshipTier, player);
        }
        String temp = "";
        int count = 0;
        for (final Map.Entry<String, Integer> entry : map.entrySet()) {
            temp = temp.concat(entry.getKey() + ((count < map.entrySet().size() - 1) ? "?" : ""));
            ++count;
        }
        if (!temp.equals(playerStats.savedPlanetList) || player.ticksExisted % 100 == 0) {
            GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_DIMENSION_LIST, new Object[] { player.getGameProfile().getName(), temp }), player);
            playerStats.savedPlanetList = new String(temp);
        }
    }

    protected static void sendAirRemainingPacket(final EntityPlayerMP player, final GCPlayerStats playerStats) {
        final float f1 = (playerStats.tankInSlot1 == null) ? 0.0f : (playerStats.tankInSlot1.getMaxDamage() / 90.0f);
        final float f2 = (playerStats.tankInSlot2 == null) ? 0.0f : (playerStats.tankInSlot2.getMaxDamage() / 90.0f);
        GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_AIR_REMAINING, new Object[] { MathHelper.floor_float(playerStats.airRemaining / f1), MathHelper.floor_float(playerStats.airRemaining2 / f2), player.getGameProfile().getName() }), player);
    }

    protected void sendThermalLevelPacket(final EntityPlayerMP player, final GCPlayerStats playerStats) {
        GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_THERMAL_LEVEL, new Object[] { playerStats.thermalLevel, playerStats.thermalLevelNormalising }), player);
    }

    public static void sendGearUpdatePacket(final EntityPlayerMP player, final EnumModelPacket gearType) {
        final MinecraftServer theServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (theServer != null && PlayerUtil.getPlayerForUsernameVanilla(theServer, player.getGameProfile().getName()) != null) {
            GalacticraftCore.packetPipeline.sendToAllAround(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_GEAR_SLOT, new Object[] { player.getGameProfile().getName(), gearType.ordinal(), -1 }), new NetworkRegistry.TargetPoint(player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ, 50.0));
        }
    }

    public static void sendGearUpdatePacket(final EntityPlayerMP player, final EnumModelPacket gearType, final int subtype) {
        final MinecraftServer theServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (theServer != null && PlayerUtil.getPlayerForUsernameVanilla(theServer, player.getGameProfile().getName()) != null) {
            GalacticraftCore.packetPipeline.sendToAllAround(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_GEAR_SLOT, new Object[] { player.getGameProfile().getName(), gearType.ordinal(), subtype }), new NetworkRegistry.TargetPoint(player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ, 50.0));
        }
    }

    public void onPlayerUpdate(final EntityPlayerMP player) {
        final int tick = player.ticksExisted - 1;
        final GCPlayerStats GCPlayer = GCPlayerStats.get(player);
        if (ConfigManagerCore.challengeSpawnHandling && GCPlayer.unlockedSchematics.size() == 0) {
            if (GCPlayer.startDimension.length() > 0) {
                GCPlayer.startDimension = "";
            }
            else {
                final WorldServer worldOld = (WorldServer)player.worldObj;
                try {
                    worldOld.getPlayerManager().removePlayer(player);
                }
                catch (Exception ex) {}
                worldOld.playerEntities.remove(player);
                worldOld.updateAllPlayersSleepingFlag();
                worldOld.loadedEntityList.remove(player);
                worldOld.onEntityRemoved((Entity)player);
                if (player.addedToChunk && worldOld.getChunkProvider().chunkExists(player.chunkCoordX, player.chunkCoordZ)) {
                    final Chunk chunkOld = worldOld.getChunkFromChunkCoords(player.chunkCoordX, player.chunkCoordZ);
                    chunkOld.removeEntity((Entity)player);
                    chunkOld.isModified = true;
                }
                final WorldServer worldNew = WorldUtil.getStartWorld(worldOld);
                final int dimID = worldNew.provider.dimensionId;
                player.dimension = dimID;
                GCLog.debug("DEBUG: Sending respawn packet to player for dim " + dimID);
                player.playerNetServerHandler.sendPacket((Packet)new S07PacketRespawn(dimID, player.worldObj.difficultySetting, player.worldObj.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType()));
                if (worldNew.provider instanceof WorldProviderSpaceStation) {
                    GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_RESET_THIRD_PERSON, new Object[0]), player);
                }
                worldNew.spawnEntityInWorld((Entity)player);
                player.setWorld((World)worldNew);
            }
            final ITeleportType type = GalacticraftRegistry.getTeleportTypeForDimension((Class)player.worldObj.provider.getClass());
            final Vector3 spawnPos = type.getPlayerSpawnLocation((WorldServer)player.worldObj, player);
            final ChunkCoordIntPair pair = player.worldObj.getChunkFromChunkCoords(spawnPos.intX(), spawnPos.intZ()).getChunkCoordIntPair();
            GCLog.debug("Loading first chunk in new dimension.");
            ((WorldServer)player.worldObj).theChunkProviderServer.loadChunk(pair.chunkXPos, pair.chunkZPos);
            player.setLocationAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, player.rotationYaw, player.rotationPitch);
            type.setupAdventureSpawn(player);
            type.onSpaceDimensionChanged(player.worldObj, player, false);
            player.setSpawnChunk(new ChunkCoordinates(spawnPos.intX(), spawnPos.intY(), spawnPos.intZ()), true, player.worldObj.provider.dimensionId);
            GCPlayer.newAdventureSpawn = true;
        }
        final boolean isInGCDimension = player.worldObj.provider instanceof IGalacticraftWorldProvider;
        if (tick >= 25) {
            if (ConfigManagerCore.enableSpaceRaceManagerPopup && !GCPlayer.openedSpaceRaceManager) {
                final SpaceRace race = SpaceRaceManager.getSpaceRaceFromPlayer(player.getGameProfile().getName());
                if (race == null || race.teamName.equals("gui.spaceRace.unnamed")) {
                    GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_OPEN_SPACE_RACE_GUI, new Object[0]), player);
                }
                GCPlayer.openedSpaceRaceManager = true;
            }
            if (!GCPlayer.sentFlags) {
                GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_STATS, new Object[] { GCPlayer.buildFlags }), player);
                GCPlayer.sentFlags = true;
            }
        }
        if (GCPlayer.cryogenicChamberCooldown > 0) {
            final GCPlayerStats gcPlayerStats = GCPlayer;
            --gcPlayerStats.cryogenicChamberCooldown;
        }
        if (!player.onGround && GCPlayer.lastOnGround) {
            GCPlayer.touchedGround = true;
        }
        if (GCPlayer.teleportCooldown > 0) {
            final GCPlayerStats gcPlayerStats2 = GCPlayer;
            --gcPlayerStats2.teleportCooldown;
        }
        if (GCPlayer.chatCooldown > 0) {
            final GCPlayerStats gcPlayerStats3 = GCPlayer;
            --gcPlayerStats3.chatCooldown;
        }
        if (GCPlayer.openPlanetSelectionGuiCooldown > 0) {
            final GCPlayerStats gcPlayerStats4 = GCPlayer;
            --gcPlayerStats4.openPlanetSelectionGuiCooldown;
            if (GCPlayer.openPlanetSelectionGuiCooldown == 1 && !GCPlayer.hasOpenedPlanetSelectionGui) {
                WorldUtil.toCelestialSelection(player, GCPlayer, GCPlayer.spaceshipTier);
                GCPlayer.hasOpenedPlanetSelectionGui = true;
            }
        }
        if (GCPlayer.usingParachute) {
            if (GCPlayer.lastParachuteInSlot != null) {
                player.fallDistance = 0.0f;
            }
            if (player.onGround) {
                setUsingParachute(player, GCPlayer, false);
            }
        }
        this.checkCurrentItem(player);
        if (GCPlayer.usingPlanetSelectionGui) {
            this.sendPlanetList(player, GCPlayer);
        }
        if (GCPlayer.damageCounter > 0) {
            final GCPlayerStats gcPlayerStats5 = GCPlayer;
            --gcPlayerStats5.damageCounter;
        }
        if (isInGCDimension) {
            if (tick % 30 == 0) {
                sendAirRemainingPacket(player, GCPlayer);
                this.sendThermalLevelPacket(player, GCPlayer);
            }
            if (player.ridingEntity instanceof EntityLanderBase) {
                GCPlayer.inLander = true;
                GCPlayer.justLanded = false;
            }
            else {
                if (GCPlayer.inLander) {
                    GCPlayer.justLanded = true;
                }
                GCPlayer.inLander = false;
            }
            if (player.onGround && GCPlayer.justLanded) {
                GCPlayer.justLanded = false;
                if (player.getBedLocation(player.worldObj.provider.dimensionId) == null || GCPlayer.newAdventureSpawn) {
                    final int i = 30000000;
                    final int j = Math.min(i, Math.max(-i, MathHelper.floor_double(player.posX + 0.5)));
                    final int k = Math.min(256, Math.max(0, MathHelper.floor_double(player.posY + 1.5)));
                    final int l = Math.min(i, Math.max(-i, MathHelper.floor_double(player.posZ + 0.5)));
                    final ChunkCoordinates coords = new ChunkCoordinates(j, k, l);
                    player.setSpawnChunk(coords, true, player.worldObj.provider.dimensionId);
                    GCPlayer.newAdventureSpawn = false;
                }
                GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_RESET_THIRD_PERSON, new Object[0]), player);
            }
            if (player.worldObj.provider instanceof WorldProviderSpaceStation || player.worldObj.provider instanceof IZeroGDimension || (GalacticraftCore.isPlanetsLoaded && player.worldObj.provider instanceof WorldProviderAsteroids)) {
                this.preventFlyingKicks(player);
            }
            if (player.worldObj.provider instanceof WorldProviderSpaceStation) {
                if (GCPlayer.newInOrbit) {
                    ((WorldProviderSpaceStation)player.worldObj.provider).getSpinManager().sendPacketsToClient(player);
                    GCPlayer.newInOrbit = false;
                }
            }
            else {
                GCPlayer.newInOrbit = true;
            }
        }
        else {
            GCPlayer.newInOrbit = true;
        }
        checkGear(player, GCPlayer, false);
        if (GCPlayer.chestSpawnCooldown > 0) {
            final GCPlayerStats gcPlayerStats6 = GCPlayer;
            --gcPlayerStats6.chestSpawnCooldown;
            if (GCPlayer.chestSpawnCooldown == 180 && GCPlayer.chestSpawnVector != null) {
                final EntityParachest chest = new EntityParachest(player.worldObj, GCPlayer.rocketStacks, GCPlayer.fuelLevel);
                chest.setPosition(GCPlayer.chestSpawnVector.x, GCPlayer.chestSpawnVector.y, GCPlayer.chestSpawnVector.z);
                if (!player.worldObj.isRemote) {
                    if (player.worldObj.isAirBlock((int)GCPlayer.chestSpawnVector.x, (int)GCPlayer.chestSpawnVector.y, (int)GCPlayer.chestSpawnVector.z)) {
                        player.worldObj.spawnEntityInWorld((Entity)chest);
                    }
                    else {
                        for (final ItemStack stacks : GCPlayer.rocketStacks) {
                            final EntityItem entityitem = new EntityItem(player.worldObj, GCPlayer.chestSpawnVector.x, GCPlayer.chestSpawnVector.y + 1.0, GCPlayer.chestSpawnVector.z, stacks);
                            player.worldObj.spawnEntityInWorld((Entity)entityitem);
                        }
                    }
                }
            }
        }
        if (GCPlayer.launchAttempts > 0 && player.ridingEntity == null) {
            GCPlayer.launchAttempts = 0;
        }
        this.checkThermalStatus(player, GCPlayer);
        this.checkOxygen(player, GCPlayer);
        if (isInGCDimension && (GCPlayer.oxygenSetupValid != GCPlayer.lastOxygenSetupValid || tick % 100 == 0)) {
            GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_OXYGEN_VALIDITY, new Object[] { GCPlayer.oxygenSetupValid }), player);
        }
        this.throwMeteors(player);
        this.updateSchematics(player, GCPlayer);
        if (tick % 250 == 0 && GCPlayer.frequencyModuleInSlot == null && !GCPlayer.receivedSoundWarning && isInGCDimension && player.onGround && tick > 0 && ((IGalacticraftWorldProvider)player.worldObj.provider).getSoundVolReductionAmount() > 1.0f) {
            final String[] string2 = GCCoreUtil.translate("gui.frequencymodule.warning1").split(" ");
            final StringBuilder sb = new StringBuilder();
            for (int m = 0; m < string2.length; ++m) {
                sb.append(" " + EnumColor.YELLOW + string2[m]);
            }
            player.addChatMessage((IChatComponent)new ChatComponentText(EnumColor.YELLOW + GCCoreUtil.translate("gui.frequencymodule.warning0") + " " + EnumColor.AQUA + GCItems.basicItem.getItemStackDisplayName(new ItemStack(GCItems.basicItem, 1, 19)) + sb.toString()));
            GCPlayer.receivedSoundWarning = true;
        }
        GCPlayer.lastOxygenSetupValid = GCPlayer.oxygenSetupValid;
        GCPlayer.lastUnlockedSchematics = GCPlayer.unlockedSchematics;
        GCPlayer.lastOnGround = player.onGround;
    }

    public void preventFlyingKicks(final EntityPlayerMP player) {
        player.fallDistance = 0.0f;
        try {
            if (this.ftc == null) {
                (this.ftc = player.playerNetServerHandler.getClass().getDeclaredField(VersionUtil.getNameDynamic("floatingTickCount"))).setAccessible(true);
            }
            this.ftc.setInt(player.playerNetServerHandler, 0);
        }
        catch (Exception ex) {}
    }

    public static class ThermalArmorEvent extends Event
    {
        public ArmorAddResult armorResult;
        public final int armorIndex;
        public final ItemStack armorStack;

        public ThermalArmorEvent(final int armorIndex, final ItemStack armorStack) {
            this.armorResult = ArmorAddResult.NOTHING;
            this.armorIndex = armorIndex;
            this.armorStack = armorStack;
        }

        public void setArmorAddResult(final ArmorAddResult result) {
            this.armorResult = result;
        }

        public enum ArmorAddResult
        {
            ADD,
            REMOVE,
            NOTHING;
        }
    }

    public enum EnumModelPacket
    {
        ADDMASK,
        REMOVEMASK,
        ADDGEAR,
        REMOVEGEAR,
        ADDLEFTREDTANK,
        ADDLEFTORANGETANK,
        ADDLEFTGREENTANK,
        REMOVE_LEFT_TANK,
        ADDRIGHTREDTANK,
        ADDRIGHTORANGETANK,
        ADDRIGHTGREENTANK,
        REMOVE_RIGHT_TANK,
        ADD_PARACHUTE,
        REMOVE_PARACHUTE,
        ADD_FREQUENCY_MODULE,
        REMOVE_FREQUENCY_MODULE,
        ADD_THERMAL_HELMET,
        ADD_THERMAL_CHESTPLATE,
        ADD_THERMAL_LEGGINGS,
        ADD_THERMAL_BOOTS,
        REMOVE_THERMAL_HELMET,
        REMOVE_THERMAL_CHESTPLATE,
        REMOVE_THERMAL_LEGGINGS,
        REMOVE_THERMAL_BOOTS;
    }
}
