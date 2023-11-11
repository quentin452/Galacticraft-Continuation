package micdoodle8.mods.galacticraft.planets.mars.util;

import net.minecraft.item.*;
import java.util.*;
import micdoodle8.mods.galacticraft.core.recipe.*;
import micdoodle8.mods.galacticraft.api.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import micdoodle8.mods.galacticraft.planets.mars.network.*;
import micdoodle8.mods.galacticraft.planets.mars.inventory.*;
import micdoodle8.mods.galacticraft.planets.mars.entities.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.inventory.*;

public class MarsUtil
{
    public static void addRocketBenchT2Recipe(final ItemStack result, final HashMap<Integer, ItemStack> input) {
        GalacticraftRegistry.addT2RocketRecipe((INasaWorkbenchRecipe)new NasaWorkbenchRecipe(result, (HashMap)input));
    }
    
    public static void adCargoRocketRecipe(final ItemStack result, final HashMap<Integer, ItemStack> input) {
        GalacticraftRegistry.addCargoRocketRecipe((INasaWorkbenchRecipe)new NasaWorkbenchRecipe(result, (HashMap)input));
    }
    
    public static void openParachestInventory(final EntityPlayerMP player, final EntityLandingBalloons landerInv) {
        player.getNextWindowId();
        player.closeContainer();
        final int windowId = player.currentWindowId;
        GalacticraftCore.packetPipeline.sendTo((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.C_OPEN_PARACHEST_GUI, new Object[] { windowId, 1, landerInv.getEntityId() }), player);
        player.openContainer = (Container)new ContainerParaChest((IInventory)player.inventory, (IInventory)landerInv);
        player.openContainer.windowId = windowId;
        player.openContainer.addCraftingToCrafters((ICrafting)player);
    }
    
    public static void openSlimelingInventory(final EntityPlayerMP player, final EntitySlimeling slimeling) {
        player.getNextWindowId();
        player.closeContainer();
        final int windowId = player.currentWindowId;
        GalacticraftCore.packetPipeline.sendTo((IPacket)new PacketSimpleMars(PacketSimpleMars.EnumSimplePacketMars.C_OPEN_CUSTOM_GUI, new Object[] { windowId, 0, slimeling.getEntityId() }), player);
        player.openContainer = (Container)new ContainerSlimeling(player.inventory, slimeling);
        player.openContainer.windowId = windowId;
        player.openContainer.addCraftingToCrafters((ICrafting)player);
    }
    
    public static void openCargoRocketInventory(final EntityPlayerMP player, final EntityCargoRocket rocket) {
        player.getNextWindowId();
        player.closeContainer();
        final int windowId = player.currentWindowId;
        GalacticraftCore.packetPipeline.sendTo((IPacket)new PacketSimpleMars(PacketSimpleMars.EnumSimplePacketMars.C_OPEN_CUSTOM_GUI, new Object[] { windowId, 1, rocket.getEntityId() }), player);
        player.openContainer = (Container)new ContainerRocketInventory((IInventory)player.inventory, (IInventory)rocket, rocket.rocketType);
        player.openContainer.windowId = windowId;
        player.openContainer.addCraftingToCrafters((ICrafting)player);
    }
}
