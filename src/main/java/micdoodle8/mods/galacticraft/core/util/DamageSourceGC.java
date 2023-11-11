package micdoodle8.mods.galacticraft.core.util;

import net.minecraft.util.*;

public class DamageSourceGC extends DamageSource
{
    public static final DamageSourceGC spaceshipCrash;
    public static final DamageSourceGC oxygenSuffocation;
    public static final DamageSourceGC thermal;
    
    public DamageSourceGC(final String damageType) {
        super(damageType);
    }
    
    static {
        spaceshipCrash = (DamageSourceGC)new DamageSourceGC("spaceshipCrash").setDamageBypassesArmor().setExplosion();
        oxygenSuffocation = (DamageSourceGC)new DamageSourceGC("oxygenSuffocation").setDamageBypassesArmor().setDamageIsAbsolute();
        thermal = (DamageSourceGC)new DamageSourceGC("thermal").setDamageBypassesArmor().setDamageIsAbsolute();
    }
}
