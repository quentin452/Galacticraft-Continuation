package micdoodle8.mods.galacticraft.core.asm.config;

import com.falsepattern.lib.config.ConfigException;
import com.falsepattern.lib.config.SimpleGuiConfig;
import fr.iamacat.optimizationsandtweaks.Tags;
import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksConfig;
import micdoodle8.mods.galacticraft.core.Constants;
import net.minecraft.client.gui.GuiScreen;

public class GalacticraftGuiConfig extends SimpleGuiConfig {

    public GalacticraftGuiConfig(GuiScreen parent) throws ConfigException {
        super(parent, GalacticraftConfig.class, Constants.MOD_ID_CORE, Constants.COREMOD_NAME_SIMPLE);
    }
}
