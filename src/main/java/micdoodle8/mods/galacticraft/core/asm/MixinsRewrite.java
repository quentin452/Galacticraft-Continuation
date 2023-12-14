package micdoodle8.mods.galacticraft.core.asm;

import com.falsepattern.lib.mixin.IMixin;
import com.falsepattern.lib.mixin.ITargetedMod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import micdoodle8.mods.galacticraft.core.asm.config.GalacticraftConfig;

import java.util.List;
import java.util.function.Predicate;

@RequiredArgsConstructor
public enum MixinsRewrite implements IMixin {

    // TWEAKING MIXINS

    PREVENT_FIRE_RENDERING_WITHOUT_O2(Side.COMMON,
        avoid(TargetedModRewrite.ITEMPHYSICFULL).and(m -> GalacticraftConfig.enableEntityItemMixin),
        "core.MixinWorld"),

    PHYSIC_FULL_COMPAT(Side.COMMON,
        m -> GalacticraftConfig.enablePhysicFullCompatMixin,
        "lotrimprovements.MixinMain"),

    // MOD-FILTERED MIXINS

    // The modFilter argument is a predicate, so you can also use the .and(), .or(), and .negate() methods to mix and
    // match multiple predicates.
    ;

    @Getter
    public final Side side;
    @Getter
    public final Predicate<List<ITargetedMod>> filter;
    @Getter
    public final String mixin;

    static Predicate<List<ITargetedMod>> require(TargetedModRewrite in) {
        return modList -> modList.contains(in);
    }

    static Predicate<List<ITargetedMod>> avoid(TargetedModRewrite in) {
        return modList -> !modList.contains(in);
    }
}
