package micdoodle8.mods.galacticraft.core.mixins.minecraft;

import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable;
import net.minecraft.entity.monster.EntityGolem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityGolem.class)
public class EntityGolemMixin implements IEntityBreathable {

    @Override
    public boolean canBreath() {
        return true;
    }
}
