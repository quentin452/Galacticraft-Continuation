package micdoodle8.mods.galacticraft.core.event;

import cpw.mods.fml.client.event.*;
import micdoodle8.mods.galacticraft.core.world.*;
import net.minecraftforge.event.world.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;
import micdoodle8.mods.galacticraft.api.world.*;
import net.minecraft.init.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.dimension.*;
import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.world.*;
import net.minecraft.item.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.planets.asteroids.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import micdoodle8.mods.galacticraft.api.event.oxygen.*;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.entity.*;
import net.minecraftforge.event.terraingen.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.world.biome.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.world.chunk.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import micdoodle8.mods.galacticraft.core.network.*;
import cpw.mods.fml.client.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import net.minecraft.client.entity.*;
import java.util.*;
import net.minecraftforge.event.entity.player.*;
import micdoodle8.mods.galacticraft.planets.mars.network.*;
import net.minecraftforge.event.entity.living.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraftforge.client.event.*;
import net.minecraft.potion.*;
import net.minecraft.client.*;
import micdoodle8.mods.galacticraft.core.client.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.util.*;
import net.minecraftforge.client.event.sound.*;
import micdoodle8.mods.galacticraft.core.wrappers.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import net.minecraft.client.audio.*;
import net.minecraft.entity.monster.*;

public class EventHandlerGC
{
    public static Map<Block, Item> bucketList;
    public static boolean bedActivated;
    private List<SoundPlayEntry> soundPlayList;

    public EventHandlerGC() {
        this.soundPlayList = new ArrayList<SoundPlayEntry>();
    }

    @SubscribeEvent
    public void onRocketLaunch(final EntitySpaceshipBase.RocketLaunchEvent event) {
    }

    @SubscribeEvent
    public void onConfigChanged(final ConfigChangedEvent event) {
        if (event.modID.equals("GalacticraftCore")) {
            ConfigManagerCore.syncConfig(false);
        }
    }

    @SubscribeEvent
    public void onWorldSave(final WorldEvent.Save event) {
        ChunkLoadingCallback.save((WorldServer)event.world);
    }

    @SubscribeEvent
    public void onChunkDataLoad(final ChunkDataEvent.Load event) {
        ChunkLoadingCallback.load((WorldServer)event.world);
    }

    @SubscribeEvent
    public void onWorldLoad(final ChunkEvent.Load event) {
        if (!event.world.isRemote) {
            ChunkLoadingCallback.load((WorldServer)event.world);
        }
    }

    @SubscribeEvent
    public void onEntityDamaged(final LivingHurtEvent event) {
        if (event.source.damageType.equals(DamageSource.onFire.damageType) && OxygenUtil.noAtmosphericCombustion(event.entityLiving.worldObj.provider)) {
            if (OxygenUtil.isAABBInBreathableAirBlock(event.entityLiving.worldObj, event.entityLiving.boundingBox)) {
                return;
            }
            if (event.entityLiving.worldObj instanceof WorldServer) {
                ((WorldServer)event.entityLiving.worldObj).func_147487_a("smoke", event.entityLiving.posX, event.entityLiving.posY + event.entityLiving.boundingBox.maxY - event.entityLiving.boundingBox.minY, event.entityLiving.posZ, 50, 0.0, 0.05, 0.0, 0.001);
            }
            event.entityLiving.extinguish();
        }
    }

    @SubscribeEvent
    public void onEntityFall(final LivingFallEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer)event.entityLiving;
            if (player.ridingEntity instanceof EntityAutoRocket || player.ridingEntity instanceof EntityLanderBase) {
                event.distance = 0.0f;
                event.setCanceled(true);
                return;
            }
        }
        if (event.entityLiving.worldObj.provider instanceof IGalacticraftWorldProvider) {
            event.distance *= ((IGalacticraftWorldProvider)event.entityLiving.worldObj.provider).getFallDamageModifier();
        }
    }

    @SubscribeEvent
    public void onPlayerClicked(final PlayerInteractEvent event) {
        if (event.entityPlayer == null || event.entityPlayer.inventory == null) {
            return;
        }
        final World worldObj = event.entityPlayer.worldObj;
        if (worldObj == null) {
            return;
        }
        final Block idClicked = worldObj.getBlock(event.x, event.y, event.z);
        if (idClicked == Blocks.bed && worldObj.provider instanceof IGalacticraftWorldProvider && event.action.equals((Object)PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) && !worldObj.isRemote && !((IGalacticraftWorldProvider)worldObj.provider).hasBreathableAtmosphere()) {
            if (GalacticraftCore.isPlanetsLoaded) {
                GCPlayerStats.tryBedWarning((EntityPlayerMP)event.entityPlayer);
            }
            if (worldObj.provider instanceof WorldProviderOrbit) {
                event.setCanceled(true);
                return;
            }
            EventHandlerGC.bedActivated = true;
            if (worldObj.provider.canRespawnHere() && !EventHandlerGC.bedActivated) {
                EventHandlerGC.bedActivated = true;
                event.entityPlayer.setSpawnChunk(new ChunkCoordinates(event.x, event.y, event.z), false);
            }
            else {
                EventHandlerGC.bedActivated = false;
            }
        }
        final ItemStack heldStack = event.entityPlayer.inventory.getCurrentItem();
        final TileEntity tileClicked = worldObj.getTileEntity(event.x, event.y, event.z);
        if (heldStack != null) {
            if (tileClicked != null && tileClicked instanceof IKeyable) {
                if (event.action.equals((Object)PlayerInteractEvent.Action.LEFT_CLICK_BLOCK)) {
                    event.setCanceled(!((IKeyable)tileClicked).canBreak() && !event.entityPlayer.capabilities.isCreativeMode);
                    return;
                }
                if (event.action.equals((Object)PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) {
                    if (heldStack.getItem() instanceof IKeyItem) {
                        if (((IKeyItem)heldStack.getItem()).getTier(heldStack) == -1 || ((IKeyable)tileClicked).getTierOfKeyRequired() == -1 || ((IKeyItem)heldStack.getItem()).getTier(heldStack) == ((IKeyable)tileClicked).getTierOfKeyRequired()) {
                            event.setCanceled(((IKeyable)tileClicked).onValidKeyActivated(event.entityPlayer, heldStack, event.face));
                        }
                        else {
                            event.setCanceled(((IKeyable)tileClicked).onActivatedWithoutKey(event.entityPlayer, event.face));
                        }
                    }
                    else {
                        event.setCanceled(((IKeyable)tileClicked).onActivatedWithoutKey(event.entityPlayer, event.face));
                    }
                }
            }
            if ((heldStack.getItem() instanceof ItemFlintAndSteel || heldStack.getItem() instanceof ItemFireball) && !worldObj.isRemote && event.action.equals((Object)PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) && idClicked != Blocks.tnt && OxygenUtil.noAtmosphericCombustion(worldObj.provider) && !OxygenUtil.isAABBInBreathableAirBlock(worldObj, AxisAlignedBB.getBoundingBox((double)event.x, (double)event.y, (double)event.z, (double)(event.x + 1), (double)(event.y + 2), (double)(event.z + 1)))) {
                event.setCanceled(true);
            }
        }
        else if (tileClicked != null && tileClicked instanceof IKeyable) {
            if (event.action.equals((Object)PlayerInteractEvent.Action.LEFT_CLICK_BLOCK)) {
                event.setCanceled(!((IKeyable)tileClicked).canBreak() && !event.entityPlayer.capabilities.isCreativeMode);
                return;
            }
            event.setCanceled(((IKeyable)tileClicked).onActivatedWithoutKey(event.entityPlayer, event.face));
        }
    }

    @SubscribeEvent
    public void entityLivingEvent(final LivingEvent.LivingUpdateEvent event) {
        final EntityLivingBase entityLiving = event.entityLiving;
        if (entityLiving instanceof EntityPlayerMP) {
            GalacticraftCore.handler.onPlayerUpdate((EntityPlayerMP)entityLiving);
            if (GalacticraftCore.isPlanetsLoaded) {
                AsteroidsModule.playerHandler.onPlayerUpdate((EntityPlayerMP)entityLiving);
            }
            return;
        }
        if (entityLiving.ticksExisted % 100 == 0 && entityLiving.worldObj.provider instanceof IGalacticraftWorldProvider && !(entityLiving instanceof EntityPlayer) && (!(entityLiving instanceof IEntityBreathable) || !((IEntityBreathable)entityLiving).canBreath()) && !((IGalacticraftWorldProvider)entityLiving.worldObj.provider).hasBreathableAtmosphere()) {
            if (ConfigManagerCore.challengeMobDropsAndSpawning && entityLiving instanceof EntityEnderman) {
                return;
            }
            if (!OxygenUtil.isAABBInBreathableAirBlock(entityLiving)) {
                final GCCoreOxygenSuffocationEvent suffocationEvent = (GCCoreOxygenSuffocationEvent)new GCCoreOxygenSuffocationEvent.Pre(entityLiving);
                MinecraftForge.EVENT_BUS.post((Event)suffocationEvent);
                if (suffocationEvent.isCanceled()) {
                    return;
                }
                entityLiving.attackEntityFrom((DamageSource)DamageSourceGC.oxygenSuffocation, 1.0f);
                final GCCoreOxygenSuffocationEvent suffocationEventPost = (GCCoreOxygenSuffocationEvent)new GCCoreOxygenSuffocationEvent.Post(entityLiving);
                MinecraftForge.EVENT_BUS.post((Event)suffocationEventPost);
            }
        }
    }

    private ItemStack fillBucket(final World world, final MovingObjectPosition position) {
        final Block block = world.getBlock(position.blockX, position.blockY, position.blockZ);
        final Item bucket = EventHandlerGC.bucketList.get(block);
        if (bucket != null && world.getBlockMetadata(position.blockX, position.blockY, position.blockZ) == 0) {
            world.setBlockToAir(position.blockX, position.blockY, position.blockZ);
            return new ItemStack(bucket);
        }
        return null;
    }

    @SubscribeEvent
    public void onBucketFill(final FillBucketEvent event) {
        final MovingObjectPosition pos = event.target;
        final ItemStack ret = this.fillBucket(event.world, pos);
        if (ret == null) {
            return;
        }
        event.result = ret;
        event.setResult(Event.Result.ALLOW);
    }

    @SubscribeEvent
    public void populate(final PopulateChunkEvent.Post event) {
        final boolean doGen = TerrainGen.populate(event.chunkProvider, event.world, event.rand, event.chunkX, event.chunkZ, event.hasVillageGenerated, PopulateChunkEvent.Populate.EventType.CUSTOM);
        if (!doGen) {
            return;
        }
        final int worldX = event.chunkX << 4;
        final int worldZ = event.chunkZ << 4;
        generateOil(event.world, event.rand, worldX + event.rand.nextInt(16), worldZ + event.rand.nextInt(16), false);
    }

    public static boolean oilPresent(final World world, final Random rand, final int x, final int z, final BlockVec3 pos) {
        boolean doGen2 = false;
        for (final Integer dim : ConfigManagerCore.externalOilGen) {
            if (dim == world.provider.dimensionId) {
                doGen2 = true;
                break;
            }
        }
        if (!doGen2) {
            return false;
        }
        final BiomeGenBase biomegenbase = world.getBiomeGenForCoords(x + 8, z + 8);
        if (biomegenbase.biomeID == BiomeGenBase.sky.biomeID || biomegenbase.biomeID == BiomeGenBase.hell.biomeID) {
            return false;
        }
        rand.setSeed(world.getSeed());
        final long i1 = rand.nextInt() / 2L * 2L + 1L;
        final long j1 = rand.nextInt() / 2L * 2L + 1L;
        rand.setSeed(x * i1 + z * j1 ^ world.getSeed());
        double randMod = Math.min(0.2, 0.08 * ConfigManagerCore.oilGenFactor);
        if (biomegenbase.rootHeight >= 0.45f) {
            randMod /= 2.0;
        }
        if (biomegenbase.rootHeight < -0.5f) {
            randMod *= 1.8;
        }
        if (biomegenbase instanceof BiomeGenDesert) {
            randMod *= 1.8;
        }
        final boolean flag1 = rand.nextDouble() <= randMod;
        final boolean flag2 = rand.nextDouble() <= randMod;
        if (flag1 || flag2) {
            pos.y = 17 + rand.nextInt(10) + rand.nextInt(5);
            pos.x = x + rand.nextInt(16);
            pos.z = z + rand.nextInt(16);
            return true;
        }
        return false;
    }

    public static void generateOil(final World world, final Random rand, final int xx, final int zz, final boolean testFirst) {
        final BlockVec3 pos = new BlockVec3();
        if (oilPresent(world, rand, xx, zz, pos)) {
            final int x = pos.x;
            final int cy = pos.y;
            final int z = pos.z;
            final int r = 3 + rand.nextInt(5);
            if (testFirst && checkOilPresent(world, x, cy, z, r)) {
                return;
            }
            final int r2 = r * r;
            for (int bx = -r; bx <= r; ++bx) {
                for (int by = -r + 2; by <= r - 2; ++by) {
                    for (int bz = -r; bz <= r; ++bz) {
                        final int d2 = bx * bx + by * by * 3 + bz * bz;
                        if (d2 <= r2) {
                            if (!checkBlock(world, bx + x - 1, by + cy, bz + z)) {
                                if (!checkBlock(world, bx + x + 1, by + cy, bz + z)) {
                                    if (!checkBlock(world, bx + x, by + cy - 1, bz + z)) {
                                        if (!checkBlock(world, bx + x, by + cy, bz + z - 1)) {
                                            if (!checkBlock(world, bx + x, by + cy, bz + z + 1)) {
                                                if (!checkBlockAbove(world, bx + x, by + cy + 1, bz + z)) {
                                                    world.setBlock(bx + x, by + cy, bz + z, GCBlocks.crudeOil, 0, 2);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean checkOilPresent(final World world, final int x, final int cy, final int z, final int r) {
        final int r2 = r * r;
        for (int bx = -r; bx <= r; ++bx) {
            for (int by = -r + 2; by <= r - 2; ++by) {
                for (int bz = -r; bz <= r; ++bz) {
                    final int d2 = bx * bx + by * by * 3 + bz * bz;
                    if (d2 <= r2) {
                        if (!checkBlock(world, bx + x - 1, by + cy, bz + z)) {
                            if (!checkBlock(world, bx + x + 1, by + cy, bz + z)) {
                                if (!checkBlock(world, bx + x, by + cy - 1, bz + z)) {
                                    if (!checkBlock(world, bx + x, by + cy, bz + z - 1)) {
                                        if (!checkBlock(world, bx + x, by + cy, bz + z + 1)) {
                                            if (!checkBlockAbove(world, bx + x, by + cy + 1, bz + z)) {
                                                if (world.getBlock(bx + x, by + cy, bz + z) == GCBlocks.crudeOil) {
                                                    return true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void retrogenOil(final World world, final Chunk chunk) {
        final int cx = chunk.xPosition;
        final int cz = chunk.zPosition;
        generateOil(world, new Random(), cx << 4, cz << 4, true);
    }

    private static boolean checkBlock(final World w, final int x, final int y, final int z) {
        final Block b = w.getBlock(x, y, z);
        return b.getMaterial() == Material.air || (b instanceof BlockLiquid && b != GCBlocks.crudeOil);
    }

    private static boolean checkBlockAbove(final World w, final int x, final int y, final int z) {
        final Block b = w.getBlock(x, y, z);
        return b instanceof BlockSand || b instanceof BlockGravel;
    }

    @SubscribeEvent
    public void schematicUnlocked(final SchematicEvent.Unlock event) {
        final GCPlayerStats stats = GCPlayerStats.get(event.player);
        if (!stats.unlockedSchematics.contains(event.page)) {
            stats.unlockedSchematics.add(event.page);
            Collections.sort(stats.unlockedSchematics);
            if (event.player != null && event.player.playerNetServerHandler != null) {
                final Integer[] iArray = new Integer[stats.unlockedSchematics.size()];
                for (int i = 0; i < iArray.length; ++i) {
                    final ISchematicPage page = stats.unlockedSchematics.get(i);
                    iArray[i] = ((page == null) ? -2 : page.getPageID());
                }
                final List<Object> objList = new ArrayList<Object>();
                objList.add(iArray);
                GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_SCHEMATIC_LIST, objList), event.player);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void schematicFlipEvent(final SchematicEvent.FlipPage event) {
        ISchematicPage page = null;
        switch (event.direction) {
            case 1: {
                page = getNextSchematic(event.index);
                break;
            }
            case -1: {
                page = getLastSchematic(event.index);
                break;
            }
        }
        if (page != null) {
            GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(PacketSimple.EnumSimplePacket.S_OPEN_SCHEMATIC_PAGE, new Object[] { page.getPageID() }));
            FMLClientHandler.instance().getClient().thePlayer.openGui((Object)GalacticraftCore.instance, page.getGuiID(), FMLClientHandler.instance().getClient().thePlayer.worldObj, (int)FMLClientHandler.instance().getClient().thePlayer.posX, (int)FMLClientHandler.instance().getClient().thePlayer.posY, (int)FMLClientHandler.instance().getClient().thePlayer.posZ);
        }
    }

    @SideOnly(Side.CLIENT)
    private static ISchematicPage getNextSchematic(final int currentIndex) {
        final HashMap<Integer, Integer> idList = new HashMap<Integer, Integer>();
        final EntityClientPlayerMP player = PlayerUtil.getPlayerBaseClientFromPlayer((EntityPlayer)FMLClientHandler.instance().getClient().thePlayer, false);
        final GCPlayerStatsClient stats = GCPlayerStatsClient.get((EntityPlayerSP)player);
        for (int i = 0; i < stats.unlockedSchematics.size(); ++i) {
            idList.put(i, stats.unlockedSchematics.get(i).getPageID());
        }
        final SortedSet<Integer> keys = new TreeSet<Integer>(idList.keySet());
        final Iterator<Integer> iterator = keys.iterator();
        int count = 0;
        while (count < keys.size()) {
            final int j = iterator.next();
            final ISchematicPage page = SchematicRegistry.getMatchingRecipeForID((int)idList.get(j));
            if (page.getPageID() == currentIndex) {
                if (count + 1 < stats.unlockedSchematics.size()) {
                    return stats.unlockedSchematics.get(count + 1);
                }
                return null;
            }
            else {
                ++count;
            }
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private static ISchematicPage getLastSchematic(final int currentIndex) {
        final HashMap<Integer, Integer> idList = new HashMap<Integer, Integer>();
        final EntityClientPlayerMP player = PlayerUtil.getPlayerBaseClientFromPlayer((EntityPlayer)FMLClientHandler.instance().getClient().thePlayer, false);
        final GCPlayerStatsClient stats = GCPlayerStatsClient.get((EntityPlayerSP)player);
        for (int i = 0; i < stats.unlockedSchematics.size(); ++i) {
            idList.put(i, stats.unlockedSchematics.get(i).getPageID());
        }
        final SortedSet<Integer> keys = new TreeSet<Integer>(idList.keySet());
        final Iterator<Integer> iterator = keys.iterator();
        int count = 0;
        while (count < keys.size()) {
            final int j = iterator.next();
            final ISchematicPage page = SchematicRegistry.getMatchingRecipeForID((int)idList.get(j));
            if (page.getPageID() == currentIndex) {
                if (count - 1 >= 0) {
                    return stats.unlockedSchematics.get(count - 1);
                }
                return null;
            }
            else {
                ++count;
            }
        }
        return null;
    }

    @SubscribeEvent
    public void onPlayerDeath(final PlayerDropsEvent event) {
        if (event.entityLiving instanceof EntityPlayerMP) {
            final GCPlayerStats stats = GCPlayerStats.get((EntityPlayerMP)event.entityLiving);
            if (!event.entityLiving.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
                event.entityLiving.captureDrops = true;
                for (int i = stats.extendedInventory.getSizeInventory() - 1; i >= 0; --i) {
                    final ItemStack stack = stats.extendedInventory.getStackInSlot(i);
                    if (stack != null) {
                        ((EntityPlayerMP)event.entityLiving).func_146097_a(stack, true, false);
                        stats.extendedInventory.setInventorySlotContents(i, null);
                    }
                }
                event.entityLiving.captureDrops = false;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onLeaveBedButtonClicked(final SleepCancelledEvent event) {
        final EntityPlayer player = (EntityPlayer)FMLClientHandler.instance().getClient().thePlayer;
        final ChunkCoordinates c = player.playerLocation;
        if (c != null) {
            final EventWakePlayer event2 = new EventWakePlayer(player, c.posX, c.posY, c.posZ, true, true, false, true);
            MinecraftForge.EVENT_BUS.post((Event)event2);
            player.wakeUpPlayer(true, true, false);
            if (player.worldObj.isRemote && GalacticraftCore.isPlanetsLoaded) {
                GalacticraftCore.packetPipeline.sendToServer(new PacketSimpleMars(PacketSimpleMars.EnumSimplePacketMars.S_WAKE_PLAYER, new Object[0]));
            }
        }
    }

    @SubscribeEvent
    public void onZombieSummonAid(final ZombieEvent.SummonAidEvent event) {
        if (event.entity instanceof EntityEvolvedZombie) {
            event.customSummonedAid = (EntityZombie)new EntityEvolvedZombie(event.world);
            if (((EntityLivingBase)event.entity).getRNG().nextFloat() < ((EntityEvolvedZombie)event.entity).getEntityAttribute(((EntityEvolvedZombie)event.entity).getReinforcementsAttribute()).getAttributeValue()) {
                event.setResult(Event.Result.ALLOW);
            }
            else {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void overrideSkyColor(final EntityViewRenderEvent.FogColors event) {
        if (event.entity.isPotionActive(Potion.nightVision)) {
            final WorldClient worldclient = Minecraft.getMinecraft().theWorld;
            if (worldclient.provider instanceof IGalacticraftWorldProvider && ((IGalacticraftWorldProvider)worldclient.provider).getCelestialBody().atmosphere.size() == 0 && event.block.getMaterial() == Material.air && !((IGalacticraftWorldProvider)worldclient.provider).hasBreathableAtmosphere()) {
                final Vec3 vec = worldclient.getFogColor(1.0f);
                event.red = (float)vec.xCoord;
                event.green = (float)vec.yCoord;
                event.blue = (float)vec.zCoord;
                return;
            }
            if (worldclient.provider.getSkyRenderer() instanceof SkyProviderOverworld && event.entity.posY > 200.0) {
                final Vec3 vec = WorldUtil.getFogColorHook(event.entity.worldObj);
                event.red = (float)vec.xCoord;
                event.green = (float)vec.yCoord;
                event.blue = (float)vec.zCoord;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onSoundPlayed(final PlaySoundEvent17 event) {
        if (event.result == null) {
            return;
        }
        final EntityPlayerSP player = (EntityPlayerSP)FMLClientHandler.instance().getClient().thePlayer;
        if (player != null && player.worldObj != null && player.worldObj.provider instanceof IGalacticraftWorldProvider && event != null && event.result.getAttenuationType() != ISound.AttenuationType.NONE) {
            final PlayerGearData gearData = ClientProxyCore.playerItemData.get(player.getGameProfile().getName());
            final float x = event.result.getXPosF();
            final float y = event.result.getYPosF();
            final float z = event.result.getZPosF();
            if (gearData == null || gearData.getFrequencyModule() == -1) {
                final AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(x - 0.0015, y - 0.0015, z - 0.0015, x + 0.0015, y + 0.0015, z + 0.0015);
                final boolean playerInAtmosphere = OxygenUtil.isAABBInBreathableAirBlock((EntityLivingBase)player);
                final boolean soundInAtmosphere = OxygenUtil.isAABBInBreathableAirBlock(player.worldObj, bb);
                if (!playerInAtmosphere || !soundInAtmosphere) {
                    final float volume = event.result.getVolume();
                    for (int i = 0; i < this.soundPlayList.size(); ++i) {
                        final SoundPlayEntry entry = this.soundPlayList.get(i);
                        if (entry.name.equals(event.name) && entry.x == x && entry.y == y && entry.z == z && entry.volume == volume) {
                            this.soundPlayList.remove(i);
                            return;
                        }
                    }
                    final float newVolume = volume / Math.max(0.01f, ((IGalacticraftWorldProvider)player.worldObj.provider).getSoundVolReductionAmount());
                    this.soundPlayList.add(new SoundPlayEntry(event.name, x, y, z, newVolume));
                    final ISound newSound = (ISound)new PositionedSoundRecord(event.result.getPositionedSoundLocation(), newVolume, event.result.getPitch(), x, y, z);
                    event.manager.playSound(newSound);
                    event.result = null;
                }
            }
        }
    }

    static {
        EventHandlerGC.bucketList = new HashMap<Block, Item>();
    }

    private static class SoundPlayEntry
    {
        private final String name;
        private final float x;
        private final float y;
        private final float z;
        private final float volume;

        private SoundPlayEntry(final String name, final float x, final float y, final float z, final float volume) {
            this.name = name;
            this.volume = volume;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static class SleepCancelledEvent extends Event
    {
    }

    public static class OrientCameraEvent extends Event
    {
    }
}
