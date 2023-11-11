package micdoodle8.mods.galacticraft.core.util;

import cpw.mods.fml.common.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.core.client.gui.screen.*;
import net.minecraft.client.gui.*;
import cpw.mods.fml.relauncher.*;

public class ThreadRequirementMissing extends Thread
{
    private static Side threadSide;
    public static ThreadRequirementMissing INSTANCE;
    
    public ThreadRequirementMissing(final Side threadSide) {
        super("Galacticraft Requirement Check Thread");
        this.setDaemon(true);
        ThreadRequirementMissing.threadSide = threadSide;
    }
    
    public static void beginCheck(final Side threadSide) {
        (ThreadRequirementMissing.INSTANCE = new ThreadRequirementMissing(threadSide)).start();
    }
    
    @Override
    public void run() {
        if (!Loader.isModLoaded("Micdoodlecore")) {
            if (ThreadRequirementMissing.threadSide.isServer()) {
                FMLCommonHandler.instance().getMinecraftServerInstance().logSevere("===================================================================");
                FMLCommonHandler.instance().getMinecraftServerInstance().logSevere("MicdoodleCore not found in mods folder. Galacticraft will not load.");
                FMLCommonHandler.instance().getMinecraftServerInstance().logSevere("===================================================================");
            }
            else {
                openGuiClient();
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    private static void openGuiClient() {
        FMLClientHandler.instance().getClient().displayGuiScreen((GuiScreen)new GuiMissingCore());
    }
}
