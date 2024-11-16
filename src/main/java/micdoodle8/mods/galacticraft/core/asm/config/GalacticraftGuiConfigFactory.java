package micdoodle8.mods.galacticraft.core.asm.config;

import net.minecraft.client.gui.GuiScreen;

import com.falsepattern.lib.config.SimpleGuiFactory;

public class GalacticraftGuiConfigFactory implements SimpleGuiFactory {

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return GalacticraftGuiConfig.class;
    }
}
