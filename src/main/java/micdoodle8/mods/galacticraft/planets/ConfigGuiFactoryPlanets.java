package micdoodle8.mods.galacticraft.planets;

import cpw.mods.fml.client.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import cpw.mods.fml.client.config.*;
import micdoodle8.mods.galacticraft.core.util.*;
import java.util.*;

public class ConfigGuiFactoryPlanets implements IModGuiFactory
{
    public void initialize(final Minecraft minecraftInstance) {
    }
    
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return (Class<? extends GuiScreen>)PlanetsConfigGUI.class;
    }
    
    public Set<IModGuiFactory.RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
    
    public IModGuiFactory.RuntimeOptionGuiHandler getHandlerFor(final IModGuiFactory.RuntimeOptionCategoryElement element) {
        return null;
    }
    
    public static class PlanetsConfigGUI extends GuiConfig
    {
        public PlanetsConfigGUI(final GuiScreen parent) {
            super(parent, (List)GalacticraftPlanets.getConfigElements(), "GalacticraftMars", false, false, GCCoreUtil.translate("gc.configgui.planets.title"));
        }
    }
}
