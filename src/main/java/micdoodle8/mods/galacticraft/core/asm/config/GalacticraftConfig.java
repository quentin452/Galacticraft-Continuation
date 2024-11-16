package micdoodle8.mods.galacticraft.core.asm.config;

import com.falsepattern.lib.config.Config;

import fr.iamacat.optimizationsandtweaks.Tags;

@Config(modid = Tags.MODID)
public class GalacticraftConfig {

    @Config.Comment("Entities respect changing gravity ")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enableEntityItemMixin;
    @Config.Comment("Add Physic Full Compat")
    @Config.DefaultBoolean(true)
    @Config.RequiresWorldRestart
    public static boolean enablePhysicFullCompatMixin;
}
