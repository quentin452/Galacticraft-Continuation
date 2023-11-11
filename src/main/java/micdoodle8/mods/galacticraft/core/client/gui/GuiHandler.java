package micdoodle8.mods.galacticraft.core.client.gui;

import cpw.mods.fml.common.network.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.core.inventory.*;
import net.minecraft.entity.player.*;
import net.minecraft.tileentity.*;
import cpw.mods.fml.common.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.galaxies.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import micdoodle8.mods.galacticraft.core.client.gui.screen.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.core.client.gui.container.*;
import micdoodle8.mods.galacticraft.core.entities.player.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import net.minecraft.client.entity.*;
import net.minecraft.client.gui.*;
import cpw.mods.fml.relauncher.*;

public class GuiHandler implements IGuiHandler
{
    public Object getServerGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
        final EntityPlayerMP playerBase = PlayerUtil.getPlayerBaseServerFromPlayer(player, false);
        if (playerBase == null) {
            player.addChatMessage((IChatComponent)new ChatComponentText("Galacticraft player instance null server-side. This is a bug."));
            return null;
        }
        final GCPlayerStats stats = GCPlayerStats.get(playerBase);
        if (ID == 4 && player.ridingEntity instanceof EntityTieredRocket) {
            return new ContainerRocketInventory((IInventory)player.inventory, (IInventory)player.ridingEntity, ((EntityTieredRocket)player.ridingEntity).getType());
        }
        if (ID == 5) {
            return new ContainerExtendedInventory(player, stats.extendedInventory);
        }
        final TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null) {
            if (tile instanceof TileEntityRefinery) {
                return new ContainerRefinery(player.inventory, (TileEntityRefinery)tile);
            }
            if (tile instanceof TileEntityOxygenCollector) {
                return new ContainerOxygenCollector(player.inventory, (TileEntityOxygenCollector)tile);
            }
            if (tile instanceof TileEntityOxygenDistributor) {
                return new ContainerOxygenDistributor(player.inventory, (TileEntityOxygenDistributor)tile);
            }
            if (tile instanceof TileEntityFuelLoader) {
                return new ContainerFuelLoader(player.inventory, (TileEntityFuelLoader)tile);
            }
            if (tile instanceof TileEntityOxygenSealer) {
                return new ContainerOxygenSealer(player.inventory, (TileEntityOxygenSealer)tile);
            }
            if (tile instanceof TileEntityCargoLoader) {
                return new ContainerCargoLoader(player.inventory, (IInventory)tile);
            }
            if (tile instanceof TileEntityCargoUnloader) {
                return new ContainerCargoLoader(player.inventory, (IInventory)tile);
            }
            if (tile instanceof TileEntityParaChest) {
                return new ContainerParaChest((IInventory)player.inventory, (IInventory)tile);
            }
            if (tile instanceof TileEntitySolar) {
                return new ContainerSolar(player.inventory, (TileEntitySolar)tile);
            }
            if (tile instanceof TileEntityEnergyStorageModule) {
                return new ContainerEnergyStorageModule(player.inventory, (TileEntityEnergyStorageModule)tile);
            }
            if (tile instanceof TileEntityCoalGenerator) {
                return new ContainerCoalGenerator(player.inventory, (TileEntityCoalGenerator)tile);
            }
            if (tile instanceof TileEntityElectricFurnace) {
                return new ContainerElectricFurnace(player.inventory, (TileEntityElectricFurnace)tile);
            }
            if (tile instanceof TileEntityIngotCompressor) {
                return new ContainerIngotCompressor(player.inventory, (TileEntityIngotCompressor)tile);
            }
            if (tile instanceof TileEntityElectricIngotCompressor) {
                return new ContainerElectricIngotCompressor(player.inventory, (TileEntityElectricIngotCompressor)tile);
            }
            if (tile instanceof TileEntityCircuitFabricator) {
                return new ContainerCircuitFabricator(player.inventory, (TileEntityCircuitFabricator)tile);
            }
            if (tile instanceof TileEntityOxygenStorageModule) {
                return new ContainerOxygenStorageModule(player.inventory, (TileEntityOxygenStorageModule)tile);
            }
            if (tile instanceof TileEntityOxygenCompressor) {
                return new ContainerOxygenCompressor(player.inventory, (TileEntityOxygenCompressor)tile);
            }
            if (tile instanceof TileEntityOxygenDecompressor) {
                return new ContainerOxygenDecompressor(player.inventory, (TileEntityOxygenDecompressor)tile);
            }
        }
        for (final ISchematicPage page : stats.unlockedSchematics) {
            if (ID == page.getGuiID()) {
                return page.getResultContainer((EntityPlayer)playerBase, x, y, z);
            }
        }
        return null;
    }
    
    public Object getClientGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            return this.getClientGuiElement(ID, player, world, new Vector3((double)x, (double)y, (double)z));
        }
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    private Object getClientGuiElement(final int ID, final EntityPlayer player, final World world, final Vector3 position) {
        final EntityClientPlayerMP playerClient = PlayerUtil.getPlayerBaseClientFromPlayer(player, false);
        if (ID == 3) {
            return new GuiCelestialSelection(true, null);
        }
        if (ID == 4 && player.ridingEntity instanceof EntityTieredRocket) {
            return new GuiRocketInventory((IInventory)player.inventory, (IInventory)player.ridingEntity, ((EntityTieredRocket)player.ridingEntity).getType());
        }
        if (ID == 5) {
            return new GuiExtendedInventory(player, ClientProxyCore.dummyInventory);
        }
        if (ID == 6) {
            return new GuiNewSpaceRace(player);
        }
        if (ID == 7) {
            return new GuiJoinSpaceRace(playerClient);
        }
        final TileEntity tile = world.getTileEntity(position.intX(), position.intY(), position.intZ());
        if (tile != null) {
            if (tile instanceof TileEntityRefinery) {
                return new GuiRefinery(player.inventory, (TileEntityRefinery)world.getTileEntity(position.intX(), position.intY(), position.intZ()));
            }
            if (tile instanceof TileEntityOxygenCollector) {
                return new GuiOxygenCollector(player.inventory, (TileEntityOxygenCollector)tile);
            }
            if (tile instanceof TileEntityOxygenDistributor) {
                return new GuiOxygenDistributor(player.inventory, (TileEntityOxygenDistributor)tile);
            }
            if (tile instanceof TileEntityFuelLoader) {
                return new GuiFuelLoader(player.inventory, (TileEntityFuelLoader)tile);
            }
            if (tile instanceof TileEntityOxygenSealer) {
                return new GuiOxygenSealer(player.inventory, (TileEntityOxygenSealer)tile);
            }
            if (tile instanceof TileEntityCargoLoader) {
                return new GuiCargoLoader(player.inventory, (TileEntityCargoLoader)tile);
            }
            if (tile instanceof TileEntityCargoUnloader) {
                return new GuiCargoUnloader(player.inventory, (TileEntityCargoUnloader)tile);
            }
            if (tile instanceof TileEntityParaChest) {
                return new GuiParaChest((IInventory)player.inventory, (IInventory)tile);
            }
            if (tile instanceof TileEntitySolar) {
                return new GuiSolar(player.inventory, (TileEntitySolar)tile);
            }
            if (tile instanceof TileEntityAirLockController) {
                return new GuiAirLockController((TileEntityAirLockController)tile);
            }
            if (tile instanceof TileEntityEnergyStorageModule) {
                return new GuiEnergyStorageModule(player.inventory, (TileEntityEnergyStorageModule)tile);
            }
            if (tile instanceof TileEntityCoalGenerator) {
                return new GuiCoalGenerator(player.inventory, (TileEntityCoalGenerator)tile);
            }
            if (tile instanceof TileEntityElectricFurnace) {
                return new GuiElectricFurnace(player.inventory, (TileEntityElectricFurnace)tile);
            }
            if (tile instanceof TileEntityIngotCompressor) {
                return new GuiIngotCompressor(player.inventory, (TileEntityIngotCompressor)tile);
            }
            if (tile instanceof TileEntityElectricIngotCompressor) {
                return new GuiElectricIngotCompressor(player.inventory, (TileEntityElectricIngotCompressor)tile);
            }
            if (tile instanceof TileEntityCircuitFabricator) {
                return new GuiCircuitFabricator(player.inventory, (TileEntityCircuitFabricator)tile);
            }
            if (tile instanceof TileEntityOxygenStorageModule) {
                return new GuiOxygenStorageModule(player.inventory, (TileEntityOxygenStorageModule)tile);
            }
            if (tile instanceof TileEntityOxygenCompressor) {
                return new GuiOxygenCompressor(player.inventory, (TileEntityOxygenCompressor)tile);
            }
            if (tile instanceof TileEntityOxygenDecompressor) {
                return new GuiOxygenDecompressor(player.inventory, (TileEntityOxygenDecompressor)tile);
            }
        }
        if (playerClient != null) {
            final GCPlayerStatsClient stats = GCPlayerStatsClient.get((EntityPlayerSP)playerClient);
            for (final ISchematicPage page : stats.unlockedSchematics) {
                if (ID == page.getGuiID()) {
                    final GuiScreen screen = page.getResultScreen((EntityPlayer)playerClient, position.intX(), position.intY(), position.intZ());
                    if (screen instanceof ISchematicResultPage) {
                        ((ISchematicResultPage)screen).setPageIndex(page.getPageID());
                    }
                    return screen;
                }
            }
        }
        return null;
    }
}
