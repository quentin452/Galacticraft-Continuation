package micdoodle8.mods.galacticraft.core.proxy;

import micdoodle8.mods.galacticraft.core.entities.player.*;
import cpw.mods.fml.common.event.*;
import net.minecraft.block.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import cpw.mods.fml.common.*;
import net.minecraft.server.*;
import net.minecraft.entity.player.*;
import net.minecraft.network.*;

public class CommonProxyCore
{
    public IPlayerServer player;
    
    public CommonProxyCore() {
        this.player = (IPlayerServer)new PlayerServer();
    }
    
    public void preInit(final FMLPreInitializationEvent event) {
    }
    
    public void init(final FMLInitializationEvent event) {
    }
    
    public void postInit(final FMLPostInitializationEvent event) {
    }
    
    public int getBlockRender(final Block blockID) {
        return -1;
    }
    
    public int getTitaniumArmorRenderIndex() {
        return 0;
    }
    
    public int getSensorArmorRenderIndex() {
        return 0;
    }
    
    public World getClientWorld() {
        return null;
    }
    
    public void spawnParticle(final String particleID, final Vector3 position, final Vector3 motion, final Object[] otherInfo) {
    }
    
    public World getWorldForID(final int dimensionID) {
        final MinecraftServer theServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (theServer == null) {
            return null;
        }
        return (World)theServer.worldServerForDimension(dimensionID);
    }
    
    public EntityPlayer getPlayerFromNetHandler(final INetHandler handler) {
        if (handler instanceof NetHandlerPlayServer) {
            return (EntityPlayer)((NetHandlerPlayServer)handler).playerEntity;
        }
        return null;
    }
}
