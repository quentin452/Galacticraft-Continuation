package micdoodle8.mods.galacticraft.core.client.gui.screen;

import cpw.mods.fml.client.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import cpw.mods.fml.client.config.*;
import micdoodle8.mods.galacticraft.core.util.*;
import java.util.*;

public class ConfigGuiFactoryCore implements IModGuiFactory
{
    public void initialize(final Minecraft minecraftInstance) {
    }
    
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return (Class<? extends GuiScreen>)CoreConfigGUI.class;
    }
    
    public Set<IModGuiFactory.RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
    
    public IModGuiFactory.RuntimeOptionGuiHandler getHandlerFor(final IModGuiFactory.RuntimeOptionCategoryElement element) {
        return null;
    }
    
    public static class CoreConfigGUI extends GuiConfig
    {
        public CoreConfigGUI(final GuiScreen parent) {
            super(parent, (List)ConfigManagerCore.getConfigElements(), "GalacticraftCore", false, false, GCCoreUtil.translate("gc.configgui.title"));
        }
    }
}
