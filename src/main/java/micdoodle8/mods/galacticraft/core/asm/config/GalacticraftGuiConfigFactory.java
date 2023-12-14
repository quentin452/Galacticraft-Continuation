package micdoodle8.mods.galacticraft.core.asm.config;

import com.falsepattern.lib.config.SimpleGuiFactory;
import fr.iamacat.optimizationsandtweaks.config.OptimizationsandTweaksGuiConfig;
import net.minecraft.client.gui.GuiScreen;

public class GalacticraftGuiConfigFactory implements SimpleGuiFactory {

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return GalacticraftGuiConfig.class;
    }
}
